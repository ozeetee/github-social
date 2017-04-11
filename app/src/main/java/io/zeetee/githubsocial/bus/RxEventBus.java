package io.zeetee.githubsocial.bus;

import android.os.Handler;
import android.os.Looper;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import io.reactivex.Observable;

/**
 * By GT.
 */
public class RxEventBus {

    private final Relay<Object> bus;

    private static RxEventBus instance;

    private RxEventBus(){
        bus = PublishRelay.create().toSerialized();
    }

    public static RxEventBus getInstance() {
        if(instance == null){
            synchronized (RxEventBus.class){
                if (instance == null){
                    instance = new RxEventBus();
                }
            }
        }
        return instance;
    }

    public Observable<Object> getEventBus(){
        return bus;
    }

    public void post(final Object event) {
        //Always post the events to UI Thread.
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                bus.accept(event);
            }
        });
    }
}
