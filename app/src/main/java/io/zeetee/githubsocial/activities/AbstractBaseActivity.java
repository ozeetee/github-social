package io.zeetee.githubsocial.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.bus.RxEventBus;
import io.zeetee.githubsocial.models.GithubItem;
import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.network.RestApi;
import io.zeetee.githubsocial.utils.ActionsManager;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.IActions;
import io.zeetee.githubsocial.utils.UserManager;
import io.zeetee.githubsocial.utils.UserProfileManager;
import io.zeetee.githubsocial.utils.Utils;
import retrofit2.Response;

/**
 * By GT.
 *
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
    private AlertDialog loginDialog;
    private Map<Long,Disposable> currentServerOperations = new WeakHashMap<>();
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();


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
        showFullScreenError(Utils.getErrorTitle(throwable),Utils.getErrorMessage(throwable));
    }

    @Override
    public void showLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    public abstract void hideContent();

    public abstract void showContent();

    public abstract Consumer<Object> getRxBusConsumer();


    @Override
    public void showLoginPrompt(String s){
        if(loginDialog == null){
            loginDialog =
            new AlertDialog.Builder(this)
                    .setMessage(R.string.login_message)
                    .setTitle(R.string.login_title)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            showLoginScreen();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    }).create();
        }
        if(s!=null) loginDialog.setMessage(s);
        loginDialog.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscribeToEventBus();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear(); //To avoid memory leak with subscribe
        super.onDestroy();
    }

    private void subscribeToEventBus(){
        Consumer<Object> consumer = getRxBusConsumer();
        if(consumer == null) return;
        Disposable rxBusDisposable; rxBusDisposable = RxEventBus.getInstance().getEventBus().subscribe(getRxBusConsumer());
        compositeDisposable.add(rxBusDisposable); //So later on we can release the resource for avoiding any memory leaks
    }


    @Override
    public void followUnFollowClicked(@NonNull GithubUser githubUser){
        if(!UserManager.getSharedInstance().isLoggedIn()){
            showLoginPrompt("Please login to follow user");
            return;
        }
        if(UserManager.getSharedInstance().isMe(githubUser.login) ) return;
        doStarUnStarWork(githubUser);
    }


    public void starUnstarClicked(GithubRepo githubRepo){
        if(githubRepo == null) return;
        if(!UserManager.getSharedInstance().isLoggedIn()){
            showLoginPrompt("Please login to star repo");
            return;
        }
        doStarUnStarWork(githubRepo);
    }


    private void doStarUnStarWork(GithubItem item) {
        Observable<Response<Void>> observable;
        if (currentServerOperations.get(item.id) != null) {
            Disposable disposable = currentServerOperations.get(item.id);
            if (!disposable.isDisposed()) disposable.dispose();
        }

        if (item instanceof GithubUser) {
            final GithubUser githubUser = (GithubUser) item;
            boolean isFollowing = UserProfileManager.getSharedInstance().isFollowing(githubUser);

            if (!isFollowing) {
                //Follow the user
                observable = RestApi.followUser(githubUser);
            } else {
                //UnFollow user
                observable = RestApi.unFollowUser(githubUser);
            }

            Disposable operation = observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Utils.emptyVoidConsumer, snackBarErrorConsumer);
            currentServerOperations.put(item.id, operation);
            compositeDisposable.add(operation);
        }

        if(item instanceof GithubRepo){
            final GithubRepo githubRepo = (GithubRepo) item;
            boolean isStarred = UserProfileManager.getSharedInstance().isStarred(githubRepo);
            if (!isStarred) {
                //Follow the user
                observable = RestApi.starRepo(githubRepo);
            } else {
                //UnFollow user
                observable = RestApi.unStarRepo(githubRepo);
            }

            Disposable operation = observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Utils.emptyVoidConsumer, snackBarErrorConsumer);
            currentServerOperations.put(item.id, operation);
            compositeDisposable.add(operation);
        }

    }


    private Consumer<Throwable> snackBarErrorConsumer = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            String message = Utils.getErrorMessage(throwable);
            Snackbar.make(mProgressAndErrorContainer, message, Snackbar.LENGTH_LONG).show();
        }
    };

}
