/*******************************************************************************
 * StatusActivity.java
 * 
 * Copyright (c) 2012 SeedBoxer Team.
 * 
 * This file is part of SeedBoxer.
 * 
 * SeedBoxer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SeedBoxer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SeedBoxer.  If not, see <http ://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.seedboxer.seedroid.activities;

import net.seedboxer.seedroid.R;
import net.seedboxer.seedroid.activities.adapters.TabsAdapter;
import net.seedboxer.seedroid.activities.fragments.DownloadsFragment;
import net.seedboxer.seedroid.activities.fragments.InQueueFragment;
import net.seedboxer.seedroid.activities.fragments.StatusFragment;
import net.seedboxer.seedroid.gcm.GCMManager;
import net.seedboxer.seedroid.utils.HelpUtils;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.gcm.GCMRegistrar;

/**
 * @author Jorge Davison (jdavisonc)
 *
 */
public class MainActivity extends FragmentActivity {
	
	
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
	TabsAdapter mAppSectionsPagerAdapter;

    ViewPager mViewPager;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        setContentView(R.layout.main_activity_view);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        
        mAppSectionsPagerAdapter = new TabsAdapter(this, getSupportFragmentManager(), mViewPager);
        mAppSectionsPagerAdapter.addTab(actionBar.newTab().setText(R.string.tab_title_inprogres), StatusFragment.class, null);
        mAppSectionsPagerAdapter.addTab(actionBar.newTab().setText(R.string.tab_title_queue), InQueueFragment.class, null);
        mAppSectionsPagerAdapter.addTab(actionBar.newTab().setText(R.string.tab_title_downloads), DownloadsFragment.class, null);
        
        if (savedInstanceState != null) {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
        
        GCMRegistrar.checkManifest(this);
        GCMRegistrar.checkDevice(this);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }
    
    /**
     * Menu settings
     */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_preferences:
			startActivity(new Intent(this, Preferences.class));
			return true;
		case R.id.action_about:
            HelpUtils.showAboutDialog(MainActivity.this);
            return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
    @Override
    protected void onDestroy() {
    	GCMManager.clear(this);
        super.onDestroy();
    }

}
