package ga.softogi.themoviecatalogue.fragment.root;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.fragment.MovieFragment;
import ga.softogi.themoviecatalogue.fragment.TvFragment;

public class HomeFragment extends Fragment {
    private Fragment movieFragment = new MovieFragment();
    private Fragment tvFragment = new TvFragment();
    private Fragment activeFragment = movieFragment;

    private String movieTag = MovieFragment.class.getSimpleName();
    private String tvTag = TvFragment.class.getSimpleName();
    private String activeTag = "ActiveTab";

    private TabLayout tabLayout;
    private TabLayout.Tab tab;
    private int position;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //        String movieTitle = editSearch.getText().toString();
//        bundle.putString("extra_search", movieTitle);
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tabs);
//        adapter = new SectionsPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());

//        adapter.populateFragment(movieFragment);
//        adapter.populateFragment(tvFragment);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                HomeFragment.this.tab = tab;
                switch (tab.getPosition()) {
                    case 0:
                        getChildFragmentManager().beginTransaction().hide(activeFragment).show(movieFragment).commit();
                        activeFragment = movieFragment;
                        break;
                    case 1:
                        getChildFragmentManager().beginTransaction().hide(activeFragment).show(tvFragment).commit();
                        if (null == getChildFragmentManager().findFragmentByTag(tvTag)) {
                            getChildFragmentManager().beginTransaction().add(R.id.view_frame, tvFragment, tvTag).commit();
                        }
                        activeFragment = tvFragment;
                        break;
                }
                position = tab.getPosition();
//                Bundle bundle = new Bundle();
//                bundle.putInt("tabpos", position);
//                Intent intent = new Intent(getContext(), HomeFragment.class);
//                intent.putExtras(bundle);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        setViewPager(viewPager);
//        tabLayout.setupWithViewPager(viewPager);
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction().add(R.id.view_frame, movieFragment, movieTag).commit();
        } else {
//            tab = tabLayout.getTabAt(position);
//            Objects.requireNonNull(tab).select();
            activeFragment = getChildFragmentManager().getFragment(savedInstanceState, activeTag);
            getChildFragmentManager().beginTransaction().attach(activeFragment).commit();
            movieFragment = getChildFragmentManager().getFragment(savedInstanceState, movieTag);
            getChildFragmentManager().beginTransaction().attach(movieFragment).commit();
            if (null != getChildFragmentManager().findFragmentByTag(tvTag)) {
                tvFragment = getChildFragmentManager().getFragment(savedInstanceState, tvTag);
                getChildFragmentManager().beginTransaction().attach(tvFragment).commit();
            }
            if (movieFragment.isHidden()) {
                position = 1;
            } else if (tvFragment.isHidden()) {
                position = 0;
            }
            tab = tabLayout.getTabAt(position);
            Objects.requireNonNull(tab).select();
        }
    }

//    private void setViewPager(ViewPager viewPager) {
//        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getContext(), getChildFragmentManager());
//        adapter.populateFragment(new MovieFragment(), getString(R.string.title_movies));
//        adapter.populateFragment(new FavoriteFragment(), getString(R.string.title_tv));
//        viewPager.setAdapter(adapter);
//    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
//        position = outState.getInt("tabpos");
        getChildFragmentManager().putFragment(outState, activeTag, activeFragment);
        getChildFragmentManager().putFragment(outState, movieTag, movieFragment);
        if (null != getChildFragmentManager().findFragmentByTag(tvTag)) {
            getChildFragmentManager().putFragment(outState, tvTag, tvFragment);
        }
        super.onSaveInstanceState(outState);
    }
}