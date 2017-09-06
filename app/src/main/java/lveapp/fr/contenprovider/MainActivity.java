package lveapp.fr.contenprovider;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText userName;
    private Button addUserName, showContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = (EditText)findViewById(R.id.userName);
        addUserName = (Button)findViewById(R.id.addUserName);
        addUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = userName.getText().toString().trim().replace("'", "''");
                if(name.length() > 0){
                    ContentValues values = new ContentValues();
                    values.put(ContactProvider.name, name);
                    Uri uri = getContentResolver().insert(ContactProvider.CONTENT_URL, values);
                    Toast.makeText(MainActivity.this, "Row ("+name+") added correctly", Toast.LENGTH_LONG).show();
                    userName.setText("");
                }
                else{
                    userName.setError(getResources().getString(R.string.field_required));
                }
            }
        });

        showContacts = (Button)findViewById(R.id.showContacts);
        showContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ShowContactActivity.class);
                startActivity(intent);
            }
        });
    }
}
