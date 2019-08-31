package ga.softogi.themoviecatalogue.db;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class FavDatabaseContract {

    public static final String TABLE_MOVIE = "MovieCatalogue";
    public static final String TABLE_TV = "TvCatalogue";
    public static final String AUTHORITY_MOVIE = "ga.softogi.themoviecatalogue.provider.FavMovieProvider";
    public static final String AUTHORITY_TV = "ga.softogi.themoviecatalogue.provider.FavTvProvider";
    private static final String SCHEME = "content";

    public static final class TableColumns implements BaseColumns {
        public static String TITLE = "title";
        public static String OVERVIEW = "overview";
        public static String RELEASE = "released";
//        public static String GENRE = "genre";
        public static String RATING = "rating";
        public static String VOTE_COUNT = "vote_count";
        public static String POSTER_PATH = "poster_path";
        public static String BACKDROP_PATH = "backdrop_path";
        public static String TYPE = "type";

        public static final Uri CONTENT_URI_MOVIE = new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY_MOVIE)
                .appendPath(TABLE_MOVIE)
                .build();

        public static final Uri CONTENT_URI_TV = new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY_TV)
                .appendPath(TABLE_TV)
                .build();
    }

    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public static int getColumnInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    public static double getColumnDouble(Cursor cursor, String columnName) {
        return cursor.getDouble(cursor.getColumnIndex(columnName));
    }
}
