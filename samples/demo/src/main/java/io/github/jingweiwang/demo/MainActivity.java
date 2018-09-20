/*
 * Copyright 2018 JingweiWang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.jingweiwang.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_demo_android_native).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AndroidNativeFAActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btn_demo_1).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoopActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btn_demo_2).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OnceActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btn_demo_3).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OnceAndGoneActivity.class);
            startActivity(intent);
        });
    }
}
