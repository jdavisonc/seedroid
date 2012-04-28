package com.superdownloader.droideasy;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.superdownloader.droideasy.types.Item;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSClient;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSClientImpl;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSFactory;

public class DroidEasyActivity extends ListActivity implements OnItemClickListener {

	private ArrayAdapter<Item> adapter;
	private ProgressDialog m_ProgressDialog = null;
	private SuperdownloaderWSClient wsclient = null;
	private ActionMode mActionMode;
	private int selectedItem = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerC2DM();
        
        // Initialize
        wsclient = SuperdownloaderWSFactory.getClient(this);

        // Set adapter
        //adapter = new InteractiveArrayAdapter(this, new ArrayList<Item>());
        adapter = new ArrayAdapter<Item>(this, R.layout.rowbuttonlayout, new ArrayList<Item>());
		setListAdapter(adapter);
        
		mActionMode = null;
        getListView().setItemsCanFocus(false);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        getListView().setOnItemClickListener(this);
		
		// Create thread for get data
		Thread thread = new Thread(null, new Runnable() {
			public void run() {
				try {
					// Calling Web Service
					final List<Item> items = wsclient.getItemsAvaibleForDownload();

					// Render Items
					runOnUiThread(new Runnable() {
						public void run() {
							renderItems(items);
							m_ProgressDialog.dismiss();
						}
					});
				} catch (Exception e) {
					Log.e("droidEasy", e.getMessage());
				}
			}
		}, "MagentoBackground");

		thread.start();
		m_ProgressDialog = ProgressDialog.show(DroidEasyActivity.this, "Please wait...", "Retrieving data ...", true);
    }
    
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
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
    
    /**
     * Method for add items to adapter and render it
     * @param items
     */
    private void renderItems(List<Item> items) {
    	if (items != null) {
	    	for (Item item : items) {
				adapter.add(item);
			}
	    	adapter.notifyDataSetChanged();
    	}
    }

	protected void download() {
		// TODO Auto-generated method stub
		
	}

	private void registerC2DM() {
		Log.w("C2DM", "start registration process");
		Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
		intent.putExtra("app",PendingIntent.getBroadcast(this, 0, new Intent(), 0));
		intent.putExtra("sender", "jdavisonc@gmail.com");
		startService(intent);
	}
	
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Notice how the ListView api is lame
        // You can use mListView.getCheckedItemIds() if the adapter
        // has stable ids, e.g you're using a CursorAdaptor
        SparseBooleanArray checked = getListView().getCheckedItemPositions();
        boolean hasCheckedElement = false;
        for (int i = 0 ; i < checked.size() && ! hasCheckedElement ; i++) {
            hasCheckedElement = checked.valueAt(i);
        }
 
        if (hasCheckedElement) {
            if (mActionMode == null) {
            	mActionMode = startActionMode(new ModeCallback());
            }
        } else {
            if (mActionMode != null) {
            	mActionMode.finish();
            }
        }
    };
 
    private final class ModeCallback implements ActionMode.Callback {
 
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Create the menu from the xml file
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.rowselection, menu);
            return true;
        }
 
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // Here, you can checked selected items to adapt available actions
            return false;
        }
 
        public void onDestroyActionMode(ActionMode mode) {
            // Destroying action mode, let's unselect all items
            for (int i = 0; i < getListView().getAdapter().getCount(); i++)
            	getListView().setItemChecked(i, false);
 
            if (mode == mActionMode) {
                mActionMode = null;
            }
        }
 
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            long[] selected = getListView().getCheckedItemIds();
            if (selected.length > 0) {
                for (long id: selected) {
                    // Do something with the selected item
                }
            }
            mode.finish();
            return true;
        }
    };
	

}