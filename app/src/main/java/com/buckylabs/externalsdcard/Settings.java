package com.buckylabs.externalsdcard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class Settings extends PreferenceActivity {
    SharedPreferences preferences;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    static String paths[];
    private static final int READ_REQUEST_CODE = 42;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

       paths=getIntent().getStringArrayExtra("paths");

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                // Implementation


                if (key.equals("example_switch")) {

                    boolean value = preferences.getBoolean("example_switch", false);
            } else {


                    }
                }



        };
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }


    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
            addPreferencesFromResource(R.xml.preferences);

            final ListPreference lp= (ListPreference) getPreferenceScreen().findPreference("list");
            lp.setEntries(paths);
            lp.setEntryValues(paths);
            lp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    int index=lp.findIndexOfValue(newValue.toString());

                    if(index==0){
                        Log.e("Value0",newValue.toString());
                        String rootPath=paths[0]+"App_Backup_Proooo/";
                        Log.e("Value0",rootPath);

                        File f2 = new File(rootPath);
                        if (!f2.exists()) {
                            f2.mkdirs();
                        }
                        return true;
                    }
                    if(index==1){
                        Log.e("Value1",newValue.toString());
                        Intent i=new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
                        startActivityForResult(i,42);
                      /* String rootPath=paths[1]+"App_Backup_Proooo/";
                        Log.e("Value1 Path",rootPath);

                        File f2 = new File(rootPath);
                        if (!f2.exists()) {
                            f2.mkdirs();
                        }*/
                        return true;
                    }
                    return false;
                }
            });

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            // handle result
            Uri treeUri = data.getData();
            Log.e("Uri",""+treeUri);


        }
    }
}
