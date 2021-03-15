package itmo.bluetoothChecker.gradeFragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import itmo.bluetoothChecker.R;

import static android.content.Context.MODE_PRIVATE;

public class fragmentOskeCalc extends Fragment {
    TextView tvGradeVal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grade, container, false);
        Log.e("CALC", "" + requireActivity().getSharedPreferences("gr_sys", MODE_PRIVATE).getInt("gr_sys", 5));
        tvGradeVal = view.findViewById(R.id.grade_value);
        tvGradeVal.setTextSize(20);
        tvGradeVal.setText(grade(requireContext()));

        return view;
    }


    String grade(Context context) {
        String result = "";
        Log.e("CALC", "" + context.getSharedPreferences("gr_sys", MODE_PRIVATE).getInt("gr_sys", 5));
        if (context.getSharedPreferences("gr_sys", MODE_PRIVATE).getInt("gr_sys", -1) == 1) {
            for (int i = 1; i < 46; i++) {
                String name = "ans" + i;

                if (context.getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt(name, -1) != -1) {
                    if (context.getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt(name, -1)
                            != getResources().getIntArray(R.array.oskeAnswer)[i - 1]) {
                        result = "Неудовлетворительно";
                        break;
                    } else {
                        Log.i("oskeCalc", "answer " + i + ":   "
                                + context.getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt(name, -1)
                                + " array " + i + ":   " + getResources().getIntArray(R.array.oskeAnswer)[i - 1]);
                    }
                }

            }
            if (result.isEmpty()) {
                result = "Удовлетворительно";
            }
        } else if (context.getSharedPreferences("gr_sys", MODE_PRIVATE).getInt("gr_sys", -1) == 2) {
            double good = 0;
            int len = getResources().getStringArray(R.array.lp_items).length;

            for (int i = 1; i <= len; i++) {
                String name = "ans" + i;

                if (context.getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt(name, -1) == 0) {
                    good += 1;
                }

            }

            result = (int) good + "/" + len + "\n" + "\n" + String.format(Locale.getDefault(), "%.1f", (good * 100 / len) ) + "%";

            requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().putString("resultLP", result).apply();

            Log.e("Calc", "" + (good * 100 / len));

        } else {
            Log.e("CALC", "OskeCalc Exception");
        }

        return result;
    }


}
