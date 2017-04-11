package io.zeetee.githubsocial.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.utils.IActions;
import io.zeetee.githubsocial.utils.UserManager;
import io.zeetee.githubsocial.utils.UserProfileManager;

/**
 * By GT.
 */

public class UserListViewHolder extends RecyclerView.ViewHolder {

    private final SimpleDraweeView mUserImage;
    private final TextView mUserName;

    private final Button mFollowButton;
    private final Button mUnFollowButton;

    public UserListViewHolder(View itemView, final IActions iUserActions) {
        super(itemView);
        mUserImage = (SimpleDraweeView) itemView.findViewById(R.id.user_image);
        mUserName = (TextView) itemView.findViewById(R.id.user_name);
        mFollowButton = (Button) itemView.findViewById(R.id.btn_follow);
        mUnFollowButton = (Button) itemView.findViewById(R.id.btn_un_follow);

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
        mFollowButton.setTag(githubUser);
        mUnFollowButton.setTag(githubUser);

        if(UserManager.getSharedInstance().isMe(githubUser.login)){
            mFollowButton.setVisibility(View.GONE);
            mUnFollowButton.setVisibility(View.GONE);
        }else{
            if(UserProfileManager.getSharedInstance().isFollowing(githubUser.login)){
                mFollowButton.setVisibility(View.GONE);
                mUnFollowButton.setVisibility(View.VISIBLE);
            }else{
                mFollowButton.setVisibility(View.VISIBLE);
                mUnFollowButton.setVisibility(View.GONE);
            }
        }


    }
}
