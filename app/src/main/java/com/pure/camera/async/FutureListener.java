package com.pure.camera.async;

public interface FutureListener<T> {
    public void onFutureDone(Future<T> future);
}
