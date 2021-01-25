package itmo.bluetoothChecker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    TextView time1;
    TextView time2;
    TextView time3;
    CheckBox cbMode;
    Button bSave;

    SharedPreferences spTime1;
    SharedPreferences spTime2;
    SharedPreferences spTime3;


    private final String LIMIT_TIME_1 = "limit_1";
    private final String LIMIT_TIME_2 = "limit_2";
    private final String LIMIT_TIME_3 = "limit_3";
    private final String MANUAL_MODE = "manual_mode";
    private final String GRADE_SYSTEM = "grade_system";

    private final String SETTINGS_MODE = "MedSimTech_send_mode";
    private final String SYS_GRADE = "MedSimTech_sys_grade";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        time1 = findViewById(R.id.val_time1);
        time2 = findViewById(R.id.val_time2);
        time3 = findViewById(R.id.val_time3);
        cbMode = findViewById(R.id.s_mode_check);
        bSave = findViewById(R.id.buttonSave);


        time1.setText(loadTime(spTime1, LIMIT_TIME_1, "time1"));
        time2.setText(loadTime(spTime2, LIMIT_TIME_2, "time2"));
        time3.setText(loadTime(spTime3, LIMIT_TIME_3, "time3"));
        cbMode.setChecked(loadMode(MANUAL_MODE, "mode"));


        //cbMode.setChecked(loadBool(spMode, MANUAL_MODE));


        cbMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    saveAll(MANUAL_MODE, "mode", "bool", time1.getText().toString(), true);
                    sendMode();
                }
                else
                {
                    saveAll(MANUAL_MODE, "mode", "bool", time1.getText().toString(), false);
                    sendMode();
                }
            }
        });

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAll(LIMIT_TIME_1, "time1", "string", time1.getText().toString(), false);
                saveAll(LIMIT_TIME_2, "time2", "string", time2.getText().toString(), false);
                saveAll(LIMIT_TIME_3, "time3", "string", time3.getText().toString(), false);
                Toast.makeText(SettingsActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();
            }
        });

        customTimePickerDialog(time1);
        customTimePickerDialog(time2);
        customTimePickerDialog(time3);

    }


    String loadTime(SharedPreferences sp, String name, String key) {
        sp = getSharedPreferences(name, MODE_PRIVATE);
        return sp.getString(key, "00:10");
    }

    Boolean loadMode(String name, String key) {
        SharedPreferences sp = getSharedPreferences(name, MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    void saveAll(String name, String key, String type, String sParam, Boolean bParam) {
        SharedPreferences sharedPreferences = getSharedPreferences(name, MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        if (type.equals("bool")){
            ed.putBoolean(key, bParam);
        }else if (type.equals("string")){
            ed.putString(key, sParam);
        }else{
            return;
        }
        ed.apply();
    }



    private void customTimePickerDialog(@NonNull final TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final AlertDialog.Builder timePickerBuilder = new AlertDialog.Builder(SettingsActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                LayoutInflater layoutInflater = Objects.requireNonNull(SettingsActivity.this).getLayoutInflater();
                view = layoutInflater.inflate(R.layout.custom_timepicker, null);
                final NumberPicker minute = view.findViewById(R.id.MinutePicker);
                final NumberPicker second = view.findViewById(R.id.SecondPicker);

                minute.setMinValue(0);
                minute.setMaxValue(59);
                second.setMinValue(0);
                second.setMaxValue(59);

                minute.setWrapSelectorWheel(true);
                second.setWrapSelectorWheel(true);

                minute.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        return String.format(Locale.getDefault(),"%02d", value);
                    }
                });

                second.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        return String.format(Locale.getDefault(),"%02d", value);
                    }
                });



                timePickerBuilder.setTitle("Введите время")
                        .setView(view)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(minute.getValue() == 0 && second.getValue() == 0){
                                    Toast.makeText(SettingsActivity.this, "Ошибка. Задано нулевое время.", Toast.LENGTH_SHORT).show();
                                }else {
                                    textView.setText(String.format(Locale.getDefault(), "%02d", minute.getValue())
                                            + ":" + String.format(Locale.getDefault(), "%02d", second.getValue()));
                                    dialog.dismiss();
                                }
                            }

                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();
            }

        });

    }


    private void sendMode() {
        Intent intentMode = new Intent();
        intentMode.setAction(SETTINGS_MODE);
        intentMode.putExtra("mode_state", cbMode.isChecked());
        sendBroadcast(intentMode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int REQUEST_SETTINGS = 301;
        switch (item.getItemId()) {
            case android.R.id.home:
               // Intent setIntent = new Intent(this, MainActivity.class);
               // startActivityForResult(setIntent, REQUEST_SETTINGS);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void composeEmail(String[] addresses, String subject, Uri attachment) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, attachment);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
