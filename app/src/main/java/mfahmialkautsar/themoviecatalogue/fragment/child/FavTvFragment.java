package mfahmialkautsar.themoviecatalogue.fragment.child;

import android.content.Context;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

import mfahmialkautsar.themoviecatalogue.LoadFavoriteCallback;
import mfahmialkautsar.themoviecatalogue.MappingHelper;
import mfahmialkautsar.themoviecatalogue.R;
import mfahmialkautsar.themoviecatalogue.adapter.TvAdapter;
import mfahmialkautsar.themoviecatalogue.db.FavDatabaseContract;
import mfahmialkautsar.themoviecatalogue.entity.TvData;

public class FavTvFragment extends Fragment implements LoadFavoriteCallback {
    private static final String EXTRA_TV_STATE = "EXTRA_TV_STATE";
    private static final String EXTRA_IS_NOT_FOUND = "extra_is_not_found";
    private static final String EXTRA_SEARCH = "extra_search";
    private static final String EXTRA_IS_EMPTY = "extra_is_empty";
    private static final String EXTRA_TEXT_IF_EMPTY = "extra_text_if_empty";
    private ProgressBar progressBar;
    private TvAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private TextView tvIfEmpty;
    private String searchKeyword;
    private String emptyFav;
    private boolean showNoFound;
    private boolean showEmpty;
    private Handler handler;

    public FavTvFragment() {
    }

    public Handler getHandler() {
        return handler;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        progressBar = view.findViewById(R.id.progress_bar);
        TextView tvHelper = view.findViewById(R.id.helper_text);
        tvHelper.setVisibility(View.GONE);

        tvIfEmpty = view.findViewById(R.id.tv_if_empty);

        searchView = view.findViewById(R.id.search_view);
        searchView.setQueryHint(getString(R.string.search_tv));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (isResumed()) {
                    FavTvFragment.this.searchKeyword = s;
                    onDestroy();
                    onViewCreated(Objects.requireNonNull(getView()), null);
                }
                return true;
            }
        });

        HandlerThread handlerThread = new HandlerThread("TvDataObserver");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        TvDataObserver myObserver = new TvDataObserver(handler, this, getContext(), searchKeyword);
        view.getContext().getContentResolver().registerContentObserver(FavDatabaseContract.TableColumns.CONTENT_URI_TV, true, myObserver);

        adapter = new TvAdapter();

        RecyclerView rvFavTv = view.findViewById(R.id.rv_content);
        rvFavTv.setHasFixedSize(true);
        if (Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            rvFavTv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            rvFavTv.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        }
        rvFavTv.setAdapter(adapter);

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
                tvIfEmpty.setText(getString(R.string.no_fav_tv));
                tvIfEmpty.setVisibility(View.VISIBLE);
            } else {
                tvIfEmpty.setVisibility(View.GONE);
            }
            ArrayList<TvData> list = savedInstanceState.getParcelableArrayList(EXTRA_TV_STATE);
            if (list != null) {
                adapter.setTvDataList(list);
            }
        }

        swipeRefreshLayout = view.findViewById(R.id.rl);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (TextUtils.isEmpty(searchView.getQuery().toString())) {
                    searchKeyword = null;
                    adapter.clear();
                    onDestroy();
                    onViewCreated(Objects.requireNonNull(getView()), null);
                } else {
                    onDestroy();
                    onViewCreated(Objects.requireNonNull(getView()), null);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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
        ArrayList<TvData> listTv = MappingHelper.mapTvCursorToArrayList(items);
        if (listTv.size() > 0) {
            tvIfEmpty.setVisibility(View.GONE);
            adapter.setTvDataList(listTv);
            showNoFound = false;
            showEmpty = false;
        } else {
            adapter.clear();
            if (getContext() != null) {
                if (TextUtils.isEmpty(searchKeyword)) {
                    tvIfEmpty.setText(getString(R.string.no_fav_tv));
                    showEmpty = true;
                    showNoFound = false;
                } else {
                    emptyFav = "\"" + searchKeyword + "\"" + getString(R.string.search_not_in_favorite);
                    tvIfEmpty.setText(emptyFav);
                    showNoFound = true;
                    showEmpty = false;
                }
                tvIfEmpty.setVisibility(View.VISIBLE);
            }
        }
        items.close();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.requestFocus();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(EXTRA_TV_STATE, adapter.getTvDataList());
        outState.putString(EXTRA_SEARCH, searchKeyword);
        outState.putBoolean(EXTRA_IS_NOT_FOUND, showNoFound);
        outState.putString(EXTRA_TEXT_IF_EMPTY, emptyFav);
        outState.putBoolean(EXTRA_IS_EMPTY, showEmpty);
        super.onSaveInstanceState(outState);
    }

    public static class TvDataObserver extends ContentObserver {
        final FavTvFragment favTvFragment;
        final Context context;
        final String searchTv;

        public TvDataObserver(Handler handler, FavTvFragment favTvFragment, Context context, String searchTv) {
            super(handler);
            this.favTvFragment = favTvFragment;
            this.context = context;
            this.searchTv = searchTv;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new LoadFavoriteAsync(context, favTvFragment, searchTv).execute();
        }
    }

    private static class LoadFavoriteAsync extends AsyncTask<Void, Void, Cursor> {
        private final WeakReference<Context> weakContext;
        private final WeakReference<LoadFavoriteCallback> weakCallback;
        private final String title;

        private LoadFavoriteAsync(Context context, LoadFavoriteCallback callback, String searchTv) {
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
                return context.getContentResolver().query(FavDatabaseContract.TableColumns.CONTENT_URI_TV, null, null, null, null);
            } else {
                return context.getContentResolver().query(FavDatabaseContract.TableColumns.CONTENT_URI_TV, null, "title LIKE ?", new String[]{"%" + title + "%"}, null);
            }
        }

        @Override
        protected void onPostExecute(Cursor items) {
            super.onPostExecute(items);
            weakCallback.get().postExecute(items);
        }
    }
}
