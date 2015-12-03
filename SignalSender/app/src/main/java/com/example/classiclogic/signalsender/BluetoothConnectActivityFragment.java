package com.example.classiclogic.signalsender;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * A placeholder fragment containing a simple view.
 */
public class BluetoothConnectActivityFragment extends Fragment {

    public final String LOGTAG = "SIG_SENDER";

    CommunicationInterface communicationCallback;

    BluetoothAdapter bluetoothAdapter = null;
    BluetoothDevice clickedDevice = null;


    ListView bondedList;
    Activity activity;

    public BluetoothConnectActivityFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = getActivity();

        try {
            communicationCallback = (CommunicationInterface) getActivity();
            //bluetoothAdapter = communicationCallback.getBluetoothAdapter();

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if( bluetoothAdapter != null )  {
                Log.v(LOGTAG, " Got bluetooth adapter from " + activity.toString());
            } else  {
                Log.v(LOGTAG, " Received bluetoothAdapter is null");
            }

        } catch (ClassCastException E)  {
            Log.v(LOGTAG, " ClassCastException: " + activity.toString());
            throw new ClassCastException(activity.toString()
                    + " must implement CommunicationInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth_connect, container, false);


        bondedList = (ListView) view.findViewById(R.id.mylist_btdevices);
        if( bondedList == null )    {
            Log.v(LOGTAG, " bondedList is null");
        } else  {
            Log.v(LOGTAG, " bondedList is not null");
        }
        bondedList.setAdapter((ListAdapter) getPairedDevicesArrayAdapter(bluetoothAdapter));

        bondedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedDevice = (BluetoothDevice) bondedList.getItemAtPosition(position);

                Log.v(LOGTAG, " Clicked Device " + clickedDevice);
            }
        });

        return view;
    }

    public interface CommunicationInterface {
        BluetoothAdapter getBluetoothAdapter();
    }

    private ArrayAdapter<BluetoothDevice> getPairedDevicesArrayAdapter(BluetoothAdapter btAdapter)  {
        ArrayAdapter<BluetoothDevice> mArrayAdapter = new ArrayAdapter<BluetoothDevice>(getContext(), android.R.layout.simple_list_item_1);

        Set<BluetoothDevice> pairedDevices = null;
        try {
            if( btAdapter != null ) {
                pairedDevices = btAdapter.getBondedDevices();
                Log.v(LOGTAG, " btAdapter is not null");
            } else  {
                Log.v(LOGTAG, " btAdapter is null");
            }

        } catch (NullPointerException E)    {
            Log.v(LOGTAG, "NullPointerException " + E.toString());
        } catch (Exception E)   {
            Log.v(LOGTAG, "Exception " + E.toString());
        }

        if( pairedDevices == null ) {
            Log.v(LOGTAG, " pairedDevices is null! ");
        }

        for(BluetoothDevice device: pairedDevices)  {
            mArrayAdapter.add(device);
        }

        return mArrayAdapter;
    }
}

private class ConnectThread  extends Thread {

    private final BluetoothDevice connectedDevice;
    private final BluetoothSocket mSocket;
    private final BluetoothAdapter bluetoothAdapter;

    public final String LOGTAG = "SIG_SENDER";

    private UUID MY_UUID;

    public ConnectThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter)    {

        MY_UUID = UUID.fromString("406b2483-c8c6-4585-8cf1-062d6a7b8ac9");

        this.bluetoothAdapter = bluetoothAdapter;
        this.connectedDevice = device;
        BluetoothSocket tempSocket = null;

        try {
            tempSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException E) {
            Log.v(LOGTAG, " IOException while creating RfComm Socket " + E);
        } catch (Exception E) {
            Log.v(LOGTAG, " " + E);
        }

        mSocket = tempSocket;
    }

    @Override
    public void run() {
        //super.run();
        bluetoothAdapter.cancelDiscovery();

        try {
            // connect the device through the socket
            // Blocking call
            mSocket.connect();
        } catch (IOException E) {
            // unable to connect. Close the socket and get out.
            try {
                mSocket.close();
            } catch (Exception E) {
                Log.v(LOGTAG, " Cannot close socket! " + E);
            }
            return;
        }

    }
}

/*class DeviceListeArrayAdapter extends ArrayAdapter<BluetoothDevice> {
    DeviceListeArrayAdapter(Context context, int resource)  {
        super(context, resource);
    }

    @Override
    public void add(BluetoothDevice object) {
        super.add(object);
    }

    @Override
    public String toString() {
        return "";
    }
}*/
