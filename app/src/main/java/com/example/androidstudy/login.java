package com.example.androidstudy;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {

    private EditText adminText = null;
    private EditText ipText = null;
    private EditText portText = null;
    private EditText passwordText = null;
    private Button loginButton = null;

    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        adminText = (EditText) findViewById(R.id.admin);
        ipText = (EditText)findViewById(R.id.ip);
        portText = (EditText)findViewById(R.id.port);
        passwordText = (EditText)findViewById(R.id.password);

        SharedPreferences preferences = getPreferences(Activity.MODE_PRIVATE);
        editor = preferences.edit();

        String admin0 = preferences.getString("admin", null);
        String password0 = preferences.getString("password", null);
        String ip0 = preferences.getString("ip", null);
        String port0 = preferences.getString("port", null);

        String admin = adminText.getText().toString();
        String ip = ipText.getText().toString();
        String port = portText.getText().toString();
        String password = passwordText.getText().toString();

        if(TextUtils.isEmpty(admin) && !TextUtils.isEmpty(admin0)){
            adminText.setText(admin0);
        }
        if(TextUtils.isEmpty(password) && !TextUtils.isEmpty(password0)){
            passwordText.setText(password0);
        }
        if(TextUtils.isEmpty(port) && !TextUtils.isEmpty(port0)){
            portText.setText(port0);
        }
        if(TextUtils.isEmpty(ip) && !TextUtils.isEmpty(ip0)){
            ipText.setText(ip0);
        }

        loginButton = (Button)findViewById(R.id.login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.setuser(adminText.getText().toString());
                MainActivity.setIPAddress(ipText.getText().toString());
                MainActivity.setport(portText.getText().toString());
                MainActivity.setpassword(passwordText.getText().toString());

                editor.putString("admin", adminText.getText().toString());
                editor.putString("password", passwordText.getText().toString());
                editor.putString("ip", ipText.getText().toString());
                editor.putString("port", portText.getText().toString());
                editor.commit();

                Toast.makeText(login.this, "设置完成", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}