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
    private GithubItemAdapter githubItemAdapter;
    private SwipeRefreshLayout swipeContainer;
    protected Consumer<Throwable> currentErrorConsumer;

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
    }

    protected abstract void fetchList();

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
                return;
            }

            page++;
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

}
