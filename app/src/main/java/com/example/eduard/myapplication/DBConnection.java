package com.example.eduard.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eduard on 12.05.2017.
 */

public class DBConnection extends SQLiteOpenHelper {
    private static String DB_NAME = "mobappdev.db";
    private static String DB_PATH = "";
    private final Context mContext;
    private SQLiteDatabase mDataBase;

    private static String createDB = "CREATE DATABASE test ( id integer, name nvarchar(255))";

    public DBConnection(Context context) {
        super(context, DB_NAME, null, 1);

        if(android.os.Build.VERSION.SDK_INT >= 17){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        }
        else
        {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;

        try {
            this.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.openDataBase();

    }

    public void createDataBase() throws IOException
    {
        //If the database does not exist, copy it from the assets.

        boolean mDataBaseExist = checkDataBase();
        if(!mDataBaseExist)
        {
            this.getReadableDatabase();
            this.close();
            try
            {
                //Copy the database from assests
                copyDataBase();
              //  Log.e(TAG, "createDatabase database created");
            }
            catch (IOException mIOException)
            {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    //Copy the database from assets
    private void copyDataBase() throws IOException
    {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0)
        {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Check that the database exists here: /data/data/your package/databases/Da Name
    private boolean checkDataBase()
    {
        File dbFile = new File(DB_PATH + DB_NAME);
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    //Open the database, so we can query it
    public boolean openDataBase() throws SQLException
    {
        String mPath = DB_PATH + DB_NAME;
        //Log.v("mPath", mPath);
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return mDataBase != null;
    }

    public List<String> getAllToDos() throws Exception{

        List<Pair<String, String>> liste = mDataBase.getAttachedDbs();
        mDataBase = this.getWritableDatabase();

        Cursor cursor = mDataBase.rawQuery("SELECT * FROM todo",null);
        int columnIndex = cursor.getColumnIndexOrThrow("ID");
        List<String> resultList = new ArrayList<>();
       if (cursor.moveToFirst()) {
            do{
                resultList.add(cursor.getString(columnIndex));
            }while(cursor.moveToNext());

        }
        return resultList;
    }
}
