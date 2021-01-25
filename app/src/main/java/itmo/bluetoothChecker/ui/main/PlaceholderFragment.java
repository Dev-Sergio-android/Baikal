package itmo.bluetoothChecker.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import itmo.bluetoothChecker.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements View.OnClickListener{

    private static final String ARG_SECTION_NUMBER = "section_number";


    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PageViewModel pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 2;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {



        //final View root = inflater.inflate(R.layout.fragment_main, container, false);
        final View pump = inflater.inflate(R.layout.tab_check, container, false);
        //final TextView textView = root.findViewById(R.id.section_label);
        final Button button = pump.findViewById(R.id.buttonSend);
        /*pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/



//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(root.getContext(), "Включаем LED", Toast.LENGTH_SHORT).show();  //выводим на устройстве сообщение
//            }
//        });

        button.setOnClickListener(this);

        return pump;

    }

    private void errorExit(String title, String message){
        Toast.makeText(requireActivity().getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        requireActivity().finish();
    }




    @Override
    public void onClick(View v) {
        Log.d("bluetooth", "...Посылаем данные: ");
    }
}