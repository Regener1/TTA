package com.example.regener.texttranslationassistant;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.regener.texttranslationassistant.manager.DBInfo;

import java.util.ArrayList;

public class EditWordActivity extends AppCompatActivity {

    private EditText mEditTextWord;
    private EditText mEditTextTranscription;
    private EditText mEditTextTranslate;
    private ListView mListViewTranslation;
    private ImageButton mImageButtonAddTranslate;
    private ImageButton mImageButtonRemoveTranslate;
    private Button mButtonOK;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> translations = new ArrayList<>();

    private SQLiteDatabase mDatabase = null;
    private final String mDbPath = DBInfo.DB_PATH + DBInfo.DB_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_word);
        setTitle("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEdit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        String word = intent.getStringExtra("word");

        mEditTextWord = (EditText) findViewById(R.id.editTextWord);
        mEditTextTranscription = (EditText) findViewById(R.id.editTextTranscription);
        mEditTextTranslate = (EditText) findViewById(R.id.editTextTranslate);
        mListViewTranslation = (ListView) findViewById(R.id.listViewTranslation);
        mImageButtonAddTranslate = (ImageButton) findViewById(R.id.btnAddTranslate);
        mImageButtonRemoveTranslate = (ImageButton) findViewById(R.id.btnRemoveTranslate);
        mButtonOK = (Button) findViewById(R.id.btnOK);

        mEditTextWord.setEnabled(false);
        mEditTextWord.setText(word);

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item, translations);
        mListViewTranslation.setAdapter(arrayAdapter);

        mImageButtonAddTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEditTextTranslate.getText().length() != 0){
                    translations.add(mEditTextTranslate.getText().toString());
                    arrayAdapter.notifyDataSetChanged();
                    mEditTextTranslate.setText("");
                }

            }
        });

        mImageButtonRemoveTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = mListViewTranslation.getCheckedItemPosition();

                if(id == -1){
                    return;
                }

                translations.remove(id);
                arrayAdapter.notifyDataSetChanged();
            }
        });

        mButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEditTextWord.getText().length() == 0 || mListViewTranslation.getChildCount() == 0){
                    Toast.makeText(getApplicationContext(), "Not all fields are filled in", Toast.LENGTH_SHORT).show();
                }
                else if(mListViewTranslation.getCheckedItemPosition() == -1){
                    Toast.makeText(getApplicationContext(), "Select translation", Toast.LENGTH_SHORT).show();
                }
                else{
                    try {
                        mDatabase = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READWRITE);
                        ContentValues insertValuesWord = new ContentValues();
                        insertValuesWord.put(DBInfo.KEY_WORDS_WORD, mEditTextWord.getText().toString());
                        insertValuesWord.put(DBInfo.KEY_WORDS_TRANSCRIPTION, "[" + mEditTextTranscription.getText().toString() + "]");

                        long id = mDatabase.insert(DBInfo.TABLE_WORDS, null, insertValuesWord);
                        Log.i("last id", id+"");

                        ContentValues insertValuesTranslate = new ContentValues();
                        for (int i = 0; i < mListViewTranslation.getChildCount(); i++) {
                            TextView textView = (TextView) mListViewTranslation.getChildAt(i);
                            insertValuesTranslate.put(DBInfo.KEY_TRANSLATION_TRANSLATE, textView.getText().toString());
                            insertValuesTranslate.put(DBInfo.KEY_TRANSLATION_ID_WORD, id);
                            mDatabase.insert(DBInfo.TABLE_TRANSLATION, null, insertValuesTranslate);
                        }

                        mDatabase.close();

                        int curPos = mListViewTranslation.getCheckedItemPosition();
                        TextView textView = (TextView) mListViewTranslation.getChildAt(curPos);
                        Intent intent = new Intent();
                        intent.putExtra("translate", textView.getText());
                        setResult(RESULT_OK, intent);

                        finish();
                    }
                    catch (Exception e){
                        Log.e("db_er", e.getMessage());
                    }
                }
            }
        });
    }
}
