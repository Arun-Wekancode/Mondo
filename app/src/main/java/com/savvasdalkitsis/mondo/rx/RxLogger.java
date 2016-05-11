package com.savvasdalkitsis.mondo.rx;

import android.util.Log;

import rx.plugins.DebugHook;
import rx.plugins.DebugNotificationListener;
import rx.plugins.RxJavaPlugins;

public class RxLogger {

    public static void logRx() {
        RxJavaPlugins.getInstance().registerObservableExecutionHook(new DebugHook<>(new DebugNotificationListener<Object>() {
            @Override
            public void error(Object context, Throwable e) {
                Log.e("RxLog", "onError()", e);
                super.error(context, e);
            }
        }));
    }
}