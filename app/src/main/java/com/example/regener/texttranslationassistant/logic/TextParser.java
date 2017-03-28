package com.example.regener.texttranslationassistant.logic;

import java.util.ArrayList;

/**
 * Created by Regener on 08.03.2017.
 */

public class TextParser {
    /*
    temp variables
     */
    float textSize;
    /**/

    private String text = "";
    private ArrayList<String> pages = new ArrayList<>();

    private int charsOnLine;
    private int lineCount;

    public TextParser(String text){
        this.text = text;
    }

    public void setCharsOnLine(int value){
        this.charsOnLine = value;
    }

    public void setLineCount(int value){
        this.lineCount = value;
    }

    public void loadText(String text){
        this.text = text;
        pages.clear();
    }

    public void parseText(){
        StringBuilder allText = new StringBuilder(text);
        StringBuilder textOnPage = new StringBuilder();

        int i;
        int lineLength = charsOnLine;
        while(allText.length() != 0) {
            for (i = 0; i < lineCount; i++) {

                if(allText.length() < charsOnLine){
                    lineLength = allText.length();
                }
                else {
                    lineLength = charsOnLine;
                    while(Character.isLetter(allText.charAt(lineLength))){
                        lineLength--;
                    }
                }
                textOnPage.append(allText.substring(0, lineLength));
                allText.delete(0, lineLength);
            }

            pages.add(textOnPage.toString());
            textOnPage.setLength(0);
        }
    }

    public ArrayList<String> getPages(){
        return pages;
    }
}
