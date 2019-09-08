package ga.softogi.themoviecatalogue.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Objects;

import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.adapter.TvAdapter;
import ga.softogi.themoviecatalogue.entity.Tv;
import ga.softogi.themoviecatalogue.entity.TvData;
import ga.softogi.themoviecatalogue.network.ApiService;
import ga.softogi.themoviecatalogue.util.EndlessOnScrollListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainTvFragment extends Fragment {

    private static final String STATE_TV = "state_tv";
    private static final String EXTRA_IS_NOT_FOUND = "extra_is_not_found";
    private static final String EXTRA_SEARCH = "extra_search";
    private static final String EXTRA_HELPER = "extra_helper";
    private static final String EXTRA_PAGE_STATE = "extra_page_state";
    private static final String EXTRA_PAGE = "extra_page";
    private static final String EXTRA_SCROLL = "extra_scroll";
    private static final String EXTRA_TEXT_IF_EMPTY = "extra_text_if_empty";
    private RecyclerView rvMovies;
    private GridLayoutManager gridLayoutManager;
    private TvAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView tvIfEmpty;
    private ArrayList<TvData> tvList = new ArrayList<>();
    private String keyword;
    private SearchView searchView;
    private TextView tvHelper;
    private boolean shouldAddScroll;
    private boolean nextPageInstance;
    private boolean showHelper;
    private boolean showNoFound;
    private String noFound;

    private int page;
//    private int limit = 20;

    private EndlessOnScrollListener endlessOnScrollListener;

    private ApiService apiService;
    private Call<Tv> callTv;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new TvAdapter();
        progressBar = view.findViewById(R.id.progress_bar);
        tvHelper = view.findViewById(R.id.helper_text);

        if (Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(getContext(), 2);
        } else {
            gridLayoutManager = new GridLayoutManager(getContext(), 4);
        }
        rvMovies = view.findViewById(R.id.rv_content);
        rvMovies.setLayoutManager(gridLayoutManager);
        tvIfEmpty = view.findViewById(R.id.tv_if_empty);
        swipeRefreshLayout = view.findViewById(R.id.rl);

        rvMovies.setHasFixedSize(true);
        rvMovies.setAdapter(adapter);

        searchView = view.findViewById(R.id.search_view);
        searchView.clearFocus();
        searchView.setQueryHint(getString(R.string.search_tv));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
//                removeScroll();
//                page = 1;
//                onDestroy();
//                onViewCreated(Objects.requireNonNull(getView()), null);
                MainTvFragment.this.keyword = s;
                refreshData(keyword);
                String showing = getString(R.string.showing) + keyword;
                tvHelper.setText(showing);
                tvHelper.setVisibility(View.VISIBLE);
                showHelper = true;
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

//        keyword = searchView.getQuery().toString();
        if (TextUtils.isEmpty(searchView.getQuery())) {
            if (savedInstanceState != null) {
                keyword = savedInstanceState.getString(EXTRA_SEARCH);
                showHelper = savedInstanceState.getBoolean(EXTRA_HELPER);
                if (showHelper) {
                    String showing = getString(R.string.showing) + keyword;
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (TextUtils.isEmpty(searchView.getQuery().toString())) {
                    removeScroll();
                    page = 1;
                    keyword = null;
                    onDestroy();
                    onViewCreated(Objects.requireNonNull(getView()), null);
                    showHelper = false;
                } else {
                    refreshData(keyword);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        if (savedInstanceState == null) {
            page = 1;
            loadData(keyword);
        } else {
            showNoFound = savedInstanceState.getBoolean(EXTRA_IS_NOT_FOUND);
            if (showNoFound) {
                noFound = savedInstanceState.getString(EXTRA_TEXT_IF_EMPTY);
                tvIfEmpty.setText(noFound);
                tvIfEmpty.setVisibility(View.VISIBLE);
            }
            page = savedInstanceState.getInt(EXTRA_PAGE);
            nextPageInstance = savedInstanceState.getBoolean(EXTRA_PAGE_STATE);
            keyword = savedInstanceState.getString(EXTRA_SEARCH);
            removeScroll();
            shouldAddScroll = savedInstanceState.getBoolean(EXTRA_SCROLL);
            if (shouldAddScroll) {
                addScroll(keyword, nextPageInstance);
            }
            tvList = savedInstanceState.getParcelableArrayList(STATE_TV);
            adapter.setTvDataList(tvList);
        }
    }

    private void addScroll(final String keyword, final boolean nextPage) {
        endlessOnScrollListener = new EndlessOnScrollListener(gridLayoutManager, page) {
            @Override
            protected void onLoadMore(int next) {
                if (nextPage) {
                    page = next;
                }
                loadData(keyword);
            }
        };

        rvMovies.addOnScrollListener(endlessOnScrollListener);
    }

    private void removeScroll() {
        rvMovies.removeOnScrollListener(endlessOnScrollListener);
    }

    private void loadData(String keyword) {
//        if (swipeRefreshLayout != null) {
//            swipeRefreshLayout.post(new Runnable() {
//                @Override
//                public void run() {
//                    swipeRefreshLayout.setRefreshing(true);
        progressBar.setVisibility(View.VISIBLE);
//                }
//            });
//        }

        apiService = new ApiService();
        if (TextUtils.isEmpty(keyword)) {
            callTv = apiService.getApiInterface().discoverTvs(page);
        } else {
            callTv = apiService.getApiInterface().searchTv(page, keyword);
        }
        callTv.enqueue(callback(keyword));
    }

    private Callback<Tv> callback(final String keyword) {
        return new Callback<Tv>() {
            @Override
            public void onResponse(@NonNull Call<Tv> call, @NonNull Response<Tv> response) {
                Tv tv = response.body();

                if (tv != null) {
                    if (adapter != null) {
                        adapter.addAllTvs(tv.getResults());
                        tvIfEmpty.setVisibility(View.GONE);
                        showNoFound = false;
                    }
                    if (tv.getTotalResults() == 0) {
                        noFound = "\"" + keyword + "\"" + getString(R.string.search_not_found);
                        tvIfEmpty.setText(noFound);
                        tvIfEmpty.setVisibility(View.VISIBLE);
                        showNoFound = true;
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.no_data), Toast.LENGTH_LONG).show();
                }
                shouldAddScroll = true;
                nextPageInstance = true;
                removeScroll();
                addScroll(keyword, nextPageInstance);
                if (tv != null && page >= tv.getTotalPages()) {
                    removeScroll();
                    shouldAddScroll = false;
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Tv> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(getContext(), getString(R.string.request_timeout), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                }

                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                nextPageInstance = false;
                progressBar.setVisibility(View.GONE);
                removeScroll();
                addScroll(keyword, nextPageInstance);
            }
        };
    }

    private void refreshData(String keyword) {
        if (adapter != null) {
            adapter.clear();
        }
        page = 1;

//        limit = 20;

        removeScroll();
//        addScroll(keyword, true);

        loadData(keyword);
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.requestFocus();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_TV, adapter.getTvDataList());
        outState.putBoolean(EXTRA_PAGE_STATE, nextPageInstance);
        outState.putInt(EXTRA_PAGE, page);
        outState.putString(EXTRA_SEARCH, keyword);
        outState.putBoolean(EXTRA_SCROLL, shouldAddScroll);
        outState.putBoolean(EXTRA_HELPER, showHelper);
        outState.putBoolean(EXTRA_IS_NOT_FOUND, showNoFound);
        outState.putString(EXTRA_TEXT_IF_EMPTY, noFound);
    }
}
