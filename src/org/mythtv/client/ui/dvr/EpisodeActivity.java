package org.mythtv.client.ui.dvr;

import org.mythtv.R;
import org.mythtv.db.dvr.ProgramConstants;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

	private EpisodeFragment episodeFragment;
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.AbstractDvrActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.activity_dvr_episode );
		
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
		Cursor cursor = getContentResolver().query(
				ContentUris.withAppendedId( ProgramConstants.CONTENT_URI_RECORDED, episodeId ),
				new String[] { ProgramConstants.FIELD_PROGRAM_GROUP },
				null, null, null );
		if( cursor.moveToFirst() ) {
			programGroup = cursor.getString( cursor.getColumnIndexOrThrow( ProgramConstants.FIELD_PROGRAM_GROUP ) );
		}
		cursor.close();
		
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
				Intent i = new Intent( this, EpisodeActivity.class );
				i.putExtra( EpisodeActivity.EPISODE_KEY, id );
				startActivity( i );
			}

		} else {
		
			finish();

		}
		cursor.close();


		Log.v( TAG, "onEpisodeDeleted : exit" );
	}

}
