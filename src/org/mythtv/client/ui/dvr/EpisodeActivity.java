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
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.dvr.programGroup.ProgramGroupConstants;
import org.mythtv.services.api.dvr.Program;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

/**
 * @author pot8oe
 * @author Daniel Frey
 *
 */
public class EpisodeActivity extends AbstractDvrActivity implements EpisodeFragment.OnEpisodeActionListener {

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
		int channelId = args.getInt( ProgramConstants.FIELD_CHANNEL_ID, -1 );
		Long startTime = args.getLong( ProgramConstants.FIELD_START_TIME, -1 );
		
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
		int channelId = args.getInt( ProgramConstants.FIELD_CHANNEL_ID, -1 );
		Long startTime = args.getLong( ProgramConstants.FIELD_START_TIME, -1 );
		Program program = mRecordedDaoHelper.findOne( channelId, new DateTime( startTime ) );
		
		switch( item.getItemId() ) {
			case android.R.id.home:
			
				Intent intent = new Intent( this, ProgramGroupActivity.class );
				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK  );
				intent.putExtra( ProgramGroupConstants.FIELD_TITLE, program.getTitle() );
				startActivity( intent );
				
				finish();
				
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
		
		Intent intent = new Intent( this, ProgramGroupActivity.class );
		intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK  );
		intent.putExtra( ProgramGroupConstants.FIELD_TITLE, "" );
		startActivity( intent );
		
		finish();

		Log.v( TAG, "onEpisodeDeleted : exit" );
	}

}
