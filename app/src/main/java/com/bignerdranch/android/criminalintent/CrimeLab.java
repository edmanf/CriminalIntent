package com.bignerdranch.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Stores a list of crimes.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;

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
    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public void addCrime(Crime c) {

    }

    public List<Crime> getCrimes() {
        return new ArrayList<>();
    }

    public Crime getCrime(UUID id) {
        return null;
    }

    private static ContentValues getContentValues(Crime crime) {
        /*
        ContentValues are a key-value store class, specifically for SQLite
        Keys are column names, specify cols that you want to insert or update
         */
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);

        return values;
    }
}
