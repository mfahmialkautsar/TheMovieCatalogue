package mfahmialkautsar.themoviecatalogue.provider;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Objects;

import mfahmialkautsar.themoviecatalogue.R;
import mfahmialkautsar.themoviecatalogue.db.FavDatabaseContract;
import mfahmialkautsar.themoviecatalogue.db.FavMovieHelper;
import mfahmialkautsar.themoviecatalogue.fragment.child.FavMovieFragment;
import mfahmialkautsar.themoviecatalogue.widget.FavoriteWidget;

public class FavMovieProvider extends ContentProvider {
    private static final int MOVIE = 1;
    private static final int MOVIE_ID = 2;
    private static final int MOVIE_TITLE = 3;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(FavDatabaseContract.AUTHORITY_MOVIE, FavDatabaseContract.TABLE_MOVIE, MOVIE);

        sUriMatcher.addURI(FavDatabaseContract.AUTHORITY_MOVIE, FavDatabaseContract.TABLE_MOVIE + "/#", MOVIE_ID);

        sUriMatcher.addURI(FavDatabaseContract.AUTHORITY_MOVIE, FavDatabaseContract.TABLE_MOVIE, MOVIE_TITLE);
    }

    private FavMovieFragment favMovieFragment = new FavMovieFragment();
    private FavMovieHelper favMovieHelper;
    private String searchMovie = favMovieFragment.getSearchMovie();
    private Handler handler = favMovieFragment.getHandler();
    private FavMovieFragment.MovieDataObserver movieDataObserver = new FavMovieFragment.MovieDataObserver(handler, favMovieFragment, getContext(), searchMovie);

    @Override
    public boolean onCreate() {
        favMovieHelper = FavMovieHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        favMovieHelper.openMovie();
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                cursor = favMovieHelper.queryProvider();
                break;
            case MOVIE_ID:
                cursor = favMovieHelper.queryByIdProvider(uri.getLastPathSegment());
                break;
            case MOVIE_TITLE:
                cursor = favMovieHelper.queryByTitleProvider(selection, selectionArgs);
                break;
            default:
                cursor = null;
                break;
        }
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        favMovieHelper.openMovie();
        long added = favMovieHelper.insertProvider(values);

        if (getContext() != null)
            getContext().getContentResolver().notifyChange(FavDatabaseContract.TableColumns.CONTENT_URI_MOVIE, movieDataObserver);
        notifyMovieWidgetChange();
        return Uri.parse(FavDatabaseContract.TableColumns.CONTENT_URI_MOVIE + "/" + added);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        favMovieHelper.openMovie();
        int deleted;
        if (sUriMatcher.match(uri) == MOVIE_ID) {
            deleted = favMovieHelper.deleteProvider(uri.getLastPathSegment());
        } else {
            deleted = 0;
        }

        if (getContext() != null)
            getContext().getContentResolver().notifyChange(FavDatabaseContract.TableColumns.CONTENT_URI_MOVIE, movieDataObserver);
        notifyMovieWidgetChange();
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    public void notifyMovieWidgetChange() {
        int[] widgetIdMovie = AppWidgetManager.getInstance(getContext()).getAppWidgetIds(new ComponentName(Objects.requireNonNull(getContext()), FavoriteWidget.class));
        for (int id : widgetIdMovie) {
            AppWidgetManager.getInstance(getContext()).notifyAppWidgetViewDataChanged(id, R.id.stack_view);
        }
    }
}
