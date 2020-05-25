package mfahmialkautsar.thefavoritemovie.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import mfahmialkautsar.thefavoritemovie.R;
import mfahmialkautsar.thefavoritemovie.entity.ContentItem;

import static android.provider.BaseColumns._ID;
import static mfahmialkautsar.thefavoritemovie.db.DatabaseContract.TableColumns.BACKDROP_PATH;
import static mfahmialkautsar.thefavoritemovie.db.DatabaseContract.TableColumns.CONTENT_URI_MOVIE;
import static mfahmialkautsar.thefavoritemovie.db.DatabaseContract.TableColumns.CONTENT_URI_TV;
import static mfahmialkautsar.thefavoritemovie.db.DatabaseContract.TableColumns.GENRE;
import static mfahmialkautsar.thefavoritemovie.db.DatabaseContract.TableColumns.OVERVIEW;
import static mfahmialkautsar.thefavoritemovie.db.DatabaseContract.TableColumns.POSTER_PATH;
import static mfahmialkautsar.thefavoritemovie.db.DatabaseContract.TableColumns.RATING;
import static mfahmialkautsar.thefavoritemovie.db.DatabaseContract.TableColumns.RELEASE;
import static mfahmialkautsar.thefavoritemovie.db.DatabaseContract.TableColumns.RUNTIME;
import static mfahmialkautsar.thefavoritemovie.db.DatabaseContract.TableColumns.TITLE;
import static mfahmialkautsar.thefavoritemovie.db.DatabaseContract.TableColumns.TYPE;
import static mfahmialkautsar.thefavoritemovie.entity.ContentItem.TYPE_MOVIE;
import static mfahmialkautsar.thefavoritemovie.entity.ContentItem.TYPE_TV;

public class DetailContentActivity extends AppCompatActivity {
    public static final int NO_INTERNET = R.string.no_internet;
    private static final String EXTRA_ID = "extra_id";
    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_OVERVIEW = "extra_overview";
    private static final String EXTRA_RELEASE = "extra_release";
    private static final String EXTRA_RATING = "extra_rating";
    private static final String EXTRA_RUNTIME = "extra_runtime";
    private static final String EXTRA_GENRE = "extra_genre";
    private static final String EXTRA_TYPE = "extra_type";
    private static final String EXTRA_POSTER = "extra_poster";
    private static final String EXTRA_BACKDROP = "extra_backdrop";
    private int id;
    private String title;
    private String overview;
    private String release;
    private double rating;
    private String runtime;
    private String genre;
    private String type;
    private String poster_path;
    private String backdrop_path;
    private MaterialFavoriteButton favoriteButton;
    private ProgressBar progressBar;

