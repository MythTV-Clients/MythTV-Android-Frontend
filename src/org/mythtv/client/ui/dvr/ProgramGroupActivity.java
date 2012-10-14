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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.mythtv.R;
import org.mythtv.service.dvr.ProgramGroupRecordedDownloadService;
import org.mythtv.service.util.UrlUtils;
import org.mythtv.services.api.dvr.Programs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Daniel Frey
 * @author John Baab
 * 
 */
public class ProgramGroupActivity extends AbstractDvrActivity {

	private static final String TAG = ProgramGroupActivity.class.getSimpleName();

	public static final String EXTRA_PROGRAM_GROUP_KEY = "org.mythtv.client.ui.dvr.programGroup.EXTRA_PROGRAM_GROUP_KEY";

	private ProgramGroupFragment programGroupFragment = null;

	// ***************************************
	// Activity methods
	// ***************************************

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		Bundle extras = getIntent().getExtras(); 
		String programGroup = extras.getString( EXTRA_PROGRAM_GROUP_KEY );

		String encodedTitle = UrlUtils.encodeUrl( programGroup );

		File programGroupDirectory = mFileHelper.getProgramGroupDirectory( programGroup );
		File programGroupJson = new File( programGroupDirectory, encodedTitle + ProgramGroupRecordedDownloadService.RECORDED_FILE );

		Programs programs = null; 
		InputStream is = null;
		try {
			is = new BufferedInputStream( new FileInputStream( programGroupJson ), 8192 );
			programs = getMainApplication().getObjectMapper().readValue( is, Programs.class );
		} catch( FileNotFoundException e ) {
			Log.e( TAG, "onProgramGroupSelected : error, json could not be found", e );

			programs = RecordingsActivity.getDownloadingPrograms( programGroup );
		} catch( JsonParseException e ) {
			Log.e( TAG, "onProgramGroupSelected : error, json could not be parsed", e );

			programs = RecordingsActivity.getDownloadingPrograms( programGroup );
		} catch( JsonMappingException e ) {
			Log.e( TAG, "onProgramGroupSelected : error, json could not be mapped", e );
			programs = RecordingsActivity.getDownloadingPrograms( programGroup );

		} catch( IOException e ) {
			Log.e( TAG, "onProgramGroupSelected : error, io exception reading file", e );

			programs = RecordingsActivity.getDownloadingPrograms( programGroup );
		}

		Log.v( TAG, "onCreate : programs=" + programs.toString() );
		
		setContentView( R.layout.fragment_dvr_program_group );

		programGroupFragment = (ProgramGroupFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_program_group );
		programGroupFragment.loadPrograms( this, programs );
		
		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );

		switch( item.getItemId() ) {
			case android.R.id.home:
				// app icon in action bar clicked; go home
				Intent intent = new Intent( this, RecordingsActivity.class );
				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
				startActivity( intent );

				return true;
		}

		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}
	
}
