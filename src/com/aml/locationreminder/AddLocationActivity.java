package com.aml.locationreminder;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aml.locationreminder.R.id;
import com.aml.locationreminder.databasecomponent.LocationReminderDatabaseAccessUtility;
import com.aml.locationreminder.databasecomponent.LocationReminderDatabaseHandler;
import com.aml.locationreminder.servicecomponent.LocationReminderService;

public class AddLocationActivity extends Activity {

	Button okButton, cancelButton;
	EditText locationNmaeEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_location);

		okButton = (Button) findViewById(id.ok_button);
		cancelButton = (Button) findViewById(id.cancel_button);
		locationNmaeEditText = (EditText) findViewById(id.locationName_editText);

		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (locationNmaeEditText.getText().toString().trim().length() > 0) {
					ContentValues cv = new ContentValues();
					cv.put(LocationReminderDatabaseHandler.UserTable.id,
							locationNmaeEditText.getText().toString().trim());
					cv.put(LocationReminderDatabaseHandler.UserTable.latitude,
							0);
					cv.put(LocationReminderDatabaseHandler.UserTable.longitude,
							0);
					getContentResolver().insert(
							LocationReminderDatabaseAccessUtility.CONTENT_URI,
							cv);
					makeChangeInService();
					finish();
				} else {

					Toast.makeText(getApplicationContext(), "Enter input",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_location, menu);
		return true;
	}

	private void makeChangeInService() {

		if (LocationReminderService.SERVICE_COUNT >= 1) {
			startService(new Intent(getApplicationContext(),
					LocationReminderService.class));
		}
	}

}
