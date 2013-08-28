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
public class ManageProfilesActionRow extends ActionRow {

	public ManageProfilesActionRow( Context context, String action ) {
		super( context, action );
	}
	
	@Override
	public View getView(View convertView) {
		View view = super.getView(convertView);
		
		ImageView imgView = (ImageView)view.findViewById(R.id.navigation_drawer_action_icon);
		imgView.setImageResource(R.drawable.ic_menu_preferences);
		
		return view;
	}

}
