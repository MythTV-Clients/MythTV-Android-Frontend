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

import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.dvr.programGroup.ProgramGroup;
import org.mythtv.db.dvr.programGroup.ProgramGroupDaoHelper;
import org.mythtv.service.util.FileHelper;
import org.mythtv.service.util.image.ImageCache;
import org.mythtv.service.util.image.ImageFetcher;
import org.mythtv.services.api.dvr.Program;

import android.content.Intent;
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
	private ProgramGroupDaoHelper mProgramGroupDaoHelper;
	private RecordedDaoHelper mRecordedDaoHelper;
	
	private boolean mUseMultiplePanes;

	private RecordingsFragment mRecordingsFragment;
	private ProgramGroupFragment mProgramGroupFragment;
	private EpisodeFragment mEpisodeFragment;
	
	private ProgramGroup selectedProgramGroup;
	private Program selectedProgram;
	
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		mProgramGroupDaoHelper = new ProgramGroupDaoHelper( this );
		mRecordedDaoHelper = new RecordedDaoHelper( this );
		
		setContentView( R.layout.activity_dvr_recordings );
		
        // Fetch screen height and width, to use as our max size when loading images as this activity runs full screen
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
        
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        Log.v( TAG, "onCreate : device hxw - " + height + " x " + width );
        
        int longest = width; //( height < width ? height : width );
        
		FileHelper mFileHelper = new FileHelper( this );
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams( mFileHelper.getProgramRecordedDataDirectory() );
        cacheParams.setMemCacheSizePercent( this, 0.25f ); // Set memory cache to 25% of mem class

        mRecordingsFragment = (RecordingsFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_program_groups );
        mRecordingsFragment.setOnProgramGroupListener( this );

		mUseMultiplePanes = ( null != findViewById( R.id.fragment_dvr_program_group ) );

		if( mUseMultiplePanes ) {
			
			longest = width / 3;
			
			mProgramGroupFragment = (ProgramGroupFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_program_group );
			mProgramGroupFragment.setOnEpisodeSelectedListener(this);
			
			mEpisodeFragment = (EpisodeFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_episode );
			mEpisodeFragment.setOnEpisodeActionListener( this );
			
			List<ProgramGroup> programGroups = mProgramGroupDaoHelper.findAll();
			if( null != programGroups && !programGroups.isEmpty() ) {
				onProgramGroupSelected( programGroups.get( 0 ) );
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

    /* (non-Javadoc)
     * @see org.mythtv.client.ui.dvr.RecordingsFragment.OnProgramGroupListener#onProgramGroupSelected(org.mythtv.db.dvr.programGroup.ProgramGroup)
     */
    public void onProgramGroupSelected( ProgramGroup programGroup ) {
		Log.d( TAG, "onProgramGroupSelected : enter" );

		if( null == programGroup ) {
			Log.d( TAG, "onProgramGroupSelected : exit, programGroups is empty" );

			return;
		}
		
		selectedProgramGroup = programGroup;
		selectedProgram = null;
		
		List<Program> programs = mRecordedDaoHelper.findAllByTitle( programGroup.getTitle() );
		if( null == programs || programs.isEmpty() ) {
			Log.d( TAG, "onProgramGroupSelected : exit, no programs in programGroup" );

			return;
		}

		selectedProgram = programs.get( 0 );
		
		if( null != findViewById( R.id.fragment_dvr_program_group ) ) {
			FragmentManager manager = getSupportFragmentManager();

			final boolean programGroupAdded = ( mProgramGroupFragment != null );
			if( programGroupAdded ) {
				if( null != mProgramGroupFragment.getSelectedProgramGroup() && mProgramGroupFragment.getSelectedProgramGroup().equals( programGroup ) ) {
					Log.d( TAG, "onProgramGroupSelected : exit, programGroup already selected" );
					
					return;
				}
				
				mProgramGroupFragment.loadProgramGroup( programGroup );
			} else {
				Log.v( TAG, "onProgramGroupSelected : creating new programGroupFragment" );
				FragmentTransaction transaction = manager.beginTransaction();
				mProgramGroupFragment = new ProgramGroupFragment();

				if( mUseMultiplePanes ) {
					Log.v( TAG, "onProgramGroupSelected : adding to multipane" );

					transaction.add( R.id.fragment_dvr_program_group, mProgramGroupFragment, PROGRAM_GROUP_LIST_TAG );
				} else {
					Log.v( TAG, "onProgramGroupSelected : replacing fragment" );

					transaction.replace( R.id.fragment_dvr_program_group, mProgramGroupFragment, PROGRAM_GROUP_LIST_TAG );
					transaction.addToBackStack( null );
				}
				transaction.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN );
				transaction.commit();

				Log.v( TAG, "onProgramGroupSelected : setting program group to display" );
				mProgramGroupFragment.loadProgramGroup( programGroup );
			}

			onEpisodeSelected( selectedProgram.getChannelInfo().getChannelId(), selectedProgram.getStartTime() );
			
		} else {
			Log.v( TAG, "onProgramGroupSelected : starting program group activity" );

			Intent i = new Intent( this, ProgramGroupActivity.class );
			i.putExtra( ProgramGroupActivity.EXTRA_PROGRAM_GROUP_KEY, programGroup.getProgramGroup() );
			startActivity( i );
		}

		Log.d( TAG, "onProgramGroupSelected : exit" );
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.ProgramGroupFragment.OnEpisodeSelectedListener#onEpisodeSelected(java.lang.Long, org.joda.time.DateTime)
	 * 
	 * This is called when an episode is selected in the ProgramGroupFragment. The ProgramGroupFragment
	 * will only be visible during this activities life cycle on larger screens.
	 * 
	 */
	@Override
	public void onEpisodeSelected( Long channelId, DateTime startTime ) {
		Log.v( TAG, "onEpisodeSelect : enter" );
		
		//check if we're hosting multiple fragments and have the episode fragment
		if( mUseMultiplePanes && null != mEpisodeFragment ){
			//tell the episode fragment to do it's business
			mEpisodeFragment.loadEpisode( channelId, startTime );
		}
		
		Log.v( TAG, "onEpisodeSelect : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.EpisodeFragment.OnEpisodeActionListener#onEpisodeDeleted(java.lang.String)
	 */
	@Override
	public void onEpisodeDeleted( String programGroup ) {
		Log.v( TAG, "onEpisodeDeleted : enter" );

		mRecordingsFragment.notifyDeleted();
		
		Log.v( TAG, "onEpisodeDeleted : exit" );
	}

    /**
     * @return
     */
    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }

    /**
     * @return
     */
    public ProgramGroupDaoHelper getProgramGroupDaoHelper() {
    	return mProgramGroupDaoHelper;
    }
    
    /**
     * @return
     */
    public RecordedDaoHelper getRecordedDaoHelper() {
    	return mRecordedDaoHelper;
    }

    /**
     * @return
     */
    public ProgramGroup getSelectedProgramGroup() {
    	return selectedProgramGroup;
    }
    
    /**
     * @return
     */
    public Program getSelectedProgram() {
    	return selectedProgram;
    }

}
