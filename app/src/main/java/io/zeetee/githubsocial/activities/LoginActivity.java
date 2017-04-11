package io.zeetee.githubsocial.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;
import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.models.GithubRepo;
import io.zeetee.githubsocial.models.GithubUser;
import io.zeetee.githubsocial.models.GithubUserDetails;
import io.zeetee.githubsocial.network.RestApi;
import io.zeetee.githubsocial.utils.GSConstants;
import io.zeetee.githubsocial.utils.UserManager;
import io.zeetee.githubsocial.utils.UserProfileManager;
import io.zeetee.githubsocial.utils.Utils;

public class LoginActivity extends AbstractPushActivity  {

    // UI references.
    private AutoCompleteTextView mGithubLogin;
    private EditText mPasswordView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(getSupportActionBar() !=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up the login form.
        mGithubLogin = (AutoCompleteTextView) findViewById(R.id.github_login);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
    }


    private void attemptLogin() {
        // Reset errors.
        mGithubLogin.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String userName = mGithubLogin.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if(TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userName)) {
            mGithubLogin.setError(getString(R.string.error_field_required));
            focusView = mGithubLogin;
            cancel = true;
        } else if (!isUserNameValid(userName)) {
            mGithubLogin.setError(getString(R.string.error_invalid_email));
            focusView = mGithubLogin;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            hideKeyboard();
            doLogin(userName,password);
        }
    }

    private void hideKeyboard(){
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void doLogin(final String userName, final String password) {
        showScreenLoading();
        RestApi
                .auth(userName,password)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<GithubUserDetails>>() {
                    @Override
                    public ObservableSource<GithubUserDetails> apply(String token) throws Exception {
                        //First save the token in Shared pref this is happening in background thread
                        UserManager.getSharedInstance().userLoggedIn(userName,password,token);
                        return Observable.zip(RestApi.meProfile(), RestApi.meFollowing(0,GSConstants.PER_PAGE), RestApi.meStarred(0,GSConstants.PER_PAGE), new Function3<GithubUserDetails, List<GithubUser>, List<GithubRepo>, GithubUserDetails>() {
                            @Override
                            public GithubUserDetails apply(GithubUserDetails userDetails, List<GithubUser> githubUsers, List<GithubRepo> githubRepos) throws Exception {
                                //Save this information to
                                UserProfileManager.getSharedInstance().setUserDetails(userDetails);
                                UserProfileManager.getSharedInstance().setFollowing(githubUsers);
                                UserProfileManager.getSharedInstance().setStarredRepo(githubRepos);
                                return userDetails;
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GithubUserDetails>() {
                    @Override
                    public void accept(GithubUserDetails userDetails) throws Exception {
                        //User is logged in and profile data is fetched fine.
                        onPostLoginSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("GTGT","Error Occured while login");
                        showLoginError(throwable);
                    }
                });
    }

    private void onPostLoginSuccess(){
        setResult(GSConstants.LOGIN_SUCCESS);
        finish();
    }

    private void showLoginError(Throwable throwable){
        showScreenContent();
        int code = Utils.getHttpStatusCode(throwable);
        String serverMessage = Utils.getServerErrorMessage(throwable);
        Snackbar.make(mLoginFormView,serverMessage,Snackbar.LENGTH_LONG).show();
    }


    private boolean isUserNameValid(String email) {
        return true;//  email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 2;
    }


    @Override
    public void hideContent() {
        mLoginFormView.setVisibility(View.GONE);
    }

    @Override
    public void showContent() {
        mLoginFormView.setVisibility(View.VISIBLE);
    }
}

