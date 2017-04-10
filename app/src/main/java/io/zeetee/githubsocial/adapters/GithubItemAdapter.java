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
import io.zeetee.githubsocial.utils.IActions;
import io.zeetee.githubsocial.viewholders.GithubItemViewHolder;
import io.zeetee.githubsocial.viewholders.RepoCardViewHolder;
import io.zeetee.githubsocial.viewholders.UserCardViewHolder;

/**
 * By GT.
 */
public class GithubItemAdapter extends RecyclerView.Adapter<GithubItemViewHolder>{

    private static final int VIEW_TYPE_UNKNOWN = -1;
    private static final int VIEW_TYPE_USER = 0;
    private static final int VIEW_TYPE_REPO = 1;

    private final IActions iUserActions;
    private List<GithubItem> githubItems= new ArrayList<>();

    public GithubItemAdapter(IActions iUserActions) {
        this.iUserActions = iUserActions;
    }

    @Override
    public GithubItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_USER){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_user,parent,false);
            return new UserCardViewHolder(v, iUserActions);
        }

        if(viewType == VIEW_TYPE_REPO){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_repository,parent,false);
            return new RepoCardViewHolder(v, iUserActions);
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

    public void setGithubItems(List<GithubItem> githubItems){
        this.githubItems = githubItems == null ? new ArrayList<GithubItem>(1) : githubItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(githubItems.get(position) instanceof GithubUser) return VIEW_TYPE_USER;
        if(githubItems.get(position) instanceof GithubRepo) return VIEW_TYPE_REPO;
        else return VIEW_TYPE_UNKNOWN;
    }
}
