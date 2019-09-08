package ga.softogi.themoviecatalogue.network;

import ga.softogi.themoviecatalogue.entity.Movie;
import ga.softogi.themoviecatalogue.entity.MovieDetail;
import ga.softogi.themoviecatalogue.entity.MovieTrailer;
import ga.softogi.themoviecatalogue.entity.MovieTrailerData;
import ga.softogi.themoviecatalogue.entity.Tv;
import ga.softogi.themoviecatalogue.entity.TvDetail;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET(NetworkContract.VERSION + "/discover" + NetworkContract.MOVIE)
    Call<Movie> discoverMovies(
            @Query("page") int page);

    @GET(NetworkContract.MOVIE_PATH + "/{movie_id}")
    Call<MovieDetail> movieDetail(
            @Path("movie_id") int movieId);

    @GET(NetworkContract.VERSION + "/search" + NetworkContract.MOVIE)
    Call<Movie> searchMovie(
            @Query("page") int page,
            @Query("query") String keyword);

    @GET(NetworkContract.MOVIE_PATH + "/{movie_id}/" + "videos")
    Call<MovieTrailer> trailers(
            @Path("movie_id") int movieId);

    @GET(NetworkContract.VERSION + "/discover" + NetworkContract.TV)
    Call<Tv> discoverTvs(
            @Query("page") int page);

    @GET(NetworkContract.TV_PATH + "/{tv_id}")
    Call<TvDetail> tvDetail(
            @Path("tv_id") int tvId);

    @GET(NetworkContract.VERSION + "/search" + NetworkContract.TV)
    Call<Tv> searchTv(
            @Query("page") int page,
            @Query("query") String keyword);
}
