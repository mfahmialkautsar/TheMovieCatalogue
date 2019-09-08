package ga.softogi.themoviecatalogue.provider;

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

import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.db.FavTvHelper;
import ga.softogi.themoviecatalogue.fragment.FavTvFragment;
import ga.softogi.themoviecatalogue.widget.FavoriteWidget;

import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.AUTHORITY_TV;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TABLE_TV;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.CONTENT_URI_TV;

public class FavTvProvider extends ContentProvider {
    private static final int TV = 1;
    private static final int TV_ID = 2;
    private static final int TV_TITLE = 3;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private FavTvFragment favTvFragment = new FavTvFragment();

    static {
        sUriMatcher.addURI(AUTHORITY_TV, TABLE_TV, TV);

        sUriMatcher.addURI(AUTHORITY_TV, TABLE_TV + "/#", TV_ID);

        sUriMatcher.addURI(AUTHORITY_TV, TABLE_TV, TV_TITLE);
    }

    private FavTvHelper favTvHelper;
    private String searchTv = favTvFragment.getSearchKeyword();
    private Handler handler = favTvFragment.getHandler();
    private FavTvFragment.TvDataObserver tvDataObserver = new FavTvFragment.TvDataObserver(handler, favTvFragment, getContext(), searchTv);

    @Override
    public boolean onCreate() {
        favTvHelper = FavTvHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        favTvHelper.openTv();
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case TV:
                cursor = favTvHelper.queryProvider();
                break;
            case TV_ID:
                cursor = favTvHelper.queryByIdProvider(uri.getLastPathSegment());
                break;
            case TV_TITLE:
                cursor = favTvHelper.queryByTitleProvider(selection, selectionArgs);
                break;
            default:
                cursor = null;
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        favTvHelper.openTv();
        long added = favTvHelper.insertProvider(values);

        if (getContext() != null)
        getContext().getContentResolver().notifyChange(CONTENT_URI_TV, tvDataObserver);
        notifyTvWidgetChange();
        return Uri.parse(CONTENT_URI_TV + "/" + added);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        favTvHelper.openTv();
        int deleted;
        switch (sUriMatcher.match(uri)) {
            case TV_ID:
                deleted = favTvHelper.deleteProvider(uri.getLastPathSegment());
                break;
            default:
                deleted = 0;
                break;
        }

        if (getContext() != null)
        getContext().getContentResolver().notifyChange(CONTENT_URI_TV, tvDataObserver);
        notifyTvWidgetChange();
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    public void notifyTvWidgetChange() {
        int[] widgetIdTv = AppWidgetManager.getInstance(getContext()).getAppWidgetIds(new ComponentName(Objects.requireNonNull(getContext()), FavoriteWidget.class));
        for (int id : widgetIdTv) {
            AppWidgetManager.getInstance(getContext()).notifyAppWidgetViewDataChanged(id, R.id.stack_view);
        }
    }
}
