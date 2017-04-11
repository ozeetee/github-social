package io.zeetee.githubsocial.activities;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.models.GithubUserDetails;
import io.zeetee.githubsocial.network.RestApi;
import io.zeetee.githubsocial.utils.ColorDrawableHelper;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.LinkSpan;
import io.zeetee.githubsocial.utils.UserManager;

public class ProfileActivity extends AbstractPushActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    private GithubUserDetails userDetails;
    private String userName;

    private SimpleDraweeView mImage;
    private TextView mName;
    private TextView mBio;

    private TextView mBlog;


    private Button mRepos;
    private Button mGists;
    private Button mFollowers;
    private Button mFollowing;
    private Button mMembers;

    //Containers
    private View mProfileContainer;
    private View mFollowingFollowerContainer;
    private View mOrgMembersContainer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mImage = (SimpleDraweeView) findViewById(R.id.user_image);

        mName = (TextView) findViewById(R.id.tv_name);

        mBio = (TextView) findViewById(R.id.tv_bio);
        mBlog = (TextView) findViewById(R.id.tv_blog);

        mFollowingFollowerContainer = findViewById(R.id.following_follower_container);
        mProfileContainer = findViewById(R.id.main_container);
        mOrgMembersContainer = findViewById(R.id.org_members_container);

        mFollowers = (Button) findViewById(R.id.btn_followers);
        mFollowing = (Button) findViewById(R.id.btn_following);
        mMembers = (Button) findViewById(R.id.btn_members);

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
            Log.e(TAG,"Error userName and AppUser Details are null");
            finish();
            return;
        }

        setTitle(userName);
        fetchUserDetails(userName);
        mRepos = (Button) findViewById(R.id.btn_repos);
        mGists = (Button) findViewById(R.id.btn_gits);
        mRepos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserRepos(userName);
            }
        });
        mFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserList(GSConstants.ListType.FOLLOWING, userName);
            }
        });

        mFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserList(GSConstants.ListType.FOLLOWERS, userName);
            }
        });

        mMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserList(GSConstants.ListType.ORG_MEMBERS, userName);
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
        if(TextUtils.isEmpty(userName)) {
            showFullScreenError("Internal Error","UserName is null");
            mErrorResolveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            mErrorResolveButton.setText("Go Back");
            return;
        }

        if(userName.equalsIgnoreCase(GSConstants.ME) && !UserManager.getSharedInstance().isLoggedIn()){
            showFullScreenError("Please Login", "You need to login to view your profile");
            mErrorResolveButton.setText("Login");
            mErrorResolveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLoginScreen();
                }
            });
            return;
        }

        showScreenLoading();
        Observable<GithubUserDetails> observable = getApiObservable();
        if(observable == null) return;

        observable
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

    private Observable<GithubUserDetails> getApiObservable(){
        if(userName == null) return null;
        if(userName.equalsIgnoreCase(GSConstants.ME)) return RestApi.meProfile();
        return RestApi.user(userName);
    }


    private void initUI(){
        showScreenContent();
        if(userDetails == null) return;
        if(!TextUtils.isEmpty(userDetails.avatar_url)){
            mImage.setImageURI(userDetails.avatar_url);
        }
        mName.setText(userDetails.name);
        Drawable mUserDrawable = ColorDrawableHelper.getInstance().getDrawableForUserType(userDetails.type);
        mName.setCompoundDrawablesWithIntrinsicBounds(null,null,mUserDrawable,null);
        mBio.setText(userDetails.bio);
        mBlog.setText(userDetails.blog);
        showFollowingFollowersCount();
        showGithubStats();

    }

    private void showFollowingFollowersCount(){
        if(userDetails == null || userDetails.type == null || userDetails.type.equalsIgnoreCase(GSConstants.UserType.ORG)) {
            mFollowingFollowerContainer.setVisibility(View.GONE);
            mOrgMembersContainer.setVisibility(View.VISIBLE);
            return;
        }
        mOrgMembersContainer.setVisibility(View.GONE);
        mFollowingFollowerContainer.setVisibility(View.VISIBLE);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(String.valueOf(userDetails.following));
        stringBuilder.append(" Following");
        mFollowing.setText(stringBuilder);

        stringBuilder = new SpannableStringBuilder(String.valueOf(userDetails.followers));
        stringBuilder.append(" Followers");
        mFollowers.setText(stringBuilder);
    }

    private void showGithubStats(){
        int start = 0;
        int end = 0;

        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(String.valueOf(userDetails.public_repos));
        end = stringBuilder.length();
        stringBuilder.setSpan(new StyleSpan(Typeface.BOLD),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.append("\n").append("Repos");
        mRepos.setText(stringBuilder);

        stringBuilder = new SpannableStringBuilder(String.valueOf(userDetails.public_gists));
        end = stringBuilder.length();
        stringBuilder.setSpan(new StyleSpan(Typeface.BOLD),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.append("\n").append("Gists");
        mGists.setText(stringBuilder);
    }


    @Override
    public void hideContent() {
        mProfileContainer.setVisibility(View.GONE);
    }

    @Override
    public void showContent() {
        mProfileContainer.setVisibility(View.VISIBLE);
    }
}
