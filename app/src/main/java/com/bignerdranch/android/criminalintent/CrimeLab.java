package com.bignerdranch.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper;
import com.bignerdranch.android.criminalintent.database.CrimeCursorWrapper;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.io.File;
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
        /* Applications last longer than activities (duh)
        Singletons last until the entire application is destroyed
        Since CrimeLab has a ref to mContext, if its an activity,
            dead activities won't be garbage collected

         */
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
        ContentValues values = getContentValues(c);

        // insert(table name, hack thing (leave as null), CoontentValue data)
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return crimes;
    }

    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
            );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    /**
     * Returns a file handle for the crime photo.
     * @param crime The crime associated with the returned file.
     * @return The file handle for the photo of the crime, or null if there
     *         isn't external storage on the device.
     */
    public File getPhotoFile(Crime crime) {
        File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Check if the device has external storage available.
        if (externalFilesDir == null) {
            return null;
        }

        // Does not create a file.
        return new File(externalFilesDir, crime.getPhotoFilename());
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        // update(table name, data, where clause)
        // ? treats String as a value, not SQL code
        mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    /**
     * Returns a ContentValue for a specific crime, which can be used for
     * writing to a CrimeDb
     * @param crime The crime that needs to be written
     * @return The ContentValue that represents the crime.
     */
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
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, // null selects all cols
                whereClause,
                whereArgs,
                null,   // groupBy
                null,   // having
                null    // orderBy
        );

        return new CrimeCursorWrapper(cursor);
    }
}
