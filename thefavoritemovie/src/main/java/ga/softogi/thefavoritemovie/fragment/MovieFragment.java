package ga.softogi.thefavoritemovie.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

import ga.softogi.thefavoritemovie.LoadFavoriteCallback;
import ga.softogi.thefavoritemovie.R;
import ga.softogi.thefavoritemovie.activity.DetailContentActivity;
import ga.softogi.thefavoritemovie.adapter.ContentAdapter;
import ga.softogi.thefavoritemovie.entity.ContentItem;

import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.CONTENT_URI_MOVIE;
import static ga.softogi.thefavoritemovie.helper.MappingHelper.mapCursorToArrayList;

public class MovieFragment extends Fragment implements LoadFavoriteCallback {
    //Ini dipake kalo gak mao pake onResume (tapi harus implement parcellable di ContentItem). Bisa sih dipake berbarengan, tapi jadi useless
//    private static final String EXTRA_MOVIE_STATE = "EXTRA_MOVIE_STATE";
    private ProgressBar progressBar;
    private ContentAdapter adapter;


    public MovieFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        progressBar = view.findViewById(R.id.progress_bar);

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
            new getData(getContext(), this, searchMovie).execute();
            isNetworkAvailable();
        }
        /* Ini dipake kalo gak mao pake onResume (tapi harus implement parcellable di ContentItem). Bisa sih dipake berbarengan, tapi jadi useless
        else {
            listFavMovie = savedInstanceState.getParcelableArrayList(EXTRA_MOVIE_STATE);
//            if (listFavMovie != null) {
                adapter.setData(listFavMovie);
//            }
        }
         */
    }

    @Override
    public void postExecute(Cursor items) {
        ArrayList<ContentItem> listMovie = mapCursorToArrayList(items);
        if (listMovie.size() > 0) {
            adapter.setData(listMovie);
        } else {
            adapter.setData(new ArrayList<ContentItem>());
            showToast(getString(R.string.empty_fav));
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        init(Objects.requireNonNull(getView()), null);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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

    /* Ini dipake kalo gak mao pake onResume (tapi harus implement parcellable di ContentItem). Bisa sih dipake berbarengan, tapi jadi useless
        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putParcelableArrayList(EXTRA_MOVIE_STATE, adapter.getData());
        }
    */
    private static class getData extends AsyncTask<Void, Void, Cursor> {
        private final WeakReference<Context> weakContext;
        private final WeakReference<LoadFavoriteCallback> weakCallback;
        private final String title;

        private getData(Context context, LoadFavoriteCallback callback, String searchMovie) {
            weakContext = new WeakReference<>(context);
            weakCallback = new WeakReference<>(callback);
            title = searchMovie;
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