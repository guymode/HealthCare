package pl.polidea.asl.healthcare;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends TabActivity {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private DataOutputStream os;
    private DataInputStream is;
    private static final int PORT = 5555;
    private Socket socket;
    private String IP = "192.168.0.99";

    public static final String PROPERTY_REG_ID = "mylittlecalendar-1143";
    private static final String PROPERTY_APP_VERSION = "1.0";
    private static final String TAG = "ICELANCER";
    String SENDER_ID = "98102895089";

    String clientID;
    String clientEM;
    String clientNAME;
    int clientHGT;
    int clientWGT;
    int clientAGE;  //AsyncTask를 위한 변수들.

    GoogleCloudMessaging gcm;
    Context context;

    SharedPreferences information;

    String regid;


    int gender = -1;
    int choiceButton=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabHost = getTabHost();

        // 1번째 Tab      캘린더
        tabHost.addTab(tabHost.newTabSpec("INFORMATION")
                .setIndicator("INFORMATION", getResources().getDrawable(R.drawable.ic_btn_addtask))
                .setContent(new Intent(this, InformationActivity.class)));
        // 2번째 Tab      그룹
        tabHost.addTab(tabHost.newTabSpec("CALENDAR")
                .setIndicator("CALENDAR", getResources().getDrawable(R.drawable.ic_btn_addtask))
                .setContent(new Intent(this, CalendarActivity.class)));
        // 2번째 Tab      그룹
        tabHost.addTab(tabHost.newTabSpec("FOOD")
                .setIndicator("FOOD", getResources().getDrawable(R.drawable.ic_btn_addtask))
                .setContent(new Intent(this, FoodActivity.class)));
        // 2번째 Tab      그룹
        tabHost.addTab(tabHost.newTabSpec("EXERCISE")
                .setIndicator("EXERCISE", getResources().getDrawable(R.drawable.ic_btn_addtask))
                .setContent(new Intent(this, ExerciseActivity.class)));

        tabHost.setCurrentTab(0);

        information = PreferenceManager.getDefaultSharedPreferences(this);
        context = getApplicationContext();

        if(information.getString("storedId","") == ""){
            register_info();
        }       //저장된 아이디가 없다면 계정 등록

        if(information.getString("storedIp","") != ""){
            IP = information.getString("storedIp","");
        }       //아이피가 저장될 경우 아이피 저장


    }
    public void register_info(){
        LayoutInflater inflater = getLayoutInflater();
        final View view_enroll = inflater.inflate(R.layout.activity_dialogue, null);
        final EditText myid = (EditText) view_enroll.findViewById(R.id.et_regid);
        myid.setNextFocusDownId(R.id.et_regname);
        final EditText myname = (EditText) view_enroll.findViewById(R.id.et_regname);
        myname.setNextFocusDownId(R.id.et_regemail);
        final EditText myemail = (EditText) view_enroll.findViewById(R.id.et_regemail);
        myemail.setNextFocusDownId(R.id.et_regip);
        final EditText ipadrress = (EditText)   view_enroll.findViewById(R.id.et_regip);
        ipadrress.setNextFocusDownId(R.id.et_age);
        final EditText myage = (EditText) view_enroll.findViewById(R.id.et_age);
        myage.setNextFocusDownId(R.id.et_height);
        final EditText myheight = (EditText) view_enroll.findViewById(R.id.et_height);
        myheight.setNextFocusDownId(R.id.et_weight);
        final EditText myweight = (EditText) view_enroll.findViewById(R.id.et_weight);

        final AlertDialog.Builder buider = new AlertDialog.Builder(this); //객체 생성

        final Button bu_gender = (Button) view_enroll.findViewById(R.id.bu_gender);

        bu_gender.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String genders[] = {"남자", "여자"};

                AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                ab.setTitle("성별을 선택하세요");
                ab.setSingleChoiceItems(genders, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(getApplicationContext(), whichButton + "", Toast.LENGTH_SHORT).show();
                                // 각 리스트를 선택했을때
                                choiceButton = whichButton;
                            }
                        }).setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                gender = choiceButton;
                                if (choiceButton == -1) gender = 0;
                                Toast.makeText(getApplicationContext(), whichButton + "", Toast.LENGTH_SHORT).show();
                                if (gender == 0) bu_gender.setText("남자");
                                if (gender == 1) bu_gender.setText("여자");
                                // OK 버튼 클릭시 , 여기서 선택한 값을 메인 Activity 로 넘기면 된다.
                            }
                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Cancel 버튼 클릭시
                            }
                        });
                ab.show();
            }
        });


        ipadrress.setText(IP);

        Drawable mInfoIcon = this.getResources().getDrawable(R.drawable.bg_d_infor_title);

   //     Bitmap bm = BitmapFactory.decodeResource(this.getResources(), R.drawable.bg_d_infor_title);
        View v= new LinearLayout(this);
        v.setBackground(mInfoIcon);

        buider.setCustomTitle(v);
        //buider.setTitle("INPUT YOUR INFORMATION"); //타이틀
        buider.setView(view_enroll); //빌더 세팅
        //buider.setIcon(R.drawable.logo);   //이미지
        buider.setPositiveButton("제출", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (myname.getText().length() >= 1 && myemail.getText().length() >= 1 && myid.getText().length() >= 1
                        && myage.getText().toString().length() >= 1 && myheight.getText().toString().length() >= 1 && myweight.getText().toString().length() >= 1
                        && gender != -1) {
                    String name = myname.getText().toString();//사용자이름
                    String email = myemail.getText().toString();//사용자id
                    String id = myid.getText().toString();
                    String age = myage.getText().toString();
                    String height = myheight.getText().toString();
                    String weight = myweight.getText().toString();
                    IP = ipadrress.getText().toString();

                    clientNAME = name;
                    clientEM = email;
                    clientID = id;
                    clientAGE = Integer.parseInt(age);
                    clientHGT = Integer.parseInt(height);
                    clientWGT = Integer.parseInt(weight);

                    if (checkPlayServices()) {      //gcm과 통신이 가능한지 검사
                        gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                        regid = getRegistrationId(context);
                        //regID를 가져온다.
                        if (regid.isEmpty() || information.getString("storedIp", "") == null) {
                            registerInBackground();
                        }
                        //만약 없다면 백그라운드에서 새로운 regID를 발급 받는다.
                    } else {
                        Log.i(TAG, "No valid Google Play Services APK found.");
                    }


                } else {
                    Toast.makeText(MainActivity.this, "NULL값을 가질 수 없습니다.", Toast.LENGTH_LONG).show();
                    register_info();
                }
            }
        });
        buider.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        buider.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        buider.show();
    }

    private void registerInBackground() {
        new AsyncTask<Integer, String, Integer>() {
            private ProgressDialog mDlg;

            @Override
            protected void onCancelled() {
                // TODO Auto-generated method stub
                mDlg.dismiss();
                finish();
                Log.d(TAG,"OnCancelled");
            }

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                mDlg = new ProgressDialog(MainActivity.this);
                mDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mDlg.setMessage("서버 찾는중");
                mDlg.show();
                mDlg.setCancelable(false);
            }

            @Override
            protected Integer doInBackground(Integer... params) {
                // TODO Auto-generated method stub
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    //regID를 발급받는다.
                    SharedPreferences.Editor editor = information.edit();
                    editor.putString("regid", regid);
                    editor.commit();
                    System.out.println(msg);

                    //mDlg.show();
                    //while문의 break 조건은 계정정보가 완료이다.
                    msg = "UP_" + clientID
                            + "NAME_" + clientNAME
                            + "EMAIL_" + clientEM
                            + "GENDER_" + gender
                            + "HEIGHT_" + clientHGT
                            + "WEIGHT_" + clientWGT
                            + "AGE_" + clientAGE
                            + "REG_" + regid;

                    publishProgress("서버에 접속 중");

                    //dev
                    return 1;
/*
                    socket = new Socket(InetAddress.getByName(IP), PORT);
                    os = new DataOutputStream(socket.getOutputStream());
                    is = new DataInputStream(socket.getInputStream());
                    os.writeUTF(msg);
                    msg = is.readUTF();
                    socket.close();

                    if(msg.equals("success")){
                        //regid와 앱 버전을 저장한다.
                        storeRegistrationId(context, regid);
                        return 1;
                    }
                    else if(msg.equals("false")){
                        return 2;
                    }
                    return 0;
                    */
                } catch (IOException ex) {
                    //msg = "Error :" + ex.getMessage();
                    Log.d("TAG", ex.getMessage());
                    return 0;
                }
            }

            //onProgressUpdate() 함수는 publishProgress() 함수로 넘겨준 데이터들을 받아옴
            @Override
            protected void onProgressUpdate(String... msg) {
                // TODO Auto-generated method stub
                if (msg[0] ==""){
                    mDlg.setMessage(msg[0]);
                }
            }

            @Override
            protected void onPostExecute(Integer msg) {
                // TODO Auto-generated method stub
                if(msg == 1){
                    Toast.makeText(MainActivity.this, "서버 접속 성공.", Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor= information.edit();
                    editor.putString("storedName", clientNAME);
                    editor.putString("storedEmail", clientEM);
                    editor.putString("storedId",clientID);
                    editor.putString("storedIp",IP);
                    editor.putInt("storedGender", gender);
                    editor.putInt("storedHgt", clientHGT);
                    editor.putInt("storedWgt", clientWGT);
                    editor.putInt("storedAge", clientAGE);
                    double total=0;
                    if(gender == 0){
                        total = 66.47 + (13.75 * (double)clientWGT) + (5*(double)clientHGT) - (6.76*(double)clientAGE);
                    }
                    else if(gender == 1){
                        total = 655.1 + (9.56 * (double)clientWGT) + (1*(double)clientHGT) - (4.68*(double)clientAGE);
                    }
                    editor.putInt("storedTotal",(int)total);

                    editor.commit();
                    mDlg.dismiss();
                }
                else if(msg == 2){
                    Toast.makeText(MainActivity.this, "중복 ID가 존재합니다..", Toast.LENGTH_LONG).show();
                    mDlg.dismiss();
                    finish();
                }
                else{
                    Toast.makeText(MainActivity.this, "서버 등록 실패.", Toast.LENGTH_LONG).show();
                    mDlg.dismiss();
                    finish();
                }
            }

        }.execute(1);
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

    private void storeRegistrationId(Context context, String regid) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regid);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }


    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("ICELANCER", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public String getStoredID(){
        return information.getString("storedId", "");
    }


}
