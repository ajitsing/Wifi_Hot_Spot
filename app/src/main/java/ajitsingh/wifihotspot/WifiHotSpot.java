package ajitsingh.wifihotspot;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

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
                boolean flag = ((CheckBox) v).isChecked();
                setWifiTetheringEnabled(flag);
            }
        });

    }

    public void listenToDataCheckBox() {
        CheckBox dataCheckBox = (CheckBox) findViewById(R.id.enableDataId);
        dataCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean flag = ((CheckBox) v).isChecked();
                setMobileDataEnabled(flag);
            }
        });

    }

    private void setWifiTetheringEnabled(boolean enable) {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        Method[] methods = wifiManager.getClass().getDeclaredMethods();

        try {
            Boolean wifiTetheringEnabled = (Boolean) getMethod(methods, "isWifiApEnabled").invoke(wifiManager);

            boolean enableTethering = enable && !wifiTetheringEnabled;
            boolean disableTethering = !enable && wifiTetheringEnabled;

            if (disableTethering || enableTethering)
                getMethod(methods, "setWifiApEnabled").invoke(wifiManager, null, enable);
        } catch (Exception ignored) {
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
            final Method getMobileDataEnabled = connectivityManagerClass.getDeclaredMethod("getMobileDataEnabled");

            setMobileDataEnabledMethod.setAccessible(true);

            boolean turnOffData = !enabled && (Boolean) getMobileDataEnabled.invoke(connectivityManager);
            boolean turnOnData = enabled && !(Boolean) getMobileDataEnabled.invoke(connectivityManager);

            if (turnOnData || turnOffData)
                setMobileDataEnabledMethod.invoke(connectivityManager, enabled);

        } catch (Exception ignored) {
        }
    }

    private Method getMethod(Method[] methods, String methodName) {
        for (Method method : methods) {
            System.out.println(method);
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
}