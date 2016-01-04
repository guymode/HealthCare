package pl.polidea.asl.healthcare;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import java.util.Date;

import pl.polidea.asl.healthcare.dbhelper.MyDBHelper;

/**
 * Created by admin on 2015-12-29.
 */
public class InformationActivity  extends Activity {

    int year, mon, day;
    Date date;
    static MyDBHelper todayDBHelper;
    static SQLiteDatabase db;
    String today;
    int totalkcal = 0;
    int exerkcal = 0;
    int foodkcal = 0;
    int remainkcal = 0;
    SharedPreferences information;
    TextView tv_total, tv_food, tv_exer, tv_remain;

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
            foodkcal += Integer.parseInt(c.getString(4));
        }


        totalkcal = information.getInt("storedTotal",0);
        System.out.println("StoredTotal ->" +totalkcal);
        tv_total.setText(totalkcal+"");
        tv_exer.setText(exerkcal+"");
        tv_food.setText(foodkcal+"");
        remainkcal = totalkcal-foodkcal+exerkcal;
        tv_remain.setText(remainkcal+"");

    }

    @Override
    protected void onResume() {
        super.onResume();

        date = new Date();// 오늘에 날짜를 세팅 해준다.
        foodkcal = 0;
        exerkcal = 0;

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
            foodkcal += Integer.parseInt(c.getString(4));
        }


        totalkcal = information.getInt("storedTotal",0);
        System.out.println("StoredTotal ->" +totalkcal);
        tv_total.setText(totalkcal+"");
        tv_exer.setText(exerkcal+"");
        tv_food.setText(foodkcal+"");
        remainkcal = totalkcal-foodkcal+exerkcal;
        tv_remain.setText(remainkcal+"");

    }

}