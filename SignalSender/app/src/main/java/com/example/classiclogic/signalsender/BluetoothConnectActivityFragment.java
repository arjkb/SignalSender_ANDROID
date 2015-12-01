package com.example.classiclogic.signalsender;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class BluetoothConnectActivityFragment extends Fragment {

    public final String LOGTAG = "SIG_SENDER";

    BluetoothAdapter bluetoothAdapter;

    CommunicationInterface communicationCallback;

    public BluetoothConnectActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_connect, container, false);
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

    public interface CommunicationInterface {
        void getBluetoothAdapter(BluetoothAdapter bluetoothAdapter);
    }
}
