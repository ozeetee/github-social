package io.zeetee.githubsocial.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import io.reactivex.functions.Consumer;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.IActions;

/**
 * By GT.
 */

public abstract class AbstractBaseActivity extends AppCompatActivity implements IActions{

    @Override
    public void onUserClicked(String userName) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(GSConstants.USER_NAME, userName);
        startActivity(intent);
    }

    @Override
    public void onRepoClicked(String repoName) {

    }

    @Override
    public void showUserList(int userListType, String userName){
        Intent intent = new Intent(this, UserListActivity.class);
        intent.putExtra(GSConstants.ListType.LIST_TYPE,userListType);
        intent.putExtra(GSConstants.USER_NAME, userName);
        startActivity(intent);
    }

    @Override
    public void showUserList(int userListType, String repoName, String ownerUserName){
        Intent intent = new Intent(this, UserListActivity.class);
        intent.putExtra(GSConstants.ListType.LIST_TYPE,userListType);
        intent.putExtra(GSConstants.REPO_NAME, repoName);
        intent.putExtra(GSConstants.USER_NAME, ownerUserName);
        startActivity(intent);
    }
    @Override
    public void showUserRepos(String userName){
        Intent intent = new Intent(this, RepoListActivity.class);
        intent.putExtra(GSConstants.USER_NAME, userName);
        startActivity(intent);
    }

    @Override
    public void showRepoDetails(String url){
        Intent intent = new Intent(this, RepoDetailsActivity.class);
        intent.putExtra(GSConstants.URL,url);
        startActivity(intent);
    }


    protected Consumer<Throwable> throwableConsumer = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            Log.e("GTGT", "Error while executing network operation",throwable);
        }
    };

}
