package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Stores a list of crimes.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;

    private  List<Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();

        /* getWritableDatabase() does:
        1. open /data/data/com.bignerdranch.android.criminalintent/
            databases/crimeBase.db
            creates a new db if it doesn't exist
        2. If this is frist create, call onCreate(SQLiteDatabase),
            then save latest version #
        3. If not, check version # in DB. If version # in CrimeOpenHelper is higher,
            call onUpgrade(SQLiteDatabase, int, int)
         */
        mDatabase = new CrimeBaseHelper(mContext)
                .getWritableDatabase();
        mCrimes = new ArrayList<Crime>(); //new ArrayList<>() is okay in java 7+
    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public void addCrime(Crime c) {
        mCrimes.add(c);
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(id)) {
                return crime;
            }
        }
        return null;
    }
}
