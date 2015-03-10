package com.expidev.gcmapp.support.v4.fragment.measurement;

import static com.expidev.gcmapp.Constants.ARG_GUID;
import static com.expidev.gcmapp.Constants.ARG_MCC;
import static com.expidev.gcmapp.Constants.ARG_MINISTRY_ID;
import static com.expidev.gcmapp.Constants.ARG_PERIOD;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.expidev.gcmapp.R;
import com.expidev.gcmapp.model.Ministry;
import com.expidev.gcmapp.model.measurement.MeasurementType;

import org.joda.time.YearMonth;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

public class ColumnsListFragment extends Fragment {
    @Nullable
    @Optional
    @InjectView(R.id.faithHeader)
    View mFaithHeader;
    @Nullable
    @Optional
    @InjectView(R.id.fruitHeader)
    View mFruitHeader;
    @Nullable
    @Optional
    @InjectView(R.id.outcomesHeader)
    View mOutcomesHeader;

    @Nullable
    @Optional
    @InjectView(R.id.faithContent)
    View mFaithContent;
    @Nullable
    @Optional
    @InjectView(R.id.fruitContent)
    View mFruitContent;
    @Nullable
    @Optional
    @InjectView(R.id.outcomesContent)
    View mOutcomesContent;

    private String mGuid;
    private String mMinistryId = Ministry.INVALID_ID;
    private Ministry.Mcc mMcc = Ministry.Mcc.UNKNOWN;
    private YearMonth mPeriod;

    public static ColumnsListFragment newInstance(@NonNull final String guid, @NonNull final String ministryId,
                                                  @NonNull final Ministry.Mcc mcc, @NonNull final YearMonth period) {
        final ColumnsListFragment fragment = new ColumnsListFragment();

        final Bundle args = new Bundle();
        args.putString(ARG_GUID, guid);
        args.putString(ARG_MINISTRY_ID, ministryId);
        args.putString(ARG_MCC, mcc.toString());
        args.putString(ARG_PERIOD, period.toString());
        fragment.setArguments(args);

        return fragment;
    }

    /* BEGIN lifecycle */

    @Override
    public void onCreate(final Bundle savedState) {
        super.onCreate(savedState);

        // process arguments
        final Bundle args = this.getArguments();
        mGuid = args.getString(ARG_GUID);
        mMinistryId = args.getString(ARG_MINISTRY_ID);
        mMcc = Ministry.Mcc.fromRaw(args.getString(ARG_MCC));
        mPeriod = YearMonth.parse(args.getString(ARG_PERIOD));
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedState) {
        final View view = inflater.inflate(R.layout.fragment_measurement_columns_accordion, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedState) {
        super.onViewCreated(view, savedState);
        setupMeasurementFragments();
    }

    @Optional
    @OnClick({R.id.faithHeader, R.id.fruitHeader, R.id.outcomesHeader})
    void onToggleSection(@NonNull final View view) {
        if (mFaithContent != null) {
            mFaithContent.setVisibility(mFaithHeader == view ? View.VISIBLE : View.GONE);
        }
        if (mFruitContent != null) {
            mFruitContent.setVisibility(mFruitHeader == view ? View.VISIBLE : View.GONE);
        }
        if (mOutcomesContent != null) {
            mOutcomesContent.setVisibility(mOutcomesHeader == view ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /* END lifecycle */

    private void setupMeasurementFragments() {
        final MeasurementsPagerFragment faithFragment = mFaithContent != null ?
                MeasurementsPagerFragment.newInstance(mGuid, mMinistryId, mMcc, mPeriod, MeasurementType.Column.FAITH) :
                null;
        final MeasurementsPagerFragment fruitFragment = mFruitContent != null ?
                MeasurementsPagerFragment.newInstance(mGuid, mMinistryId, mMcc, mPeriod, MeasurementType.Column.FRUIT) :
                null;
        final MeasurementsPagerFragment outcomesFragment = mOutcomesContent != null ? MeasurementsPagerFragment
                .newInstance(mGuid, mMinistryId, mMcc, mPeriod, MeasurementType.Column.OUTCOME) : null;

        final FragmentTransaction tx = getChildFragmentManager().beginTransaction();
        if (faithFragment != null) {
            tx.replace(R.id.faithPagerFragment, faithFragment);
        }
        if (fruitFragment != null) {
            tx.replace(R.id.fruitPagerFragment, fruitFragment);
        }
        if (outcomesFragment != null) {
            tx.replace(R.id.outcomesPagerFragment, outcomesFragment);
        }
        tx.commit();
    }
}