package io.zeetee.githubsocial.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.utils.IActions;
import io.zeetee.githubsocial.viewholders.RepoListViewHolder;

/**
 * By GT.
 */

public class RepoListAdapter extends RecyclerView.Adapter<RepoListViewHolder>{

    private List<GithubRepo> repos = new ArrayList<>(0);
    private final IActions iActions;

    public RepoListAdapter(IActions iActions) {
        this.iActions = iActions;
    }

    public void setRepos(List<GithubRepo> repos) {
        this.repos = repos == null ? new ArrayList<GithubRepo>(0):repos;
        notifyDataSetChanged();
    }

    @Override
    public RepoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_repository,parent,false);
        return new RepoListViewHolder(v,iActions);
    }

    @Override
    public void onBindViewHolder(RepoListViewHolder holder, int position) {
        holder.bind(repos.get(position));
    }

    @Override
    public int getItemCount() {
        return repos.size();
    }
}
