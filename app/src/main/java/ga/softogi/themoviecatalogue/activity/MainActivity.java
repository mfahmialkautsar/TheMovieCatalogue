package ga.softogi.themoviecatalogue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.fragment.parent.FavoriteFragment;
import ga.softogi.themoviecatalogue.fragment.parent.HomeFragment;
import ga.softogi.themoviecatalogue.preference.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private Fragment homeFragment = new HomeFragment();
    private Fragment favFragment = new FavoriteFragment();
    private Fragment activeFragment = homeFragment;

    private String homeTag = HomeFragment.class.getSimpleName();
    private String favTag = FavoriteFragment.class.getSimpleName();
    private String activeTag = "ActiveFragment";
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_movies:
                    getSupportFragmentManager().beginTransaction().hide(activeFragment).show(homeFragment).commit();
                    activeFragment = homeFragment;
                    break;
                case R.id.navigation_tv:
                    getSupportFragmentManager().beginTransaction().hide(activeFragment).show(favFragment).commit();
                    if (null == find(favTag)) {
                        getSupportFragmentManager().beginTransaction().add(R.id.container_layout, favFragment, favTag).commit();
                    }
                    activeFragment = favFragment;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container_layout, homeFragment, homeTag).commit();
        } else {
            activeFragment = getSupportFragmentManager().getFragment(savedInstanceState, activeTag);
            getSupportFragmentManager().beginTransaction().attach(activeFragment).commit();
            homeFragment = getSupportFragmentManager().getFragment(savedInstanceState, homeTag);
            getSupportFragmentManager().beginTransaction().attach(homeFragment).commit();
            if (null != find(favTag)) {
                favFragment = getSupportFragmentManager().getFragment(savedInstanceState, favTag);
                getSupportFragmentManager().beginTransaction().attach(favFragment).commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getSupportFragmentManager().putFragment(outState, activeTag, activeFragment);
        getSupportFragmentManager().putFragment(outState, homeTag, homeFragment);
        if (null != find(favTag)) {
            getSupportFragmentManager().putFragment(outState, favTag, favFragment);
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
        if (item.getItemId() == R.id.action_settings) {
            Intent mIntent = new Intent(this, SettingsActivity.class);
            startActivity(mIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
