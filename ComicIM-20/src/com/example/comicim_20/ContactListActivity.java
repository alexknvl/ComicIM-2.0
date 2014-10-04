package com.example.comicim_20;

import java.util.ArrayList;
import java.util.List;

import com.example.comicim_20.contactlist.ContactDatabaseHelper;
import com.example.comicim_20.contactlist.ContactListAdapter;
import com.example.comicim_20.messageview.MessageView;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;


public class ContactListActivity extends ActionBarActivity {
	private static String TAG = ContactListActivity.class.getName();
	
	public ContactDatabaseHelper databaseHelper;
	public List<Contact> contacts;
	public ListView contactListView;
	public ContactListAdapter contactListViewAdapter;
	private final int PICK_CONTACT = 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list_activity);
        
        contactListView = (ListView) this.findViewById(R.id.contact_list);
        
        databaseHelper = new ContactDatabaseHelper(this);
        contacts = databaseHelper.getAllContacts();
        
        contactListViewAdapter = new ContactListAdapter(this, contacts);
        contactListView.setAdapter(contactListViewAdapter);
        
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i(TAG, "Clicked on " + contacts.get(position).phoneNumber);
				Intent intent = new Intent(view.getContext(), MessageView.class);
				startActivity(intent);
			}
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add_conversation){
        	Intent contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
			contactIntent.setType(Phone.CONTENT_TYPE); // This line is important! 
			startActivityForResult(contactIntent, PICK_CONTACT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
			switch(requestCode) {
			case(PICK_CONTACT):
				if(resultCode == Activity.RESULT_OK) {
					Uri contactUri = data.getData();
					String[] projection = {Phone.NUMBER};
					Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
					cursor.moveToFirst();
					int column = cursor.getColumnIndex(Phone.NUMBER);
					
					String phoneNumber = PhoneNumberUtils.stripSeparators(cursor.getString(column));
					
					Contact contact = this.databaseHelper.newContact(phoneNumber);
					contacts.add(contact);
					contactListViewAdapter.notifyDataSetChanged();
				}
			}
	}
}
