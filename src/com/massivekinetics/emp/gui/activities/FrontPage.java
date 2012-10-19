package com.massivekinetics.emp.gui.activities;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.massivekinetics.emp.R;
import com.massivekinetics.emp.adapters.TabsAdapter;
import com.massivekinetics.emp.gui.fragments.AlbumsFragment;
import com.massivekinetics.emp.gui.fragments.ArtistsFragment;
import com.massivekinetics.emp.gui.fragments.PlaylistsFragment;
import com.massivekinetics.emp.gui.fragments.SongsFragment;

public class FrontPage extends SherlockFragmentActivity {
	ActionBar actionBar;
	ViewPager viewPager;
	TabsAdapter tabsAdapter;
	ActionBar.TabListener tabListener;

	@Override
	protected void onCreate(Bundle savedState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedState);
		setContentView(R.layout.fragment_tabs_pager);
		initViews();
	}

	private void initViews() {
		viewPager = (ViewPager) findViewById(R.id.pager);
		List<Fragment> fragmentList = new ArrayList<Fragment>();
		fragmentList.add(Fragment.instantiate(this, PlaylistsFragment.class.getName()));
		fragmentList.add(Fragment.instantiate(this, SongsFragment.class.getName()));
		fragmentList.add(Fragment.instantiate(this, AlbumsFragment.class.getName()));
		fragmentList.add(Fragment.instantiate(this, ArtistsFragment.class.getName()));

		tabsAdapter = new TabsAdapter(getSupportFragmentManager(), fragmentList);
		viewPager.setAdapter(tabsAdapter);

		try {
			initActionBar();
		} catch (Exception e) {
		}
		viewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

	}

	private void initActionBar() {
		tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				int position = tab.getPosition();
				viewPager.setCurrentItem(position);
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}
		};

		actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.addTab(actionBar.newTab().setText("Playlists")
				.setTabListener(tabListener));

		actionBar.addTab(actionBar.newTab().setText("Songs")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Albums")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Artists")
				.setTabListener(tabListener));
	}

}
