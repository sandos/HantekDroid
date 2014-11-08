package com.example.john.usbtest;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.*;
import android.widget.Toast;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        UsbDevice device = (UsbDevice) getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
		if(device==null) return;
		Log.e("majs","usb " + device.getDeviceName() + "|" + device.getProductId()+ "|" + device.getVendorId());
        UsbInterface uif = device.getInterface(1);
        UsbEndpoint endpoint = uif.getEndpoint(0);
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        UsbDeviceConnection connection = manager.openDevice(device);
		if(connection==null)return;
        connection.claimInterface(uif, true);

        int index = 0;
        for(int i=0; i<458; i++) {
            int size = FW.HT6022_Firmware[index] + ((FW.HT6022_Firmware[index+1])<<8);
            int value = FW.HT6022_Firmware[index+2] + ((FW.HT6022_Firmware[index+3])<<8);
			byte tmp[] = new byte[size];
			for(int j=0; j<size; j++) {
				tmp[j] = (byte)(FW.HT6022_Firmware[j+index+4] & 0xff);
				Log.e("majs", "" + tmp[j]);
			}
            int res = connection.controlTransfer(
                    FW.HT6022_FIRMWARE_REQUEST_TYPE,
                    FW.HT6022_FIRMWARE_REQUEST,
                    value,
                    FW.HT6022_FIRMWARE_INDEX,
                    tmp,
                    size,
                    3000);
            if(res != size ){
                Log.e("majs", "Did not transfer all of " + size + " just " + res);
                if(res < 0) {
                    Log.e("majs", "ERROR");
                }
            }
			index += size+4;

        }
        Toast toast = Toast.makeText(getApplicationContext(), "Did FW init", Toast.LENGTH_LONG);
        toast.show();
		Log.e("majs","usb " + endpoint.getMaxPacketSize());
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
