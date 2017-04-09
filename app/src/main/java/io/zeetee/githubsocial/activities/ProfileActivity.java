package io.zeetee.githubsocial.activities;

import android.graphics.Color;
import android.os.Bundle;
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

public class ProfileActivity extends AbstractPushActivity {

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

        findViewById(R.id.btn_repositories).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserRepos(userName);
            }
        });

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
                }, throwableConsumer);
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
        showUserList(GSConstants.ListType.FOLLOWERS,userName);
        }
    };

    ClickableSpan followingClicked = new LinkSpan() {
        @Override
        public void onClick(View textView) {
            showUserList(GSConstants.ListType.FOLLOWING,userName);
        }
    };


}
