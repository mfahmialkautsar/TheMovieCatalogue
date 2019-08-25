package ga.softogi.themoviecatalogue.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.db.FavMovieHelper;
import ga.softogi.themoviecatalogue.db.FavTvHelper;
import ga.softogi.themoviecatalogue.entity.ContentItem;

public class DetailContentActivity extends AppCompatActivity {
    public static final String EXTRA_CONTENT = "extra_movie";
    public static final int NO_INTERNET = R.string.no_internet;
    private MaterialFavoriteButton favoriteButton;
    private ProgressBar progressBar;

    private ContentItem content;
    private FavMovieHelper favMovieHelper;
    private FavTvHelper favTvHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_content);

        ImageView ivPoster = findViewById(R.id.iv_poster);
        ImageView ivBackdrop = findViewById(R.id.iv_backdrop);

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvOverview = findViewById(R.id.tv_overview);
        TextView tvRelease = findViewById(R.id.tv_release);
        TextView tvRating = findViewById(R.id.tv_rating);

        content = getIntent().getParcelableExtra(EXTRA_CONTENT);

        String title = content.getTitle();
        String overview = content.getOverview();
        String rating = content.getRating();
        String type = content.getType();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(type);
        }

        tvTitle.setText(title);
        tvOverview.setText(overview);

        String theOverview;
        if (TextUtils.isEmpty(overview)) {
            theOverview = getString(R.string.overview_not_found);
        } else {
            theOverview = overview;
        }
        tvOverview.setText(theOverview);

        String theRating;
        if (Objects.equals(rating, "0")) {
            theRating = getString(R.string.no_rating);
        } else {
            theRating = rating;
        }
        tvOverview.setText(theOverview);
        tvRating.setText(String.format(" %s", theRating));

        String release = content.getRelease();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String theDate = getString(R.string.date);

        try {
            Date date = dateFormat.parse(release);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat newDateFormat = new SimpleDateFormat(theDate);
            String releaseYear = newDateFormat.format(date);
            tvRelease.setText(releaseYear);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Picasso.get()
                .load("https://image.tmdb.org/t/p/original/" + content.getPosterPath())
                .placeholder(this.getResources().getDrawable(R.drawable.ic_loading_24dp))
                .error(this.getResources().getDrawable(R.drawable.ic_error_outline_black_24dp))
                .into(ivPoster);
        Picasso.get()
                .load("https://image.tmdb.org/t/p/original/" + content.getBackdropPath())
                .placeholder(this.getResources().getDrawable(R.drawable.ic_loading_24dp))
                .error(this.getResources().getDrawable(R.drawable.ic_error_outline_black_24dp))
                .into(ivBackdrop);

        progressBar = findViewById(R.id.progress_bar);
        showLoading(true);

        favoriteButton = findViewById(R.id.favorite);
        if (type != null) {
            if (type.equals(ContentItem.TYPE_MOVIE)) {
                favMovieHelper = FavMovieHelper.getInstance(getApplicationContext());
                favMovieHelper.openMovie();
                favMovie(title, favMovieHelper);
            } else if (type.equals(ContentItem.TYPE_TV)) {
                favTvHelper = FavTvHelper.getInstance(getApplicationContext());
                favTvHelper.openTv();
                favTv(title, favTvHelper);
            }
        }

        if (ivPoster != null && ivBackdrop != null && tvRelease != null) {
            showLoading(false);
        }

        isNetworkAvailable();
    }

    private void favMovie(final String title, final FavMovieHelper favMovieHelper) {
        if (favMovieHelper.isMovieExists(title)) {
            favoriteButton.setFavorite(true);
            favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                    if (favorite) {
                        favMovieHelper.beginTransaction();
                        favMovieHelper.insertTransaction(content);
                        favMovieHelper.setTransactionSuccess();
                        showSnackbarMessage(title + getString(R.string.add_fav_success));
                        favMovieHelper.endTransaction();
                    } else {
                        long result = favMovieHelper.deleteFromMovieFav(content.getId());
                        if (result > 0) {
                            showSnackbarMessage(title + getString(R.string.del_fav_success));
                        } else {
                            showSnackbarMessage(title + getString(R.string.del_fav_failed));
                        }
                    }
                }
            });
        } else {
            favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                    if (favorite) {
                        favMovieHelper.beginTransaction();
                        favMovieHelper.insertTransaction(content);
                        favMovieHelper.setTransactionSuccess();
                        showSnackbarMessage(title + getString(R.string.add_fav_success));
                        favMovieHelper.endTransaction();
                    } else {
                        long result = favMovieHelper.deleteFromMovieFav(content.getId());
                        if (result > 0) {
                            showSnackbarMessage(title + getString(R.string.del_fav_success));
                        } else {
                            showSnackbarMessage(title + getString(R.string.del_fav_failed));
                        }
                    }
                }
            });
        }
    }

    private void favTv(final String title, final FavTvHelper favTvHelper) {
        if (favTvHelper.isTvExists(title)) {
            favoriteButton.setFavorite(true);
            favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                    if (favorite) {
                        long result = favTvHelper.addToTvFav(content);
                        if (result > 0) {
                            content.setId((int) result);
                            showSnackbarMessage(title + getString(R.string.add_fav_success));
                        } else {
                            showSnackbarMessage(title + getString(R.string.add_fav_failed));
                        }
                    } else {
                        long result = favTvHelper.deleteFromTvFav(content.getId());
                        if (result > 0) {
                            showSnackbarMessage(title + getString(R.string.del_fav_success));
                        } else {
                            showSnackbarMessage(title + getString(R.string.del_fav_failed));
                        }
                    }
                }
            });
        } else {
            favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                    if (favorite) {
                        long result = favTvHelper.addToTvFav(content);
                        if (result > 0) {
                            content.setId((int) result);
                            showSnackbarMessage(title + getString(R.string.add_fav_success));
                        } else {
                            showSnackbarMessage(title + getString(R.string.add_fav_failed));
                        }
                    } else {
                        long result = favTvHelper.deleteFromTvFav(content.getId());
                        if (result > 0) {
                            showSnackbarMessage(title + getString(R.string.del_fav_success));
                        } else {
                            showSnackbarMessage(title + getString(R.string.del_fav_failed));
                        }
                    }
                }
            });
        }
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
    protected void onDestroy() {
        super.onDestroy();
        content = getIntent().getParcelableExtra(EXTRA_CONTENT);
        String type = content.getType();
        if (type != null) {
            if (type.equals(ContentItem.TYPE_MOVIE)) {
                favMovieHelper.closeMovie();
            } else if (type.equals(ContentItem.TYPE_TV)) {
                favTvHelper.closeTv();
            }
        }
    }
}
