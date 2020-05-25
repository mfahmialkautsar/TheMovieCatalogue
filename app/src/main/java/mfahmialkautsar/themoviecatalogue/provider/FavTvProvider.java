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
import mfahmialkautsar.themoviecatalogue.db.FavTvHelper;
import mfahmialkautsar.themoviecatalogue.fragment.child.FavTvFragment;
import mfahmialkautsar.themoviecatalogue.widget.FavoriteWidget;

public class FavTvProvider extends ContentProvider {
    private static final int TV = 1;
    private static final int TV_ID = 2;
    private static final int TV_TITLE = 3;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(FavDatabaseContract.AUTHORITY_TV, FavDatabaseContract.TABLE_TV, TV);

        sUriMatcher.addURI(FavDatabaseContract.AUTHORITY_TV, FavDatabaseContract.TABLE_TV + "/#", TV_ID);

        sUriMatcher.addURI(FavDatabaseContract.AUTHORITY_TV, FavDatabaseContract.TABLE_TV, TV_TITLE);
    }

    private FavTvFragment favTvFragment = new FavTvFragment();
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
            getContext().getContentResolver().notifyChange(FavDatabaseContract.TableColumns.CONTENT_URI_TV, tvDataObserver);
        notifyTvWidgetChange();
        return Uri.parse(FavDatabaseContract.TableColumns.CONTENT_URI_TV + "/" + added);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        favTvHelper.openTv();
        int deleted;
        if (sUriMatcher.match(uri) == TV_ID) {
            deleted = favTvHelper.deleteProvider(uri.getLastPathSegment());
        } else {
            deleted = 0;
        }

        if (getContext() != null)
            getContext().getContentResolver().notifyChange(FavDatabaseContract.TableColumns.CONTENT_URI_TV, tvDataObserver);
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
