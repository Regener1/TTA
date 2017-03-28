package com.example.regener.texttranslationassistant;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.regener.texttranslationassistant.manager.DBInfo;
import com.example.regener.texttranslationassistant.manager.DataBaseHelper;

public class TranslatorActivity extends ActionBarActivity {

    private SimpleCursorAdapter mTranslateAdaptor = null;
    private SQLiteDatabase mDatabase = null;
    private EditText mEditTextQuery;
    private ListView mListViewWords;

    private final String mDbPath = DBInfo.DB_PATH + DBInfo.DB_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);
        setTitle(R.string.drawer_item_translator);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTranslator);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mEditTextQuery = (EditText) findViewById(R.id.editTextQuery);
        mListViewWords = (ListView) findViewById(R.id.listViewWords);

        /*
        Get info from database dictionary
         */
        // можно вынести в отдельный класс

        try {
            mDatabase = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READONLY);

            Cursor cursor = mDatabase.rawQuery("select * from " + DBInfo.TABLE_WORDS, null);

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
        } catch (Exception e) {
            Log.e("io_er",e.getMessage());
            finish();
        }

        mListViewWords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.textViewWordItem);

                Intent intent = new Intent(TranslatorActivity.this, TranslatedWordViewActivity.class);
                intent.putExtra("word", textView.getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        try {
            mEditTextQuery.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int count, int after) {
                    mDatabase = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READONLY);
                    mTranslateAdaptor.getFilter().filter(s.toString().toLowerCase());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            mTranslateAdaptor.setFilterQueryProvider(new FilterQueryProvider() {
                @Override
                public Cursor runQuery(CharSequence constraint) {
                    if (constraint == null || constraint.length() == 0) {
                        return mDatabase.rawQuery("select * from " + DBInfo.TABLE_WORDS, null);
                    } else {
                        return mDatabase.rawQuery("select * from " + DBInfo.TABLE_WORDS +
                                        " where " + DBInfo.KEY_WORDS_WORD + " like ?",
                                new String[]{constraint.toString() + "%"});
                    }
                }
            });

            mListViewWords.setAdapter(mTranslateAdaptor);
        } catch (Exception e) {
            Log.e("io_er",e.getMessage());
            finish();
        }
    }
}
