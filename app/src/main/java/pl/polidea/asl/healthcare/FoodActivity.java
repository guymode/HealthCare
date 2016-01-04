package pl.polidea.asl.healthcare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;

import pl.polidea.asl.healthcare.dbhelper.FoodDBHelper;
import pl.polidea.asl.healthcare.dbhelper.MyDBHelper;

/**
 * Created by admin on 2015-12-29.
 */
public class FoodActivity extends ActionBarActivity implements View.OnClickListener, TextWatcher, AdapterView.OnItemClickListener {

    private AutoCompleteTextView FoodText;
    private TextView textView, textView2, textView3, textView4, textView5,
            textView6, textView7, textView8, textView9, textView10, textView11;

    private TextView
            FoodName,OneMeal,kcalText,carbo,protein,fat,saccharide,natrium,choles,saturated,transfat;
    private Button Searching, sending, mic;
    private String Keyword;
    private FoodDBHelper myDbHelper;
    private SQLiteDatabase sqdb;


    static MyDBHelper todayDBHelper;
    static SQLiteDatabase db;

    private java.util.ArrayList<String> mResult;
    private String mSelectedString;
    private String sendKcal = "";
    private String ArrayList[] = {"콩나물해장국", "김치찌개", "감자탕", "소머리국밥", "김밥", "참치김밥", "치즈김밥", "샐러드김밥", "소고기김밥", "충무김밥", "감자볶음", "감자찌개", "김치볶음밥"};

    private static final String TAG = "SearchFood";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        Log.i(TAG, "onCreate");
        myDbHelper = new FoodDBHelper(this);

        todayDBHelper = new MyDBHelper(this, "Today.db", null, 1);
        db = todayDBHelper.getWritableDatabase();
        try {
            myDbHelper.createDatabase();
        }
        catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            myDbHelper.openDatabase();
        }
        catch(SQLException sqle){
            throw sqle;
        }

        findViewById(R.id.foodMic).setOnClickListener(this);
        findViewById(R.id.btn_searchfood).setOnClickListener(this);
        //findViewById(R.id.btn_addfood).setOnClickListener(this);

        FoodText = (AutoCompleteTextView) findViewById(R.id.FoodInput);
        /*
        FoodName = (TextView) findViewById(R.id.FoodName);
        OneMeal = (TextView) findViewById(R.id.OneMeal);
        kcalText = (TextView) findViewById(R.id.KcalText);
        carbo = (TextView) findViewById(R.id.carbo);
        protein = (TextView) findViewById(R.id.protein);
        fat = (TextView) findViewById(R.id.fat);
        saccharide = (TextView) findViewById(R.id.saccharide);
        natrium = (TextView) findViewById(R.id.natrium);
        choles = (TextView) findViewById(R.id.choles);
        saturated = (TextView) findViewById(R.id.saturated);
        transfat = (TextView) findViewById(R.id.transfat);*/

        FoodText.addTextChangedListener(this);
        FoodText.setAdapter(new ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                ArrayList));


    }

    @Override
    public void onClick(View v) {
        int view = v.getId();

        if (view == R.id.btn_searchfood){
            sqdb = myDbHelper.getWritableDatabase();
            Keyword = FoodText.getText().toString();

            Cursor c = sqdb.rawQuery("select * from FoodList" + " where col_3 = '"+ Keyword +"';", null);
            String[] from = new String[]{"name", "kcal"};
            c.moveToFirst();
            int[]to = new int[]{ R.id.tv_name, R.id.tv_kcal};
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.activity_cell, c,
                    from,
                    to);
            ListView foodList = (ListView) findViewById(R.id.lv_d_foodlist);
            foodList.setAdapter(adapter);


            /*
            Cursor Cdb = sqdb.rawQuery("select * from FoodList"
                    + " where col_3 = '"+ Keyword +"';", null);

            if(Cdb.moveToFirst()){
                sendKcal = Cdb.getString(4);
                //Cdb.getString(2);
                //음식명, 한끼 식사량, Kcal, 탄수화물, 단백질, 지방, 당류, 나트륨, 콜레스테롤, 포화지방, 트랜스지방


                FoodName.setText(Cdb.getString(2));
                OneMeal.setText(Cdb.getString(3));
                kcalText.setText(Cdb.getString(4));
                carbo.setText(Cdb.getString(5));
                protein.setText(Cdb.getString(6));
                fat.setText(Cdb.getString(7));
                saccharide.setText(Cdb.getString(8));
                natrium.setText(Cdb.getString(9));
                choles.setText(Cdb.getString(10));
                saturated.setText(Cdb.getString(11));
                transfat.setText(Cdb.getString(12));

            }
        */

        }

        if (view == R.id.foodMic) {        //구글 음성인식 앱 사용이면


            Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);            //intent 생성
            i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());    //음성인식을 호출한 패키지
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");                            //음성인식 언어 설정
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "말을 하세요.");                        //사용자에게 보여 줄 글자

            startActivityForResult(i, 1000);                                                //구글 음성인식 실행

        }
