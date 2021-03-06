package com.stuff.stuffapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.adapters.HomeFeedAdapter;
import com.stuff.stuffapp.models.Conversation;
import com.stuff.stuffapp.models.Item;

public class HomeFragment extends Fragment {

	
	//TODO: SJ Clean this up. 
	public interface OnItemClickedListener {
        public void onItemClicked(Item item);
        public void onMessageCompose(Conversation conversation,Item item);
    }
	
	private static String TAG = "HomeFragment";
	
	private View view;

	public static HomeFragment newInstance() {
		HomeFragment fragment = new HomeFragment();
		return fragment;
	}
	
	private  HomeFeedAdapter mainAdapter;
	
	private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_home, container, false);
        
        if(savedInstanceState == null) {
        	

        	if ( mainAdapter == null ) {
        		mainAdapter = new HomeFeedAdapter(getActivity());
        	}

        	
	        ListView lvHome = (ListView) view.findViewById(R.id.lvHome);
	        
	        if ( swingBottomInAnimationAdapter == null ) {
	        	swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(mainAdapter);
        		swingBottomInAnimationAdapter.setInitialDelayMillis(300);
    			swingBottomInAnimationAdapter.setAbsListView(lvHome);
	        }
        	
    		lvHome.setAdapter(swingBottomInAnimationAdapter);
    		lvHome.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Item item = (Item) arg0.getItemAtPosition(arg2);
					((OnItemClickedListener) getActivity()).onItemClicked(item);
				}
			});
        
        }

		return view;
    }

}
