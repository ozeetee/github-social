package io.zeetee.githubsocial.viewholders;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.models.GithubItem;
import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.utils.ColorDrawableHelper;
import io.zeetee.githubsocial.utils.IActions;

/**
 * By GT.
 */

public class RepoCardViewHolder extends GithubItemViewHolder {

    private final TextView mTitle;
    private final TextView mDescription;
    private final TextView mReproLanguage;
    private final TextView mRepoForks;
    private final TextView mRepoStars;
    private final TextView mLastUpdated;
    private final View mRepoDetailsContainer;

    private final View mUserContainer;
    private final TextView mUserName;
    private final SimpleDraweeView mUserImage;

    public RepoCardViewHolder(View itemView, final IActions iActions) {
        super(itemView);

        mUserContainer = itemView.findViewById(R.id.user_container);
        mUserName = (TextView) itemView.findViewById(R.id.user_name);
        mUserImage = (SimpleDraweeView) itemView.findViewById(R.id.user_image);

        mTitle = (TextView) itemView.findViewById(R.id.title);
        mDescription = (TextView) itemView.findViewById(R.id.description);
        mReproLanguage = (TextView) itemView.findViewById(R.id.repo_language);
        mRepoForks = (TextView) itemView.findViewById(R.id.repo_forks);
        mRepoStars = (TextView) itemView.findViewById(R.id.repo_stars);
        mLastUpdated = (TextView) itemView.findViewById(R.id.repo_last_updated);
        mRepoDetailsContainer = itemView.findViewById(R.id.repo_details_container);
        mRepoDetailsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GithubRepo repo = (GithubRepo)v.getTag();
                if(repo == null) return;
                iActions.showRepoDetails(repo.name,repo.owner.login);
            }
        });

        mUserContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GithubUser user = (GithubUser) v.getTag();
                if(user == null) return;
                iActions.onUserClicked(user.login);
            }
        });

    }

    public void bind(GithubItem githubItem) {
        if(githubItem == null) return;
        if(githubItem instanceof GithubRepo){
            GithubRepo repo = (GithubRepo) githubItem;
            mRepoDetailsContainer.setTag(repo);
            mTitle.setText(repo.name);
            if(TextUtils.isEmpty(repo.description)){
                mDescription.setVisibility(View.GONE);
            }else {
                mDescription.setVisibility(View.VISIBLE);
                mDescription.setText(repo.description);
            }
            mReproLanguage.setText(repo.language);
            Drawable img = ColorDrawableHelper.getInstance().getColorDrawableForLang(repo.language);
            mReproLanguage.setCompoundDrawables(img, null, null, null);

            mRepoForks.setText(String.valueOf(repo.forks));
            mRepoStars.setText(String.valueOf(repo.watchers_count));
            mLastUpdated.setVisibility(View.GONE);

            if(repo.owner == null){
                mUserContainer.setVisibility(View.GONE);
            }else {
                mUserContainer.setVisibility(View.VISIBLE);
                mUserName.setText(repo.owner.login);
                mUserImage.setImageURI(repo.owner.avatar_url);
                mUserContainer.setTag(repo.owner);
            }

        }


    }


}
