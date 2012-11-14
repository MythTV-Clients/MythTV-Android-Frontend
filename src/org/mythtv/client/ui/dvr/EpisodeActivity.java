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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;

/**
 * @author pot8oe
 * @author Daniel Frey
 *
 */
public class EpisodeActivity extends AbstractDvrActivity implements EpisodeFragment.OnEpisodeActionListener {

	public static final String EPISODE_KEY = "EPISODE_ID";
	
	private static final String TAG = EpisodeActivity.class.getSimpleName();

	private ImageFetcher mImageFetcher;
	private FileHelper mFileHelper;

	private EpisodeFragment episodeFragment;
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.AbstractDvrActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_dvr_episode );
		
		mFileHelper = new FileHelper( this );

        // Fetch screen height and width, to use as our max size when loading images as this activity runs full screen
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
        
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        Log.v( TAG, "onCreate : device hxw - " + height + " x " + width );
        
        final int longest = width; //( height < width ? height : width );
        
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams( mFileHelper.getProgramRecordedDataDirectory() );
        cacheParams.setMemCacheSizePercent( this, 0.25f ); // Set memory cache to 25% of mem class

        mImageFetcher = new ImageFetcher( this, longest );
        mImageFetcher.addImageCache( getSupportFragmentManager(), cacheParams );
        mImageFetcher.setImageFadeIn( false );

		Bundle args = getIntent().getExtras();
		Long episodeId = args.getLong( EPISODE_KEY, -1 );
		
		episodeFragment = (EpisodeFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_episode );
		episodeFragment.setOnEpisodeActionListener( this );
		
		if( episodeId > 0 ) {
			episodeFragment.loadEpisode( episodeId );
		}
		
		Log.v( TAG, "onCreate : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );

		Bundle args = getIntent().getExtras();
		Long episodeId = args.getLong( EPISODE_KEY, -1 );

		String programGroup = null;
//		Cursor cursor = getContentResolver().query(
//				ContentUris.withAppendedId( ProgramConstants.CONTENT_URI_RECORDED, episodeId ),
//				new String[] { ProgramConstants.FIELD_PROGRAM_GROUP },
//				null, null, null );
//		if( cursor.moveToFirst() ) {
//			programGroup = cursor.getString( cursor.getColumnIndexOrThrow( ProgramConstants.FIELD_PROGRAM_GROUP ) );
//		}
//		cursor.close();
		
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

    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.EpisodeFragment.OnEpisodeActionListener#onEpisodeDeleted(java.lang.String)
	 */
	@Override
	public void onEpisodeDeleted( String programGroup ) {
		Log.v( TAG, "onEpisodeDeleted : enter" );
		
		String[] projection = new String[] { ProgramConstants._ID };
		
//		Cursor cursor = getContentResolver().query( ProgramConstants.CONTENT_URI_RECORDED, projection, ProgramConstants.FIELD_PROGRAM_GROUP + " = ?", new String[] { programGroup }, ProgramConstants.FIELD_PROGRAM_GROUP );
//		if( cursor.getCount() > 0 ) {
//
//			if( cursor.moveToFirst() ) {
//				Long id = cursor.getLong( cursor.getColumnIndexOrThrow( ProgramConstants._ID ) );
//				Intent i = new Intent( this, EpisodeActivity.class );
//				i.putExtra( EpisodeActivity.EPISODE_KEY, id );
//				startActivity( i );
//			}
//
//		} else {
//		
//			finish();
//
//		}
//		cursor.close();


		Log.v( TAG, "onEpisodeDeleted : exit" );
	}

}
