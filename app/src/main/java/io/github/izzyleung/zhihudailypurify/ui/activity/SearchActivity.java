package io.github.izzyleung.zhihudailypurify.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import io.github.izzyleung.zhihudailypurify.R;
import io.github.izzyleung.zhihudailypurify.bean.DailyNews;
import io.github.izzyleung.zhihudailypurify.task.BaseSearchTask;
import io.github.izzyleung.zhihudailypurify.ui.fragment.SearchNewsFragment;
import io.github.izzyleung.zhihudailypurify.ui.widget.IzzySearchView;

public class SearchActivity extends ActionBarActivity {
    private IzzySearchView searchView;
    private SearchNewsFragment searchNewsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        searchNewsFragment = new SearchNewsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, searchNewsFragment)
                .commit();
    }

    @Override
    public void onDestroy() {
        searchNewsFragment = null;

        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initView() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        searchView = new IzzySearchView(this);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new IzzySearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new SearchTask().execute(query);
                searchView.clearFocus();
                return true;
            }
        });

        RelativeLayout relative = new RelativeLayout(this);
        relative.addView(searchView);
        getSupportActionBar().setCustomView(relative);
    }

    class SearchTask extends BaseSearchTask {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(SearchActivity.this);
            dialog.setMessage(getString(R.string.searching));
            dialog.setCancelable(true);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    SearchTask.this.cancel(true);
                }
            });
            dialog.show();
        }

        @Override
        protected void onPostExecute(List<DailyNews> newsList) {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }

            if (!isCancelled()) {
                if (isSearchSuccess) {
                    searchNewsFragment.updateContent(newsList);
                } else {
                    Toast.makeText(SearchActivity.this,
                            getString(R.string.no_result_found),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
