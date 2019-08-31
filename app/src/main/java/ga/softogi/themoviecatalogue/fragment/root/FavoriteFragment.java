package ga.softogi.themoviecatalogue.fragment.root;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.fragment.FavMovieFragment;
import ga.softogi.themoviecatalogue.fragment.FavTvFragment;

public class FavoriteFragment extends Fragment {
    private Fragment favMovieFragment = new FavMovieFragment();
    private Fragment favTvFragment = new FavTvFragment();
    private Fragment favActiveFragment = favMovieFragment;

    private String favMovieTag = FavMovieFragment.class.getSimpleName();
    private String favTvTag = FavTvFragment.class.getSimpleName();
    private String favActiveTag = "FavActiveTab";

    private TabLayout tabLayout;
    private TabLayout.Tab tab;
    private int position;

    public FavoriteFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getChildFragmentManager().beginTransaction().hide(favActiveFragment).show(favMovieFragment).commit();
                        favActiveFragment = favMovieFragment;
                        break;
                    case 1:
                        getChildFragmentManager().beginTransaction().hide(favActiveFragment).show(favTvFragment).commit();
                        if (null == getChildFragmentManager().findFragmentByTag(favTvTag)) {
                            getChildFragmentManager().beginTransaction().add(R.id.view_frame, favTvFragment, favTvTag).commit();
                        }
                        favActiveFragment = favTvFragment;
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
            getChildFragmentManager().beginTransaction().add(R.id.view_frame, favMovieFragment, favMovieTag).commit();
        } else {
            favActiveFragment = getChildFragmentManager().getFragment(savedInstanceState, favActiveTag);
            getChildFragmentManager().beginTransaction().attach(favActiveFragment).commit();
            favMovieFragment = getChildFragmentManager().getFragment(savedInstanceState, favMovieTag);
            getChildFragmentManager().beginTransaction().attach(favMovieFragment).commit();
            if (null != getChildFragmentManager().findFragmentByTag(favTvTag)) {
                favTvFragment = getChildFragmentManager().getFragment(savedInstanceState, favTvTag);
                getChildFragmentManager().beginTransaction().attach(favTvFragment).commit();
            }
            if (favMovieFragment.isHidden()) {
                position = 1;
            } else if (favTvFragment.isHidden()) {
                position = 0;
            }
            tab = tabLayout.getTabAt(position);
            Objects.requireNonNull(tab).select();
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        getChildFragmentManager().putFragment(outState, favActiveTag, favActiveFragment);
        getChildFragmentManager().putFragment(outState, favMovieTag, favMovieFragment);
        if (null != getChildFragmentManager().findFragmentByTag(favTvTag)) {
            getChildFragmentManager().putFragment(outState, favTvTag, favTvFragment);
        }
        super.onSaveInstanceState(outState);
    }
}