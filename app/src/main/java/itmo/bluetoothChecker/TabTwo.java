package itmo.bluetoothChecker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import itmo.bluetoothChecker.CircularSeekBar.OnCircularSeekBarChangeListener;

import static android.content.Context.MODE_PRIVATE;


public class TabTwo extends Fragment {

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 0;
    private static final String TAG = "Fragment 2 send";
    private static final String TAG1 = "Button Start";
    public static boolean flagComplete = true;
    private boolean flagStop = false;
    public static boolean flagPause = false;

    private static TextView answer;

    private final String SEND = "MedSimTech_send_message";
    private final String RECEIVE = "MedSimTech_receive_message";
    private final String SETTINGS_MODE = "MedSimTech_send_mode";
    private final String CONNECTION = "MedSimTech_bl_connection";

    SharedPreferences seekPref;

    ColorStateList colorState;

    private Button buttonStart;
    private Button buttonSend;
    private Button buttonStop;
    private Button buttonPause;
    private Button buttonMute;
    private Button button_1to2;
    private Button button_2to3;
    private Button button_3to0;

    private CircularSeekBar bpmCirSeek;
    private CircularSeekBar pressCirSeek;
    private CircularSeekBar respCirSeek;
    private CircularSeekBar satCirSeek;

    private Chronometer time_1;
    private Chronometer time_2;
    private Chronometer time_3;

    private TextView result_1;
    private TextView result_2;
    private TextView result_3;

    private boolean runningChrono_1 = false;
    private boolean runningChrono_2 = false;
    private boolean runningChrono_3 = false;
    private boolean pauseChrono_1 = false;
    private boolean pauseChrono_2 = false;
    private boolean pauseChrono_3 = false;
    private long pauseOffset = 0;


