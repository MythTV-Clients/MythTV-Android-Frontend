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
package org.mythtv.client.ui.setup.capture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.mythtv.client.MainApplication;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.service.util.MythtvServiceHelper;
import org.mythtv.db.captureCard.model.CaptureCard;
import org.mythtv.db.captureCard.model.CaptureCardList;
import org.springframework.http.ResponseEntity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 * @author Daniel Frey
 *
 */
public class CaptureCardsListFragment extends ListFragment {

	private static final String TAG = CaptureCardsListFragment.class.getSimpleName();
	
	private MythtvServiceHelper mMythtvServiceHelper = MythtvServiceHelper.getInstance();
	
    private int mCurrentCaptureCard = 0;
    boolean mDualPane;
    
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		
		super.onActivityCreated( savedInstanceState );

		downloadCaptureCards();
		
        if( null != savedInstanceState ) {
            // Restore last state for checked position.
        	mCurrentCaptureCard = savedInstanceState.getInt( "CurrentCaptureCard", 0 );
        }

        Log.v( TAG, "onActivityCreated : exit" );
	}

    @Override
    public void onSaveInstanceState( Bundle outState ) {
		Log.v( TAG, "onSaveInstanceState : enter" );

		super.onSaveInstanceState( outState );
        
		outState.putInt( "CurrentCaptureCard", mCurrentCaptureCard );

		Log.v( TAG, "onSaveInstanceState : exit" );
    }

    @Override
    public void onListItemClick( ListView l, View v, int position, long id ) {
		Log.v( TAG, "onListItemClick : enter" );

		showDetails( position );

		Log.v( TAG, "onListItemClick : exit" );
    }

    
	//***************************************
    // Private methods
    //***************************************
    
    private void showDetails( int index ) {
		Log.v( TAG, "showDetails : enter" );

		mCurrentCaptureCard = index;
		
		if( mDualPane ) {
			// Update fragments in place
			getListView().setItemChecked( index, true );
			
//            // Check what fragment is currently shown, replace if needed.
//            ProgramGroupListFragment programGroup = (ProgramGroupListFragment) getFragmentManager().findFragmentById( R.id.dvr_fragment_program_group );
//            if( null == programGroup || programGroup.getShownIndex() != index ) {
//                // Make new fragment to show this selection.
//            	programGroup = ProgramGroupListFragment.newInstance( index );
//
//                // Execute a transaction, replacing any existing fragment
//                // with this one inside the frame.
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.replace( R.id.dvr_fragment_program_group, programGroup );
//                ft.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_FADE );
//                ft.commit();
//            }
		} else {
			// Show activity
			Intent intent = new Intent();
//            intent.setClass( getActivity(), ProgramGroupActivity.class );
            intent.putExtra( "index", index );
            startActivity( intent );
		}
		
		Log.v( TAG, "showDetails : enter" );
    }

	private void refreshCaptureCards() {	
		Log.v( TAG, "refreshCaptureCards : enter" );

//		if( null == mainApplication.getCaptureCards() ) {
//			Log.v( TAG, "refreshCaptureCards : exit, refreshProgramGroups is empty" );
//			return;
//		}
		
		// Populate List
//		setListAdapter( new ArrayAdapter<String>( getActivity(), android.R.layout.simple_list_item_activated_1, mainApplication.getCaptureCards() ) );

        if( mDualPane ) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode( ListView.CHOICE_MODE_SINGLE) ;
            
            // Make sure our UI is in the correct state.
            showDetails( mCurrentCaptureCard );
        }

		Log.v( TAG, "refreshCaptureCards : exit" );
	}
		
	private void downloadCaptureCards() {
		Log.v( TAG, "downloadCaptureCards : enter" );

		new DownloadCaptureCardsTask().execute();

		Log.v( TAG, "downloadCaptureCards : exit" );
	}

	
	// ***************************************
	// Private classes
	// ***************************************
	private class DownloadCaptureCardsTask extends AsyncTask<Void, Void, ResponseEntity<CaptureCardList>> {

		@Override
		protected ResponseEntity<CaptureCardList> doInBackground( Void... params ) {
			Log.v( TAG, "DownloadCaptureCardsTask.doInBackground : enter" );

			try {
				Log.v( TAG, "DownloadCaptureCardsTask.doInBackground : exit" );

				MainApplication mainApplication = (MainApplication) getActivity().getApplicationContext();
				EtagInfoDelegate eTag = EtagInfoDelegate.createEmptyETag();
				return mMythtvServiceHelper.getMythServicesApi( getActivity() ).captureOperations().getCaptureCardList( eTag );
			} catch( Exception e ) {
				Log.e( TAG, "DownloadCaptureCardsTask.doInBackground : error", e );
			}

			Log.v( TAG, "DownloadCaptureCardsTask.doInBackground : exit, failed" );
			return null;
		}

		@Override
		protected void onPostExecute( ResponseEntity<CaptureCardList> result ) {
			Log.v( TAG, "DownloadCaptureCardsTask.onPostExecute : enter" );

			List<String> sortedCaptureCards = new ArrayList<String>();
			Map<String,List<CaptureCard>> sortedResult = new TreeMap<String, List<CaptureCard>>();
			for( CaptureCard captureCard : result.getBody().getCaptureCards().getCaptureCards() ) {
				String device = captureCard.getVideoDevice();
				
				if( sortedResult.containsKey( device ) ) {
					List<CaptureCard> groupPrograms = new ArrayList<CaptureCard>();
					groupPrograms.add( captureCard );
					sortedResult.put( device, groupPrograms );
				} else {
					sortedResult.get( device ).add( captureCard );
				}
				
				if( !sortedCaptureCards.contains( device ) ) {
					sortedCaptureCards.add( device );
				}
			}
			
			if( !sortedCaptureCards.isEmpty() ) {
				Collections.sort( sortedCaptureCards, String.CASE_INSENSITIVE_ORDER );
			}
			
//			mainApplication.setCaptureCards( sortedCaptureCards );
//			mainApplication.setCurrentCaptureCards( sortedResult );
			
			refreshCaptureCards();

			Log.v( TAG, "DownloadCaptureCardsTask.onPostExecute : exit" );
		}
	}

}
