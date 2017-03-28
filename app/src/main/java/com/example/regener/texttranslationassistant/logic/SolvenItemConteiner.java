package com.example.regener.texttranslationassistant.logic;

import com.example.regener.texttranslationassistant.ItemObject;

import java.util.List;

/**
 * Created by Regener on 28.03.2017.
 */

public class SolvenItemConteiner {

    private static List<ItemObject> openedFiles;

    public static List<ItemObject> getOpenedFiles() {
        return openedFiles;
    }

    public static void setOpenedFiles(List<ItemObject> openedFiles) {
        SolvenItemConteiner.openedFiles = openedFiles;
    }

    public static ItemObject getItem(int i){
        return openedFiles.get(i);
    }

    public static void addItem(ItemObject item){
        openedFiles.add(item);
    }

    public static int getSize(){
        return openedFiles.size();
    }
}
