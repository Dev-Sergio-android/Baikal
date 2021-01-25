package itmo.bluetoothChecker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import itmo.bluetoothChecker.gradeFragment.fragmentKroot;
import itmo.bluetoothChecker.gradeFragment.fragmentKrootStart;
import itmo.bluetoothChecker.gradeFragment.fragmentOske;
import itmo.bluetoothChecker.gradeFragment.fragmentOskeStart;

import static android.content.Context.MODE_PRIVATE;
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

    private static int cntItem = 0;

    private int pos;

    Spinner spinner;
    Button btnNext, btnPrev;
    ImageButton btnDel;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        try {
            switch (loadMode()) {
                case 1:
                    cntItem = 0;
                    requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().clear().apply();
                    transaction.replace(R.id.parent_fragment, new fragmentOskeStart()).commit();
                    break;

                case 2:
                    cntItem = 0;
                    break;

                default:
                    cntItem = 0;
                    requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().clear().apply();
                    transaction.replace(R.id.parent_fragment, new fragmentKrootStart()).commit();
                    break;
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
                    requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().clear().apply();
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.replace(R.id.parent_fragment, new fragmentOskeStart()).commit();
                } else if (i == 2) {
                    cntItem = 0;
                    /*btnPrev.setVisibility(View.INVISIBLE);
                    requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().clear().apply();
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.replace(R.id.parent_fragment, new fragmentKrootStart()).commit();*/
                    btnPrev.setVisibility(View.INVISIBLE);

                } else {
                    cntItem = 0;
                    btnPrev.setVisibility(View.INVISIBLE);
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
                                if (cntItem == 0) {
                                    if (OskeStartChecked()) {
                                        SharedPreferences.Editor ed = requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit();
                                        ed.putString("date", getmDate());
                                        ed.putString("num", getmStNumber()).apply();
                                        btnNext.setText("СЛЕД.");
                                        Fragment fragment = new fragmentOske();
                                        FragmentTransaction transaction1 = getChildFragmentManager().beginTransaction();
                                        transaction1.replace(R.id.parent_fragment, fragment).commit();
                                        btnPrev.setVisibility(View.VISIBLE);
                                        btnDel.setVisibility(View.INVISIBLE);
                                    } else {
                                        Toast.makeText(requireActivity(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                                    }
                                } else if (cntItem > 0 && cntItem < (getResources().getTextArray(R.array.oske_items).length)) {
                                    if (rgOskeChecked()) {
                                        StringBuilder sb = new StringBuilder(20);

                                        if (cntItem == getResources().getTextArray(R.array.oske_items).length) {
                                            saveAns(cntItem, getCheckedIndex());
                                            cntItem = 0;

                                            setGradeTitle(Html.fromHtml(sb.append("<b>№").append(cntItem).append("  </b>")
                                                    .append(getResources().getTextArray(R.array.oske_items)[cntItem]).toString()));

                                            sb.delete(0, sb.length());
                                            btnPrev.setVisibility(View.INVISIBLE);
                                            btnDel.setVisibility(View.VISIBLE);
                                            btnNext.setText(getResources().getString(R.string.button_next));
                                            requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).edit().clear().apply();
                                            rgOskeChecked();
                                        } else {

                                            btnPrev.setVisibility(View.VISIBLE);
                                            btnDel.setVisibility(View.INVISIBLE);

                                            saveAns(cntItem, fragmentOske.getCheckedIndex());

                                            Log.e(TAG, "answer " + cntItem + ":   " + fragmentOske.getCheckedIndex());

                                            if (cntItem == getResources().getTextArray(R.array.oske_items).length - 1) {
                                                btnNext.setText("Завершить");
                                            } else {
                                                btnNext.setText("СЛЕД.");
                                            }

                                            String ans = fragmentOske.getCheckedIndex() == 0 ? "yes" : "no";

                                            setGradeTitle(Html.fromHtml(sb.append("<b>№").append(cntItem + 1).append("  </b>")
                                                    .append(getResources().getTextArray(R.array.oske_items)[cntItem]).toString()));

                                            sb.delete(0, sb.length());


                                            Toast.makeText(requireActivity(),
                                                    "question " + cntItem + ": " + ans,
                                                    Toast.LENGTH_SHORT).show();

                                            cntItem += 1;

                                            fragmentOske.rgOskeClear();

                                            String name = "ans" + (cntItem);
                                            //Toast.makeText(requireContext(), " -- SharedPref " + name + " is " + requireActivity().getSharedPreferences("GradeAnswer", MODE_PRIVATE).getInt("ans" + (cntItem), -1), Toast.LENGTH_SHORT).show();
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
                                break;

                            case 2:
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

                                        saveAns(cntItem, fragmentKroot.getCheckedIndex());

                                        Log.e(TAG, "answer " + cntItem + ":   " + fragmentKroot.getCheckedIndex());

                                        if (cntItem == (getResources().getTextArray(R.array.kroot_items).length / 3) - 1) {
                                            btnNext.setText("Завершить");
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
                                } else if (cntItem > 1 && cntItem <= (getResources().getTextArray(R.array.oske_items).length)) {

                                    cntItem -= 1;

                                    if (cntItem == getResources().getTextArray(R.array.oske_items).length - 1) {
                                        btnNext.setText(getResources().getString(R.string.button_next));
                                    }

                                    /*Toast.makeText(requireActivity(),
                                            "cntItem --> " + cntItem,
                                            Toast.LENGTH_SHORT).show();*/

                                    setGradeTitle(Html.fromHtml(sb.append("<b>№").append(cntItem).append("  </b>")
                                            .append(getResources().getTextArray(R.array.oske_items)[cntItem - 1]).toString()));

                                    sb.delete(0, sb.length());

                                    rgOskeClear();

                                    loadAns(cntItem);

                                } else {
                                    Toast.makeText(requireActivity(),
                                            "Недоступно во время операции",
                                            Toast.LENGTH_SHORT).show();
                                }


                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                                break;

                            case 2:
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
                        .setMessage("Введенные данные будут стерты.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Fragment fragment = new Fragment();
                                        switch (spinner.getSelectedItemPosition()) {
                                            case 1:
                                                fragment = new fragmentOskeStart();
                                                break;
                                            case 2:
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


    void saveSys(int iParam) {
        SharedPreferences spSys = requireActivity().getSharedPreferences("gr_sys", MODE_PRIVATE);
        SharedPreferences.Editor ed = spSys.edit();
        ed.putInt("gr_sys", iParam);
        ed.apply();
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
