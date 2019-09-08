package ga.softogi.themoviecatalogue.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

import ga.softogi.themoviecatalogue.LoadFavoriteCallback;
import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.adapter.MovieAdapter;
import ga.softogi.themoviecatalogue.db.FavMovieHelper;
import ga.softogi.themoviecatalogue.entity.MovieData;

import static ga.softogi.themoviecatalogue.MappingHelper.mapMovieCursorToArrayList;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.CONTENT_URI_MOVIE;

public class FavMovieFragment extends Fragment implements LoadFavoriteCallback {
    private static final String EXTRA_MOVIE_STATE = "extra_movie_state";
    private static final String EXTRA_IS_NOT_FOUND = "extra_is_not_found";
    private static final String EXTRA_SEARCH = "extra_search";
    private static final String EXTRA_HELPER = "extra_helper";
    private static final String EXTRA_IS_EMPTY = "extra_is_empty";
    private static final String EXTRA_TEXT_IF_EMPTY = "extra_text_if_empty";
    private ProgressBar progressBar;
    private FavMovieHelper favMovieHelper;
    private MovieAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private TextView tvIfEmpty;
    private TextView tvHelper;
//    private boolean showHelper;
    private String searchKeyword;
    private String emptyFav;
    private boolean showNoFound;
    private boolean showEmpty;
//    private String searchMovie;
    private HandlerThread handlerThread;
    private MovieDataObserver myObserver;
    private Handler handler;

    public Handler getHandler() {
        return handler;
    }

    public String getSearchMovie() {
        return searchKeyword;
    }
//    private View.OnClickListener btnSearchListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            searchKeyword = editSearch.getText().toString();
//            if (!TextUtils.isEmpty(searchKeyword)) {
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
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progress_bar);
        tvHelper = view.findViewById(R.id.helper_text);
        tvHelper.setVisibility(View.GONE);
//        favMovieHelper = FavMovieHelper.getInstance(getContext());
//        favMovieHelper.openMovie();

//        Button btnSearch = view.findViewById(R.id.btn_search);
//        btnSearch.setOnClickListener(btnSearchListener);

        tvIfEmpty = view.findViewById(R.id.tv_if_empty);
        init(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.rl);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (TextUtils.isEmpty(searchView.getQuery().toString())) {
                    searchKeyword = null;
                    onDestroy();
                    onViewCreated(Objects.requireNonNull(getView()), null);
//                    showHelper = false;
                } else {
                    onDestroy();
                    onViewCreated(Objects.requireNonNull(getView()), null);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void init(View view, Bundle savedInstanceState) {
//        editSearch = view.findViewById(R.id.search_view);

        searchView = view.findViewById(R.id.search_view);
        searchView.setQueryHint(getString(R.string.search_movie));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (isResumed()) {
                    FavMovieFragment.this.searchKeyword = s;
                    onDestroy();
                    onViewCreated(Objects.requireNonNull(getView()), null);
//                    String showing = getString(R.string.showing) + searchKeyword;
//                    tvHelper.setText(showing);
//                    tvHelper.setVisibility(View.VISIBLE);
//                    showHelper = false;
                }
                return false;
            }
        });
        //    private EditText editSearch;
