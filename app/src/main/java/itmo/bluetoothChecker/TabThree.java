package itmo.bluetoothChecker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.text.Html;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import itmo.bluetoothChecker.gradeFragment.fragmentKroot;
import itmo.bluetoothChecker.gradeFragment.fragmentKrootCalc;
import itmo.bluetoothChecker.gradeFragment.fragmentKrootStart;
import itmo.bluetoothChecker.gradeFragment.fragmentOske;
import itmo.bluetoothChecker.gradeFragment.fragmentOskeCalc;
import itmo.bluetoothChecker.gradeFragment.fragmentOskeStart;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.PRINT_SERVICE;
import static itmo.bluetoothChecker.TabTwo.flagComplete;
import static itmo.bluetoothChecker.gradeFragment.fragmentKroot.rgKrootChecked;
import static itmo.bluetoothChecker.gradeFragment.fragmentKroot.rgKrootClear;
import static itmo.bluetoothChecker.gradeFragment.fragmentKroot.setKrootTitle;
import static itmo.bluetoothChecker.gradeFragment.fragmentOske.getCheckedIndex;
import static itmo.bluetoothChecker.gradeFragment.fragmentOske.rgOskeChecked;
import static itmo.bluetoothChecker.gradeFragment.fragmentOske.rgOskeClear;
import static itmo.bluetoothChecker.gradeFragment.fragmentOske.setGradeTitle;
import static itmo.bluetoothChecker.gradeFragment.fragmentOskeStart.OskeStartChecked;
import static itmo.bluetoothChecker.gradeFragment.fragmentOskeStart.getmDate;
import static itmo.bluetoothChecker.gradeFragment.fragmentOskeStart.getmStNumber;


public class TabThree extends Fragment {

    private final String SYS_GRADE = "MedSimTech_sys_grade";
    private final String TAG = "check";

    private final String CONNECTION = "MedSimTech_bl_connection";
    private final String TIME = "MedSimTech_send_time";

    private static int cntItem = 0;

    private int pos;

    Spinner spinner;
    Button btnNext, btnPrev;
    ImageButton btnDel;

