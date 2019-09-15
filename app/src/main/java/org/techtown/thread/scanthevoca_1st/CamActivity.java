package org.techtown.thread.scanthevoca_1st;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.pranavpandey.android.dynamic.utils.DynamicUnitUtils;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.opencv.imgproc.Imgproc.COLOR_RGBA2GRAY;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.cvtColor;


public class CamActivity extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener{

    private ImageButton imgbtn;
    private static final String TAG = "opencv";
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat matInput;
    private Mat temp_matInput;
    private Mat matResult;
    private Mat matInput1, matInput2;
    private Mat matMask = null, matNotMask = null;
    Rect rectROI = new Rect();
    private int step = 0;
    private int screen2 = 0;

    private ImageView imageView;
    private static final int CAMERA_PHOTO = 111;
    private Uri imageToUploadUri;
    private boolean button_not_clicked = true;


    //바코드 -> 넘코드
    String numCode;
    String barcode_data;
    private TextView textView;
    private int numCode_len = 0;
    private String[] barcode_templates = {"00110", "10001", "01001", "11000", "00101", "10100",
            "01100", "00011", "10010", "01010"};

    private TextView textResult;

    public CamActivity(){ }
    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mOpenCvCameraView.enableView();

                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        temp_matInput = null;



        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_cam);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {

                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }


        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        mOpenCvCameraView.setOnTouchListener(this);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setVisibility(View.GONE);
                textResult.setVisibility(View.GONE);
                button_not_clicked = !(button_not_clicked);
            }
        });

        textView = findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setVisibility(View.GONE);


        imgbtn = (ImageButton) findViewById(R.id.imageButton);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                numCode = "";
                barcode_data = "";

                //가는 선 = 0
                //굵은 선 = 1
                int thin_cnt = 8;
                int thick_cnt = 15;
                int cursor = 0;
                int zero_cnt = 0; // 연속되는 0의 개수로 선 굵기 판단;

                if(temp_matInput != null){
                    button_not_clicked = !(button_not_clicked);

                    Log.d("SampleCapture", "1 : " + temp_matInput.cols() + "\n2 : " + temp_matInput.rows());


                    Toast.makeText(getApplicationContext(), "Image captured with RECT", Toast.LENGTH_LONG).show();

                    Log.d("templete matching ", "1111");

                    try {
                        cvtColor(temp_matInput, temp_matInput, Imgproc.COLOR_BGR2GRAY);
                    }catch (CvException cve){
                        Toast.makeText(CamActivity.this, "Try again - CV Exception at line 202", Toast.LENGTH_SHORT).show();
                    }
                    GaussianBlur(temp_matInput, temp_matInput, new Size(5, 5), 0);
                    Imgproc.threshold(temp_matInput, temp_matInput, 127, 255, Imgproc.THRESH_BINARY+ Imgproc.THRESH_OTSU);

                    int height = temp_matInput.height();
                    int width = temp_matInput.width();
                    Log.d("height, width ", ""+height + ", " + width);
                    double[] pixel_arr = new double[height];


                    textResult = findViewById(R.id.textResult);


                    for(int y=0; y<height; y++){
                        int x = width/2;
                        Log.d("width/2 :", ""+x);
                        pixel_arr[y] = temp_matInput.get(y, x)[0];
                    }


                    String tempstr = "";
                    for(int y=0; y<height; y++){
                        tempstr = tempstr + pixel_arr[y];
                    }
                    Log.d("pixel_arr : ", ""+tempstr + "  ");

                    for (int y=0; y<height; y++) {
                        if(pixel_arr[y] > 0.01){
                            barcode_data = barcode_data + "1";
                        }else{
                            barcode_data = barcode_data + "0";
                        }
                    }

                    Log.d("바코드 데이터 :  ", ""+barcode_data);

                    int k = 0;
                    while(k < 200) {
                        if(cursor >= barcode_data.length()) break;

                        zero_cnt = 0;
                        Log.d("while ", k+"");
                        k++;

                        //공백 점프
                        for (int i = cursor; i < barcode_data.length(); i++) {
                            if (barcode_data.charAt(i) == '0') {
                                cursor = i;
                                Log.d("공백점프 커서: ", ""+cursor);
                                break;
                            }
                        }

                        //바코드 굵기에 따른 코드생성
                        for(int i = cursor; i < barcode_data.length(); i++){
                            if(barcode_data.charAt(i) == '0'){
                                zero_cnt++;
                            }else{
                                cursor = i;
                                break;
                            }
                        }
                        Log.d("zero_cnt : ", ""+zero_cnt);

                        if(zero_cnt < thin_cnt + 4){
                            numCode = numCode + "0";
                        }else{
                            numCode =  numCode + "1";
                        }



                    }
                    Log.d("numCode Before : ", ""+numCode);

                    String tmpStr = "";

                    try{
                        for(int s=0; s<numCode.length(); s++){
                            tmpStr = tmpStr + numCode.charAt(numCode.length()-s-1);
                        }
                        numCode = tmpStr;

                        tmpStr = "";
                        int idx = numCode.indexOf("00110");
                        Log.d("idx", ""+idx);

                        for(int s=idx; s<numCode.length(); s++){
                            tmpStr = tmpStr + numCode.charAt(s);
                        }
                    }catch (StringIndexOutOfBoundsException se){
                        Toast.makeText(CamActivity.this, "Try Agoin - StringOutOfBoundsException", Toast.LENGTH_SHORT).show();
                    }

                    numCode = tmpStr;


                    Log.d("numCode : ", ""+numCode);
                    Log.d("numCode Len: ", ""+numCode.length());
                    Toast.makeText(getApplicationContext(), "numCode : " + numCode, Toast.LENGTH_LONG).show();

                    numCode_len = numCode.length();


                }else{
                    Toast.makeText(getApplicationContext(), "Try again!", Toast.LENGTH_LONG).show();
                }

                Bitmap testBitmap = Bitmap.createBitmap(temp_matInput.cols(), temp_matInput.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(temp_matInput, testBitmap);

                imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(testBitmap);
                imageView.setVisibility(View.VISIBLE);


                int barcode_data = get_data_from_num_code();
                textResult = findViewById(R.id.textResult);
                textResult.setText("Barcode Data = ");

                //인식 제대로 이루어짐
                if(barcode_data != -1){
                    textResult.append(""+barcode_data);

                    Intent intent = new Intent();
                    intent.putExtra("barcode_num", ""+barcode_data);
                    setResult(RESULT_OK, intent);

                    Log.d("BARCODE", "아직 ?" + intent.getExtras().getString("barcode_num"));
                    finish();
                    Log.e("abcdefg", "I will be back");

                    // return;
                }else{ //인식 오류. 다시시도.
                    textResult.append("DATA ERROR");
                    Toast.makeText(CamActivity.this, "Try again. - No barcode data.", Toast.LENGTH_SHORT).show();
                }
                textResult.setVisibility(View.VISIBLE);
            }
        });



    }

    private int get_data_from_num_code(){
        String a = "";
        String b = "";
        String c = "";
        int num_a = 0;
        int num_b = 0;
        int num_c = 0;

        Log.d("numCode_len", numCode_len+"");


        if(numCode_len == 15){     //한 자리 수
            a = numCode.substring(5, 10);
            Log.d("a", a);
            for(int i=0 ;i<10; i++){
                if(a.contains(barcode_templates[i])) {
                    Log.d("template", barcode_templates[i]);
                    num_a = i;
                    return num_a;
                }
            }
        }else if(numCode_len == 20){ // 두 자리 수
            a = numCode.substring(5, 10);
            for(int i=0 ;i<10; i++){
                if(a.contains(barcode_templates[i])) {
                    num_a = i;
                    break;
                }
            }
            b = numCode.substring(10, 15);
            Log.d("b", b);
            for(int i=0 ;i<10; i++){
                if(b.contains(barcode_templates[i])) {
                    num_b = i;
                    return num_a*10 + num_b;
                }
            }
        }else if(numCode_len == 25) { //세 자리 수
            Log.d("c", c);
            a = numCode.substring(5, 10);
            for(int i=0 ;i<10; i++){
                if(a.contains(barcode_templates[i])) {
                    num_a = i;
                    break;
                }
            }
            b = numCode.substring(10, 15);
            for(int i=0 ;i<10; i++){
                if(b.contains(barcode_templates[i])) {
                    num_b = i;
                    break;
                }
            }
            c = numCode.substring(15, 20);
            for(int i=0 ;i<10; i++){
                if(c.contains(barcode_templates[i])) {
                    num_c = i;
                    return num_a*100 + num_b*10 + num_c;
                }
            }
        }
        Log.d("a, b, c", ""+num_a + ", " + num_b + ", " + num_c);

        return -1;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

        finish();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();



        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();


    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }


    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        matInput = inputFrame.rgba();


        //if ( matResult != null ) matResult.release(); fix 2018. 8. 18

        if (matResult == null)
            matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());

        if (matInput1 == null)
            matInput1 = new Mat(matInput.rows(), matInput.cols(), matInput.type());

        if (matInput2 == null)
            matInput2 = new Mat(matInput.rows(), matInput.cols(), matInput.type());

        if (matNotMask == null)
            matNotMask = new Mat(matInput.rows(), matInput.cols(), matInput.type());


        if (matMask != null) {
            Core.bitwise_and(matInput, matMask, matInput1);
            ConvertRGBtoGray(matInput1.getNativeObjAddr(), matInput1.getNativeObjAddr());

            if (matMask == null || matNotMask == null) {
                return matInput;
            }

            Core.bitwise_not(matMask, matNotMask);
            Core.bitwise_and(matInput, matNotMask, matInput2);
            Imgproc.cvtColor(matInput1, matInput1, Imgproc.COLOR_GRAY2RGBA);
            Core.bitwise_or(matInput1, matInput2, matResult);

            Mat threshold = new Mat();
            Imgproc.Canny(matResult, threshold, 80, 100, 3);
            Mat lines = new Mat();

            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 18));
            Mat tmp = new Mat();
            Mat out = new Mat();
            Imgproc.morphologyEx(matResult, tmp, Imgproc.MORPH_CLOSE, kernel);
            for (int i = 0; i < 8; i++) {
                Imgproc.erode(tmp, tmp, kernel);
            }

            Imgproc.dilate(tmp, tmp, kernel);
            Imgproc.dilate(tmp, tmp, kernel);
            Imgproc.dilate(tmp, tmp, kernel);
            Imgproc.dilate(tmp, tmp, kernel);
            Imgproc.dilate(tmp, tmp, kernel);

            Imgproc.dilate(tmp, out, kernel);


            //투명 부분에서만 바코드 인식하도록 검정색으로 영상 덮음 (background 에서만)
            Rect blockRect1 = new Rect(new Point(0, 0), new Point(matInput.width()/2 - 200, matInput.height()));
            tmp.submat(blockRect1).setTo(Scalar.all(0));
            out.submat(blockRect1).setTo(Scalar.all(0));

            Rect blockRect2 = new Rect(new Point(matInput.width()/2 + 200, 0), new Point(matInput.width(), matInput.height()));
            tmp.submat(blockRect2).setTo(Scalar.all(0));
            out.submat(blockRect2                                                                        ).setTo(Scalar.all(0));

            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.cvtColor(out, out, COLOR_RGBA2GRAY);

            //스캔할 영역... (나중에 지워. 영역 범위 알아볼라고 쓴거야) !!!!!!!!!!!!@@@@@@@@@@@@@@@@@@@@@@@@@@@
            // Imgproc.rectangle(matInput, new Point(matInput.width()/2-200, 0), new Point(matInput.width()/2+200, matInput.height()), new Scalar(255, 0, 0) , 5);
