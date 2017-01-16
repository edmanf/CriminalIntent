package com.bignerdranch.android.criminalintent.database;

/**
 * Contains definitions of string constants used to describe the moving
 * pieces of the table definition
 */

public class CrimeDbSchema {

    /**
     * Name of the database. Access with CrimeTable.NAME
     */
    public static final class CrimeTable {
        public static final String NAME = "crimes";
    }

    /**
     * Definitions for database columns. Access with CrimeTable.Cols.TITLE
     */
    public static final class Cols {
        public static final String UUID = "uuid";
        public static final String TITLE = "title";
        public static final String DATE = "date";
        public static final String SOLVED = "solved";
    }


}
