package ga.softogi.themoviecatalogue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.fragment.MovieFragment;
import ga.softogi.themoviecatalogue.fragment.TvFragment;

public class MainActivity extends AppCompatActivity {

    private Fragment movieFragment = new MovieFragment();
    private Fragment tvFragment = new TvFragment();
    private Fragment activeFragment = movieFragment;

    private String movieTag = MovieFragment.class.getSimpleName();
    private String tvTag = TvFragment.class.getSimpleName();
    private String activeTag = "ActiveFragment";
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_movies:
                    getSupportFragmentManager().beginTransaction().hide(activeFragment).show(movieFragment).commit();
                    activeFragment = movieFragment;
                    break;
                case R.id.navigation_tv:
                    getSupportFragmentManager().beginTransaction().hide(activeFragment).show(tvFragment).commit();
                    if (null == find(tvTag)) {
                        getSupportFragmentManager().beginTransaction().add(R.id.container_layout, tvFragment, tvTag).commit();
                    }
                    activeFragment = tvFragment;
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

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container_layout, movieFragment, movieTag).commit();
        } else {
            activeFragment = getSupportFragmentManager().getFragment(savedInstanceState, activeTag);
            getSupportFragmentManager().beginTransaction().attach(activeFragment).commit();
            movieFragment = getSupportFragmentManager().getFragment(savedInstanceState, movieTag);
            getSupportFragmentManager().beginTransaction().attach(movieFragment).commit();
            if (null != find(tvTag)) {
                tvFragment = getSupportFragmentManager().getFragment(savedInstanceState, tvTag);
                getSupportFragmentManager().beginTransaction().attach(tvFragment).commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getSupportFragmentManager().putFragment(outState, activeTag, activeFragment);
        getSupportFragmentManager().putFragment(outState, movieTag, movieFragment);
        if (null != find(tvTag)) {
            getSupportFragmentManager().putFragment(outState, tvTag, tvFragment);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_language_setting:
                Intent mIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(mIntent);
                break;
            case R.id.favorite:
                Intent favoriteIntent = new Intent(MainActivity.this, FavActivity.class);
                startActivity(favoriteIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
