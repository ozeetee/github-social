package io.zeetee.githubsocial;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;
import io.zeetee.githubsocial.activities.AboutActivity;
import io.zeetee.githubsocial.activities.AbstractListActivity;
import io.zeetee.githubsocial.bus.RxEvents;
import io.zeetee.githubsocial.models.GithubItem;
import io.zeetee.githubsocial.models.GithubSearchResult;
import io.zeetee.githubsocial.models.GithubUserDetails;
import io.zeetee.githubsocial.network.RestApi;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.UserManager;
import io.zeetee.githubsocial.utils.UserProfileManager;
import io.zeetee.githubsocial.utils.Utils;

public class MainActivity extends AbstractListActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SimpleDraweeView mLoggedInUserImage;
    private Button mLoginButton;
    private TextView mUserMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isMorePage = false;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mLoginButton = (Button) navigationView.getHeaderView(0).findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkShowLogin();
            }
        });

        initLoginButton();
        mUserMessage = (TextView) navigationView.getHeaderView(0).findViewById(R.id.logged_in_user_name);

        mLoggedInUserImage = (SimpleDraweeView) navigationView.getHeaderView(0).findViewById(R.id.logged_in_user_image);

        initLoginButton();
        initAvatar();
        mUserMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUserClicked(GSConstants.ME);
            }
        });

        mLoggedInUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUserClicked(GSConstants.ME);
            }
        });

        showScreenLoading();
        fetchList();

    }

    private void initLoginButton(){
        if(UserManager.getSharedInstance().isLoggedIn()){
            mLoginButton.setText("Logout");
            if(mUserMessage != null)mUserMessage.setText("Welcome " + UserManager.getSharedInstance().getUserName() + "!!!");
        }else{
            mLoginButton.setText("Login");
            if(mUserMessage != null) mUserMessage.setText("Welcome Guest!!!");
        }
    }

    private void initAvatar(){
        String avatarUrl = UserProfileManager.getSharedInstance().getAavatarUrl();
        mLoggedInUserImage.setImageURI(avatarUrl);
    }

    private void checkShowLogin(){
        if(UserManager.getSharedInstance().isLoggedIn()){
            UserManager.getSharedInstance().logOfUser();
        }else{
            showLoginScreen();
        }
        initLoginButton();
    }

    Consumer<Object> busEventConsumer = new Consumer<Object>() {
        @Override
        public void accept(Object o) throws Exception {
            if(o instanceof RxEvents.UserLoggedInEvent){
                initLoginButton();
            }

            if(o instanceof RxEvents.UserInfoLoadedEvent){
                initAvatar();
            }

            if(o instanceof RxEvents.UserFollowedEvent){
                onGithubItemChanged(((RxEvents.UserFollowedEvent)o).githubUser);
            }

            if(o instanceof RxEvents.UserUnFollowedEvent){
                onGithubItemChanged(((RxEvents.UserUnFollowedEvent)o).githubUser);
            }
        }
    };

    @Override
    public Consumer<Object> getRxBusConsumer() {
        return busEventConsumer;
    }

    @Override
    protected void fetchList() {
        isLoading = true;
        Observable
        .zip(RestApi.fetchTopAndroidRepo(), RestApi.fetchMostFollowedAndroidDevs(), RestApi.fetchHomeProfile(), new Function3<GithubSearchResult, GithubSearchResult, GithubUserDetails, List<GithubItem>>() {
            @Override
            public List<GithubItem> apply(GithubSearchResult topAndroidRepo, GithubSearchResult mostFollowedAndroidDev, GithubUserDetails userDetails) throws Exception {
                return Utils.constructHomePage(topAndroidRepo,mostFollowedAndroidDev,userDetails);
            }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(listConsumer, getListErrorConsumer());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_repos) {
            // Handle the camera action
            showUserRepos(GSConstants.ME);
        } else if (id == R.id.nav_followers) {
            showUserList(GSConstants.ListType.FOLLOWERS, GSConstants.ME);
        } else if (id == R.id.nav_following) {
            showUserList(GSConstants.ListType.FOLLOWING, GSConstants.ME);
        } else if (id == R.id.nav_starred_repo) {
            showStarredRepos();
        } else if (id == R.id.nav_share) {
            shareApp();
        } else if (id == R.id.nav_about) {
            showAboutScreen();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareThisApp(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Github Social");
        intent.putExtra(Intent.EXTRA_TEXT, "Checkout this app from open source app from GT. https://github.com/ozeetee/github-social");
        startActivity(Intent.createChooser(intent, "Share with"));
    }

    private void shareApp(){
        shareThisApp();
    }

    private void showAboutScreen(){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    protected boolean isFormatCard() {
        return true;
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_main;
    }
}
