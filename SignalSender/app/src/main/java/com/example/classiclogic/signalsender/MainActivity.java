package com.example.classiclogic.signalsender;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity
        implements BluetoothConnectActivityFragment.MainActivityCommInterface {

    public final String LOGTAG = "SIG_SENDER";

    public ConnectedThread connectedThread = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button sendButton = (Button) findViewById(R.id.button);
        final EditText editText = (EditText) findViewById(R.id.editText);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOGTAG, " SENDBUTTON clicked!");
                String text = editText.getText().toString();

                if( connectedThread != null)    {
                    connectedThread.write(text.getBytes());
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch(id) {
            case R.id.menuitem_btpair:
                Log.v(LOGTAG, "Bluetooth Icon Clicked!");

                Intent btConnectIntent = new Intent(this, BluetoothConnectActivity.class);
                startActivity(btConnectIntent);
                return true;

            case R.id.action_settings:
                return true;

            default:
                // The user's action was not recognized. Calling superclass method.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void sendConnectedThread(ConnectedThread connectedThread) {
        this.connectedThread = connectedThread;
    }
}