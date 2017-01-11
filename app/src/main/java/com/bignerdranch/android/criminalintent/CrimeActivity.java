package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    private static final String EXTRA_CRIME_ID =
            "com.bignerdranch.android.criminalintent.crime_id";
    private static final String EXTRA_HOLDER_POSITION =
            "com.bignerdranch.android.criminalintent.holder_position";

    // Returns an intent to start the crime activity for a specific crime
    public static Intent newIntent(Context packageContext, UUID crimeID, int holderPosition) {
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeID);
        intent.putExtra(EXTRA_HOLDER_POSITION, holderPosition);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        int holderPosition = getIntent().getIntExtra(EXTRA_HOLDER_POSITION, -1);
        return CrimeFragment.newInstance(crimeId, holderPosition);
    }
}
