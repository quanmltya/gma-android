package com.expidevapps.android.measurements.support.v4.content;

import static com.expidevapps.android.measurements.Constants.PREFS_SETTINGS;
import static com.expidevapps.android.measurements.Constants.PREF_ACTIVE_MINISTRY;
import static org.ccci.gto.android.common.db.AbstractDao.bindValues;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.expidevapps.android.measurements.db.Contract;
import com.expidevapps.android.measurements.db.GmaDao;
import com.expidevapps.android.measurements.model.Assignment;
import com.expidevapps.android.measurements.model.Ministry;
import com.expidevapps.android.measurements.sync.BroadcastUtils;

import org.ccci.gto.android.common.support.v4.content.AsyncTaskBroadcastReceiverSharedPreferencesChangeLoader;

import java.util.EnumSet;
import java.util.List;

public class CurrentAssignmentLoader extends AsyncTaskBroadcastReceiverSharedPreferencesChangeLoader<Assignment> {
    public static final String ARG_LOAD_MINISTRY = CurrentAssignmentLoader.class.getSimpleName() + ".ARG_LOAD_MINISTRY";

    private final GmaDao mDao;

    @NonNull
    private final String mGuid;
    private final boolean mLoadMinistry;

    public CurrentAssignmentLoader(@NonNull final Context context, @NonNull final String guid,
                                   @Nullable final Bundle args) {
        super(context, context.getSharedPreferences(PREFS_SETTINGS(guid), Context.MODE_PRIVATE));
        mDao = GmaDao.getInstance(context);
        mGuid = guid;
        mLoadMinistry = args != null && args.getBoolean(ARG_LOAD_MINISTRY, false);

        // setup listeners for events
        addPreferenceKey(PREF_ACTIVE_MINISTRY);
        addIntentFilter(BroadcastUtils.updateAssignmentsFilter(mGuid));
    }

    @Override
    public Assignment loadInBackground() {
        // load the current active assignment
        final String ministryId = mPrefs.getString(PREF_ACTIVE_MINISTRY, Ministry.INVALID_ID);
        Assignment assignment = mDao.find(Assignment.class, mGuid, ministryId);

        // reset to default assignment if a current current assignment isn't found
        if (assignment == null) {
            assignment = initActiveAssignment();
        }

        // do some additional processing if we have an assignment
        if (assignment != null) {
            // load the associated ministry if required
            if (mLoadMinistry) {
                loadMinistry(assignment);
            }

            // set an MCC if a valid one is not already selected
            if (assignment.getMcc() == Ministry.Mcc.UNKNOWN || (assignment.getMinistry() != null &&
                    !assignment.getMinistry().getMccs().contains(assignment.getMcc()))) {
                updateMcc(assignment);
            }
        }

        // return the assignment
        return assignment;
    }

    private Assignment initActiveAssignment() {
        final List<Assignment> assignments =
                mDao.get(Assignment.class, Contract.Assignment.SQL_WHERE_GUID, bindValues(mGuid));

        // short-circuit if there are no assignments for the current user
        if (assignments.size() == 0) {
            return null;
        }

        // find the default assignment based on role
        Assignment assignment = null;
        for (final Assignment current : assignments) {
            // XXX: this currently relies on Roles being ordered from most important to least important
            if (assignment == null || current.getRole().ordinal() < assignment.getRole().ordinal()) {
                assignment = current;
            }
        }
        assert assignment != null : "there is at least 1 assignment in assignments, so this should never be null";

        // set an MCC if one is not already selected
        if (assignment.getMcc() == Ministry.Mcc.UNKNOWN) {
            updateMcc(assignment);
        }

        mPrefs.edit().putString(PREF_ACTIVE_MINISTRY, assignment.getMinistryId()).apply();

        // return the found assignment
        return assignment;
    }

    private void updateMcc(@NonNull final Assignment assignment) {
        loadMinistry(assignment);

        // set the MCC based off of what is available for the ministry
        final Ministry ministry = assignment.getMinistry();
        if (ministry != null) {
            // pick a "random" MCC
            final EnumSet<Ministry.Mcc> mccs = ministry.getMccs();
            final Ministry.Mcc mcc = mccs.size() > 0 ? mccs.iterator().next() : Ministry.Mcc.UNKNOWN;

            // update the assignment if the mcc is changing
            if (mcc != assignment.getMcc()) {
                assignment.setMcc(mcc);
                mDao.update(assignment, new String[] {Contract.Assignment.COLUMN_MCC});

                // broadcast the MCC update
                final LocalBroadcastManager bm = LocalBroadcastManager.getInstance(getContext());
                bm.sendBroadcast(BroadcastUtils.updateAssignmentsBroadcast(mGuid));
            }
        }
    }

    private void loadMinistry(@NonNull final Assignment assignment) {
        if (assignment.getMinistry() == null) {
            assignment.setMinistry(mDao.find(Ministry.class, assignment.getMinistryId()));
        }
    }
}
