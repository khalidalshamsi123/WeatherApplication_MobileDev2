package com.example.mob_dev_portfolio;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mob_dev_portfolio.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected ActivityMainBinding binding;

    protected List<CheckBox> checkBoxList = new ArrayList<>();

    private int progressInc;

    public static final String channelID = "type1";

    private MidnightReceiver midnightReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Register the broadcast receiver
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        BroadCastReceiver receiver = new BroadCastReceiver();
        registerReceiver(receiver, filter);

        //register midnight reset receiver
        midnightReceiver = new MidnightReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(midnightReceiver, intentFilter);

        //gives function to the add button
        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCheckBox();
            }
        });

        //gives function to the reset button
        binding.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset(MainActivity.this);
            }
        });


        binding.floatingWeatherPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                startActivity(intent);
            }
        });

        binding.helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "For accurate progression, press reset after adding/deleting a tree if there were checked trees", Toast.LENGTH_SHORT).show();
            }
        });

        //reloads the checkboxes when the app is rerun
        int containerSize = sharedPreferences.getInt("containerSize", 0);
        if(containerSize > 0) {
            for(int i = 0; i < containerSize; i++) {
                boolean isChecked = sharedPreferences.getBoolean("checkbox_" + i, false);
                String text = sharedPreferences.getString("checkbox_text_" + i, "");
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(text);
                checkBox.setChecked(isChecked);
                checkBoxList.add(checkBox);
                addCheckBoxToLayout(checkBox);
            }
        }

        int progress = sharedPreferences.getInt("progress", 0);
        binding.progressBar.setProgress(progress);
        updateProgressText(progress);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister the receiver
        unregisterReceiver(midnightReceiver);
    }


    protected void updateProgressText(int progress){
        int percent = (int) ((double) progress / binding.progressBar.getMax() * 100);
        String text = percent + "%";
        binding.progressNumber.setText(text);
    }

    private int incrementalChange(int inc){
        int size = checkBoxList.size(); //sets variable size to the size of the list
        inc = 100 / size;   //sets the incremental change to be 100 divide by the number of checkboxes
        return inc;
    }

    private void addNewCheckBox(){

        //creating a new checkbox
        CheckBox checkBox = new CheckBox(this);
        int count = checkBoxList.size() + 1;
        checkBox.setText("Tree " + count);

        //add the checkbox to the list
        checkBoxList.add(checkBox);

        //creating the edit button
        ImageButton editButton = new ImageButton(this);
        editButton.setImageResource(R.drawable.ic_edit);
        editButton.setBackgroundColor(Color.parseColor("#78FF00"));
        editButton.setPadding(5,5,5,5);


        //creating a delete button next to the checkbox and setting an image icon
        ImageButton deleteButton = new ImageButton(this);
        deleteButton.setImageResource(R.drawable.ic_delete);
        deleteButton.setBackgroundColor(Color.parseColor("#A0B02A"));
        deleteButton.setPadding(5,5,5,5);

        // set layout parameters to make the delete button smaller
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(16, 0, 0, 0); // add left margin
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.width = 100; // set width
        deleteButton.setLayoutParams(lp); // delete button layout set
        editButton.setLayoutParams(lp); //edit button layout set


        //making the edit button function
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout container = (LinearLayout) v.getParent(); // get the parent layout
                CheckBox cb = (CheckBox) container.getChildAt(0); // get the checkbox view
                editTextDialog(cb); // show edit dialog for the selected checkbox
            }
        });

        //making the delete button function
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout container = (LinearLayout) v.getParent(); // get the parent layout
                CheckBox cb = (CheckBox) container.getChildAt(0); // get the checkbox view
                binding.checkboxContainer.removeView(container); // remove checkbox and delete button from layout
                checkBoxList.remove(cb); // remove checkbox from list
                updateProgressText(binding.progressBar.getProgress()); // update progress bar text
                updatingSharedPreferences();
            }
        });

        // add checkbox and delete button to the layout
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.HORIZONTAL); //sets the layout to be horizontal

        container.addView(checkBox);
        container.addView(editButton);
        container.addView(deleteButton);
        binding.checkboxContainer.addView(container);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("containerSize", checkBoxList.size());
        for(int i = 0; i < checkBoxList.size(); i++) {
            editor.putBoolean("checkbox_" + i, checkBoxList.get(i).isChecked());
            editor.putString("checkbox_text_" + i, checkBoxList.get(i).getText().toString());
        }
        editor.apply();

        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        container.addView(line);


        checkBox.setOnCheckedChangeListener(checkBoxListener);

        progressInc = 100 / checkBoxList.size();
    }

    private CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int currentProg = binding.progressBar.getProgress();
            if (isChecked) {
                int newprog = currentProg + incrementalChange(progressInc);

                if (newprog > binding.progressBar.getMax()) {
                    newprog = binding.progressBar.getMax();
                    //updatingSharedPreferences();

                }
                if(newprog >= 96){
                    newprog = binding.progressBar.getMax();
                }

                binding.progressBar.setProgress(newprog);
                // Save the progress using SharedPreferences
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("progress", binding.progressBar.getProgress());
                editor.apply();

                updateProgressText(newprog);
                updatingSharedPreferences();
            }
            else{
                int newprog = currentProg - incrementalChange(progressInc);

                if (newprog < binding.progressBar.getMin()) {
                    newprog = binding.progressBar.getMin();
                    //updatingSharedPreferences();
                }

                binding.progressBar.setProgress(newprog);
                // Save the progress using SharedPreferences
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("progress", binding.progressBar.getProgress());
                editor.apply();

                updateProgressText(newprog);
            }
        }
    };

    private void editTextDialog(CheckBox checkBox){

        //creates the dialog and sets the title
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Tree name");

        //creates the text input field
        EditText editText = new EditText(this);
        editText.setText(checkBox.getText());
        builder.setView(editText);

        //setting up the save button
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newText = editText.getText().toString();
                checkBox.setText(newText);
                updatingSharedPreferences();
            }
        });

        //setting up the cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    //loads the check boxes back when the app is restarted
    private void addCheckBoxToLayout(CheckBox checkBox) {
        //creating the edit button
        ImageButton editButton = new ImageButton(this);
        editButton.setImageResource(R.drawable.ic_edit);
        editButton.setBackgroundColor(Color.parseColor("#78FF00"));
        editButton.setPadding(5,5,5,5);


        //creating a delete button next to the checkbox and setting an image icon
        ImageButton deleteButton = new ImageButton(this);
        deleteButton.setImageResource(R.drawable.ic_delete);
        deleteButton.setBackgroundColor(Color.parseColor("#A0B02A"));
        deleteButton.setPadding(5,5,5,5);

        // set layout parameters to make the delete button smaller
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(16, 0, 0, 0); // add left margin
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.width = 100; // set width
        deleteButton.setLayoutParams(lp); // delete button layout set
        editButton.setLayoutParams(lp); //edit button layout set


        //making the edit button function
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout container = (LinearLayout) v.getParent(); // get the parent layout
                CheckBox cb = (CheckBox) container.getChildAt(0); // get the checkbox view
                editTextDialog(cb); // show edit dialog for the selected checkbox
            }
        });

        //making the delete button function
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout container = (LinearLayout) v.getParent(); // get the parent layout
                CheckBox cb = (CheckBox) container.getChildAt(0); // get the checkbox view
                binding.checkboxContainer.removeView(container); // remove checkbox and delete button from layout
                checkBoxList.remove(cb); // remove checkbox from list
                updateProgressText(binding.progressBar.getProgress()); // update progress bar text
                updatingSharedPreferences();
            }
        });


        // add checkbox and delete button to the layout
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.HORIZONTAL);

        container.addView(checkBox);
        container.addView(editButton);
        container.addView(deleteButton);
        binding.checkboxContainer.addView(container);

        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        container.addView(line);

        checkBox.setOnCheckedChangeListener(checkBoxListener);

        progressInc = 100 / checkBoxList.size();
    }

    protected void updatingSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("containerSize", checkBoxList.size());
        for(int i = 0; i < checkBoxList.size(); i++) {
            editor.putBoolean("checkbox_" + i, checkBoxList.get(i).isChecked());
            editor.putString("checkbox_text_" + i, checkBoxList.get(i).getText().toString());
        }
        editor.apply();
    }


    public void reset(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        binding.progressBar.setProgress(0);
        updateProgressText(0);

        for(CheckBox checkBox: checkBoxList){
            checkBox.setChecked(false);
        }
        for(int i = 0; i < checkBoxList.size(); i++) {
            editor.putBoolean("checkbox_" + i, false);
        }

        // Clear the shared preferences
        editor.putInt("progress", binding.progressBar.getProgress());
        editor.apply();
    }


}
