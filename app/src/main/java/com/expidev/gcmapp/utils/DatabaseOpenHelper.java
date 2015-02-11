package com.expidev.gcmapp.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.expidev.gcmapp.db.Contract;
import com.expidev.gcmapp.sql.TableNames;

/**
 * Created by William.Randall on 1/15/2015.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper
{
    private final String TAG = getClass().getSimpleName();
    
    private static DatabaseOpenHelper instance;
    private Context context;

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "gcm_data.db";

    private DatabaseOpenHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    
    public static DatabaseOpenHelper getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new DatabaseOpenHelper(context.getApplicationContext());
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // TODO: implement db upgrade logic once we have actual users with actual data
        resetDatabase(db);
    }

    @Override
    public void onDowngrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // reset the database, don't try and downgrade tables
        this.resetDatabase(db);
    }

    private void resetDatabase(final SQLiteDatabase db) {
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
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TableNames.ASSIGNMENTS.getTableName() + "( " +
            "id TEXT PRIMARY KEY, " +
            "team_role TEXT, " +               // Team Role of the current user for this ministry/team
            "ministry_id TEXT, " +
            "latitude DECIMAL, " +
            "longitude DECIMAL, " +
            "location_zoom INTEGER, " +
            "last_synced TEXT, " +             // Last time this information was synced with the web
            "FOREIGN KEY(ministry_id) REFERENCES " + TableNames.ASSOCIATED_MINISTRIES.getTableName() + "(ministry_id));");
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
        db.execSQL("DROP TABLE IF EXISTS " + TableNames.ASSIGNMENTS.getTableName());
        db.execSQL("DROP TABLE IF EXISTS " + TableNames.USER.getTableName());
    }
}
