package io.zeetee.githubsocial.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.utils.IActions;
import io.zeetee.githubsocial.viewholders.UserListViewHolder;

/**
 * By GT.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListViewHolder> {

    private List<GithubUser> users = new ArrayList<>(1);
    private IActions iUserActions;

    public UserListAdapter(IActions iUserActions) {
        this.iUserActions = iUserActions;
    }

    @Override
    public UserListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_list,parent,false);
        return new UserListViewHolder(v,iUserActions);
    }

    @Override
    public void onBindViewHolder(UserListViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<GithubUser> githubUsers) {
        this.users = githubUsers == null ? new ArrayList<GithubUser>(0): githubUsers;
        notifyDataSetChanged();
    }

    public void addUsers(List<GithubUser> githubUsers){
        if(githubUsers == null || githubUsers.isEmpty()) return;
        if(this.users == null) this.users = new ArrayList<>();
        int positionStart = this.users.size();
        int itemCount = githubUsers.size();
        this.users.addAll(githubUsers);
        notifyItemRangeInserted(positionStart,itemCount);
    }
}
