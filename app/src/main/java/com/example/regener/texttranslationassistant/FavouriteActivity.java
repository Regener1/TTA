package com.example.regener.texttranslationassistant;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.regener.texttranslationassistant.manager.DBInfo;

public class FavouriteActivity extends ActionBarActivity {
    private SimpleCursorAdapter mTranslateAdaptor = null;
    private SQLiteDatabase mDatabase = null;
    private ListView mListViewWords;

    private final String mDbPath = DBInfo.DB_PATH + DBInfo.DB_NAME;

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
}
