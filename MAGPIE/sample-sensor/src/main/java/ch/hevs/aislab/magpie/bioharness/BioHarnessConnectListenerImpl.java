package ch.hevs.aislab.magpie.bioharness;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.sensor.SensorHandler;
import zephyr.android.BioHarnessBT.BTClient;
import zephyr.android.BioHarnessBT.BTComms;
import zephyr.android.BioHarnessBT.ConnectListenerImpl;
import zephyr.android.BioHarnessBT.ConnectedEvent;
import zephyr.android.BioHarnessBT.PacketTypeRequest;
import zephyr.android.BioHarnessBT.ZephyrPacketArgs;
import zephyr.android.BioHarnessBT.ZephyrPacketEvent;
import zephyr.android.BioHarnessBT.ZephyrPacketListener;
import zephyr.android.BioHarnessBT.ZephyrProtocol;

public class BioHarnessConnectListenerImpl extends ConnectListenerImpl {

    private final String TAG = getClass().getName();

    public static final String MAGPIE_LTE_EVENT = "LogicTupleEvent";
    public static final String HEART_RATE = "heart_rate";

    /** Packet Identifiers */
    protected final int GENERAL_PACKET_ID = 32;

    /** Objects for the different types of packets */
    private PacketTypeRequest pckTypeRq = new PacketTypeRequest();

    private GeneralPacketInfo gpPacketInfo = new GeneralPacketInfo();

    /** Handler to send the physiological values from the BioHarness */
    private BioHarnessHandler handler;

    public BioHarnessConnectListenerImpl(BioHarnessHandler handler) {
        super(handler, null);
        this.handler = handler;
    }

    @Override
    public void Connected(ConnectedEvent<BTClient> eventArgs) {
        pckTypeRq.EnableGP(true);

        BTComms btComms = eventArgs.getSource().getComms();
        ZephyrProtocol protocol = new ZephyrProtocol(btComms, pckTypeRq);

        protocol.addZephyrPacketEventListener(new ZephyrPacketListener() {
            @Override
            public void ReceivedPacket(ZephyrPacketEvent zephyrPacketEvent) {
                ZephyrPacketArgs packetArgs = zephyrPacketEvent.getPacket();
                int packetID = packetArgs.getMsgID();
                byte[] dataArray = packetArgs.getBytes();

                switch (packetID) {
                    case GENERAL_PACKET_ID:
                        byte worn = gpPacketInfo.GetWornStatus(dataArray);
                        if (worn == 1) { // Send values only when the device is attached to the strap
                            Message msg = handler.obtainMessage();
                            msg.arg1 = SensorHandler.SEND_MESSAGE;
                            Bundle bundle = new Bundle();

                            // Put the Heart Rate in the Bundle as a LogicTupleEvent
                            int heartRateNum = gpPacketInfo.GetHeartRate(dataArray);
                            String heartRate = String.valueOf(heartRateNum);
                            LogicTupleEvent ev = new LogicTupleEvent(HEART_RATE, heartRate);
                            bundle.putParcelable(MAGPIE_LTE_EVENT,ev);
                            // Put the bundle in the message and send it back to the Handler
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                            break;
                        }
                    default:
                        Log.e(TAG, "Packet type '" + packetID + "' from BioHarness sensor not processed");
                }
            }
        });
    }
}