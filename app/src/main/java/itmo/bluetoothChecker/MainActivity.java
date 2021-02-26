package itmo.bluetoothChecker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import itmo.bluetoothChecker.ui.login.LoginActivity;
import itmo.bluetoothChecker.ui.main.SectionsPagerAdapter;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static itmo.bluetoothChecker.TabTwo.flagComplete;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_MULTIPLE_PERMISSION = 10010;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_SETTINGS = 12;
    private static final int REQUEST_ABOUT = 14;
    private static final int REQUEST_ENABLE_BT = 3;

    private static final String BLUETOOTH = Manifest.permission.BLUETOOTH;
    private static final String BLUETOOTH_ADMIN = Manifest.permission.BLUETOOTH_ADMIN;
    private static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private final String SEND = "MedSimTech_send_message";

    public BluetoothChatService mChatService = null;
    Boolean getMode;

    private long back_pressed;

    androidx.appcompat.widget.Toolbar toolbar;


    ////////////            !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!     ////////////
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();


            if (SEND.equals(action)) {
                String sendMessage = intent.getStringExtra("send_message_text");
                //customToast(sendMessage);
                sendSim(sendMessage);
            }

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                toolbar.setSubtitle(R.string.statusConnected);
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                toolbar.setSubtitle(R.string.title_not_connected);
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                        == BluetoothAdapter.STATE_ON) {
                    //TabTwo.bondList();
                    Log.i("MainActivity", "r:91");
                } else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                        == BluetoothAdapter.STATE_OFF) {
                    BluetoothChatService.disconnect();
                } else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                        == BluetoothAdapter.STATE_CONNECTED) {
                    toolbar.setSubtitle(R.string.statusConnected);
                }

            }
        }
    };


    private BluetoothAdapter mBluetoothAdapter = null;
    private String mConnectedDeviceName = null;
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            saveState(true);
                            //mConversationArrayAdapter.clear();
                            toolbar.setSubtitle(getString(R.string.title_connected_to, mConnectedDeviceName));
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            toolbar.setSubtitle(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                            saveState(false);
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            toolbar.setSubtitle(R.string.title_not_connected);
                            saveState(false);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    customToast("Отправлено:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    sendMes(readMessage);
                    customToast(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Toast.makeText(MainActivity.this, "Подключено к " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(MainActivity.this, msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle("не подключено");
        setSupportActionBar(toolbar);
        formatDate();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        /*if (mBluetoothAdapter == null) {
            customToast("Bluetooth is not available");
            finish();
        }*/


        SharedPreferences s = getSharedPreferences("manual_mode", MODE_PRIVATE);
        getMode = s.getBoolean("mode", false);


        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        final ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);


        viewPager.setCurrentItem(0); // show first tab

        mChatService = new BluetoothChatService(this, mHandler);

        final TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


        if (!isPermissionGranted()) {
            requestPermission();
        } else {
            if (ContextCompat.checkSelfPermission(this, BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
            } else if (shouldShowRequestPermissionRationale(BLUETOOTH)) {
            } else {
                // You can directly ask for the permission.
                requestPermission();
            }
        }

        //requestApplicationConfig();

        IntentFilter filter = new IntentFilter();
//        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
//        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);



        registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(mReceiver, new IntentFilter(SEND));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_find:
                if(mChatService.getState() == BluetoothChatService.STATE_CONNECTED){
                    mChatService.stop();
                    customToast("Устройство отключено от тренажера");
                }else{
                    customToast("Нет подключенных устройств");
                }

                break;

            case R.id.menu_conn:
                if(mBluetoothAdapter.isEnabled()){
                    Intent serverIntent = new Intent(this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                }else{
                    customToast("Включите Bluetooth");
                }

                break;

            case R.id.menu_bar_settings:
                if (flagComplete) {
                    mChatService.stop();
                    Intent LoginIntent = new Intent(this, LoginActivity.class);
                    startActivityForResult(LoginIntent, REQUEST_SETTINGS);
                    this.finish();
                } else {
                    customToast("Недоступно во время операции");
                }
                break;
            case R.id.menu_bar_about:
                if (flagComplete) {
                    Intent aboutIntent = new Intent(this, About.class);
                    startActivityForResult(aboutIntent, REQUEST_ABOUT);
                } else {
                    customToast("Недоступно во время операции");
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    customToast("Bluetooth включен");
                    Log.e("main", "BT enabled");
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.e("main", "BT not enabled");
                    customToast(getResources().getString(R.string.bt_not_enabled_leaving));
                    //finish();
                }
        }
    }

    private void sendMes(String message) {
        Intent intentMSM = new Intent();
        String RECEIVE = "MedSimTech_receive_message";
        intentMSM.setAction(RECEIVE);
        intentMSM.putExtra("receive_message_text", message);
        sendBroadcast(intentMSM);
    }

    private void setStatus(int resId) {
        final ActionBar actionBar = this.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    private void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = this.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        Bundle extras = data.getExtras();
        if (extras == null) {
            return;
        }
        String address = extras.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }


    /////////////////////////////////////////////   Runtime permisiion  ////////////////////////////

    private void sendSim(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            //customToast("Sending: " + message + " ...");
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

        }
    }

    private void openAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_MULTIPLE_PERMISSION);
    }

    private boolean isPermissionGranted() {
        // проверяем разрешение - есть ли оно у нашего приложения

        if (needRequestRuntimePermissions()) {
            int permissionCheckBluetooth = ActivityCompat.checkSelfPermission(this, MainActivity.BLUETOOTH);
            int permissionCheckBluetoothAdmin = ActivityCompat.checkSelfPermission(this, MainActivity.BLUETOOTH_ADMIN);
            int permissionCheckAccessCoarse = ActivityCompat.checkSelfPermission(this, MainActivity.ACCESS_COARSE_LOCATION);
            int permissionCheckAccessFine = ActivityCompat.checkSelfPermission(this, MainActivity.ACCESS_FINE_LOCATION);
            int permissionCheckWriteStorage = ActivityCompat.checkSelfPermission(this, MainActivity.WRITE_EXTERNAL_STORAGE);

            return permissionCheckBluetooth == PERMISSION_GRANTED
                    & permissionCheckBluetoothAdmin == PERMISSION_GRANTED
                    & permissionCheckAccessCoarse == PERMISSION_GRANTED
                    & permissionCheckWriteStorage == PERMISSION_GRANTED
                    & permissionCheckAccessFine == PERMISSION_GRANTED;
        } else {
            return true;
        }

    }

    private boolean needRequestRuntimePermissions() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private void showPermissionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String title = getResources().getString(R.string.app_name);
        builder.setTitle(title);
        builder.setMessage(title + " требует разрешение на включение Bluetooth");

        String positiveText = "Настройки";
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openAppSettings();
            }
        });

        String negativeText = "Выход";
        builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();

        ////// display dialog //////////
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_MULTIPLE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) { //& grantResults[1] == PackageManager.PERMISSION_GRANTED & grantResults[2] == PackageManager.PERMISSION_GRANTED ){
                //customToast("Разрешения получены");
                Log.i("Main", "Разрешения получены");
            } else {
                customToast("Разрешения не получены");
                showPermissionDialog(MainActivity.this);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void requestPermission() {
        // запрашиваем разрешение
        //ActivityCompat.requestPermissions(this, new String[]{MainActivity.SMS_SEND_STATE_PERMISSION}, MainActivity.REQUEST_SMS_SEND_STATE);
        ActivityCompat.requestPermissions(this, new String[]{
                MainActivity.BLUETOOTH,
                MainActivity.BLUETOOTH_ADMIN,
                MainActivity.ACCESS_COARSE_LOCATION,
                MainActivity.ACCESS_FINE_LOCATION,
                MainActivity.WRITE_EXTERNAL_STORAGE}, MainActivity.REQUEST_MULTIPLE_PERMISSION);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
/*
    private void requestApplicationConfig() {
        if (isPermissionGranted()) {
            customToast("Разрешения получены");
        } else {
            customToast("Пользователь снова не дал разрешение");
            requestPermission();
        }
    }*/

    void customToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    void saveState(Boolean bParam) {
        String CONNECTION = "MedSimTech_bl_connection";
        SharedPreferences sharedPreferences = getSharedPreferences(CONNECTION, MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putBoolean("con_state", bParam);
        ed.apply();
    }

    void formatDate() {
        String valid_until = "01-04-2021";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Date date2 = null;
        try {
            date2 = sdf.parse(valid_until);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (new Date().after(date2)) {
            this.finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mBluetoothAdapter == null) {
            customToast("main: code: 0");
            //return;
        }
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        //!!!! it change 23/12/2020!!!
        /*if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            Log.e("onStart", "if");
            customToast("main: code: 1");
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            Log.e("onStart", "else");
            customToast("main: code: 2");
        }*/

    }

    @Override
    protected void onStop() {
        super.onStop();
        this.getSharedPreferences("KrootTime", MODE_PRIVATE).edit().clear().apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }

        this.getSharedPreferences("KrootTime", MODE_PRIVATE).edit().clear().apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.

        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
                Log.e("onResume", "if");
            }
        }

        //customToast(String.valueOf(getMode));

    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            customToast("Нажмите «Назад» еще раз, чтобы выйти");
            back_pressed = System.currentTimeMillis();
        }

    }

}