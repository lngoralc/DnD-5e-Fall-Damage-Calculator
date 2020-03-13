package com.example.falldamage.falldamagecalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class MainActivity extends AppCompatActivity {

    // Create two strings to pass the results and input names to the Display activity
    public static final String EXTRA_RESULTS = "com.example.falldamage.RESULTS";
    public static final String EXTRA_NAMES = "com.example.falldamage.NAMES";

    // Create a list of items for the dropdown terrain hardness selector
    private static final String[] terrainTypes = new String[]{"Stone", "Wood", "Grassland", "Marsh", "Fall broken"};

    // Create a string to store the selected terrain hardness
    private String dropdownSelection;

    // Acrobatics check modifiers corresponding to the various levels of hardness
    private static final int terrainModTab[] = {-15,0,15,30,45};

    // Effective fall distance modifiers, corresponding to the result of an Acrobatics check
    //                                       DC -20,-15,-10, -5,  0,  5, 10, 15,  20, 25, 30, 35,  40, 45,  50, 55,  60,  65,  70,  75,  80
    private static final double distModTab[] = {3.0,2.7,2.4,2.0,1.7,1.3,1.0,0.8,0.65,0.5,0.4,0.3,0.25,0.2,0.15,0.1,0.07,0.05,0.03,0.02,0.01};

    // Max effective fall distance based off terminal velocity distance times worst fall distance modifier
    private static final int maxFall = 500 * distModTab[0];

    // Initialize an array to store fall damage dice for 10 foot increments from 0 to maxFall feet
    private String damageDieTab[] = new String[maxFall/10 + 1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Spinner section to create the dropdown terrain hardness menu. Code from online tutorial
        Spinner dropdown = findViewById(R.id.terrainChoice);
        // Create an adapter to describe how the items are displayed
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, terrainTypes);
        // Set the spinner's adapter to the created one.
        dropdown.setAdapter(dropdownAdapter);
        // Set default terrain hardness
        dropdown.setSelection(2);

        // Listener for the spinner
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            // On selecting one of the dropdown options
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                // Store the selected dropdown option
                dropdownSelection = adapter.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // Auto-generated method stub
            }
        });

        // Populating the damage die table - each index represents 10 feet (i.e. [5] is 50 feet)
        damageDieTab[0] = "1";
        damageDieTab[1] = "1d6";
        damageDieTab[2] = "2d6";
        damageDieTab[3] = "3d8";
        damageDieTab[4] = "5d8";
        damageDieTab[5] = "7d10";
        damageDieTab[6] = "10d10";
        damageDieTab[7] = "12d12";
        damageDieTab[8] = "14d12";
        damageDieTab[9] = "16d12";
        // Square root function for damage up to maxFall feet (the worst-case scenario for damage)
        for (int i = 10; i < maxFall/10+1; i++) {
            String sqrt = Long.toString(Math.round(Math.sqrt(10*i)));
            damageDieTab[i] = sqrt+"d20+"+sqrt;
        }
    }

    // This function returns the damage dice for one of up to six creatures (index passed in determines which creature)
    private String calculateDamage(int checkIndex) {
        int terrainTypeIndex;
        int distModIndex;
        double distMod;
        EditText checkResultText;
        int checkResult;
        float fallDist;
        String fallDistString;
        String checkResultString;

        // Get the fall distance as text
        EditText fallDistText = findViewById(R.id.distIn);
        fallDistString = fallDistText.getText().toString();

        // Extract integer distance from text, or return no damage if the fall distance field is empty
        if (fallDistString.isEmpty())
            return "-";
        else
            fallDist = Integer.parseInt(fallDistString);

        // Select the check result corresponding to the selected creature
        switch (checkIndex) {
            case 1:
                checkResultText = findViewById(R.id.checkIn1);
                break;
            case 2:
                checkResultText = findViewById(R.id.checkIn2);
                break;
            case 3:
                checkResultText = findViewById(R.id.checkIn3);
                break;
            case 4:
                checkResultText = findViewById(R.id.checkIn4);
                break;
            case 5:
                checkResultText = findViewById(R.id.checkIn5);
                break;
            case 6:
                checkResultText = findViewById(R.id.checkIn6);
                break;
            default:
                checkResultText = findViewById(R.id.checkIn1);
        }

        // Get the ability check result, or return no damage for an empty or non-integer ability check field
        checkResultString = checkResultText.getText().toString();
        if (checkResultString.isEmpty() || checkResultString.equals("-"))
            return "-";
        else checkResult = Integer.parseInt(checkResultString);

        // Assign the proper terrainTypeIndex to look up in terrainModTab, chosen from the dropdown
        terrainTypeIndex = java.util.Arrays.asList(terrainTypes).indexOf(dropdownSelection);

        // Apply the terrain hardness modifier to the Acrobatics check result
        checkResult += terrainModTab[terrainTypeIndex];

        // Choose the appropriate index for the fall distance modifier, calculated from the modified Acrobatics check
        distModIndex = checkResult/5 + 4;
        if (checkResult < 0 && checkResult % 5 != 0) // applies correction to calculation for negative numbers
            distModIndex--;

        // Apply boundaries to calculated index
        if (distModIndex > 20)
            distModIndex = 20;
        else if (distModIndex < 0)
            distModIndex = 0;

        // Calculate effective distance fallen, with terminal velocity reached after 500'
        // (Note that damage continues scaling, because distMod can be > 1)
        if (fallDist > 500)
            fallDist = 500;
        distMod = distModTab[distModIndex];
        fallDist *= distMod;

        // Divide by 10 to prepare index for damageDieTab (index = distance / 10)
        // Also round to nearest whole number (representing nearest 10' increment)
        fallDist /= 10;
        fallDist = Math.round(fallDist);

        // Return the damage dice for the effective distance fallen
        return damageDieTab[(int)fallDist];
    }

    // Called when the user taps the Calculate button
    // Intent and putExtra function usage from online tutorial
    public void showDamage(View view) {
        Intent resultsIntent = new Intent(this, DisplayMessageActivity.class);

        String result[] = new String[6];
        String names[] = new String[6];

        // Calculate damage for each of the six fields in the app
        for (int i = 0; i < 6; i++)
            result[i] = calculateDamage(i + 1);

        // If a name is not entered corresponding to a given check, instead display the check result
        // This distinguishes the damage dice even when multiple nameless entries are present
        names[0] = ((EditText)findViewById(R.id.name1)).getText().toString();
        if (names[0].length() == 0)
            names[0] = ((EditText)findViewById(R.id.checkIn1)).getText().toString();

        names[1] = ((EditText)findViewById(R.id.name2)).getText().toString();
        if (names[1].length() == 0)
            names[1] = ((EditText)findViewById(R.id.checkIn2)).getText().toString();

        names[2] = ((EditText)findViewById(R.id.name3)).getText().toString();
        if (names[2].length() == 0)
            names[2] = ((EditText)findViewById(R.id.checkIn3)).getText().toString();

        names[3] = ((EditText)findViewById(R.id.name4)).getText().toString();
        if (names[3].length() == 0)
            names[3] = ((EditText)findViewById(R.id.checkIn4)).getText().toString();

        names[4] = ((EditText)findViewById(R.id.name5)).getText().toString();
        if (names[4].length() == 0)
            names[4] = ((EditText)findViewById(R.id.checkIn5)).getText().toString();

        names[5] = ((EditText)findViewById(R.id.name6)).getText().toString();
        if (names[5].length() == 0)
            names[5] = ((EditText)findViewById(R.id.checkIn6)).getText().toString();

        // Pass the damage dice and the names to the results page
        resultsIntent.putExtra(EXTRA_RESULTS, result);
        resultsIntent.putExtra(EXTRA_NAMES, names);

        startActivity(resultsIntent);
    }

}
