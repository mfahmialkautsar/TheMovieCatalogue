package ga.softogi.themoviecatalogue.db;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import ga.softogi.themoviecatalogue.entity.ContentItem;

public class FavDatabaseContract {

    public static String TABLE_MOVIE = "MovieCatalogue";
    public static String TABLE_TV = "TvCatalogue";
    public static final String AUTHORITY = "ga.softogi.mynotesapp";
    private static final String SCHEME = "content";

    public static String TYPE_MOVIE = ContentItem.TYPE_MOVIE;
    public static String TYPE_TV = ContentItem.TYPE_TV;

    public static final class TableColumns implements BaseColumns {
        public static String TITLE = "title";
        static String OVERVIEW = "overview";
        static String RELEASE = "released";
        static String RATING = "rating";
        static String POSTER_PATH = "poster_path";
        public static String BACKDROP_PATH = "backdrop_path";

        public static final Uri CONTENT_URI = new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_MOVIE)
                .build();
    }

    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public static int getColumnInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }
}
