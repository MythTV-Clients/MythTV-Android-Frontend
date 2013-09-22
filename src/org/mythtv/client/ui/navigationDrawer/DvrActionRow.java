/**
 * 
 */
package org.mythtv.client.ui.navigationDrawer;

import org.mythtv.R;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

/**
 * @author dmfrey
 *
 */
public class DvrActionRow extends ActionRow {

	public DvrActionRow( Context context, int actionResId ) {
		super( context, actionResId, true );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.ActionRow#getView(android.view.View)
	 */
	@Override
	public View getView( View convertView ) {
		View view = super.getView( convertView );
		
		ImageView imgView = (ImageView)view.findViewById( R.id.navigation_drawer_action_icon );
		imgView.setImageResource( R.drawable.ic_recordings_default );
		
		return view;
	}
}
