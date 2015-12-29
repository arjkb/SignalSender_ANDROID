package com.example.classiclogic.signalsender;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

public class BluetoothConnectActivity extends AppCompatActivity
        implements BluetoothConnectActivityFragment.CommunicationInterface {

    Activity activity;

    public final String LOGTAG = "SIG_SENDER";

    private final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothConnectActivityFragment btConnActivityFragment;

    MainActivityCommInterface mainActivityCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_bluetooth_connect);
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

        // get support action bar corresponding to the toolbar, and enable the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* set up bluetooth */
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            Log.v(LOGTAG, "This device does not support bluetooth");
        } else  {
            if(!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        activity = getParent();

        try     {
            mainActivityCallback = (MainActivityCommInterface) getParent();
        } catch (ClassCastException E)  {
            Log.v(LOGTAG, " ClassCastException: " + activity.toString());
            throw new ClassCastException(activity.toString()
                    + " must implement MainActivityCommInterface");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == REQUEST_ENABLE_BT )  {
            if( resultCode == RESULT_OK )   {
                Log.v(LOGTAG, "Bluetooth Enable Success");
            } else if( resultCode == RESULT_CANCELED )  {
                Log.v(LOGTAG, "Bluetooth Enable Failed");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(LOGTAG, "BTCON: onOptionsItemSelected");

        return super.onOptionsItemSelected(item);
    }

    @Override // Overrides method from the CommunicationInterface
    public BluetoothAdapter getBluetoothAdapter() {
        return this.bluetoothAdapter;
    }

    @Override
    public void sendConnectedThreadToMain(ConnectedThread connectedThread) {
        mainActivityCallback.sendConnectedThread(connectedThread);
    }

    public interface MainActivityCommInterface  {
        void sendConnectedThread(ConnectedThread connectedThread);
    }
}