    private ContentItem content;
    private MaterialFavoriteButton.OnFavoriteChangeListener favoritedFailed = new MaterialFavoriteButton.OnFavoriteChangeListener() {
        @Override
        public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
            showSnackbarMessage(getString(R.string.favorite_failed));
            favoriteButton.setFavorite(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_content);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        ImageView ivPoster = findViewById(R.id.iv_poster);
        ImageView ivBackdrop = findViewById(R.id.iv_backdrop);

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvOverview = findViewById(R.id.tv_overview);
        TextView tvRelease = findViewById(R.id.tv_release);
        TextView tvRating = findViewById(R.id.tv_rating);
        TextView tvRuntime = findViewById(R.id.tv_runtime);
        TextView tvGenre = findViewById(R.id.tv_genre);
        TextView isReleased = findViewById(R.id.release);

        ImageView ivStar1 = findViewById(R.id.iv_star1);
        ImageView ivStar2 = findViewById(R.id.iv_star2);
        ImageView ivStar3 = findViewById(R.id.iv_star3);
        ImageView ivStar4 = findViewById(R.id.iv_star4);
        ImageView ivStar5 = findViewById(R.id.iv_star5);

        List<ImageView> ivStar = new ArrayList<>();
        ivStar.add(ivStar1);
        ivStar.add(ivStar2);
        ivStar.add(ivStar3);
        ivStar.add(ivStar4);
        ivStar.add(ivStar5);

        Uri uri = getIntent().getData();

        if (uri != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) content = new ContentItem(cursor);
                cursor.close();
            }
        }
        if (savedInstanceState == null) {
            id = content.getId();
            title = content.getTitle();
            overview = content.getOverview();
            release = content.getRelease();
            rating = content.getRating();
            runtime = content.getRuntime();
            genre = content.getGenre();
            type = content.getType();
            poster_path = content.getPosterPath();
            backdrop_path = content.getBackdropPath();
        } else {
            id = savedInstanceState.getInt(EXTRA_ID);
            title = savedInstanceState.getString(EXTRA_TITLE);
            overview = savedInstanceState.getString(EXTRA_OVERVIEW);
            release = savedInstanceState.getString(EXTRA_RELEASE);
            rating = savedInstanceState.getDouble(EXTRA_RATING);
            runtime = savedInstanceState.getString(EXTRA_RUNTIME);
            genre = savedInstanceState.getString(EXTRA_GENRE);
            type = savedInstanceState.getString(EXTRA_TYPE);
            poster_path = savedInstanceState.getString(EXTRA_POSTER);
            backdrop_path = savedInstanceState.getString(EXTRA_BACKDROP);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String topType = (String) getSupportActionBar().getTitle();
            if (Objects.equals(type, TYPE_MOVIE)) {
                topType = "Movie";
            } else if (Objects.equals(type, TYPE_TV)) {
                topType = "TV Show";
            }
            getSupportActionBar().setTitle(topType);
        }

        tvTitle.setText(title);
        tvOverview.setText(overview);

        String theOverview;
        if (TextUtils.isEmpty(overview)) {
            theOverview = getString(R.string.overview_unknown);
        } else {
            theOverview = overview;
        }
        tvOverview.setText(theOverview);

        NumberFormat numberFormat = new DecimalFormat("#.0");
        String theRating;
        if (Objects.equals(rating, 0.0)) {
            theRating = getString(R.string.no_rating);
        } else {
            theRating = numberFormat.format(rating);
        }

        int integerRating = (int) rating / 2;
        for (int i = 0; i < integerRating; i++) {
            ivStar.get(i).setImageResource(R.drawable.ic_star_full_24dp);
        }
        if (Math.round(rating) > integerRating) {
            ivStar.get(integerRating).setImageResource(R.drawable.ic_star_half_24dp);
        }

        tvOverview.setText(theOverview);
        tvRating.setText(String.format(" %s", theRating));

        tvRuntime.setText(runtime);
        tvGenre.setText(genre);
        tvGenre.setMaxLines(2);
        tvGenre.setEllipsize(TextUtils.TruncateAt.END);

        if (tvOverview.getText().toString().equals("Overview unknown") || tvOverview.getText().toString().equals("Sinopsis tidak diketahui")) {
            tvOverview.setText(getString(R.string.overview_unknown));
        }

        if (tvRuntime.getText().toString().equals("Runtime Unknown") || tvRuntime.getText().toString().equals("Durasi tidak diketahui")) {
            tvRuntime.setText(getString(R.string.runtime_unknown));
        }

        if (tvGenre.getText().toString().equals("Genre Unknown") || tvGenre.getText().toString().equals("Genre tidak diketahui")) {
            tvGenre.setText(getString(R.string.genre_unknown));
        }

        if (tvRelease.getText().toString().equals("Release date Unknown") || tvRelease.getText().toString().equals("Tanggal rilis tidak diketahui")) {
            tvRelease.setText(getString(R.string.release_date_unknown));
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date today = new Date();
        String theDate = getString(R.string.date);

        try {
            Date date = dateFormat.parse(release);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat newDateFormat = new SimpleDateFormat(theDate);
            String releaseYear;
            if (date != null) {
                releaseYear = newDateFormat.format(date);
                tvRelease.setText(releaseYear);
            } else {
                tvRelease.setText(getString(R.string.release_date_unknown));
            }
            if (today.equals(date) || today.after(date)) {
                isReleased.setText(getString(R.string.has_released));
                if (type.equals(TYPE_TV)) {
                    isReleased.setText(getString(R.string.first_episode));
                }
            } else {
                isReleased.setText(getString(R.string.coming_soon));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Picasso.get()
                .load("https://image.tmdb.org/t/p/original/" + poster_path)
                .placeholder(this.getResources().getDrawable(R.drawable.ic_loading_24dp))
                .error(this.getResources().getDrawable(R.drawable.ic_error_outline_black_24dp))
                .into(ivPoster);
        Picasso.get()
                .load("https://image.tmdb.org/t/p/original/" + backdrop_path)
                .placeholder(this.getResources().getDrawable(R.drawable.ic_loading_24dp))
                .error(this.getResources().getDrawable(R.drawable.ic_error_outline_black_24dp))
                .into(ivBackdrop);

        progressBar = findViewById(R.id.progress_bar);
        showLoading(true);

        ContentValues values = new ContentValues();
        values.put(_ID, id);
        values.put(TITLE, title);
        values.put(OVERVIEW, overview);
        values.put(RELEASE, release);
        values.put(RATING, rating);
        values.put(RUNTIME, runtime);
        values.put(GENRE, genre);
        values.put(POSTER_PATH, poster_path);
        values.put(BACKDROP_PATH, backdrop_path);
        values.put(TYPE, type);

        favoriteButton = findViewById(R.id.favorite_button);
        if (type != null) {
            doFav(id, title, type, values);
        } else {
            favoriteButton.setOnFavoriteChangeListener(favoritedFailed);
        }

        if (ivPoster != null && ivBackdrop != null) {
            showLoading(false);
        }

        isNetworkAvailable();
    }

    private void doFav(final int id, final String title, final String type, final ContentValues values) {
        String[] projection = {
                _ID, TITLE, OVERVIEW, RELEASE, RATING, POSTER_PATH, BACKDROP_PATH, GENRE, RUNTIME, TYPE
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
                        showSnackbarMessage(title + getString(R.string.add_fav_success));
                    } else {
                        getContentResolver().delete(Objects.requireNonNull(getIntent().getData()), String.valueOf(id), null);
                        showSnackbarMessage(title + getString(R.string.del_fav_success));
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
                        showSnackbarMessage(title + getString(R.string.add_fav_success));
                    } else {
                        getContentResolver().delete(Objects.requireNonNull(getIntent().getData()), String.valueOf(id), null);
                        showSnackbarMessage(title + getString(R.string.del_fav_success));
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
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null) {
                Toast.makeText(this, NO_INTERNET, Toast.LENGTH_LONG).show();
            }
        }
        if (networkInfo != null) {
            networkInfo.isConnected();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_ID, id);
        outState.putString(EXTRA_TITLE, title);
        outState.putString(EXTRA_OVERVIEW, overview);
        outState.putString(EXTRA_RELEASE, release);
        outState.putDouble(EXTRA_RATING, rating);
        outState.putString(EXTRA_RUNTIME, runtime);
        outState.putString(EXTRA_GENRE, genre);
        outState.putString(EXTRA_TYPE, type);
        outState.putString(EXTRA_POSTER, poster_path);
        outState.putString(EXTRA_BACKDROP, backdrop_path);
    }
}
