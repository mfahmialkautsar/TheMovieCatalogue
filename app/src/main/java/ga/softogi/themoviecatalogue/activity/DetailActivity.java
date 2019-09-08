package ga.softogi.themoviecatalogue.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ga.softogi.themoviecatalogue.BuildConfig;
import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.entity.Genre;
import ga.softogi.themoviecatalogue.entity.MovieData;
import ga.softogi.themoviecatalogue.entity.MovieDetail;
import ga.softogi.themoviecatalogue.entity.MovieTrailer;
import ga.softogi.themoviecatalogue.entity.MovieTrailerData;
import ga.softogi.themoviecatalogue.entity.TvData;
import ga.softogi.themoviecatalogue.entity.TvDetail;
import ga.softogi.themoviecatalogue.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.provider.BaseColumns._ID;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.BACKDROP_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.CONTENT_URI_MOVIE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.CONTENT_URI_TV;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.GENRE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.OVERVIEW;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.POSTER_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RATING;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RELEASE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RUNTIME;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TITLE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TYPE;
import static ga.softogi.themoviecatalogue.entity.MovieData.TYPE_MOVIE;
import static ga.softogi.themoviecatalogue.entity.TvData.TYPE_TV;

public class DetailActivity extends AppCompatActivity {
    private static final String STATE_RUNTIME = "state_runtime";
    private static final String STATE_GENRE = "state_genre";
    public static final String EXTRA_MOVIE = "extra_movie";
    public static final String EXTRA_TV = "extra_tv";
    public static final int NO_INTERNET = R.string.no_internet;
    private MaterialFavoriteButton favoriteButton;
    private ProgressBar progressBar;
    private List<ImageView> ivStar;
    private ImageView ivStar1;
    private ImageView ivStar2;
    private ImageView ivStar3;
    private ImageView ivStar4;
    private ImageView ivStar5;
    private ContentValues values;
    private int id;
    private ImageView ivPoster;
    private ImageView ivBackdrop;
    private String title;
    private String overview;
    private double rating;
    private int runtime;
    private List<Integer> episodeRunTIme;
    private String poster_path;
    private String backdrop_path;
    private String type;
    private String theOverview;
    private String release;
    private List<Genre> genreList;
    private MovieData movieDataUri;
    private TvData tvDataUri;
    private Cursor cursor;
    private WebView webView;
//    private Cursor cursorTv;

