package ga.softogi.themoviecatalogue.entity;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import static android.provider.BaseColumns._ID;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.BACKDROP_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.OVERVIEW;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.POSTER_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RATING;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RELEASE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TITLE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TYPE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.getColumnInt;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.getColumnString;

public class ContentItem implements Parcelable {
    public static final String STATE_CONTENT = "state_content";
    public static final String TYPE_MOVIE = "type_movie";
    public static final String TYPE_TV = "type_tv";

    private int id;
    private String title;
    private String overview;
    private String release;
    private String rating;
    private String posterPath;
    private String backdropPath;
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.overview);
        dest.writeString(this.release);
        dest.writeString(this.rating);
        dest.writeString(this.posterPath);
        dest.writeString(this.backdropPath);
        dest.writeString(this.type);
    }

    public ContentItem() {
    }

    public ContentItem(int id, String title, String overview, String release, String rating, String posterPath, String backdropPath, String type) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.release = release;
        this.rating = rating;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.type = type;
    }

    public ContentItem(Cursor cursor) {
        this.id = getColumnInt(cursor, _ID);
        this.title = getColumnString(cursor, TITLE);
        this.overview = getColumnString(cursor, OVERVIEW);
        this.release = getColumnString(cursor, RELEASE);
        this.rating = getColumnString(cursor, RATING);
        this.posterPath = getColumnString(cursor, POSTER_PATH);
        this.backdropPath = getColumnString(cursor, BACKDROP_PATH);
        this.type = getColumnString(cursor, TYPE);
//        if (getColumnString(cursor, TYPE_MOVIE) != null) {
//            this.type = getColumnString(cursor, TYPE_MOVIE);
//        } else if (getColumnString(cursor, TYPE_TV) != null) {
//            this.type = getColumnString(cursor, TYPE_TV);
//        }
    }

    protected ContentItem(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.overview = in.readString();
        this.release = in.readString();
        this.rating = in.readString();
        this.posterPath = in.readString();
        this.backdropPath = in.readString();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<ContentItem> CREATOR = new Parcelable.Creator<ContentItem>() {
        @Override
        public ContentItem createFromParcel(Parcel source) {
            return new ContentItem(source);
        }

        @Override
        public ContentItem[] newArray(int size) {
            return new ContentItem[size];
        }
    };
}
