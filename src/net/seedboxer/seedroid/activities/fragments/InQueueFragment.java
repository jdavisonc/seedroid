/*******************************************************************************
 * InQueueFragment.java
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
package net.seedboxer.seedroid.activities.fragments;

import java.util.ArrayList;
import java.util.List;

import net.seedboxer.seedroid.R;
import net.seedboxer.seedroid.services.seedboxer.SeedBoxerWSClient;
import net.seedboxer.seedroid.services.seedboxer.SeedBoxerWSFactory;
import net.seedboxer.seedroid.services.seedboxer.types.FileValue;
import net.seedboxer.seedroid.tools.LauncherUtils;
import net.seedboxer.seedroid.utils.Runners;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.apps.dashclock.ui.SwipeDismissListViewTouchListener;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;


/**
 * @author Jorge Davison (jdavisonc)
 *
 */
public class InQueueFragment extends Fragment {
	
	private InQueueItemsAdapter adapter;
	
	private DragSortListView mListView;
	
	private ActionMode mActionMode;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set adapter
        adapter = new InQueueItemsAdapter(getActivity(), R.layout.in_queue_view, new ArrayList<FileValue>());
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		if (isVisibleToUser == true) { 
			refresh();			
		} else {
	    	if (mActionMode != null) {
	    		mActionMode.finish();
	    	}
		}
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
    	ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.in_queue_view, container, false);

        mListView = (DragSortListView) rootView.findViewById(android.R.id.list);
        mListView.setAdapter(adapter);
        mListView.setEmptyView(rootView.findViewById(android.R.id.empty));
        
        final DragSortController dragSortController = new ConfigurationDragSortController();
        mListView.setFloatViewManager(dragSortController);
        mListView.setDropListener(new DragSortListView.DropListener() {
            public void drop(int from, int to) {
                adapter.change(from, to);
            }
        });
        final SwipeDismissListViewTouchListener swipeDismissTouchListener =
                new SwipeDismissListViewTouchListener(
                        mListView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            public boolean canDismiss(int position) {
                                return position < adapter.getCount();
                            }

                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                            	for (int position : reverseSortedPositions) {
                            		FileValue item = adapter.remove(position);
                            		delete(item);
                                }
                            }
                        });
        mListView.setOnScrollListener(swipeDismissTouchListener.makeScrollListener());
        mListView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return dragSortController.onTouch(view, motionEvent)
                        || (!dragSortController.isDragging()
                        && swipeDismissTouchListener.onTouch(view, motionEvent));

            }
        });

        mListView.setItemsCanFocus(true);
		
		setHasOptionsMenu(true);
        
        return rootView;
    }
    
	private void renderList(List<FileValue> items) {
		if (items != null) {
			adapter.clear();
			adapter.addAll(items);
			adapter.notifyDataSetChanged();
		}
	}
	
	public class InQueueItemsAdapter extends ArrayAdapter<FileValue> {
		
		private final List<FileValue> items;
		
		public InQueueItemsAdapter(Context context, int textViewResourceId, List<FileValue> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.queue_item_view, parent, false);
            }

            TextView titleView = (TextView) convertView.findViewById(android.R.id.text1);
            //TextView descriptionView = (TextView) convertView.findViewById(android.R.id.text2);
            //ImageView iconView = (ImageView) convertView.findViewById(android.R.id.icon1);

			FileValue item = getItem(position);
			titleView.setText(item.getName());

			return convertView;
		}
		
		public FileValue remove(int index) {
			FileValue item = getItem(index);
			remove(item);
			return item;
		}
		
		public void change(int from, int to) {
			FileValue item = getItem(from);
			remove(item);
            insert(item, to);
            notifyDataSetChanged();
		}
		
		public List<FileValue> getItems() {
			return items;
		}
	}
	

    private class ConfigurationDragSortController extends DragSortController {
        private int mPos;

        public ConfigurationDragSortController() {
            super(InQueueFragment.this.mListView, R.id.drag_handle,
                    DragSortController.ON_DOWN, 0);
            setRemoveEnabled(false);
        }

        @Override
        public int startDragPosition(MotionEvent ev) {
            int res = super.dragHandleHitPosition(ev);
            if (res >= adapter.getCount()) {
                return DragSortController.MISS;
            }

            return res;
        }
        
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
        		float distanceX, float distanceY) {
        	return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public View onCreateFloatView(int position) {
            mPos = position;
        	
            if (mActionMode == null) {
				mActionMode = getView().startActionMode(new ModeCallback());
			}
        	
            return adapter.getView(position, null, mListView);
        }

        private int origHeight = -1;

        @Override
        public void onDragFloatView(View floatView, Point floatPoint, Point touchPoint) {
            final int addPos = adapter.getCount();
            final int first = mListView.getFirstVisiblePosition();
            final int lvDivHeight = mListView.getDividerHeight();

            if (origHeight == -1) {
                origHeight = floatView.getHeight();
            }

            View div = mListView.getChildAt(addPos - first);

            if (touchPoint.x > mListView.getWidth() / 2) {
                float scale = touchPoint.x - mListView.getWidth() / 2;
                scale /= mListView.getWidth() / 5;
                ViewGroup.LayoutParams lp = floatView.getLayoutParams();
                lp.height = Math.max(origHeight, (int) (scale * origHeight));
                //Log.d("mobeta", "setting height " + lp.height);
                floatView.setLayoutParams(lp);
            }

            if (div != null) {
                if (mPos > addPos) {
                    // don't allow floating View to go above
                    // section divider
                    final int limit = div.getBottom() + lvDivHeight;
                    if (floatPoint.y < limit) {
                        floatPoint.y = limit;
                    }
                } else {
                    // don't allow floating View to go below
                    // section divider
                    final int limit = div.getTop() - lvDivHeight - floatView.getHeight();
                    if (floatPoint.y > limit) {
                        floatPoint.y = limit;
                    }
                }
            }
        }

        @Override
        public void onDestroyFloatView(View floatView) {
            //do nothing; block super from crashing
        }
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			refresh();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private final class ModeCallback implements ActionMode.Callback {

		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.in_queue_item_menu, menu);
			return true;
		}

		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		public void onDestroyActionMode(ActionMode mode) {
			if (mode == mActionMode) {
				mActionMode = null;
			}
		}

		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.menu_update:
				update(adapter.getItems());
				mode.finish();
			default:
				mode.finish();
			}
			return true;
		}
	}
	
	//
	// SeedBoxer communication
	// 
    
	private void refresh() {
		Runners.runOnThread(getActivity(), new Runnable() {
			public void run() {
				try {
					SeedBoxerWSClient wsclient = SeedBoxerWSFactory.getClient(getActivity());
					final List<FileValue> inQueueItems = wsclient.getQueue();

					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							renderList(inQueueItems);
						}
					});
				} catch (Exception e) {
					Log.e("seedroid", "Error communicating with SeedBoxer.");
					LauncherUtils.showError("Error communicating with SeedBoxer.", getActivity());
				}
			}
		});
	}
	
	private void delete(final FileValue item) {
		Runners.runOnThread(getActivity(), new Runnable() {
			public void run() {
				try {
					SeedBoxerWSClient wsclient = SeedBoxerWSFactory.getClient(getActivity());
					wsclient.removeFromQueue(item.getQueueId());
				} catch (Exception e) {
					Log.e("seedroid", "Error communicating with SeedBoxer.");
					LauncherUtils.showError("Error communicating with SeedBoxer.", getActivity());
				}
			}
		});
	}
	
	private void update(List<FileValue> items) {
		final List<FileValue> updatedItems = getUpdatedList(items);
		
		Runners.runOnThread(getActivity(), new Runnable() {
			public void run() {
				try {
					SeedBoxerWSClient wsclient = SeedBoxerWSFactory.getClient(getActivity());
					wsclient.updateQueue(updatedItems);
				} catch (Exception e) {
					Log.e("seedroid", "Error communicating with SeedBoxer.");
					LauncherUtils.showError("Error communicating with SeedBoxer.", getActivity());
				}
			}
		});
	}

	private List<FileValue> getUpdatedList(List<FileValue> items) {
		final List<FileValue> updatedItems = new ArrayList<FileValue>();
		int order = 0;
		for(FileValue value : items) {
			FileValue item = new FileValue();
			item.setQueueId(value.getQueueId());
			item.setOrder(order++);
			updatedItems.add(item);
		}
		return updatedItems;
	}

}
