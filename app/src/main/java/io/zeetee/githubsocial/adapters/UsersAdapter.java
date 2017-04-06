package io.zeetee.githubsocial.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.utils.IUserActions;
import io.zeetee.githubsocial.viewholders.UserViewHolder;

/**
 * By GT.
 */

public class UsersAdapter extends RecyclerView.Adapter<UserViewHolder>{

    private final IUserActions iUserActions;
    private List<GithubUser> users = new ArrayList<>();

    public UsersAdapter(IUserActions iUserActions) {
        this.iUserActions = iUserActions;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_card,parent,false);
        return new UserViewHolder(v, iUserActions);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<GithubUser> users){
        this.users = users == null ? new ArrayList<GithubUser>(1) : users;
        notifyDataSetChanged();
    }

}
