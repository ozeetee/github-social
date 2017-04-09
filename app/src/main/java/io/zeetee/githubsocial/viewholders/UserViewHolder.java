package io.zeetee.githubsocial.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.utils.IActions;

/**
 * By GT.
 */

public class UserViewHolder extends RecyclerView.ViewHolder {

    private final TextView mName;
    private final TextView mStats;
    private final TextView mInfo;
    private final SimpleDraweeView mImage;
    private final View mUserContainer;


    public UserViewHolder(View v, final IActions iUserActions) {
        super(v);
        mName = (TextView) v.findViewById(R.id.tv_name);
        mStats = (TextView) v.findViewById(R.id.tv_stats);
        mInfo = (TextView) v.findViewById(R.id.tv_info);
        mImage = (SimpleDraweeView) v.findViewById(R.id.user_image);
        mUserContainer = v.findViewById(R.id.user_container);
        mUserContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iUserActions.onUserClicked(((GithubUser)v.getTag()).login);
            }
        });
    }

    public void bind(GithubUser githubUser) {
        mName.setText(githubUser.login);
        mImage.setImageURI(githubUser.avatar_url);
        mUserContainer.setTag(githubUser);
    }
}
