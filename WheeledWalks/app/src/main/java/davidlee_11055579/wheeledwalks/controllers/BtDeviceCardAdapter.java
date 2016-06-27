package davidlee_11055579.wheeledwalks.controllers;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import davidlee_11055579.wheeledwalks.R;
import davidlee_11055579.wheeledwalks.activities.fragments.ConfigBtSensorDialogFragment;
import davidlee_11055579.wheeledwalks.models.Constants;
import davidlee_11055579.wheeledwalks.utilities.Utils;

/**
 * Created by David on 20/04/2016.
 */
public class BtDeviceCardAdapter extends
        RecyclerView.Adapter<BtDeviceCardAdapter.ViewHolder> {

    // Store a member variable for the contacts
    private SparseArray<BluetoothDevice> mDevices;
    private Context mContext;


    // Pass in the contact array into the constructor
    public BtDeviceCardAdapter(Context context, SparseArray<BluetoothDevice> devices) {
        mDevices = devices;
        mContext = context;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public BtDeviceCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom layout
        View deviceView = inflater.inflate(R.layout.bt_device_card_view, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(deviceView, ((AppCompatActivity)mContext).getSupportFragmentManager());
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(BtDeviceCardAdapter.ViewHolder viewHolder, int position) {

        // Get the data model based on position
        int idKey = mDevices.keyAt(position);
        BluetoothDevice device = mDevices.get(idKey);

        if (device != null) {
            viewHolder.device = device;
            // Set item views based on the data model
            viewHolder.typeTextView.setText(device.getName());
            viewHolder.idTextView.setText(device.getAddress());
            viewHolder.aliasTextView.setText(GetAlias(device.getAddress()) + ":");
            DevicesDatabaseHelper devicesDatabaseHelper = new DevicesDatabaseHelper(mContext);
            if(devicesDatabaseHelper.getSensorEnabled(device.getAddress())) {
                viewHolder.iconImageView.setBackgroundColor(mContext.getResources()
                        .getColor(R.color.start_recording));
            }else {
                viewHolder.iconImageView.setBackgroundColor(mContext.getResources()
                        .getColor(R.color.pause_recording));
            }

        }

    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        //Log.d(Constants.DEBUG_TAG, "devices.size = : " + String.valueOf(mDevices.size()));
        return mDevices.size();
    }

    private String GetAlias(String deviceId){
        DevicesDatabaseHelper devicesDatabaseHelper = new DevicesDatabaseHelper(mContext);
        return devicesDatabaseHelper.getAlias(deviceId);
    }


    /**
     * ViewHolder Class
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView iconImageView;
        public TextView typeTextView;
        public TextView idTextView;
        public TextView aliasTextView;

        public BluetoothDevice device;



        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(final View itemView, FragmentManager fragmentManager) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            final FragmentManager myFragmentManager = fragmentManager;

            iconImageView = (ImageView) itemView.findViewById(R.id.bt_scan_rowIcon);
            typeTextView = (TextView) itemView.findViewById(R.id.bt_device_type_text);
            idTextView = (TextView) itemView.findViewById(R.id.bt_device_id_text);
            aliasTextView = (TextView) itemView.findViewById(R.id.bt_device_alias_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    //TODO:setup and connect device if not already connected

                    switch (device.getName()){
                        case "CC2650 SensorTag":

                            ConfigBtSensorDialogFragment connectSensorTagDialogFragment = ConfigBtSensorDialogFragment
                                    .newInstance("Configure Sensor Tag");
                            connectSensorTagDialogFragment.mDevice = device;
                            connectSensorTagDialogFragment.show(myFragmentManager, Constants.EDIT_DIALOG_FRAGMENT_TAG);


                        break;
                        case "Polar H7 3958881C":
                            ConfigBtSensorDialogFragment connectHeartRateDialogFragment = ConfigBtSensorDialogFragment
                                    .newInstance("Configure Heart Rate Monitor");
                            connectHeartRateDialogFragment.mDevice = device;
                            connectHeartRateDialogFragment.show(myFragmentManager, Constants.EDIT_DIALOG_FRAGMENT_TAG);

                            break;

                        default:
                            Utils.showToast(v.getContext(), "Unknown device type: " + device.getAddress());
                            break;
                    }
                }
            });
        }




    }

}