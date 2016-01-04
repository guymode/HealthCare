package pl.polidea.asl.healthcare;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.Date;

import pl.polidea.asl.healthcare.dbhelper.MyDBHelper;

public class DetailActivity extends Activity implements OnClickListener {
    private static final int DIALOG_DATE =0;
    private static final int DIALOG_TIME = 1;

    int mId;
    String today;
    String title;
    String time;
    int year,month,day;
    int hh,mm;
    boolean aa;

    String selectedDate;

    int Param;
    SQLiteDatabase db;
    MyDBHelper myDBHelper;

    Date date = new Date();// 오늘에 날짜를 세팅 해준다.
    TextView tv_food, tv_exer, tv_total, tv_remain;
    int toyear, tomon;

    SharedPreferences information;
    int foodkcal = 0;
    int exerkcal = 0;
    int totalkcal = 0;
    int remainkcal = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tv_total = (TextView)findViewById(R.id.tv_total);
        tv_food = (TextView)findViewById(R.id.tv_food);
        tv_exer = (TextView)findViewById(R.id.tv_exer);
        tv_remain = (TextView)findViewById(R.id.tv_remains);

        information = PreferenceManager.getDefaultSharedPreferences(this);
        toyear = date.getYear() + 1900;
        tomon = date.getMonth() + 1;

        Intent intent = getIntent();
        mId = intent.getIntExtra("_id", -1);
        Param = intent.getIntExtra("Param", -1);
        selectedDate = intent.getStringExtra("_date");
        myDBHelper = new MyDBHelper(this, "Today.db", null, 1);
        db=myDBHelper.getWritableDatabase();

        today = toyear +"/"+tomon+"/"+selectedDate;
        System.out.println("detail_today date -> " + today);
        Cursor c = db.rawQuery("SELECT * FROM today WHERE date = '" + today + "' and what = '운동'", null);
        String[] from = new String[]{"name", "kcal"};
        c.moveToFirst();
        int[]to = new int[]{ R.id.tv_name, R.id.tv_kcal};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.activity_cell, c,
                                            from,
                                            to);
        //System.out.println(c.getString(4));
        //foodkcal += Integer.parseInt(c.getString(4));
        ListView exerList = (ListView) findViewById(R.id.lv_exerlist);
        exerList.setAdapter(adapter);
        c.moveToFirst();

        c = db.rawQuery("SELECT * FROM today WHERE date = '" + today + "' and what = '운동'", null);
        // foodkcal += Integer.parseInt(c.getString(2));
        while (c.moveToNext()){
            // foodkcal += Integer.parseInt(c.getString(2));
            System.out.println(c.getString(4));
            if(c.getString(4) == "")    continue;
            exerkcal += Integer.parseInt(c.getString(4));
        }

        c = db.rawQuery("SELECT * FROM today WHERE date = '" + today + "' and what = '음식'", null);
       // foodkcal += Integer.parseInt(c.getString(2));
        while (c.moveToNext()){
           // foodkcal += Integer.parseInt(c.getString(2));
            System.out.println(c.getString(4));
            if(c.getString(4) == "")    continue;
            foodkcal += Integer.parseInt(c.getString(4));
        }


        c = db.rawQuery("SELECT * FROM today WHERE date = '" + today + "' and what = '음식'", null);
        from = new String[]{"name", "kcal"};
        c.moveToFirst();
        to = new int[]{ R.id.tv_name, R.id.tv_kcal};
        adapter = new SimpleCursorAdapter(this, R.layout.activity_cell, c,
                from,
                to);
        ListView foodList = (ListView) findViewById(R.id.lv_foodlist);
        foodList.setAdapter(adapter);

        totalkcal = information.getInt("storedTotal",0);
        tv_total.setText(totalkcal+"");
        tv_food.setText(foodkcal + "");
        tv_exer.setText(exerkcal+"");

        remainkcal = totalkcal-foodkcal+exerkcal;
        tv_remain.setText(remainkcal+"");
        //final SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, c, new String[]{"what", "name"},
        //        new int[]{android.R.id.text1, android.R.id.text2});

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
    }

}