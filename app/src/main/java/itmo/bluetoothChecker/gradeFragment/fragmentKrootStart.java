package itmo.bluetoothChecker.gradeFragment;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import itmo.bluetoothChecker.R;

import static android.content.Context.MODE_PRIVATE;


public class fragmentKrootStart extends Fragment {

    private final String TIME = "MedSimTech_send_time";


    EditText teacherName, studentName, teacherID, studentID;
    TextView prepStartTime_1, prepStartTime_2, prepStartTime_3,
            prepStopTime_1, prepStopTime_2, prepStopTime_3,
            prepAllTime_1, prepAllTime_2, prepAllTime_3,
            operStartTime_1, operStartTime_2, operStartTime_3,
            operStopTime_1, operStopTime_2, operStopTime_3,
            operAllTime_1, operAllTime_2, operAllTime_3,
            operStartTimeAll, operStopTimeAll, operAllTime;
    RadioGroup rgProgram, rgTest, rgKroot, rgOperation, rgDiff;
    RadioButton rbProgramType_1, rbProgramType_2,
            rbTest_1, rbTest_2, rbTest_3, rbTest_4, rbTest_5,
            rb_kroot_1, rb_kroot_2, rb_kroot_3, rb_kroot_4, rb_kroot_5,
            rb_diff_1, rb_diff_2, rb_diff_3, rb_diff_4, rb_diff_5;

    int mHour, mMinute;


    private final BroadcastReceiver checkReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String startTime, stopTime;

