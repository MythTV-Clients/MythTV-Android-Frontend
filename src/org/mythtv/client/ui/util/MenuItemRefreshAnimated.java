package org.mythtv.client.ui.util;

import org.mythtv.R;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;

/**
 * 
 * @author Thomas G. Kenny Jr
 *
 */
public class MenuItemRefreshAnimated extends ImageButton {

	private ObjectAnimator refreshAnimator;
	
	public MenuItemRefreshAnimated(Context context) {
		super(context);
		this.construction();
	}
	public MenuItemRefreshAnimated(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.construction();
	}
	public MenuItemRefreshAnimated(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.construction();
	}
	
	
	
	private void construction(){
		
		setBackgroundColor(0x00ffffff);
		setImageResource(R.drawable.ic_menu_refresh);
		
		refreshAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 180f);
		refreshAnimator.setDuration(500);
		refreshAnimator.setInterpolator(new LinearInterpolator());
		refreshAnimator.setRepeatMode(ValueAnimator.RESTART);
		refreshAnimator.setRepeatCount(ValueAnimator.INFINITE);
	}
	
	
	
	public void startRefreshAnimation(){
		if(null != this.refreshAnimator){
			this.refreshAnimator.setRepeatCount(ObjectAnimator.INFINITE);
			this.refreshAnimator.start();
		}
		
	}
	
	public void stopRefreshAnimation(){
		if(null != this.refreshAnimator){
			this.refreshAnimator.end();
		}
	}


}
