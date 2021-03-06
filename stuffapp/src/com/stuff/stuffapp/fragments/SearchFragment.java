package com.stuff.stuffapp.fragments;

import java.lang.reflect.Field;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.stuff.stuffapp.CustomViewPager;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.activities.MainActivity;
import com.stuff.stuffapp.models.Item;

public class SearchFragment extends Fragment {

	private static String TAG = "SearchFragment";

	// BEGIN BUG FIX for fragment within fragment
	// http://stackoverflow.com/questions/14929907/causing-a-java-illegalstateexception-error-no-activity-only-when-navigating-to
	// https://code.google.com/p/android/issues/detail?id=42601
	private static final Field sChildFragmentManagerField;
	static {
		Field f = null;
		try {
			f = Fragment.class.getDeclaredField("mChildFragmentManager");
			f.setAccessible(true);
		} catch (NoSuchFieldException e) {
			Log.e(TAG, "Error getting mChildFragmentManager field", e);
		}
		sChildFragmentManagerField = f;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		if (sChildFragmentManagerField != null) {
			try {
				sChildFragmentManagerField.set(this, null);
			} catch (Exception e) {
				Log.e(TAG, "Error setting mChildFragmentManager field", e);
			}
		}
	}

	// END BUG FIX

	// listener interface for parent activity
	public interface OnSearchViewChangedListener {
	    public void onChangeView(int position);
	}
	private OnSearchViewChangedListener mToggleListener;
	
	private SearchListFragment searchListFragment;

	private SearchMapFragment searchMapFragment;

	private View view;

	private CustomViewPager vpResultFragments;

	private ResultFragmentsPagerAdapter adapter;

	public static SearchFragment newInstance() {
		SearchFragment fragment = new SearchFragment();
		return fragment;
	}
	
	public void search(String query) {
	    Log.d(TAG, "Issuing search for '" + query + "'");
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.please_wait), true);
	    ParseQuery<Item> searchQuery = new ParseQuery<Item>(Item.class).whereContains("searchable",
				query.trim().toLowerCase()).whereNear("location",
				((MainActivity) SearchFragment.this.getActivity()).getLastKnownLocation());
		searchQuery.include("owner");
		searchQuery.findInBackground(new FindCallback<Item>() {
			@Override
			public void done(List<Item> objects, ParseException e) {
			    progressDialog.dismiss();
				if (objects != null) {
					Log.d(TAG, "Got " + objects.size() + " results");
					if ( searchListFragment != null ) {
						searchListFragment.displayResults(objects);
					}
					if ( searchMapFragment != null ) {
						searchMapFragment.displayResults(objects);
					}
				}
				else {
				    Log.d(TAG, "No results found, possible ParseException");
				}
			}
		});
	}

	@Override
	public void onAttach(Activity activity) {
	    Log.d(TAG, "onAttach");
	    super.onAttach(activity);

	    if ( activity instanceof OnSearchViewChangedListener ) {
	        mToggleListener = (OnSearchViewChangedListener) activity;
	    }
	    else
	        throw new ClassCastException(activity.toString() + " must implement SearchFragment.OnToggleSearchViewListener");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		view = inflater.inflate(R.layout.fragment_search, container, false);

		//
		// vpResultFragments
		//
		vpResultFragments = (CustomViewPager) view.findViewById(R.id.vpResultFragments);
		adapter = new ResultFragmentsPagerAdapter(getChildFragmentManager());
		vpResultFragments.setAdapter(adapter);
		vpResultFragments.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "CustomViewPager.onPageSelected: " + position);
                mToggleListener.onChangeView(position);
            }
            
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            
            @Override
            public void onPageScrollStateChanged(int state) { }
        });

		if (searchListFragment == null) {
			searchListFragment = SearchListFragment.newInstance();
		}
		if (searchMapFragment == null) {
			searchMapFragment = SearchMapFragment.newInstance();
		}

		return view;
	}

    /**
     * Sets the currently selected view in the vpResultFragments view pager.
     * 
     * @param position the numerical item to send to the PagerAdapter
     */
    public void changeSearchView(int position) {
        vpResultFragments.setCurrentItem(position, true);
    }

	private class ResultFragmentsPagerAdapter extends FragmentPagerAdapter {

		public ResultFragmentsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return searchListFragment;
			} else {
				return searchMapFragment;
			}
		}

		@Override
		public int getCount() {
			return 2;
		}
	}
}
