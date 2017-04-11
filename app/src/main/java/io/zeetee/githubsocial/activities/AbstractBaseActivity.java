package io.zeetee.githubsocial.activities;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.UnknownHostException;

import io.reactivex.functions.Consumer;
import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.IActions;
import io.zeetee.githubsocial.utils.Utils;

/**
 * By GT.
 */

public abstract class AbstractBaseActivity extends AppCompatActivity implements IActions{

    private static final int REQUEST_CODE_LOGIN = 100;
    protected Toolbar mToolbar;
    private View mProgressAndErrorContainer;
    private View mProgressBar;
    private View mErrorContainer;
    private TextView mErrorTitle;
    private TextView mErrorMessage;
    protected Button mErrorResolveButton;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setupActivity();
    }

    @Override
    public void onUserClicked(String userName) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(GSConstants.USER_NAME, userName);
        startActivity(intent);
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
    public void showStarredRepos() {
        Intent intent = new Intent(this, RepoListActivity.class);
        intent.putExtra(GSConstants.USER_NAME, GSConstants.ME);
        intent.putExtra(GSConstants.STARRED, true);
        startActivity(intent);
    }

    @Override
    public void showRepoDetails(String repoName, String repoOwner){
        Intent intent = new Intent(this, RepoDetailsActivity.class);
        intent.putExtra(GSConstants.REPO_NAME,repoName);
        intent.putExtra(GSConstants.USER_NAME,repoOwner);
        startActivity(intent);
    }


    protected Consumer<Throwable> throwableConsumer = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            Log.e("GTGT", "Error while executing network operation", throwable);
        }
    };


    protected Consumer<Throwable> fullScreenErrorConsumer = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            Log.e("GTGT", "Error while executing network operation",throwable);
            showFullScreenError(throwable);
        }
    };

    protected void setupActivity(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mProgressAndErrorContainer = findViewById(R.id.progress_and_error_container);
        mErrorTitle = (TextView) findViewById(R.id.error_title);
        mErrorMessage = (TextView) findViewById(R.id.error_message);
        mProgressBar = findViewById(R.id.progress_bar);
        mErrorResolveButton = (Button) findViewById(R.id.btn_error_resolve);
        mErrorContainer = findViewById(R.id.error_container);
    }

    protected void showScreenLoading(){
        mProgressAndErrorContainer.setVisibility(View.VISIBLE);
        hideContent();
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorContainer.setVisibility(View.GONE);
    }

    protected void showScreenContent(){
        mProgressAndErrorContainer.setVisibility(View.GONE);
        showContent();
    }

    protected void showFullScreenError(String title, String message){
        hideContent();
        mProgressBar.setVisibility(View.GONE);
        mProgressAndErrorContainer.setVisibility(View.VISIBLE);
        mErrorContainer.setVisibility(View.VISIBLE);
        mErrorTitle.setText(title);
        if(message != null) mErrorMessage.setText(message);
    }

    protected void showFullScreenError(Throwable throwable){
        if(throwable instanceof UnknownHostException){
            //Usually due to not able to connect to internet.
            mErrorTitle.setText(R.string.unable_to_connect);
            mErrorMessage.setText(R.string.check_internet_connection);
            return;
        }

        int code = Utils.getHttpStatusCode(throwable);
        String message = Utils.getServerErrorMessage(throwable);
        showFullScreenError("Server Error" + ": " + String.valueOf(code),message);
    }

    @Override
    public void showLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    public abstract void hideContent();

    public abstract void showContent();
}
