package io.zeetee.githubsocial.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null ) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if(getIntent() != null){
            if(getIntent().getStringExtra(GSConstants.USER_NAME) != null) this.userName = getIntent().getStringExtra(GSConstants.USER_NAME);
            if(getIntent().getStringExtra(GSConstants.REPO_NAME) != null) this.repoName = getIntent().getStringExtra(GSConstants.REPO_NAME);
            this.listType = getIntent().getIntExtra(GSConstants.ListType.LIST_TYPE,0);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
        userListAdapter = new UserListAdapter(this);
        mRecyclerView.setAdapter(userListAdapter);
        fetchUserList();
        setListTitle();
    }

    private void fetchUserList(){
        if(TextUtils.isEmpty(userName)) return;
        Observable<List<GithubUser>> observable = getApiObservable();
        if(observable != null){
            observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(consumer,throwableConsumer);
        }
    }


    private Observable<List<GithubUser>> getApiObservable(){
        switch (listType){
            case GSConstants.ListType.FOLLOWERS:
                return RestApi.followers(userName);
            case GSConstants.ListType.FOLLOWING:
                return RestApi.following(userName);
            case GSConstants.ListType.STARGAZER:
                return RestApi.stargazer(repoName, userName);
            case GSConstants.ListType.WATCHERS:
                return RestApi.watchers(repoName, userName);
        }
        return null;
    }


    private void setListTitle(){
        switch (listType){
            case GSConstants.ListType.FOLLOWERS:
                setTitle("Followers");
                break;
            case GSConstants.ListType.FOLLOWING:
                setTitle("Following");
                break;
            case GSConstants.ListType.STARGAZER:
                setTitle("Stargazer");
                break;
            case GSConstants.ListType.WATCHERS:
                setTitle("Watchers");
                break;
        }
    }


    private Consumer<List<GithubUser>> consumer = new Consumer<List<GithubUser>>() {
        @Override
        public void accept(List<GithubUser> githubUsers) throws Exception {
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
