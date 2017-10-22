package example.yzhhzq.courseschedule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import junit.framework.Test;

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

public class SearchActivity extends AppCompatActivity {
    private Spinner spinner;
    private List<String> major_list;
    private ArrayAdapter<String> major_adapter;
    private EditText searchEdit;
    private Button btn_search;
    private Button btn_add;
    private CheckBox cb_crn;
    private CheckBox cb_cname;
    private SharedPreferences pref;
    private List<Course> myCourses=new ArrayList<Course>();
    private int key=0;
    public static final int SHOW_RESPONSE = 0;
    public static final int SHOW_RESPONSE_MAJOR = 1;
    public static final int SHOW_RESPONSE_ADD = 2;
    // handler were used to deal the search
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE: {
                    parseJSONWithJSONObject((String) msg.obj);
                    ListViewAdapter adapter = new ListViewAdapter(SearchActivity.this,
                            R.layout.course_item, myCourses,1);
                    ListView listView = (ListView) findViewById(R.id.s_list_view);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new OnItemClickListener());
                } break;
                case SHOW_RESPONSE_MAJOR:
                {
                    parseJSONWithJSONObject_major((String) msg.obj);

                    //adpater
                    major_adapter= new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_spinner_item, major_list);
                    //adpater style
                    major_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //load adpater
                    spinner.setAdapter(major_adapter);
                    spinner.setOnItemSelectedListener(
                            new AdapterView.OnItemSelectedListener() {
                                public void onItemSelected(AdapterView<?> parent,
                                                           View view, int position, long id) {
                                    sendRequestWithHttpURLConnection_major_search(major_list.get(position));
                                }

                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                } break;
                case SHOW_RESPONSE_ADD:
                {
                    parseJSONWithJSONObject_add((String) msg.obj);
                 //  System.out.println("key_s="+key);
                    if(key==1)
                        Toast.makeText(SearchActivity.this, "Add successed", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(SearchActivity.this, "Add failed", Toast.LENGTH_LONG).show();
                }break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        spinner = (Spinner) findViewById(R.id.spinner);
        searchEdit = (EditText) findViewById(R.id.CourseName);
        cb_cname=(CheckBox) findViewById(R.id.cb_cname);
        cb_crn=(CheckBox) findViewById(R.id.cb_crn);
        pref = getSharedPreferences("login_info",Context.MODE_PRIVATE);
        major_list = new ArrayList<String>();
        major_list.add("");
        sendRequestWithHttpURLConnection_major();
        btn_search=(Button) findViewById(R.id.button);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // use different way(crn,course name) to serach
                if(cb_crn.isChecked())
                {
                 String crn;
                    crn=searchEdit.getText().toString();
                    sendRequestWithHttpURLConnection(crn,1);
                 }
                if(cb_cname.isChecked())
                {
                    String cname;
                    cname=searchEdit.getText().toString();
                    sendRequestWithHttpURLConnection(cname,0);
                }

            }
        });
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
    private void sendRequestWithHttpURLConnection(final String
                                                          key,final int spec) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    String a=key;
                    int b=spec;
                    URL url = new
                            URL("http://10.0.2.2:8080/Schedule/search.do");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(16000);
                    connection.setReadTimeout(16000);
                    DataOutputStream out = new
                            DataOutputStream(connection.getOutputStream());
                    if(b==1) {
                        out.writeBytes("crn="+a+"&spec="+b);
                    }
                  else
                        out.writeBytes("name_char="+a+"&spec="+b);
                    InputStream in = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Message message= new Message();
                    message.what=SHOW_RESPONSE;
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
    private void parseJSONWithJSONObject_major(String jsonData) {

        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                major_list.add(jsonObject.getString("major"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void sendRequestWithHttpURLConnection_major_search(final String majorname) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    String a=majorname;
                    URL url = new
                            URL("http://10.0.2.2:8080/Schedule/major.do");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(16000);
                    connection.setReadTimeout(16000);
                    DataOutputStream out = new
                            DataOutputStream(connection.getOutputStream());

                        out.writeBytes("majorname="+a);

                    InputStream in = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Message message= new Message();
                    message.what=SHOW_RESPONSE;
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
    private void sendRequestWithHttpURLConnection_major() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {

                    URL url = new
                            URL("http://10.0.2.2:8080/Schedule/major.do");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(16000);
                    connection.setReadTimeout(16000);
                    InputStream in = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Message message= new Message();
                    message.what=SHOW_RESPONSE_MAJOR;
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
    public void sendRequestWithHttpURLConnection_add(final Course c,final int uid) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    int a=c.getCRN();
                    int b=uid;
                    URL url = new
                            URL("http://10.0.2.2:8080/Schedule/addtolist.do");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    DataOutputStream out = new
                            DataOutputStream(connection.getOutputStream());
                    out.writeBytes("crn="+a+"&uid="+b);
                    InputStream in = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Message message= new Message();
                    message.what=SHOW_RESPONSE_ADD;
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
    private void parseJSONWithJSONObject_add(String jsonData) {
        key=0;
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                key = jsonObject.getInt("add");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //click listener to course list item
    private class OnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
           final Course c1 = myCourses.get(position);
            AlertDialog.Builder dialog = new AlertDialog.Builder
                    (SearchActivity.this);
            dialog.setTitle("Detail Information");
            dialog.setMessage("CRN:"+c1.getCRN()+" "+c1.getCid()+"  "+c1.getCname()+"\n"+"Credits： "+c1.getCredit()+"\n"+"Instructor: "+c1.getInstructor()+"\n"+c1.getWeek()
                    +" "+c1.getStar_time()+" to "+c1.getEnd_time()+"\n"+c1.getLocation()+"\n"+c1.getFrom_data()+" to "
                    +c1.getTo_data()+"\n"+"pre_requested Course: "+c1.getPre());
            dialog.setCancelable(false);
            dialog.setPositiveButton("Add to MyCourses", new DialogInterface.
                    OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                 sendRequestWithHttpURLConnection_add(c1,pref.getInt("uid",0));
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
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
