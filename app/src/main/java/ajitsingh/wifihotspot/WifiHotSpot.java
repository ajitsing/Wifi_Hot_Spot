package ajitsingh.wifihotspot;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class WifiHotSpot extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_hot_spot);

        setWifiTetheringEnabled(true);
        setMobileDataEnabled(true);

        listenToWifiHotSpotCheckBox();
        listenToDataCheckBox();
    }

    public void listenToWifiHotSpotCheckBox() {
        CheckBox enableWifiHotSpotCheckBox = (CheckBox) findViewById(R.id.enableWifiHotSpotId);

        enableWifiHotSpotCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    Toast.makeText(WifiHotSpot.this, "setting wifi hot spot", Toast.LENGTH_SHORT).show();
                    setWifiTetheringEnabled(true);
                } else {
                    Toast.makeText(WifiHotSpot.this, "turning wifi hot spot off", Toast.LENGTH_SHORT).show();
                    setWifiTetheringEnabled(false);
                }
            }
        });

    }

    public void listenToDataCheckBox() {
        CheckBox dataCheckBox = (CheckBox) findViewById(R.id.enableDataId);
        dataCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    Toast.makeText(WifiHotSpot.this, "data is enabled", Toast.LENGTH_SHORT).show();
                    setMobileDataEnabled(true);
                } else {
                    Toast.makeText(WifiHotSpot.this, "data is turned off", Toast.LENGTH_SHORT).show();
                    setMobileDataEnabled(false);
                }
            }
        });

    }

    private void setWifiTetheringEnabled(boolean enable) {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        Method[] methods = wifiManager.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals("setWifiApEnabled")) {
                try {
                    method.invoke(wifiManager, null, enable);
                } catch (Exception ignored) {
                }
                break;
            }
        }
    }

    private void setMobileDataEnabled(boolean enabled) {
        try {
            final ConnectivityManager conman = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
            connectivityManagerField.setAccessible(true);
            final Object connectivityManager = connectivityManagerField.get(conman);
            final Class connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
        } catch(Exception ignored) {
            System.out.println(ignored.getStackTrace());
        }
    }
}