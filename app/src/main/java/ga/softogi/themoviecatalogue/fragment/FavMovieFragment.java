package ga.softogi.themoviecatalogue.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

import ga.softogi.themoviecatalogue.LoadFavoriteCallback;
import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.adapter.ContentAdapter;
import ga.softogi.themoviecatalogue.db.FavMovieHelper;
import ga.softogi.themoviecatalogue.entity.ContentItem;

public class FavMovieFragment extends Fragment implements LoadFavoriteCallback {
    private static final String EXTRA_MOVIE_STATE = "EXTRA_MOVIE_STATE";
    private ProgressBar progressBar;
    private FavMovieHelper favMovieHelper;
    private ContentAdapter adapter;
    private ArrayList<ContentItem> listFavMovie = new ArrayList<>();

//    private View.OnClickListener btnSearchListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            movieTitle = editSearch.getText().toString();
//            if (!TextUtils.isEmpty(movieTitle)) {
//                onDestroy();
//                onViewCreated(Objects.requireNonNull(getView()), null);
//            }
//        }
//    };


    public FavMovieFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        progressBar = view.findViewById(R.id.progress_bar);
        favMovieHelper = FavMovieHelper.getInstance(getContext());
        favMovieHelper.openMovie();

//        Button btnSearch = view.findViewById(R.id.btn_search);
//        btnSearch.setOnClickListener(btnSearchListener);

        init(view, savedInstanceState);

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

    private void init(View view, Bundle savedInstanceState) {
//        editSearch = view.findViewById(R.id.search_view);

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
        //    private EditText editSearch;
        String movieTitle = searchView.getQuery().toString();
        String searchMovie;
        if (TextUtils.isEmpty(movieTitle)) {
            searchMovie = null;
        } else {
            searchMovie = movieTitle;
        }
        adapter = new ContentAdapter();

        RecyclerView rvFavorite = view.findViewById(R.id.rv_content);
        rvFavorite.setHasFixedSize(true);
        rvFavorite.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFavorite.setAdapter(adapter);

        if (savedInstanceState == null) {
            new FavMovieFragment.LoadFavoriteAsync(favMovieHelper, this, searchMovie).execute();
        }
        /* Ini dipake kalo gak mao pake onResume. Bisa sih dipake berbarengan, tapi jadi useless
        else {
            listFavMovie = savedInstanceState.getParcelableArrayList(EXTRA_MOVIE_STATE);
//            if (listFavMovie != null) {
                adapter.setData(listFavMovie);
//            }
        }
         */
    }

    @Override
    public void preExecute() {
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void postExecute(ArrayList<ContentItem> items) {
        adapter.setData(items);
        listFavMovie = items;
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        init(Objects.requireNonNull(getView()), null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        favMovieHelper.closeMovie();
    }
    /* Ini dipake kalo gak mao pake onResume. Bisa sih dipake berbarengan, tapi jadi useless
        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putParcelableArrayList(EXTRA_MOVIE_STATE, adapter.getData());
        }
    */
    private static class LoadFavoriteAsync extends AsyncTask<Void, Void, ArrayList<ContentItem>> {
        private final WeakReference<FavMovieHelper> weakContentHelper;
        private final WeakReference<LoadFavoriteCallback> weakCallback;
        private final String title;

        private LoadFavoriteAsync(FavMovieHelper favMovieHelper, LoadFavoriteCallback callback, String searchMovie) {
            weakContentHelper = new WeakReference<>(favMovieHelper);
            weakCallback = new WeakReference<>(callback);
            title = searchMovie;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected ArrayList<ContentItem> doInBackground(Void... voids) {
            return weakContentHelper.get().getAllMovies(title);
        }

        @Override
        protected void onPostExecute(ArrayList<ContentItem> items) {
            super.onPostExecute(items);
            weakCallback.get().postExecute(items);
        }
    }
}
