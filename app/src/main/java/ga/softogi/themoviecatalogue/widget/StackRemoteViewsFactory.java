package ga.softogi.themoviecatalogue.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.db.FavMovieHelper;
import ga.softogi.themoviecatalogue.db.FavTvHelper;
import ga.softogi.themoviecatalogue.entity.MovieData;
import ga.softogi.themoviecatalogue.network.NetworkContract;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context mContext;
    private ArrayList<MovieData> listMovie;
    private FavMovieHelper favMovieHelper;
    private FavTvHelper favTvHelper;

    StackRemoteViewsFactory(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {
        favMovieHelper = FavMovieHelper.getInstance(mContext);
        favMovieHelper.openMovie();
        favTvHelper = FavTvHelper.getInstance(mContext);
        favTvHelper.openTv();
    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();
        listMovie = new ArrayList<>();

        listMovie.addAll(favMovieHelper.getAllMovies(null));
        listMovie.addAll(favTvHelper.getAllTv(null));

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        favMovieHelper.closeMovie();
        favTvHelper.closeTv();
    }

    @Override
    public int getCount() {
        return listMovie.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        String title = null;
        Bitmap backdrop = null;
        if (listMovie.size() > 0) {
            title = listMovie.get(position).getTitle();
            if (listMovie.get(position).getBackdropPath() != null) {
                try {
                    backdrop = Picasso.get()
                            .load(NetworkContract.IMG_URL + "w500" + listMovie.get(position).getBackdropPath())
                            .get();
                } catch (Exception e) {
                    Log.e("Widget load ERROR", e.getMessage());
                }
            }
            rv.setImageViewBitmap(R.id.image_view, backdrop);
        }

        Bundle extras = new Bundle();
        extras.putString(FavoriteWidget.EXTRA_ITEM, title);

        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        rv.setOnClickFillInIntent(R.id.image_view, fillInIntent);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
