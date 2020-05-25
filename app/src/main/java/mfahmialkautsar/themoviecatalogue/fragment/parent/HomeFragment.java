package mfahmialkautsar.themoviecatalogue.fragment.parent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import mfahmialkautsar.themoviecatalogue.R;
import mfahmialkautsar.themoviecatalogue.fragment.child.HomeMovieFragment;
import mfahmialkautsar.themoviecatalogue.fragment.child.HomeTvFragment;

public class HomeFragment extends Fragment {
    private Fragment movieFragment = new HomeMovieFragment();
    private Fragment tvFragment = new HomeTvFragment();
    private Fragment activeFragment = movieFragment;

    private String movieTag = HomeMovieFragment.class.getSimpleName();
    private String tvTag = HomeTvFragment.class.getSimpleName();
    private String activeTag = "ActiveTab";
    private int position;

    public HomeFragment() {
    }

    private Fragment find(String fTag) {
        return getChildFragmentManager().findFragmentByTag(fTag);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getChildFragmentManager().beginTransaction().hide(activeFragment).show(movieFragment).commit();
                        activeFragment = movieFragment;
                        break;
                    case 1:
                        getChildFragmentManager().beginTransaction().hide(activeFragment).show(tvFragment).commit();
                        if (null == find(tvTag)) {
                            getChildFragmentManager().beginTransaction().add(R.id.view_frame, tvFragment, tvTag).commit();
                        }
                        activeFragment = tvFragment;
                        break;
                }
                position = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction().add(R.id.view_frame, movieFragment, movieTag).commit();
        } else {
            activeFragment = getChildFragmentManager().getFragment(savedInstanceState, activeTag);
            getChildFragmentManager().beginTransaction().attach(activeFragment).commit();
            movieFragment = getChildFragmentManager().getFragment(savedInstanceState, movieTag);
            getChildFragmentManager().beginTransaction().attach(movieFragment).commit();
            if (null != find(tvTag)) {
                tvFragment = getChildFragmentManager().getFragment(savedInstanceState, tvTag);
                getChildFragmentManager().beginTransaction().attach(tvFragment).commit();
            }
            if (movieFragment.isHidden()) {
                position = 1;
            } else if (tvFragment.isHidden()) {
                position = 0;
            }
            TabLayout.Tab tab = tabLayout.getTabAt(position);
            Objects.requireNonNull(tab).select();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        getChildFragmentManager().putFragment(outState, activeTag, activeFragment);
        getChildFragmentManager().putFragment(outState, movieTag, movieFragment);
        if (null != find(tvTag)) {
            getChildFragmentManager().putFragment(outState, tvTag, tvFragment);
        }
        super.onSaveInstanceState(outState);
    }
}