/*
        if (view == R.id.btn_addfood){
            Date date = new Date();// 오늘에 날짜를 세팅 해준다.
            int year, mon, day;
            year = date.getYear() + 1900;
            mon = date.getMonth() + 1;
            day = date.getDate();

            if(FoodName.getText().toString() != "") {

                db.execSQL("INSERT INTO today VALUES(null, '"
                        + year + "/" + mon + "/" + day + "', '"
                        + "음식" + "', '"
                        + FoodName.getText().toString() + "', '"
                        + kcalText.getText().toString() + "');");
                System.out.println("ADDED TASK");

                System.out.println(year + "/" + mon + "/" + day);
            }
            else{
                Toast.makeText(getApplicationContext(),"음식을 먼저 검색해주세요",Toast.LENGTH_SHORT).show();
            }

        }*/
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long arg3) {
        //날짜를 클릭한경우
        switch (parent.getId()) {
            //리스트를 클릭한경우
            //이미 있는 일정을 수정하거나 삭제
            case R.id.lv_task:
//                Toast.makeText(getApplicationContext(), "list_Clicked", Toast.LENGTH_SHORT).show();
                // TODO Auto-generated method stub
                //커서 객체를 통해 일정의 타이틀을 가져옴
                Cursor c = (Cursor) parent.getItemAtPosition(pos);
//                Toast.makeText(getApplicationContext(),c.getString(1),Toast.LENGTH_SHORT).show();
                System.out.println("getString(0), (1)-> "+ c.getString(0)+c.getString(1));
                System.out.println("getString(2), (3) ->" + c.getString(2) + c.getString(3));
                //lv_Task버튼 누를시 Param에 2전달
                //intent.putExtra("Param", 2);
                break;
        }

    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK && requestCode == 1000) {        //결과가 있으면
            showSelectDialog(requestCode, data);                //결과를 다이얼로그로 출력.
        } else {                                                            //결과가 없으면 에러 메시지 출력
            String msg = null;
            //내가 만든 activity에서 넘어오는 오류 코드를 분류
            switch (resultCode) {
                case SpeechRecognizer.ERROR_AUDIO:
                    msg = "오디오 입력 중 오류가 발생했습니다.";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    msg = "단말에서 오류가 발생했습니다.";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    msg = "권한이 없습니다.";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    msg = "네트워크 오류가 발생했습니다.";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    msg = "일치하는 항목이 없습니다.";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    msg = "음성인식 서비스가 과부하 되었습니다.";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    msg = "서버에서 오류가 발생했습니다.";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    msg = "입력이 없습니다.";
                    break;
            }

            if (msg != null)        //오류 메시지가 null이 아니면 메시지 출력
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
    private void showSelectDialog ( int requestCode, Intent data){
        String key = "";
        if (requestCode == 1000)                    //구글음성인식이면
            key = RecognizerIntent.EXTRA_RESULTS;    //키값 설정

        mResult = data.getStringArrayListExtra(key);        //인식된 데이터 list 받아옴.
        String[] result = new String[mResult.size()];            //배열생성. 다이얼로그에서 출력하기 위해
        mResult.toArray(result);                                    //	list 배열로 변환


        //1개 선택하는 다이얼로그 생성
        AlertDialog ad = new AlertDialog.Builder(this).setTitle("선택하세요.")
                .setSingleChoiceItems(result, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedString = mResult.get(which);        //선택하면 해당 글자 저장
                    }
                })
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FoodText.setText(mSelectedString + "");        //확인 버튼 누르면 결과 출력
                        //addNewMessage(new Message(mSelectedString,true));
                        //sendMessage();
                        //FoodText.setText();
                        // new SendMessage().execute();

                        sqdb = myDbHelper.getWritableDatabase();
                        Keyword = FoodText.getText().toString();

                        Cursor Cdb = sqdb.rawQuery("select * from FoodList"
                                + " where col_3 = '"+ Keyword +"';", null);

                        Cdb.moveToFirst();
                        sendKcal = Cdb.getString(4);
                        //Cdb.getString(2);
                        //음식명, 한끼 식사량, Kcal, 탄수화물, 단백질, 지방, 당류, 나트륨, 콜레스테롤, 포화지방, 트랜스지방



                        FoodName.setText(Cdb.getString(2));
                        OneMeal.setText(Cdb.getString(3));
                        kcalText.setText(Cdb.getString(4));
                        carbo.setText(Cdb.getString(5));
                        protein.setText(Cdb.getString(6));
                        fat.setText(Cdb.getString(7));
                        saccharide.setText(Cdb.getString(8));
                        natrium.setText(Cdb.getString(9));
                        choles.setText(Cdb.getString(10));
                        saturated.setText(Cdb.getString(11));
                        transfat.setText(Cdb.getString(12));

                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FoodText.setText("");        //취소버튼 누르면 초기화
                        //mSelectedString = null;
                    }
                }).create();
        ad.show();

        //text = mSelectedString;

        //msg.addNewMessage(new Message(text,true));

        //Intent i = new Intent(this, MessageActivity.class);
        //i.putExtra("인식",mSelectedString);
        //startActivity(i);
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
