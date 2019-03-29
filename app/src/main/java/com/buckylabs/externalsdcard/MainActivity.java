package com.buckylabs.externalsdcard;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    Button button;
    SharedPreferences preferences;
    String rootUri;
    String directoryUri;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        preferences=getSharedPreferences("prefs",MODE_PRIVATE);
        rootUri= preferences.getString("RootUri","");
        directoryUri = preferences.getString("DirUri","");

        Log.e("RootUri",rootUri);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.WRITE_SETTINGS}, 1);
            requestPermissions(new String[]{Manifest.permission.MANAGE_DOCUMENTS}, 1);
            //   requestPermissions(new String[]{android.Manifest.permission.F}, 1);

        }
StringBuilder s=new StringBuilder();
        String[] externalStoragePaths = StorageUtil.getStorageDirectories(this);
        final String[] paths = new String[externalStoragePaths.length];
        for (int i = 0; i < externalStoragePaths.length; i++) {

             s.append(externalStoragePaths[i]);
             s.append("**");
           // paths[i] = s;
        }

        textView.setText(s);
        //createDirectory(rootUri);
        createFile(rootUri);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  Intent i=new Intent(MainActivity.this,Settings.class);
                i.putExtra("paths",paths);
                startActivity(i);*/

              Intent i=new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
               startActivityForResult(i,42);




            }
        });
        //createDirectory(Environment.getExternalStorageDirectory().getAbsolutePath() + "/App_Backup_Pro/");
    }


public void createDirectory(String treeUri){


        DocumentFile rootPath = DocumentFile.fromTreeUri(this,Uri.parse(treeUri));
        DocumentFile directory = rootPath.createDirectory("App_Backup_Pro");
        Log.e("1","1");

        /*directoryUri = directory.getUri().getPath();
        Log.e("dDirUri",directoryUri);
        directory.createFile("text/plain","Hello");
        Log.e("1","1");*/
    }

 public void createFile(String treeUri){

     DocumentFile rootPath = DocumentFile.fromTreeUri(this,Uri.parse(treeUri));

     //Works on emulator APi 21 and Api 28

     DocumentFile dir= rootPath.findFile("App_Backup_Pro");
     dir.createFile("text/plain","Wow");

     //works on Api 28
    /*try {
        for (DocumentFile file : rootPath.listFiles()) {

             //Log.e("files",file.getName());
            if (file.getName().equals("App_Backup_Pro") && file.isDirectory()) {

                file.createFile("text/plain", "Wow");

                Log.e("DirFound", "******");
            }

        }
    }catch (NullPointerException e){
        e.printStackTrace();
    }*/

    }







    public String getStoragePaths(){
        String removableStoragePath="";
        File fileList[] = new File("/storage/").listFiles();
        for (File file : fileList)
        { if(!file.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath()) && file.isDirectory() && file.canRead())
            removableStoragePath = file.getAbsolutePath();
        return removableStoragePath;
        }
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 42 && resultCode == RESULT_OK) {
            // handle result
            Uri treeUri = data.getData();
            Log.e("tree",treeUri+"");


            Log.e("root",rootUri);
            final int takeFlags =data.getFlags()&
                     (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(treeUri, takeFlags);

//createDirectory(rootUri);
            /*DocumentFile rootPath = DocumentFile.fromTreeUri(this,treeUri);
            DocumentFile directory = rootPath.createDirectory("App_Backup_Pro");
            Log.e("1","1");

            directoryUri = directory.getUri().getPath();
            directory.createFile("text/plain","Hello");
            Log.e("1","1");
*/


            SharedPreferences.Editor prefEditor=preferences.edit();
            prefEditor.putString("RootUri", String.valueOf(treeUri));
            prefEditor.putInt("flags",takeFlags);

            prefEditor.commit();

        }
    }
}
