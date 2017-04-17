package io.zeetee.githubsocial.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import io.zeetee.githubsocial.models.GithubItem;
import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.network.RestApi;

/**
 * By GT.
 */

public class ActionsManager {

    private static ActionsManager sharedInstance;

    private Map<Long, Disposable> currentServerOperations = new GithubItemLRU<>(5);

    private ActionsManager() {

    }

    public static ActionsManager getSharedInstance() {
        if(sharedInstance == null){
            synchronized (ActionsManager.class){
                if (sharedInstance == null){
                    sharedInstance = new ActionsManager();
                }
            }
        }
        return sharedInstance;
    }





    public static class GithubItemLRU<K,V> extends LinkedHashMap<K,V> {
        private int cacheSize;

        public GithubItemLRU(int size) {
            super(5, .75F, true);
            this.cacheSize = size;
        }

        @Override
        protected boolean removeEldestEntry(Entry<K, V> eldest) {
            return size() > cacheSize;
        }
    }
}
