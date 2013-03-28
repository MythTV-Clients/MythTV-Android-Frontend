package org.mythtv.client.ui.dvr;

import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.dvr.programGroup.ProgramGroup;
import org.mythtv.db.dvr.programGroup.ProgramGroupConstants;
import org.mythtv.db.dvr.programGroup.ProgramGroupDaoHelper;
import org.mythtv.services.api.dvr.Program;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
	RecordingsFragment.OnProgramGroupListener,
	ProgramGroupFragment.OnEpisodeSelectedListener,
	EpisodeFragment.OnEpisodeActionListener {

    private static final String TAG = RecordingsParentFragment.class.getSimpleName();
    private static final String PROGRAM_GROUP_LIST_TAG = "PROGRAM_GROUP_LIST_TAG";

    private boolean mUseMultiplePanes;
    private RecordedDaoHelper mRecordedDaoHelper = RecordedDaoHelper.getInstance();
    private ProgramGroupDaoHelper mProgramGroupDaoHelper = ProgramGroupDaoHelper.getInstance();
    private RecordingsFragment mRecordingsFragment;
    private ProgramGroupFragment mProgramGroupFragment;
    private EpisodeFragment mEpisodeFragment;
    private ProgramGroup selectedProgramGroup;
    private Program selectedProgram;
    private LocationProfile mLocationProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {

	// inflate recordings activity/parent fragment view
	View view = inflater.inflate(R.layout.activity_dvr_recordings, container, false);

	

	return view;
    }

    @Override
    public void onResume() {
	super.onResume();
	
	View view = this.getView();
	
	// get child fragment manager
	FragmentManager fMan = this.getChildFragmentManager();

	// look for recording groups list placeholder framelayout
	FrameLayout fLayout = (FrameLayout) view
		.findViewById(R.id.frame_layout_recording_groups);
	if (null != fLayout) {
	    mRecordingsFragment = (RecordingsFragment) fMan
		    .findFragmentByTag(RecordingsFragment.class.getName());
	    if (null == mRecordingsFragment) {
		mRecordingsFragment = (RecordingsFragment) RecordingsFragment
			.instantiate(getActivity(),
				RecordingsFragment.class.getName());
		mRecordingsFragment.setOnProgramGroupListener(this);
	    }

	    fMan.beginTransaction()
		    .replace(R.id.frame_layout_recording_groups,
			    mRecordingsFragment,
			    RecordingsFragment.class.getName()).commit();
	}

	// look for program group placeholder framelayout
	fLayout = (FrameLayout) view
		.findViewById(R.id.frame_layout_program_group);
	if (null != fLayout) {
	    this.mUseMultiplePanes = true;

	    mProgramGroupFragment = (ProgramGroupFragment) this.findChildFragmentByIdOrTag(R.id.fragment_dvr_program_group);
	    if (null == mProgramGroupFragment) {
		mProgramGroupFragment = (ProgramGroupFragment) ProgramGroupFragment.instantiate(getActivity(), ProgramGroupFragment.class.getName());
		mProgramGroupFragment.setOnEpisodeSelectedListener(this);
	    }

	    fMan.beginTransaction()
        	    .replace(R.id.frame_layout_program_group, mProgramGroupFragment, Integer.toString(R.id.fragment_dvr_program_group))
        	    .commit();
	    
	}

	// look for program group placeholder framelayout
	fLayout = (FrameLayout) view.findViewById(R.id.frame_layout_episode);
	if (null != fLayout) {
	    this.mUseMultiplePanes = true;

	    mEpisodeFragment = (EpisodeFragment) this.findChildFragmentByIdOrTag(R.id.fragment_dvr_episode);
	    if (null == mEpisodeFragment) {
		mEpisodeFragment = (EpisodeFragment) EpisodeFragment.instantiate(getActivity(),EpisodeFragment.class.getName());
		mEpisodeFragment.setOnEpisodeActionListener(this);
	    }

	    fMan.beginTransaction()
	    	.replace(R.id.frame_layout_episode, mEpisodeFragment,Integer.toString(R.id.fragment_dvr_episode))
		.commit();
	}
	
	
	

	mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile(getActivity());

	if (mUseMultiplePanes) {
	    List<ProgramGroup> programGroups = mProgramGroupDaoHelper.findAll(getActivity(), mLocationProfile);
	    if (null != programGroups && !programGroups.isEmpty()) {
//		onProgramGroupSelected(programGroups.get(0));
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mythtv.client.ui.dvr.RecordingsFragment.OnProgramGroupListener#
     * onProgramGroupSelected(org.mythtv.db.dvr.programGroup.ProgramGroup)
     */
    public void onProgramGroupSelected(ProgramGroup programGroup) {
	Log.d(TAG, "onProgramGroupSelected : enter");

	if (null == programGroup) {
	    Log.d(TAG, "onProgramGroupSelected : exit, programGroups is empty");
	    return;
	}

	selectedProgramGroup = programGroup;
	selectedProgram = null;

	List<Program> programs = mRecordedDaoHelper.findAllByTitle(
		getActivity(), mLocationProfile, programGroup.getTitle());
	if (null == programs || programs.isEmpty()) {
	    Log.d(TAG, "onProgramGroupSelected : no programs in programGroup");
	    mRecordedDaoHelper.findAll(getActivity(), mLocationProfile);
	}

	if (null != programs && !programs.isEmpty()) {
	    selectedProgram = programs.get(0);
	}

	if (this.mUseMultiplePanes && null != mProgramGroupFragment) {
	    FragmentManager manager = this.getActivity().getSupportFragmentManager();

	    final boolean programGroupAdded = (mProgramGroupFragment != null);
	    if (programGroupAdded) {
		if (null != mProgramGroupFragment.getSelectedProgramGroup()
			&& mProgramGroupFragment.getSelectedProgramGroup().equals(programGroup)) {
		    Log.d(TAG, "onProgramGroupSelected : exit, programGroup already selected");
		    return;
		}

		mProgramGroupFragment.loadProgramGroup(programGroup);
	    } else {
		Log.v(TAG, "onProgramGroupSelected : creating new programGroupFragment");
		FragmentTransaction transaction = manager.beginTransaction();
		mProgramGroupFragment = new ProgramGroupFragment();

		if (mUseMultiplePanes) {
		    Log.v(TAG, "onProgramGroupSelected : adding to multipane");
		    transaction.add(R.id.fragment_dvr_program_group, mProgramGroupFragment, PROGRAM_GROUP_LIST_TAG);
		} else {
		    Log.v(TAG, "onProgramGroupSelected : replacing fragment");

		    transaction.replace(R.id.fragment_dvr_program_group, mProgramGroupFragment, PROGRAM_GROUP_LIST_TAG);
		    transaction.addToBackStack(null);
		}
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.commit();

		Log.v(TAG,"onProgramGroupSelected : setting program group to display");
		mProgramGroupFragment.loadProgramGroup(programGroup);
	    }

	    if (null != selectedProgram) {
		onEpisodeSelected(selectedProgram.getChannelInfo().getChannelId(), selectedProgram.getStartTime());
	    }

	} else {
	    Log.v(TAG, "onProgramGroupSelected : starting program group activity");

	    Intent i = new Intent(this.getActivity(), ProgramGroupActivity.class);
	    i.putExtra(ProgramGroupConstants.FIELD_TITLE, programGroup.getTitle());
	    startActivity(i);
	}

	Log.d(TAG, "onProgramGroupSelected : exit");
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
    public void onEpisodeSelected(int channelId, DateTime startTime) {
	Log.v(TAG, "onEpisodeSelect : enter");

	// check if we're hosting multiple fragments and have the episode
	// fragment
	if (mUseMultiplePanes && null != mEpisodeFragment) {
	    // tell the episode fragment to do it's business
	    mEpisodeFragment.loadEpisode(channelId, startTime);
	}

	Log.v(TAG, "onEpisodeSelect : exit");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mythtv.client.ui.dvr.EpisodeFragment.OnEpisodeActionListener#
     * onEpisodeDeleted(java.lang.String)
     */
    @Override
    public void onEpisodeDeleted(ProgramGroup programGroup) {
	Log.v(TAG, "onEpisodeDeleted : enter");

	mRecordingsFragment.notifyDeleted();

	selectedProgramGroup = programGroup;
	selectedProgram = null;

	List<Program> programs = mRecordedDaoHelper.findAllByTitle(getActivity(), mLocationProfile, programGroup.getTitle());
	if (null == programs || programs.isEmpty()) {
	    Log.d(TAG, "onProgramGroupSelected : exit, no programs in programGroup");
	    programs = mRecordedDaoHelper.findAll(getActivity(), mLocationProfile);
	}

	selectedProgram = programs.get(0);
	onEpisodeSelected(selectedProgram.getChannelInfo().getChannelId(), selectedProgram.getStartTime());

	Log.v(TAG, "onEpisodeDeleted : exit");
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
