package itmo.bluetoothCheckerStudent;

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

    int cnt = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_slide, container, false);

        btnPrev = view.findViewById(R.id.btn_prev);
        btnNext = view.findViewById(R.id.btn_next);

        slide = view.findViewById(R.id.slide);
        slideName = view.findViewById(R.id.slide_name);

        btnPrev.setVisibility(View.INVISIBLE);

        slideName.setText(getResources().getStringArray(R.array.stage)[cnt]);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               switch (cnt){

                   case 1:
                       cnt += 1;
                       slideName.setText(getResources().getStringArray(R.array.stage)[cnt]);
                       slide.setImageResource(R.drawable.b3);
                       break;

                   case 2:
                       cnt += 1;
                       slideName.setText(getResources().getStringArray(R.array.stage)[cnt]);
                       slide.setImageResource(R.drawable.b4);
                       break;

                   case 3:
                       cnt += 1;
                       slideName.setText(getResources().getStringArray(R.array.stage)[cnt]);
                       slide.setImageResource(R.drawable.b5);
                       break;

                   case 4:
                       cnt += 1;
                       slideName.setText(getResources().getStringArray(R.array.stage)[cnt]);
                       slide.setImageResource(R.drawable.b6);
                       break;

                   case 5:
                       cnt += 1;
                       btnNext.setVisibility(View.INVISIBLE);
                       slideName.setText(getResources().getStringArray(R.array.stage)[cnt]);
                       slide.setImageResource(R.drawable.b7);
                       break;

                   default:
                       cnt += 1;
                       slideName.setText(getResources().getStringArray(R.array.stage)[cnt]);
                       btnPrev.setVisibility(View.VISIBLE);
                       slide.setImageResource(R.drawable.b2);
                       break;

               }

            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (cnt){

                    case 1:
                        cnt -= 1;
                        slideName.setText(getResources().getStringArray(R.array.stage)[cnt]);
                        btnPrev.setVisibility(View.INVISIBLE);
                        slide.setImageResource(R.drawable.b1);
                        break;

                    case 2:
                        cnt -= 1;
                        slideName.setText(getResources().getStringArray(R.array.stage)[cnt]);
                        slide.setImageResource(R.drawable.b2);

                        break;

                    case 3:
                        cnt -= 1;
                        slideName.setText(getResources().getStringArray(R.array.stage)[cnt]);
                        slide.setImageResource(R.drawable.b3);

                        break;

                    case 4:
                        cnt -= 1;
                        slideName.setText(getResources().getStringArray(R.array.stage)[cnt]);
                        slide.setImageResource(R.drawable.b4);

                        break;

                    case 5:
                        cnt -= 1;
                        slideName.setText(getResources().getStringArray(R.array.stage)[cnt]);
                        slide.setImageResource(R.drawable.b5);

                        break;

                    case 6:
                        cnt -= 1;
                        slideName.setText(getResources().getStringArray(R.array.stage)[cnt]);
                        btnNext.setVisibility(View.VISIBLE);
                        slide.setImageResource(R.drawable.b6);

                        break;

                    default:

                        break;

                }

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

}
