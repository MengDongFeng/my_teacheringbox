package com.example.teachingbox3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_interface);
        ImageButton button = findViewById(R.id.programming);
        button.setOnClickListener(this);
        ImageButton button1 = findViewById(R.id.run);
        button1.setOnClickListener(this);

    }

    @Override
     public void onClick(View v){
         switch (v.getId()) {
             case R.id.programming:
                 startActivity(new Intent(MainActivity.this, RunActivity.class));
                 break;
             case R.id.run:
                 startActivity(new Intent(MainActivity.this, RealclientActivity.class));
                 break;
             default:
             break;
          }
     }

}
