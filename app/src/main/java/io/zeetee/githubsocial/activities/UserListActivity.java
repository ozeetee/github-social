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
import io.zeetee.githubsocial.models.GithubItem;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.network.RestApi;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.UserManager;
import io.zeetee.githubsocial.utils.UserProfileManager;

public class UserListActivity extends AbstractListActivity {

    private String userName;
    private int listType;
    private String repoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar() != null ) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent() != null){
            if(getIntent().getStringExtra(GSConstants.USER_NAME) != null) this.userName = getIntent().getStringExtra(GSConstants.USER_NAME);
            if(getIntent().getStringExtra(GSConstants.REPO_NAME) != null) this.repoName = getIntent().getStringExtra(GSConstants.REPO_NAME);
            this.listType = getIntent().getIntExtra(GSConstants.ListType.LIST_TYPE,0);
        }
        setListTitle();
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

    protected void fetchList(){
        if(userName == null) return;
        isLoading = true;
        Observable<List<GithubUser>> observable = getApiObservable();
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
        Consumer<List<? extends GithubItem>> consumer = (isMe() && listType == GSConstants.ListType.FOLLOWING) ? listConsumerWrapper : listConsumer;

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer, getListErrorConsumer());
    }


    private Consumer<List<? extends GithubItem>> listConsumerWrapper = new Consumer<List<? extends GithubItem>>(){

        @Override
        public void accept(List<? extends GithubItem> githubItems) throws Exception {
            listConsumer.accept(githubItems);
//            UserProfileManager.getSharedInstance().setFollowing((List<GithubUser>) githubItems);
        }
    };

    private boolean isMe(){
       return UserManager.getSharedInstance().isMe(userName);
    }

    private Observable<List<GithubUser>> getApiObservable(){
        if(userName == null) return null;
        switch (listType){
            case GSConstants.ListType.FOLLOWERS:
                return userName.equalsIgnoreCase(GSConstants.ME) ? RestApi.meFollowers(page, GSConstants.PER_PAGE) :RestApi.followers(userName, page, GSConstants.PER_PAGE);
            case GSConstants.ListType.FOLLOWING:
                return userName.equalsIgnoreCase(GSConstants.ME) ? RestApi.meFollowing(page, GSConstants.PER_PAGE) :RestApi.following(userName, page, GSConstants.PER_PAGE);
            case GSConstants.ListType.STARGAZER:
                return RestApi.stargazer(repoName, userName, page, GSConstants.PER_PAGE);
            case GSConstants.ListType.WATCHERS:
                return RestApi.watchers(repoName, userName, page, GSConstants.PER_PAGE);
            case GSConstants.ListType.ORG_MEMBERS:
                return RestApi.orgMembers(userName, page, GSConstants.PER_PAGE);
        }
        return null;
    }


    private void setListTitle(){
        setTitle(getScreenTitle());
    }

    private String getUserNamePrefix(){
        if(TextUtils.isEmpty(this.userName)) return "";
        if(this.userName.equalsIgnoreCase(GSConstants.ME)) return "Your ";
        return this.userName + " ";
    }

    private String getScreenTitle(){
        switch (listType){
            case GSConstants.ListType.FOLLOWERS:
                return getUserNamePrefix() + " Followers";
            case GSConstants.ListType.FOLLOWING:
                return getUserNamePrefix() + " Following";
            case GSConstants.ListType.STARGAZER:
                return this.repoName + " Stargazer";
            case GSConstants.ListType.WATCHERS:
                return  this.repoName + " Watchers";
            case GSConstants.ListType.ORG_MEMBERS:
                return  this.userName + " Members";
        }
        return "Users";
    }

    @Override
    protected boolean isFormatCard() {
        return false;
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_user_list;
    }

    @Override
    public Consumer<Object> getRxBusConsumer() {
        return generalBusEventConsumer;
    }



}
