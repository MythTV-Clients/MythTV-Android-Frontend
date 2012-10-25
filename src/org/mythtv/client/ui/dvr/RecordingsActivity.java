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
package org.mythtv.client.ui.dvr;

import org.mythtv.R;
import org.mythtv.db.dvr.ProgramConstants;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

/**
 * @author Daniel Frey
 * 
 */
public class RecordingsActivity extends AbstractDvrActivity implements RecordingsFragment.OnProgramGroupListener, ProgramGroupFragment.OnEpisodeSelectedListener {

	private static final String TAG = RecordingsActivity.class.getSimpleName();
	private static final String PROGRAM_GROUP_LIST_TAG = "PROGRAM_GROUP_LIST_TAG";
	
	private boolean mUseMultiplePanes;

	private RecordingsFragment recordingsFragment;
	private ProgramGroupFragment programGroupFragment;
	private EpisodeFragment mEpisodeFragment;
	
	
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_dvr_recordings );

		recordingsFragment = (RecordingsFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_program_groups );
		recordingsFragment.setOnProgramGroupListener( this );

		mUseMultiplePanes = ( null != findViewById( R.id.fragment_dvr_program_group ) );

		if( mUseMultiplePanes ) {
			
			mEpisodeFragment = (EpisodeFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_episode );
			
			programGroupFragment = (ProgramGroupFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_program_group );
			programGroupFragment.setOnEpisodeSelectedListener(this);
			
			Cursor cursor = getContentResolver().query( ProgramConstants.CONTENT_URI_RECORDED, new String[] { ProgramConstants._ID }, null, null, ProgramConstants.FIELD_PROGRAM_GROUP );
			if( cursor.moveToFirst() ) {
				Long id = cursor.getLong( cursor.getColumnIndexOrThrow( ProgramConstants._ID ) );
				onProgramGroupSelected( id );
			}
		}
		
		Log.v( TAG, "onCreate : exit" );
	}

	public void onProgramGroupSelected( Long recordedId ) {
		Log.d( TAG, "onProgramGroupSelected : enter" );
		
		String programGroup = "";
		
		Cursor cursor = getContentResolver().query( ContentUris.withAppendedId( ProgramConstants.CONTENT_URI_RECORDED, recordedId ), new String[] { ProgramConstants.FIELD_PROGRAM_GROUP }, null, null, null );
		if( cursor.moveToFirst() ) {
	        programGroup = cursor.getString( cursor.getColumnIndexOrThrow( ProgramConstants.FIELD_PROGRAM_GROUP ) );
	        
	        Log.d( TAG, "onProgramGroupSelected : programGroup=" + programGroup );
		}
		cursor.close();
		
		if( null == programGroup || "".equals( programGroup ) ) {
			Log.d( TAG, "onProgramGroupSelected : exit, programGroups is empty" );

			return;
		}
		
		if( null != findViewById( R.id.fragment_dvr_program_group ) ) {
			FragmentManager manager = getSupportFragmentManager();

			final boolean programGroupAdded = ( programGroupFragment != null );
			if( programGroupAdded ) {
				if( null != programGroupFragment.getSelectedProgramGroup() && programGroupFragment.getSelectedProgramGroup().equals( programGroup ) ) {
					return;
				}
				
				programGroupFragment.loadProgramGroup( programGroup );
			} else {
				Log.v( TAG, "onProgramGroupSelected : creating new programGroupFragment" );
				FragmentTransaction transaction = manager.beginTransaction();
				programGroupFragment = new ProgramGroupFragment();

				if( mUseMultiplePanes ) {
					Log.v( TAG, "onProgramGroupSelected : adding to multipane" );

					transaction.add( R.id.fragment_dvr_program_group, programGroupFragment, PROGRAM_GROUP_LIST_TAG );
				} else {
					Log.v( TAG, "onProgramGroupSelected : replacing fragment" );

					transaction.replace( R.id.fragment_dvr_program_group, programGroupFragment, PROGRAM_GROUP_LIST_TAG );
					transaction.addToBackStack( null );
				}
				transaction.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN );
				transaction.commit();

				Log.v( TAG, "onProgramGroupSelected : setting program group to display" );
				programGroupFragment.loadProgramGroup( programGroup );
			}
		} else {
			Log.v( TAG, "onProgramGroupSelected : starting program group activity" );

			Intent i = new Intent( this, ProgramGroupActivity.class );
			i.putExtra( ProgramGroupActivity.EXTRA_PROGRAM_GROUP_KEY, programGroup );
			startActivity( i );
		}

		Log.d( TAG, "onProgramGroupSelected : exit" );
	}
	
	/**
	 * This is called when an episode is selected in the ProgramGroupFragment. The ProgramGroupFragment
	 * will only be visible during this activities life cycle on larger screens.
	 */
	@Override
	public void onEpisodeSelected(long id) {
		
		Log.v(TAG,  "onEpisodeSelect : enter");
		
		//check if we're hosting multiple fragments and have the episode fragment
		if( mUseMultiplePanes && null != mEpisodeFragment ){
			//tell the episode fragment to do it's business
			mEpisodeFragment.loadEpisode(id);
		}
		
		/*
		//Start Video Playback -- this will be moving to the activity bar
		Intent i = new Intent( activity, VideoActivity.class );
		i.putExtra( VideoActivity.EXTRA_PROGRAM_KEY, id );
		startActivity( i );
		*/
		
		Log.v(TAG,  "onEpisodeSelect : exit");
	}

}
