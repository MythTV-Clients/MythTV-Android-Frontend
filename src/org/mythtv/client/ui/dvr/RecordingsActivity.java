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
import org.mythtv.service.util.FileHelper;
import org.mythtv.service.util.image.ImageCache;
import org.mythtv.service.util.image.ImageFetcher;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * @author Daniel Frey
 * 
 */
public class RecordingsActivity extends AbstractDvrActivity implements RecordingsFragment.OnProgramGroupListener, ProgramGroupFragment.OnEpisodeSelectedListener, EpisodeFragment.OnEpisodeActionListener {

	private static final String TAG = RecordingsActivity.class.getSimpleName();
	private static final String PROGRAM_GROUP_LIST_TAG = "PROGRAM_GROUP_LIST_TAG";
	
	private ImageFetcher mImageFetcher;
	private FileHelper mFileHelper;
	
	private boolean mUseMultiplePanes;

	private RecordingsFragment recordingsFragment;
	private ProgramGroupFragment programGroupFragment;
	private EpisodeFragment mEpisodeFragment;
	
	
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_dvr_recordings );

		mFileHelper = new FileHelper( this );
		
        // Fetch screen height and width, to use as our max size when loading images as this activity runs full screen
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
        
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        Log.v( TAG, "onCreate : device hxw - " + height + " x " + width );
        
        int longest = width; //( height < width ? height : width );
        
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams( mFileHelper.getProgramRecordedDataDirectory() );
        cacheParams.setMemCacheSizePercent( this, 0.25f ); // Set memory cache to 25% of mem class

        recordingsFragment = (RecordingsFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_program_groups );
		recordingsFragment.setOnProgramGroupListener( this );

		mUseMultiplePanes = ( null != findViewById( R.id.fragment_dvr_program_group ) );

		if( mUseMultiplePanes ) {
			
			longest = width / 3;
			
			programGroupFragment = (ProgramGroupFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_program_group );
			programGroupFragment.setOnEpisodeSelectedListener(this);
			
			mEpisodeFragment = (EpisodeFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_episode );
			mEpisodeFragment.setOnEpisodeActionListener( this );
			
			Cursor cursor = getContentResolver().query( ProgramConstants.CONTENT_URI_RECORDED, new String[] { ProgramConstants._ID }, null, null, ProgramConstants.FIELD_PROGRAM_GROUP );
			if( cursor.moveToFirst() ) {
				Long id = cursor.getLong( cursor.getColumnIndexOrThrow( ProgramConstants._ID ) );
				onProgramGroupSelected( id );
			}
		}
		
        mImageFetcher = new ImageFetcher( this, longest );
        mImageFetcher.addImageCache( getSupportFragmentManager(), cacheParams );
        mImageFetcher.setImageFadeIn( false );

        Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();
		
        mImageFetcher.setExitTasksEarly( false );

		Log.v( TAG, "onResume : exit" );
	}

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause() {
		Log.v( TAG, "onPause : enter" );
        super.onPause();

        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();

        Log.v( TAG, "onPause : enter" );
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
		Log.v( TAG, "onDestroy : enter" );
		super.onDestroy();
        
		mImageFetcher.closeCache();

        Log.v( TAG, "onDestroy : exit" );
    }

    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
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

		onEpisodeSelected( recordedId );
		
		Log.d( TAG, "onProgramGroupSelected : exit" );
	}
	
	/**
	 * This is called when an episode is selected in the ProgramGroupFragment. The ProgramGroupFragment
	 * will only be visible during this activities life cycle on larger screens.
	 */
	@Override
	public void onEpisodeSelected(long id) {
		Log.v( TAG, "onEpisodeSelect : enter" );
		
		//check if we're hosting multiple fragments and have the episode fragment
		if( mUseMultiplePanes && null != mEpisodeFragment ){
			//tell the episode fragment to do it's business
			mEpisodeFragment.loadEpisode(id);
		}
		
		Log.v( TAG, "onEpisodeSelect : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.EpisodeFragment.OnEpisodeActionListener#onEpisodeDeleted(java.lang.String)
	 */
	@Override
	public void onEpisodeDeleted( String programGroup ) {
		Log.v( TAG, "onEpisodeDeleted : enter" );

		String[] projection = new String[] { ProgramConstants._ID };
		
		Cursor cursor = getContentResolver().query( ProgramConstants.CONTENT_URI_RECORDED, projection, ProgramConstants.FIELD_PROGRAM_GROUP + " = ?", new String[] { programGroup }, ProgramConstants.FIELD_PROGRAM_GROUP );
		if( cursor.getCount() > 0 ) {

			if( cursor.moveToFirst() ) {
				Long id = cursor.getLong( cursor.getColumnIndexOrThrow( ProgramConstants._ID ) );
				onProgramGroupSelected( id );
				onEpisodeSelected( id );
			}

		} else {
		
			cursor.close();
			
			cursor = getContentResolver().query( ProgramConstants.CONTENT_URI_RECORDED, projection, null, null, ProgramConstants.FIELD_PROGRAM_GROUP );
			if( cursor.moveToFirst() ) {
				Long id = cursor.getLong( cursor.getColumnIndexOrThrow( ProgramConstants._ID ) );
				onProgramGroupSelected( id );
				onEpisodeSelected( id );
			}

		}
		cursor.close();
		
		recordingsFragment.notifyDeleted();
		
		Log.v( TAG, "onEpisodeDeleted : exit" );
	}

}
