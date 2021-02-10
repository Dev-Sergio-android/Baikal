package itmo.bluetoothChecker;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;


public class NewDeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater layoutInflater;
    private ArrayList<BluetoothDevice> mDevices;
    private int  mViewResourceId;

    public NewDeviceListAdapter(Context context, int resource, ArrayList<BluetoothDevice> devices){
        super(context, resource, devices);
        this.mDevices = devices;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = resource;
    }

    @NonNull
    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        try {

            convertView = layoutInflater.inflate(mViewResourceId, null);
            TextView deviceName =  convertView.findViewById(R.id.deviceName);
            BluetoothDevice device = mDevices.get(position);

            if (device != null) {
                //TextView deviceName =  convertView.findViewById(R.id.deviceName);
                TextView deviceAddress = convertView.findViewById(R.id.deviceAddress);


                if (device.getName() != null) {
                    deviceName.setText(device.getName());
                }else {
                    device.getName();
                    deviceName.setText(R.string.unnamed);
                }
                if (deviceAddress != null) {
                    deviceAddress.setText(device.getAddress());
                }
            }

        }catch (Exception e){
            Log.e ("Adapter: ","Fuck up");
        }
        return convertView;
    }

}
