package itmo.bluetoothChecker.gradeFragment;

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

public class fragmentKrootCalc extends Fragment {
    TextView tvGradeVal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grade, container, false);

        tvGradeVal = view.findViewById(R.id.grade_value);

        tvGradeVal.setText(grade());

        return view;
    }


    String grade(){
        double gradeVal = 0;
        for(int i = 1; i < 22; i++) {
            String name = "ans" + i;
            if(requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt(name, 0) != 0){
                gradeVal += requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt(name, 0);
            }else{
                Toast.makeText(requireContext(), "Grade Error" + name, Toast.LENGTH_SHORT).show();
                Log.e("Calc", name + ": " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt(name, 0));
                break;
            }

        }
        //gradeVal = Double.parseDouble(String.format(Locale.getDefault(),"%.2f", gradeVal));
        return String.format(Locale.getDefault(),"%.2f", gradeVal/21);
    }


}
