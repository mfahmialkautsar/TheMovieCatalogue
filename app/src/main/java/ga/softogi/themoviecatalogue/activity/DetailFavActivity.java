//package ga.softogi.themoviecatalogue.activity;
//
//import android.annotation.SuppressLint;
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.design.widget.CollapsingToolbarLayout;
//import android.support.design.widget.Snackbar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.github.ivbaranov.mfb.MaterialFavoriteButton;
//import com.squareup.picasso.Picasso;
//
//import java.net.SocketTimeoutException;
//import java.text.DecimalFormat;
//import java.text.NumberFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Objects;
//
//import ga.softogi.themoviecatalogue.BuildConfig;
//import ga.softogi.themoviecatalogue.R;
//import ga.softogi.themoviecatalogue.entity.ContentItem;
//import ga.softogi.themoviecatalogue.entity.Genre;
//import ga.softogi.themoviecatalogue.entity.MovieData;
//import ga.softogi.themoviecatalogue.entity.MovieDetail;
//import ga.softogi.themoviecatalogue.entity.TvData;
//import ga.softogi.themoviecatalogue.entity.TvDetail;
//import ga.softogi.themoviecatalogue.network.ApiService;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//import static android.provider.BaseColumns._ID;
//import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.BACKDROP_PATH;
//import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.CONTENT_URI_MOVIE;
//import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.CONTENT_URI_TV;
//import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.GENRE;
//import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.OVERVIEW;
//import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.POSTER_PATH;
//import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RATING;
//import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RELEASE;
//import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RUNTIME;
//import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TITLE;
//import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TYPE;
//import static ga.softogi.themoviecatalogue.entity.ContentItem.TYPE_MOVIE;
//import static ga.softogi.themoviecatalogue.entity.ContentItem.TYPE_TV;
//
//public class DetailFavActivity extends AppCompatActivity {
//    public static final String EXTRA_MOVIE = "extra_movie";
//    public static final String EXTRA_TV = "extra_tv";
//    public static final int NO_INTERNET = R.string.no_internet;
//    private MaterialFavoriteButton favoriteButton;
//    private ProgressBar progressBar;
//    private List<ImageView> ivStar;
//    private ImageView ivStar1;
//    private ImageView ivStar2;
//    private ImageView ivStar3;
//    private ImageView ivStar4;
//    private ImageView ivStar5;
//    private ContentValues values;
//    private MovieData movieDataUri;
//    private TvData tvDataUri;
//    private int id;
//    private ImageView ivPoster;
//    private ImageView ivBackdrop;
//    private String title;
//    private String overview;
//    private double rating;
//    private String runtime;
//    private String poster_path;
//    private String backdrop_path;
//    private String type;
//    private String theOverview;
//    private String release;
//    private String genre;
//
//    private TextView tvTitle;
//    private TextView tvOverview;
//    private TextView tvRelease;
//    private TextView tvRating;
//    private TextView tvVoteCount;
//    private TextView tvGenre;
//    private TextView tvRuntime;
//    private CollapsingToolbarLayout collapsingToolbarLayout;
//    private MaterialFavoriteButton.OnFavoriteChangeListener favoritedFailed = new MaterialFavoriteButton.OnFavoriteChangeListener() {
//        @Override
//        public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite_button) {
//            showSnackbarMessage(getString(R.string.favorite_failed));
//        }
//    };
//
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail_content);
//        progressBar = findViewById(R.id.progress_bar);
//        showLoading(true);
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
//        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
//
//        ivPoster = findViewById(R.id.iv_poster);
//        ivBackdrop = findViewById(R.id.iv_backdrop);
//
//        tvTitle = findViewById(R.id.tv_title);
//        tvOverview = findViewById(R.id.tv_overview);
//        tvRelease = findViewById(R.id.tv_release);
//        tvRating = findViewById(R.id.tv_rating);
//        tvVoteCount = findViewById(R.id.tv_vote_count);
//        tvRuntime = findViewById(R.id.tv_runtime);
//        tvGenre = findViewById(R.id.tv_genre);
//
//        ivStar1 = findViewById(R.id.iv_star1);
//        ivStar2 = findViewById(R.id.iv_star2);
//        ivStar3 = findViewById(R.id.iv_star3);
//        ivStar4 = findViewById(R.id.iv_star4);
//        ivStar5 = findViewById(R.id.iv_star5);
//
//        favoriteButton = findViewById(R.id.favorite_button);
//
//        ivStar = new ArrayList<>();
//        ivStar.add(ivStar1);
//        ivStar.add(ivStar2);
//        ivStar.add(ivStar3);
//        ivStar.add(ivStar4);
//        ivStar.add(ivStar5);
//
//        Uri uri = getIntent().getData();
//
//        if (uri != null) {
//            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//
//            if (cursor != null) {
//                if (cursor.moveToFirst()){
//                    movieDataUri = new MovieData(cursor);
//                    tvDataUri = new TvData(cursor);
//                }
//                cursor.close();
//            }
//        }
//
//        if (movieDataUri != null) {
//            id = movieDataUri.getId();
//            title = movieDataUri.getTitle();
//            overview = movieDataUri.getOverview();
//            rating = movieDataUri.getVoteAverage();
//            poster_path = movieDataUri.getPosterPath();
//            backdrop_path = movieDataUri.getBackdropPath();
//            release = movieDataUri.getReleaseDate();
//            runtime = movieDataUri.getRuntime();
//            genre = movieDataUri.getGenre();
//            type = movieDataUri.getType();
//            showLoading(false);
//        } else if (tvDataUri != null) {
//            id = tvDataUri.getId();
//            title = tvDataUri.getName();
//            overview = tvDataUri.getOverview();
//            rating = tvDataUri.getVoteAverage();
//            poster_path = tvDataUri.getPosterPath();
//            backdrop_path = tvDataUri.getBackdropPath();
//            release = tvDataUri.getFirstAirDate();
//            runtime = tvDataUri.getRuntime();
//            genre = tvDataUri.getGenre();
//            type = tvDataUri.getType();
////            episodeRunTIme = tvDataUri.getRuntime();
////            if (savedInstanceState == null) {
////            }
////            else {
////                tvRuntime.setText(savedInstanceState.getString("extra_runtime"));
////                tvGenre.setText(savedInstanceState.getString("extra_genre"));
////                showLoading(false);
////            }
//            showLoading(false);
//        }
//
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            String topTitle = (String) getSupportActionBar().getTitle();
//            if (Objects.equals(type, TYPE_MOVIE)) {
//                topTitle = "Movie";
//            } else if (Objects.equals(type, TYPE_TV)) {
//                topTitle = "TV Show";
//            }
//            getSupportActionBar().setTitle(topTitle);
//        }
//
//        if (TextUtils.isEmpty(overview)) {
//            theOverview = getString(R.string.overview_not_found);
//        } else {
//            theOverview = overview;
//        }
//        tvOverview.setText(theOverview);
//        tvTitle.setText(title);
//
//        NumberFormat numberFormat = new DecimalFormat("#.0");
//        String theRating;
//        if (Objects.equals(rating, 0.0)) {
//            theRating = getString(R.string.no_rating);
//        } else {
//            theRating = numberFormat.format(rating/2) + "/5";
//        }
//
//        int integerRating = (int) rating/2;
//        for (int i = 0; i < integerRating; i++) {
//            ivStar.get(i).setImageResource(R.drawable.ic_star_full_24dp);
//        }
//        if (Math.round(rating) > integerRating) {
//            ivStar.get(integerRating).setImageResource(R.drawable.ic_star_half_24dp);
//        }
//
//        tvRating.setText(String.format(" %s", theRating));
//
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//
//        String theDate = getString(R.string.date);
//
//        try {
//            Date date = dateFormat.parse(release);
//            @SuppressLint("SimpleDateFormat") SimpleDateFormat newDateFormat = new SimpleDateFormat(theDate);
//            String releaseYear = newDateFormat.format(date);
//            tvRelease.setText(releaseYear);
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        tvRuntime.setText(runtime);
//        tvGenre.setText(genre);
//
//        Picasso.get()
//                .load(BuildConfig.IMG_URL + "original" + poster_path)
//                .placeholder(getResources().getDrawable(R.drawable.ic_loading_24dp))
//                .error(getResources().getDrawable(R.drawable.ic_error_outline_black_24dp))
//                .into(ivPoster);
//        Picasso.get()
//                .load(BuildConfig.IMG_URL + "original" + backdrop_path)
//                .placeholder(getResources().getDrawable(R.drawable.ic_loading_24dp))
//                .error(getResources().getDrawable(R.drawable.ic_error_outline_black_24dp))
//                .into(ivBackdrop);
//
//        if (type != null && id != 0) {
//            doFav(id);
//        } else {
//            favoriteButton.setOnFavoriteChangeListener(favoritedFailed);
//        }
//        showLoading(false);
////        isNetworkAvailable();
//    }
//
//    private void doFav(final int id) {
//        values = new ContentValues();
//        values.put(_ID, id);
//        values.put(TITLE, title);
//        values.put(OVERVIEW, overview);
//        values.put(RELEASE, release);
//        values.put(RATING, rating);
//        values.put(RUNTIME, runtime);
////                values.put(VOTE_COUNT, vote_count);
//        values.put(POSTER_PATH, poster_path);
//        values.put(BACKDROP_PATH, backdrop_path);
//        values.put(GENRE, tvGenre.getText().toString());
//        values.put(TYPE, type);
//        String[] projection = {
//                _ID, TITLE, OVERVIEW, RELEASE, RATING, RUNTIME, POSTER_PATH, BACKDROP_PATH, TYPE
//        };
//        String selection = _ID + " =?";
//        String[] selectionArgs = {String.valueOf(id)};
//        Cursor cursor = getContentResolver().query(Objects.requireNonNull(getIntent().getData()), projection, selection, selectionArgs, null);
//        if (Objects.requireNonNull(cursor).getCount() > 0) {
//            favoriteButton.setFavorite(true);
//            favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
//                @Override
//                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite_button) {
//                    if (favorite_button) {
//                        if (type.equals(TYPE_MOVIE)) {
//                            getContentResolver().insert(CONTENT_URI_MOVIE, values);
//                        } else if (type.equals(TYPE_TV)) {
//                            getContentResolver().insert(CONTENT_URI_TV, values);
//                        }
//                        showSnackbarMessage(title + getString(R.string.add_fav_success));
//                    } else {
//                        getContentResolver().delete(Objects.requireNonNull(getIntent().getData()), String.valueOf(id), null);
//                        showSnackbarMessage(title + getString(R.string.del_fav_success));
//                    }
//                }
//            });
//        } else {
//            favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
//                @Override
//                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite_button) {
//                    if (favorite_button) {
//                        if (type.equals(TYPE_MOVIE)) {
//                            getContentResolver().insert(CONTENT_URI_MOVIE, values);
//                        } else if (type.equals(TYPE_TV)) {
//                            getContentResolver().insert(CONTENT_URI_TV, values);
//                        }
//                        showSnackbarMessage(title + getString(R.string.add_fav_success));
//                    } else {
//                        getContentResolver().delete(Objects.requireNonNull(getIntent().getData()), String.valueOf(id), null);
//                        showSnackbarMessage(title + getString(R.string.del_fav_success));
//                    }
//                }
//            });
//        }
//        cursor.close();
//    }
//
//    private void showSnackbarMessage(String message) {
//        Snackbar.make(findViewById(R.id.detail_layout), message, Snackbar.LENGTH_SHORT).show();
//    }
//
//    private void showLoading(boolean state) {
//        if (state) {
//            progressBar.setVisibility(View.VISIBLE);
//        } else {
//            progressBar.setVisibility(View.GONE);
//        }
//    }
//
//    private void isNetworkAvailable() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//        if (networkInfo == null) {
//            Toast.makeText(this, NO_INTERNET, Toast.LENGTH_LONG).show();
//        }
//        if (networkInfo != null) {
//            networkInfo.isConnected();
//        }
//    }
//}