    private TextView tvTitle;
    private TextView tvOverview;
    private TextView tvRelease;
    private TextView tvRating;
    private TextView isReleased;
    private TextView tvVoteCount;
    private TextView tvGenre;
    private TextView tvRuntime;
    private MaterialFavoriteButton.OnFavoriteChangeListener favoritedFailed = new MaterialFavoriteButton.OnFavoriteChangeListener() {
        @Override
        public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
            showSnackbarMessage(getString(R.string.favorite_failed));
            favoriteButton.setFavorite(false);
        }
    };

    private ApiService apiService;

    private void loadMovieDetail(final int id) {
        Call<MovieDetail> movieDetailCall = apiService.getApiInterface().movieDetail(id);
        movieDetailCall.enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(@NonNull Call<MovieDetail> call, @NonNull Response<MovieDetail> response) {
                MovieDetail movieDetail = response.body();

                if (movieDetail != null) {
//                    int id = movieDetail.getId();
                    runtime = movieDetail.getRuntime();
                    genreList = movieDetail.getGenres();
//                    tvVoteCount.setText(String.valueOf(vote_count));
//                    setGenreList(genreList);
//                    setRuntime(runtime);
                    String theRuntime = runtime + getString(R.string.minutes);
//                    if (loadOnline) {
                    tvRuntime.setText(theRuntime);
                    for (int i = 0; i < genreList.size(); i++) {
                        Genre genre = genreList.get(i);

                        if (i < genreList.size() - 1) {
                            tvGenre.append(genre.getName() + ", ");
                        } else {
                            tvGenre.append(genre.getName());
                        }
                    }
//                    }

                    showLoading(false);
                }
                if (tvRuntime.getText().toString().matches("0" + getString(R.string.minutes))
                || tvRuntime.getText().toString().matches("" + getString(R.string.minutes))) {
                    tvRuntime.setText(getString(R.string.runtime_unknown));
                }
                if (tvGenre.getText().toString().matches("")) {
                    tvGenre.setText(getString(R.string.genre_unknown));
                }
                if (!tvRuntime.getText().toString().matches("") && !tvGenre.getText().toString().matches("")) {
                    if (type != null && id != 0) {
                        doFav(id);
                    } else {
                        favoriteButton.setOnFavoriteChangeListener(favoritedFailed);
                    }
                } else {
                    favoriteButton.setOnFavoriteChangeListener(favoritedFailed);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetail> call, @NonNull Throwable t) {
                favoriteButton.setOnFavoriteChangeListener(favoritedFailed);
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(DetailActivity.this, getString(R.string.request_timeout), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailActivity.this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                }

                showLoading(false);
            }
        });
    }

    private void loadTvDetail(final int id) {
        Call<TvDetail> tvDetailCall = apiService.getApiInterface().tvDetail(id);
        tvDetailCall.enqueue(new Callback<TvDetail>() {
            @Override
            public void onResponse(@NonNull Call<TvDetail> call, @NonNull Response<TvDetail> response) {
                TvDetail tvDetail = response.body();

                if (tvDetail != null) {
//                    int id = movieDetail.getId();
                    episodeRunTIme = tvDetail.getRuntime();
                    genreList = tvDetail.getGenres();
//                    tvVoteCount.setText(String.valueOf(vote_count));
//                    setGenreList(genreList);
//                    setRuntime(runtime);
                    String theRuntime = episodeRunTIme.toString().replace("[", "")
                            .replace(",", " -").replace("]", "") + getString(R.string.minutes);
//                    if (loadOnline) {
                    tvRuntime.setText(theRuntime);
                    if (tvRuntime.getText().toString().matches("0" + getString(R.string.minutes))
                            || tvRuntime.getText().toString().matches("" + getString(R.string.minutes))) {
                        tvRuntime.setText(getString(R.string.runtime_unknown));
                    }
                    for (int i = 0; i < genreList.size(); i++) {
                        Genre genre = genreList.get(i);

                        if (i < genreList.size() - 1) {
                            tvGenre.append(genre.getName() + ", ");
                        } else {
                            tvGenre.append(genre.getName());
                        }
                    }
//                    }

                    showLoading(false);
                }
                if (tvRuntime.getText().toString().matches("0" + getString(R.string.minutes))) {
                    tvRuntime.setText(getString(R.string.runtime_unknown));
                }
                if (tvGenre.getText().toString().matches("")) {
                    tvGenre.setText(getString(R.string.genre_unknown));
                }
                if (!tvRuntime.getText().toString().matches("") && !tvGenre.getText().toString().matches("")) {
                    if (type != null && id != 0) {
                        doFav(id);
                    } else {
                        favoriteButton.setOnFavoriteChangeListener(favoritedFailed);
                    }
                } else {
                    favoriteButton.setOnFavoriteChangeListener(favoritedFailed);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TvDetail> call, @NonNull Throwable t) {
                favoriteButton.setOnFavoriteChangeListener(favoritedFailed);
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(DetailActivity.this, getString(R.string.request_timeout), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailActivity.this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                }
                Log.e("ERRORR", t.getMessage());

                showLoading(false);
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_content);
        progressBar = findViewById(R.id.progress_bar);
        showLoading(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        ivPoster = findViewById(R.id.iv_poster);
        ivBackdrop = findViewById(R.id.iv_backdrop);

        tvTitle = findViewById(R.id.tv_title);
        tvOverview = findViewById(R.id.tv_overview);
        tvRelease = findViewById(R.id.tv_release);
        tvRating = findViewById(R.id.tv_rating);
//        tvVoteCount = findViewById(R.id.tv_vote_count);
        tvRuntime = findViewById(R.id.tv_runtime);
        tvGenre = findViewById(R.id.tv_genre);
        isReleased = findViewById(R.id.release);

        ivStar1 = findViewById(R.id.iv_star1);
        ivStar2 = findViewById(R.id.iv_star2);
        ivStar3 = findViewById(R.id.iv_star3);
        ivStar4 = findViewById(R.id.iv_star4);
        ivStar5 = findViewById(R.id.iv_star5);

        favoriteButton = findViewById(R.id.favorite_button);

        ivStar = new ArrayList<>();
        ivStar.add(ivStar1);
        ivStar.add(ivStar2);
        ivStar.add(ivStar3);
        ivStar.add(ivStar4);
        ivStar.add(ivStar5);

        apiService = new ApiService();

        Uri uri = getIntent().getData();

        if (uri != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()){
                    movieDataUri = new MovieData(cursor);
                    tvDataUri = new TvData(cursor);
                }
                cursor.close();
            }
        }


        MovieData movieData = getIntent().getParcelableExtra(EXTRA_MOVIE);
        TvData tvData = getIntent().getParcelableExtra(EXTRA_TV);

        String[] projection = {
                _ID, TITLE, OVERVIEW, RELEASE, GENRE, RUNTIME, RATING, POSTER_PATH, BACKDROP_PATH, TYPE
        };
        String selection = _ID + " =?";
        String[] selectionArgs = {String.valueOf(id)};
        if (uri != null) {
            cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
//            cursorTv = getContentResolver().query(uri, projection, selection, selectionArgs, null);
        }
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            id = movieDataUri.getId();
            title = movieDataUri.getTitle();
            overview = movieDataUri.getOverview();
            rating = movieDataUri.getVoteAverage();
            poster_path = movieDataUri.getPosterPath();
            backdrop_path = movieDataUri.getBackdropPath();
            release = movieDataUri.getReleaseDate();
            tvRuntime.setText(movieDataUri.getRuntime());
            tvGenre.setText(movieDataUri.getGenre());
            type = movieDataUri.getType();
            if (type != null && id != 0) {
                doFav(id);
            } else {
                favoriteButton.setOnFavoriteChangeListener(favoritedFailed);
            }
            showLoading(false);
        } else if (movieData != null) {
            id = movieData.getId();
            title = movieData.getTitle();
            overview = movieData.getOverview();
            release = movieData.getReleaseDate();
            rating = movieData.getVoteAverage();
            poster_path = movieData.getPosterPath();
            backdrop_path = movieData.getBackdropPath();
            type = movieData.getType();
//            runtime = movieData.getRuntime();
//            String genre = movieData.getGenre();
//            if (genreList != null) {
//                tvGenre.setText(genre);
//            } else {
//                loadOnline = true;
//            }
            if (savedInstanceState == null) {
//                if (null != movieDataUri.getRuntime()
//                        && null != movieDataUri.getGenre()) {
//                    tvRuntime.setText(movieDataUri.getRuntime());
//                    tvGenre.setText(movieDataUri.getGenre());
//                    showLoading(false);
//                } else {
                    loadMovieDetail(id);
//                }
            } else {
                tvRuntime.setText(savedInstanceState.getString(STATE_RUNTIME));
                tvGenre.setText(savedInstanceState.getString(STATE_GENRE));
                if (!tvRuntime.getText().toString().matches("") && !tvGenre.getText().toString().matches("")) {
                    if (type != null && id != 0) {
                        doFav(id);
                    } else {
                        favoriteButton.setOnFavoriteChangeListener(favoritedFailed);
                    }
                } else {
                    favoriteButton.setOnFavoriteChangeListener(favoritedFailed);
                }
                showLoading(false);
            }
        } else if (tvData != null) {
            id = tvData.getId();
            title = tvData.getName();
            overview = tvData.getOverview();
            release = tvData.getFirstAirDate();
            rating = tvData.getVoteAverage();
            poster_path = tvData.getPosterPath();
            backdrop_path = tvData.getBackdropPath();
            type = tvData.getType();
            if (savedInstanceState == null) {
                loadTvDetail(id);
            } else {
                tvRuntime.setText(savedInstanceState.getString(STATE_RUNTIME));
                tvGenre.setText(savedInstanceState.getString(STATE_GENRE));
                if (!tvRuntime.getText().toString().matches("") && !tvGenre.getText().toString().matches("")) {
                    if (type != null && id != 0) {
                        doFav(id);
                    } else {
                        favoriteButton.setOnFavoriteChangeListener(favoritedFailed);
                    }
                } else {
                    favoriteButton.setOnFavoriteChangeListener(favoritedFailed);
                }
                showLoading(false);
            }
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String topTitle = (String) getSupportActionBar().getTitle();
            if (Objects.equals(type, TYPE_MOVIE)) {
                topTitle = getString(R.string.movie);
            } else if (Objects.equals(type, TYPE_TV)) {
                topTitle = getString(R.string.tv_show);
            }
            getSupportActionBar().setTitle(topTitle);
        }

        if (TextUtils.isEmpty(overview)) {
            theOverview = getString(R.string.overview_not_found);
        } else {
            theOverview = overview;
        }
        tvOverview.setText(theOverview);
        tvTitle.setText(title);

        NumberFormat numberFormat = new DecimalFormat("#.0");
        String theRating;
        if (Objects.equals(rating, 0.0)) {
            theRating = getString(R.string.no_rating);
        } else {
            theRating = numberFormat.format(rating);
        }

        int integerRating = (int) rating/2;
        for (int i = 0; i < integerRating; i++) {
            ivStar.get(i).setImageResource(R.drawable.ic_star_full_24dp);
        }
        if (Math.round(rating) > integerRating) {
            ivStar.get(integerRating).setImageResource(R.drawable.ic_star_half_24dp);
        }

        tvRating.setText(String.format(" %s", theRating));
        tvGenre.setMaxLines(2);
        tvGenre.setEllipsize(TextUtils.TruncateAt.END);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date today = new Date();
        String theDate = getString(R.string.date);

        try {
            Date date = dateFormat.parse(release);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat newDateFormat = new SimpleDateFormat(theDate);
            String releaseYear = newDateFormat.format(date);
            tvRelease.setText(releaseYear);
            if (today.equals(date) || today.after(date)) {
                isReleased.setText(getString(R.string.has_released));
            } else {
                isReleased.setText(getString(R.string.coming_soon));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Picasso.get()
                .load(BuildConfig.IMG_URL + "original" + poster_path)
                .placeholder(getResources().getDrawable(R.drawable.ic_loading_24dp))
                .error(getResources().getDrawable(R.drawable.ic_error_outline_black_24dp))
                .into(ivPoster);
        Picasso.get()
                .load(BuildConfig.IMG_URL + "original" + backdrop_path)
                .placeholder(getResources().getDrawable(R.drawable.ic_loading_24dp))
                .error(getResources().getDrawable(R.drawable.ic_error_outline_black_24dp))
                .into(ivBackdrop);
/* kalo mao pake trailer
        webView = findViewById(R.id.vid_trailer);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        if (Objects.equals(type, TYPE_MOVIE)) {
            loadTrailer(id);
        } else if (Objects.equals(type, TYPE_TV)) {
            //hampir sama kayak loadTrailer buat movie
        }
 */
//        isNetworkAvailable();
    }
/* kalo mao pake trailer
    private void loadTrailer(int id) {
        Call<MovieTrailer> movieTrailerCall = apiService.getApiInterface().trailers(id);
        movieTrailerCall.enqueue(new Callback<MovieTrailer>() {
            @Override
            public void onResponse(@NonNull Call<MovieTrailer> call, @NonNull Response<MovieTrailer> response) {
                MovieTrailer movieTrailer = response.body();

                if (movieTrailer != null) {
                    showTrailers(movieTrailer.getResults());
                }
                showLoading(false);
            }

            @Override
            public void onFailure(@NonNull Call<MovieTrailer> call, @NonNull Throwable t) {
                favoriteButton.setOnFavoriteChangeListener(favoritedFailed);
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(DetailActivity.this, getString(R.string.request_timeout), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailActivity.this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                }
                Log.e("ERRORR", t.getMessage());

                showLoading(false);
            }
        });
    }

    private void showTrailers(ArrayList<MovieTrailerData> results) {
            MovieTrailerData movieTrailerData = results.get(0);
            Log.d("TRAILERRRR", movieTrailerData.getKey());
            webView.loadData("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + movieTrailerData.getKey() + "\" frameborder=\"0\" allowfullscreen></iframe>"
                    , "text/html", "utf-8");
    }
 */

    private void doFav(final int id) {
        values = new ContentValues();
        values.put(_ID, id);
        values.put(TITLE, title);
        values.put(OVERVIEW, overview);
        values.put(RELEASE, release);
        values.put(RATING, rating);
//                values.put(VOTE_COUNT, vote_count);
        values.put(POSTER_PATH, poster_path);
        values.put(BACKDROP_PATH, backdrop_path);
        values.put(RUNTIME, tvRuntime.getText().toString());
        values.put(GENRE, tvGenre.getText().toString());
        values.put(TYPE, type);
        String[] projection = {
                _ID, TITLE, OVERVIEW, RELEASE, GENRE, RUNTIME, RATING, POSTER_PATH, BACKDROP_PATH, TYPE
        };
        String selection = _ID + " =?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = getContentResolver().query(Objects.requireNonNull(getIntent().getData()), projection, selection, selectionArgs, null);
        if (Objects.requireNonNull(cursor).getCount() > 0) {
            favoriteButton.setFavorite(true);
            favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                    if (favorite) {
                        if (type.equals(TYPE_MOVIE)) {
                            getContentResolver().insert(CONTENT_URI_MOVIE, values);
                        } else if (type.equals(TYPE_TV)) {
                            getContentResolver().insert(CONTENT_URI_TV, values);
                        }
                        //                        favMovieHelper.beginTransaction();
                        //                        favMovieHelper.insertTransaction(content);
                        //                        favMovieHelper.setTransactionSuccess();
                        showSnackbarMessage(title + getString(R.string.add_fav_success));
                        //                        favMovieHelper.endTransaction();
                    } else {
                        getContentResolver().delete(Objects.requireNonNull(getIntent().getData()), String.valueOf(id), null);
                        //                        long result = favMovieHelper.deleteFromMovieFav(content.getId());
                        //                        if (result > 0) {
                        showSnackbarMessage(title + getString(R.string.del_fav_success));
                        //                        } else {
                        //                            showSnackbarMessage(title + getString(R.string.del_fav_failed));
                        //                        }
                    }
                }
            });
        } else {
            favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                    if (favorite) {
                        if (type.equals(TYPE_MOVIE)) {
                            getContentResolver().insert(CONTENT_URI_MOVIE, values);
                        } else if (type.equals(TYPE_TV)) {
                            getContentResolver().insert(CONTENT_URI_TV, values);
                        }
                        //                        favMovieHelper.beginTransaction();
                        //                        favMovieHelper.insertTransaction(content);
                        //                        favMovieHelper.setTransactionSuccess();
                        showSnackbarMessage(title + getString(R.string.add_fav_success));
                        //                        favMovieHelper.endTransaction();
                    } else {
                        getContentResolver().delete(Objects.requireNonNull(getIntent().getData()), String.valueOf(id), null);
                        //                        long result = favMovieHelper.deleteFromMovieFav(content.getId());
                        //                        if (result > 0) {
                        showSnackbarMessage(title + getString(R.string.del_fav_success));
                        //                        } else {
                        //                            showSnackbarMessage(title + getString(R.string.del_fav_failed));
                        //                        }
                    }
                }
            });
        }
        cursor.close();
    }

    private void showSnackbarMessage(String message) {
        Snackbar.make(findViewById(R.id.detail_layout), message, Snackbar.LENGTH_SHORT).show();
    }

    private void showLoading(boolean state) {
        if (state) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(this, NO_INTERNET, Toast.LENGTH_LONG).show();
        }
        if (networkInfo != null) {
            networkInfo.isConnected();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("state", outState);
        outState.putString(STATE_RUNTIME, tvRuntime.getText().toString());
        outState.putString(STATE_GENRE, tvGenre.getText().toString());
    }
}
