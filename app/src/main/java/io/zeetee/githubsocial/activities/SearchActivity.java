package io.zeetee.githubsocial.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.zeetee.githubsocial.R;
import io.zeetee.githubsocial.models.GithubItem;
import io.zeetee.githubsocial.models.GithubSearchResult;
import io.zeetee.githubsocial.network.RestApi;
import io.zeetee.githubsocial.utils.GSConstants;

/**
 * By GT.
 */

public class SearchActivity extends AbstractListActivity{

    private EditText mSearch;
    private View mClear;
    private Disposable searchDisposable;
    private String searchQuery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        if(getSupportActionBar() != null ) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mClear = findViewById(R.id.btn_clear);
        mSearch = (EditText) findViewById(R.id.tv_search);
        mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    return true;
                }
                return false;
            }
        });

        Disposable disposable = RxTextView
                .textChanges(mSearch)
                .debounce(800, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        doSearch(charSequence);
                    }
                });
        compositeDisposable.add(disposable);
    }


    private void doSearch(CharSequence charSequence){
        String currentQuery = charSequence == null ? null : charSequence.toString().trim();

        //Search only for 2 or more
        if(currentQuery == null || currentQuery.length() < 2){
            isMorePage = false;
            githubItemAdapter.setGithubItems(null);
            searchQuery = currentQuery;
            return;
        }

        if(currentQuery.equalsIgnoreCase(searchQuery)){
            //Do nothing
            return;
        }
        searchQuery = currentQuery;
        if(searchDisposable != null && !searchDisposable.isDisposed()) searchDisposable.dispose();
        showScreenLoading();
        page = 1;
        isMorePage = true;
        fetchList();
    }

    @Override
    protected void fetchList() {
        super.fetchList();
        if(searchQuery == null || searchQuery.length() < 2){
            swipeContainer.setRefreshing(false);
            return;
        }
        if(page == 1){
            mErrorResolveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fetchList();
                }
            });
        }
        searchDisposable =
                RestApi.searchRepos(searchQuery, page, GSConstants.PER_PAGE)
                .map(new Function<GithubSearchResult, List<GithubItem>>() {
                    @Override
                    public List<GithubItem> apply(GithubSearchResult githubSearchResult) throws Exception {
                        return githubSearchResult == null || githubSearchResult.items == null ? new ArrayList<GithubItem>(0) : githubSearchResult.items;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listConsumer,getListErrorConsumer());
        compositeDisposable.add(searchDisposable);
    }

    @Override
    protected boolean isFormatCard() {
        return true;
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_search;
    }

    @Override
    public Consumer<Object> getRxBusConsumer() {
        return generalBusEventConsumer;
    }
}
