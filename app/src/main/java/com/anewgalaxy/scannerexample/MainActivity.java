package com.anewgalaxy.scannerexample;

/**
 * Copyright (C) 2020 Tyler Sizse
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
