package kr.edcan.acar.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import kr.edcan.acar.R;
import kr.edcan.acar.views.SeekArc;

public class MainActivity extends AppCompatActivity {

    BluetoothSPP bt;
    SeekArc seekArc;
    TextView progressText;
    int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt = new BluetoothSPP(this);
        bt.setAutoConnectionListener(new BluetoothSPP.AutoConnectionListener() {
            public void onNewConnection(String name, String address) {
                Toast.makeText(MainActivity.this, name + " " + address, Toast.LENGTH_SHORT).show();
            }

            public void onAutoConnectionStarted() {
            }
        });
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                Log.e("asdf", message);
            }
        });
        seekArc = (SeekArc) findViewById(R.id.main_percent_exp);
        progressText = (TextView) findViewById(R.id.mainPercentText);
        seekArc.setProgress(0);
        seekArc.setMax(100);
        setProgress();
    }


    void setProgress() {
        seekArc.setProgress(progress++);
        progressText.setText(progress + "%");
        if (progress < 72) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setProgress();
                }
            }, 10);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            bt.enable();
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            }
        }
    }

    private void setup() {
        bt.autoConnect("HC-06");
    }
}
