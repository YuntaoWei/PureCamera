package com.pure.camera.async;

import com.pure.camera.common.LogPrinter;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    @SuppressWarnings("unused")
    private static final String TAG = "ThreadPool";
    private static final int CORE_POOL_SIZE = 4;
    private static final int MAX_POOL_SIZE = 8;
    private static final int KEEP_ALIVE_TIME = 10;

    public static final int MODE_NONE = 0;
    public static final int MODE_CPU = 1;
    public static final int MODE_NETWORK = 2;

    public static final JobContext JOB_CONTEXT_STUB = new JobContextStub();

    ResourceCounter mCpuCounter = new ResourceCounter(2);
    ResourceCounter mNetworkCounter = new ResourceCounter(2);

    public interface Job<T> {
        public T run(JobContext jc);
    }

    public interface JobContext {
        boolean isCancelled();

        void setCancelListener(CancelListener listener);

        boolean setMode(int mode);
    }

    private static class JobContextStub implements JobContext {
        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public void setCancelListener(CancelListener listener) {
        }

        @Override
        public boolean setMode(int mode) {
            return true;
        }
    }

    public interface CancelListener {
        public void onCancel();
    }

    private static class ResourceCounter {
        public int value;

        public ResourceCounter(int v) {
            value = v;
        }
    }

    private final Executor mExecutor;

    private static ThreadPool DEFAULT_POOL;

    public static ThreadPool getDefaultPool() {
        if(null == DEFAULT_POOL) {
            synchronized (ThreadPool.class) {
                DEFAULT_POOL = new ThreadPool();
            }
        }
        return DEFAULT_POOL;
    }

    private ThreadPool() {
        this(CORE_POOL_SIZE, MAX_POOL_SIZE);
    }

    public ThreadPool(int initPoolSize, int maxPoolSize) {
        mExecutor = new ThreadPoolExecutor(
                initPoolSize, maxPoolSize, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                new PriorityThreadFactory("thread-pool",
                        android.os.Process.THREAD_PRIORITY_BACKGROUND));
    }

    public <T> Future<T> submit(Job<T> job, FutureListener<T> listener) {
        Worker<T> w = new Worker<T>(job, listener);
        mExecutor.execute(w);
        return w;
    }

    public <T> Future<T> submit(Job<T> job) {
        return submit(job, null);
    }

    private class Worker<T> implements Runnable, Future<T>, JobContext {
        @SuppressWarnings("hiding")
        private static final String TAG = "Worker";
        private Job<T> mJob;
        private FutureListener<T> mListener;
        private CancelListener mCancelListener;
        private ResourceCounter mWaitOnResource;
        private volatile boolean mIsCancelled;
        private boolean mIsDone;
        private T mResult;
        private int mMode;

        public Worker(Job<T> job, FutureListener<T> listener) {
            mJob = job;
            mListener = listener;
        }

        @Override
        public void run() {
            T result = null;

            if (setMode(MODE_CPU)) {
                try {
                    result = mJob.run(this);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    LogPrinter.w(TAG, "ingore exception : " + ex.getMessage());
                }
            }

            synchronized (this) {
                setMode(MODE_NONE);
                mResult = result;
                mIsDone = true;
                notifyAll();
            }
            if (mListener != null) mListener.onFutureDone(this);
        }

        @Override
        public synchronized void cancel() {
            if (mIsCancelled) return;
            mIsCancelled = true;
            if (mWaitOnResource != null) {
                synchronized (mWaitOnResource) {
                    mWaitOnResource.notifyAll();
                }
            }
            if (mCancelListener != null) {
                mCancelListener.onCancel();
            }
        }

        @Override
        public boolean isCancelled() {
            return mIsCancelled;
        }

        @Override
        public synchronized boolean isDone() {
            return mIsDone;
        }

        @Override
        public synchronized T get() {
            while (!mIsDone) {
                try {
                    wait();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    LogPrinter.w(TAG, "ingore exception : " + ex.getMessage());
                }
            }
            return mResult;
        }

        @Override
        public void waitDone() {
            get();
        }

        @Override
        public synchronized void setCancelListener(CancelListener listener) {
            mCancelListener = listener;
            if (mIsCancelled && mCancelListener != null) {
                mCancelListener.onCancel();
            }
        }

        @Override
        public boolean setMode(int mode) {
            ResourceCounter rc = modeToCounter(mMode);
            if (rc != null) releaseResource(rc);
            mMode = MODE_NONE;

            rc = modeToCounter(mode);
            if (rc != null) {
                if (!acquireResource(rc)) {
                    return false;
                }
                mMode = mode;
            }

            return true;
        }

        private ResourceCounter modeToCounter(int mode) {
            if (mode == MODE_CPU) {
                return mCpuCounter;
            } else if (mode == MODE_NETWORK) {
                return mNetworkCounter;
            } else {
                return null;
            }
        }

        private boolean acquireResource(ResourceCounter counter) {
            while (true) {
                synchronized (this) {
                    if (mIsCancelled) {
                        mWaitOnResource = null;
                        return false;
                    }
                    mWaitOnResource = counter;
                }

                synchronized (counter) {
                    if (counter.value > 0) {
                        counter.value--;
                        break;
                    } else {
                        try {
                            counter.wait();
                        } catch (InterruptedException ex) {
                            // ignore.
                        }
                    }
                }
            }

            synchronized (this) {
                mWaitOnResource = null;
            }

            return true;
        }

        private void releaseResource(ResourceCounter counter) {
            synchronized (counter) {
                counter.value++;
                counter.notifyAll();
            }
        }
    }
}