    private final BroadcastReceiver tabThreeReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TIME.equals(action)) {
                Log.e(TAG, "broadcast");
                if(intent.hasExtra("send_message_time_reset")){
                    if(loadMode() == 0) {
                        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                        ft.replace(R.id.parent_fragment, new fragmentKrootStart()).commit();
                    }
                }else{
                    Log.e(TAG, "Exception Broadcast");
                }

            }
        }
    };



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        try {
            if(loadMode() == 1 || loadMode() == 2) {
                cntItem = 0;
                requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().clear().apply();
                transaction.replace(R.id.parent_fragment, new fragmentOskeStart()).commit();
            }else{
                cntItem = 0;
                requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().clear().apply();
                transaction.replace(R.id.parent_fragment, new fragmentKrootStart()).commit();
            }
        } catch (Exception e) {
            Log.e(TAG, "ERROR in onActivityCreated");
        }

    }


    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_check, container, false);

        Objects.requireNonNull(requireActivity()).registerReceiver(tabThreeReceiver, new IntentFilter(TIME));

        btnNext = view.findViewById(R.id.btn_next);
        btnPrev = view.findViewById(R.id.btn_prev);
        btnDel = view.findViewById(R.id.btn_del);

        spinner = view.findViewById(R.id.sp_grade_system);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.grade_systems, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(loadMode());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    cntItem = 0;
                    btnPrev.setVisibility(View.INVISIBLE);
                    btnDel.setVisibility(View.VISIBLE);
                    btnNext.setText(getResources().getString(R.string.button_next));
                    requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().clear().apply();
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.replace(R.id.parent_fragment, new fragmentOskeStart()).commit();
                } else if (i == 2) {
                    cntItem = 0;
                    btnPrev.setVisibility(View.INVISIBLE);
                    btnDel.setVisibility(View.VISIBLE);
                    btnNext.setText(getResources().getString(R.string.button_next));
                    requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().clear().apply();
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.replace(R.id.parent_fragment, new fragmentOskeStart()).commit();

                } else {
                    cntItem = 0;
                    btnPrev.setVisibility(View.INVISIBLE);
                    btnDel.setVisibility(View.VISIBLE);
                    btnNext.setText(getResources().getString(R.string.button_next));
                    requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().clear().apply();
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.replace(R.id.parent_fragment, new fragmentKrootStart()).commit();
                }
                saveSys(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!requireActivity().getSharedPreferences(CONNECTION, MODE_PRIVATE).getBoolean("con_state", false)) {
                    if (flagComplete) {

                        switch (spinner.getSelectedItemPosition()) {
                            case 1:
                                oske(getResources().getTextArray(R.array.oske_items));
                                break;

                            case 2:
                                oske(getResources().getTextArray(R.array.lp_items));
                                break;

                            default:

                                if (cntItem == 0) {
                                    btnNext.setText("СЛЕД.");
                                    Fragment fragment = new fragmentKroot();
                                    FragmentTransaction transaction1 = getChildFragmentManager().beginTransaction();
                                    transaction1.replace(R.id.parent_fragment, fragment).commit();
                                    btnPrev.setVisibility(View.VISIBLE);
                                    btnDel.setVisibility(View.INVISIBLE);
                                } else if (cntItem > 0 && cntItem < (getResources().getTextArray(R.array.kroot_items).length) / 3) {

                                    if (rgKrootChecked()) {

                                        saveAns(cntItem, fragmentKroot.getCheckedIndex() + 1);

                                        Log.e(TAG, "answer " + cntItem + ":   " + fragmentKroot.getCheckedIndex());

                                        if (cntItem == (getResources().getTextArray(R.array.kroot_items).length / 3) - 1) {
                                            btnNext.setText(getResources().getString(R.string.button_calc));
                                        }

                                        setRB(cntItem);

                                        cntItem += 1;
                                        rgKrootClear();

                                        String name = "ans" + (cntItem);
                                        //Toast.makeText(requireContext(), " -- SharedPref is " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt("ans" + cntItem, -1), Toast.LENGTH_SHORT).show();
                                        if (requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt(name, -1) != -1) {
                                            Log.i(TAG, " ----------------------- load ---------------------");
                                            loadAns(cntItem);
                                        }

                                        titleKroot();

                                    } else {
                                        Toast.makeText(requireActivity(), "Выберите ответ", Toast.LENGTH_SHORT).show();
                                    }
                                }else if (cntItem == (getResources().getTextArray(R.array.kroot_items).length / 3)){
                                    if (rgKrootChecked()) {
                                        saveAns(cntItem, fragmentKroot.getCheckedIndex() + 1);
                                        try {
                                            pdfRes();
                                        }catch(Exception e){
                                            Log.e(TAG, e.toString());
                                        }
                                        Fragment fragment = new fragmentKrootCalc();
                                        FragmentTransaction transaction2 = getChildFragmentManager().beginTransaction();
                                        transaction2.replace(R.id.parent_fragment, fragment).commit();
                                        btnPrev.setVisibility(View.INVISIBLE);
                                        btnNext.setText(getResources().getString(R.string.oske_button_end));
                                        cntItem += 1;
                                    } else {
                                        Toast.makeText(requireActivity(), "Выберите ответ", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Fragment fragment = new fragmentKrootStart();
                                    FragmentTransaction transaction1 = getChildFragmentManager().beginTransaction();
                                    transaction1.replace(R.id.parent_fragment, fragment).commit();

                                    btnPrev.setVisibility(View.INVISIBLE);
                                    btnDel.setVisibility(View.VISIBLE);
                                    cntItem = 0;
                                    btnNext.setText(getResources().getString(R.string.button_next));
                                    requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().clear().apply();
                                    //Toast.makeText(requireActivity(), "cnt reset", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }


                    } else {
                        Toast.makeText(requireActivity(), "Недоступно во время операции", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(requireActivity(), "Ваше устройство не подключено к тренажеру", Toast.LENGTH_SHORT).show();
                }

                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!requireActivity().getSharedPreferences(CONNECTION, MODE_PRIVATE).getBoolean("con_state", false)) {
                    if (flagComplete) {

                        switch (spinner.getSelectedItemPosition()) {
                            case 1:
                                oskePrev(getResources().getTextArray(R.array.oske_items));
                                break;

                            case 2:
                                oskePrev(getResources().getTextArray(R.array.lp_items));
                                break;

                            default:
                                if (cntItem == 1) {
                                    cntItem -= 1;
                                    Fragment fragment = new fragmentKrootStart();
                                    FragmentTransaction transaction1 = getChildFragmentManager().beginTransaction();
                                    transaction1.replace(R.id.parent_fragment, fragment).commit();
                                    btnPrev.setVisibility(View.INVISIBLE);
                                    btnDel.setVisibility(View.VISIBLE);
                                } else if (cntItem > 1 && cntItem <= (getResources().getTextArray(R.array.kroot_items).length) / 3) {

                                    cntItem -= 1;

                                    setRB(cntItem - 1);

                                    rgKrootClear();

                                    //Toast.makeText(requireActivity(), "cntItem decrease to " + cntItem, Toast.LENGTH_SHORT).show();

                                    if (cntItem == (getResources().getTextArray(R.array.kroot_items).length / 3) - 1) {
                                        btnNext.setText(getResources().getString(R.string.button_next));
                                    }


                                    if (cntItem != 0) {
                                        String name = "ans" + (cntItem);
                                        //Toast.makeText(requireContext(), " -- SharedPref is " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt("ans" + cntItem, -1), Toast.LENGTH_SHORT).show();
                                        if (requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt(name, -1) != -1) {
                                            Log.i(TAG, " ----------------------- load ---------------------");
                                            loadAns(cntItem);
                                        }
                                    }

                                    titleKroot();

                                } else {
                                    Toast.makeText(requireActivity(), "code:101", Toast.LENGTH_SHORT).show();
                                }

                                break;
                        }
                    } else {
                        Toast.makeText(requireActivity(), "Недоступно во время операции", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireActivity(), "Ваше устройство не подключено к тренажеру", Toast.LENGTH_SHORT).show();
                }

                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }
        });


        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);
                alertDialog.setTitle("Удаление данных")
                        .setMessage("Введенные данные будут стерты.\nПродолжить?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Fragment fragment = new Fragment();
                                        switch (spinner.getSelectedItemPosition()) {
                                            case 1:
                                                fragment = new fragmentOskeStart();
                                                break;
                                            case 2:
                                                fragment = new fragmentOskeStart();
                                                break;
                                            default:
                                                fragment = new fragmentKrootStart();
                                                break;
                                        }

                                        FragmentTransaction transactionDel = getChildFragmentManager().beginTransaction();
                                        transactionDel.replace(R.id.parent_fragment, fragment).commit();
                                        btnPrev.setVisibility(View.INVISIBLE);
                                        cntItem = 0;
                                        requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().clear().apply();

                                        /*Toast.makeText(requireActivity(), "cntItem reset to " + cntItem
                                                        + " SharedPref is " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt("ans2", -1)
                                                , Toast.LENGTH_SHORT).show();*/
                                        dialogInterface.cancel();
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                                    }
                                }
                        )
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                            }
                        });
                alertDialog.show();
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }

        });

        return view;
    }
