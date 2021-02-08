package itmo.bluetoothChecker.gradeFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import itmo.bluetoothChecker.R;

import static android.content.Context.MODE_PRIVATE;

public class fragmentOskeCalc extends Fragment {
    TextView tvGradeVal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grade, container, false);

        tvGradeVal = view.findViewById(R.id.grade_value);
        tvGradeVal.setTextSize(20);
        tvGradeVal.setText(grade());

        return view;
    }


    String grade(){
        String result = "";
        for(int i = 1; i < 46; i++) {
            String name = "ans" + i;

            if(requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt(name, -1) != -1){
                if(requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt(name, -1)
                        != getResources().getIntArray(R.array.oskeAnswer)[i-1]){
                            result = "Неудовлетворительно";
                            break;
                }else{
                    Log.i("oskeCalc", "answer " + i + ":   "
                            + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt(name, -1)
                            + " array " + i + ":   " + getResources().getIntArray(R.array.oskeAnswer)[i-1]);
                }
            }

        }
        if(result.isEmpty()){result = "Удовлетворительно";}
        return result;
    }


}
