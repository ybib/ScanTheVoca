package org.techtown.thread.scanthevoca_1st;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class Menu4Fragment extends Fragment {

    Button btnReset;
    private MySQLiteOpenHelper mySQLiteOpenHelper;

    //empty public constructor
    public Menu4Fragment() {
    }

    @Nullable
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_menu4, container, false);
        btnReset = rootView.findViewById(R.id.btnReset);


        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mySQLiteOpenHelper = new MySQLiteOpenHelper(Menu4Fragment.this.getActivity());
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("초기화 하시겠습니까?")
                        .setPositiveButton(" 예 ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mySQLiteOpenHelper.reset();

                            }
                        })
                        .setNeutralButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }

                        })
                        .create()
                        .show();
            }
        });


        return rootView;
    }


}