///////////////////////////////////////////////////////////////////       OSKE      /////////////////////////////////////////////////////////

    void oske(CharSequence[] charSequences){

        if (cntItem == 0) {
            if (OskeStartChecked()) {
                requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                        .edit().putString("date", getmDate())
                        .putString("num", getmStNumber()).apply();
                btnNext.setText("СЛЕД.");
                Fragment fragment = new fragmentOske();
                FragmentTransaction transaction1 = getChildFragmentManager().beginTransaction();
                transaction1.replace(R.id.parent_fragment, fragment).commit();
                btnPrev.setVisibility(View.VISIBLE);
                btnDel.setVisibility(View.INVISIBLE);
            } else {
                Toast.makeText(requireActivity(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            }
        } else if (cntItem > 0 && cntItem < (charSequences.length)) {
            if (rgOskeChecked()) {
                StringBuilder sb = new StringBuilder(20);

                if (cntItem == charSequences.length) {
                    saveAns(cntItem, getCheckedIndex());
                    cntItem = 0;

                    setGradeTitle(Html.fromHtml(sb.append("<b>№").append(cntItem).append("  </b>")
                            .append(charSequences[cntItem]).toString()));

                    sb.delete(0, sb.length());
                    btnPrev.setVisibility(View.INVISIBLE);
                    btnDel.setVisibility(View.VISIBLE);
                    btnNext.setText(getResources().getString(R.string.button_next));
                    requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().clear().apply();
                    rgOskeChecked();
                } else {

                    btnPrev.setVisibility(View.VISIBLE);
                    btnDel.setVisibility(View.INVISIBLE);

                    saveAns(cntItem, getCheckedIndex());

                    Log.e(TAG, "answer " + cntItem + ":   " + getCheckedIndex());

                    if (cntItem == charSequences.length - 1) {
                        btnNext.setText(getResources().getString(R.string.button_calc));
                    }else {
                        btnNext.setText("СЛЕД.");
                    }

                    setGradeTitle(Html.fromHtml(sb.append("<b>№").append(cntItem + 1).append("  </b>")
                            .append(charSequences[cntItem]).toString()));

                    sb.delete(0, sb.length());

                    cntItem += 1;

                    fragmentOske.rgOskeClear();

                    String name = "ans" + (cntItem);

                    if (requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt(name, -1) != -1) {
                        Log.i(TAG, " ----------------------- load ---------------------");
                        loadAns(cntItem);
                    }

                }

            } else {
                Toast.makeText(requireActivity(),
                        "Выберите ответ",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (cntItem == (charSequences.length)){
            if (rgOskeChecked()) {
                saveAns(cntItem, getCheckedIndex());
                Log.e(TAG, "answer " + cntItem + ":   " + getCheckedIndex());

                Fragment fragment = new fragmentOskeCalc();
                FragmentTransaction transaction3 = getChildFragmentManager().beginTransaction();
                transaction3.replace(R.id.parent_fragment, fragment).commit();
                btnPrev.setVisibility(View.INVISIBLE);
                btnNext.setText(getResources().getString(R.string.oske_button_end));
                cntItem += 1;
            } else {
                Toast.makeText(requireActivity(),
                        "Выберите ответ",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Fragment fragment = new fragmentOskeStart();
            FragmentTransaction transaction1 = getChildFragmentManager().beginTransaction();
            transaction1.replace(R.id.parent_fragment, fragment).commit();
            //Toast.makeText(requireActivity(), "switch 1: cnt is " + cntItem, Toast.LENGTH_SHORT).show();
            btnPrev.setVisibility(View.INVISIBLE);
            btnDel.setVisibility(View.VISIBLE);
            cntItem = 0;
            btnNext.setText(getResources().getString(R.string.button_next));
            requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().clear().apply();
            //Toast.makeText(requireActivity(), "cnt reset", Toast.LENGTH_SHORT).show();
        }

    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void oskePrev(CharSequence[] charSequences){
        StringBuilder sb = new StringBuilder(20);

        if (cntItem == 1) {
            cntItem -= 1;
            Fragment fragment = new fragmentOskeStart();
            FragmentTransaction transaction1 = getChildFragmentManager().beginTransaction();
            transaction1.replace(R.id.parent_fragment, fragment).commit();


            Log.e(TAG, requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getString("date", "empty"));
            Log.e(TAG, requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getString("num", "empty"));

            btnPrev.setVisibility(View.INVISIBLE);
            btnDel.setVisibility(View.VISIBLE);
        } else if (cntItem > 1 && cntItem <= (charSequences.length)) {

            cntItem -= 1;

            if (cntItem == charSequences.length - 1) {
                btnNext.setText(getResources().getString(R.string.button_next));
            }

                                    /*Toast.makeText(requireActivity(),
                                            "cntItem --> " + cntItem,
                                            Toast.LENGTH_SHORT).show();*/

            setGradeTitle(Html.fromHtml(sb.append("<b>№").append(cntItem).append("  </b>")
                    .append(charSequences[cntItem - 1]).toString()));

            sb.delete(0, sb.length());

            rgOskeClear();

            loadAns(cntItem);

        } else {
            Toast.makeText(requireActivity(),
                    "Недоступно во время операции",
                    Toast.LENGTH_SHORT).show();
        }

        requireView().performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

    }



    void saveAns(int question, int witch) {
        String name = "ans" + question;
        Log.i(TAG, "save name -> " + name);
        SharedPreferences.Editor ed = requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit();
        ed.putInt(name, witch).apply();
        Log.i(TAG, "save data -> " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt(name, 0));
    }

    void loadAns(int question) {
        RadioButton savedCheckedRadioButton;
        String name = "ans" + question;
        Log.e(TAG, "Name load is " + name);
        int savedRadioIndex = requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt(name, 0);
        Log.e(TAG, "RB load index: " + savedRadioIndex);
        if (spinner.getSelectedItemPosition() == 0) {
            savedCheckedRadioButton = (RadioButton) fragmentKroot.getRgKroot().getChildAt(savedRadioIndex);
        } else {
            savedCheckedRadioButton = (RadioButton) fragmentOske.getRgOske().getChildAt(savedRadioIndex);
        }
        savedCheckedRadioButton.setChecked(true);

        Log.i(TAG, "load -> name: " + name + " -> " + savedCheckedRadioButton.isChecked());
    }

    void titleKroot() {
        if (cntItem < 7) {
            Log.e("Fragment Three: ", cntItem + " --> " + getResources().getTextArray(R.array.stage_name)[cntItem]);
            setKrootTitle(Html.fromHtml(getResources().getTextArray(R.array.stage_name)[cntItem].toString()));
        } else if (cntItem < 10) {
            Log.e("Fragment Three: ", cntItem + " --> " + getResources().getTextArray(R.array.stage_name)[7] + getResources().getTextArray(R.array.stage)[cntItem % 3]);
            setKrootTitle(Html.fromHtml(getResources().getTextArray(R.array.stage_name)[7].toString() + getResources().getTextArray(R.array.stage)[cntItem % 3].toString()));
        } else if (cntItem < 13) {
            Log.e("Fragment Three: ", cntItem + " --> " + getResources().getTextArray(R.array.stage_name)[8] + getResources().getTextArray(R.array.stage)[cntItem % 3]);
            setKrootTitle(Html.fromHtml(getResources().getTextArray(R.array.stage_name)[8].toString() + getResources().getTextArray(R.array.stage)[cntItem % 3].toString()));
        } else if (cntItem < 16) {
            Log.e("Fragment Three: ", cntItem + " --> " + getResources().getTextArray(R.array.stage_name)[9] + getResources().getTextArray(R.array.stage)[cntItem % 3]);
            setKrootTitle(Html.fromHtml(getResources().getTextArray(R.array.stage_name)[9].toString() + getResources().getTextArray(R.array.stage)[cntItem % 3].toString()));
        } else if (cntItem < 19) {
            Log.e("Fragment Three: ", cntItem + " --> " + getResources().getTextArray(R.array.stage_name)[10] + getResources().getTextArray(R.array.stage)[cntItem % 3]);
            setKrootTitle(Html.fromHtml(getResources().getTextArray(R.array.stage_name)[10].toString() + getResources().getTextArray(R.array.stage)[cntItem % 3].toString()));
        } else {
            Log.e("Fragment Three: ", cntItem + " --> " + getResources().getTextArray(R.array.stage_name)[11] + getResources().getTextArray(R.array.stage)[cntItem % 3]);
            setKrootTitle(Html.fromHtml(getResources().getTextArray(R.array.stage_name)[11].toString() + getResources().getTextArray(R.array.stage)[cntItem % 3].toString()));
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().unregisterReceiver(tabThreeReceiver);
    }

    void setRB(int param) {
        StringBuilder sb = new StringBuilder(20);
        fragmentKroot.setRb1(Html.fromHtml(sb.append("<b><u>Плохо</u></b>").append(" - ").append(getResources().getStringArray(R.array.kroot_items)[3 * param]).toString()));
        sb.delete(0, sb.length());
        fragmentKroot.setRb2(Html.fromHtml("<b><u>Неудовлетворительно</u></b>"));
        sb.delete(0, sb.length());
        fragmentKroot.setRb3(Html.fromHtml(sb.append("<b><u>Удовлетворительно</u></b>").append(" - ").append(getResources().getStringArray(R.array.kroot_items)[3 * param + 1]).toString()));
        sb.delete(0, sb.length());
        fragmentKroot.setRb4(Html.fromHtml("<b><u>Хорошо</u></b>"));
        sb.delete(0, sb.length());
        fragmentKroot.setRb5(Html.fromHtml(sb.append("<b><u>Отлично</u></b>").append(" - ").append(getResources().getStringArray(R.array.kroot_items)[3 * param + 2]).toString()));
        sb.delete(0, sb.length());
    }

    
    void pdfRes(){
        Paint title = new Paint();
        Paint content = new Paint();
        float pageWidth = 600;

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());

        PrintAttributes printAttrs = new PrintAttributes.Builder().
                setColorMode(PrintAttributes.COLOR_MODE_COLOR).
                setMediaSize(PrintAttributes.MediaSize.NA_LETTER).
                setResolution(new PrintAttributes.Resolution("MedSim", PRINT_SERVICE, 300, 300)).
                setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                build();

        PdfDocument document = new PrintedPdfDocument(requireActivity(), printAttrs);
        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 1100, 1).create();
        // create a new page from the PageInfo
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        title.setTextSize(30);

        content.setTextAlign(Paint.Align.LEFT);
        content.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        content.setTextSize(14);

        canvas.drawText("MedSimTech", pageWidth/2, 35, title);

        canvas.drawText("Дата: " , requireActivity().getResources().getInteger(R.integer.pdf_margin_title), 80, content);
        canvas.drawText(formatter.format(date) , requireActivity().getResources().getInteger(R.integer.pdf_margin_title_content), 80, content);
        canvas.drawText("Аттестуемый: ", requireActivity().getResources().getInteger(R.integer.pdf_margin_title), 110, content);
        canvas.drawText("Аттестующий: ", requireActivity().getResources().getInteger(R.integer.pdf_margin_title), 140, content);
        canvas.drawText("Тип операции: ", requireActivity().getResources().getInteger(R.integer.pdf_margin_title), 170, content);
        canvas.drawText("Время выполнения операции: ", requireActivity().getResources().getInteger(R.integer.pdf_margin_title), 200, content);
        canvas.drawText("Время выполнения этапов: ", requireActivity().getResources().getInteger(R.integer.pdf_margin_title), 230, content);
        canvas.drawText("Оцениваемые параметры: ", requireActivity().getResources().getInteger(R.integer.pdf_margin_title), 260, content);
        canvas.drawText("ПОДГОТОВКА БОЛЬНОГО - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans1", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 290, content);
        canvas.drawText("ОБРАБОТКА РУК - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans2", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 320, content);
        canvas.drawText("НАДЕВАНИЕ ХАЛАТА - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans3", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 350, content);
        canvas.drawText("НАДЕВАНИЕ ПЕРЧАТОК - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans4", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 380, content);
        canvas.drawText("ОБРАБОТКА ОПЕРАЦИОННОГО ПОЛЯ - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans5", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 410, content);
        canvas.drawText("ОТГРАНИЧЕНИЕ ОПЕРАЦИОННОГО ПОЛЯ - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans6", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 440, content);
        canvas.drawText("РАЗРЕЗ:ТЕХНИКА - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans7", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 470, content);
        canvas.drawText("РАЗРЕЗ:АНАТОМИЯ - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans8", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 500, content);
        canvas.drawText("РАЗРЕЗ:ОРГАНИЗАЦИЯ - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans9", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 530, content);
        canvas.drawText("СОСУДИСТЫЙ ДОСТУП:ТЕХНИКА - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans10", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 560, content);
        canvas.drawText("СОСУДИСТЫЙ ДОСТУП:АНАТОМИЯ - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans11", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 590, content);
        canvas.drawText("СОСУДИСТЫЙ ДОСТУП:ОРГАНИЗАЦИЯ - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans12", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 620, content);
        canvas.drawText("ИДЕНТИФИКАЦИЯ АРТЕРИИ:ТЕХНИКА - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans13", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 650, content);
        canvas.drawText("ИДЕНТИФИКАЦИЯ АРТЕРИИ:АНАТОМИЯ - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans14", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 680, content);
        canvas.drawText("ИДЕНТИФИКАЦИЯ АРТЕРИИ:ОРГАНИЗАЦИЯ - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans15", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 710, content);
        canvas.drawText("ВСКРЫТИЕ ПРОСВЕТА ТРАХЕИ:ТЕХНИКА - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans16", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 740, content);
        canvas.drawText("ВСКРЫТИЕ ПРОСВЕТА ТРАХЕИ:АНАТОМИЯ - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans17", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 770, content);
        canvas.drawText("ВСКРЫТИЕ ПРОСВЕТА ТРАХЕИ:ОРГАНИЗАЦИЯ - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans18", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 800, content);
        canvas.drawText("СОСУДИСТЫЙ ШОВ:ТЕХНИКА - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans19", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 830, content);
        canvas.drawText("СОСУДИСТЫЙ ШОВ:АНАТОМИЯ - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans20", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 860, content);
        canvas.drawText("СОСУДИСТЫЙ ШОВ:ОРГАНИЗАЦИЯ - " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE)
                .getInt("ans21", 0), requireActivity().getResources().getInteger(R.integer.pdf_margin_item), 890, content);


        // do final processing of the page
        document.finishPage(page);


        try {
            File f = new File(Environment.getExternalStorageDirectory().getPath() + "/MedSimTech_report_" + formatter.format(date) + ".pdf");
            FileOutputStream fos = new FileOutputStream(f);
            document.writeTo(fos);
            document.close();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException("Error generating file", e);
        }
    }
    

    void saveSys(int iParam) {
        requireActivity().getSharedPreferences("gr_sys", MODE_PRIVATE).edit().putInt("gr_sys", iParam).apply();

    }

    int loadMode() {
        SharedPreferences _sp = requireActivity().getSharedPreferences("gr_sys", MODE_PRIVATE);
        Log.i(TAG, "load " + _sp.getInt("gr_sys", 1));
        return _sp.getInt("gr_sys", 1);
    }

    public static int getCntItem() {
        return cntItem;
    }

    public static void setCntItem(int cntItem) {
        TabThree.cntItem = cntItem;
    }
}