    private final BroadcastReceiver tabReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (RECEIVE.equals(action)) {
                String receiveMessage = intent.getStringExtra("receive_message_text");
                JSONObject jsonObj = null;
                try {
                    assert receiveMessage != null;
                    jsonObj = new JSONObject(receiveMessage);
                    Log.e("My App", jsonObj.toString());


                    if (jsonObj.getString("device").equals("pump")) {
                        Log.e(TAG, "device pump");
                        Log.e(TAG, "manual mode " + getMode());

                        if(jsonObj.has("started") && jsonObj.getString("started").equals("true")){
                            Log.e(TAG, "started true");
                            customToast("started true");
                            String ad = (jsonObj.getString("ad").substring(1, jsonObj.getString("ad").length()-1)).replace(",","/");
                            int pos = -1;
                            for(int i = 0; i < getResources().getStringArray(R.array.pressure).length; i++){
                                if(getResources().getStringArray(R.array.pressure)[i].equals(ad)){
                                    pos = i/2;
                                    break;
                                }
                            }

                            try{
                                bpmCirSeek.setProgress(jsonObj.getInt("hr")-70);
                            }catch (Exception e){
                                Log.e(TAG, "Receive wrong value of bpm: " + jsonObj.getInt("hr"));
                            }

                            try{
                                pressCirSeek.setProgress(pos);
                            }catch (Exception e){
                                Log.e(TAG, "Receive wrong value of pressure: " + jsonObj.getInt("ad"));
                            }

                            try{
                                respCirSeek.setProgress(jsonObj.getInt("rr")-15);
                            }catch (Exception e){
                                Log.e(TAG, "Receive wrong value of respiration rate: " + jsonObj.getInt("rr"));
                            }

                            try{
                                satCirSeek.setProgress(jsonObj.getInt("sp"));
                            }catch (Exception e){
                                Log.e(TAG, "Receive wrong value of saturation: " + jsonObj.getInt("sp"));
                            }

                        }else{
                            Log.e(TAG, "started false");
                            customToast("started false");
                        }

                        ///// Запуск таймеров от датчика давления только в автоматическом режиме ////
                        if (!getMode() && !flagComplete) {
                            Log.e(TAG, "manual mode" + getMode());
                            if (jsonObj.has("timer") && jsonObj.getInt("timer") < 4) {
                                Log.e(TAG, "timer: " + jsonObj.getInt("timer"));
                                if (jsonObj.getInt("timer") == 1
                                        && !flagComplete
                                        && result_1.getVisibility() == View.INVISIBLE) {

                                    if (!runningChrono_1 && !runningChrono_2 && !runningChrono_3) {
                                        startChrono(time_1);
                                        resetChrono(time_2);
                                        time_2.stop();
                                        resetChrono(time_3);
                                        time_3.stop();
                                        sendMessage("OK");
                                    } else {
                                        customToast("Wrong -> timer: 1 , mode: auto");
                                    }
                                } else if (jsonObj.getInt("timer") == 2
                                        && !flagComplete
                                        && result_2.getVisibility() == View.INVISIBLE) {

                                    if (!runningChrono_2 && runningChrono_1) {

                                        if (!time_1.getText().equals("00:00")) {
                                            result_1.setText(time_1.getText());
                                            result_1.setVisibility(View.VISIBLE);
                                            time_1.stop();
                                            /////// reset timer ////////
                                            time_1.setBase(SystemClock.elapsedRealtime());
                                            pauseOffset = 0;
                                            ///////////////////////////
                                            startChrono(time_2);
                                            customToast("Переход ко 2 этапу");
                                            sendMessage("OK");
                                        } else {
                                            customToast("Wrong -> timer: 1 to 2 , mode: auto");
                                        }

                                    }
                                } else if (jsonObj.getInt("timer") == 3
                                        && !flagComplete
                                        && result_3.getVisibility() == View.INVISIBLE) {

                                    if (!runningChrono_3 && runningChrono_2) {

                                        if (!time_2.getText().equals("00:00")) {
                                            result_2.setText(time_2.getText());
                                            result_2.setVisibility(View.VISIBLE);
                                            time_2.stop();
                                            /////// reset timer ////////
                                            time_2.setBase(SystemClock.elapsedRealtime());
                                            pauseOffset = 0;
                                            ///////////////////////////
                                            startChrono(time_3);
                                            customToast("Переход к 3 этапу");
                                            sendMessage("OK");
                                        } else {
                                            customToast("Wrong -> timer: 2 to 3, mode: auto");
                                        }

                                    }
                                } else if (jsonObj.getInt("timer") == 0
                                        && result_1.getVisibility() == View.VISIBLE
                                        && result_2.getVisibility() == View.VISIBLE
                                        && result_3.getVisibility() == View.INVISIBLE) {
                                    result_3.setText(time_3.getText());
                                    result_3.setVisibility(View.VISIBLE);
                                    resetChrono(time_3);
                                    time_3.stop();
                                    /////// reset timer ////////
                                    time_3.setBase(SystemClock.elapsedRealtime());
                                    pauseOffset = 0;
                                    ///////////////////////////
                                    flagComplete = true;
                                    customToast("Операция окончена!");
                                    sendMessage("OK");
                                } else {
                                    sendMessage("timer" + jsonObj.getString("timer") + ": " + "out of conditions");
                                }
                            } else if (jsonObj.has("status")) {
                                customToast("status");
                            } else if (jsonObj.has("hr")
                                    && jsonObj.has("ap")
                                    && jsonObj.has("rr")
                                    && jsonObj.has("sp")) {

                                customToast("ЧСС = " + jsonObj.getString("hr") + "; " +
                                        "АД = " + jsonObj.getString("ap") + "; " +
                                        "ЧДД = " + jsonObj.getString("rr") + "; " +
                                        "SPO2 = " + jsonObj.getString("sp"));
                            } else {
                                customToast("code: 9");
                            }
                        } else {
                            //// Во всех режимах прием подтверждения доставки команды ////
                            if (jsonObj.has("ansOK") && jsonObj.getString("ansOK").equals("OK") && !flagComplete) {
                                customToast("Сообщение доставлено - OK");
                            }
                        }
                    }
                }catch (JSONException e) {
                    try {
                        assert jsonObj != null;
                        if(jsonObj.has("device")){

                            if(!jsonObj.getString("device").equals("pump")){
                                Log.e(TAG,"not device pump");
                                customToast("Принято сообщение со стороннего устройства");
                            }else{
                                if(!receiveMessage.substring(0,1).equals("{")
                                        && !receiveMessage.substring(receiveMessage.length() - 1).equals("}")){
                                    Log.e(TAG,"wrong data");
                                    customToast("Приняты поврежденные данные: " + receiveMessage);
                                }
                            }
                        }else{
                            Log.e(TAG,"Принято сообщение с неизвестного устройство");
                            customToast("Принято сообщение с неизвестного устройство");
                        }

                    } catch (JSONException ex) {
                        Log.e(TAG,"catch error");
                        ex.printStackTrace();
                    }
                    answer.setText(receiveMessage);
                    e.printStackTrace();
                }
            }

