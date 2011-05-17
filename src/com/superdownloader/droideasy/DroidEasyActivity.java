package com.superdownloader.droideasy;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.superdownloader.droideasy.types.Item;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSClient;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSClientImpl;

public class DroidEasyActivity extends ListActivity {

	private ItemsAdapter adapter;
	private ProgressDialog m_ProgressDialog = null;
	private SuperdownloaderWSClient wsclient = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize
        wsclient = new SuperdownloaderWSClientImpl("harley", "p2prulz",null);

        // Set adapter
        adapter = new ItemsAdapter(DroidEasyActivity.this, R.layout.row, new ArrayList<Item>());
		setListAdapter(adapter);

		// Create thread for get data
		Thread thread = new Thread(null, new Runnable() {
			@Override
			public void run() {
				try {
					// Calling Web Service
					final List<Item> items = wsclient.getItemsAvaibleForDownload();

					// Render Items
					runOnUiThread(new Runnable() {
						@Override
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

    /**
     * Custom Adapter for render item
     * @author harley
     *
     */
	public class ItemsAdapter extends ArrayAdapter<Item> {

		public ItemsAdapter(Context context, int textViewResourceId, List<Item> items) {
			super(context, textViewResourceId, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.row, parent, false);
			}

			TextView label = (TextView) row.findViewById(R.id.weekofday);
			label.setText(getItem(position).getName());
			ImageView icon = (ImageView) row.findViewById(R.id.icon);

			icon.setImageResource(R.drawable.icon);

			return row;
		}
	}

}