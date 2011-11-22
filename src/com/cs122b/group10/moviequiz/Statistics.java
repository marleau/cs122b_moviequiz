package com.cs122b.group10.moviequiz;

import android.os.*;
import android.view.View;
import android.widget.Button;
import android.app.Activity;

public class Statistics extends Activity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);
        

        Button goBack = (Button) findViewById(R.id.goBackButton);
        
        goBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
				finish();
            }

        });
	}
}
