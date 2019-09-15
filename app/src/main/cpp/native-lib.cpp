#include <jni.h>
#include <opencv2/opencv.hpp>

using namespace cv;

extern "C"
JNIEXPORT void JNICALL
Java_org_techtown_thread_scanthevoca_11st_CamActivity_ConvertRGBtoGray(JNIEnv *env,
                                                       jobject instance,
                                                       jlong matAddrInput,
                                                       jlong matAddrResult) {

    // 입력 RGBA 이미지를 GRAY 이미지로 변환

    Mat &matInput = *(Mat *)matAddrInput;
    Mat &matResult = *(Mat *)matAddrResult;
    Mat edge, gray;

    cvtColor(matInput, matResult, COLOR_RGBA2GRAY);
    blur( matResult, matResult, Size(3,3));
    //threshold(matResult, matResult, 127, 255, THRESH_BINARY);
    int threshold = 50;
    int ratio = 4;
    Canny( matResult, matResult, 127, 255, 3);

}