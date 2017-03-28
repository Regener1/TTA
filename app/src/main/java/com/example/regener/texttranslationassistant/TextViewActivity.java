package com.example.regener.texttranslationassistant;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.regener.texttranslationassistant.manager.DBInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class TextViewActivity extends ActionBarActivity {
    private final String FILE_OPENED_FILES = "OpenedFiles.xml";

    private final float MOVE_LENGTH = 200;
    private final int REQUEST_CODE = 1;

    private String mTitle = "";
    private String mText = "";
    private String mPath = "";

    private SQLiteDatabase mDatabase = null;
    private final String mDbPath = DBInfo.DB_PATH + DBInfo.DB_NAME;

    private HashMap<String, String> mTranslate = new HashMap<>();
    private StringBuilder mCurrentKey = new StringBuilder();

//    private ViewFlipper mViewFlipper;
    private float mFromPosition;
//    private TextView mTextViewPageRatio;
    private TextView mTextViewMainText;

    private ItemObject curOpenedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTextView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(ItemObject.class.getCanonicalName(), curOpenedFile);
                setResult(RESULT_OK, intent);

                finish();
            }
        });


        Intent intent = getIntent();
        String path = intent.getStringExtra("path");

        try {
            File file = new File(path);
            if (file.exists()) {
                mTitle = file.getName();
                mPath = file.getAbsolutePath();
                BufferedReader br = new BufferedReader(new FileReader(path));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append('\n');
                }
                br.close();

                mText = sb.toString();

                curOpenedFile = new ItemObject(mTitle, mText.substring(0, 200), mPath);
            } else {
                mText = "";
            }
        } catch (Exception e) {
            Log.e("io_er",e.getMessage());
            finish();
        }

        setTitle(mTitle);

//        mViewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
//        mTextViewPageRatio = (TextView)findViewById(R.id.tvPageRatio);
        mTextViewMainText = (TextView) findViewById(R.id.textViewMainText);

//        mViewFlipper.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                switch (event.getAction())
//                {
//                    case MotionEvent.ACTION_DOWN:
//                        mFromPosition = event.getX();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        float toPosition = event.getX();
//                        // MOVE_LENGTH - расстояние по оси X, после которого можно переходить на след. экран
//                        // В моем тестовом примере MOVE_LENGTH = 150
//                        if ((mFromPosition - MOVE_LENGTH) > toPosition)
//                        {
//                            mFromPosition = toPosition;
//                            mViewFlipper.showNext();
//                        }
//                        else if ((mFromPosition + MOVE_LENGTH) < toPosition)
//                        {
//                            mFromPosition = toPosition;
//                            mViewFlipper.showPrevious();
//                        }
//                    default:
//                        break;
//                }
//                return true;
//            }
//        });
        mTextViewMainText.setTextSize(SettingsParam.textSize);
        mTextViewMainText.setMovementMethod(LinkMovementMethod.getInstance());
        mTextViewMainText.setText(mText, TextView.BufferType.SPANNABLE);
