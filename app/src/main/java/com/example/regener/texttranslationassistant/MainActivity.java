package com.example.regener.texttranslationassistant;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.regener.texttranslationassistant.logic.SimpleFileDialog;
import com.example.regener.texttranslationassistant.logic.SolvenItemConteiner;
import com.example.regener.texttranslationassistant.manager.DataBaseHelper;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static android.support.v4.content.ContextCompat.startActivity;

public class MainActivity extends ActionBarActivity {
    private static String TAG = "PermissionDemo";
    private final int REQUEST_CODE = 1;
    private static final int REQUEST_READ_STORAGE = 112;
    private final String FILE_OPENED_FILES = "OpenedFiles.xml";

    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private RecyclerView recyclerView;
    private SolventRecyclerViewAdapter rcAdapter;
    private Drawer.Result drawerResult = null;
    private SimpleFileDialog FileOpenDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SolvenItemConteiner.setOpenedFiles(getOpenedFiles());

        if(!loadDataBase()){
            Log.i("MY_WARNING", "Ошибка загрузки бд");
        }

        /////////////////////////////////
        //PERMISSION
        /////////////////////////////////
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Permission to access the SD-CARD is required for this app to Open books.")
                        .setTitle("Permission required");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "Clicked");
                        makeRequest();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                makeRequest();
            }
        }

//        permission = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            Log.i(TAG, "Permission to record denied");
//
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage("Permission to access the SD-CARD is required for this app to Open books.")
//                        .setTitle("Permission required");
//
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int id) {
//                        Log.i(TAG, "Clicked");
//                        makeRequest();
//                    }
//                });
//
//                AlertDialog dialog = builder.create();
//                dialog.show();
//
//            } else {
//                makeRequest();
//            }
//        }


        /////////////////////////////////
        //ToolBar
        /////////////////////////////////
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /////////////////////////////////
        //StaggeredGridLayoutManager
        /////////////////////////////////
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);
        rcAdapter = new SolventRecyclerViewAdapter(MainActivity.this, SolvenItemConteiner.getOpenedFiles());
        recyclerView.setAdapter(rcAdapter);

        ////////////////////////////////
        //FileDialog
        ////////////////////////////////
        FileOpenDialog =  new SimpleFileDialog(MainActivity.this, "FileOpen",
                new SimpleFileDialog.SimpleFileDialogListener()
                {
                    @Override
                    public void onChosenDir(String chosenDir)
                    {
                        Intent intent = new Intent(MainActivity.this, TextViewActivity.class);
                        intent.putExtra("path", chosenDir);
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                });

        ///////////////////////////////
        //LeftMenu
        ///////////////////////////////
        drawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_read_book).withIcon(FontAwesome.Icon.faw_book),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_translator).withIcon(FontAwesome.Icon.faw_language),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_favourite).withIcon(FontAwesome.Icon.faw_star),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_about).withIcon(FontAwesome.Icon.faw_info)
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                    if (drawerItem instanceof Nameable) {
                        Intent intent;
                        switch (((Nameable) drawerItem).getNameRes()){
                            case R.string.drawer_item_read_book:
                                FileOpenDialog.chooseFile_or_Dir();
                                break;
                            case R.string.drawer_item_translator:
                                intent = new Intent(MainActivity.this, TranslatorActivity.class);
                                startActivity(intent);
                                break;
                            case R.string.drawer_item_favourite:
                                intent = new Intent(MainActivity.this, FavouriteActivity.class);
                                startActivity(intent);
                                break;
                            case R.string.drawer_item_settings:
                                intent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent);
                                break;
                            case R.string.drawer_item_about:

                                break;
                        }
                        Toast.makeText(MainActivity.this, MainActivity.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                    }
                }

        }).build();



    }

    @Override
    protected void onStart() {
        super.onStart();
        getPrefs();
        rcAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        // Закрываем Navigation Drawer по нажатию системной кнопки "Назад" если он открыт
        if (drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_STORAGE: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user");

                } else {

                    Log.i(TAG, "Permission has been granted by user");

                }
                return;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    int count = 0;
                    ItemObject itemObject = (ItemObject)data.getParcelableExtra(ItemObject.class.getCanonicalName());
                    for(int i = 0; i < SolvenItemConteiner.getOpenedFiles().size(); i++){
                        if(SolvenItemConteiner.getItem(i).getPath().equals(itemObject.getPath())){
                            count++;
                        }
                    }
                    if(count == 0){
                        SolvenItemConteiner.addItem(itemObject);
                        WriteOpenedFileInfo();
                    }

            }
        }
    }

    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_READ_STORAGE);
    }

    private void getPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SettingsParam.textSize = Float.parseFloat(prefs.getString(SettingsInfo.TEXT_SIZE_KEY, "24"));
        SettingsParam.notificationTime = prefs.getString(SettingsInfo.NOTIFY_FREQ_KEY, "");
    }

    private List<ItemObject> getOpenedFiles(){
        List<ItemObject> openedFiles = new ArrayList<>();
        try{
            File file = new File(getBaseContext().getFilesDir().getAbsolutePath() + File.separator + FILE_OPENED_FILES);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("book");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element element = (Element) node;
                    openedFiles.add(new ItemObject(element.getElementsByTagName("title").item(0).getTextContent().toString(),
                            element.getElementsByTagName("text").item(0).getTextContent().toString(),
                            element.getElementsByTagName("path").item(0).getTextContent().toString()));
                }
            }
        }
        catch(Exception e){
            Log.e("openedfilelist", e.getMessage());
        }
        finally {
            return openedFiles;
        }
    }

    private void WriteOpenedFileInfo(){
        try{
            File file = new File(getBaseContext().getFilesDir().getAbsolutePath() + File.separator + FILE_OPENED_FILES);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Element rootElem = doc.createElement("books");
            doc.appendChild(rootElem);
            for(int i = 0; i < SolvenItemConteiner.getSize(); i++){
                Element bookElem = doc.createElement("book");
                rootElem.appendChild(bookElem);

                Element titleElem = doc.createElement("title");
                titleElem.appendChild(doc.createTextNode(SolvenItemConteiner.getItem(i).getTitle()));
                bookElem.appendChild(titleElem);

                Element textElem = doc.createElement("text");
                textElem.appendChild(doc.createTextNode(SolvenItemConteiner.getItem(i).getText()));
                bookElem.appendChild(textElem);

                Element pathElem = doc.createElement("path");
                pathElem.appendChild(doc.createTextNode(SolvenItemConteiner.getItem(i).getPath()));
                bookElem.appendChild(pathElem);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);

            transformer.transform(source, result);
        }
        catch(Exception e){
            Log.e("myex",e.getMessage());
        }
    }


    private boolean loadDataBase(){
        DataBaseHelper dbHelper = new DataBaseHelper(this);

        try {
            dbHelper.createDataBase();
        }
        catch(IOException e){
            Log.e("MY_WARNING", e.getMessage());
            return false;
        }
        catch(Exception e){
            Log.i("MY_WARNING",e.getMessage());
            return false;
        }

        return true;
    }
}
