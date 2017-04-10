package io.zeetee.githubsocial.viewholders;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.utils.ColorDrawableHelper;
import io.zeetee.githubsocial.utils.IActions;

/**
 * By GT.
 */

public class RepoListViewHolder extends RecyclerView.ViewHolder {

    private final TextView mTitle;
    private final TextView mDescription;
    private final TextView mReproLanguage;
    private final TextView mRepoForks;
    private final TextView mRepoStars;
    private final TextView mLastUpdated;


    public RepoListViewHolder(View itemView, IActions iActions) {
        super(itemView);
        mTitle = (TextView) itemView.findViewById(R.id.title);
        mDescription = (TextView) itemView.findViewById(R.id.description);
        mReproLanguage = (TextView) itemView.findViewById(R.id.repo_language);
        mRepoForks = (TextView) itemView.findViewById(R.id.repo_forks);
        mRepoStars = (TextView) itemView.findViewById(R.id.repo_stars);
        mLastUpdated = (TextView) itemView.findViewById(R.id.repo_last_updated);
    }

    public void bind(GithubRepo repo){
        if(repo == null) return;
        mTitle.setText(repo.name);
        if(TextUtils.isEmpty(repo.description)){
            mDescription.setVisibility(View.GONE);
        }else {
            mDescription.setVisibility(View.VISIBLE);
            mDescription.setText(repo.description);
        }
        mReproLanguage.setText(repo.language);
        Drawable img = ColorDrawableHelper.getInstance().getColorDrawableForString(repo.language);
        mReproLanguage.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

        mRepoForks.setText(String.valueOf(repo.forks));
        mRepoStars.setText(String.valueOf(repo.watchers_count));
//        mLastUpdated.setText(String.valueOf(repo.updated_at));
        mLastUpdated.setVisibility(View.GONE);


    }


}
