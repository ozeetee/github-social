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
import io.zeetee.githubsocial.viewholders.RepoCardViewHolder;

/**
 * By GT.
 */

public class RepoListAdapter extends RecyclerView.Adapter<RepoCardViewHolder>{

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
    public RepoCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_repository,parent,false);
        return new RepoCardViewHolder(v,iActions);
    }

    @Override
    public void onBindViewHolder(RepoCardViewHolder holder, int position) {
        holder.bind(repos.get(position));
    }

    @Override
    public int getItemCount() {
        return repos.size();
    }

    public void addRepos(List<GithubRepo> githubRepos) {
        if(githubRepos == null || githubRepos.isEmpty()) return;

        if(this.repos == null) this.repos = new ArrayList<>();

        int positionStart = this.repos.size();
        int itemCount = githubRepos.size();

        this.repos.addAll(githubRepos);
        notifyItemRangeInserted(positionStart,itemCount);
    }
}
