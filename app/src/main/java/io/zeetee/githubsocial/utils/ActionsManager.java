package io.zeetee.githubsocial.utils;

import android.support.annotation.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
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
import retrofit2.Response;

/**
 * By GT.
 */

public class ActionsManager {

    private static ActionsManager sharedInstance;

    private Map<Long, Subject<GithubItem>> starSubjectMap = new GithubItemLRU<>(5);
    private Map<Long, Disposable> currentServerOpetaions = new GithubItemLRU<>(5);

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


    public void followUnFollowUser(GithubUser githubUser){
        if(githubUser == null) return;
        if(!UserManager.getSharedInstance().isLoggedIn()) return;
        if(UserManager.getSharedInstance().isMe(githubUser.login)) return;

        githubUser.starState.currentState = !githubUser.starState.currentState;
        if(githubUser.starState.currentState) UserProfileManager.getSharedInstance().userFollowed(githubUser);
        else UserProfileManager.getSharedInstance().userUnFollowed(githubUser);
        //Debonce the event
        getGithubItemSubject(githubUser).onNext(githubUser);
    }


    private Subject<GithubItem> getGithubItemSubject(@NonNull final GithubItem githubItem){
        Subject<GithubItem> subject = starSubjectMap.get(githubItem.id);
        if(subject == null){
            subject = PublishSubject.create();
            subject.debounce(1000L, TimeUnit.MILLISECONDS);
            starSubjectMap.put(githubItem.id,subject);

            subject.subscribeOn(Schedulers.io());
            subject.observeOn(Schedulers.io());
            subject.subscribe(new Consumer<GithubItem>() {
                @Override
                public void accept(GithubItem item) throws Exception {
                    doStarUnStarWork(item);
                }
            });
        }
        return subject;
    }


    private void doStarUnStarWork(GithubItem item) {
        if (item.starState.currentState == item.starState.originalState) return;// Observable.just(""); // Implement
        item.starState.originalState = item.starState.currentState;
        final boolean starringCall = item.starState.originalState;
        Observable<Response<Void>> observable;

        if (currentServerOpetaions.get(item.id) != null) {
            Disposable disposable = currentServerOpetaions.get(item.id);
            if (!disposable.isDisposed()) disposable.dispose();
        }

        if (item instanceof GithubUser) {
            final GithubUser githubUser = (GithubUser) item;

            if (starringCall) {
                //Follow the user
                observable = RestApi.followUser(githubUser);

            } else {
                //UnFollow user
                observable = RestApi.unFollowUser(githubUser);
            }

            Disposable operation = observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Response<Void>>() {
                        @Override
                        public void accept(Response<Void> voidResponse) throws Exception {

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            //Revert back the operation.
                        }
                    });
            currentServerOpetaions.put(item.id, operation);
            return;// operation;
        }
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
