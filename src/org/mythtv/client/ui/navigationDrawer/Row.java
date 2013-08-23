package org.mythtv.client.ui.navigationDrawer;

import android.view.View;

/**
 * @author dmfrey
 *
 */
public interface Row {

	public View getView( View convertView );
	
    public int getViewType();
    
    public String getTitle();
    
    public String getFragment();

}
