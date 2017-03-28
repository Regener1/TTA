package com.example.regener.texttranslationassistant.manager;

/**
 * Created by Regener on 07.03.2017.
 */

public final class DBInfo{
    public final static String DB_PATH = "/data/data/com.example.regener.texttranslationassistant/databases/";
    public final static String DB_ASSETS_PATH = "database/";
    public final static int DB_VERSION = 1;
    public final static String DB_NAME = "dictionary.db";

    public final static String TABLE_WORDS = "words";
    public final static String KEY_WORDS_ID = "_id";
    public final static String KEY_WORDS_WORD = "word";
    public final static String KEY_WORDS_TRANSCRIPTION = "transcription";

    public final static String TABLE_TRANSLATION = "translation";
    public final static String KEY_TRANSLATION_ID = "_id";
    public final static String KEY_TRANSLATION_TRANSLATE = "translate";
    public final static String KEY_TRANSLATION_ID_WORD = "id_word";

    public final static String TABLE_FAVOURITE = "favourite";
    public final static String KEY_FAVOURITE_ID = "_id";
    public final static String KEY_FAVOURITE_WORD = "word";
}
