/**
 * 
 */
package org.mythtv.client.ui.setup.capture;

import java.util.List;

import org.mythtv.R;
import org.mythtv.services.api.capture.CaptureCard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author Daniel Frey
 *
 */
public class CaptureCardsListAdapter extends BaseAdapter {

	private List<CaptureCard> captureCards;
	private final LayoutInflater layoutInflater;
	
	public CaptureCardsListAdapter( Context context, List<CaptureCard> captureCards ) {
		this.captureCards = captureCards;
		this.layoutInflater = LayoutInflater.from( context );
	}
	
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		if( null == captureCards ) {
			return 0;
		}
		
		return captureCards.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public CaptureCard getItem( int position ) {
		return captureCards.get( position );
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId( int position ) {
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		CaptureCard captureCard = getItem( position );
		View view = convertView;
		
		if( null == view ) {
			view = layoutInflater.inflate( R.layout.capture_cards_list_item, parent, false );
		}

		if( null != captureCard ) {
			TextView t = (TextView) view.findViewById( R.id.capture_card_list_item_video_device ); 
			t.setText( captureCard.getVideoDevice() );
			
			t = (TextView) view.findViewById( R.id.capture_card_list_item_card_type ); 
			t.setText( captureCard.getCardType() );

			t = (TextView) view.findViewById( R.id.capture_card_list_item_hostname ); 
			t.setText( captureCard.getHostName() );
		}
		
		return view;
	}

}
