package org.techtown.thread.scanthevoca_1st;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class VocaViewAdapter extends BaseAdapter {


    //Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<VocaViewItem> vocaViewItemList = new ArrayList<VocaViewItem>();

    //VocaViewAdapter 생성자
    public VocaViewAdapter(){}

    //Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return vocaViewItemList.size();
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return vocaViewItemList.get(position);
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }


    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "vocaview_item" Layout을 inflate하여 convertView 참조 획득.
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.vocaview_item,parent,false);
        }


        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView engTextView = (TextView) convertView.findViewById(R.id.textEng) ;
        TextView meanTextView = (TextView) convertView.findViewById(R.id.textMean) ;

        //Data Set(vocaviewItemList)에서 position에 위치한 데이터 참조 획득
        VocaViewItem vocaViewItem = vocaViewItemList.get(position);

        //아이템 내 각 위젯에 데이터 반영
        engTextView.setText(vocaViewItem.getWord());
        meanTextView.setText(vocaViewItem.getMean());


        return convertView;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String Word, String Mean) {
        VocaViewItem item = new VocaViewItem();

        item.setWord(Word);
        item.setMean(Mean);

        vocaViewItemList.add(item);
    }

}
