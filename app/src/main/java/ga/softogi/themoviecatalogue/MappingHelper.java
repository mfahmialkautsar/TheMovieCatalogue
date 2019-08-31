package ga.softogi.themoviecatalogue;

import android.database.Cursor;

import java.util.ArrayList;

import ga.softogi.themoviecatalogue.entity.ContentItem;

import static android.provider.BaseColumns._ID;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.BACKDROP_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.OVERVIEW;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.POSTER_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RATING;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.VOTE_COUNT;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RELEASE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TITLE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TYPE;

public class MappingHelper {

    public static ArrayList<ContentItem> mapCursorToArrayList(Cursor cursor) {
        ArrayList<ContentItem> contentList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE));
            String overview = cursor.getString(cursor.getColumnIndexOrThrow(OVERVIEW));
            String release = cursor.getString(cursor.getColumnIndexOrThrow(RELEASE));
            double rating = cursor.getDouble(cursor.getColumnIndexOrThrow(RATING));
            int voteCount = cursor.getInt(cursor.getColumnIndexOrThrow(VOTE_COUNT));
            String poster = cursor.getString(cursor.getColumnIndexOrThrow(POSTER_PATH));
            String backdrop = cursor.getString(cursor.getColumnIndexOrThrow(BACKDROP_PATH));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(TYPE));
            contentList.add(new ContentItem(id, title, overview, release, rating, voteCount, poster, backdrop, type));
        }
        return contentList;
    }
}
