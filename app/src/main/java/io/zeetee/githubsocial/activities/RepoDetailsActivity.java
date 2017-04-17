package io.zeetee.githubsocial.activities;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.UnsupportedEncodingException;

import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.styles.Github;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.bus.RxEvents;
import io.zeetee.githubsocial.models.GithubRepoDetails;
import io.zeetee.githubsocial.models.GithubRepoReadme;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.network.RestApi;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.UserManager;
import io.zeetee.githubsocial.utils.UserProfileManager;

/**
 * By GT.
 */

public class RepoDetailsActivity extends AbstractPushActivity {

    private GithubRepoDetails githubRepoDetails;
    private GithubRepoReadme githubRepoReadme;

    private TextView mName;
    private TextView mInfo;
    private String repoOwner;
    private String repoName;
    private MarkdownView mMarkdownView;

    private SimpleDraweeView mUserImage;
    private TextView mUserName;

    private View mUserContainer;
    private View mMainContainer;

    private Button mStarGazers;
    private Button mWatchers;

    private Button mFollowButton;
    private Button mUnFollowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_details);
        mMainContainer = findViewById(R.id.main_container);

        mName = (TextView) findViewById(R.id.tv_name);
        mInfo = (TextView) findViewById(R.id.tv_info);
        mFollowButton = (Button) findViewById(R.id.btn_follow);
        mStarGazers = (Button) findViewById(R.id.btn_stargazer);
        mWatchers = (Button) findViewById(R.id.btn_watchers);

        mMarkdownView = (MarkdownView)findViewById(R.id.markdown_view);
        mMarkdownView.addStyleSheet(new Github());

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent() != null){
            repoOwner = getIntent().getStringExtra(GSConstants.USER_NAME);
            repoName = getIntent().getStringExtra(GSConstants.REPO_NAME);
        }

        if(TextUtils.isEmpty(repoName) || TextUtils.isEmpty(repoOwner)){
            //Error condition
            return;
        }


        mUserName = (TextView) findViewById(R.id.user_name);
        mUserImage = (SimpleDraweeView) findViewById(R.id.user_image);
        mUserContainer = findViewById(R.id.user_container);

        mUserContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUserClicked(repoOwner);
            }
        });

        mErrorResolveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchRepoDetails();
            }
        });
        fetchRepoDetails();
        mStarGazers.setOnClickListener(starGazersClicked);
        mWatchers.setOnClickListener(watchersClicked);

        mFollowButton = (Button) findViewById(R.id.btn_follow);
        mUnFollowButton = (Button) findViewById(R.id.btn_un_follow);
    }





    private void fetchRepoDetails(){
        showScreenLoading();
        RestApi.repoDetails(repoOwner,repoName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(detailsConsumer,fullScreenErrorConsumer);

        RestApi.repoReadme(repoOwner,repoName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(readmeConsumer,throwableConsumer);
    }


    private Consumer<GithubRepoDetails> detailsConsumer = new Consumer<GithubRepoDetails>() {
        @Override
        public void accept(GithubRepoDetails githubRepoDetails) throws Exception {
            RepoDetailsActivity.this.githubRepoDetails = githubRepoDetails;
            initDetails();
        }
    };



    private Consumer<GithubRepoReadme> readmeConsumer = new Consumer<GithubRepoReadme>() {
        @Override
        public void accept(GithubRepoReadme githubRepoReadme) throws Exception {
            RepoDetailsActivity.this.githubRepoReadme = githubRepoReadme;
            initReadMe();
        }
    };

    private void initDetails(){
        showScreenContent();
        setTitle(githubRepoDetails.name);
        mName.setText(githubRepoDetails.name);
        mInfo.setText(githubRepoDetails.description);
        showStats();
        showOwner();
        invalidateOptionsMenu();
    }

    private void showStats(){

        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(String.valueOf(githubRepoDetails.stargazers_count));
        stringBuilder.append(" Stars");
        mStarGazers.setText(stringBuilder);

        stringBuilder = new SpannableStringBuilder(String.valueOf(githubRepoDetails.watchers_count));
        stringBuilder.append(" Watchers");
        mWatchers.setText(stringBuilder);
    }

    private void showOwner(){
        if(githubRepoDetails == null || githubRepoDetails.owner == null || githubRepoDetails.owner.avatar_url == null) return;
        mUserImage.setImageURI(githubRepoDetails.owner.avatar_url);
        mUserName.setText(repoOwner);
        initFollowButton();
    }


    private void initFollowButton(){
        if(githubRepoDetails == null || githubRepoDetails.owner == null) return;
        String userName = githubRepoDetails.owner.login;
        GithubUser userDetails = githubRepoDetails.owner;
        if(UserManager.getSharedInstance().isMe(userName) || userDetails == null || userDetails.type == null || !userDetails.type.equalsIgnoreCase(GSConstants.UserType.USER)){
            mFollowButton.setVisibility(View.GONE);
            mUnFollowButton.setVisibility(View.GONE);
        }else{
            if(UserProfileManager.getSharedInstance().isFollowing(userDetails)){
                mFollowButton.setVisibility(View.GONE);
                mUnFollowButton.setVisibility(View.VISIBLE);
            }else{
                mFollowButton.setVisibility(View.VISIBLE);
                mUnFollowButton.setVisibility(View.GONE);
            }
        }
    }

    View.OnClickListener starGazersClicked = new View.OnClickListener() {
        @Override
        public void onClick(View textView) {
            if(githubRepoDetails == null || githubRepoDetails.owner == null) return;
            showUserList(GSConstants.ListType.STARGAZER, githubRepoDetails.name, githubRepoDetails.owner.login);
        }
    };

    View.OnClickListener watchersClicked = new View.OnClickListener() {
        @Override
        public void onClick(View textView) {
            if(githubRepoDetails == null || githubRepoDetails.owner == null) return;
            showUserList(GSConstants.ListType.WATCHERS, githubRepoDetails.name, githubRepoDetails.owner.login);
        }
    };


    private void initReadMe(){
        if(githubRepoReadme == null || TextUtils.isEmpty(githubRepoReadme.content) || TextUtils.isEmpty(githubRepoReadme.encoding) || !githubRepoReadme.encoding.equalsIgnoreCase("base64")) return;
        byte[] bytes = Base64.decode(githubRepoReadme.content, Base64.DEFAULT);
        try {
            String str = new String(bytes, "UTF-8");
            mMarkdownView.loadMarkdown(str);
        } catch (UnsupportedEncodingException e) {
            Log.e("GTGT","Unable to decode", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_repo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_repo_star) {
            starUnstarClicked(githubRepoDetails);
            invalidateOptionsMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem repoStar = menu.findItem(R.id.action_repo_star);
        if(UserProfileManager.getSharedInstance().isStarred(githubRepoDetails)){
            repoStar.setIcon(R.drawable.ic_star_yellow);
        }else{
            repoStar.setIcon(R.drawable.ic_star_white);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void hideContent() {
        mMainContainer.setVisibility(View.GONE);
    }

    @Override
    public void showContent() {
        mMainContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public Consumer<Object> getRxBusConsumer() {
        return busEventConsumer;
    }

    Consumer<Object> busEventConsumer = new Consumer<Object>() {
        @Override
        public void accept(Object o) throws Exception {

            if(o instanceof RxEvents.RepoStarredEvent){
                invalidateOptionsMenu();
            }

            if(o instanceof RxEvents.RepoStarredEvent){
                invalidateOptionsMenu();
            }

            if(o instanceof RxEvents.UserInfoLoadedEvent || o instanceof RxEvents.UserFollowedEvent || o instanceof RxEvents.UserUnFollowedEvent){
                initFollowButton();
            }
        }
    };
}
