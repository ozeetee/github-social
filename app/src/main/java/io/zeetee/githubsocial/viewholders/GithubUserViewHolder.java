package io.zeetee.githubsocial.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.models.GithubItem;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.utils.IActions;
import io.zeetee.githubsocial.utils.UserManager;
import io.zeetee.githubsocial.utils.UserProfileManager;
import io.zeetee.githubsocial.utils.Utils;

/**
 * By GT.
 *
 */
public class GithubUserViewHolder extends GithubItemViewHolder {

    private final SimpleDraweeView mUserImage;
    private final TextView mUserName;
    private final Button mFollowButton;
    private final Button mUnFollowButton;
    private final IActions iActions;

    public GithubUserViewHolder(View itemView, final IActions iActions) {
        super(itemView);
        this.iActions = iActions;
        mUserImage = (SimpleDraweeView) itemView.findViewById(R.id.user_image);
        mUserName = (TextView) itemView.findViewById(R.id.user_name);
        mFollowButton = (Button) itemView.findViewById(R.id.btn_follow);
        mUnFollowButton = (Button) itemView.findViewById(R.id.btn_un_follow);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iActions.onUserClicked(((GithubUser)v.getTag()).login);
            }
        });

        mFollowButton.setOnClickListener(followClickListener);
        mUnFollowButton.setOnClickListener(followClickListener);
    }

    private View.OnClickListener followClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            iActions.followUnFollowClicked((GithubUser)v.getTag());
        }
    };

    public void bind(GithubItem githubItem) {
        if(githubItem == null) return;
        GithubUser githubUser = (GithubUser) githubItem;
        mUserImage.setImageURI(githubUser.avatar_url);
        mUserName.setText(githubUser.login);
        itemView.setTag(githubUser);
        mFollowButton.setTag(githubUser);
        mUnFollowButton.setTag(githubUser);

        if(UserManager.getSharedInstance().isMe(githubUser.login) || !Utils.isUser(githubUser)){
            mFollowButton.setVisibility(View.GONE);
            mUnFollowButton.setVisibility(View.GONE);
        }else{
            if(UserProfileManager.getSharedInstance().isFollowing(githubUser)){
                mFollowButton.setVisibility(View.GONE);
                mUnFollowButton.setVisibility(View.VISIBLE);
            }else{
                mFollowButton.setVisibility(View.VISIBLE);
                mUnFollowButton.setVisibility(View.GONE);
            }
        }


    }
}