            if (SETTINGS_MODE.equals(action)) {
                //boolean receiveMode = intent.getBooleanExtra("mode_state", false);
                if (intent.getBooleanExtra("mode_state", false)) {
                    customToast("brod state: " + intent.getBooleanExtra("mode_state", false));
                    answer.setVisibility(View.VISIBLE);
                } else {
                    customToast("brod state: " + intent.getBooleanExtra("mode_state", false));
                    answer.setVisibility(View.INVISIBLE);
                }
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState2) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.tab_pump, container, false);
        FragmentActivity activity = getActivity();

        String limit_1 = "10:00";
        String limit_2 = "15:00";
        String limit_3 = "10:00";

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Objects.requireNonNull(activity).registerReceiver(tabReceiver, new IntentFilter(RECEIVE));
        Objects.requireNonNull(activity).registerReceiver(tabReceiver, new IntentFilter(SETTINGS_MODE));

        requireActivity().getSharedPreferences("limit", MODE_PRIVATE).edit().putString("time1", limit_1).apply();
        requireActivity().getSharedPreferences("limit", MODE_PRIVATE).edit().putString("time2", limit_2).apply();
        requireActivity().getSharedPreferences("limit", MODE_PRIVATE).edit().putString("time3", limit_3).apply();

        // If the adapter is null, then Bluetooth is not supported

        /*if (mBluetoothAdapter == null) {
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }*/


        if (!Objects.requireNonNull(mBluetoothAdapter).isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }


        TabLayout tabs = requireActivity().findViewById(R.id.tabs);
        final ViewPager viewPager = requireActivity().findViewById(R.id.view_pager);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());//setting current selected item over viewpager
                switch (tab.getPosition()) {
                    case 0:
                        Log.e("TAG", "TAB1");
                        break;
                    case 1:
                        //bondList();
                        Log.e("TAG", "TAB2");
                        break;
                    case 2:
                        Log.e("TAG", "TAB3");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        answer = view.findViewById(R.id.tvAnswer);

        time_1 = view.findViewById(R.id.chronometer1);
        time_2 = view.findViewById(R.id.chronometer2);
        time_3 = view.findViewById(R.id.chronometer3);

        result_1 = view.findViewById(R.id.tvResult_1);
        result_2 = view.findViewById(R.id.tvResult_2);
        result_3 = view.findViewById(R.id.tvResult_3);

        colorState = time_1.getTextColors();

        bpmCirSeek = view.findViewById(R.id.circularSeekBarBpm);
        bpmCirSeek.setOnSeekBarChangeListener(new OnCircularSeekBarChangeListener() {
            int val = 15;

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                val = progress;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });


        pressCirSeek = view.findViewById(R.id.circularSeekBarPress);
        pressCirSeek.setOnSeekBarChangeListener(new OnCircularSeekBarChangeListener() {
            int val = 5;

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                val = progress;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });


        respCirSeek = view.findViewById(R.id.circularSeekBarResp);
        respCirSeek.setOnSeekBarChangeListener(new OnCircularSeekBarChangeListener() {
            int val = 10;

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                val = progress;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });


        satCirSeek = view.findViewById(R.id.circularSeekBarSat);
        satCirSeek.setOnSeekBarChangeListener(new OnCircularSeekBarChangeListener() {
            int val = 17;

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                val = progress;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }

        });

        buttonStart = view.findViewById(R.id.buttonStart);
        buttonSend = view.findViewById(R.id.buttonSend);
        buttonPause = view.findViewById(R.id.buttonPause);
        buttonMute = view.findViewById(R.id.buttonMute);
        buttonStop = view.findViewById(R.id.buttonStop);

        button_1to2 = view.findViewById(R.id.bt_chrono1_next);
        button_2to3 = view.findViewById(R.id.bt_chrono2_next);
        button_3to0 = view.findViewById(R.id.bt_chrono3_next);

        showNextButton();

        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (loadState()) {

                    try {
                        JSONObject send = JSONSend(
                                bpmCirSeek.getProgress() + bpmCirSeek.getMin(),
                                String.valueOf(getResources().getStringArray(R.array.pressure)[(pressCirSeek.getProgress() * 2) + 1]),
                                respCirSeek.getProgress() + respCirSeek.getMin(),
                                satCirSeek.getProgress() + satCirSeek.getMin());
                        sendMessage(send.toString());
                        answer.setText(send.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    buttonProtect(buttonSend);
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                } else {
                    customToast("Ваше устройство не подключено к тренажеру");
                }
            }
        });


        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadState()) {
                    Log.e(TAG1, "STATE: " + loadState());
                    if (!getMode()) {
                        if (flagComplete) {
                            flagComplete = false;
                            flagStop = false;
                            sendStatus("start");
                        } else {
                            if (pauseChrono_1) {
                                startChrono(time_1);
                                sendStatus("start");
                                customToast("Операция возобновлена");
                            } else if (pauseChrono_2) {
                                startChrono(time_2);
                                sendStatus("start");
                                customToast("Операция возобновлена");
                            } else if (pauseChrono_3) {
                                startChrono(time_3);
                                sendStatus("start");
                                customToast("Операция возобновлена");
                            } else {
                                customToast("Действие не требуется");
                            }
                        }

                    } else {
                        if (flagComplete) {
                            flagComplete = false;
                            flagStop = false;
                            result_1.setVisibility(View.INVISIBLE);
                            result_2.setVisibility(View.INVISIBLE);
                            result_3.setVisibility(View.INVISIBLE);
                            result_1.setTextColor(colorState);
                            result_2.setTextColor(colorState);
                            result_3.setTextColor(colorState);
                            resetChrono(time_1);
                            resetChrono(time_2);
                            resetChrono(time_3);
                            startChrono(time_1);
                            customToast("Операция начата");
                            Log.e(TAG1, "FLAG_COMPLETE: true  -> " + loadState());
                        } else {
                            if (runningChrono_1 && pauseChrono_1) {
                                startChrono(time_1);
                                sendStatus("start");
                                customToast("Операция возобновлена");
                            } else if (runningChrono_2 && pauseChrono_2) {
                                startChrono(time_2);
                                sendStatus("start");
                                customToast("Операция возобновлена");
                            } else if (runningChrono_3 && pauseChrono_3) {
                                startChrono(time_3);
                                sendStatus("start");
                                customToast("Операция возобновлена");
                            } else {
                                customToast("Идет операция");
                            }

                            Log.e(TAG1, "FLAG_COMPLETE: false  -> " + loadState());
                        }
                    }

                } else {
                    Log.e(TAG1, "State (else): " + loadState());
                    Log.e(TAG1, "flagComplete (else): " + flagComplete);
                    customToast("Ваше устройство не подключено к тренажеру");
                }
                buttonProtect(buttonStart);
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }
        });

        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadState()) {
                    Log.i(TAG, "State" + loadState());

                    if (!flagComplete) {
                        if (runningChrono_1 && !pauseChrono_1) {
                            pauseChrono(time_1);
                            flagPause = true;
                            sendStatus("pause");
                            customToast("Операция приостановлена");
                        } else if (runningChrono_2 && !pauseChrono_2) {
                            pauseChrono(time_2);
                            flagPause = true;
                            sendStatus("pause");
                            customToast("Операция приостановлена");
                        } else if (runningChrono_3 && !pauseChrono_3) {
                            pauseChrono(time_3);
                            flagPause = true;
                            sendStatus("pause");
                            customToast("Операция приостановлена");
                        } else if (pauseChrono_1 || pauseChrono_2 || pauseChrono_3) {
                            customToast("Действие не требуется");
                        } else {
                            customToast("code: 11");
                        }
                    } else {
                        customToast("Операция не начата");
                    }
                } else {
                    Log.e(TAG, "State" + loadState());
                    Log.e(TAG, "flagComplete: " + flagComplete);
                    customToast("Ваше устройство не подключено к тренажеру");
                }
                buttonProtect(buttonPause);
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }
        });

        buttonMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadState()) {
                    Log.i(TAG, "State" + loadState());
                    JSONObject mesMute = new JSONObject();
                    try {
                        mesMute.put("device", "android");
                        mesMute.put("mute", true);

                        sendMessage(mesMute.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    customToast("Звук отключен");
                } else {
                    Log.e(TAG, "State" + loadState());
                    Log.e(TAG, "flagComplete: " + flagComplete);
                    customToast("Ваше устройство не подключено к тренажеру");
                }
                buttonProtect(buttonMute);
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadState()) {
                    Log.i(TAG, "State" + loadState());
                    if (flagComplete) {
                        result_1.setVisibility(View.INVISIBLE);
                        result_2.setVisibility(View.INVISIBLE);
                        result_3.setVisibility(View.INVISIBLE);
                        result_1.setTextColor(colorState);
                        result_2.setTextColor(colorState);
                        result_3.setTextColor(colorState);
                        resetChrono(time_1);
                        resetChrono(time_2);
                        resetChrono(time_3);
                        flagStop = false;
                        customToast("Сброс таймеров");
                    } else {
                        if (!flagStop) {
                            if (runningChrono_1) {
                                    time_1.stop();
                                    result_1.setText(R.string.def_val_settings_time);
                                    result_2.setText(R.string.def_val_settings_time);
                                    result_3.setText(R.string.def_val_settings_time);
                                    result_1.setVisibility(View.VISIBLE);
                                    result_2.setVisibility(View.VISIBLE);
                                    result_3.setVisibility(View.VISIBLE);
                            } else if (runningChrono_2) {
                                    time_2.stop();
                                    result_2.setText(R.string.def_val_settings_time);
                                    result_3.setText(R.string.def_val_settings_time);
                                    result_2.setVisibility(View.VISIBLE);
                                    result_3.setVisibility(View.VISIBLE);
                            } else if (runningChrono_3) {
                                    time_3.stop();
                                    result_3.setText(R.string.def_val_settings_time);
                                    result_3.setVisibility(View.VISIBLE);
                            }
                            sendStatus("stop");

                            if(pauseChrono_1){
                                pauseChrono_1 = false;
                            }else if(pauseChrono_2){
                                pauseChrono_2 = false;
                            }else if(pauseChrono_3){
                                pauseChrono_3 = false;
                            }

                            flagComplete = true;
                            flagStop = true;
                            customToast("Операция остановлена");
                        } else {
                            customToast("Действие не требуется");
                        }
                    }
                } else {
                    Log.e(TAG, "State" + loadState());
                    Log.e(TAG, "flagComplete: " + flagComplete);
                    customToast("Ваше устройство не подключено к тренажеру");
                }
                buttonProtect(buttonStop);
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }
        });


        button_1to2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (runningChrono_1 && getMode() && !time_1.getText().equals("00:00") && !flagComplete) {
                    result_1.setText(time_1.getText());
                    result_1.setVisibility(View.VISIBLE);
                    time_1.stop();
                    /////// reset timer ////////
                    time_1.setBase(SystemClock.elapsedRealtime());
                    pauseOffset = 0;
                    ///////////////////////////
                    startChrono(time_2);
                    customToast("Переход ко 2 этапу");
                } else if (!getMode()) {
                    customToast("Не доступно в автоматическом режиме");
                } else {
                    if (flagComplete) {
                        customToast("Операция не начата");
                    } else {
                        customToast("Нарушена очередность этапов");
                    }
                }
                buttonProtect(buttonSend);
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }
        });

        button_2to3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (runningChrono_2 && getMode() && !time_2.getText().equals("00:00") && !flagComplete) {
                    result_2.setText(time_2.getText());
                    result_2.setVisibility(View.VISIBLE);
                    time_2.stop();
                    /////// reset timer ////////
                    time_2.setBase(SystemClock.elapsedRealtime());
                    pauseOffset = 0;
                    ///////////////////////////
                    startChrono(time_3);
                    customToast("Переход к 3 этапу");
                } else if (!getMode()) {
                    customToast("Не доступно в автоматическом режиме");
                } else {
                    customToast("Нарушена очередность этапов");
                }
                buttonProtect(buttonSend);
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }
        });

        button_3to0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (runningChrono_3 && getMode() && !time_3.getText().equals("00:00") && !flagComplete) {
                    result_3.setText(time_3.getText());
                    result_3.setVisibility(View.VISIBLE);
                    resetChrono(time_3);
                    time_3.stop();
                    customToast("Окончание операции");
                    flagComplete = true;
                } else if (!getMode()) {
                    customToast("Не доступно в автоматическом режиме");
                } else {
                    customToast("Нарушена очередность этапов");
                }
                buttonProtect(buttonSend);
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }
        });


        time_1.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime() - time_1.getBase();
                if (elapsedMillis > getLimit("time1")) {
                    result_1.setText(R.string.def_val_settings_time);
                    result_1.setVisibility(View.VISIBLE);
                    colorState = result_1.getTextColors();
                    result_1.setTextColor(ContextCompat.getColor(requireContext(), R.color.heart_color));
                    //resetChrono(time_1);
                    time_1.stop();
                    customToast("Время на завршение этапа истекло");
                    flagComplete = true;
                }
            }
        });

        time_2.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime() - time_2.getBase();
                if (elapsedMillis > getLimit("time2")) {
                    result_2.setText(R.string.def_val_settings_time);
                    result_2.setVisibility(View.VISIBLE);
                    colorState = result_2.getTextColors();
                    result_2.setTextColor(ContextCompat.getColor(requireContext(), R.color.heart_color));
                    //resetChrono(time_2);
                    time_2.stop();
                    customToast("Время на завршение этапа истекло");
                    flagComplete = true;
                }
            }
        });

        time_3.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime() - time_3.getBase();
                if (elapsedMillis > getLimit("time3")) {
                    result_3.setText(R.string.def_val_settings_time);
                    result_3.setVisibility(View.VISIBLE);
                    colorState = result_3.getTextColors();
                    result_3.setTextColor(ContextCompat.getColor(requireContext(), R.color.heart_color));
                    //resetChrono(time_3);
                    time_3.stop();
                    customToast("Время на завршение этапа истекло");
                    flagComplete = true;
                }
            }
        });

        clickChono(time_1);
        clickChono(time_2);
        clickChono(time_3);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        seekPref.edit().clear().apply();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveSeekState();
        Log.e(TAG, "PAUSE " + flagComplete);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "START " + flagComplete);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "RESUME " + flagComplete);
        loadSeekState();
        showNextButton();
    }

    private void sendMessage(String message) {
        Intent intentSM = new Intent();
        intentSM.setAction(SEND);
        intentSM.putExtra("send_message_text", message);
        requireActivity().sendBroadcast(intentSM);
    }

    private void sendSPS(String param, Boolean cond) {
        try {
            JSONObject mes = JSONMode(param, cond);
            sendMessage(mes.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendStatus(String param) {
        try {
            JSONObject mes = JSONStatus(param);
            sendMessage(mes.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private Boolean getMode() {
        SharedPreferences s = requireActivity().getSharedPreferences("manual_mode", MODE_PRIVATE);
        return s.getBoolean("mode", false);
    }

    private long getLimit(String key) {
        long seconds;
        long minutes;
        Date time = null;
        //StringBuffer strBuffer = new StringBuffer();
        SharedPreferences s = requireActivity().getSharedPreferences("limit", MODE_PRIVATE);
        String str = s.getString(key, "10:00");
        SimpleDateFormat format = new SimpleDateFormat("mm:ss", new Locale("en"));
        format.setTimeZone(TimeZone.getTimeZone("UTC")); //// !!!важно для отображения getTime в положительном формате
        try {
            time = format.parse(str);
            minutes = (Objects.requireNonNull(time).getTime() / (60 * 1000) % 60);
            seconds = (Objects.requireNonNull(time).getTime() / 1000 % 60);
            //minutes = TimeUnit.MILLISECONDS.toMinutes(Objects.requireNonNull(time).getTime()) / 60;
            //seconds = TimeUnit.MILLISECONDS.toSeconds(time.getTime()) % 60;
            Log.i(TAG,minutes + " minutes and " + seconds + " seconds and " + Objects.requireNonNull(time).getTime() + " get time");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /////  Возвращает String в формате мм:сс   /////
        //return  String.format(new Locale("ru"),"%02d:%02d", minutes, seconds);
        /////  Возвращает long в милисекундах   /////
        return Objects.requireNonNull(time).getTime();
    }

    private void customToast(String string) {
       Toast.makeText(getContext(), string, Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {// When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(requireActivity(), "Bluetooth включен", Toast.LENGTH_SHORT).show();
                //customToast("Bluetooth is available.");
                //bondList();
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    customToast("Bluetooth выключен");
                    activity.finish();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startChrono(Chronometer chronometer) {
        if (time_1.equals(chronometer)) {
            if (!runningChrono_1 && !runningChrono_2 && !runningChrono_3) {
                if (flagComplete) {
                    flagComplete = false;
                    result_1.setVisibility(View.INVISIBLE);
                    result_2.setVisibility(View.INVISIBLE);
                    result_3.setVisibility(View.INVISIBLE);
                    resetChrono(time_1);
                    resetChrono(time_2);
                    resetChrono(time_3);
                }
                chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                chronometer.start();
                runningChrono_1 = true;
                runningChrono_2 = false;
                runningChrono_3 = false;
            } else if (runningChrono_1 && pauseChrono_1) {
                chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                chronometer.start();
                pauseChrono_1 = false;
            } else {
                customToast("startChrono1 exception");
            }
        } else if (time_2.equals(chronometer)) {
            if (!runningChrono_2 && runningChrono_1 && !runningChrono_3) {
                chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                chronometer.start();
                runningChrono_1 = false;
                runningChrono_2 = true;
                runningChrono_3 = false;
            } else if (runningChrono_2 && pauseChrono_2) {
                chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                chronometer.start();
                pauseChrono_2 = false;
            } else {
                customToast("startChrono2 exception");
            }
        } else if (time_3.equals(chronometer)) {
            if (!runningChrono_3 && !runningChrono_1 && runningChrono_2) {
                chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                chronometer.start();
                runningChrono_1 = false;
                runningChrono_2 = false;
                runningChrono_3 = true;
            } else if (runningChrono_3 && pauseChrono_3) {
                chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                chronometer.start();
                pauseChrono_3 = false;
            } else {
                customToast("startChrono3 exception");
            }
        } else {
            customToast("Соблюдайте очередность этапов!");
        }
    }

    private void pauseChrono(Chronometer chronometer) {
        if (chronometer == time_1 & time_1.isEnabled()) {
            time_1.stop();
            pauseOffset = SystemClock.elapsedRealtime() - time_1.getBase();
            pauseChrono_1 = true;
        } else if (chronometer == time_2 & time_2.isEnabled()) {
            time_2.stop();
            pauseOffset = SystemClock.elapsedRealtime() - time_2.getBase();
            pauseChrono_2 = true;
        } else if (chronometer == time_3 & time_3.isEnabled()) {
            time_3.stop();
            pauseOffset = SystemClock.elapsedRealtime() - time_3.getBase();
            pauseChrono_3 = true;
        }
    }

    private void resetChrono(Chronometer chronometer) {
        if (time_1.equals(chronometer)) {
            if (runningChrono_1) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                pauseOffset = 0;
                runningChrono_1 = false;
            }
        } else if (time_2.equals(chronometer)) {
            if (runningChrono_2) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                pauseOffset = 0;
                runningChrono_2 = false;
            }

        } else if (time_3.equals(chronometer)) {
            if (runningChrono_3) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                pauseOffset = 0;
                runningChrono_3 = false;
            }
        }
    }

    private void clickChono(final Chronometer chronometer) {
        chronometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time_1.equals(chronometer)) {
                    if (runningChrono_1) {
                        pauseChrono(chronometer);
                    } else {
                        startChrono(chronometer);
                    }

                } else if (time_2.equals(chronometer)) {
                    if (runningChrono_2) {
                        pauseChrono(chronometer);
                    } else {
                        startChrono(chronometer);
                    }

                } else if (time_3.equals(chronometer)) {
                    if (runningChrono_3) {
                        pauseChrono(chronometer);
                    } else {
                        startChrono(chronometer);
                    }
                }
            }
        });
    }

    private JSONObject JSONSend(Integer rate, String pressure, Integer resp, Integer spo2) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("device", "android");
        message.put("hr", rate);
        message.put("ap", pressure);
        message.put("rr", resp);
        message.put("sp", spo2);
        return message;
    }

    private JSONObject JSONAnswer(String answer) throws JSONException {
        JSONObject mesAns = new JSONObject();
        mesAns.put("device", "android");
        mesAns.put("answer", answer);
        return mesAns;
    }

    private JSONObject JSONMode(String param, Boolean cond) throws JSONException {
        JSONObject mesMode = new JSONObject();
        mesMode.put("device", "android");
        mesMode.put("mode", param);
        mesMode.put("cond", cond);
        return mesMode;
    }

    private JSONObject JSONStatus(String param) throws JSONException {
        JSONObject mesStat = new JSONObject();
        mesStat.put("device", "android");
        mesStat.put("status", param);
        return mesStat;
    }

    Boolean loadState() {
        SharedPreferences sp = requireActivity().getSharedPreferences(CONNECTION, MODE_PRIVATE);
        return sp.getBoolean("con_state", false);
    }

    private void showNextButton(){
        if(getMode()){
            button_1to2.setEnabled(true);
            button_2to3.setEnabled(true);
            button_3to0.setEnabled(true);
            button_1to2.setVisibility(View.VISIBLE);
            button_2to3.setVisibility(View.VISIBLE);
            button_3to0.setVisibility(View.VISIBLE);
        }else{
            button_1to2.setEnabled(false);
            button_2to3.setEnabled(false);
            button_3to0.setEnabled(false);
            button_1to2.setVisibility(View.INVISIBLE);
            button_2to3.setVisibility(View.INVISIBLE);
            button_3to0.setVisibility(View.INVISIBLE);
        }
    }

    void saveSeekState() {
        SharedPreferences.Editor ed = requireActivity().getPreferences(MODE_PRIVATE).edit();
        ed.putInt("bpm", bpmCirSeek.getProgress());
        ed.putInt("ap", pressCirSeek.getProgress());
        ed.putInt("rr", respCirSeek.getProgress());
        ed.putInt("spo2", satCirSeek.getProgress());
        ed.apply();
    }

    void loadSeekState() {
        seekPref = requireActivity().getPreferences(MODE_PRIVATE);
        int savedStateBpm = seekPref.getInt("bpm", 15);
        bpmCirSeek.setProgress(savedStateBpm);
        int savedStateAP = seekPref.getInt("ap", 5);
        pressCirSeek.setProgress(savedStateAP);
        int savedStateRR = seekPref.getInt("rr", 10);
        respCirSeek.setProgress(savedStateRR);
        int savedStateSPO2 = seekPref.getInt("spo2", 17);
        satCirSeek.setProgress(savedStateSPO2);
    }

    void buttonProtect(final Button button) {
        Thread buttonDelay = new Thread() {
            @Override
            public void run() {
                try {
                    button.setClickable(false);
                    Log.i("button", "deactivated");
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Log.i("button", "activated");
                    button.setClickable(true);
                }
            }
        };
        buttonDelay.start();

    }


    /*public static class ObservableString{
        private String str = "";
        private ChangeListener listener;

        public void setStr(String str) {
            this.str = str;
            if (listener != null) listener.onChange();
        }

        public ChangeListener getListener() {
            return listener;
        }

        public void setListener(ChangeListener listener) {
            this.listener = listener;
        }
    }

    public interface ChangeListener {
        void onChange();
    }*/

}
