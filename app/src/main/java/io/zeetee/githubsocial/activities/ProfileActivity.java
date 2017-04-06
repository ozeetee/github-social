package io.zeetee.githubsocial.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.models.GithubUserDetails;
import io.zeetee.githubsocial.network.RestApi;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.LinkSpan;

public class ProfileActivity extends AbstractBaseActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    private GithubUserDetails userDetails;
    private String userName;

    private SimpleDraweeView mImage;
    private TextView mName;

    private TextView mStats;
    private TextView mInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        mImage = (SimpleDraweeView) findViewById(R.id.user_image);

        mName = (TextView) findViewById(R.id.tv_name);
        mStats = (TextView) findViewById(R.id.tv_stats);
        mInfo = (TextView) findViewById(R.id.tv_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(savedInstanceState != null && savedInstanceState.getParcelable(GSConstants.USER_DETAILS) != null){
            userDetails = savedInstanceState.getParcelable(GSConstants.USER_DETAILS);
            initUI();
            return;
        }


        if(savedInstanceState != null && savedInstanceState.getString(GSConstants.USER_NAME) != null){
            userName = getIntent().getStringExtra(GSConstants.USER_NAME);
        }else if(getIntent() != null && getIntent().getStringExtra(GSConstants.USER_NAME) != null){
            userName = getIntent().getStringExtra(GSConstants.USER_NAME);
        }

        if(userName == null){
            //Error Condition
            Log.e(TAG,"Error userName and User Details are null");
            finish();
            return;
        }

        setTitle(userName);

        fetchUserDetails(userName);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(GSConstants.USER_DETAILS, userDetails);
        outState.putString(GSConstants.USER_NAME, userName);
        super.onSaveInstanceState(outState);
    }


    private void fetchUserDetails(String userName){
        RestApi.user(userName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GithubUserDetails>() {
                    @Override
                    public void accept(GithubUserDetails userDetails) throws Exception {
                        ProfileActivity.this.userDetails = userDetails;
                        initUI();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("GTGT","Error while fetching User Details",throwable);
                    }
                });
    }

    private void initUI(){
        if(userDetails == null) return;
        if(!TextUtils.isEmpty(userDetails.avatar_url)){
            mImage.setImageURI(userDetails.avatar_url);
        }
        mName.setText(userDetails.name);
        mInfo.setText(userDetails.bio);
        showFollowingFollowersCount();
    }

    private void showFollowingFollowersCount(){
        int start = 0;
        int end = 0;
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder("Following: ");
        stringBuilder.append(String.valueOf(userDetails.following));
        end = stringBuilder.length();

        stringBuilder.setSpan(followingClicked,start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.append("  |  ");

        start = stringBuilder.length();

        stringBuilder.append("Followers: ");
        stringBuilder.append(String.valueOf(userDetails.followers));
        end = stringBuilder.length();

        stringBuilder.setSpan(followersClicked,start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mStats.setText(stringBuilder);
        mStats.setMovementMethod(LinkMovementMethod.getInstance());
        mStats.setHighlightColor(Color.TRANSPARENT);
    }




    ClickableSpan followersClicked = new LinkSpan() {
        @Override
        public void onClick(View textView) {
        showUserList(GSConstants.ListType.FOLLOWERS);
        }
    };

    ClickableSpan followingClicked = new LinkSpan() {
        @Override
        public void onClick(View textView) {
            showUserList(GSConstants.ListType.FOLLOWING);
        }
    };


    private void showUserList(int userListType){
        Intent intent = new Intent(this, UserListActivity.class);
        intent.putExtra(GSConstants.ListType.LIST_TYPE,userListType);
        intent.putExtra(GSConstants.USER_NAME,userDetails.login);
        startActivity(intent);
    }


}
