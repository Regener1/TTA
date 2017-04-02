package com.example.regener.texttranslationassistant;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.regener.texttranslationassistant.logic.SimpleFileDialog;
import com.example.regener.texttranslationassistant.manager.DBInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class FavouriteActivity extends ActionBarActivity {
    private SimpleCursorAdapter mTranslateAdaptor = null;
    private SQLiteDatabase mDatabase = null;
    private ListView mListViewWords;

    private final String mDbPath = DBInfo.DB_PATH + DBInfo.DB_NAME;
    private StringBuilder sbText = new StringBuilder();
    private SimpleFileDialog mFileDialogSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        setTitle(R.string.drawer_item_favourite);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarFavourite);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mListViewWords = (ListView) findViewById(R.id.listViewFavouriteWords);

        mFileDialogSave = new SimpleFileDialog(FavouriteActivity.this, "FileSave",
                new SimpleFileDialog.SimpleFileDialogListener() {
                    @Override
                    public void onChosenDir(String chosenDir) {
                        try {
                            OutputStream outputStream = new FileOutputStream(new File(chosenDir), true);
                            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
                            osw.write(sbText.toString());
                            osw.close();

                            Log.d("my_log", "Файл записан");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mDatabase = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READONLY);

            Cursor cursor = mDatabase.rawQuery("select * from " + DBInfo.TABLE_FAVOURITE, null);

            String[] from = {DBInfo.KEY_WORDS_WORD};
            int[] to = {R.id.textViewWordItem};

            mTranslateAdaptor = new SimpleCursorAdapter(
                    this,
                    R.layout.translator_item,
                    cursor,
                    from,
                    to,
                    0
            );

            mListViewWords.setAdapter(mTranslateAdaptor);

            mDatabase.close();

            mListViewWords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    TextView textView = (TextView) view.findViewById(R.id.textViewWordItem);

                    Intent intent = new Intent(FavouriteActivity.this, TranslatedWordViewActivity.class);
                    intent.putExtra("word", textView.getText().toString());
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            Log.e("io_er",e.getMessage());
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favourite_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.favourite_menu_exp_to_file:
                createWordTextList();
                mFileDialogSave.chooseFile_or_Dir();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createWordTextList(){
        sbText.setLength(0);
        try{
            mDatabase = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READONLY);

            Cursor cursor = mDatabase.rawQuery("select * from " + DBInfo.TABLE_FAVOURITE, null);
            while(cursor.moveToNext()) {
                Cursor wordsCursor = mDatabase.rawQuery("select * from " + DBInfo.TABLE_WORDS +
                        " where " + DBInfo.KEY_WORDS_WORD + " = ?", new String[]{cursor.getString(1)});
                wordsCursor.moveToNext();

                sbText.append(wordsCursor.getString(1) + "\t" + wordsCursor.getString(2) + "\t");

                Cursor translationCursor = mDatabase.rawQuery("select * from " + DBInfo.TABLE_TRANSLATION +
                        " where " + DBInfo.KEY_TRANSLATION_ID_WORD + "=?", new String[]{wordsCursor.getString(0)});

                while (translationCursor.moveToNext()) {
                    sbText.append(translationCursor.getString(1) + ", ");
                }
                sbText.append("\r\n");
            }
            mDatabase.close();



        }
        catch(Exception e){
            Log.e("io_er",e.getMessage());
        }



    }
}
