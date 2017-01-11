package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Launch activity. Shows a list of crimes.
 */

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
