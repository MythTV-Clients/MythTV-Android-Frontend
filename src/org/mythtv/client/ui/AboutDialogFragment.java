/**
 * This file is part of MythTV Android Frontend
 *
 * MythTV Android Frontend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MythTV Android Frontend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MythTV Android Frontend.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This software can be found at <https://github.com/MythTV-Clients/MythTV-Android-Frontend/>
 */
package org.mythtv.client.ui;

import org.mythtv.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Daniel Frey
 *
 */
public class AboutDialogFragment extends DialogFragment {

	private static final String TAG = AboutDialogFragment.class.getSimpleName();
	
    /**
     * @return
     */
    public static AboutDialogFragment newInstance() {
    	return new AboutDialogFragment();
    }

    /* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );

		View v = inflater.inflate( R.layout.fragment_about, container, false );
		
		TextView url1 = (TextView) v.findViewById( R.id.about_url1 );
		url1.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick( View v ) {
				
				Intent intent = new Intent( Intent.ACTION_VIEW );
				intent.setData( Uri.parse( "https://github.com/MythTV-Clients/MythTV-Android-Frontend" ) );
				startActivity( intent );
				
			}
		});
		
		TextView url2 = (TextView) v.findViewById( R.id.about_url2 );
		url2.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick( View v ) {
				
				Intent intent = new Intent( Intent.ACTION_VIEW );
				intent.setData( Uri.parse( "http://mythtv.org" ) );
				startActivity( intent );
				
			}
		});

		TextView url3 = (TextView) v.findViewById( R.id.about_url3 );
		url3.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick( View v ) {
				
				Intent intent = new Intent( Intent.ACTION_VIEW );
				intent.setData( Uri.parse( "http://c9studio.com" ) );
				startActivity( intent );
				
			}
		});
		
		getDialog().setTitle( getResources().getString( R.string.about_title ) );
		getDialog().setCancelable( true );
		getDialog().setCanceledOnTouchOutside( true );
		
		Log.v( TAG, "onCreateView : exit" );
		return v;
	}

}