            if (TIME.equals(action)) {
                Log.e("Kroooooot", "broadcast");
                if(intent.hasExtra("send_message_time1Start")){
                    operStartTime_1.setText(intent.getStringExtra("send_message_time1Start"));
                }else if(intent.hasExtra("send_message_time1Stop")){
                    operStopTime_1.setText(intent.getStringExtra("send_message_time1Stop"));
                    operStartTime_2.setText(intent.getStringExtra("send_message_time1Stop"));

                    stopTime =  context.getSharedPreferences("KrootTime", MODE_PRIVATE).getString("time1Stop", "00:00");
                    startTime = context.getSharedPreferences("KrootTime", MODE_PRIVATE).getString("time1Start", "00:00");

                    Log.e("Kroooooot", "----> " + startTime + " ---> " + stopTime);

                    /*if(!operStartTime_1.getText().toString().equals("") && !operStopTime_1.getText().toString().equals("")) {
                        try {
                            operAllTime_1.setText(timeDif(operStartTime_1.getText().toString(), operStopTime_1.getText().toString()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }*/

                }else if(intent.hasExtra("send_message_time2Stop")){
                    operStopTime_2.setText(intent.getStringExtra("send_message_time2Stop"));
                    operStartTime_3.setText(intent.getStringExtra("send_message_time2Stop"));

                    if(!operStartTime_2.getText().toString().equals("") && !operStopTime_2.getText().toString().equals("")) {
                        try {
                            operAllTime_2.setText(timeDif(operStartTime_1.getText().toString(), operStopTime_2.getText().toString()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                }else if(intent.hasExtra("send_message_time3Stop")){
                    operStopTime_3.setText(intent.getStringExtra("send_message_time3Stop"));

                    if(!operStartTime_3.getText().toString().equals("") && !operStopTime_3.getText().toString().equals("")) {
                        try {
                            operAllTime_3.setText(timeDif(operStartTime_3.getText().toString(), operStopTime_3.getText().toString()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                }else{
                    Log.e("Kroooooot", "else");
                }

            }
        }
    };


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kroot_start, container, false);

        Objects.requireNonNull(requireActivity()).registerReceiver(checkReceiver, new IntentFilter(TIME));

        teacherName = view.findViewById(R.id.kroot_teacher_name);
        studentName = view.findViewById(R.id.kroot_student_name);
        teacherID = view.findViewById(R.id.kroot_teacher_id);
        studentID = view.findViewById(R.id.kroot_student_id);

        prepStartTime_1 = view.findViewById(R.id.kroot_prepare_1_start_time);
        prepStartTime_2 = view.findViewById(R.id.kroot_prepare_2_start_time);
        prepStartTime_3 = view.findViewById(R.id.kroot_prepare_3_start_time);
        prepStopTime_1 = view.findViewById(R.id.kroot_prepare_1_stop_time);
        prepStopTime_2 = view.findViewById(R.id.kroot_prepare_2_stop_time);
        prepStopTime_3 = view.findViewById(R.id.kroot_prepare_3_stop_time);
        prepAllTime_1 = view.findViewById(R.id.kroot_prepare_1_all_time);
        prepAllTime_2 = view.findViewById(R.id.kroot_prepare_2_all_time);
        prepAllTime_3 = view.findViewById(R.id.kroot_prepare_3_all_time);

        operStartTime_1 = view.findViewById(R.id.kroot_operation_1_start_time);
        operStartTime_2 = view.findViewById(R.id.kroot_operation_2_start_time);
        operStartTime_3 = view.findViewById(R.id.kroot_operation_3_start_time);
        operStopTime_1 = view.findViewById(R.id.kroot_operation_1_stop_time);
        operStopTime_2 = view.findViewById(R.id.kroot_operation_2_stop_time);
        operStopTime_3 = view.findViewById(R.id.kroot_operation_3_stop_time);
        operAllTime_1 = view.findViewById(R.id.kroot_operation_1_all_time);
        operAllTime_2 = view.findViewById(R.id.kroot_operation_2_all_time);
        operAllTime_3 = view.findViewById(R.id.kroot_operation_3_all_time);

        operStartTimeAll = view.findViewById(R.id.kroot_start_time);
        operStopTimeAll = view.findViewById(R.id.kroot_stop_time);
        operAllTime = view.findViewById(R.id.kroot_all_time);

        rgProgram = view.findViewById(R.id.kroot_rg_program);
        rgTest = view.findViewById(R.id.kroot_rg_test);
        rgKroot = view.findViewById(R.id.kroot_rg_kroot);
        rgOperation = view.findViewById(R.id.kroot_rg_operation_type);
        rgDiff = view.findViewById(R.id.kroot_rg_difficulty);

        rbProgramType_1 = view.findViewById(R.id.program_type_1);
        rbProgramType_2 = view.findViewById(R.id.program_type_2);
        rbTest_1 = view.findViewById(R.id.test_level_1);
        rbTest_2 = view.findViewById(R.id.test_level_2);
        rbTest_3 = view.findViewById(R.id.test_level_3);
        rbTest_4 = view.findViewById(R.id.test_level_4);
        rbTest_5 = view.findViewById(R.id.test_level_5);
        rb_kroot_1 = view.findViewById(R.id.kroot_level_1);
        rb_kroot_2 = view.findViewById(R.id.kroot_level_2);
        rb_kroot_3 = view.findViewById(R.id.kroot_level_3);
        rb_kroot_4 = view.findViewById(R.id.kroot_level_4);
        rb_kroot_5 = view.findViewById(R.id.kroot_level_5);
        rb_diff_1 = view.findViewById(R.id.diff_level_1);
        rb_diff_2 = view.findViewById(R.id.diff_level_2);
        rb_diff_3 = view.findViewById(R.id.diff_level_3);
        rb_diff_4 = view.findViewById(R.id.diff_level_4);
        rb_diff_5 = view.findViewById(R.id.diff_level_5);


        teacherName.setBackgroundResource(android.R.color.transparent);
        teacherID.setBackgroundResource(android.R.color.transparent);
        studentName.setBackgroundResource(android.R.color.transparent);
        studentID.setBackgroundResource(android.R.color.transparent);


        setOnClickTimeListener(prepStartTime_1, true);
        setOnClickTimeListener(prepStartTime_2, true);
        setOnClickTimeListener(prepStartTime_3, true);

        setOnClickTimeListener(prepStopTime_1, false);
        setOnClickTimeListener(prepStopTime_2, false);
        setOnClickTimeListener(prepStopTime_3, false);


        teacherName
                .setText(requireContext()
                        .getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                        .getString("teacher_name", ""));

        teacherID
                .setText(requireContext()
                        .getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                        .getString("teacher_id", ""));

        studentName
                .setText(requireContext()
                        .getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                        .getString("student_name", ""));

        studentID
                .setText(requireContext()
                        .getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                        .getString("student_id", ""));


        prepStartTime_1
                .setText(requireContext()
                        .getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                        .getString("kroot_prepare_1_start_time", ""));
        prepStartTime_2
                .setText(requireContext()
                        .getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                        .getString("kroot_prepare_2_start_time", ""));
        prepStartTime_3
                .setText(requireContext()
                        .getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                        .getString("kroot_prepare_3_start_time", ""));
        prepStopTime_1
                .setText(requireContext()
                        .getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                        .getString("kroot_prepare_1_stop_time", ""));
        prepStopTime_2
                .setText(requireContext()
                        .getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                        .getString("kroot_prepare_2_stop_time", ""));
        prepStopTime_3
                .setText(requireContext()
                        .getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                        .getString("kroot_prepare_3_stop_time", ""));
        prepAllTime_1
                .setText(requireContext()
                        .getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                        .getString("prepare_allTime_1", ""));
        prepAllTime_2
                .setText(requireContext()
                        .getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                        .getString("prepare_allTime_2", ""));
        prepAllTime_3
                .setText(requireContext()
                        .getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                        .getString("prepare_allTime_3", ""));

        operStartTimeAll.setText(requireContext()
                .getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getString("kroot_prepare_1_start_time", ""));


        try {
            operAllTime_1.setText(timeDif(requireActivity().getSharedPreferences("KrootTime", MODE_PRIVATE).getString("time1Start",""),
                    requireActivity().getSharedPreferences("KrootTime", MODE_PRIVATE).getString("time1Stop","")));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        operStartTime_1.setText(requireActivity().getSharedPreferences("KrootTime", MODE_PRIVATE).getString("time1Start",""));


        teacherName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().
                        putString("teacher_name", editable.toString()).apply();
            }
        });

        teacherID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().
                        putString("teacher_id", editable.toString()).apply();
            }
        });

