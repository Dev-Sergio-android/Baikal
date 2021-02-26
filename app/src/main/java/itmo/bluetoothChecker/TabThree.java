package itmo.bluetoothChecker;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class TabThree extends Fragment {

    private final String TAG = "slide";

    private final String SYS_GRADE = "MedSimTech_sys_grade";
    private final String CONNECTION = "MedSimTech_bl_connection";

    Button btnPrev, btnNext;
    ImageView slide;
    TextView slideName;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_slide, container, false);

        btnPrev = view.findViewById(R.id.btn_prev);
        btnNext = view.findViewById(R.id.btn_next);

        slide = view.findViewById(R.id.slide);
        slideName = view.findViewById(R.id.slide_name);


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

}
