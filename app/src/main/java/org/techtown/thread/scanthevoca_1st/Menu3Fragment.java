package org.techtown.thread.scanthevoca_1st;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Menu3Fragment extends Fragment {

    PieChart pieChart;

    private MySQLiteOpenHelper mySQLiteOpenHelper;
    int memCnt;
    int unmemCnt;

    //empty public constructor
    public Menu3Fragment(){

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_menu3,container,false);

        mySQLiteOpenHelper = new MySQLiteOpenHelper(Menu3Fragment.this.getActivity());


        pieChart = rootView.findViewById(R.id.piechart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);  //modify location of pie chart

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();


        memCnt = mySQLiteOpenHelper.getMemCnt();
        unmemCnt =  mySQLiteOpenHelper.getUnmemCnt();
        if (memCnt==0 && unmemCnt ==0){
            pieChart.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(),"비어있음.", Toast.LENGTH_SHORT).show();
        }

        yValues.add(new PieEntry(memCnt,"암기"));
        yValues.add(new PieEntry(unmemCnt,"미암기"));

        Log.e("3Fragment", Integer.toString(memCnt));
        Log.e("3Fragment", Integer.toString(unmemCnt));

        Description description = new Description();
        description.setText("Current Status"); //라벨
        description.setTextSize(15);
        pieChart.setDescription(description);

        PieDataSet dataSet = new PieDataSet(yValues,"진행상황");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);
        pieChart.setData(data);

        pieChart.animateX(1000);
        pieChart.animateY(1000);
        pieChart.animateXY(1000, 1000);


        return rootView;
    }

}
