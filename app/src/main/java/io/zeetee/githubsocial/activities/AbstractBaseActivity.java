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

/**
 * By GT.
 */

public abstract class AbstractBaseActivity extends AppCompatActivity implements IActions{

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

    protected void showFullScreenError(Throwable throwable){
        hideContent();
        mProgressBar.setVisibility(View.GONE);
        mErrorContainer.setVisibility(View.VISIBLE);
        if(throwable instanceof UnknownHostException){
            //Usually due to not able to connect to internet.
            mErrorTitle.setText(R.string.unable_to_connect);
            mErrorMessage.setText(R.string.check_internet_connection);
            return;
        }

        int code = 0 ;
        String message = null;
        StringBuilder titleBuilder = new StringBuilder();

        if(throwable instanceof retrofit2.adapter.rxjava2.HttpException){
            retrofit2.adapter.rxjava2.HttpException httpException = (retrofit2.adapter.rxjava2.HttpException) throwable;
            code = httpException.code();
            message = httpException.message();
        }


        if(throwable instanceof retrofit2.HttpException){
            retrofit2.HttpException httpException = (retrofit2.HttpException) throwable;
            code = httpException.code();
            message = httpException.message();
        }

        titleBuilder.append("Server Error").append(": ").append(String.valueOf(code));
        mErrorTitle.setText(titleBuilder);
        if(message != null) mErrorMessage.setText(message);

    }

    public abstract void hideContent();

    public abstract void showContent();
}
