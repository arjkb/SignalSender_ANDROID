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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * A placeholder fragment containing a simple view.
 */
public class BluetoothConnectActivityFragment extends Fragment {

    public final String LOGTAG = "SIG_SENDER";

    CommunicationInterface communicationCallback;
//    MainActivityCommInterface mainActivityCallback;
    BluetoothAdapter bluetoothAdapter = null;
    BluetoothDevice clickedDevice = null;


    ListView bondedItemsListView;
    ArrayAdapter pairedDevicesArrayAdapter;
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
            pairedDevicesArrayAdapter = getPairedDevicesArrayAdapter(bluetoothAdapter);

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

//        try     {
//            mainActivityCallback = (MainActivityCommInterface) getActivity();
//        } catch (ClassCastException E)  {
//            Log.v(LOGTAG, " ClassCastException: " + activity.toString());
//            throw new ClassCastException(activity.toString()
//                    + " must implement MainActivityCommInterface");
//        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth_connect, container, false);


        bondedItemsListView = (ListView) view.findViewById(R.id.mylist_btdevices);
        if( bondedItemsListView == null )    {
            Log.v(LOGTAG, " bondedItemsListView is null");
        } else  {
            Log.v(LOGTAG, " bondedItemsListView is not null");
        }

        bondedItemsListView.setAdapter((ListAdapter) pairedDevicesArrayAdapter);

        bondedItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedDevice = (BluetoothDevice) bondedItemsListView.getItemAtPosition(position);

                Log.v(LOGTAG, " Clicked Device " + clickedDevice.getName() + " " + clickedDevice.getAddress());

                ConnectThread connectThread = new ConnectThread(clickedDevice, bluetoothAdapter);
           //     connectThread.start();
                communicationCallback.sendConnectedThreadToMain(new ConnectedThread(connectThread.getSocket()));
          //
          //      getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        pairedDevicesArrayAdapter.notifyDataSetChanged();
    }

    public interface CommunicationInterface {
        BluetoothAdapter getBluetoothAdapter();
        void sendConnectedThreadToMain(ConnectedThread connectedThread);
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

class ConnectThread  extends Thread {

  //  private final BluetoothDevice mDevice;
    private final BluetoothSocket mSocket;
    private final BluetoothAdapter bluetoothAdapter;

    public final String LOGTAG = "SIG_SENDER";

    private UUID MY_UUID;

    public ConnectThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter)    {

        MY_UUID = UUID.fromString("406b2483-c8c6-4585-8cf1-062d6a7b8ac9");

        this.bluetoothAdapter = bluetoothAdapter;
        // this.mDevice = device;
        BluetoothSocket tempSocket = null;

        try {
            tempSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            Log.v(LOGTAG, " ConnectThread: Socket established with remote device!");
        } catch (IOException E) {
            Log.v(LOGTAG, " ConnectThread: IOException while creating RfComm Socket " + E);
        } catch (Exception E) {
            Log.v(LOGTAG, " ConnectThread: Exception " + E);
        }

        mSocket = tempSocket;
    }

    public BluetoothSocket getSocket()  {
        return mSocket;
    }

    @Override
    public void run() {
        //super.run();
        bluetoothAdapter.cancelDiscovery();

        try {
            // connect the device through the socket
            // Blocking call
            mSocket.connect();

        } catch (IOException IE) {
            // unable to connect. Close the socket and get out.
            try {
                mSocket.close();
            } catch (Exception E) {
                Log.v(LOGTAG, " ConnectThread: Cannot close socket! " + E);
            }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        Log.v(LOGTAG, " ConnectThread: Statement after connect()");
    }
}

class ConnectedThread {
    public final String LOGTAG = "SIG_SENDER";

    private final BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public ConnectedThread(BluetoothSocket socket)    {
        this.bluetoothSocket = socket;

        InputStream tempIn = null;
        OutputStream tempOut = null;

        try {
            tempIn = bluetoothSocket.getInputStream();
            tempOut = bluetoothSocket.getOutputStream();
        } catch (Exception E)   {
            Log.v(LOGTAG, " ConnectedThread: Exception occurred while getting input/output streams " + E.toString());
        }

        inputStream = tempIn;
        outputStream = tempOut;
    }

    public void write(byte []bytes) {
        try {
            outputStream.write(bytes);
        } catch (Exception E)   {
            Log.v(LOGTAG, " ConnectedThread: Error writing out to output stream " + E.toString());
        }
    }

    public void cancel()    {
        try {
            bluetoothSocket.close();
        } catch (Exception E)   {
            Log.v(LOGTAG, " ConnectedThread: Exception in cancel() " + E.toString());
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
