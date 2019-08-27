package ga.softogi.themoviecatalogue.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.db.FavMovieHelper;
import ga.softogi.themoviecatalogue.db.FavTvHelper;
import ga.softogi.themoviecatalogue.entity.ContentItem;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context mContext;
    private Cursor cursor;
    private ArrayList<ContentItem> list;

    public StackRemoteViewsFactory(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
//        if (cursor != null) {
//            cursor.close();
//        }
//        final long identityToken = Binder.clearCallingIdentity();
//        cursor = mContext.getContentResolver().query(CONTENT_URI_MOVIE, null, null, null, null);
        FavMovieHelper favMovieHelper = new FavMovieHelper();
        FavTvHelper favTvHelper = new FavTvHelper();
        favMovieHelper.openMovie();
        favTvHelper.openTv();
        list = new ArrayList<>();
        list.addAll(favMovieHelper.getAllMovies(null));
        list.addAll(favTvHelper.getAllTv(null));
        favMovieHelper.closeMovie();
        favTvHelper.closeTv();

//        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        String title;
//        if (cursor.moveToPosition(position)) {
//            ContentItem contentItem = new ContentItem(cursor);
        Bitmap backdrop = null;
        title = list.get(position).getTitle();
        try {
            backdrop = Picasso.get()
                    .load("https://image.tmdb.org/t/p/w500/" + list.get(position).getBackdropPath())
                    .get();
        } catch (Exception e) {
            Log.d("Widget load ERROR", e.getMessage());
        }
        rv.setImageViewBitmap(R.id.image_view, backdrop);
//        }

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