/*
            Rect blockRect1 = new Rect(new Point(0, 0), new Point(matInput.width()/2 - 200, matInput.height()));
            //Mat blockMat = matInput.submat(blockRect1);
            //blockMat.setTo(Scalar.all(0));

            Mat backupMatUpper = matInput.submat(blockRect1);
            matInput.submat(blockRect1).setTo(Scalar.all(0));
            */






            if (screen2 % 2 == 1) {
                return out;
            } else {
//
                Imgproc.findContours(out, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

                double maxArea = 0;
                MatOfPoint max_contour = new MatOfPoint();

                Iterator<MatOfPoint> iterator = contours.iterator();
                while (((Iterator) iterator).hasNext()) {
                    MatOfPoint contour_m = iterator.next();
                    double area = Imgproc.contourArea(contour_m);
                    if (area > maxArea) {
                        maxArea = area;
                        max_contour = contour_m;
                    }
                }
                Rect rect = Imgproc.boundingRect(max_contour);



                //Imgproc.rectangle(matInput, rect.tl(), rect.br(), new Scalar(0, 0, 255), 5);
                //Imgproc.rectangle(matInput, new Point(rect.tl().x - 30, rect.tl().y - 30), new Point(rect.br().x + 30, rect.br().y + 30), new Scalar(0, 0, 255), 5);
                Imgproc.rectangle(matInput, new Point(rect.tl().x - 60, rect.tl().y - 60), new Point(rect.br().x + 60, rect.br().y + 60), new Scalar(0, 0, 255), 5);
                Rect cutRect = new Rect((int)rect.tl().x - 50, (int)rect.tl().y - 50, rect.width + 120, rect.height+ 100);
                rectROI = cutRect;

                if(button_not_clicked) {
                    try {
                        temp_matInput = matInput.submat(cutRect);
                        //Imgproc.cvtColor(temp_matInput, temp_matInput, Imgproc.COLOR_RGB2GRAY);
                        //잠시 보류
                        //GaussianBlur(temp_matInput, temp_matInput, new Size(5, 5), 0);
                        //Imgproc.adaptiveThreshold(temp_matInput, temp_matInput, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 2);
                        //Imgproc.threshold(temp_matInput, temp_matInput, 127, 255, Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
                    }catch (CvException e){

                    }
                }
                return matInput;
                //max 구한다음 영역좀만 넓혀서 잡으면 관심영역추출 가능. (조건문으로 out of bounds 에러 안뜨게 해야함!! 주의)

            }


        }


        return matInput;
    }


    //여기서부턴 퍼미션 관련 메소드
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {"android.permission.CAMERA"};


    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions) {

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED) {
                //허가 안된 퍼미션 발견
                return false;
            }
        }

        //모든 퍼미션이 허가되었음
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted)
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(CamActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        step++;

        if (step % 2 == 1) {
            int width = matInput.cols();
            int height = matInput.rows();


            matMask = null;
            matMask = Mat.zeros(matInput.size(), matInput.type());
            matMask.setTo(Scalar.all(255));
        } else
            matMask = null;


        return false;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {


        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            Toast.makeText(getApplicationContext(), "rectROI.x, y : " + rectROI.x + ", " + rectROI.y
                    + "\nwidth, height : " + rectROI.width + ", " + rectROI.height, Toast.LENGTH_LONG).show();
        }
        return true;
    }

    public @NonNull
    static Bitmap createBitmapFromView(@NonNull View view, int width, int height) {
        if (width > 0 && height > 0) {
            view.measure(View.MeasureSpec.makeMeasureSpec(DynamicUnitUtils
                            .convertDpToPixels(width), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(DynamicUnitUtils
                            .convertDpToPixels(height), View.MeasureSpec.EXACTLY));
        }
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable background = view.getBackground();

        if (background != null) {
            background.draw(canvas);
        }
        view.draw(canvas);

        return bitmap;
    }

    private void templeteMatching(){

        Log.d("templete matching ", "1111");

        Toast.makeText(getApplicationContext(), "아직 살아있어222111111!@#!!", Toast.LENGTH_LONG).show();
//        //template
//        Mat templete = new Mat();
//        try {
//            templete = Utils.loadResource(this, R.drawable.b0);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        cvtColor(templete, templete, COLOR_BGR2GRAY);

        cvtColor(temp_matInput, temp_matInput, Imgproc.COLOR_BGR2GRAY);
        //GaussianBlur(temp_matInput, temp_matInput, new Size(5, 5), 0);
        Imgproc.threshold(temp_matInput, temp_matInput, 180, 255, Imgproc.THRESH_BINARY+ Imgproc.THRESH_OTSU);
        //Imgproc.adaptiveThreshold(temp_matInput, temp_matInput, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 3, 10);


        int height = temp_matInput.height();
        int width = temp_matInput.width();
        //double[][] pixel_arr = new double[height][width];

        double[] pixel_arr = new double[height];

        Toast.makeText(getApplicationContext(), "아직 살아있어111111!@#!!", Toast.LENGTH_LONG).show();

        //임시 주석
        /*
        for(int y=0; y<height; y++){
            for(int x = 0; x < width; x++){
                pixel_arr[y][x] = temp_matInput.get(y, x)[0];
            }
        }
        */

        //textView = findViewById(R.id.textView);





        //ROI(바코드이미지) 에서 중간 부분 픽셀들만 가져옴
        //처음엔 전체 다 가져왔었는데, 어차피 사용할 부분은 중간이므로 연산 덜하게 하기위해 이렇게 구현
        for(int y=0; y<height; y++){
            int x = width/2;
            pixel_arr[y] = temp_matInput.get(y, x)[0];
        }

        Toast.makeText(getApplicationContext(), "아직 살아있어!@#!!", Toast.LENGTH_LONG).show();

        //string 으로 만듦.  연속되는 0의 개수에 따라 :  8+-  가는선 /  15+- 굵은선
        for (int y=0; y<height; y++) {
            if(pixel_arr[y] > 0.2){
                barcode_data = barcode_data + "1";
            }else{
                barcode_data = barcode_data + "0";
            }
        }
        Toast.makeText(getApplicationContext(), "아직 살아있어!!", Toast.LENGTH_LONG).show();

        numCode = barcode_to_numcode(barcode_data);
        Toast.makeText(getApplicationContext(), "바코드 데이터 : " + numCode, Toast.LENGTH_LONG).show();
//
//        for (int y=0; y<height; y++) {
//            for (int x = 0; x < width; x++) {
//                textView.append(Integer.toString(int_pixel_arr[y][x]));
//            }
//            textView.append("\n");
//        }


//
//        final String contents_string = textView.getText().toString();
//        ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
//        clip.setText(contents_string);





        Bitmap testBitmap = Bitmap.createBitmap(temp_matInput.cols(), temp_matInput.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(temp_matInput, testBitmap);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(testBitmap);

//        //다시푸 ㄹ어!! 결과텍스트 확인용
//        textView.setVisibility(View.VISIBLE);


//        byte[][] out=new byte[frameSize][numChannels];
//        for (int p=0,i = 0; p < frameSize; p++) {
//            for (int n = 0; n < numChannels; n++,i++) {
//                out[p][n]=byteBuffer[i];
//            }
//        }

//        textView = findViewById(R.id.textView);
//        textView.append(Integer.toString(frameSize) + "\n");
//        textView.append(Integer.toString(numChannels));
//        for (int p=0,i = 0; p < frameSize; p++) {
//            for (int n = 0; n < numChannels; n++,i++) {
//                textView.append(Byte.toString(out[p][n]));
//            }
//            //textView.append("\n");
//        }



        //textView.setVisibility(View.VISIBLE);

    }

    private String barcode_to_numcode(String barcode_data) {

        //가는 선 = 0
        //굵은 선 = 1

        int thin_cnt = 8;
        int thick_cnt = 15;
        int cursor = 0;
        int zero_cnt = 0; // 연속되는 0의 개수로 선 굵기 판단;
        String numCode1 = "";

        int[] ret_arr = new int[2];

        //첫 번째 1을 찾는다 (바코드 들어가기 전 첫 번쨰 공백) (테두리 까지 인식될 경우 보완)
        for (int i = 0; i < barcode_data.length(); i++) {
            if (barcode_data.charAt(i) == 1) {
                cursor = i;
                break;
            }
        }

        while(cursor <= barcode_data.length()) {

            cursor = jump_to_next(barcode_data, cursor);
            ret_arr = detect_barcode_line(barcode_data, cursor, thin_cnt, thick_cnt);
            cursor = ret_arr[0];
            numCode1 = numCode1 + ret_arr[1];
        }

        return numCode1;
    }

    //공백 건너뛰기
    private int jump_to_next(String str, int cursor){
        for (int i = cursor; i < str.length(); i++) {
            if (str.charAt(i) == 0)
                cursor = i;
            break;
        }
        return cursor;
    }

    private int[] detect_barcode_line(String str, int cursor, int thin_cnt, int thick_cnt) {
        int zero_cnt = 0;
        int[] ret_arr = new int[2];

        for(int i = cursor; i < str.length(); i++){
            if(str.charAt(i) == 0){
                zero_cnt++;
            }else{
                cursor = i;
                break;
            }
        }

        if(zero_cnt > thin_cnt - 3 || zero_cnt < thin_cnt + 3){
            ret_arr[0] = cursor;
            ret_arr[1] = 0;     //thin line
        }else if(zero_cnt > thick_cnt - 3 || zero_cnt < thick_cnt + 3){
            ret_arr[0] = cursor;
            ret_arr[1] = 1;     //thin line
        }

        return ret_arr;

    }





}
