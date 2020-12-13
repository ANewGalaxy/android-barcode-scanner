package com.anewgalaxy.androidbarcodescanner;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the support action bar to the respective toolbar from the layout file
        setSupportActionBar((Toolbar) findViewById(R.id.main_action_bar));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the toolbar with the menu menu_main
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;

    }

}
