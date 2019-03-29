package com.buckylabs.externalsdcard;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.ListPreference;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.WRITE_SETTINGS}, 1);
            requestPermissions(new String[]{Manifest.permission.MANAGE_DOCUMENTS}, 1);
            //   requestPermissions(new String[]{android.Manifest.permission.F}, 1);

        }
        String[] externalStoragePaths = StorageUtil.getStorageDirectories(this);
        final String[] paths = new String[externalStoragePaths.length];
        for (int i = 0; i < externalStoragePaths.length; i++) {

            String s = externalStoragePaths[i];
            paths[i] = s;
        }

        textView.setText(Environment.getExternalStorageDirectory().getAbsolutePath());

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


    public void createFile(String rootPath) {
        File f2 = new File(rootPath);
        if (!f2.exists()) {
            f2.mkdirs();
        }
    }

    public void createDirectory(String rootPath) {
        File f2 = new File(rootPath);
        if (!f2.exists()) {
            f2.mkdirs();
        }
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
            Log.e("Uri",""+treeUri.getPath());
            DocumentFile dir=DocumentFile.fromTreeUri(this,treeUri);
         //   String path = UriHelpers.getPath(context, save_tree.getUri());
            Log.e("Dir",dir.getName());


           DocumentFile pickedDir= dir.createDirectory("App_Backup_Pro");
            DocumentFile newFile2 = pickedDir.createFile("text/plain", "My Novel");
            OutputStream out = null;
            try {
                out = getContentResolver().openOutputStream(newFile2.getUri());
                out.write("A long time ago...".getBytes());
                out.close();
                Log.e("Out",newFile2.getUri().getPath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
             catch (IOException e) {
                e.printStackTrace();
            }


            //Log.e("Dirrrrr",newFile.getUri().getPath());

            Log.e("Path",Environment.getExternalStorageDirectory().getAbsolutePath());
            Log.e("StorePath",getStoragePaths());






            //createDirectory();

           // Uri path = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", file);

        }
    }
}
