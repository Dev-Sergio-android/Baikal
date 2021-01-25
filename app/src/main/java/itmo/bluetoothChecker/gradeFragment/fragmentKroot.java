package itmo.bluetoothChecker.gradeFragment;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import itmo.bluetoothChecker.R;
import itmo.bluetoothChecker.TabThree;

import static android.content.Context.MODE_PRIVATE;

public class fragmentKroot extends Fragment {

    private final String TAG = "fragment_Kroot";

    private static RadioButton rb1, rb2, rb3 ,rb4, rb5;
    static RadioGroup rgKroot;
    static TextView krootStage;

    int cntItem;

    private int pos;
    private static int checkedIndex;



    public static RadioGroup getRgKroot() {
        return rgKroot;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kroot, container, false);

        rb1 = view.findViewById(R.id.rb_1);
        rb2 = view.findViewById(R.id.rb_2);
        rb3 = view.findViewById(R.id.rb_3);
        rb4 = view.findViewById(R.id.rb_4);
        rb5 = view.findViewById(R.id.rb_5);


        rgKroot = view.findViewById(R.id.rg_kroot);

        rb1.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        rb2.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        rb3.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        rb4.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        rb5.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);

        krootStage = view.findViewById(R.id.kroot_stage);



        cntItem = TabThree.getCntItem();

        setTextRb(cntItem);


        TabThree.setCntItem(cntItem += 1);

        krootStage.setText(Html.fromHtml(getResources().getTextArray(R.array.stage_name)[TabThree.getCntItem()].toString()));

        if(requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt("ans1", -1) != -1){
            Log.i(TAG,"load");
            loadAns(cntItem);
        }

        Toast.makeText(requireContext(),  "cnt set:  " + cntItem, Toast.LENGTH_SHORT).show();


        rgKroot.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup rGroup, int checkedId) {

                int radioBtnID = rGroup.getCheckedRadioButtonId();

                RadioButton checkedRadioButton = rGroup.findViewById(checkedId);

                checkedIndex = rGroup.indexOfChild(checkedRadioButton);

                View rb = rGroup.findViewById(radioBtnID);

                pos = rGroup.indexOfChild(rb) + 1;
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }



    public void setTextRb(int cntItem){
        StringBuilder sb = new StringBuilder(20);
        rb1.setText(Html.fromHtml(sb.append("<b><u>Плохо</u></b>").append(" - ").append(getResources().getStringArray(R.array.kroot_items)[3 * cntItem]).toString()));
        sb.delete(0, sb.length());
        rb2.setText(Html.fromHtml("<b><u>Неудовлетворительно</u></b>"));
        rb3.setText(Html.fromHtml(sb.append("<b><u>Удовлетворительно</u></b>").append(" - ").append(getResources().getStringArray(R.array.kroot_items)[3* cntItem + 1]).toString()));
        sb.delete(0, sb.length());
        rb4.setText(Html.fromHtml("<b><u>Хорошо</b></u>"));
        rb5.setText(Html.fromHtml(sb.append("<b><u>Отлично</u></b>").append(" - ").append(getResources().getStringArray(R.array.kroot_items)[3* cntItem + 2]).toString()));
        sb.delete(0, sb.length());
    }

    void loadAns(int question) {
        String name  = "ans" + question;
        int savedRadioIndex = requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt(name, 5);
        RadioButton savedCheckedRadioButton = (RadioButton) rgKroot.getChildAt(savedRadioIndex);
        savedCheckedRadioButton.setChecked(true);
        Log.i(TAG, "load -> name: " + name + " -> " + savedCheckedRadioButton.isChecked());
    }


    public static void setRb1(Spanned s) {
        rb1.setText(s);
    }

    public static void setRb2(Spanned s) {
        rb2.setText(s);
    }

    public static void setRb3(Spanned s) {
        rb3.setText(s);
    }

    public static void setRb4(Spanned s) {
        rb4.setText(s);
    }

    public static void setRb5(Spanned s) {
        rb5.setText(s);
    }

    public static void setKrootTitle(Spanned s) {
        Log.e("Fragment Kroot: ", s.toString());
        krootStage.setText(s);
    }

    public static boolean rgKrootChecked() {
        return rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked() || rb5.isChecked();
    }

    public static void rgKrootClear(){
       rgKroot.clearCheck();
    }

    public static int getCheckedIndex() {
        return checkedIndex;
    }
}