//            Spannable spans = (Spannable) mTextViewMainText.getText();
//
//            BreakIterator iterator = BreakIterator.getWordInstance();
//            iterator.setText(mText);
//
//            int start = iterator.first();
//            for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator
//                    .next()) {
//                String possibleWord = mText.substring(start, end);
//                if (Character.isLetterOrDigit(possibleWord.charAt(0))) {
//                    ClickableSpan clickSpan = getClickableSpan(possibleWord);
//                    spans.setSpan(clickSpan, start, end,
//                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                    if(!mTranslate.containsKey(possibleWord)){
//                        mTranslate.put(possibleWord.toLowerCase(), "");
//                    }
//
//                }
//            }
        StringBuilder possibleWord = new StringBuilder();
        Spannable spans = (Spannable) mTextViewMainText.getText();

        for(int i = 0; i < mText.length(); i++){
            if(Character.isLetter(mText.charAt(i))){
                possibleWord.append(mText.charAt(i));
            }
            else {
                if(possibleWord.length() == 0){
                    continue;
                }
                ClickableSpan clickSpan = getClickableSpan(possibleWord.toString());
                spans.setSpan(clickSpan, i - possibleWord.length(), i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                if(!mTranslate.containsKey(possibleWord.toString())){
                    mTranslate.put(possibleWord.toString().toLowerCase(), "");
                }
                possibleWord.setLength(0);
            }
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        //Заполнение viewFlipper
//        TextView textView = new TextView(this);
//        textView.setTextSize(SettingsParam.textSize);
//
//        Display display = getWindowManager().getDefaultDisplay();
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        display.getMetrics(displayMetrics);
//
////        int viewFlipperWidth = displayMetrics.widthPixels;
//        int viewFlipperWidth = mViewFlipper.getWidth();
//        int numChars;
//
//        Paint paint = textView.getPaint();
//        for (numChars = 1; numChars <= mText.length(); ++numChars) {
//            if (paint.measureText(mText, 0, numChars) > viewFlipperWidth) {
//                break;
//            }
//        }
//
//        TextParser tp = new TextParser(mText);
//        tp.setCharsOnLine(numChars - 1);
//        tp.setLineCount((int)Math.floor(numChars*(mViewFlipper.getWidth()/(double)mViewFlipper.getHeight())));//посчитать
//        tp.parseText();
//
//        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        ArrayList<String> pages = tp.getPages();
//
//        for(int i = 0; i < pages.size(); i++){
//            TextView tv = new TextView(this);
//            tv.setLayoutParams(params);
//            tv.setTextSize(SettingsParam.textSize);
//            tv.setMovementMethod(LinkMovementMethod.getInstance());
//            tv.setText(pages.get(i), TextView.BufferType.SPANNABLE);
//
//            Spannable spans = (Spannable) tv.getText();
//
//            BreakIterator iterator = BreakIterator.getWordInstance(Locale.US);
//            iterator.setText(pages.get(i));
//
//            int start = iterator.first();
//            for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator
//                    .next()) {
//                String possibleWord = pages.get(i).substring(start, end);
//                if (Character.isLetterOrDigit(possibleWord.charAt(0))) {
//                    ClickableSpan clickSpan = getClickableSpan(possibleWord);
//                    spans.setSpan(clickSpan, start, end,
//                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                    if(!mTranslate.containsKey(possibleWord)){
//                        mTranslate.put(possibleWord.toLowerCase(), "");
//                    }
//
//                }
//            }
//
//            mViewFlipper.addView(tv);
//        }
//
//
//        Log.i("m",""+mViewFlipper.getHeight());
//        Log.i("m",""+mViewFlipper.getWidth());
        //Заполнение viewFlipper
    }

    //Кликабельный текст
    private ClickableSpan getClickableSpan(final String word) {
        return new ClickableSpan() {

            final String mWord;
            {
                mWord = word.toLowerCase();
            }

            static final long DOUBLE_PRESS_INTERVAL = 250;
            long lastClickTime;

            @Override
            public void onClick(View widget) {
                Log.d("tapped on:", mWord);

                long currentClickTime = System.currentTimeMillis();

                if(currentClickTime - lastClickTime <= DOUBLE_PRESS_INTERVAL) {

                    try {
                        mDatabase = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READONLY);
                        Cursor wordsCursor = mDatabase.rawQuery("select * from " + DBInfo.TABLE_WORDS +
                                " where " + DBInfo.KEY_WORDS_WORD + " = ?", new String[]{mWord});

                        if(wordsCursor != null && wordsCursor.getCount() > 0){
                            Intent intent = new Intent(TextViewActivity.this, TranslatedWordChooseActivity.class);
                            intent.putExtra("word", mWord);
                            startActivityForResult(intent, REQUEST_CODE);

                            mCurrentKey.setLength(0);
                            mCurrentKey.append(mWord);
                        }
                        else{
                            Intent intent = new Intent(TextViewActivity.this, EditWordActivity.class);
                            intent.putExtra("word", mWord);
                            startActivityForResult(intent, REQUEST_CODE);

                            mCurrentKey.setLength(0);
                            mCurrentKey.append(mWord);
                        }
                        mDatabase.close();
                    } catch (Exception e) {
                        Log.e("db_er",e.getMessage());
                    }
                }
                else {
                    Toast.makeText(widget.getContext(), mTranslate.get(mWord), Toast.LENGTH_SHORT)
                            .show();
                }
                lastClickTime = currentClickTime;
            }

            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    mTranslate.put(mCurrentKey.toString(), data.getStringExtra("translate"));
            }
        }
    }

//    public void btnPrevOnClick(View view) {
//        mViewFlipper.showPrevious();
//        mTextViewPageRatio.setText(mViewFlipper.getDisplayedChild()+"/"+(mViewFlipper.getChildCount()-1));
//        Log.i("m",""+mViewFlipper.getHeight());
//    }
//
//    public void btnNextOnClick(View view) {
//        mViewFlipper.showNext();
//
//        mTextViewPageRatio.setText(mViewFlipper.getDisplayedChild()+"/"+(mViewFlipper.getChildCount()-1));
//    }

}
