package org.techtown.thread.scanthevoca_1st;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class MenuActivity extends AppCompatActivity {

    //FrameLayout에 각 메뉴의 Fragment를 바꿔줌
    private FragmentManager fragmentManager = getSupportFragmentManager();
    //메뉴에 들어갈 Fragment들
    private Menu1Fragment menu1Fragment = new Menu1Fragment();
    private Menu2Fragment menu2Fragment = new Menu2Fragment();
    private Menu3Fragment menu3Fragment = new Menu3Fragment();
    FragmentTransaction transaction;
    TabLayout tabLayoutseason;

    Intent camIntent;
    public static final int REQUEST_CODE_MENU = 101;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private static final int MY_PERMISSION_STORAGE = 1;



    Button btnCam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //give the app permission to access storage
        if(ContextCompat.checkSelfPermission(MenuActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MenuActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MenuActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_STORAGE);
            }else{
                ActivityCompat.requestPermissions(MenuActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_STORAGE);
            }
        } else {
            //do nothing
        }


        mySQLiteOpenHelper = new MySQLiteOpenHelper(MenuActivity.this);
        // 첫 화면 지정
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contents, menu1Fragment).commit();

        TabLayout tabLayoutcam = (TabLayout) findViewById(R.id.tab_layout) ;
        tabLayoutseason = (TabLayout) findViewById(R.id.tab_layout);





        tabLayoutcam.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition() ;
                changeView(pos) ;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        //camera intent
        camIntent = new Intent(MenuActivity.this, CamActivity.class);
        btnCam = findViewById(R.id.button1);
        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuActivity.this.startActivityForResult(camIntent, REQUEST_CODE_MENU);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_MENU) {
            if(resultCode == RESULT_OK){
                String barcode_num = "fail";
                barcode_num = data.getExtras().getString("barcode_num");

                Voca voca = new Voca();

                voca.setNum(barcode_num);

                Log.e("1234", ""+voca.getNum());


                Toast.makeText(this, "data : "+ barcode_num, Toast.LENGTH_SHORT).show();


                mySQLiteOpenHelper.addVoca(voca);
                mySQLiteOpenHelper.onememo(voca);

            }
        }
    }


    private void changeView(int index) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch (index) {
            case 0 :
                transaction.replace(R.id.contents, menu1Fragment).commitAllowingStateLoss();
                //tabLayoutseason.setVisibility(View.VISIBLE);
                btnCam.setVisibility(View.VISIBLE);
                break ;
            case 1 :
                transaction.replace(R.id.contents, menu2Fragment).commitAllowingStateLoss();
                //tabLayoutseason.setVisibility(View.VISIBLE);
                btnCam.setVisibility(View.INVISIBLE);
                break ;

            case 2:
                transaction.replace(R.id.contents, menu3Fragment).commitAllowingStateLoss();
                //tabLayoutseason.setVisibility(View.VISIBLE);
                btnCam.setVisibility(View.INVISIBLE);
                break ;


            case 5 :
                break ;
        }
    }


}
