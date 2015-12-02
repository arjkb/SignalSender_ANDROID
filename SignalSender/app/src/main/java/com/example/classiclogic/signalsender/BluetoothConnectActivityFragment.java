package com.example.classiclogic.signalsender;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class BluetoothConnectActivityFragment extends Fragment {

    public final String LOGTAG = "SIG_SENDER";

    CommunicationInterface communicationCallback;

    BluetoothAdapter bluetoothAdapter = null;

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
