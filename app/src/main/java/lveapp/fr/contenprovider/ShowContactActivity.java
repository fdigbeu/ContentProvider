package lveapp.fr.contenprovider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * This activity can be use in an other app
 */
public class ShowContactActivity extends AppCompatActivity {

    static final String PROVIDER_NAME = "lveapp.fr.contentprovider.ContactProvider";
    static final String CP_CONTACTS = "cpcontacts";
    static final String URL = "content://"+PROVIDER_NAME+"/"+CP_CONTACTS;
    static final Uri CONTENT_URL = Uri.parse(URL);

    private ContentResolver resolver;

    private EditText userIDToDelete, userIDToFind, userNameToAdd;
    private Button showContacts, deleteUserByID, findUserByID, addUserByName;
    private TextView listContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contact);

        resolver = getContentResolver();

        listContacts = (TextView) findViewById(R.id.listContacts);

        userIDToDelete = (EditText)findViewById(R.id.userIDToDelete);
        userIDToFind = (EditText)findViewById(R.id.userIDToFind);
        userNameToAdd = (EditText)findViewById(R.id.userNameToAdd);

        showContacts = (Button)findViewById(R.id.showContacts);
        showContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContacts();
            }
        });

        deleteUserByID = (Button)findViewById(R.id.deleteUserByID);
        deleteUserByID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = userIDToDelete.getText().toString().trim();
                if(userId.length() > 0){
                    deleteContact(userId);
                }
                else{
                    userIDToDelete.setError(getResources().getString(R.string.field_required));
                }
            }
        });
        findUserByID = (Button)findViewById(R.id.findUserByID);
        findUserByID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = userIDToFind.getText().toString().trim();
                if(userId.length() > 0){
                    findContact(userId);
                }
                else{
                    userIDToFind.setError(getResources().getString(R.string.field_required));
                }
            }
        });
        addUserByName = (Button)findViewById(R.id.addUserByName);
        addUserByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = userNameToAdd.getText().toString().trim().replace("'", "''");
                if(name.length() > 0){
                    addContact(name);
                }
                else{
                    userNameToAdd.setError(getResources().getString(R.string.field_required));
                }
            }
        });
    }

    public void getContacts(){
        // ContactProvider fields
        String[] projection = new String[]{"id", "name"};
        Cursor cursor = resolver.query(CONTENT_URL, projection, null, null, null);
        String contactList = "";
        if(cursor.moveToFirst()){
            do{
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                contactList += id+" : "+name+"\n";
            }while (cursor.moveToNext());
        }
        listContacts.setText(contactList);
    }

    public void deleteContact(String idContact){
        long idDeleted = resolver.delete(CONTENT_URL, "id = ?", new String[]{idContact});
        getContacts();
    }

    public void findContact(String idContact){
        // ContactProvider fields
        String[] projection = new String[]{"id", "name"};
        Cursor cursor = resolver.query(CONTENT_URL, projection, "id = ?", new String[]{idContact}, null);
        String contact = "";
        if(cursor.moveToFirst()){
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            contact += id+" : "+name;
        }
        listContacts.setText(contact);
    }

    public void addContact(String contactName){
        ContentValues values = new ContentValues();
        values.put("name", contactName);
        resolver.insert(CONTENT_URL, values);
        getContacts();
    }
}
