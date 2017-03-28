package com.example.regener.texttranslationassistant.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Regener on 07.03.2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {



    private final Context mContext;


    public DataBaseHelper(Context context){
        super(context, DBInfo.DB_NAME, null, DBInfo.DB_VERSION);
        this.mContext = context;
    }

    public void createDataBase() throws IOException {
        boolean isExistDB = checkDataBase();

        if(!isExistDB){
            this.getReadableDatabase();

            try{
                copyDataBase();
            }
            catch(IOException e){
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;

        try {
            String path = DBInfo.DB_PATH + DBInfo.DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch(SQLiteException e){

        }

        if(checkDB != null){
            checkDB.close();
        }

        return  checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException{
        // open local db
        InputStream input = mContext.getAssets().open(DBInfo.DB_ASSETS_PATH + DBInfo.DB_NAME);

        // new db path
        String outFileName = DBInfo.DB_PATH + DBInfo.DB_NAME;

        // open new bd
        OutputStream output = new FileOutputStream(outFileName);

        // move bytes
        byte[] buffer = new byte[1024];
        int length;

        while ((length = input.read(buffer)) > 0){
            output.write(buffer, 0, length);
        }
        Log.i("MY_WARNING", "copy succes");
        output.flush();
        output.close();
        input.close();
    }

    public void executeNonQuery(String query) throws SQLiteException{
        Log.i("MY_WARNING", "executeNonQuery " + query);
        //открываем БД
        String path = DBInfo.DB_PATH + DBInfo.DB_NAME;
        SQLiteDatabase dictionary = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        dictionary.execSQL(query);

        dictionary.close();
    }

    public Cursor executeReader(String query) throws SQLiteException{
        Log.i("MY_WARNING", "executeReader " + query);
        String path = DBInfo.DB_PATH + DBInfo.DB_NAME;
        SQLiteDatabase dictionary = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);

        Cursor cursor = dictionary.rawQuery(query, null);
        dictionary.close();

        return cursor;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
