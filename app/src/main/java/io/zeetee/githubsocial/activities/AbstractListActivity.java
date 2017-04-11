package io.zeetee.githubsocial.activities;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import io.zeetee.githubsocial.utils.GSConstants;

/**
 * By GT.
 */

public abstract class AbstractListActivity extends AbstractPushActivity{

    protected int page = 1;
    protected boolean isMorePage = true;
    protected boolean isLoading = false;
    protected LinearLayoutManager layoutManager;

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
                    fetchList();
                }
            }
        }
    };



    protected abstract void fetchList();

}
