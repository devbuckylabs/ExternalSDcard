package com.buckylabs.externalsdcard;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ApkManager {

    private Context context;
    private PackageManager pm;
    private String rootPath;

    public ApkManager(Context context) {
        this.context = context;
        pm = context.getPackageManager();
        rootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/App_Backup_Pro/";
    }


    public List<Apk> getinstalledApks(boolean isSys) throws PackageManager.NameNotFoundException {

        List<Apk> installedApks = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        Collections.sort(apps, new ApplicationInfo.DisplayNameComparator(pm));
        Log.e("App Size ", "" + apps.size());

        for (ApplicationInfo app : apps) {

            PackageInfo packageInfo = pm.getPackageInfo(app.packageName, PackageManager.GET_META_DATA);

            if (isSys) {

                Apk apk = getApk(packageInfo);
                installedApks.add(apk);
            } else {

                if ((app.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0) {

                } else {

                    Apk apk = getApk(packageInfo);
                    installedApks.add(apk);

                }


            }
        }

        return installedApks;
    }

    public List<Apk> getArchivedApks() {

        List<Apk> archivedApks = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/App_Backup_Pro";
        File directory = new File(path);
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".apk");
            }
        });
        for (File file : files) {
            Log.e("Archive FilesName", "" + file.getName());
            PackageInfo packinfo = pm.getPackageArchiveInfo(path + "/" + file.getName(), 0);
            ApplicationInfo info;
            try {
                info = pm.getApplicationInfo(packinfo.packageName, PackageManager.GET_META_DATA);

            } catch (PackageManager.NameNotFoundException e) {

                info = null;
                packinfo.applicationInfo.sourceDir = file.getAbsolutePath();
                packinfo.applicationInfo.publicSourceDir = file.getAbsolutePath();

            }

            packinfo.applicationInfo.sourceDir = file.getAbsolutePath();
            packinfo.applicationInfo.publicSourceDir = file.getAbsolutePath();


            Apk apk = getApk(packinfo);
            archivedApks.add(apk);


        }

        return archivedApks;

    }


    public Apk getApk(PackageInfo packinfo) {


        String AppName = (String) packinfo.applicationInfo.loadLabel(pm);
        Drawable AppIcon = packinfo.applicationInfo.loadIcon(pm);
        String AppPackage = packinfo.packageName;
        String AppVersionName = packinfo.versionName;
        long time = new File(packinfo.applicationInfo.sourceDir).lastModified();
        String date = getAppDate(time);
        Log.e("Date", packinfo.lastUpdateTime + "");
        String sourcedirectory = packinfo.applicationInfo.sourceDir;
        String AppStatus = "";
        File file1 = new File(packinfo.applicationInfo.sourceDir);
        String Appsize = getAppSize(file1.length());
        boolean isChecked = false;
        Apk apk = new Apk(AppName, AppIcon, AppPackage, AppStatus, AppVersionName, date, Appsize, sourcedirectory, isChecked);

        return apk;

    }

    public String getAppDate(long milliseconds) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        String dateString = formatter.format(new Date(milliseconds));
        return dateString;
    }


    public String getAppSize(double sizeInBytes) {

        if (sizeInBytes < 1048576) {

            double sizeInKB = sizeInBytes / 1024;
            String size = String.format(Locale.US, "%.1f", sizeInKB);


            return size + " KB";
        } else if (sizeInBytes < 1073741824) {

            double sizeInMb = sizeInBytes / 1048576;
            String size = String.format(Locale.US, "%.1f", sizeInMb);
            return size + " MB";
        }
        return "";
    }


    public void getApksToShare(List<Apk> apks) {

        List<Uri> shareApks = new ArrayList<>();
        for (Apk apk : apks) {

            File file = new File(apk.getSourceDirectory());
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            shareApks.add(uri);

        }

        if (shareApks.isEmpty()) {
            Toast.makeText(context, "Select a app to share ", Toast.LENGTH_SHORT).show();
        } else {


            Log.e("SHAREAPKS", shareApks.size() + "");
            Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.setType("*/*");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "AppBackupPro");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "great");
            shareIntent.putExtra(Intent.EXTRA_EMAIL, "Checkout my app");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, (ArrayList<? extends Parcelable>) shareApks);
            context.startActivity(Intent.createChooser(shareIntent, "Share app via"));


        }
    }

    public void shareApkLink(String packageName) {
        StringBuilder marketLink = new StringBuilder();
        marketLink.append("https://play.google.com/store/apps/details?id=");
        marketLink.append(packageName);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        shareIntent.putExtra(Intent.EXTRA_TEXT, marketLink.toString());
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public void shareApk(Apk apk) {

        File file = new File(apk.getSourceDirectory());
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/vnd.android.package-archive");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, apk.getAppName() + ".apk");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this amazing app");
        shareIntent.putExtra(Intent.EXTRA_EMAIL, "Checkout my app");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(shareIntent, "Share app via"));


    }

    public void restoreApk(Apk apk) {

        String Appname = appNameGenerator(apk);

        File file = new File(rootPath, Appname);

        if (Build.VERSION.SDK_INT >= 24) {

            Uri path = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(path,
                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } else {

            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(uri,
                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);


        }
    }

    public boolean backupApk(Apk apk) {

        try {
            String Appname = appNameGenerator(apk);
            File f1 = new File(apk.getSourceDirectory());

            File f2 = new File(rootPath);
            if (!f2.exists()) {
                f2.mkdirs();
            }

            f2 = new File(rootPath + "/" + Appname);

            if (!f2.exists()) {
                f2.createNewFile();
                InputStream in = new FileInputStream(f1);
                FileOutputStream out = new FileOutputStream(f2);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                Log.e("BackUp Complete ", Appname.toString());
                out.flush();
                out.close();
                return true;
            } else {
                Toast.makeText(context, "App already backed up", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

            Log.e("Exception", "********************************************* ");
            e.printStackTrace();
        }
        return false;

    }


    public String appNameGenerator(Apk apk) {
        StringBuilder Appname = new StringBuilder();
        Appname.append(apk.getAppName());
        Appname.append("-");
        Appname.append(apk.getAppPackage());
        Appname.append("-");
        Appname.append(apk.getAppVersionName());
        Appname.append(".apk");

        return Appname.toString();
    }

    public void uninstallApks(List<Apk> apks) {

        for (Apk apk : apks) {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + apk.getAppPackage()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public void deleteApks(List<Apk> apks) {

        for (Apk apk : apks) {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + apk.getAppPackage()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}