/**
 * 
 */
package org.mythtv.client.ui.dvr;

import org.mythtv.R;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.db.dvr.ProgramConstants;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Daniel Frey
 * @author John Baab
 * 
 */
public class ProgramGroupFragment extends MythtvListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = ProgramGroupFragment.class.getSimpleName();

	private ProgramCursorAdapter adapter;
	
	private String programGroup = "*";
	
	public ProgramGroupFragment() { }
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
		Log.v( TAG, "onCreateLoader : enter" );
		
		String[] projection = { BaseColumns._ID, ProgramConstants.FIELD_TITLE, ProgramConstants.FIELD_SUB_TITLE };
		String[] selectionArgs = { programGroup };
		 
	    CursorLoader cursorLoader = new CursorLoader( getActivity(), ProgramConstants.CONTENT_URI, projection, ProgramConstants.FIELD_TITLE + "=?", selectionArgs, ProgramConstants.FIELD_SUB_TITLE );
	    Log.v( TAG, "onCreateLoader : cursorLoader=" + cursorLoader.toString() );
	    
	    Log.v( TAG, "onCreateLoader : exit" );
		return cursorLoader;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object)
	 */
	@Override
	public void onLoadFinished( Loader<Cursor> loader, Cursor cursor ) {
		Log.v( TAG, "onLoadFinished : enter" );
		
		adapter.swapCursor( cursor );
		
		Log.v( TAG, "onLoadFinished : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader)
	 */
	@Override
	public void onLoaderReset( Loader<Cursor> loader ) {
		Log.v( TAG, "onLoaderReset : enter" );
		
		adapter.swapCursor( null );
				
		Log.v( TAG, "onLoaderReset : exit" );
	}

	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.i( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );
	    
	    adapter = new ProgramCursorAdapter(
	            getActivity().getApplicationContext(), R.layout.program_row,
	            null, new String[] { ProgramConstants.FIELD_SUB_TITLE }, new int[] { R.id.program_sub_title },
	            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );

	    setListAdapter( adapter );

	    getLoaderManager().initLoader( 0, null, this );
		
	    Log.i( TAG, "onActivityCreated : exit" );
	}

	public void loadPrograms( String name ) {
		Log.i( TAG, "loadPrograms : enter" );

		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "loadPrograms : name=" + name );
		}
		
		programGroup = name;
		try {
			getLoaderManager().restartLoader( 0, null, this );
		} catch( Exception e ) {
			Log.w( TAG, e.getLocalizedMessage(), e );
		}

		Log.i( TAG, "loadPrograms : exit" );
	}
	
	@Override
	public void onListItemClick( ListView l, View v, int position, long id ) {
		Log.v( TAG, "onListItemClick : enter" );

		super.onListItemClick( l, v, position, id );
		
		Log.v (TAG, "onListItemClick : position=" + position + ", id=" + id );
	    
		Intent i = new Intent( getActivity(), VideoActivity.class );
		i.putExtra( VideoActivity.EXTRA_PROGRAM_KEY, id );
		startActivity( i );

		Log.v( TAG, "onListItemClick : exit" );
	}
	
	private class ProgramCursorAdapter extends SimpleCursorAdapter {

		public ProgramCursorAdapter( Context context, int layout, Cursor c, String[] from, int[] to, int flags ) {
			super( context, layout, c, from, to, flags );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			Log.v( TAG, "getView : enter" );

			View row =  super.getView( position, convertView, parent );
			
			getCursor().moveToPosition( position );
		    try {
		        int idIndex = getCursor().getColumnIndexOrThrow( BaseColumns._ID );
		        int titleIndex = getCursor().getColumnIndexOrThrow( ProgramConstants.FIELD_TITLE );
				int subTitleIndex = getCursor().getColumnIndexOrThrow( ProgramConstants.FIELD_SUB_TITLE );

		        int id = getCursor().getInt( idIndex );
		        String title = getCursor().getString( titleIndex );
		        String subTitle = getCursor().getString( subTitleIndex );

		        Log.v( TAG, "getView : id=" + id + ", title=" + title + ", subTitle=" + subTitle );
		        
				if( row == null ) {
					LayoutInflater inflater = getActivity().getLayoutInflater();

					row = inflater.inflate( R.layout.program_row, parent, false );
				}

				TextView t = (TextView) row.findViewById( R.id.program_sub_title );
				t.setText( !"".equals( subTitle ) ? subTitle : title );

		    } catch( Exception e ) {
				Log.e( TAG, "getView : error", e );
		    }
		    
			return row;
		}

	}

}
