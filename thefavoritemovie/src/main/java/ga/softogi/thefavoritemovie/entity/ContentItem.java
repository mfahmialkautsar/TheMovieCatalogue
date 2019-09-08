package ga.softogi.thefavoritemovie.entity;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import static android.provider.BaseColumns._ID;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.BACKDROP_PATH;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.GENRE;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.OVERVIEW;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.POSTER_PATH;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.RATING;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.RELEASE;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.RUNTIME;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.TITLE;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.TYPE;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.getColumnDouble;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.getColumnInt;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.getColumnString;

public class ContentItem implements Parcelable {
    public static final String TYPE_MOVIE = "type_movie";
    public static final String TYPE_TV = "type_tv";
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
    private int id;
    private String title;
    private String overview;
    private String release;
    private double rating;
    private String posterPath;
    private String backdropPath;
    private String runtime;
    private String genre;
    private String type;

    public ContentItem(int id, String title, String overview, String release, double rating, String posterPath, String backdropPath, String runtime, String genre, String type) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.release = release;
        this.rating = rating;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.runtime = runtime;
        this.genre = genre;
        this.type = type;
    }

    public ContentItem(Cursor cursor) {
        this.id = getColumnInt(cursor, _ID);
        this.title = getColumnString(cursor, TITLE);
        this.overview = getColumnString(cursor, OVERVIEW);
        this.release = getColumnString(cursor, RELEASE);
        this.rating = getColumnDouble(cursor, RATING);
        this.posterPath = getColumnString(cursor, POSTER_PATH);
        this.backdropPath = getColumnString(cursor, BACKDROP_PATH);
        this.runtime = getColumnString(cursor, RUNTIME);
        this.genre = getColumnString(cursor, GENRE);
        this.type = getColumnString(cursor, TYPE);
    }

    private ContentItem(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.overview = in.readString();
        this.release = in.readString();
        this.rating = in.readDouble();
        this.posterPath = in.readString();
        this.backdropPath = in.readString();
        this.runtime = in.readString();
        this.genre = in.readString();
        this.type = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease() {
        return release;
    }

    public double getRating() {
        return rating;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getGenre() {
        return genre;
    }

    public String getType() {
        return type;
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
        dest.writeDouble(this.rating);
        dest.writeString(this.posterPath);
        dest.writeString(this.backdropPath);
        dest.writeString(this.runtime);
        dest.writeString(this.genre);
        dest.writeString(this.type);
    }
}
