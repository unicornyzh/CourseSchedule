package example.yzhhzq.courseschedule;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import static android.Manifest.permission.READ_CONTACTS;


public class LoginActivity extends AppCompatActivity {
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button login;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPass;
    private int uid;
    private boolean echo;
    private int sign;
    String account;
    String password;
    public static final int SHOW_RESPONSE = 0;
    public static final int SHOW_RESPONSE_REGISTER = 1;
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE: {
                    parseJSONWithJSONObject((String) msg.obj);
                 //   System.out.println(echo+"  "+uid);
                    if (echo) {
                        editor = pref.edit();
                        editor.putInt("uid",uid);
                        if (rememberPass.isChecked()) { //  check the check box status then put the information to the logininfo file
                            editor.putString("account", account);
                            editor.putString("password", password);
                            editor.putBoolean("remember",true);
                        } else {
                            editor.clear();
                        }
                        editor.commit();
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder
                                (LoginActivity.this);
                        dialog.setTitle("Notice");
                        dialog.setMessage("This account or password are invalid. Do you want to use this account name and password as a new account?");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("Sign up", new DialogInterface.
                                OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendRequestWithHttpURLConnection_register(account,password);
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
                }break;
                case SHOW_RESPONSE_REGISTER: {
                    parseJSONWithJSONObject_register((String) msg.obj);
                       System.out.println("sign_up="+sign);
                    if (sign==1) {
                        Toast.makeText(LoginActivity.this, "sign up successfully",
                                Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "account has existed",
                                Toast.LENGTH_SHORT).show();
                     }
                }break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        pref = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            String account1 = pref.getString("account", "");
            String password1 = pref.getString("password", "");
            accountEdit.setText(account1);
            passwordEdit.setText(password1);
            rememberPass.setChecked(true);
        }
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                account = accountEdit.getText().toString();
                password = passwordEdit.getText().toString();
// send the account and password to server for verifying
                sendRequestWithHttpURLConnection(account,password);

            }
        });
    }

    private void sendRequestWithHttpURLConnection(final String
                                                          uname,final String pw) {
// create thread to send a internet request to verify users
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    String a=uname;
                    String b=pw;
                    URL url = new
                            URL("http://10.0.2.2:8080/Schedule/verify.do");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(16000);
                    connection.setReadTimeout(16000);
                    DataOutputStream out = new
                            DataOutputStream(connection.getOutputStream());
                    out.writeBytes("username="+a+"&pw="+b);
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

    private void sendRequestWithHttpURLConnection_register(final String
                                                          uname,final String pw) {
// create thread to send a internet request to verify users
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    String a=uname;
                    String b=pw;
                    URL url = new
                            URL("http://10.0.2.2:8080/Schedule/sign.do");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(16000);
                    connection.setReadTimeout(16000);
                    DataOutputStream out = new
                            DataOutputStream(connection.getOutputStream());
                    out.writeBytes("username="+a+"&pw="+b);
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
                    message.what=SHOW_RESPONSE_REGISTER;
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
            JSONObject jsonObject = new JSONObject(jsonData);
            echo = jsonObject.getBoolean("echo");
            uid = jsonObject.getInt("uid");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void parseJSONWithJSONObject_register(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                sign = jsonObject.getInt("sign_up");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}