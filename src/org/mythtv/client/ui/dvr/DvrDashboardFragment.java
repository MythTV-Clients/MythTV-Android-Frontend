package org.mythtv.client.ui.dvr;

import org.mythtv.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DvrDashboardFragment extends Fragment {

	private final static String TAG = DvrDashboardFragment.class.getSimpleName();

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );
		
		View root = inflater.inflate( R.layout.dvr_fragment_dashboard, container );

		// Attach event handlers
		root.findViewById( R.id.dvr_btn_recordings ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "recordings.onClick : enter" );
				
//				if( UIUtils.isHoneycombTablet( getActivity() ) ) {
//					startActivity( new Intent( getActivity(), ScheduleMultiPaneActivity.class ) );
//				} else {
//					startActivity( new Intent( getActivity(), ScheduleActivity.class ) );
//				}

				Log.v( TAG, "recordings.onClick : exit" );
			}

		} );

		root.findViewById( R.id.dvr_btn_upcoming ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "upcoming.onClick : enter" );

				Log.v( TAG, "upcoming.onClick : exit" );
			}
		} );

		root.findViewById( R.id.dvr_btn_live ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "live.onClick : enter" );

				Log.v( TAG, "live.onClick : exit" );
			}
		} );

		root.findViewById( R.id.dvr_btn_guide ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "guide.onClick : enter" );

				Log.v( TAG, "guide.onClick : exit" );
			}
		} );

		Log.v( TAG, "onCreateView : exit" );
		return root;
	}

}
