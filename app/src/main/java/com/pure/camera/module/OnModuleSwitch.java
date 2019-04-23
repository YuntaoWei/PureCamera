package com.pure.camera.module;

public interface OnModuleSwitch {
    /**
     * 模式切换动作监听
     * @param photo 为true表示当前已切换到Photo模式，否则切换到Video模式.
     */
    void onSwitch(boolean photo);
}
