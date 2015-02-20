package com.expidev.gcmapp.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.expidev.gcmapp.db.Contract;
import com.expidev.gcmapp.sql.TableNames;

/**
 * Created by William.Randall on 1/15/2015.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper
{
    private final String TAG = getClass().getSimpleName();

    /*
     * Version history
     *
     * 8: 2015-02-19
     */
    private static final int DATABASE_VERSION = 8;
    private static final String DATABASE_NAME = "gcm_data.db";

    private static final Object LOCK_INSTANCE = new Object();
    private static DatabaseOpenHelper instance;

    private DatabaseOpenHelper(final Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public static DatabaseOpenHelper getInstance(Context context)
    {
        synchronized (LOCK_INSTANCE) {
            if (instance == null) {
                instance = new DatabaseOpenHelper(context.getApplicationContext());
            }
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.i(TAG, "Creating database...");
        createAssociatedMinistryTable(db);
        createUserTable(db);
        createAssignmentsTable(db);
        createAllMinistriesTable(db);
        createTrainingTables(db);
        db.execSQL(Contract.Church.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(@NonNull final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        if (oldVersion < 7) {
            // version is too old, reset database
            resetDatabase(db);
            return;
        }

        // perform upgrade in increments
        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 8:
                    db.execSQL(Contract.Church.SQL_CREATE_TABLE);
                    break;
                default:
                    // unrecognized version, let's just reset the database and return
                    resetDatabase(db);
                    return;
            }

            // perform next upgrade increment
            upgradeTo++;
        }
    }

    @Override
    public void onDowngrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // reset the database, don't try and downgrade tables
        this.resetDatabase(db);
    }

    private void resetDatabase(final SQLiteDatabase db) {
        db.execSQL(Contract.Church.SQL_DELETE_TABLE);
        deleteAllTables(db);
        onCreate(db);
    }

    /**
     * This table holds information for ministries the current user
     * has already joined or requested to join.
     */
    private void createAssociatedMinistryTable(SQLiteDatabase db)
    {
        db.execSQL(Contract.AssociatedMinistry.SQL_CREATE_TABLE);
    }

    /**
     * This table holds information for assignments the current user
     * has to existing ministries/teams. This is closely related to the
     * Associated Ministries table, where every assignment will have an
     * associated ministry.
     */
    private void createAssignmentsTable(SQLiteDatabase db)
    {
        db.execSQL(Contract.Assignment.SQL_CREATE_TABLE);
    }

    /**
     * This table holds the user information.
     */
    private void createUserTable(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TableNames.USER.getTableName() +
            "(first_name TEXT, last_name TEXT, cas_username TEXT, person_id TEXT);");
    }

    private void createTrainingTables(SQLiteDatabase db)
    {
        db.execSQL(Contract.Training.SQL_CREATE_TABLE);
        db.execSQL(Contract.Training.Completion.SQL_CREATE_TABLE);
    }

    /**
     * This table holds information for all ministries on the server
     * that are visible for the autocomplete text field on the
     * Join Ministry page.
     */
    private void createAllMinistriesTable(SQLiteDatabase db)
    {
        db.execSQL(Contract.Ministry.SQL_CREATE_TABLE);
    }

    private void deleteAllTables(SQLiteDatabase db)
    {
        db.execSQL(Contract.Training.Completion.SQL_DELETE_TABLE);
        db.execSQL(Contract.Training.SQL_DELETE_TABLE);
        db.execSQL(Contract.AssociatedMinistry.SQL_DELETE_TABLE);
        db.execSQL(Contract.Ministry.SQL_DELETE_TABLE);
        db.execSQL(Contract.Assignment.SQL_DELETE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TableNames.USER.getTableName());
    }
}
