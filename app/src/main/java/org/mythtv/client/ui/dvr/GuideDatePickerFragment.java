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
/**
 * 
 */
package org.mythtv.client.ui.dvr;

import org.joda.time.DateTime;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

/**
 * @author dmfrey
 *
 */
public class GuideDatePickerFragment extends DialogFragment implements OnDateSetListener {

	private static final String TAG = GuideDatePickerFragment.class.getSimpleName();
	
	private DateTime selectedDate;
	private int downloadDays;
	
	private OnDialogResultListener listener;		
	
	public interface OnDialogResultListener {
	
		public void onDateChanged( DateTime selectedDate );
	
	}
	
	public void setOnDialogResultListener( OnDialogResultListener listener ) {
		this.listener = listener;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		
		if( null == listener ) {
			throw new IllegalArgumentException( "OnDialogResultListener is required!" );
		}
		
		Bundle args = getArguments();
		selectedDate = new DateTime( args.getLong( "selectedDate" ) );
		downloadDays = args.getInt( "downloadDays" );
		Log.v( TAG, "onCreate : selectedDate=" + selectedDate.toString() + ", downloadDays=" + downloadDays );
		
		// Create a new instance of DatePickerDialog and return it
		DatePickerDialog datePickerDialog = new DatePickerDialog( getActivity(), this, selectedDate.getYear(), selectedDate.getMonthOfYear() - 1, selectedDate.getDayOfMonth() );
		
		if( null != datePickerDialog.getDatePicker() ) {
			DateTime today = new DateTime().withTimeAtStartOfDay();
			Log.v( TAG, "onCreate : today=" + today.toString() );
//			datePickerDialog.getDatePicker().setMinDate( today.getMillis() );
			Log.v( TAG, "onCreate : today+downloadDays=" + today.plusDays( downloadDays ).toString() );
//			datePickerDialog.getDatePicker().setMaxDate( today.plusDays( downloadDays ).getMillis() );
		}
		
		Log.v( TAG, "onCreate : exit" );
		return datePickerDialog;
	}

	public void onDateSet( DatePicker view, int year, int month, int day ) {
		Log.v( TAG, "onDateSet : enter" );

		selectedDate = new DateTime().withYear( year ).withMonthOfYear( month + 1 ).withDayOfMonth( day ).withTimeAtStartOfDay();
		Log.v( TAG, "onDateSet : new selectedDate=" + selectedDate.toString() );

		listener.onDateChanged( selectedDate );
		
		Log.v( TAG, "onDateSet : exit" );
	}

}
