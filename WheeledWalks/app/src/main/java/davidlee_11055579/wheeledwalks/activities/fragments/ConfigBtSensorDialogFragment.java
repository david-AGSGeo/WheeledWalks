package davidlee_11055579.wheeledwalks.activities.fragments;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import davidlee_11055579.wheeledwalks.R;
import davidlee_11055579.wheeledwalks.controllers.DevicesDatabaseHelper;
import davidlee_11055579.wheeledwalks.models.Constants;
import davidlee_11055579.wheeledwalks.utilities.Utils;

/**
 * Created by David on 26/04/2016.
 */
public class ConfigBtSensorDialogFragment extends DialogFragment implements View.OnClickListener {


    public BluetoothDevice mDevice;
    private String mAlias;
    private DevicesDatabaseHelper mDevicesDatabaseHelper;

    private CheckBox mTagEnabled;


    private EditText mAliasEditText;



    public ConfigBtSensorDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static ConfigBtSensorDialogFragment newInstance(String title) {
        ConfigBtSensorDialogFragment frag = new ConfigBtSensorDialogFragment();


        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.dialog_fragment_config_bt_sensor, container, false);

        mDevicesDatabaseHelper = new DevicesDatabaseHelper(this.getContext());
                if (mDevice != null) {
                    mAlias = mDevicesDatabaseHelper.getAlias(mDevice.getAddress());
                } else {
                    Log.e(Constants.DEBUG_TAG, "No Bluetooth Device Found");
                    dismiss();
                }
            mAliasEditText = (EditText) rootView.findViewById(R.id.config_sensortag_dialog_alias_edittext);
            mAliasEditText.setText(mAlias);

        mTagEnabled = (CheckBox) rootView.findViewById( R.id.config_sensortag_dialog_connect_chkbx);

        if (mDevice != null) {
            mTagEnabled.setChecked(mDevicesDatabaseHelper.getSensorEnabled(mDevice.getAddress()));

        }


        Button cancelBtn = (Button) rootView.findViewById
                (R.id.config_sensortag_dialog_cancel_button);
        cancelBtn.setOnClickListener(this);
        Button connectBtn = (Button) rootView.findViewById
                (R.id.config_sensortag_dialog_save_config_button);
        connectBtn.setOnClickListener(this);




        return rootView;
    }

    @Override
    public void onResume() {
        getDialog().setTitle("Configure Device");
        getDialog().getWindow().setLayout(600, 460);
        super.onResume();
    }

    /**
     * Handles the buttons in a switch statement
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.config_sensortag_dialog_cancel_button):
                    this.dismiss();
                break;
            case (R.id.config_sensortag_dialog_save_config_button):
                if (mAliasEditText.getText().equals("Unknown Device" ) || mAliasEditText.getText().equals("")){
                    Utils.showToast(getContext(), "you need to set an Alias!");
                } else {
                    mDevicesDatabaseHelper.addOrUpdateDevice(mDevice, mAliasEditText.getText().toString(),
                            mTagEnabled.isChecked());

                    Intent replyIntent = new Intent(Constants.SENSOR_CONFIG_INTENT);
                    v.getContext().sendBroadcast(replyIntent);
                    this.dismiss();
                }
                break;


            default:
                Log.e(Constants.DEBUG_TAG, "Button not handled correctly");
                break;
        }
    }

}