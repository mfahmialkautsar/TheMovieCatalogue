package ga.softogi.themoviecatalogue.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.fragment.FavMovieFragment;
import ga.softogi.themoviecatalogue.fragment.FavTvFragment;
import ga.softogi.themoviecatalogue.fragment.MovieFragment;
import ga.softogi.themoviecatalogue.fragment.TvFragment;

public class FavActivity extends AppCompatActivity {

    private Fragment favMovieFragment = new FavMovieFragment();
    private Fragment favTvFragment = new FavTvFragment();
    private Fragment favActiveFragment = favMovieFragment;

    private String favMovieTag = MovieFragment.class.getSimpleName();
    private String favTvTag = TvFragment.class.getSimpleName();
    private String favActiveTag = "FavoriteActiveFragment";
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_movies:
                    getSupportFragmentManager().beginTransaction().hide(favActiveFragment).show(favMovieFragment).commit();
                    favActiveFragment = favMovieFragment;
                    break;
                case R.id.navigation_tv:
                    getSupportFragmentManager().beginTransaction().hide(favActiveFragment).show(favTvFragment).commit();
                    if (null == find(favTvTag)) {
                        getSupportFragmentManager().beginTransaction().add(R.id.container_layout, favTvFragment, favTvTag).commit();
                    }
                    favActiveFragment = favTvFragment;
                    break;
            }
            return true;
        }
    };

    private Fragment find(String fTag) {
        return getSupportFragmentManager().findFragmentByTag(fTag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.favorite));
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container_layout, favMovieFragment, favMovieTag).commit();
        } else {
            favActiveFragment = getSupportFragmentManager().getFragment(savedInstanceState, favActiveTag);
            getSupportFragmentManager().beginTransaction().attach(favActiveFragment).commit();
            favMovieFragment = getSupportFragmentManager().getFragment(savedInstanceState, favMovieTag);
            getSupportFragmentManager().beginTransaction().attach(favMovieFragment).commit();
            if (null != find(favTvTag)) {
                favTvFragment = getSupportFragmentManager().getFragment(savedInstanceState, favTvTag);
                getSupportFragmentManager().beginTransaction().attach(favTvFragment).commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getSupportFragmentManager().putFragment(outState, favActiveTag, favActiveFragment);
        getSupportFragmentManager().putFragment(outState, favMovieTag, favMovieFragment);
        if (null != find(favTvTag)) {
            getSupportFragmentManager().putFragment(outState, favTvTag, favTvFragment);
        }
        super.onSaveInstanceState(outState);
    }
}

