package website.amrfarouqa.beertest.Classes;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import website.amrfarouqa.beertest.Main.MainActivity;
import website.amrfarouqa.beertest.R;



public class Splash extends Activity {
    final static public String PREFS_NAME = "PREFS_NAME";
    final static private String PREF_KEY_SHORTCUT_ADDED = "PREF_KEY_SHORTCUT_ADDED";
    private final int SPLASH_DISPLAY_LENGTH = 5000;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
        SlideOnEntrance(view);
        new CreateShortcut().execute();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(Splash.this, WelcomeActivity.class);
                mainIntent.putExtra("RequestShow","no");
                Splash.this.startActivity(mainIntent);
                finish();
            }

        }, SPLASH_DISPLAY_LENGTH);
        new CreateShortcut().execute();
    }


    public void SlideOnEntrance(View view) {
        TextView copyRights = (TextView) findViewById(R.id.copyrights);
        copyRights.setText("Developed With \u2764 by AMRFAROUQA");
        Animation animation0 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_inmenu);
        copyRights.startAnimation(animation0);
        ImageView logoImage = (ImageView) findViewById(R.id.LogoImg);
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_top_to_bottom);
        logoImage.startAnimation(animation1);
        TextView LogoText = (TextView) findViewById(R.id.LogoText);
        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_top_to_bottom);
        LogoText.startAnimation(animation2);
        TextView LogoSlogan = (TextView) findViewById(R.id.logoSlogan);
        Animation animation3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_inmenu);
        LogoSlogan.startAnimation(animation3);

    }

    public void createShortcutIcon(){

        // Checking if ShortCut was already added
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        boolean shortCutWasAlreadyAdded = sharedPreferences.getBoolean(PREF_KEY_SHORTCUT_ADDED, false);
        if (shortCutWasAlreadyAdded) {
        }else{
            Intent shortcutIntent = new Intent(getApplicationContext(), Splash.class);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "BeerTest");
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.logo));
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            getApplicationContext().sendBroadcast(addIntent);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PREF_KEY_SHORTCUT_ADDED, true);
            editor.commit();
        }
    }


    class CreateShortcut extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            createShortcutIcon();
            return null;
        }
        protected void onPostExecute(String file_url) {

        }

    }

}
