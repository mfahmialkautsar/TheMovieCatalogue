package ga.softogi.themoviecatalogue.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
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
import ga.softogi.themoviecatalogue.adapter.ContentAdapter;
import ga.softogi.themoviecatalogue.db.FavMovieHelper;
import ga.softogi.themoviecatalogue.entity.ContentItem;

import static ga.softogi.themoviecatalogue.MappingHelper.mapCursorToArrayList;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.CONTENT_URI_MOVIE;

public class FavMovieFragment extends Fragment implements LoadFavoriteCallback {
    private static final String EXTRA_MOVIE_STATE = "EXTRA_MOVIE_STATE";
    private ProgressBar progressBar;
    private FavMovieHelper favMovieHelper;
    private ContentAdapter adapter;
    private RecyclerView rvFavMovie;
    private TextView tvIfEmpty;
    private HandlerThread handlerThread;
    //    private DataObserver myObserver;
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
//        favMovieHelper = FavMovieHelper.getInstance(getContext());
//        favMovieHelper.openMovie();

//        Button btnSearch = view.findViewById(R.id.btn_search);
//        btnSearch.setOnClickListener(btnSearchListener);

        tvIfEmpty = view.findViewById(R.id.tv_if_empty);
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

//        handlerThread = new HandlerThread("DataObserver");
//        handlerThread.start();
//        Handler handler = new Handler(handlerThread.getLooper());
//        myObserver = new FavMovieFragment.DataObserver(handler, getContext(), searchMovie);
//        getContext().getContentResolver().registerContentObserver(CONTENT_URI_MOVIE, true, myObserver);
        adapter = new ContentAdapter();

        rvFavMovie = view.findViewById(R.id.rv_content);
        rvFavMovie.setHasFixedSize(true);
        if (Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            rvFavMovie.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            rvFavMovie.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        }
        rvFavMovie.setAdapter(adapter);

        if (savedInstanceState == null) {
            new FavMovieFragment.LoadFavoriteAsync(getContext(), this, searchMovie).execute();
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
    public void postExecute(Cursor items) {
        ArrayList<ContentItem> listMovie = mapCursorToArrayList(items);
        if (listMovie.size() > 0) {
            tvIfEmpty.setVisibility(View.GONE);
            adapter.setData(listMovie);
        } else {
//            adapter.setData(new ArrayList<ContentItem>());
            tvIfEmpty.setText(getString(R.string.no_fav_movie));
            tvIfEmpty.setVisibility(View.VISIBLE);
//            showToast(getString(R.string.empty_fav));
        }
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
//        favMovieHelper.closeMovie();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

//    public static class DataObserver extends ContentObserver {
//        /**
//         * Creates a content observer.
//         *
//         * @param handler The handler to run {@link #onChange} on, or null if none.
//         */
//        final Context context;
//        String searchMovie;
//        public DataObserver(Handler handler, Context context, String searchMovie) {
//            super(handler);
//            this.context = context;
//            this.searchMovie = searchMovie;
//        }
//
//        @Override
//        public void onChange(boolean selfChange) {
//            super.onChange(selfChange);
//            new LoadFavoriteAsync(context, (LoadFavoriteCallback) context, searchMovie).execute();
//        }
//    }

    /* Ini dipake kalo gak mao pake onResume. Bisa sih dipake berbarengan, tapi jadi useless
        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putParcelableArrayList(EXTRA_MOVIE_STATE, adapter.getData());
        }
    */
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
}
