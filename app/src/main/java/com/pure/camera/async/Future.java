package com.pure.camera.async;

public interface Future<T> {
    public void cancel();
    public boolean isCancelled();
    public boolean isDone();
    public T get();
    public void waitDone();
}
