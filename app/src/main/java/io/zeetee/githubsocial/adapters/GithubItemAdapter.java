package io.zeetee.githubsocial.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.models.GithubItem;
import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.IActions;
import io.zeetee.githubsocial.viewholders.GithubItemViewHolder;
import io.zeetee.githubsocial.viewholders.GithubUserViewHolder;
import io.zeetee.githubsocial.viewholders.GithubRepoViewHolder;

/**
 * By GT.
 */
public class GithubItemAdapter extends RecyclerView.Adapter<GithubItemViewHolder>{

    private static final int VIEW_TYPE_UNKNOWN = -1;
    private static final int VIEW_TYPE_USER = 0;
    private static final int VIEW_TYPE_USER_CARD = 1;
    private static final int VIEW_TYPE_REPO_CARD = 2;

    private final IActions iUserActions;
    private final List<GithubItem> githubItems= new ArrayList<>();
    private final boolean formatCards;

    public GithubItemAdapter(IActions iUserActions, boolean formatCards) {
        this.iUserActions = iUserActions;
        this.formatCards = formatCards;
    }

    @Override
    public GithubItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflator = LayoutInflater.from(parent.getContext());

        if(viewType == VIEW_TYPE_USER){
            View v = inflator.inflate(R.layout.row_user_list,parent,false);
            return new GithubUserViewHolder(v, iUserActions);
        }

        if(viewType == VIEW_TYPE_USER_CARD){
            View v = inflator.inflate(R.layout.card_user,parent,false);
            return new GithubUserViewHolder(v, iUserActions);
        }

        if(viewType == VIEW_TYPE_REPO_CARD){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_repository,parent,false);
            return new GithubRepoViewHolder(v, iUserActions);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(GithubItemViewHolder holder, int position) {
        holder.bind(githubItems.get(position));
    }

    @Override
    public int getItemCount() {
        return githubItems.size();
    }

    public void setGithubItems(List<? extends GithubItem> githubItems){
        this.githubItems.clear();
        if(githubItems != null) this.githubItems.addAll(githubItems);
        notifyDataSetChanged();
    }

    public void addGithubItems(List<? extends GithubItem> githubItems){
        if(githubItems == null || githubItems.isEmpty()) return;
        int positionStart = this.githubItems.size();
        int itemCount = githubItems.size();
        this.githubItems.addAll(githubItems);
        notifyItemRangeInserted(positionStart,itemCount);
    }

    @Override
    public int getItemViewType(int position) {
        if(githubItems.get(position) instanceof GithubUser) return formatCards ? VIEW_TYPE_USER_CARD : VIEW_TYPE_USER;
        if(githubItems.get(position) instanceof GithubRepo) return VIEW_TYPE_REPO_CARD;
        else return VIEW_TYPE_UNKNOWN;
    }

    // name can be userName or githubRepo full name
    public int itemPosition(GithubItem item){
        if(item == null) return -1;
        for (int i = 0; i < githubItems.size(); i++) {
            if(githubItems.get(i) == null) continue;
            if(githubItems.get(i).id == item.id) return i;
        }
        return -1;
    }

    public void notifyItemChanged(GithubItem githubItem){
        int i = itemPosition(githubItem);
        if(i >= 0){
            notifyItemChanged(i);
        }
    }

}
