package com.example.falldamage.falldamagecalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.content.Intent;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the damage dice results and corresponding names
        Intent resultsIntent = getIntent();
        String result[] = resultsIntent.getStringArrayExtra(MainActivity.EXTRA_RESULTS);
        String names[] = resultsIntent.getStringArrayExtra(MainActivity.EXTRA_NAMES);

        // For each of the up-to-eight results
        for (int i = 1; i < 9; i++) {
            TextView resultView;

            // Choose which result to extract
            switch (i) {
                case 1:
                    resultView = findViewById(R.id.damageDie1);
                    break;
                case 2:
                    resultView = findViewById(R.id.damageDie2);
                    break;
                case 3:
                    resultView = findViewById(R.id.damageDie3);
                    break;
                case 4:
                    resultView = findViewById(R.id.damageDie4);
                    break;
                case 5:
                    resultView = findViewById(R.id.damageDie5);
                    break;
                case 6:
                    resultView = findViewById(R.id.damageDie6);
                    break;
                case 7:
                    resultView = findViewById(R.id.damageDie7);
                    break;
                case 8:
                    resultView = findViewById(R.id.damageDie8);
                    break;
                default:
                    resultView = findViewById(R.id.damageDie1);
            }

            // Display the corresponding result
            resultView.setText(result[i-1]);

            // Choose which name to extract
            switch (i) {
                case 1:
                    resultView = findViewById(R.id.nameResult1);
                    break;
                case 2:
                    resultView = findViewById(R.id.nameResult2);
                    break;
                case 3:
                    resultView = findViewById(R.id.nameResult3);
                    break;
                case 4:
                    resultView = findViewById(R.id.nameResult4);
                    break;
                case 5:
                    resultView = findViewById(R.id.nameResult5);
                    break;
                case 6:
                    resultView = findViewById(R.id.nameResult6);
                    break;
                case 7:
                    resultView = findViewById(R.id.nameResult7);
                    break;
                case 8:
                    resultView = findViewById(R.id.nameResult8);
                    break;
                default:
                    resultView = findViewById(R.id.nameResult1);
            }

            // Display the corresponding name
            resultView.setText(names[i-1]);

        }
    }

    // Override so that the in-app back button and the OS's back button both ONLY close the results page
    // Without this, all fields on the input page would be cleared when the in-app back button is pushed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();     // don't destroy the main activity and associated input
                onBackPressed();
                break;
        }
        return true;
    }
}
