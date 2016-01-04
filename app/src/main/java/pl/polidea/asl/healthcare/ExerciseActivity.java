package pl.polidea.asl.healthcare;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;

import pl.polidea.asl.healthcare.dbhelper.ExerDBHelper;
import pl.polidea.asl.healthcare.dbhelper.MyDBHelper;

/**
 * Created by admin on 2015-12-29.
 */
public class ExerciseActivity extends ActionBarActivity implements TextWatcher {

    private AutoCompleteTextView inputexercise;
    private Button SearchingEx, step, addExer;
    private TextView PerConsume;
    private TextView exerName,kcalText;
    private SQLiteDatabase Exsqdb;
    private ExerDBHelper myExDBhelper;
    private static final String TAG = "SearchExercise";
    private String Keyword;
    private ImageView ShowPic;
    private Bitmap bitmap = null;
    private String ArrayList[] = {"자전거", "걷기", "달리기", "축구", "농구", "탁구", "테니스", "베드민턴", "수영", "줄넘기", "배구", "골프", "스쿼서", "야구", "암벽등반", "에어로빅", "훌라후프", "등산"};


    static MyDBHelper todayDBHelper;
    static SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);


        todayDBHelper = new MyDBHelper(this, "Today.db", null, 1);
        db = todayDBHelper.getWritableDatabase();

        Log.i(TAG, "onCreate");
        myExDBhelper = new ExerDBHelper(this);

        try {
            myExDBhelper.createDatabase();
        }
        catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            myExDBhelper.openDatabase();
        }
        catch(SQLException sqle){
            throw sqle;
        }

        ShowPic = (ImageView) findViewById(R.id.ShowPic);
        inputexercise = (AutoCompleteTextView) findViewById(R.id.ExerciseInput);
        SearchingEx = (Button) findViewById(R.id.SearchExercise);
        step = (Button) findViewById(R.id.stepAct);
        exerName = (TextView) findViewById(R.id.Per);
        PerConsume = (TextView) findViewById(R.id.ExerciseName);
        kcalText = (TextView) findViewById(R.id.Calorie);
        addExer = (Button) findViewById(R.id.btn_addexer);


        addExer.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view) {

                Date date = new Date();// 오늘에 날짜를 세팅 해준다.
                int year, mon, day;
                year = date.getYear() + 1900;
                mon = date.getMonth() + 1;
                day = date.getDate();

                if(exerName.getText().toString() != "") {

                    db.execSQL("INSERT INTO today VALUES(null, '"
                            + year + "/" + mon + "/" + day + "', '"
                            + "운동" + "', '"
                            + exerName.getText().toString() + "', '"
                            + kcalText.getText().toString().substring(0,kcalText.getText().toString().indexOf("Kcal")) + "');");
                    System.out.println("ADDED TASK");

                    System.out.println(year + "/" + mon + "/" + day);
                }
                else{
                    Toast.makeText(getApplicationContext(), "운동을 먼저 검색해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });


        inputexercise.addTextChangedListener(this);
        inputexercise.setAdapter(new ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                ArrayList));

        SearchingEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = null;
                Exsqdb = myExDBhelper.getWritableDatabase();
                Keyword = inputexercise.getText().toString();

                Cursor Cdb = Exsqdb.rawQuery("select * from ExerciseList"
                        + " where exercise = '" + Keyword + "';", null);

                if(Cdb.moveToFirst()) {
                    url = GetUrl(Cdb.getInt(0) - 1);

                    new ImageBitmap(ShowPic).execute(url);
                    ShowPic.setImageBitmap(bitmap);

                    PerConsume.setText(Cdb.getString(1));
                    kcalText.setText(Cdb.getString(2) + "Kcal");
                    exerName.setText(Cdb.getString(3));
                }
            }
        });

        step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StepwalkingActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String GetUrl(int order){
        String a[] = new String[18];

        a[0] = "http://cfile23.uf.tistory.com/image/1614DB4E4F2A57AE2FD823";
        a[1] = "http://cfile217.uf.daum.net/image/1718DC494D87CBD00137F8";
        a[2] = "https://eco-institute.knps.or.kr/upload/2012062010111670.JPG";
        a[3] = "http://cfile2.uf.tistory.com/image/155E5540508F7EDC2B1C64";
        a[4] = "http://cfile24.uf.tistory.com/image/1934C23F4E9627981D00D2";
        a[5] = "http://cfile3.uf.tistory.com/original/010FDE3351DEC58C320820";
        a[6] = "http://sp.yc.go.kr/program/data/publicboard/1035/%EC%97%90%EC%96%B4%EB%A1%9C%EB%B9%85%EC%8B%A4%20(1).JPG";
        a[7] = "http://cfile25.uf.tistory.com/image/1415241449B4CA05E119BA";
        a[8] = "http://www.bhgoo.com/2011/files/attach/images/12861/194/088/%ED%9B%8C%EB%9D%BC%ED%9B%84%ED%94%84%EB%8F%8C%EB%A6%AC%EA%B8%B0.jpg";
        a[9] = "http://www.rccl.kr/upload_img/20130522/201305220105051315730.gif";
        a[10] = "http://cfile24.uf.tistory.com/image/227E6646532D89E6141970";
        a[11] = "http://cfile10.uf.tistory.com/image/2446FF3B51B5EC881A0295";
        a[12] = "http://cfile3.uf.tistory.com/image/126D981849CA94750A46B0";
        a[13] = "http://cfile207.uf.daum.net/image/2443523750DA9F1F1784E3";
        a[14] = "http://www.golfdigest.co.kr/gd/files/attach/images/259/930/305b74aadcfe1bf6e0c3371202c5630a27e.jpg";
        a[15] = "https://attachment.namu.wiki/%EC%B6%95%EA%B5%AC__AP-DO-NOT-USE-Messi-Ronaldo.jpg";
        a[16] = "http://cfile22.uf.tistory.com/image/1347AF4C51101D852ED26C";
        a[17] = "http://www.happyseed.or.kr/board/Pds/Board/society1/Editor/28EL1950.jpg";


        return a[order];
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}