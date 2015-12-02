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
import android.widget.ListView;

import java.util.List;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class BluetoothConnectActivityFragment extends Fragment {

    public final String LOGTAG = "SIG_SENDER";

    CommunicationInterface communicationCallback;

    BluetoothAdapter bluetoothAdapter;
    ListView bondedList;

    public BluetoothConnectActivityFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = getActivity();

        try {
            communicationCallback = (CommunicationInterface) getActivity();
            communicationCallback.getBluetoothAdapter(bluetoothAdapter);
            Log.v(LOGTAG, " Got bluetooth adapter from " + activity.toString());
        } catch (ClassCastException E)  {
            Log.v(LOGTAG, " ClassCastException: " + activity.toString());
            throw new ClassCastException(activity.toString()
                    + " must implement CommunicationInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bondedList = (ListView) getActivity().findViewById(R.id.listView_btdevices);
        bondedList.setAdapter(getPairedDevicesArrayAdapter(bluetoothAdapter));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_connect, container, false);
    }


    public interface CommunicationInterface {
        void getBluetoothAdapter(BluetoothAdapter bluetoothAdapter);
    }

    private ArrayAdapter<BluetoothDevice> getPairedDevicesArrayAdapter(BluetoothAdapter btAdapter)  {
        ArrayAdapter<BluetoothDevice> mArrayAdapter = new ArrayAdapter<BluetoothDevice>(getContext(), R.layout.content_bluetooth_connect);
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        for(BluetoothDevice device: pairedDevices)  {
            mArrayAdapter.add(device);
        }

        return mArrayAdapter;
    }
}
