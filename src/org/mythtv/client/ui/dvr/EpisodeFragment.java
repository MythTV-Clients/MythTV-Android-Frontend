package org.mythtv.client.ui.dvr;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.service.dvr.cache.CoverartLruMemoryCache;
import org.mythtv.services.api.Bool;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EpisodeFragment extends AbstractMythFragment {

	private static final String TAG = EpisodeFragment.class.getSimpleName();
	private static final int DELETE_ID = Menu.FIRST + 300;
	private static final int WATCH_ID = Menu.FIRST + 301;
	private static final int ADD_ID = Menu.FIRST + 302;

	private OnEpisodeActionListener listener = null;

	private CoverartLruMemoryCache cache;

	private String programGroup, title, subTitle;
	private Long episodeId;
	private Integer channelId;
	private DateTime startTime;
	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );

		View root = inflater.inflate( R.layout.fragment_dvr_episode, container, false );

		cache = new CoverartLruMemoryCache( getActivity() );

		Log.v( TAG, "onCreateView : exit" );
		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		setHasOptionsMenu( true );
		setRetainInstance( true );

		Log.v( TAG, "onActivityCreated : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@TargetApi( 11 )
	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		Log.v( TAG, "onCreateOptionsMenu : enter" );
		super.onCreateOptionsMenu( menu, inflater );

		MenuItem watch = menu.add( Menu.NONE, WATCH_ID, Menu.NONE, "Watch" );
		watch.setIcon( android.R.drawable.ic_menu_view );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	watch.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
	    }
		
		MenuItem add = menu.add( Menu.NONE, ADD_ID, Menu.NONE, "ADD" );
		add.setIcon( android.R.drawable.ic_menu_add );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	add.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
	    }

		MenuItem delete = menu.add( Menu.NONE, DELETE_ID, Menu.NONE, "Delete" );
		delete.setIcon( android.R.drawable.ic_menu_delete );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	delete.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
	    }

	    Log.v( TAG, "onCreateOptionsMenu : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.AbstractRecordingsActivity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );
		
		switch( item.getItemId() ) {
		case WATCH_ID:
			Log.d( TAG, "onOptionsItemSelected : watch selected" );

			Intent playerIntent = new Intent( getActivity(), VideoActivity.class );
			playerIntent.putExtra( VideoActivity.EXTRA_PROGRAM_KEY, episodeId );
			startActivity( playerIntent );

			return true;
		case ADD_ID:
			Log.d( TAG, "onOptionsItemSelected : add selected" );

	        return true;
		case DELETE_ID:
			Log.d( TAG, "onOptionsItemSelected : delete selected" );

			AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

			builder
				.setTitle( R.string.episode_alert_delete_title )
				.setMessage( R.string.episode_alert_delete_message )
				.setPositiveButton( R.string.episode_alert_delete_button_delete, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick( DialogInterface dialog, int which ) {
						
						new RemoveRecordingTask().execute();
						
					}
				} )
				.setNegativeButton( R.string.episode_alert_delete_button_cancel, null )
				.show();

			return true;
		}
		
		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	public void loadEpisode( long id ) {

		episodeId = id;
		
		String[] projection = { 
			ProgramConstants._ID, ProgramConstants.FIELD_TITLE, ProgramConstants.FIELD_SUB_TITLE,
			ProgramConstants.FIELD_DESCRIPTION, ProgramConstants.FIELD_AIR_DATE, ProgramConstants.FIELD_CHANNEL_NUMBER, 
			ProgramConstants.FIELD_CATEGORY, ProgramConstants.FIELD_START_TIME, ProgramConstants.FIELD_END_TIME,
			ProgramConstants.FIELD_CHANNEL_ID, ProgramConstants.FIELD_PROGRAM_GROUP
		};

		Cursor cursor = getActivity().getContentResolver().query( ContentUris.withAppendedId( ProgramConstants.CONTENT_URI_RECORDED, id ), projection, null, null, null );
		if( cursor.moveToFirst() ) {

			programGroup = cursor.getString( cursor.getColumnIndexOrThrow( ProgramConstants.FIELD_PROGRAM_GROUP ) );
			channelId = Integer.parseInt( cursor.getString( cursor.getColumnIndexOrThrow( ProgramConstants.FIELD_CHANNEL_ID ) ) );
			startTime = new DateTime( cursor.getLong( cursor.getColumnIndexOrThrow( ProgramConstants.FIELD_START_TIME ) ) );

			title = cursor.getString( cursor.getColumnIndexOrThrow( ProgramConstants.FIELD_TITLE ) );
			Log.d( TAG, "loadEpisode : Episode_Title=" + title );

			// get activity to grab views from
			FragmentActivity activity = this.getActivity();

			// coverart
			ImageView iView = (ImageView) activity.findViewById( R.id.imageView_episode_coverart );
			BitmapDrawable coverart = cache.get( title );
			if( null != coverart ) {
				iView.setImageDrawable( coverart );
			} else {
				iView.setImageDrawable( null );
			}

			// title
			TextView tView = (TextView) activity.findViewById( R.id.textView_episode_title );
			tView.setText( title );

			// subtitle
			subTitle = cursor.getString( cursor.getColumnIndexOrThrow( ProgramConstants.FIELD_SUB_TITLE ) );
			tView = (TextView) activity.findViewById( R.id.textView_episode_subtitle );
			tView.setText( subTitle );

			// description
			tView = (TextView) activity.findViewById( R.id.textView_episode_description );
			tView.setText( cursor.getString( cursor.getColumnIndexOrThrow( ProgramConstants.FIELD_DESCRIPTION ) ) );

			// channel number
			tView = (TextView) activity.findViewById( R.id.textView_episode_ch_num );
			tView.setText( cursor.getString( cursor.getColumnIndexOrThrow( ProgramConstants.FIELD_CHANNEL_NUMBER ) ) );

			// airdate
			tView = (TextView) activity.findViewById( R.id.textView_episode_airdate );
			tView.setText( cursor.getString( cursor.getColumnIndexOrThrow( ProgramConstants.FIELD_AIR_DATE ) ) );

		} else {
			Log.d( TAG, "loadEpisode: Empty Cursor Returned" );
		}
		cursor.close();
	}

	public void setOnEpisodeActionListener( OnEpisodeActionListener listener ) {
		Log.v( TAG, "setOnEpisodeActionListener : enter" );

		this.listener = listener;

		Log.v( TAG, "setOnEpisodeActionListener : exit" );
	}

	public interface OnEpisodeActionListener {
		void onEpisodeDeleted( String programGroup );
	}

	// internal helpers
	
	private void deleteNotify( Boolean deleted ) {
		
		Toast.makeText( getActivity(), ( "Episode" + ( !deleted ? " NOT " : " " ) + "deleted!" ), Toast.LENGTH_SHORT ).show();
		
	}
	
	private void exceptionDialolg( Throwable t ) {
		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

		builder
			.setTitle( R.string.exception )
			.setMessage( t.toString() )
			.setPositiveButton( R.string.close, null )
				.show();
	}

	private class RemoveRecordingTask extends AsyncTask<Void, Void, ResponseEntity<Bool>> {

		private Exception e = null;

		@Override
		protected ResponseEntity<Bool> doInBackground( Void... params ) {
			Log.v( TAG, "CreateStreamTask : enter" );

			ResponseEntity<String> hostname = getMainApplication().getMythServicesApi().mythOperations().getHostName();
			if( null == hostname || "".equals( hostname ) ) {
				return null;
			}

			ResponseEntity<Bool> removed = null;

			try {
				Log.v( TAG, "RemoveRecordingTask : api" );
				
				removed = getMainApplication().getMythServicesApi().dvrOperations().removeRecorded( channelId, startTime );
			} catch( Exception e ) {
				Log.v( TAG, "CreateStreamTask : error" );

				this.e = e;
			}

			Log.v( TAG, "CreateStreamTask : exit" );
			return removed;
		}

		@Override
		protected void onPostExecute( ResponseEntity<Bool> result ) {
			Log.v( TAG, "RemoveRecordingTask : onPostExecute - enter" );

			if( null == e ) {
				if( null != result ) {
					if( result.getStatusCode().equals( HttpStatus.OK ) ) {
						
						Bool bool = result.getBody();
						if( bool.getBool().equals( Boolean.TRUE ) ) {
						
							getActivity().getContentResolver().delete( ContentUris.withAppendedId( ProgramConstants.CONTENT_URI_RECORDED, episodeId ), null, null );
							listener.onEpisodeDeleted( programGroup );
							
						}
						
						deleteNotify( bool.getBool() );
					}
				}
			} else {
				Log.e( TAG, "RemoveRecordingTask : onPostExecute - error removing recording", e );
				exceptionDialolg( e );
			}

			Log.v( TAG, "RemoveRecordingTask : onPostExecute - exit" );
		}
	}

}
