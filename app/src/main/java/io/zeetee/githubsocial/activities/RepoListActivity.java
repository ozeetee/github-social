package io.zeetee.githubsocial.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.bus.RxEvents;
import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.network.RestApi;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.UserManager;

/**
 * By GT.
 *
 */
public class RepoListActivity extends AbstractListActivity {

    private String userName;
    private boolean starred;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null ) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent() != null){
            if(getIntent().getStringExtra(GSConstants.USER_NAME) != null) this.userName = getIntent().getStringExtra(GSConstants.USER_NAME);
            this.starred = getIntent().getBooleanExtra(GSConstants.STARRED,false);
        }
        setTitle(getScreenTitle());
        if(validateActivity()){
            showScreenLoading();
            fetchList();
        }
    }


    private boolean validateActivity(){
        if(TextUtils.isEmpty(userName)) {
            showFullScreenError("Internal Error","UserName is null");
            mErrorResolveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            mErrorResolveButton.setText("Go Back");
            return false;
        }

        if(userName.equalsIgnoreCase(GSConstants.ME) && !UserManager.getSharedInstance().isLoggedIn()){
            showFullScreenError("Please Login", "You need to login to view " + getScreenTitle());
            mErrorResolveButton.setText("Login");
            mErrorResolveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLoginScreen();
                }
            });
            return false;
        }
        return true;
    }

    @Override
    protected void fetchList(){
        isLoading = true;
        if(page == 1){
            mErrorResolveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fetchList();
                }
            });
        }

        Observable<List<GithubRepo>> observable = getApiObservable();

        if(observable == null){
            showFullScreenError("Internal Error","UserName is null");
            mErrorResolveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            mErrorResolveButton.setText("Go Back");
            return;
        }
        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listConsumer,getListErrorConsumer());

    }

    @Override
    protected boolean isFormatCard() {
        return true;
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_repo_list;
    }

    private Observable<List<GithubRepo>> getApiObservable(){
        if(userName == null) return null;

        if(userName.equalsIgnoreCase(GSConstants.ME)){
            if(starred) return RestApi.meStarred(page,GSConstants.PER_PAGE);
            return RestApi.meRepos(page,GSConstants.PER_PAGE);
        }
        return RestApi.repos(userName,page,GSConstants.PER_PAGE);
    }



    private String getScreenTitle(){
        if(starred){
            return "Starred Repos";
        }
        if(this.userName != null && this.userName.equalsIgnoreCase(GSConstants.ME)){
            return "Your Repos";
        }

        return this.userName == null ? "Repos" : this.userName + " repos";
    }

    @Override
    public Consumer<Object> getRxBusConsumer() {
        return busEventConsumer;
    }

    private Consumer<Object> busEventConsumer = new Consumer<Object>() {
        @Override
        public void accept(Object o) throws Exception {

            if(o instanceof RxEvents.RepoStarredEvent){
                onGithubItemChanged(((RxEvents.RepoStarredEvent)o).githubRepo);
            }

            if(o instanceof RxEvents.RepoUnStarredEvent){
                onGithubItemChanged(((RxEvents.RepoUnStarredEvent)o).githubRepo);
            }
        }
    };
}
