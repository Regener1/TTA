package com.example.regener.texttranslationassistant;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.regener.texttranslationassistant.manager.DBInfo;
import com.example.regener.texttranslationassistant.manager.DataBaseHelper;

public class TranslatedWordViewActivity extends AppCompatActivity {

    private Button mButtonClose;
    private ToggleButton mToggleButton;
    private LinearLayout mLinearLayout;
    private TextView mTextViewWord;
    private TextView mTextViewTranscription;

    private DataBaseHelper mDbHelper = new DataBaseHelper(this);
    private SQLiteDatabase mDatabase = null;
    private final String mDbPath = DBInfo.DB_PATH + DBInfo.DB_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translated_word_view);
        setTitle("");

        mTextViewWord = (TextView) findViewById(R.id.textViewWordView);
        mTextViewTranscription = (TextView) findViewById(R.id.textViewTranscriptionView);
        mButtonClose = (Button) findViewById(R.id.btnCloseView);
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutView);
        mToggleButton = (ToggleButton) findViewById(R.id.toggleButtonIsFavourView);

        Intent intent = getIntent();
        String word = intent.getStringExtra("word");

        try {
            mDatabase = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READONLY);
            Cursor wordsCursor = mDatabase.rawQuery("select * from " + DBInfo.TABLE_WORDS +
                    " where " + DBInfo.KEY_WORDS_WORD + " = ?", new String[]{word});
            wordsCursor.moveToNext();
            Cursor translationCursor = mDatabase.rawQuery("select * from " + DBInfo.TABLE_TRANSLATION +
                    " where " + DBInfo.KEY_TRANSLATION_ID_WORD + "=?", new String[]{wordsCursor.getString(0)});


            mTextViewWord.setText(wordsCursor.getString(1));
            mTextViewTranscription.setText(wordsCursor.getString(2));

            while (translationCursor.moveToNext()) {
                Log.i("debug", translationCursor.getString(1));
                TextView textView = new TextView(this);
                textView.setTextSize(24);
                textView.setText(translationCursor.getString(1));
                mLinearLayout.addView(textView);
            }

            //togglebutton
            Cursor favouriteCursor = mDatabase.rawQuery("select * from " + DBInfo.TABLE_FAVOURITE +
                                    " where word = ?",new String[]{mTextViewWord.getText().toString()});
            if(favouriteCursor.moveToNext()){
                mToggleButton.setChecked(true);
            }

            mDatabase.close();
        } catch (Exception e) {
            Log.e("db_er",e.getMessage());
        }

        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!mToggleButton.isChecked()){
                    try {
                        mDatabase = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READWRITE);

                        mDatabase.execSQL("delete from " + DBInfo.TABLE_FAVOURITE +
                                " where word = ?",new String[]{mTextViewWord.getText().toString()});
                        mDatabase.close();

                    } catch (Exception e) {
                        Log.e("db_er",e.getMessage());
                    }

                }
                else{
                    try {
                        mDatabase = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READWRITE);

                        mDatabase.execSQL("insert into " + DBInfo.TABLE_FAVOURITE + "(word) " +
                                "values(?)",new String[]{mTextViewWord.getText().toString()});
                        mDatabase.close();

                    } catch (Exception e) {
                        Log.e("db_er",e.getMessage());
                    }
                }
            }
        });


        mButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
