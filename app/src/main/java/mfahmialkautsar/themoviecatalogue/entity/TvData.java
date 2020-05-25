package mfahmialkautsar.themoviecatalogue.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TvData implements Parcelable {
    public static final String TYPE_TV = "type_tv";
    public static final Parcelable.Creator<TvData> CREATOR = new Parcelable.Creator<TvData>() {
        @Override
        public TvData createFromParcel(Parcel source) {
            return new TvData(source);
        }

        @Override
        public TvData[] newArray(int size) {
            return new TvData[size];
        }
    };
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("overview")
    private String overview;
    @SerializedName("first_air_date")
    private String firstAirDate;
    @SerializedName("genre_ids")
    private List<Integer> genreIds;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("popularity")
    private double popularity;
    @SerializedName("vote_count")
    private int voteCount;
    @SerializedName("vote_average")
    private double voteAverage;
    private String type;
    private String runtime;
    private String genre;

    public TvData(int id, String name, String overview, String release, double rating, String poster, String backdrop, String runtime, String genre, String type) {
        this.id = id;
        this.name = name;
        this.overview = overview;
        this.firstAirDate = release;
        this.voteAverage = rating;
        this.posterPath = poster;
        this.backdropPath = backdrop;
        this.runtime = runtime;
        this.genre = genre;
        this.type = type;
    }

    private TvData(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.posterPath = in.readString();
        this.overview = in.readString();
        this.firstAirDate = in.readString();
        this.genreIds = new ArrayList<>();
        in.readList(this.genreIds, Integer.class.getClassLoader());
        this.backdropPath = in.readString();
        this.popularity = in.readDouble();
        this.voteCount = in.readInt();
        this.voteAverage = in.readDouble();
        this.type = in.readString();
        this.runtime = in.readString();
        this.genre = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getType() {
        this.type = TYPE_TV;
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.posterPath);
        dest.writeString(this.overview);
        dest.writeString(this.firstAirDate);
        dest.writeList(this.genreIds);
        dest.writeString(this.backdropPath);
        dest.writeDouble(this.popularity);
        dest.writeInt(this.voteCount);
        dest.writeDouble(this.voteAverage);
        dest.writeString(this.type);
        dest.writeString(this.runtime);
        dest.writeString(this.genre);
    }
}
