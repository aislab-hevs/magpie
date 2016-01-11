package ch.hevs.aislab.magpie.bioharness;

import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;

import java.util.Set;

import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.event.MagpieEvent;
import ch.hevs.aislab.magpie.sensor.SensorHandler;
import zephyr.android.BioHarnessBT.BTClient;

/**
 * Handles the connection between the phone and the BioHarness sensor.
 */
public class BioHarnessHandler extends SensorHandler {

    private final String TAG = getClass().getName();

    public static final int PHONE_NO_BLUETOOTH = 100;
    public static final int BLUETOOTH_NOT_ACTIVE = 101;
    public static final int BIOHARNESS_NOT_PAIRED = 102;
    public static final int BIOHARNESS_PAIRED = 103;
    public static final int BIOHARNESS_CONNECTED = 104;
    public static final int BIOHARNESS_ALREADY_CONNECTED = 105;

    private BTClient btc;
    private BioHarnessConnectListenerImpl connListener;

    public BioHarnessHandler(Looper looper) {
        super(looper);
    }

    @Override
    public int onStartConnection() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice bioharness = null;
        int replyCode;
        if (bluetoothAdapter == null) {
            replyCode = PHONE_NO_BLUETOOTH;
        } else if (!bluetoothAdapter.isEnabled()) {
            replyCode = BLUETOOTH_NOT_ACTIVE;
        } else {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().startsWith("BH")) {
                        bioharness = device;
                    }
                }
            }
            if (bioharness == null) {
                replyCode = BIOHARNESS_NOT_PAIRED;
            } else {
                replyCode = BIOHARNESS_PAIRED;
                if (btc != null) {
                    replyCode = BIOHARNESS_ALREADY_CONNECTED;
                } else {
                    boolean result = connect(bioharness);
                    if (result) {
                        replyCode = BIOHARNESS_CONNECTED;
                        connectToAgentEnvironment();
                    }
                }
            }
        }
        return replyCode;
    }

    @Override
    public void onStopConnection() {
        if (btc.IsConnected()) {
            btc.Close();
        }
    }

    @Override
    public MagpieEvent processSensorMessage(Message message) {
        Bundle bundle = message.getData();
        return bundle.getParcelable(BioHarnessConnectListenerImpl.MAGPIE_LTE_EVENT);
    }

    @Override
    public void onAlertProduced(LogicTupleEvent alert) {
        Notification.Builder notificationBuilder = new Notification.Builder(
                getSensorService().getApplicationContext())
                .setTicker("MAGPIE Alert!")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentText(alert.toTuple())
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSensorService()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }

    private boolean connect(BluetoothDevice bioharness) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btc = new BTClient(bluetoothAdapter, bioharness.getAddress());
        connListener = new BioHarnessConnectListenerImpl(this);
        btc.addConnectedEventListener(connListener);
        if (btc.IsConnected()) {
            btc.start();
            return true;
        } else {
            btc = null;
            connListener = null;
            return false;
        }
    }
}
