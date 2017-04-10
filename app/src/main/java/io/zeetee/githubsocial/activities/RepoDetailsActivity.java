package io.zeetee.githubsocial.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
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
import io.zeetee.githubsocial.models.GithubRepoDetails;
import io.zeetee.githubsocial.models.GithubRepoReadme;
import io.zeetee.githubsocial.network.RestApi;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.LinkSpan;

/**
 * By GT.
 */

public class RepoDetailsActivity extends AbstractPushActivity {

    private GithubRepoDetails githubRepoDetails;
    private GithubRepoReadme githubRepoReadme;

    private TextView mName;
    private TextView mStats;
    private TextView mInfo;
    private String repoOwner;
    private String repoName;
    private MarkdownView mMarkdownView;
    private Button mFollowButton;
    private FloatingActionButton fab;
    boolean isStarred = false;

    private SimpleDraweeView mUserImage;
    private TextView mUserName;
    private View mUserContainer;

    private View mMainContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_details);
        mMainContainer = findViewById(R.id.main_container);

        mName = (TextView) findViewById(R.id.tv_name);
        mStats = (TextView) findViewById(R.id.tv_stats);
        mInfo = (TextView) findViewById(R.id.tv_info);
        mFollowButton = (Button) findViewById(R.id.btn_follow);

        mMarkdownView = (MarkdownView)findViewById(R.id.markdown_view);
        mMarkdownView.addStyleSheet(new Github());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab_star);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(isStarred){
                isStarred = false;
                fab.setImageResource(R.drawable.ic_star_white);
            }else{
                isStarred = true;
                fab.setImageResource(R.drawable.ic_star_border_white);
            }
            }
        });
        repoName = "guava"; //"https://api.github.com/repos/swankjesse/guava";
        repoOwner = "google";

        mUserName = (TextView) findViewById(R.id.tv_user_name);
        mUserImage = (SimpleDraweeView) findViewById(R.id.user_image);
        mUserContainer = findViewById(R.id.user_container);

        mUserContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUserClicked(repoOwner);
            }
        });

        fetchRepoDetails();
        initReadMe();

    }

    private void fetchRepoDetails(){
        RestApi.repoDetails(repoOwner,repoName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(detailsConsumer,throwableConsumer);

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
        setTitle(githubRepoDetails.name);
        mName.setText(githubRepoDetails.full_name);
        mInfo.setText(githubRepoDetails.description);
        showStats();
        showOwner();
    }

    private void showStats(){
        int start = 0;
        int end = 0;
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder("Stars: ");
        stringBuilder.append(String.valueOf(githubRepoDetails.stargazers_count));

        end = stringBuilder.length();

        stringBuilder.setSpan(starGazersClicked,start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.append("  |  ");

        start = stringBuilder.length();

        stringBuilder.append("Watchers: ");
        stringBuilder.append(String.valueOf(githubRepoDetails.watchers_count));
        end = stringBuilder.length();

        stringBuilder.setSpan(watchersClicked,start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mStats.setText(stringBuilder);
        mStats.setMovementMethod(LinkMovementMethod.getInstance());
        mStats.setHighlightColor(Color.TRANSPARENT);
    }

    private void showOwner(){
        if(githubRepoDetails == null || githubRepoDetails.owner == null || githubRepoDetails.owner.avatar_url == null) return;
        mUserImage.setImageURI(githubRepoDetails.owner.avatar_url);
        mUserName.setText(repoOwner);
    }


    ClickableSpan starGazersClicked = new LinkSpan() {
        @Override
        public void onClick(View textView) {
            if(githubRepoDetails == null || githubRepoDetails.owner == null) return;
            showUserList(GSConstants.ListType.STARGAZER, githubRepoDetails.name, githubRepoDetails.owner.login);
        }
    };

    ClickableSpan watchersClicked = new LinkSpan() {
        @Override
        public void onClick(View textView) {
            if(githubRepoDetails == null || githubRepoDetails.owner == null) return;
            showUserList(GSConstants.ListType.WATCHERS, githubRepoDetails.name, githubRepoDetails.owner.login);
        }
    };


    private void initReadMe(){
        if(githubRepoReadme == null || TextUtils.isEmpty(githubRepoReadme.content) || TextUtils.isEmpty(githubRepoReadme.encoding) || !githubRepoReadme.encoding.equalsIgnoreCase("base64")) return;
//        String url = "https://raw.githubusercontent.com/google/guava/master/README.md";
//        String base64 = "IyBHdWF2YTogR29vZ2xlIENvcmUgTGlicmFyaWVzIGZvciBKYXZhCgpbIVtC\ndWlsZCBTdGF0dXNdKGh0dHBzOi8vdHJhdmlzLWNpLm9yZy9nb29nbGUvZ3Vh\ndmEuc3ZnP2JyYW5jaD1tYXN0ZXIpXShodHRwczovL3RyYXZpcy1jaS5vcmcv\nZ29vZ2xlL2d1YXZhKQpbIVtNYXZlbiBDZW50cmFsXShodHRwczovL21hdmVu\nLWJhZGdlcy5oZXJva3VhcHAuY29tL21hdmVuLWNlbnRyYWwvY29tLmdvb2ds\nZS5ndWF2YS9ndWF2YS9iYWRnZS5zdmcpXShodHRwczovL21hdmVuLWJhZGdl\ncy5oZXJva3VhcHAuY29tL21hdmVuLWNlbnRyYWwvY29tLmdvb2dsZS5ndWF2\nYS9ndWF2YSkKCkd1YXZhIGlzIGEgc2V0IG9mIGNvcmUgbGlicmFyaWVzIHRo\nYXQgaW5jbHVkZXMgbmV3IGNvbGxlY3Rpb24gdHlwZXMgKHN1Y2ggYXMKbXVs\ndGltYXAgYW5kIG11bHRpc2V0KSwgaW1tdXRhYmxlIGNvbGxlY3Rpb25zLCBh\nIGdyYXBoIGxpYnJhcnksIGZ1bmN0aW9uYWwKdHlwZXMsIGFuIGluLW1lbW9y\neSBjYWNoZSwgYW5kIEFQSXMvdXRpbGl0aWVzIGZvciBjb25jdXJyZW5jeSwg\nSS9PLCBoYXNoaW5nLApwcmltaXRpdmVzLCByZWZsZWN0aW9uLCBzdHJpbmcg\ncHJvY2Vzc2luZywgYW5kIG11Y2ggbW9yZSEKClJlcXVpcmVzIEpESyAxLjgg\nb3IgaGlnaGVyLiBJZiB5b3UgbmVlZCBzdXBwb3J0IGZvciBKREsgMS42IG9y\nIEFuZHJvaWQsIHVzZQoyMC4wIGZvciBub3cuIEluIHRoZSBuZXh0IHJlbGVh\nc2UgKDIyLjApIHdlIHdpbGwgYmVnaW4gcHJvdmlkaW5nIGEgYmFja3BvcnQK\nZm9yIHVzZSBvbiBBbmRyb2lkIGFuZCBsb3dlciBKREsgdmVyc2lvbnMuCgoj\nIyBMYXRlc3QgcmVsZWFzZQoKVGhlIG1vc3QgcmVjZW50IHJlbGVhc2UgaXMg\nW0d1YXZhIDIxLjBdW10sIHJlbGVhc2VkIEphbnVhcnkgMTIsIDIwMTcuCgot\nIDIxLjAgQVBJIERvY3M6IFtndWF2YV1bZ3VhdmEtcmVsZWFzZS1hcGktZG9j\nc10sIFtndWF2YS10ZXN0bGliXVt0ZXN0bGliLXJlbGVhc2UtYXBpLWRvY3Nd\nCi0gMjEuMCBBUEkgRGlmZnMgZnJvbSAyMC4wOiBbZ3VhdmFdW2d1YXZhLXJl\nbGVhc2UtYXBpLWRpZmZzXQoKVG8gYWRkIGEgZGVwZW5kZW5jeSBvbiBHdWF2\nYSB1c2luZyBNYXZlbiwgdXNlIHRoZSBmb2xsb3dpbmc6CgpgYGB4bWwKPGRl\ncGVuZGVuY3k+CiAgPGdyb3VwSWQ+Y29tLmdvb2dsZS5ndWF2YTwvZ3JvdXBJ\nZD4KICA8YXJ0aWZhY3RJZD5ndWF2YTwvYXJ0aWZhY3RJZD4KICA8dmVyc2lv\nbj4yMS4wPC92ZXJzaW9uPgo8L2RlcGVuZGVuY3k+CmBgYAoKVG8gYWRkIGEg\nZGVwZW5kZW5jeSB1c2luZyBHcmFkbGU6CgpgYGAKZGVwZW5kZW5jaWVzIHsK\nICBjb21waWxlICdjb20uZ29vZ2xlLmd1YXZhOmd1YXZhOjIxLjAnCn0KYGBg\nCgojIyBTbmFwc2hvdHMKClNuYXBzaG90cyBvZiBHdWF2YSBidWlsdCBmcm9t\nIHRoZSBgbWFzdGVyYCBicmFuY2ggYXJlIGF2YWlsYWJsZSB0aHJvdWdoIE1h\ndmVuCnVzaW5nIHZlcnNpb24gYDIyLjAtU05BUFNIT1RgLgoKLSBTbmFwc2hv\ndCBBUEkgRG9jczogW2d1YXZhXVtndWF2YS1zbmFwc2hvdC1hcGktZG9jc10K\nLSBTbmFwc2hvdCBBUEkgRGlmZnMgZnJvbSAyMS4wOiBbZ3VhdmFdW2d1YXZh\nLXNuYXBzaG90LWFwaS1kaWZmc10KCiMjIExlYXJuIGFib3V0IEd1YXZhCgot\nIE91ciB1c2VycycgZ3VpZGUsIFtHdWF2YSBFeHBsYWluZWRdW10KLSBbQSBu\naWNlIGNvbGxlY3Rpb25dKGh0dHA6Ly93d3cudGZuaWNvLmNvbS9wcmVzZW50\nYXRpb25zL2dvb2dsZS1ndWF2YSkgb2Ygb3RoZXIgaGVscGZ1bCBsaW5rcwoK\nIyMgTGlua3MKCi0gW0dpdEh1YiBwcm9qZWN0XShodHRwczovL2dpdGh1Yi5j\nb20vZ29vZ2xlL2d1YXZhKQotIFtJc3N1ZSB0cmFja2VyOiBSZXBvcnQgYSBk\nZWZlY3Qgb3IgZmVhdHVyZSByZXF1ZXN0XShodHRwczovL2dpdGh1Yi5jb20v\nZ29vZ2xlL2d1YXZhL2lzc3Vlcy9uZXcpCi0gW1N0YWNrT3ZlcmZsb3c6IEFz\nayAiaG93LXRvIiBhbmQgIndoeS1kaWRuJ3QtaXQtd29yayIgcXVlc3Rpb25z\nXShodHRwczovL3N0YWNrb3ZlcmZsb3cuY29tL3F1ZXN0aW9ucy9hc2s/dGFn\ncz1ndWF2YStqYXZhKQotIFtndWF2YS1kaXNjdXNzOiBGb3Igb3Blbi1lbmRl\nZCBxdWVzdGlvbnMgYW5kIGRpc2N1c3Npb25dKGh0dHA6Ly9ncm91cHMuZ29v\nZ2xlLmNvbS9ncm91cC9ndWF2YS1kaXNjdXNzKQoKIyMgSU1QT1JUQU5UIFdB\nUk5JTkdTCgoxLiBBUElzIG1hcmtlZCB3aXRoIHRoZSBgQEJldGFgIGFubm90\nYXRpb24gYXQgdGhlIGNsYXNzIG9yIG1ldGhvZCBsZXZlbAphcmUgc3ViamVj\ndCB0byBjaGFuZ2UuIFRoZXkgY2FuIGJlIG1vZGlmaWVkIGluIGFueSB3YXks\nIG9yIGV2ZW4KcmVtb3ZlZCwgYXQgYW55IHRpbWUuIElmIHlvdXIgY29kZSBp\ncyBhIGxpYnJhcnkgaXRzZWxmIChpLmUuIGl0IGlzCnVzZWQgb24gdGhlIENM\nQVNTUEFUSCBvZiB1c2VycyBvdXRzaWRlIHlvdXIgb3duIGNvbnRyb2wpLCB5\nb3Ugc2hvdWxkCm5vdCB1c2UgYmV0YSBBUElzLCB1bmxlc3MgeW91IHJlcGFj\na2FnZSB0aGVtIChlLmcuIHVzaW5nIFByb0d1YXJkKS4KCjIuIERlcHJlY2F0\nZWQgbm9uLWJldGEgQVBJcyB3aWxsIGJlIHJlbW92ZWQgdHdvIHllYXJzIGFm\ndGVyIHRoZQpyZWxlYXNlIGluIHdoaWNoIHRoZXkgYXJlIGZpcnN0IGRlcHJl\nY2F0ZWQuIFlvdSBtdXN0IGZpeCB5b3VyCnJlZmVyZW5jZXMgYmVmb3JlIHRo\naXMgdGltZS4gSWYgeW91IGRvbid0LCBhbnkgbWFubmVyIG9mIGJyZWFrYWdl\nCmNvdWxkIHJlc3VsdCAoeW91IGFyZSBub3QgZ3VhcmFudGVlZCBhIGNvbXBp\nbGF0aW9uIGVycm9yKS4KCjMuIFNlcmlhbGl6ZWQgZm9ybXMgb2YgQUxMIG9i\namVjdHMgYXJlIHN1YmplY3QgdG8gY2hhbmdlIHVubGVzcyBub3RlZApvdGhl\ncndpc2UuIERvIG5vdCBwZXJzaXN0IHRoZXNlIGFuZCBhc3N1bWUgdGhleSBj\nYW4gYmUgcmVhZCBieSBhCmZ1dHVyZSB2ZXJzaW9uIG9mIHRoZSBsaWJyYXJ5\nLgoKNC4gT3VyIGNsYXNzZXMgYXJlIG5vdCBkZXNpZ25lZCB0byBwcm90ZWN0\nIGFnYWluc3QgYSBtYWxpY2lvdXMgY2FsbGVyLgpZb3Ugc2hvdWxkIG5vdCB1\nc2UgdGhlbSBmb3IgY29tbXVuaWNhdGlvbiBiZXR3ZWVuIHRydXN0ZWQgYW5k\nCnVudHJ1c3RlZCBjb2RlLgoKNS4gV2UgdW5pdC10ZXN0IGFuZCBiZW5jaG1h\ncmsgdGhlIGxpYnJhcmllcyB1c2luZyBvbmx5IE9wZW5KREsgMS44IG9uCkxp\nbnV4LiBTb21lIGZlYXR1cmVzLCBlc3BlY2lhbGx5IGluIGBjb20uZ29vZ2xl\nLmNvbW1vbi5pb2AsIG1heSBub3Qgd29yawpjb3JyZWN0bHkgaW4gb3RoZXIg\nZW52aXJvbm1lbnRzLgoKW0d1YXZhIDIxLjBdOiBodHRwczovL2dpdGh1Yi5j\nb20vZ29vZ2xlL2d1YXZhL3dpa2kvUmVsZWFzZTIxCltndWF2YS1yZWxlYXNl\nLWFwaS1kb2NzXTogaHR0cDovL2dvb2dsZS5naXRodWIuaW8vZ3VhdmEvcmVs\nZWFzZXMvMjEuMC9hcGkvZG9jcy8KW3Rlc3RsaWItcmVsZWFzZS1hcGktZG9j\nc106IGh0dHA6Ly93d3cuamF2YWRvYy5pby9kb2MvY29tLmdvb2dsZS5ndWF2\nYS9ndWF2YS10ZXN0bGliLzIxLjAKW2d1YXZhLXJlbGVhc2UtYXBpLWRpZmZz\nXTogaHR0cDovL2dvb2dsZS5naXRodWIuaW8vZ3VhdmEvcmVsZWFzZXMvMjEu\nMC9hcGkvZGlmZnMvCltndWF2YS1zbmFwc2hvdC1hcGktZG9jc106IGh0dHA6\nLy9nb29nbGUuZ2l0aHViLmlvL2d1YXZhL3JlbGVhc2VzL3NuYXBzaG90L2Fw\naS9kb2NzLwpbZ3VhdmEtc25hcHNob3QtYXBpLWRpZmZzXTogaHR0cDovL2dv\nb2dsZS5naXRodWIuaW8vZ3VhdmEvcmVsZWFzZXMvc25hcHNob3QvYXBpL2Rp\nZmZzLwpbR3VhdmEgRXhwbGFpbmVkXTogaHR0cHM6Ly9naXRodWIuY29tL2dv\nb2dsZS9ndWF2YS93aWtpL0hvbWUK\n";

        byte[] bytes = Base64.decode(githubRepoReadme.content, Base64.DEFAULT);
        try {
            String str = new String(bytes, "UTF-8");
            mMarkdownView.loadMarkdown(str);
        } catch (UnsupportedEncodingException e) {
            Log.e("GTGT","Unable to decode", e);
        }
//        mMarkdownView.loadMarkdownFromUrl(url);
    }

    @Override
    public void hideContent() {
        mMainContainer.setVisibility(View.GONE);
    }

    @Override
    public void showContent() {
        mMainContainer.setVisibility(View.VISIBLE);
    }
}