        studentName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().
                        putString("student_name", editable.toString()).apply();
            }
        });

        studentID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().
                        putString("student_id", editable.toString()).apply();
            }
        });


        return view;
    }


    void setOnClickTimeListener(final TextView text, final boolean start) {
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentTime(text, start);
            }
        });
    }


    void getCurrentTime(final TextView mText, final boolean start) {
        final Calendar cal = Calendar.getInstance();
        mHour = cal.get(Calendar.HOUR_OF_DAY);
        mMinute = cal.get(Calendar.MINUTE);
        final String[] id = getResources().getResourceName(mText.getId()).split("/");

        final TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), R.style.DatePicker,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, minute);
                        SimpleDateFormat simpleDate = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        simpleDate.format(cal.getTime());


                        if (start) {
                            switch (mText.getId()) {
                                case R.id.kroot_prepare_1_start_time:
                                    mText.setText(simpleDate.format(cal.getTime()));
                                    operStartTimeAll.setText(simpleDate.format(cal.getTime()));
                                    requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().
                                            putString(id[1], simpleDate.format(cal.getTime())).apply();
                                    Toast.makeText(requireContext(), id[1], Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.kroot_prepare_2_start_time:
                                    try {
                                        if (!prepStopTime_1.getText().toString().isEmpty()) {
                                            if (compareTime("prepare_1_stop", simpleDate.format(cal.getTime()), true)) {
                                                mText.setText(simpleDate.format(cal.getTime()));
                                                requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().
                                                        putString(id[1], simpleDate.format(cal.getTime())).apply();
                                                Toast.makeText(requireContext(), id[1], Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(requireContext(),
                                                        "Время начала этапа должно быть больше времени завершения предыдущего этапа",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(requireContext(), "Завершите предыдущий этап", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case R.id.kroot_prepare_3_start_time:
                                    try {
                                        if (!prepStopTime_2.getText().toString().isEmpty()) {
                                            if (compareTime("prepare_2_stop", simpleDate.format(cal.getTime()), true)) {
                                                mText.setText(simpleDate.format(cal.getTime()));
                                                requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().
                                                        putString(id[1], simpleDate.format(cal.getTime())).apply();
                                                Toast.makeText(requireContext(), id[1], Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(requireContext(),
                                                        "Время начала этапа должно быть больше времени завершения предыдущего этапа",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(requireContext(), "Завершите предыдущий этап", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                            }

                        } else {
                            try {
                                switch (mText.getId()) {
                                    case R.id.kroot_prepare_1_stop_time:
                                        if (!prepStartTime_1.getText().toString().isEmpty()) {
                                            if (compareTime("prepare_1_start", simpleDate.format(cal.getTime()), false)) {
                                                mText.setText(simpleDate.format(cal.getTime()));
                                                requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().
                                                        putString(id[1], simpleDate.format(cal.getTime())).apply();
                                                String s = ((Objects.requireNonNull(simpleDate.parse(requireActivity()
                                                        .getSharedPreferences("GradeAnswer", MODE_PRIVATE).getString(id[1], ""))).getTime() -
                                                        Objects.requireNonNull(simpleDate.parse(requireActivity()
                                                                .getSharedPreferences("GradeAnswer", MODE_PRIVATE).getString("kroot_prepare_1_start_time", ""))).getTime()
                                                ) / (1000 * 60)) % 60 + "\nминут";
                                                prepAllTime_1.setText(s);
                                                requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().
                                                        putString("prepare_allTime_1", s).apply();
                                            } else {
                                                Toast.makeText(requireActivity(),
                                                        "Время окончания этапа должно быть больше времени начала этапа",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(requireContext(), "Начните этап", Toast.LENGTH_SHORT).show();
                                        }
                                        break;

                                    case R.id.kroot_prepare_2_stop_time:
                                        if (!prepStartTime_2.getText().toString().isEmpty()) {
                                            if (compareTime("prepare_2_start", simpleDate.format(cal.getTime()), false)) {
                                                mText.setText(simpleDate.format(cal.getTime()));
                                                requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().
                                                        putString(id[1], simpleDate.format(cal.getTime())).apply();
                                                String s = ((Objects.requireNonNull(simpleDate.parse(requireActivity()
                                                        .getSharedPreferences("GradeAnswer", MODE_PRIVATE).getString(id[1], ""))).getTime() -
                                                        Objects.requireNonNull(simpleDate.parse(requireActivity()
                                                                .getSharedPreferences("GradeAnswer", MODE_PRIVATE).getString("kroot_prepare_2_start_time", ""))).getTime()
                                                ) / (1000 * 60)) % 60 + "\nминут";
                                                prepAllTime_2.setText(s);
                                                requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().
                                                        putString("prepare_allTime_2", s).apply();
                                            } else {
                                                Toast.makeText(requireActivity(),
                                                        "Время окончания этапа должно быть больше времени начала этапа",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(requireContext(), "Начните этап", Toast.LENGTH_SHORT).show();
                                        }
                                        break;

                                    case R.id.kroot_prepare_3_stop_time:
                                        if (!prepStartTime_3.getText().toString().isEmpty()) {
                                            if (compareTime("prepare_3_start", simpleDate.format(cal.getTime()), false)) {
                                                mText.setText(simpleDate.format(cal.getTime()));
                                                requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().
                                                        putString(id[1], simpleDate.format(cal.getTime())).apply();
                                                String s = ((Objects.requireNonNull(simpleDate.parse(requireActivity()
                                                        .getSharedPreferences("GradeAnswer", MODE_PRIVATE).getString(id[1], ""))).getTime() -
                                                        Objects.requireNonNull(simpleDate.parse(requireActivity()
                                                                .getSharedPreferences("GradeAnswer", MODE_PRIVATE).getString("kroot_prepare_3_start_time", ""))).getTime()
                                                ) / (1000 * 60)) % 60 + "\nминут";
                                                prepAllTime_3.setText(s);
                                                requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().
                                                        putString("prepare_allTime_3", s).apply();
                                            } else {
                                                Toast.makeText(requireActivity(),
                                                        "Время окончания этапа должно быть больше времени начала этапа",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(requireContext(), "Начните этап", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " + mText.getId());
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                                Log.e("fragmentKrootStart: ", "onClick --> Parse");
                            }
                        }


                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    boolean compareTime(String compareTo, String compareWhat, boolean isSame) throws ParseException {
        boolean before = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        if (!compareWhat.isEmpty()) {
            if (isSame) {
                if (Objects.requireNonNull(dateFormat.parse(compareWhat))
                        .after(dateFormat.parse(requireActivity()
                                .getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                                .getString("kroot_" + compareTo + "_time", ""))) ||
                        Objects.requireNonNull(dateFormat.parse(compareWhat))
                                .equals(dateFormat.parse(requireActivity()
                                        .getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                                        .getString("kroot_" + compareTo + "_time", "")))) {
                    before = true;
                }
            } else {
                if (Objects.requireNonNull(dateFormat.parse(compareWhat))
                        .after(dateFormat.parse(requireActivity()
                                .getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                                .getString("kroot_" + compareTo + "_time", "")))) {
                    before = true;
                }
            }
        }
        return before;
    }

    String timeDif(String time1, String time2) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return ((Objects.requireNonNull(simpleDateFormat.parse(time2)).getTime() - Objects.requireNonNull(simpleDateFormat.parse(time1)).getTime())/ (1000 * 60)) % 60 + "\nминут";
    }


}
