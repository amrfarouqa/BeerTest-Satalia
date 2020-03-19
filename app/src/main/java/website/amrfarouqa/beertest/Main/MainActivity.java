package website.amrfarouqa.beertest.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import website.amrfarouqa.beertest.Classes.DigitsInputFilter;
import website.amrfarouqa.beertest.Classes.WelcomeActivity;
import website.amrfarouqa.beertest.R;


public class MainActivity extends AppCompatActivity {
    private double homeLatitude;
    private double homeLongitude;
    private boolean priority;
    private EditText homeLatitudeEdit;
    private EditText homeLongitudeEdit;
    private RadioGroup radioPriorityGroup;
    private RadioButton radioPriorityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeLatitudeEdit = findViewById(R.id.homeLatitude);
        homeLongitudeEdit = findViewById(R.id.homeLongitude);
        homeLongitudeEdit.setFilters(new InputFilter[]{new DigitsInputFilter(2, 8, 19.43295600)});
        homeLatitudeEdit.setFilters(new InputFilter[]{new DigitsInputFilter(2, 8, 51.74250300)});
        Button Start = findViewById(R.id.Start_Trip_Results_Btn);
        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               readUserInput();
            }
        });
    }

    private void readUserInput() {
        radioPriorityGroup = (RadioGroup) findViewById(R.id.priorityRadioGroup);
        if (homeLatitudeEdit.getText().toString().matches("") || homeLongitudeEdit.getText().toString().matches("")){
            Toast.makeText(this,"Please Check Home Latitude And Longitude Values",Toast.LENGTH_SHORT).show();
        }else{
            if (homeLatitudeEdit.getText().toString().matches("0") || homeLongitudeEdit.getText().toString().matches("0")) {
                homeLatitude = 51.74250300;
                homeLongitude = 19.43295600;
                String homeLatitudeDouble= Double.toString(homeLatitude);
                String homeLongitudeDouble= Double.toString(homeLongitude);
                homeLatitudeEdit.setText(homeLatitudeDouble);
                homeLongitudeEdit.setText(homeLongitudeDouble);
            }else{
                homeLatitude = Double.valueOf(homeLatitudeEdit.getText().toString());
                homeLongitude = Double.valueOf(homeLongitudeEdit.getText().toString());
            }
            int selectedId = radioPriorityGroup.getCheckedRadioButtonId();
            radioPriorityButton = (RadioButton) findViewById(selectedId);
            if(radioPriorityButton.getText().equals("More Breweries")){
                priority = true;
            }else{
                priority = false;
            }
            Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
            intent.putExtra("homeLatitude", homeLatitude);
            intent.putExtra("homeLongitude", homeLongitude);
            intent.putExtra("priority", priority);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.showWelcome) {
            Intent mainIntent = new Intent(MainActivity.this, WelcomeActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mainIntent.putExtra("RequestShow","yes");
            startActivity(mainIntent);
            finish();

            return true;
        }

        if (id == R.id.contactMe) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://amrfarouqa.website/"));
            startActivity(intent);
            return true;
        }

        if (id == R.id.privacy) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://amrfarouqa.website/beerTest/privacy_policy.html"));
            startActivity(intent);
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

}
