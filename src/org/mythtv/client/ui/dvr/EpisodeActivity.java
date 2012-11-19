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

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.service.util.FileHelper;
import org.mythtv.service.util.image.ImageCache;
import org.mythtv.service.util.image.ImageFetcher;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.utils.ArticleCleaner;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;

/**
 * @author pot8oe
 * @author Daniel Frey
 *
 */
public class EpisodeActivity extends AbstractDvrActivity implements EpisodeFragment.OnEpisodeActionListener {

	public static final String CHANNEL_ID = "CHANNEL_ID";
	public static final String START_TIME = "START_TIME";
	
	private static final String TAG = EpisodeActivity.class.getSimpleName();

	private RecordedDaoHelper mRecordedDaoHelper;
	private ImageFetcher mImageFetcher;

	private EpisodeFragment episodeFragment;
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.AbstractDvrActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );
		
		mRecordedDaoHelper = new RecordedDaoHelper( this );
		
		setContentView( R.layout.activity_dvr_episode );
		
        // Fetch screen height and width, to use as our max size when loading images as this activity runs full screen
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
        
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        Log.v( TAG, "onCreate : device hxw - " + height + " x " + width );
        
        final int longest = width; //( height < width ? height : width );
        
		FileHelper mFileHelper = new FileHelper( this );
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams( mFileHelper.getProgramRecordedDataDirectory() );
        cacheParams.setMemCacheSizePercent( this, 0.25f ); // Set memory cache to 25% of mem class

        mImageFetcher = new ImageFetcher( this, longest );
        mImageFetcher.addImageCache( getSupportFragmentManager(), cacheParams );
        mImageFetcher.setImageFadeIn( false );

		Bundle args = getIntent().getExtras();
		Long channelId = args.getLong( CHANNEL_ID, -1 );
		Long startTime = args.getLong( START_TIME, -1 );
		
		episodeFragment = (EpisodeFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_episode );
		episodeFragment.setOnEpisodeActionListener( this );
		
		episodeFragment.loadEpisode( channelId, new DateTime( startTime ) );
		
		Log.v( TAG, "onCreate : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );

		Bundle args = getIntent().getExtras();
		Long channelId = args.getLong( CHANNEL_ID, -1 );
		Long startTime = args.getLong( START_TIME, -1 );
		Program program = mRecordedDaoHelper.findOne( channelId, new DateTime( startTime ) );
		
		String programGroup = ArticleCleaner.clean( program.getTitle() );
		
		switch( item.getItemId() ) {
			case android.R.id.home:
			
				if( null != programGroup ) {
					Intent intent = new Intent( this, ProgramGroupActivity.class );
					intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
					intent.putExtra( ProgramGroupActivity.EXTRA_PROGRAM_GROUP_KEY, programGroup );
					startActivity( intent );
				} else {
					Intent intent = new Intent( this, RecordingsActivity.class );
					intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
					startActivity( intent );
				}

				return true;
		}

		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
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
	 * @see org.mythtv.client.ui.dvr.EpisodeFragment.OnEpisodeActionListener#onEpisodeDeleted(java.lang.String)
	 */
	@Override
	public void onEpisodeDeleted( String programGroup ) {
		Log.v( TAG, "onEpisodeDeleted : enter" );
		
		finish();

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
    public RecordedDaoHelper getRecordedDaoHelper() {
        return mRecordedDaoHelper;
    }

}
