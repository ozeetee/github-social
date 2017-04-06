package io.zeetee.githubsocial.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.adapters.UserListAdapter;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.network.RestApi;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.IUserActions;

public class UserListActivity extends AbstractBaseActivity implements IUserActions {


    private RecyclerView mRecyclerView;
    private UserListAdapter userListAdapter;
    private String userName;
    private int listType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null ) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if(getIntent() != null){
            if(getIntent().getStringExtra(GSConstants.USER_NAME) != null) this.userName = getIntent().getStringExtra(GSConstants.USER_NAME);
            this.listType = getIntent().getIntExtra(GSConstants.ListType.LIST_TYPE,0);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
        userListAdapter = new UserListAdapter(this);
        mRecyclerView.setAdapter(userListAdapter);
        fetchUserList();
    }

    private void fetchUserList(){
        if(TextUtils.isEmpty(userName)) return;
        if(listType == GSConstants.ListType.FOLLOWERS){
            setTitle("Followers");
            RestApi.
                    followers(userName)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(consumer,throwableConsumer);
        }else if(listType == GSConstants.ListType.FOLLOWING){
            setTitle("Following");
            RestApi.
                    following(userName)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(consumer,throwableConsumer);
        }
    }


    private Consumer<List<GithubUser>> consumer = new Consumer<List<GithubUser>>() {
        @Override
        public void accept(List<GithubUser> githubUsers) throws Exception {
            userListAdapter.setUsers(githubUsers);
        }
    };


    private Consumer<Throwable> throwableConsumer = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            Log.e("GTGT","Error while fetching User List",throwable);
        }
    };

    @Override
    public void onUserClicked(String userName) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(GSConstants.USER_NAME, userName);
        startActivity(intent);
    }
}
