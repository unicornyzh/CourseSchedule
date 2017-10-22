package example.yzhhzq.courseschedule;


import android.app.AlertDialog;
import android.content.Context;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.*;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Add_dialog  {

    Context context;
    Course c=new Course();
    AlertDialog Add;
    TextView titleView;
    TextView messageView;
    LinearLayout buttonLayout;
    public int key=0;
    private static final int SHOW_RESPONSE = 0;
    private int uid=0;
    // use to handle the data from urlconnection
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE: {
                   key=parseJSONWithJSONObject_add((String) msg.obj);
                   System.out.println("key_handle= "+key);
                }break;
            }
        }
    };

    public  Add_dialog(Context context, Course c, int uid) {
        // TODO Auto-generated constructor stub
        this.context=context;
        this.c=c;
        this.uid=uid;
        Add=new AlertDialog.Builder(context,R.layout.add_dialog).create();
        Add.show();
        Add.setContentView(R.layout.add_dialog);
        titleView=(TextView)Add.findViewById(R.id.dialog_title);
        messageView=(TextView)Add.findViewById(R.id.message);
        buttonLayout=(LinearLayout)Add.findViewById(R.id.buttonlayout);

    }

    public void close()
    {
        Add.dismiss();
    }
    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setMessage(String message)
    {
        messageView.setText(message);
    }
    /**
     * set the button groups
     * @param text
     * @param listener
     */
    public void setPositiveButton(String text,final View.OnClickListener listener)
    {
        Button button=new Button(context);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(params);
        button.setText(text);
        button.setTextColor(Color.BLACK);
        button.setTextSize(20);
        button.setOnClickListener(listener);
        buttonLayout.addView(button);
    }
    public void setNegativeButton(String text,final View.OnClickListener listener)
    {
        Button button=new Button(context);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(params);
        button.setText(text);
        button.setTextColor(Color.BLACK);
        button.setTextSize(20);
        button.setOnClickListener(listener);
        if(buttonLayout.getChildCount()>0)
        {
            params.setMargins(20, 0, 0, 0);
            button.setLayoutParams(params);
            buttonLayout.addView(button, 1);
        }else{
            button.setLayoutParams(params);
            buttonLayout.addView(button);
        }
    }

    public void sendRequestWithHttpURLConnection_add() {
//  create thread to send a urlconnection request

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
                        //  read the input stream
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
    private int parseJSONWithJSONObject_add(String jsonData) {
        int k=0;
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                k = jsonObject.getInt("add");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return k;
    }
}
