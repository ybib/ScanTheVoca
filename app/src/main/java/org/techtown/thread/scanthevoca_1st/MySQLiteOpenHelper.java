package org.techtown.thread.scanthevoca_1st;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    static String NAME = "voca.sqlite";
    static SQLiteDatabase.CursorFactory FACTORY = null;
    static String PACKEGE = "org.techtown.thread.scanthevoca_1st";  //package이름에 맞도록 수정!!!!
    static String DB = "voca.db";
    static int VERSION = 1;



    public MySQLiteOpenHelper(Context context){
        super(context,DB,FACTORY,VERSION);

        try {
            boolean bResult = isCheckDB(context);
            Log.i("MiniApp", "DB Check=" + bResult);
            if (!bResult) {
                copyDB(context);
            } else {
            }

        } catch (Exception e) {
        }
    }


    //경로에 DB가 존재하는지 확인.
    public boolean isCheckDB (Context mContext){
        String filePath = "/data/data/" + PACKEGE + "/databases/" + DB;
        File file = new File(filePath);

        if (file.exists()) {
            return true;
        }
        return false;
    }

    //경로에 존재하는 DB를 핸드폰으로 복사.
    public void copyDB (Context mContext){
        Log.d("MiniApp", "copyDB");
        AssetManager manager = mContext.getAssets();
        String folderPath = "/data/data/" + PACKEGE + "/databases";
        String filePath = "/data/data/" + PACKEGE + "/databases/" + DB;
        File folder = new File(folderPath);
        File file = new File(filePath);

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            InputStream is = manager.open("db/" + DB);
            BufferedInputStream bis = new BufferedInputStream(is);
            if (folder.exists()) {
            } else {
                folder.mkdirs();
            }

            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }

            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            int read = -1;

            byte[] buffer = new byte[1024];
            while ((read = bis.read(buffer, 0, 1024)) != -1) {
                bos.write(buffer, 0, read);
            }

            bos.flush();
            bos.close();
            fos.close();
            bis.close();
            is.close();

        } catch (IOException e) {
            Log.e("ErrorMessage : ", e.getMessage());
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public List<Voca> getAllVocas() {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT _ID,WORD,MEAN,MEMO FROM my_voca");
        sb.append(" WHERE MEMO=1");

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sb.toString(), null);

        List<Voca> vocas = new ArrayList<Voca>();

        Voca voca = null;

        while (cursor.moveToNext()) {
            voca = new Voca();
            voca.set_id(cursor.getInt(0));
            voca.setWord(cursor.getString(1));
            voca.setMean(cursor.getString(2));
            voca.setMemo(cursor.getString(3));

            vocas.add(voca);
        }
        return vocas;
    }



    public void addVoca(Voca voca){
        SQLiteDatabase db = getWritableDatabase();

        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO my_voca ");
        sb.append("SELECT * FROM vocas ");
        sb.append("WHERE _ID = #NUM# AND NOT EXISTS (SELECT _ID FROM my_voca WHERE _ID=#NUM#)");

        String query = sb.toString();
        query = query.replace("#NUM#", voca.getNum());
        db.execSQL(query);
    }

    public int getMemCnt(){
        SQLiteDatabase db = getReadableDatabase();

        Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM my_voca WHERE MEMO=0",null);
        mCount.moveToFirst();
        int count = mCount.getCount();
        count = mCount.getInt(0);
        mCount.close();

        return count;
    }

    public int getUnmemCnt(){
        SQLiteDatabase db = getReadableDatabase();

        Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM my_voca WHERE MEMO=1",null);
        mCount.moveToFirst();
        int count = mCount.getCount();
        count = mCount.getInt(0);
        mCount.close();

        return count;
    }

    public void zeromemo(Voca voca){

        SQLiteDatabase db = getWritableDatabase();

        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE my_voca ");
        sb.append("SET MEMO=0 ");
        sb.append("WHERE WORD='#WORD#'");

        String query = sb.toString();
        query = query.replace("#WORD#",  voca.getWord() );
        db.execSQL(query);
    }

    public void onememo(Voca voca){

        SQLiteDatabase db = getWritableDatabase();

        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE my_voca ");
        sb.append("SET MEMO=1 ");
        sb.append("WHERE _ID=#NUM# AND MEMO = 0");

        String query = sb.toString();
        query = query.replace("#NUM#",  voca.getNum() );
        db.execSQL(query);
    }

    public void reset(){

        SQLiteDatabase db = getWritableDatabase();

        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM my_voca ");


        String query = sb.toString();
        db.execSQL(query);
    }



}
