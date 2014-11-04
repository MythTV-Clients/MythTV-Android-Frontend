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
package org.mythtv.client.ui.util;

import org.mythtv.R;

import android.animation.AnimatorSet;
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

	private AnimatorSet mRotateAnimatorSet;
	
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
		
		ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);
		rotateAnimator.setDuration(500);
		rotateAnimator.setInterpolator(new LinearInterpolator());
		rotateAnimator.setRepeatMode(ValueAnimator.RESTART);
		rotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
		
		ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0.30f);
		alphaAnimator.setDuration(500);
		alphaAnimator.setInterpolator(new LinearInterpolator());
		alphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
		alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
		
		mRotateAnimatorSet = new AnimatorSet();
		mRotateAnimatorSet.play(rotateAnimator).with(alphaAnimator);
	}
	
	
	
	public void startRefreshAnimation(){
		this.setImageResource(R.drawable.ic_menu_refresh_default);
		if(null != this.mRotateAnimatorSet){
			this.mRotateAnimatorSet.start();
		}
		
	}
	
	public void stopRefreshAnimation(){
		if(null != this.mRotateAnimatorSet){
			this.mRotateAnimatorSet.cancel();
		}
		this.setRotation(0f);
		this.setImageResource(R.drawable.ic_menu_refresh);
		this.setAlpha(1f);
	}


}
