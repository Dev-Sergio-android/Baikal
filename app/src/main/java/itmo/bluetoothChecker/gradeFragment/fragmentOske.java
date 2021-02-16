package itmo.bluetoothChecker.gradeFragment;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import itmo.bluetoothChecker.R;
import itmo.bluetoothChecker.TabThree;
import itmo.bluetoothChecker.TabTwo;

import static android.content.Context.MODE_PRIVATE;

public class fragmentOske extends Fragment {

    private final String TAG = "fragment_Oske";

    private int cntItem = 0;
    int pos;
    static int checkedIndex;


    static TextView gradeTitle;

    TextView grade;


    static RadioButton rbYes;
    static RadioButton rbNo;
    static RadioGroup rgYesNo;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_oske, container, false);

        final StringBuilder sb = new StringBuilder(20);

        gradeTitle = view.findViewById(R.id.item_grade);

        rbYes = view.findViewById(R.id.rb_yes);
        rbNo = view.findViewById(R.id.rb_no);
        rgYesNo = view.findViewById(R.id.rg_yes_no);


        cntItem = TabThree.getCntItem();

        if(requireActivity().getSharedPreferences("gr_sys", MODE_PRIVATE).getInt("gr_sys", 1) == 1) {
            gradeTitle.setText(Html.fromHtml(sb.append("<b>№").append(cntItem + 1).append("  </b>")
                    .append(getResources().getTextArray(R.array.oske_items)[cntItem]).toString()));
        }else{
            gradeTitle.setText(Html.fromHtml(sb.append("<b>№").append(cntItem + 1).append("  </b>")
                    .append(getResources().getTextArray(R.array.lp_items)[cntItem]).toString()));
        }
        gradeTitle.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);

        TabThree.setCntItem(cntItem += 1);


        if(requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt("ans1", -1) != -1){
            Log.i(TAG,"load" + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt("ans1", -1));
            loadAns(cntItem);
        }

        //saveAns(cntItem, checkedIndex);


        rgYesNo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup rGroup, int checkedId) {

                int radioBtnID = rGroup.getCheckedRadioButtonId();

                RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(checkedId);

                checkedIndex = rGroup.indexOfChild(checkedRadioButton);

                View rb = rGroup.findViewById(radioBtnID);

                pos = rGroup.indexOfChild(rb) + 1;
            }
        });

        return view;
    }

    void saveAns(int question, int witch) {
        String name  = "ans" + question;
        Log.i(TAG, "save name -> " + name);
        SharedPreferences.Editor ed = requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit();
        ed.putInt(name, witch).apply();
        Log.i(TAG, "save data -> " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt(name, 0));
    }

    void loadAns(int question) {
        String name  = "ans" + question;
        //requireActivity().getPreferences(MODE_PRIVATE).getInt(name, 1);
        int savedRadioIndex = requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt(name, 0);
        Log.e(TAG, "RB load index: " + savedRadioIndex);
        RadioButton savedCheckedRadioButton = (RadioButton) rgYesNo.getChildAt(savedRadioIndex);
        savedCheckedRadioButton.setChecked(true);



        Log.i(TAG, "load -> name: " + name + " -> " + savedCheckedRadioButton.isChecked());
    }


    public static void setRbYes(RadioButton rbYes) {
        fragmentOske.rbYes = rbYes;
    }

    public static void setRbNo(RadioButton rbNo) {
        fragmentOske.rbNo = rbNo;
    }

    public static boolean rgOskeChecked() {
        return rbYes.isChecked() || rbNo.isChecked();
    }

    public static void rgOskeClear(){
        rgYesNo.clearCheck();
    }

    public static int getCheckedIndex() {
        return checkedIndex;
    }

    public static void setGradeTitle(Spanned s) {
        gradeTitle.setText(s);
    }

    public static RadioGroup getRgOske() {
        return rgYesNo;
    }

}
