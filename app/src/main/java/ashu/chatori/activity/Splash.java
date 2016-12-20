package ashu.chatori.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import ashu.chatori.R;
import ashu.chatori.global.Constant;

/**
 * Created by apple on 07/09/16.
 */

public class Splash extends AppCompatActivity {
    private String urlOfApp = "https://play.google.com/store/apps/" +
            "details?id=ashu.chatorigali";
    private String latestVersion;
    private String currentVersion;

    private SharedPreferences sp;
    private Dialog dialog;
    private Thread splashThread = null;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        Log.d("pName", getPackageName());

        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        currentVersion = pInfo.versionName;
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());

//        @TargetApi(Build.VERSION_CODES.KITKAT)
//        public static boolean isDefaultSmsApp(Context context){
//            return context.getPackageNam().equals(Telephony.Sms.getDefaultSmsPackage(context));
//        }

        sp = getSharedPreferences("login", MODE_APPEND);
        final String from = sp.getString(Constant.FROM, "null");

        Intent intentn = getIntent();
        String action = "demo";
        action = intentn.getAction();
        Log.d("actionwa", "url is" + action);
            new GetCurrentVersion().execute();
            splashThread = new Thread() {

                @Override
                public void run() {
                    try {
                        sleep(200);
                        Intent intent = null;

                        if(from == null){
                            intent = new Intent(getApplicationContext(),
                                    Login.class);
                        }
                        else
                            intent = new Intent(getApplicationContext(),
                                    MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {

                    }
                }
            };
            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public class GetCurrentVersion extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(urlOfApp).get();
                latestVersion = doc.getElementsByAttributeValue
                        ("itemprop", "softwareVersion").first().text();

            } catch (Exception e) {

            }
            return null;
        }

        //TODO: Remove isNetConnected before release

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("curVers", currentVersion + "&&" + latestVersion);
            if (!(currentVersion.equalsIgnoreCase(latestVersion) || latestVersion == null))
                showUpdateDialog();
            else {
                splashThread.start();
            }
            super.onPostExecute(aVoid);

        }
    }


    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("A New Update is Available");
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                        ("market://details?id=filter.ashu.smsfilter")));
                finish();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                splashThread.start();
            }
        });

        builder.setCancelable(false);
        dialog = builder.show();
    }
}
