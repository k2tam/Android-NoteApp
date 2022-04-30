package com.example.noteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Locale;

public class Settings extends AppCompatActivity {
    ImageButton btnBack;
    SwitchCompat swtDisMode;
    SharedPreferences sharedPreferences;
    AutoCompleteTextView autoCompleteLang;
    ArrayAdapter<String> adapterLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initUI();
        initListener();
        loadLocale();
    }

    private void initUI() {
        swtDisMode = findViewById(R.id.swtDisplayMode);
        btnBack = findViewById(R.id.btnSettingBack);
        autoCompleteLang = findViewById(R.id.autoCompleteLang);

        sharedPreferences = getSharedPreferences("night",0);
        Boolean displayNight = sharedPreferences.getBoolean("night_mode", false);

        if(displayNight){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            swtDisMode.setChecked(true);
        }else{
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            swtDisMode.setChecked(false);
        }
    }

    private void initListener() {

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swtDisMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    swtDisMode.setChecked(true);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode",true);
                    editor.commit();
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    swtDisMode.setChecked(false);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode",false);
                    editor.commit();
                }
            }
        });

        String[] items = {"English","Viet Nam"};
        adapterLang= new ArrayAdapter<>(this,R.layout.lang_item,items);
        autoCompleteLang.setAdapter(adapterLang);


        autoCompleteLang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Toast.makeText(getApplicationContext(), "En",Toast.LENGTH_LONG).show();
                        setLocale("en");
                        recreate();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "Vi",Toast.LENGTH_LONG).show();
                        setLocale("vi");
                        recreate();
                        break;
                }
            }
        });
    }

    private void setLocale(String lang) {
        Locale locale= new Locale(lang);
        locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
        editor.putString("lang",lang);
        editor.apply();
        Log.d("check","Ok");
    }

    private void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("settings",MODE_PRIVATE);
        String lang = prefs.getString("lang","en");
        setLocale(lang);

    }
}