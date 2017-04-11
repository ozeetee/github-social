package io.zeetee.githubsocial.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import io.zeetee.githubsocial.adapters.RepoListAdapter;
import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.network.RestApi;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.UserManager;
import io.zeetee.githubsocial.utils.VerticalSpaceItemDecoration;

/**
 * By GT.
 *
 */
public class RepoListActivity extends AbstractListActivity {

    private RecyclerView mRecyclerView;
    private RepoListAdapter repoListAdapter;
    private String userName;
    private boolean starred;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_list);
        if(getSupportActionBar() != null ) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent() != null){
            if(getIntent().getStringExtra(GSConstants.USER_NAME) != null) this.userName = getIntent().getStringExtra(GSConstants.USER_NAME);
            this.starred = getIntent().getBooleanExtra(GSConstants.STARRED,false);
        }

        setTitle(getScreenTitle());

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.addOnScrollListener(onScrollListener);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        int verticalSpacing = getResources().getDimensionPixelSize(R.dimen.item_vertical_spacing);
        VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(verticalSpacing);
        mRecyclerView.addItemDecoration(verticalSpaceItemDecoration);
        repoListAdapter = new RepoListAdapter(this);
        mRecyclerView.setAdapter(repoListAdapter);
        if(validateActivity()){
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
        isLoading = true;
        if(page == 1){
            showScreenLoading();
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
                .subscribe(consumer,fullScreenErrorConsumer);

    }

    private Observable<List<GithubRepo>> getApiObservable(){
        if(userName == null) return null;

        if(userName.equalsIgnoreCase(GSConstants.ME)){
            if(starred) return RestApi.meStarred(page,GSConstants.PER_PAGE);
            return RestApi.meRepos(page,GSConstants.PER_PAGE);
        }
        return RestApi.repos(userName,page,GSConstants.PER_PAGE);
    }


    private Consumer<List<GithubRepo>> consumer = new Consumer<List<GithubRepo>>() {
        @Override
        public void accept(List<GithubRepo> githubRepos) throws Exception {
            isLoading = false;
            if(page == 1){
                showScreenContent();
            }
            if(githubRepos == null || githubRepos.isEmpty()){
                isMorePage = false;
                return;
            }
            repoListAdapter.addRepos(githubRepos);
            page++;
        }
    };


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
    public void hideContent() {
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showContent() {
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
