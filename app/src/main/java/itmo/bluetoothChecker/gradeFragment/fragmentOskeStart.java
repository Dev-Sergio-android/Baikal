package itmo.bluetoothChecker.gradeFragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import java.util.Calendar;

import itmo.bluetoothChecker.R;

import static android.content.Context.MODE_PRIVATE;

public class fragmentOskeStart extends Fragment {

    static TextView mDate;
    static EditText mStNumber;

    private int mYear, mMonth, mDay;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_oske_start, container, false);

        mDate = view.findViewById(R.id.oske_date);
        mDate.setText(requireContext().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getString("date", ""));

        mStNumber = view.findViewById(R.id.oske_text_stud_numb);
        mStNumber.setBackgroundResource(android.R.color.transparent);
        mStNumber.setText(requireContext().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getString("num", ""));

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDatePicker();
            }
        });

        return view;
    }

    public static String getmDate() {
        return mDate.getText().toString();
    }

    public static String getmStNumber() {
        return mStNumber.getText().toString();
    }

    public static boolean OskeStartChecked() {
        Log.e("Oske Start", mDate.getText().toString().isEmpty() + " and " + mStNumber.getText().toString().isEmpty());
        return !mDate.getText().toString().isEmpty() && !mStNumber.getText().toString().isEmpty();
    }

    private void callDatePicker() {
        // получаем текущую дату
        final Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        // инициализируем диалог выбора даты текущими значениями
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), R.style.DatePicker,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String sDay;
                        String sMonth;

                        if (dayOfMonth < 10) {
                            sDay = "0" + dayOfMonth;
                        } else {
                            sDay = String.valueOf(dayOfMonth);
                        }

                        if (monthOfYear < 10) {
                            sMonth = "0" + (monthOfYear + 1);
                        } else {
                            sMonth = String.valueOf(monthOfYear + 1);
                        }


                        String editTextDateParam = sDay + "." + sMonth + "." + year;
                        mDate.setText(editTextDateParam);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

}
