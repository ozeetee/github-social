package io.zeetee.githubsocial.activities;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import io.reactivex.functions.Consumer;
import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.adapters.GithubItemAdapter;
import io.zeetee.githubsocial.bus.RxEvents;
import io.zeetee.githubsocial.models.GithubItem;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.UserProfileManager;
import io.zeetee.githubsocial.utils.Utils;
import io.zeetee.githubsocial.utils.VerticalSpaceItemDecoration;

/**
 * By GT.
 *
 */
public abstract class AbstractListActivity extends AbstractPushActivity{

    protected int page = 1;
    protected boolean isMorePage = true;
    protected boolean isLoading = false;
    protected LinearLayoutManager layoutManager;
    protected RecyclerView mRecyclerView;
    protected GithubItemAdapter githubItemAdapter;
    private SwipeRefreshLayout swipeContainer;
    protected Consumer<Throwable> currentErrorConsumer;
    private View mEmptyMessage;

    protected RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {}

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if(isMorePage && !isLoading){
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if(totalItemCount - lastVisibleItem < GSConstants.THRESHOLD){
                    currentErrorConsumer = snackBarErrorConsumer;
                    fetchList();
                }
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getActivityLayout());
        mEmptyMessage = findViewById(R.id.empty_message);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        if(isFormatCard()){
            //Give 16DP spacing for cards
            //Recycler View Spacing
            int verticalSpacing = getResources().getDimensionPixelSize(R.dimen.item_vertical_spacing);
            VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(verticalSpacing);
            mRecyclerView.addItemDecoration(verticalSpaceItemDecoration);
        }


        githubItemAdapter = new GithubItemAdapter(this,isFormatCard());
        mRecyclerView.setAdapter(githubItemAdapter);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                currentErrorConsumer = snackBarErrorConsumer;
                fetchList();
                UserProfileManager.getSharedInstance().checkFetchUserInfo();
            }
        });

        mRecyclerView.addOnScrollListener(onScrollListener);

        mErrorResolveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScreenLoading();
                page = 1;
                currentErrorConsumer = fullScreenErrorConsumer;
                fetchList();
                UserProfileManager.getSharedInstance().checkFetchUserInfo();
            }
        });
        currentErrorConsumer = fullScreenErrorConsumer;
        hideEmptyListMessage();
    }

    protected void fetchList(){
        isLoading = true;
        hideEmptyListMessage();
    }

    protected abstract boolean isFormatCard();

    protected abstract  @LayoutRes int getActivityLayout();

    protected Consumer<List<? extends GithubItem>> listConsumer = new Consumer<List<? extends GithubItem>>() {

        @Override
        public void accept(List<? extends GithubItem> githubItems) throws Exception {
            isLoading = false;
            //We need to fill in that the item is starred or not
            if(page == 1){
                showScreenContent();
                swipeContainer.setRefreshing(false);
                githubItemAdapter.setGithubItems(githubItems);
            }else{
                githubItemAdapter.addGithubItems(githubItems);
            }
            if(githubItems == null || githubItems.isEmpty()){
                isMorePage = false;
                if(page == 1){
                    showEmptyListMessage();
                }
                return;
            }

            page++;
        }
    };

    protected void showEmptyListMessage(){
        if(mEmptyMessage != null) mEmptyMessage.setVisibility(View.VISIBLE);
    }

    protected void hideEmptyListMessage(){
        if(mEmptyMessage != null) mEmptyMessage.setVisibility(View.GONE);
    }


    @Override
    public void hideContent() {
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showContent() {
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    protected void onGithubItemChanged(GithubItem item){
        githubItemAdapter.notifyItemChanged(item);
    }

    public Consumer<Throwable> fullScreenErrorConsumer = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            swipeContainer.setRefreshing(false);
            showFullScreenError(throwable);
        }
    };

    private Consumer<Throwable> snackBarErrorConsumer = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            swipeContainer.setRefreshing(false);
            String message = Utils.getErrorMessage(throwable);
            Snackbar.make(mRecyclerView,message,Snackbar.LENGTH_LONG).show();
        }
    };

    protected Consumer<Throwable> getListErrorConsumer(){
        if(currentErrorConsumer == null) currentErrorConsumer = snackBarErrorConsumer;
        return currentErrorConsumer;
    }

    protected Consumer<Object> generalBusEventConsumer = new Consumer<Object>() {

        @Override
        public void accept(Object o) throws Exception {

            if(o instanceof RxEvents.RepoStarredEvent){
                onGithubItemChanged(((RxEvents.RepoStarredEvent)o).githubRepo);
            }

            if(o instanceof RxEvents.RepoUnStarredEvent){
                onGithubItemChanged(((RxEvents.RepoUnStarredEvent)o).githubRepo);
            }

            if(o instanceof RxEvents.UserFollowedEvent){
                onGithubItemChanged(((RxEvents.UserFollowedEvent)o).githubUser);
            }

            if(o instanceof RxEvents.UserUnFollowedEvent){
                onGithubItemChanged(((RxEvents.UserUnFollowedEvent)o).githubUser);
            }
        }
    };


}
