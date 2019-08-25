package ga.softogi.themoviecatalogue.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import ga.softogi.themoviecatalogue.entity.ContentItem;
import ga.softogi.themoviecatalogue.activity.DetailContentActivity;
import ga.softogi.themoviecatalogue.network.MainViewModel;
import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.adapter.ContentAdapter;

public class TvFragment extends Fragment {

    private ContentAdapter adapter;
    private ProgressBar progressBar;
    private ArrayList<ContentItem> tvs = new ArrayList<>();
    private Observer<ArrayList<ContentItem>> getContent = new Observer<ArrayList<ContentItem>>() {
        @Override
        public void onChanged(@Nullable ArrayList<ContentItem> tvs) {
            if (tvs != null) {
                adapter.setData(tvs);
                showLoading(false);
            }
        }
    };

    public TvFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvTv = view.findViewById(R.id.rv_content);
        rvTv.setHasFixedSize(true);

        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getContent().observe(this, getContent);

        adapter = new ContentAdapter();
        adapter.notifyDataSetChanged();

        rvTv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvTv.setAdapter(adapter);

        progressBar = view.findViewById(R.id.progress_bar);

        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                onDestroy();
                onViewCreated(Objects.requireNonNull(getView()), null);
                return true;
            }
        });

        String movieTitle = searchView.getQuery().toString();
        String searchTv;
        if (TextUtils.isEmpty(movieTitle)) {
            searchTv = null;
        } else {
            searchTv = movieTitle;
        }

        showLoading(true);

        if (savedInstanceState == null) {
            mainViewModel.setTv(getString(R.string.lang), searchTv);
            isNetworkAvailable();
        } else {
            tvs = savedInstanceState.getParcelableArrayList(ContentItem.STATE_CONTENT);
            adapter.setData(tvs);
        }
        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.rl);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onDestroy();
                onViewCreated(Objects.requireNonNull(getView()), null);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void showLoading(boolean state) {
        if (state) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(ContentItem.STATE_CONTENT, tvs);
        super.onSaveInstanceState(outState);
    }

    private void isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) Objects.requireNonNull(getActivity())
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(getActivity(), DetailContentActivity.NO_INTERNET, Toast.LENGTH_LONG).show();
        }
        if (networkInfo != null) {
            networkInfo.isConnected();
        }
    }
}