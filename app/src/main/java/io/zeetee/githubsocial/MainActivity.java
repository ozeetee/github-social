package io.zeetee.githubsocial;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;
import io.zeetee.githubsocial.activities.AbstractBaseActivity;
import io.zeetee.githubsocial.adapters.GithubItemAdapter;
import io.zeetee.githubsocial.bus.RxEventBus;
import io.zeetee.githubsocial.bus.RxEvents;
import io.zeetee.githubsocial.models.GithubItem;
import io.zeetee.githubsocial.models.GithubSearchResult;
import io.zeetee.githubsocial.models.GithubUserDetails;
import io.zeetee.githubsocial.network.RestApi;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.UserManager;
import io.zeetee.githubsocial.utils.UserProfileManager;
import io.zeetee.githubsocial.utils.Utils;
import io.zeetee.githubsocial.utils.VerticalSpaceItemDecoration;

public class MainActivity extends AbstractBaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private GithubItemAdapter githubItemAdapter;
    private SimpleDraweeView mLoggedInUserImage;
    private Button mLoginButton;
    private Disposable subscription;

    private TextView mUserMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);

        int verticalSpacing = getResources().getDimensionPixelSize(R.dimen.item_vertical_spacing);
        VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(verticalSpacing);
        mRecyclerView.addItemDecoration(verticalSpaceItemDecoration);

        githubItemAdapter = new GithubItemAdapter(this);
        mRecyclerView.setAdapter(githubItemAdapter);
        fetchHomePage();
        mErrorResolveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchHomePage();
            }
        });
        subscribeToEventBus();

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

    }

    @Override
    protected void onDestroy() {
        unsubscribeToEventBus();
        super.onDestroy();
    }

    private void subscribeToEventBus(){
        subscription = RxEventBus.getInstance().getEventBus().subscribe(busEventConsumer);
    }

    private void unsubscribeToEventBus(){
        if(subscription != null && !subscription.isDisposed()){
            subscription.dispose();
        }
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
        }
    };

    private void fetchHomePage(){
        showScreenLoading();
        Observable
                .zip(RestApi.fetchTopAndroidRepo(), RestApi.fetchMostFollowedAndroidDevs(), RestApi.fetchHomeProfile(), new Function3<GithubSearchResult, GithubSearchResult, GithubUserDetails, List<GithubItem>>() {
                    @Override
                    public List<GithubItem> apply(GithubSearchResult topAndroidRepo, GithubSearchResult mostFollowedAndroidDev, GithubUserDetails userDetails) throws Exception {
                        return Utils.constructHomePage(topAndroidRepo,mostFollowedAndroidDev,userDetails);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<GithubItem>>() {
                    @Override
                    public void accept(List<GithubItem> gihubItems) throws Exception {
                        githubItemAdapter.setGithubItems(gihubItems);
                        initUI();
                    }
                }, fullScreenErrorConsumer);
    }


    private void initUI(){
        showScreenContent();
    }

    private void checkCloseDrawer(){

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


    private void shareApp(){
        Snackbar.make(mRecyclerView, "Not yet implemented", Snackbar.LENGTH_SHORT).show();
    }

    private void showAboutScreen(){
        Snackbar.make(mRecyclerView, "Not yet implemented", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void hideContent() {
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showContent() {
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
