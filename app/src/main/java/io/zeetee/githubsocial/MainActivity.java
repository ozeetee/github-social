package io.zeetee.githubsocial;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.zeetee.githubsocial.activities.AbstractBaseActivity;
import io.zeetee.githubsocial.activities.LoginActivity;
import io.zeetee.githubsocial.adapters.GithubItemAdapter;
import io.zeetee.githubsocial.models.GithubItem;
import io.zeetee.githubsocial.models.GithubSearchResult;
import io.zeetee.githubsocial.network.RestApi;
import io.zeetee.githubsocial.utils.Utils;
import io.zeetee.githubsocial.utils.VerticalSpaceItemDecoration;

public class MainActivity extends AbstractBaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private GithubItemAdapter githubItemAdapter;

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
    }


    private void fetchHomePage(){
        showScreenLoading();
        Observable
                .zip(RestApi.fetchTopAndroidRepo(), RestApi.fetchMostFollowedAndroidDevs(), new BiFunction<GithubSearchResult, GithubSearchResult, List<GithubItem>>() {
                    @Override
                    public List<GithubItem> apply(GithubSearchResult topAndroidRepo, GithubSearchResult mostFollowedAndroidDev) throws Exception {
                        return Utils.constructHomePage(topAndroidRepo,mostFollowedAndroidDev);
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            openLoginActivity();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
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
