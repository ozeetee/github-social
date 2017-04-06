package io.zeetee.githubsocial.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.utils.IUserActions;

/**
 * By GT.
 */

public class UserListViewHolder extends RecyclerView.ViewHolder {

    private final SimpleDraweeView mUserImage;
    private final TextView mUserName;
    private final ImageView mUserStar;


    public UserListViewHolder(View itemView, final IUserActions iUserActions) {
        super(itemView);
        mUserImage = (SimpleDraweeView) itemView.findViewById(R.id.user_image);
        mUserName = (TextView) itemView.findViewById(R.id.user_name);
        mUserStar = (ImageView) itemView.findViewById(R.id.user_star);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iUserActions.onUserClicked(((GithubUser)v.getTag()).login);
            }
        });
    }

    public void bind(GithubUser githubUser) {
        if(githubUser == null) return;
        mUserImage.setImageURI(githubUser.avatar_url);
        mUserName.setText(githubUser.login);
        itemView.setTag(githubUser);
    }
}
