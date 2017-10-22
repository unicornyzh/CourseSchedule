package example.yzhhzq.courseschedule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;

public class ReminderActivity extends AppCompatActivity {
    private List<Course> myCourses=new ArrayList<Course>();
    private SharedPreferences pref;
    private static final int SHOW_RESPONSE = 0;
    public static final int SHOW_RESPONSE_DELETE = 2;
    private int key=0;
    // handler used to deal user's courses search and delete courses
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE: {
                    parseJSONWithJSONObject((String) msg.obj);
                    ListViewAdapter adapter = new ListViewAdapter(ReminderActivity.this,
                            R.layout.course_item, myCourses,0);
                    ListView listView = (ListView) findViewById(R.id.r_list_view);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new OnItemClickListener());
                }break;

                case SHOW_RESPONSE_DELETE:
                {
                    parseJSONWithJSONObject_delete((String) msg.obj);
                    System.out.println("key_r="+key);
                    if(key==1) {
                        sendRequestWithHttpURLConnection();
                        Toast.makeText(ReminderActivity.this, "Delete successed", Toast.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(ReminderActivity.this, "Delete failed", Toast.LENGTH_LONG).show();
                }break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        pref = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        sendRequestWithHttpURLConnection();

        }
    //find course
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
                    // store result to Message and send it to handler
                    Message message = new Message();
                    message.what = SHOW_RESPONSE;
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

    //click listener for each course
    private class OnItemClickListener implements AdapterView.OnItemClickListener {
        @Override

        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            final Course c1 = myCourses.get(position);
            AlertDialog.Builder dialog = new AlertDialog.Builder
                    (ReminderActivity.this);
            dialog.setTitle("Detail Information");
            dialog.setMessage("CRN:"+c1.getCRN()+" "+c1.getCid()+"  "+c1.getCname()+c1.getCid()+"\n"+"Credits： "+c1.getCredit()+"\n"+"Instructor: "+c1.getInstructor()+"\n"+c1.getWeek()
                    +" "+c1.getStar_time()+" to "+c1.getEnd_time()+"\n"+c1.getLocation()+"\n"+c1.getFrom_data()+" to "
                    +c1.getTo_data()+"\n"+"pre_requested Course: "+c1.getPre());
            dialog.setCancelable(false);
            dialog.setPositiveButton("delete this course", new DialogInterface.
                    OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendRequestWithHttpURLConnection_delete(c1,pref.getInt("uid",0));
                    dialog.dismiss();
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.
                    OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        }
    }
    //connection server to delete course
    public void sendRequestWithHttpURLConnection_delete(final Course c,final int uid) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    String k1;
                    int a=c.getCRN();
                    int b=uid;
                    k1= "http://10.0.2.2:8080/Schedule/addtolist.do?uid="+String.valueOf(b)+"&crn="+String.valueOf(a);
                    System.out.println("url==**: "+k1);
                    URL url = new
                            URL(k1);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Message message= new Message();
                    message.what=SHOW_RESPONSE_DELETE;
                    message.obj=response.toString();
                    handler.sendMessage(message);
                }
                catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

        private void parseJSONWithJSONObject(String jsonData) {

            try {
                JSONArray jsonArray = new JSONArray(jsonData);
                myCourses.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Course item1=new Course();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
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
                    if(jsonObject.getString("pre").equals(" "))
                        item1.setPre("no pre-request");
                    else
                        item1.setPre(jsonObject.getString("pre"));
                    myCourses.add(item1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    private void parseJSONWithJSONObject_delete(String jsonData) {
        key=0;
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                key = jsonObject.getInt("delete");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //rewrite the Back event
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}

