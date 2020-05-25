package mfahmialkautsar.themoviecatalogue.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class InfiniteOnScrollListener extends RecyclerView.OnScrollListener {

    private int previousTotal = 0;
    private boolean loading = true;
    private int firstVisibleItem;

    private int offset;

    private RecyclerView.LayoutManager mLayoutManager;
    private boolean isUsingLinearLayout;

    protected InfiniteOnScrollListener(LinearLayoutManager linearLayoutManager, int offset) {
        this.mLayoutManager = linearLayoutManager;
        isUsingLinearLayout = true;
        this.offset = offset;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (isUsingLinearLayout && mLayoutManager instanceof LinearLayoutManager) {
            firstVisibleItem = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        }

        int visibleItemCount = mLayoutManager.getChildCount();
        int totalItemCount = mLayoutManager.getItemCount();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        int visibleThreshold = 1;
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            offset++;
            onLoadMore(offset);

            loading = true;
        }
    }

    protected abstract void onLoadMore(int offset);
}
