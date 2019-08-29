package ga.softogi.themoviecatalogue.provider;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Objects;

import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.db.FavMovieHelper;
import ga.softogi.themoviecatalogue.widget.FavoriteWidget;

import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.AUTHORITY_MOVIE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TABLE_MOVIE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.CONTENT_URI_MOVIE;

public class FavMovieProvider extends ContentProvider {
    private static final int MOVIE = 1;
    private static final int MOVIE_ID = 2;
    private static final int MOVIE_TITLE = 3;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY_MOVIE, TABLE_MOVIE, MOVIE);

        sUriMatcher.addURI(AUTHORITY_MOVIE, TABLE_MOVIE + "/#", MOVIE_ID);

        sUriMatcher.addURI(AUTHORITY_MOVIE, TABLE_MOVIE, MOVIE_TITLE);
    }

    private FavMovieHelper favMovieHelper;

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

        notifyMovieWidgetChange();
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(CONTENT_URI_MOVIE + "/" + added);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        favMovieHelper.openMovie();
        int deleted;
        switch (sUriMatcher.match(uri)) {
            case MOVIE_ID:
                deleted = favMovieHelper.deleteProvider(uri.getLastPathSegment());
                break;
            default:
                deleted = 0;
                break;
        }

        notifyMovieWidgetChange();
        getContext().getContentResolver().notifyChange(uri, null);
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
