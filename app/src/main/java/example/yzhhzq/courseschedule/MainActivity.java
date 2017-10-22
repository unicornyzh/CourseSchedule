package example.yzhhzq.courseschedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.View;//注意view的大小写
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.*;



import org.json.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity {
    private Button btn_search;
    private Button btn_reminder;
    private Button btn_logout;
    /**
     * empty grid
     */
    protected TextView empty;
    /**
     * grid of Monday
     */
    protected TextView monColum;
    /**
     * grid of Tuesday
     */
    protected TextView tueColum;
    /**
     * grid of Wednesday
     */
    protected TextView wedColum;
    /**
     * grid of Thrusday
     */
    protected TextView thrusColum;
    /**
     * grid of Friday
     */
    protected TextView friColum;
    /**
     * grid of Saturday
     */
    protected TextView satColum;
    /**
     * grid of Sunday
     */
    protected TextView sunColum;
    /**
     * The course schedule body part
     */
    protected RelativeLayout course_table_layout;
    /**
     * screen width
     **/
    protected int screenWidth;
    /**
     * the average width of each course
     **/
    protected int aveWidth;
    protected String[] Weekdays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    protected java.sql.Time times=strToTime("00:00:00");
    List<Course> myCourses=new ArrayList<Course>();
    public static final int SHOW_RESPONSE = 0;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE: {
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    //screen width
                    int width = dm.widthPixels;
                    //average width
                    int aveWidth = width / 8;
                    int height = dm.heightPixels;
                    int gridHeight = height / 15;
                    int[] background = {R.drawable.course_info_blue, R.drawable.course_info_green,
                            R.drawable.course_info_red, R.drawable.course_info_red,
                            R.drawable.course_info_yellow, R.color.brown,R.color.magenta,R.color.olive,R.color.blue,R.color.bisque,
                            R.color.antiquewhite,R.color.rosybrown};
                    // Add the course information
                    parseJSONWithJSONObject((String) msg.obj);
                    System.out.println("size= "+myCourses.size());

                    for(final Course context:myCourses) {
                        int n2=0;
                        int n1=0;
                        TextView courseInfo = new TextView(MainActivity.this);
                        String week;
                        courseInfo.setText(context.getCid()+"\n"+context.getCname()+"\n"+context.getInstructor());
                        n2=context.getEnd_time().getHours()-context.getStar_time().getHours()+1;
                        //(n2) the course height was by the time of one course
                        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                                aveWidth * 31 / 32,
                                (gridHeight - 5) * n2);
                        //(n2)the star position was decided by the star time of this course
                        n2=context.getStar_time().getHours();
                        rlp.topMargin = 5 + (n2 - 7) * gridHeight;
                        rlp.leftMargin = 1;
                        // the margin was decided by the day of this course,like Monday is 1
                     //   System.out.println(context.getWeek());
                     //   System.out.println(context.getCname());
                        week=context.getWeek();
                        switch(week)
                        {
                            case "M": n2=1; break;
                            case "T": n2=2; break;
                            case "W": n2=3;break;
                            case "R": n2=4;break;
                            case "F": n2=5;break;
                            case "S": n2=6;break;
                            case "SR":n2=7;break;
                            case"WF": n2=2;break;
                            case "MW": n2=7; break;

                        }
                        rlp.addRule(RelativeLayout.RIGHT_OF, n2);

                        courseInfo.setGravity(Gravity.CENTER);
                        //  set the background
                        courseInfo.setBackgroundResource(background[(int)(Math.random()*(11-0+1))]);
                        courseInfo.setTextSize(12);
                        courseInfo.setLayoutParams(rlp);
                        courseInfo.setTextColor(Color.WHITE);
                        //set the alpha
                        courseInfo.getBackground().setAlpha(222);
                        courseInfo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Add_dialog dialog = new Add_dialog
                                        (MainActivity.this,context,pref.getInt("uid",0));
                                dialog.setTitle("detail information");
                                dialog.setMessage("CRN:"+context.getCRN()+" "+context.getCid()+"  "+context.getCname()+"\n"+"Credits： "+context.getCredit()+"\n"+"Instructor: "+context.getInstructor()+"\n"+context.getWeek()
                                        +" "+context.getStar_time()+" to "+context.getEnd_time()+"\n"+context.getLocation()+"\n"+context.getFrom_data()+" to "
                                        +context.getTo_data());
                                dialog.setNegativeButton("booklist", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        editor=pref.edit();
                                        editor.putString("bookurl",context.getBooklist());
                                        editor.commit();
                                        System.out.println("m_url: "+context.getBooklist());
                                        Intent intent = new Intent(MainActivity.this,
                                                BookActivity.class);
                                        MainActivity.this.startActivity(intent);
                                        dialog.close();

                                    }
                                });
                                dialog.setNegativeButton("Cancel", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                     // TODO Auto-generated method stub
                                        dialog.close();

                                    }
                                });
                            }
                        });
                        course_table_layout.addView(courseInfo);
                    }


                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences("login_info",Context.MODE_PRIVATE);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_reminder = (Button) findViewById(R.id.btn_reminder);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_logout.setOnClickListener(new logout_listener());
        btn_reminder.setOnClickListener(new reminder_listener());
        btn_search.setOnClickListener(new search_listener());
        empty = (TextView) this.findViewById(R.id.test_empty);
        monColum = (TextView) this.findViewById(R.id.test_monday_course);
        tueColum = (TextView) this.findViewById(R.id.test_tuesday_course);
        wedColum = (TextView) this.findViewById(R.id.test_wednesday_course);
        thrusColum = (TextView) this.findViewById(R.id.test_thursday_course);
        friColum = (TextView) this.findViewById(R.id.test_friday_course);
        satColum = (TextView) this.findViewById(R.id.test_saturday_course);
        sunColum = (TextView) this.findViewById(R.id.test_sunday_course);
        course_table_layout = (RelativeLayout) this.findViewById(R.id.test_course_rl);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        // screen width
        int width = dm.widthPixels;
        // average width
        int aveWidth = width / 8;
        // the first grid is set to 25 width
        empty.setWidth(aveWidth * 3 / 4);
        monColum.setWidth(aveWidth * 33 / 32 + 1);
        tueColum.setWidth(aveWidth * 33 / 32 + 1);
        wedColum.setWidth(aveWidth * 33 / 32 + 1);
        thrusColum.setWidth(aveWidth * 33 / 32 + 1);
        friColum.setWidth(aveWidth * 33 / 32 + 1);
        satColum.setWidth(aveWidth * 33 / 32 + 1);
        sunColum.setWidth(aveWidth * 33 / 32 + 1);
        this.screenWidth = width;
        this.aveWidth = aveWidth;
        int height = dm.heightPixels;
        int gridHeight = height / 15;
        //设置课表界面Design the Course Schedule GUI
        //dynamically generate 15*maxCourseNum textview
        for (int i = 0; i <= 15; i++) {

            for (int j = 1; j <= 8; j++) {

                TextView tx = new TextView(MainActivity.this);
                tx.setId(i * 8 + j);
                //除了最后一列，都使用course_text_view_bg背景（最后一列没有右边框）
                if (j < 8)
                    tx.setBackgroundResource(
                            R.drawable.course_text_view_bg);
                else
                    tx.setBackgroundResource(
                            R.drawable.course_table_last_colum);
                //set the relativelayout
                RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
                        aveWidth * 33 / 32 + 1,
                        gridHeight);


                tx.setTextAppearance(this, R.style.courseTableText);
                //The first column(j) need to set the time of a day(8am to 10pm)
                if (j == 1) {

                    rp.width = aveWidth * 3 / 4;

                    if (i == 0)
                        rp.addRule(RelativeLayout.BELOW, empty.getId());
                    else {
                        times.setHours(i+7);
                        tx.setText(times.toString());
                        rp.addRule(RelativeLayout.BELOW, i * 8);
                    }
                } else {
                    rp.addRule(RelativeLayout.RIGHT_OF, i * 8 + j - 1);
                    rp.addRule(RelativeLayout.ALIGN_TOP, i * 8 + j - 1);
                    tx.setText("");
                }
                //the first row used for days(Monday->Sunday)
                if (i == 0) {
                    if (j == 1)
                        tx.setText(" ");
                    else
                        tx.setText(Weekdays[j - 2]);

                }

                tx.setLayoutParams(rp);
                course_table_layout.addView(tx);
            }

        }
        // Add course information
        sendRequestWithHttpURLConnection();


    }

    class logout_listener implements View.OnClickListener {
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = new Intent(MainActivity.this,
                    LoginActivity.class);
            MainActivity.this.startActivity(intent);
        }
    }

    class reminder_listener implements View.OnClickListener {
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ReminderActivity.class);
            MainActivity.this.startActivity(intent);
        }
    }

    class search_listener implements View.OnClickListener {
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SearchActivity.class);
            MainActivity.this.startActivity(intent);
        }
    }
    //json analyzer for courses
    private void parseJSONWithJSONObject(String jsonData) {

        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            myCourses.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                Course item1=new Course();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                item1.setBooklist(jsonObject.getString("booklist"));
                item1.setCname(jsonObject.getString("course_name"));
                item1.setCid(jsonObject.getString("id_course"));
                item1.setCredit(jsonObject.getDouble("credits"));
                item1.setWeek(jsonObject.getString("day_of_week"));
                item1.setCRN(jsonObject.getInt("crn"));
                item1.setInstructor(jsonObject.getString("instructor"));
                item1.setLocation(jsonObject.getString("room"));
                item1.setEnd_time(jsonObject.getString("end_time"));
                item1.setStar_time(jsonObject.getString("start_time"));
                item1.setTo_data(jsonObject.getString("to_date"));
                item1.setFrom_data(jsonObject.getString("from_date"));
                item1.setStatus(jsonObject.getInt("status"));
                if(jsonObject.getString("pre")==" ")
                    item1.setPre("no pre-request");
                else
                    item1.setPre(jsonObject.getString("pre"));
                myCourses.add(item1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRequestWithHttpURLConnection() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("http://10.0.2.2:8080/Schedule/viewhas.do");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    int uid=0;
                    uid=pref.getInt("uid",0);
                    System.out.println(uid);
                    out.writeBytes("uid="+uid);
                    InputStream in = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Message message = new Message();
                    message.what = SHOW_RESPONSE;
// 将服务器返回的结果存放到Message中
                    message.obj = response.toString();
                    System.out.println(response.toString());
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    public static java.sql.Time strToTime(String strDate) {
        String str = strDate;
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        java.util.Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.sql.Time time = new java.sql.Time(d.getTime());
        return time.valueOf(str);
    }

}
