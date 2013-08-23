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
