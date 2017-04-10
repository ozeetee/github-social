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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.adapters.RepoListAdapter;
import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.network.RestApi;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.VerticalSpaceItemDecoration;

/**
 * By GT.
 *
 */
public class RepoListActivity extends AbstractPushActivity {

    private RecyclerView mRecyclerView;
    private RepoListAdapter repoListAdapter;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_list);
        if(getSupportActionBar() != null ) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent() != null){
            if(getIntent().getStringExtra(GSConstants.USER_NAME) != null) this.userName = getIntent().getStringExtra(GSConstants.USER_NAME);
        }

        setTitle(this.userName + " repos");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        int verticalSpacing = getResources().getDimensionPixelSize(R.dimen.item_vertical_spacing);
        VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(verticalSpacing);
        mRecyclerView.addItemDecoration(verticalSpaceItemDecoration);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
        repoListAdapter = new RepoListAdapter(this);
        mRecyclerView.setAdapter(repoListAdapter);
        fetchRepos();
        mErrorResolveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchRepos();
            }
        });
    }

    private void fetchRepos(){
        if(TextUtils.isEmpty(userName)) return;
        showScreenLoading();
        RestApi.repos(userName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer,fullScreenErrorConsumer);

    }


    private Consumer<List<GithubRepo>> consumer = new Consumer<List<GithubRepo>>() {
        @Override
        public void accept(List<GithubRepo> githubRepos) throws Exception {
            showScreenContent();
            repoListAdapter.setRepos(githubRepos);
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
