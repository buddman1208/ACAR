package kr.edcan.acar.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import kr.edcan.acar.R;
import kr.edcan.acar.service.MessageInstanceIDService;
import kr.edcan.acar.service.MessagingService;
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
        startService(new Intent(getApplicationContext(), MessageInstanceIDService.class));
        startService(new Intent(getApplicationContext(), MessagingService.class));

        FirebaseInstanceId.getInstance().getToken();
        bt = new BluetoothSPP(this);

        if (!bt.isBluetoothAvailable())

        {
            Toast.makeText(getApplicationContext()
                    , "블루투스를 켜주세요"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener()

        {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "연결되었습니다", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext()
                        , "연결이끊겼습니다"
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() {
            }
        });

        bt.setAutoConnectionListener(new BluetoothSPP.AutoConnectionListener() {
            public void onNewConnection(String name, String address) {
                Log.e("asdf", name);
            }

            public void onAutoConnectionStarted() {
            }
        });
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                Log.e("asdf", message+" asdf");
            }
        });

        seekArc = (SeekArc) findViewById(R.id.main_percent_exp);
        progressText = (TextView) findViewById(R.id.mainPercentText);
        seekArc.setProgress(0);
        seekArc.setMax(100);
        setProgress();
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

    public void setup() {
        bt.autoConnect("songjunfuck");
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
            } else {
                Toast.makeText(getApplicationContext()
                        , "블루투스 켜주세요"
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    void updateUI(String originStr){
        String[] status = originStr.split(",");
        /*뒷자리애0 1,앞자리 사람종류 1어른 2어린이 3애기,문에 누구있음1 없음0,온도*/
        int back = Integer.parseInt(status[0]);
        int front = Integer.parseInt(status[1]);
        int door = Integer.parseInt(status[2]);
        int temp = Integer.parseInt(status[3]);


    }

}