//        searchKeyword = searchView.getQuery().toString();
//        if (TextUtils.isEmpty(searchKeyword)) {
//            searchMovie = null;
//        } else {
//            searchMovie = searchKeyword;
//        }
/*
        if (TextUtils.isEmpty(searchView.getQuery())) {
            if (savedInstanceState != null) {
                searchKeyword = savedInstanceState.getString(EXTRA_SEARCH);
                showHelper = savedInstanceState.getBoolean(EXTRA_HELPER);
                if (showHelper) {
                    String showing = getString(R.string.showing) + searchKeyword;
                    tvHelper.setText(showing);
                    tvHelper.setVisibility(View.VISIBLE);
                } else {
                    tvHelper.setVisibility(View.GONE);
                    showHelper = false;
                }
            } else {
                tvHelper.setVisibility(View.GONE);
                showHelper = false;
            }
        }
 */

        handlerThread = new HandlerThread("MovieDataObserver");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        myObserver = new MovieDataObserver(handler, this, getContext(), searchKeyword);
        view.getContext().getContentResolver().registerContentObserver(CONTENT_URI_MOVIE, true, myObserver);

        adapter = new MovieAdapter();

        RecyclerView rvFavMovie = view.findViewById(R.id.rv_content);
        rvFavMovie.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager;
        if (Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(getContext(), 2);
        } else {
            gridLayoutManager = new GridLayoutManager(getContext(), 4);
        }
        rvFavMovie.setLayoutManager(gridLayoutManager);
        rvFavMovie.setAdapter(adapter);

        if (savedInstanceState == null) {
            new LoadFavoriteAsync(getContext(), this, searchKeyword).execute();
        } else {
            showNoFound = savedInstanceState.getBoolean(EXTRA_IS_NOT_FOUND);
            showEmpty = savedInstanceState.getBoolean(EXTRA_IS_EMPTY);
            if (showNoFound) {
                emptyFav = savedInstanceState.getString(EXTRA_TEXT_IF_EMPTY);
                tvIfEmpty.setText(emptyFav);
                tvIfEmpty.setVisibility(View.VISIBLE);
            } else if (showEmpty) {
                tvIfEmpty.setText(getString(R.string.no_fav_movie));
                tvIfEmpty.setVisibility(View.VISIBLE);
            } else {
                tvIfEmpty.setVisibility(View.GONE);
            }
            //    private MovieDataObserver myObserver;
            ArrayList<MovieData> listFavMovie = savedInstanceState.getParcelableArrayList(EXTRA_MOVIE_STATE);
//            if (listFavMovie != null) {
            adapter.setMovieDataList(listFavMovie);
//            }
        }
    }

    @Override
    public void preExecute() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public void postExecute(Cursor items) {
        ArrayList<MovieData> listMovie = mapMovieCursorToArrayList(items);
        if (listMovie.size() > 0) {
            Log.d("postExecute", "YESSSS");
            tvIfEmpty.setVisibility(View.GONE);
            adapter.setMovieDataList(listMovie);
            showNoFound = false;
            showEmpty = false;
        } else {
            Log.d("postExecute", "NOOOOOOO");
            adapter.clear();
            if (getContext() != null) {
//            adapter.setData(new ArrayList<ContentItem>());
                if (TextUtils.isEmpty(searchKeyword)) {
                    tvIfEmpty.setText(getString(R.string.no_fav_movie));
                    showEmpty = true;
                    showNoFound = false;
                } else {
                    emptyFav = "\"" + searchKeyword + "\"" + getString(R.string.search_not_in_favorite);
                    tvIfEmpty.setText(emptyFav);
                    showNoFound = true;
                    showEmpty = false;
                }
                tvIfEmpty.setVisibility(View.VISIBLE);
//            showToast(getString(R.string.empty_fav));
            }
        }
        progressBar.setVisibility(View.GONE);
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        favMovieHelper.closeMovie();
//    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static class MovieDataObserver extends ContentObserver {
        final Context context;
        final FavMovieFragment favMovieFragment;
        final String searchMovie;
        public MovieDataObserver(Handler handler, FavMovieFragment favMovieFragment, Context context, String searchMovie) {
            super(handler);
            this.favMovieFragment = favMovieFragment;
            this.searchMovie = searchMovie;
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new LoadFavoriteAsync(context, favMovieFragment, searchMovie).execute();
        }
    }

    private static class LoadFavoriteAsync extends AsyncTask<Void, Void, Cursor> {
        //        private final WeakReference<FavMovieHelper> weakContentHelper;
        private final WeakReference<Context> weakContext;
        private final WeakReference<LoadFavoriteCallback> weakCallback;
        private final String title;

        private LoadFavoriteAsync(Context context, LoadFavoriteCallback callback, String searchMovie) {
//            weakContentHelper = new WeakReference<>(favMovieHelper);
            weakContext = new WeakReference<>(context);
            weakCallback = new WeakReference<>(callback);
            title = searchMovie;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            Context context = weakContext.get();
            if (TextUtils.isEmpty(title)) {
                return context.getContentResolver().query(CONTENT_URI_MOVIE, null, null, null, null);
            } else {
                return context.getContentResolver().query(CONTENT_URI_MOVIE, null, "title LIKE ?", new String[]{"%" + title + "%"}, null);
            }
        }

        @Override
        protected void onPostExecute(Cursor items) {
            super.onPostExecute(items);
            weakCallback.get().postExecute(items);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.requestFocus();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_MOVIE_STATE, adapter.getMovieDataList());
//        outState.putBoolean(EXTRA_HELPER, showHelper);
        outState.putString(EXTRA_SEARCH, searchKeyword);
        outState.putBoolean(EXTRA_IS_NOT_FOUND, showNoFound);
        outState.putString(EXTRA_TEXT_IF_EMPTY, emptyFav);
        outState.putBoolean(EXTRA_IS_EMPTY, showEmpty);
    }
}
