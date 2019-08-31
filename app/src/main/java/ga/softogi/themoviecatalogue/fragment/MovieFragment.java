package ga.softogi.themoviecatalogue.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.activity.DetailContentActivity;
import ga.softogi.themoviecatalogue.activity.MainActivity;
import ga.softogi.themoviecatalogue.adapter.ContentAdapter;
import ga.softogi.themoviecatalogue.entity.ContentItem;
import ga.softogi.themoviecatalogue.network.MainViewModel;

public class MovieFragment extends Fragment {

    private String search;
    private Bundle mBundle;
    MovieFragment movieFragment;
    private ContentAdapter adapter;
    private ProgressBar progressBar;
    private ArrayList<ContentItem> movies = new ArrayList<>();
    private Observer<ArrayList<ContentItem>> getMovie = new Observer<ArrayList<ContentItem>>() {
        @Override
        public void onChanged(@Nullable ArrayList<ContentItem> movies) {
            if (movies != null) {
                adapter.setData(movies);
                showLoading(false);
            }
        }
    };

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

//    private View.OnClickListener btnSearchListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            movieTitle = searchView.getQuery().toString();
//            if (!TextUtils.isEmpty(movieTitle)) {
//                onDestroy();
//                onViewCreated(Objects.requireNonNull(getView()), null);
//            }
//        }
//    };

    public MovieFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //        String movieTitle = editSearch.getText().toString();
//        bundle.putString("extra_search", movieTitle);
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rvMovie = view.findViewById(R.id.rv_content);
        rvMovie.setHasFixedSize(true);
//        editSearch = view.findViewById(R.id.search_view);

        final MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getContent().observe(this, getMovie);

        adapter = new ContentAdapter();
        adapter.notifyDataSetChanged();

        if (Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            rvMovie.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            rvMovie.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        }
//        rvMovie.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvMovie.setAdapter(adapter);

        progressBar = view.findViewById(R.id.progress_bar);

        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setQueryHint(getString(R.string.search_movie));
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
        //    private EditText editSearch;
        String movieTitle = searchView.getQuery().toString();
        String searchMovie;
        if (TextUtils.isEmpty(movieTitle)) {
            searchMovie = null;
        } else {
            searchMovie = movieTitle;
        }

//        Button btnSearch = view.findViewById(R.id.btn_search);
//        btnSearch.setOnClickListener(btnSearchListener);

        showLoading(true);

        if (savedInstanceState == null) {
            mainViewModel.setMovie(getString(R.string.lang), searchMovie);
            isNetworkAvailable();
        } else {
            movies = savedInstanceState.getParcelableArrayList(ContentItem.STATE_CONTENT);
            adapter.setData(movies);
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

    private void showLoading(Boolean state) {
        if (state) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(ContentItem.STATE_CONTENT, movies);
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
