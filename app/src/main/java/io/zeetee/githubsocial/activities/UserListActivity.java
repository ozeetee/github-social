package io.zeetee.githubsocial.activities;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.adapters.UserListAdapter;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.network.RestApi;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.UserManager;

public class UserListActivity extends AbstractPushActivity {

    private RecyclerView mRecyclerView;
    private UserListAdapter userListAdapter;
    private String userName;
    private int listType;
    private String repoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        if(getSupportActionBar() != null ) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent() != null){
            if(getIntent().getStringExtra(GSConstants.USER_NAME) != null) this.userName = getIntent().getStringExtra(GSConstants.USER_NAME);
            if(getIntent().getStringExtra(GSConstants.REPO_NAME) != null) this.repoName = getIntent().getStringExtra(GSConstants.REPO_NAME);
            this.listType = getIntent().getIntExtra(GSConstants.ListType.LIST_TYPE,0);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        userListAdapter = new UserListAdapter(this);
        mRecyclerView.setAdapter(userListAdapter);
        setListTitle();
        fetchUserList();
    }

    private void fetchUserList(){
        if(TextUtils.isEmpty(userName)) {
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

        if(userName.equalsIgnoreCase(GSConstants.ME) && !UserManager.getSharedInstance().isLoggedIn()){
            showFullScreenError("Please Login", "You need to login to view " + getScreenTitle());
            mErrorResolveButton.setText("Login");
            mErrorResolveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLoginScreen();
                }
            });
            return;
        }
        showScreenLoading();
        Observable<List<GithubUser>> observable = getApiObservable();
        if(observable != null){
            observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(consumer,fullScreenErrorConsumer);
        }
    }


    private Observable<List<GithubUser>> getApiObservable(){
        if(userName == null) return null;
        switch (listType){
            case GSConstants.ListType.FOLLOWERS:
                return userName.equalsIgnoreCase(GSConstants.ME) ? RestApi.meFollowers() :RestApi.followers(userName);
            case GSConstants.ListType.FOLLOWING:
                return userName.equalsIgnoreCase(GSConstants.ME) ? RestApi.meFollowing() :RestApi.following(userName);
            case GSConstants.ListType.STARGAZER:
                return RestApi.stargazer(repoName, userName);
            case GSConstants.ListType.WATCHERS:
                return RestApi.watchers(repoName, userName);
            case GSConstants.ListType.ORG_MEMBERS:
                return RestApi.orgMembers(userName);
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

    private Consumer<List<GithubUser>> consumer = new Consumer<List<GithubUser>>() {
        @Override
        public void accept(List<GithubUser> githubUsers) throws Exception {
            showScreenContent();
            userListAdapter.setUsers(githubUsers);
        }
    };

    @Override
    public void hideContent() {
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showContent() {
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
