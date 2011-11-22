package com.cs122b.group10.moviequiz;

import android.os.*;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Statistics extends Activity {
	
	private AlertDialog.Builder builder;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);
        

        Button goBack = (Button) findViewById(R.id.goBackButton);
        
        goBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
				finish();
            }

        });
        
        Button resetStats = (Button) findViewById(R.id.resetStatsButton);
        
        //build reset stats dialog
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to reset statistics?")
	       .setCancelable(false)
	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                //TODO RESET STATS
	           }
	       })
	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	           }
	       });
        
        resetStats.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
				//ask for confirmation and reset stats
            	AlertDialog alert = builder.create();
                alert.show();
            }

        });
	}
}
