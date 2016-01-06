package pl.polidea.asl.healthcare;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;

import pl.polidea.asl.healthcare.dbhelper.MyDBHelper;

/**
 * Created by admin on 2015-12-29.
 */
public class InformationActivity  extends Activity {

    int carboProgress;
    int proProgress;
    int fatProgress;
    int year, mon, day;
    Date date;
    static MyDBHelper todayDBHelper;
    static SQLiteDatabase db;
    String today;
    float totalkcal = 0;
    float exerkcal = 0;
    float foodkcal = 0;
    float remainkcal = 0;
    float sumCarbo=0;    float sumProtein=0;  float sumFat=0;
    int maxCarbo = 1000, maxProtein = 1000, maxFat = 1000;
    SharedPreferences information;
    TextView tv_total, tv_food, tv_exer, tv_remain;
    TextView tv_carbo, tv_protein, tv_fat;
    ProgressBar carboProgressBar, proProgressBar, fatProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        information = PreferenceManager.getDefaultSharedPreferences(this);
        todayDBHelper = new MyDBHelper(this, "Today.db", null, 1);
        db = todayDBHelper.getWritableDatabase();

        tv_total = (TextView) findViewById(R.id.tv_totalkcal);
        tv_food = (TextView) findViewById(R.id.tv_foodkcal);
        tv_exer = (TextView) findViewById(R.id.tv_exerkcal);
        tv_remain = (TextView) findViewById(R.id.tv_remainskcal);

        tv_carbo = (TextView) findViewById(R.id.tv_infor_carbo);
        tv_protein = (TextView) findViewById(R.id.tv_infor_protein);
        tv_fat = (TextView) findViewById(R.id.tv_infor_fat);

        carboProgressBar = (ProgressBar) findViewById(R.id.pb_carbo);
        proProgressBar = (ProgressBar) findViewById(R.id.pb_protein);
        fatProgressBar = (ProgressBar) findViewById(R.id.pb_fat);

        date = new Date();// 오늘에 날짜를 세팅 해준다.

        year = date.getYear() + 1900;
        mon = date.getMonth() + 1;
        day = date.getDate();
        today = year+"/"+mon+"/"+day;

        Cursor c = db.rawQuery("SELECT * FROM today WHERE date = '" + today + "' and what = '운동'", null);
        while (c.moveToNext()){
            System.out.println(c.getString(4));
            if(c.getString(4) == "")    continue;
            exerkcal += Integer.parseInt(c.getString(4));
        }

        c = db.rawQuery("SELECT * FROM today WHERE date = '" + today + "' and what = '음식'", null);
        while (c.moveToNext()){
            System.out.println(c.getString(4));
            if(c.getString(4) == "")    continue;
            foodkcal += Float.parseFloat(c.getString(4));
            sumCarbo += Float.parseFloat(c.getString(5));
            sumProtein += Float.parseFloat(c.getString(6));
            sumFat += Float.parseFloat(c.getString(7));

        }


        totalkcal = information.getInt("storedTotal",0);
        System.out.println("StoredTotal ->" + totalkcal);
        tv_total.setText(totalkcal+"");
        tv_exer.setText(exerkcal+"");
        tv_food.setText(foodkcal+"");
        remainkcal = totalkcal-foodkcal+exerkcal;
        tv_remain.setText(remainkcal+"");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TAG","onResum: Called");

        date = new Date();// 오늘에 날짜를 세팅 해준다.
        foodkcal = 0;
        exerkcal = 0;
        sumFat = 0;
        sumProtein = 0;
        sumCarbo = 0;

        year = date.getYear() + 1900;
        mon = date.getMonth() + 1;
        day = date.getDate();
        today = year+"/"+mon+"/"+day;

        Cursor c = db.rawQuery("SELECT * FROM today WHERE date = '" + today + "' and what = '운동'", null);
        while (c.moveToNext()){
            System.out.println(c.getString(4));
            if(c.getString(4) == "")    continue;
            exerkcal += Integer.parseInt(c.getString(4));
        }

        c = db.rawQuery("SELECT * FROM today WHERE date = '" + today + "' and what = '음식'", null);
        while (c.moveToNext()){
            System.out.println(c.getString(4));
            if(c.getString(4) == "")    continue;
            foodkcal += Float.parseFloat(c.getString(4));
            sumCarbo += Float.parseFloat(c.getString(5));
            sumProtein += Float.parseFloat(c.getString(6));
            sumFat += Float.parseFloat(c.getString(7));
        }

        carboProgressBar.setProgress((int)sumCarbo);
        proProgressBar.setProgress((int)sumProtein);
        fatProgressBar.setProgress((int)sumFat);
        System.out.println(sumCarbo +""+ sumProtein +""+ sumFat);

        tv_carbo.setText(sumCarbo+"/"+maxCarbo);
        tv_protein.setText(sumProtein+"/"+maxProtein);
        tv_fat.setText(sumFat+"/"+maxFat);


        totalkcal = information.getInt("storedTotal",0);
        System.out.println("StoredTotal ->" +totalkcal);
        tv_total.setText(totalkcal+"");
        tv_exer.setText(exerkcal+"");
        tv_food.setText(foodkcal+"");
        remainkcal = totalkcal-foodkcal+exerkcal;
        tv_remain.setText(remainkcal+"");

    }

}