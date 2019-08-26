package ga.softogi.themoviecatalogue.fragment;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
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
import ga.softogi.themoviecatalogue.db.FavTvHelper;
import ga.softogi.themoviecatalogue.entity.ContentItem;

import static ga.softogi.themoviecatalogue.MappingHelper.mapTvCursorToArrayList;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.CONTENT_URI_TV;

public class FavTvFragment extends Fragment implements LoadFavoriteCallback {
    private static final String EXTRA_TV_STATE = "EXTRA_TV_STATE";
    private ProgressBar progressBar;
    private FavTvHelper favTvHelper;
    private ContentAdapter adapter;
    private RecyclerView rvFavorite;
    private HandlerThread handlerThread;
//    private DataObserver myObserver;


    public FavTvFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        progressBar = view.findViewById(R.id.progress_bar);
//        favTvHelper = FavTvHelper.getInstance(getContext());
//        favTvHelper.openTv();

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

        String tvTitle = searchView.getQuery().toString();
        String searchTv;
        if (TextUtils.isEmpty(tvTitle)) {
            searchTv = null;
        } else {
            searchTv = tvTitle;
        }

//        handlerThread = new HandlerThread("DataObserver");
//        handlerThread.start();
//        Handler handler = new Handler(handlerThread.getLooper());
//        myObserver = new DataObserver(handler, view.getContext(), searchTv);
//        view.getContext().getContentResolver().registerContentObserver(CONTENT_URI_TV, true, myObserver);
        adapter = new ContentAdapter();

        rvFavorite = view.findViewById(R.id.rv_content);
        rvFavorite.setHasFixedSize(true);
        rvFavorite.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFavorite.setAdapter(adapter);

        if (savedInstanceState == null) {
            new FavTvFragment.LoadFavoriteAsync(getContext(), this, searchTv).execute();
        }
        /*
        else {
            ArrayList<ContentItem> list = savedInstanceState.getParcelableArrayList(EXTRA_TV_STATE);
            if (list != null) {
                adapter.setData(list);
            }
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
        ArrayList<ContentItem> listTv = mapTvCursorToArrayList(items);
        if (listTv.size() > 0) {
            adapter.setData(listTv);
        } else {
            adapter.setData(new ArrayList<ContentItem>());
            showSnackbarMessage(getString(R.string.empty_fav));
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
//        favTvHelper.closeTv();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(EXTRA_TV_STATE, adapter.getData());
        super.onSaveInstanceState(outState);
    }

    private static class LoadFavoriteAsync extends AsyncTask<Void, Void, Cursor> {
//        private final WeakReference<FavTvHelper> weakTvHelper;
        private final WeakReference<Context> weakContext;
        private final WeakReference<LoadFavoriteCallback> weakCallback;
        private final String title;

        private LoadFavoriteAsync(Context context, LoadFavoriteCallback callback, String searchTv) {
//            weakTvHelper = new WeakReference<>(favTvHelper);
            weakContext = new WeakReference<>(context);
            weakCallback = new WeakReference<>(callback);
            title = searchTv;
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
                return context.getContentResolver().query(CONTENT_URI_TV, null, null, null, null);
            } else {
                return context.getContentResolver().query(CONTENT_URI_TV, null, "title LIKE ?", new String[]{"%" + title + "%"}, null);
            }
        }

        @Override
        protected void onPostExecute(Cursor items) {
            super.onPostExecute(items);
            weakCallback.get().postExecute(items);
        }
    }

//    public static class DataObserver extends ContentObserver {
//        /**
//         * Creates a content observer.
//         *
//         * @param handler The handler to run {@link #onChange} on, or null if none.
//         */
//        final Context context;
//        final String searchTv;
//        public DataObserver(Handler handler, Context context, String searchTv) {
//            super(handler);
//            this.context = context;
//            this.searchTv = searchTv;
//        }
//
//        @Override
//        public void onChange(boolean selfChange) {
//            super.onChange(selfChange);
//            new LoadFavoriteAsync(context, (LoadFavoriteCallback) context, searchTv).execute();
//        }
//    }

    private void showSnackbarMessage(String message) {
        Snackbar.make(rvFavorite, message, Snackbar.LENGTH_SHORT).show();
    }
}
