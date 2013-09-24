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
package org.mythtv.client.ui.dvr;

import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.dvr.model.Program;
import org.mythtv.db.dvr.programGroup.ProgramGroup;
import org.mythtv.db.dvr.programGroup.ProgramGroupConstants;
import org.mythtv.db.dvr.programGroup.ProgramGroupDaoHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 
 * @author Thomas G. Kenny Jr
 * 
 */
public class RecordingsParentFragment extends AbstractMythFragment implements
		RecordingsFragment.OnProgramGroupListener, ProgramGroupFragment.OnEpisodeSelectedListener,
		EpisodeFragment.OnEpisodeActionListener {

	private static final String TAG = RecordingsParentFragment.class.getSimpleName();
	private static final String PROGRAM_GROUP_LIST_TAG = "PROGRAM_GROUP_LIST_TAG";

	private FragmentManager mFragmentManager;

	private boolean mUseMultiplePanes;

	private ProgramGroupDaoHelper mProgramGroupDaoHelper = ProgramGroupDaoHelper.getInstance();
	private RecordedDaoHelper mRecordedDaoHelper = RecordedDaoHelper.getInstance();
	private RecordingsFragment mRecordingsFragment;

	private ProgramGroupFragment mProgramGroupFragment;
	private EpisodeFragment mEpisodeFragment;

	private ProgramGroup selectedProgramGroup;
	private Program selectedProgram;
	private LocationProfile mLocationProfile;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );

		// inflate recordings activity/parent fragment view
		View view = inflater.inflate( R.layout.activity_dvr_recordings, container, false );

		Log.v( TAG, "onCreateView : exit" );
		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );

		View view = getView();

		// get child fragment manager
		mFragmentManager = this.getChildFragmentManager();

		// look for recording groups list placeholder framelayout
		FrameLayout recordingGroupsLayout = (FrameLayout) view.findViewById( R.id.frame_layout_recording_groups );
		if( null != recordingGroupsLayout ) {
			mRecordingsFragment = (RecordingsFragment) mFragmentManager.findFragmentByTag( RecordingsFragment.class
					.getName() );
			if( null == mRecordingsFragment ) {
				mRecordingsFragment = (RecordingsFragment) RecordingsFragment.instantiate( getActivity(),
						RecordingsFragment.class.getName() );
				mRecordingsFragment.setOnProgramGroupListener( this );
			}

			mFragmentManager
					.beginTransaction()
					.replace( R.id.frame_layout_recording_groups, mRecordingsFragment,
							RecordingsFragment.class.getName() ).commit();
		}

		// look for program group placeholder framelayout
		FrameLayout programGroupLayout = (FrameLayout) view.findViewById( R.id.frame_layout_program_group );
		if( null != programGroupLayout ) {
			this.mUseMultiplePanes = true;

			mProgramGroupFragment = (ProgramGroupFragment) mFragmentManager
					.findFragmentByTag( ProgramGroupFragment.class.getName() );
			if( null == mProgramGroupFragment ) {
				mProgramGroupFragment = (ProgramGroupFragment) ProgramGroupFragment.instantiate( getActivity(),
						ProgramGroupFragment.class.getName() );
				mProgramGroupFragment.setOnEpisodeSelectedListener( this );
			}

			mFragmentManager
					.beginTransaction()
					.replace( R.id.frame_layout_program_group, mProgramGroupFragment,
							ProgramGroupFragment.class.getName() ).commit();

		}

		// look for program group placeholder framelayout
		FrameLayout episodeLayout = (FrameLayout) view.findViewById( R.id.frame_layout_episode );
		if( null != episodeLayout ) {
			this.mUseMultiplePanes = true;

			mEpisodeFragment = (EpisodeFragment) mFragmentManager.findFragmentByTag( EpisodeFragment.class.getName() );
			if( null == mEpisodeFragment ) {
				mEpisodeFragment = (EpisodeFragment) EpisodeFragment.instantiate( getActivity(),
						EpisodeFragment.class.getName() );
				mEpisodeFragment.setOnEpisodeActionListener( this );
			}

			mFragmentManager.beginTransaction()
					.replace( R.id.frame_layout_episode, mEpisodeFragment, EpisodeFragment.class.getName() ).commit();
		}

		// if( mUseMultiplePanes ) {
		//
		// List<ProgramGroup> programGroups = mProgramGroupDaoHelper.findAll(
		// getActivity(), mLocationProfile );
		// if( null != programGroups && !programGroups.isEmpty() ) {
		//
		// onProgramGroupSelected( programGroups.get( 0 ) );
		//
		// }
		//
		// }

		Log.v( TAG, "onActivityCreated : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mythtv.client.ui.dvr.RecordingsFragment.OnProgramGroupListener#
	 * onProgramGroupSelected(org.mythtv.db.dvr.programGroup.ProgramGroup)
	 */
	public void onProgramGroupSelected( ProgramGroup programGroup ) {
		Log.d( TAG, "onProgramGroupSelected : enter" );

		if( null == programGroup ) {
			Log.d( TAG, "onProgramGroupSelected : exit, programGroups is empty" );
			return;
		}

		// leave if fragment is not added to activity
		if( !isAdded() )
			return;

		selectedProgramGroup = programGroup;
		selectedProgram = null;

		List<Program> programs = null;
		if( "All".equals( selectedProgramGroup.getProgramGroup() ) ) {
			programs = mRecordedDaoHelper.findAll( getActivity(), mLocationProfile );
		} else {
			programs = mRecordedDaoHelper.findAllByTitle( getActivity(), mLocationProfile, programGroup.getTitle() );
		}

		if( null == programs || programs.isEmpty() ) {
			Log.d( TAG, "onProgramGroupSelected : no programs in programGroup" );
			mRecordedDaoHelper.findAll( getActivity(), mLocationProfile );
		}

		if( null != programs && !programs.isEmpty() ) {
			selectedProgram = programs.get( 0 );
		}

		if( this.mUseMultiplePanes && null != mProgramGroupFragment ) {
			FragmentManager manager = getActivity().getSupportFragmentManager();

			final boolean programGroupAdded = ( mProgramGroupFragment != null );
			if( programGroupAdded ) {
				if( null != mProgramGroupFragment.getSelectedProgramGroup()
						&& mProgramGroupFragment.getSelectedProgramGroup().equals( programGroup ) ) {
					Log.d( TAG, "onProgramGroupSelected : exit, programGroup already selected" );
					return;
				}

				mProgramGroupFragment.loadProgramGroup( programGroup );
			} else {
				Log.v( TAG, "onProgramGroupSelected : creating new programGroupFragment" );
				FragmentTransaction transaction = manager.beginTransaction();
				mProgramGroupFragment = new ProgramGroupFragment();

				if( mUseMultiplePanes ) {
					Log.v( TAG, "onProgramGroupSelected : adding to multipane" );
					transaction.add( R.id.fragment_dvr_program_group, mProgramGroupFragment, PROGRAM_GROUP_LIST_TAG );
				} else {
					Log.v( TAG, "onProgramGroupSelected : replacing fragment" );

					transaction.replace( R.id.fragment_dvr_program_group, mProgramGroupFragment, PROGRAM_GROUP_LIST_TAG );
					transaction.addToBackStack( null );
				}
				transaction.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN );
				transaction.commit();

				Log.v( TAG, "onProgramGroupSelected : setting program group to display" );
				mProgramGroupFragment.loadProgramGroup( programGroup );
			}

			if( null != selectedProgram ) {
				onEpisodeSelected( selectedProgram.getChannelInfo().getChannelId(), selectedProgram.getStartTime() );
			}

		} else {
			Log.v( TAG, "onProgramGroupSelected : starting program group activity" );

			Intent i = new Intent( this.getActivity(), ProgramGroupActivity.class );
			i.putExtra( ProgramGroupConstants.FIELD_TITLE, programGroup.getTitle() );
			startActivity( i );
		}

		Log.d( TAG, "onProgramGroupSelected : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mythtv.client.ui.dvr.ProgramGroupFragment.OnEpisodeSelectedListener
	 * #onEpisodeSelected(java.lang.Long, org.joda.time.DateTime)
	 * 
	 * This is called when an episode is selected in the ProgramGroupFragment.
	 * The ProgramGroupFragment will only be visible during this activities life
	 * cycle on larger screens.
	 */
	@Override
	public void onEpisodeSelected( int channelId, DateTime startTime ) {
		Log.v( TAG, "onEpisodeSelect : enter" );

		Log.v( TAG, "onEpisodeSelect : channelId=" + channelId + ", startTime=" + startTime.getMillis() );
		
		// check if we're hosting multiple fragments and have the episode
		// fragment
		if( mUseMultiplePanes && null != mEpisodeFragment ) {
			// tell the episode fragment to do it's business
			mEpisodeFragment.loadEpisode( channelId, startTime );
		}

		Log.v( TAG, "onEpisodeSelect : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mythtv.client.ui.dvr.EpisodeFragment.OnEpisodeActionListener#
	 * onEpisodeDeleted(java.lang.String)
	 */
	@Override
	public void onEpisodeDeleted( ProgramGroup programGroup ) {
		Log.v( TAG, "onEpisodeDeleted : enter" );

		mRecordingsFragment.notifyDeleted();

		if( null == programGroup ) {
			programGroup = mProgramGroupDaoHelper.findByTitle( getActivity(), mLocationProfile, "All" );
		}

		selectedProgramGroup = programGroup;
		selectedProgram = null;

		List<Program> programs = mRecordedDaoHelper.findAllByTitle( getActivity(), mLocationProfile, programGroup.getTitle() );
		if( null == programs || programs.isEmpty() ) {
			Log.d( TAG, "onProgramGroupSelected : exit, no programs in programGroup" );
			programs = mRecordedDaoHelper.findAll( getActivity(), mLocationProfile );
		}

		if( null != programs && !programs.isEmpty() ) {
			selectedProgram = programs.get( 0 );
			onProgramGroupSelected( programGroup );
			onEpisodeSelected( selectedProgram.getChannelInfo().getChannelId(), selectedProgram.getStartTime() );
		}
		
		Log.v( TAG, "onEpisodeDeleted : exit" );
	}

	/**
	 * @return
	 */
	public ProgramGroup getSelectedProgramGroup() {
		return selectedProgramGroup;
	}

	/**
	 * @return
	 */
	public Program getSelectedProgram() {
		return selectedProgram;
	}
}
