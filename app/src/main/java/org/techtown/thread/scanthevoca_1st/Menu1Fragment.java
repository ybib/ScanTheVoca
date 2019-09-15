package org.techtown.thread.scanthevoca_1st;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import java.util.List;

public class Menu1Fragment extends ListFragment {

    VocaViewAdapter adapter;

    private MySQLiteOpenHelper mySQLiteOpenHelper;


    @Nullable
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        //adapter 생성 및 adapter 지정
        adapter = new VocaViewAdapter();
        setListAdapter(adapter);

        mySQLiteOpenHelper = new MySQLiteOpenHelper(getActivity());
        List<Voca> vocas =  mySQLiteOpenHelper.getAllVocas();


        Log.e("fargment1","DB확인");
        Log.e("fargment1",""+ Integer.toString( vocas.size()));
        for(Voca i: vocas){
            adapter.addItem(i.getWord(),i.getMean());
        }

        return super.onCreateView(inflater,container,savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new VocaViewAdapter();
        setListAdapter(adapter);

        mySQLiteOpenHelper = new MySQLiteOpenHelper(getActivity());
        List<Voca> vocas =  mySQLiteOpenHelper.getAllVocas();

        for(Voca i: vocas){
            adapter.addItem(i.getWord(),i.getMean());
        }
    }
}
