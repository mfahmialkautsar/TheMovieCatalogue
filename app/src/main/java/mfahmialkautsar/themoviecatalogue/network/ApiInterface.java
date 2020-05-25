package mfahmialkautsar.themoviecatalogue.network;

import mfahmialkautsar.themoviecatalogue.entity.Movie;
import mfahmialkautsar.themoviecatalogue.entity.MovieDetail;
import mfahmialkautsar.themoviecatalogue.entity.MovieTrailer;
import mfahmialkautsar.themoviecatalogue.entity.Tv;
import mfahmialkautsar.themoviecatalogue.entity.TvDetail;
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
