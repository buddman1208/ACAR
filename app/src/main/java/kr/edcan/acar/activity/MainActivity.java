package kr.edcan.acar.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.login.LoginManager;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.Random;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import kr.edcan.acar.R;
import kr.edcan.acar.databinding.ActivityMainBinding;
import kr.edcan.acar.utils.DataManager;
import kr.edcan.acar.views.SeekArc;

public class MainActivity extends AppCompatActivity {
    MediaPlayer audio_play;
    ActivityMainBinding binding;
    BluetoothSPP bt;
    SeekArc seekArc;
    TextView progressText;
    int progress = 0;
    MaterialDialog.Builder builder, childTempErr;
    MaterialDialog dialog, childTempDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        builder = new MaterialDialog.Builder(this)
                .title("사람이 내리는 중")
                .content("출발하거나 문을 닫지 말아주세요")
                .cancelable(false);
        dialog = builder.build();
        childTempErr = new MaterialDialog.Builder(this)
                .customView(R.layout.custom_dialog_view, true)
                .cancelable(false);
        childTempDialog = childTempErr.build();
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        AssetFileDescriptor afd = null;
        try {
            afd = getAssets().openFd("errorsound.mp3");
            audio_play = new MediaPlayer();
            audio_play.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            audio_play.prepare();
        } catch (IOException e) {
            Log.e("asdf", e.getMessage());
            e.printStackTrace();
        }

//        startService(new Intent(getApplicationContext(), MessageInstanceIDService.class));
//        startService(new Intent(getApplicationContext(), MessagingService.class));
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

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                updateUI(message);
                Log.e("asdf", message + "");

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
        bt.autoConnect("haejukwang");
    }


    void setProgress() {
        if (progress < 100) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    seekArc.setProgress(progress++);
                    progressText.setText(progress + "%");
                    setProgress();
                }
            }, 10);
        } else if (progress == 100) {
            setFuelStatus();
        }
    }

    private void setFuelStatus() {
        if (progress > 0) {
            progress--;
            seekArc.setProgress(progress);
            progressText.setText(progress + "%");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setFuelStatus();
                }
            }, 500);
        } else if (progress == 0) {
            Toast.makeText(MainActivity.this, "엔진이 꺼집니다.", Toast.LENGTH_SHORT).show();
            binding.mainEngineText.setText("꺼짐");
            bt.send("a", false);
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

    void updateUI(String originStr) {
        Random random = new Random();
//        final String originStr = random.nextInt(2) + "," + random.nextInt(4) + "," + random.nextInt(2) + "," + random.nextInt(100);
        String[] status = originStr.split(",");
        /*뒷자리애0 1,앞자리 사람종류 1어른 2어린이 3애기,문에 누구있음1 없음0,온도*/
        Resources res = getResources();
        int back = Integer.parseInt(status[0]);
        int front = Integer.parseInt(status[1]);
        int door = Integer.parseInt(status[2]);
        int temp = Integer.parseInt(status[3]);
//        int engine = Integer.parseInt(status[4]);

        binding.mainCarTemp.setText(temp + "");
        binding.mainFrontIcon.setImageDrawable(res.getDrawable((front == 0) ? R.drawable.acc_status_seat_off : R.drawable.acc_status_seat_on));
        binding.mainRearIcon.setImageDrawable(res.getDrawable((back == 0) ? R.drawable.acc_status_seat_off : R.drawable.acc_status_seat_on));
        binding.mainFrontBackground.setBackgroundResource((front == 0) ? R.drawable.round_10dp_not_colored : R.drawable.round_10dp_colored);
        binding.mainRearBackground.setBackgroundResource((back == 0) ? R.drawable.round_10dp_not_colored : R.drawable.round_10dp_colored);
//        binding.mainEngineText.setText((engine == 0) ? "꺼짐" : "양호");
        binding.mainRearrText.setTextColor(res.getColor((back == 0) ? R.color.main_not_selected_color : R.color.main_selected_color));
        binding.mainFrontText.setTextColor(res.getColor(!(front == 0) ? R.color.main_selected_color : R.color.main_not_selected_color));
        binding.mainFrontSubText.setTextColor(res.getColor(!(front == 0) ? R.color.sub_selected_color : R.color.sub_not_selected_color));
        binding.mainRearSubText.setTextColor(res.getColor((back == 0) ? R.color.sub_not_selected_color : R.color.sub_selected_color));
        binding.mainCloseText.setText("열림");

        String frontResult;
        switch (front) {
            case 0:
                frontResult = "없음";
                break;
            case 1:
                frontResult = "어른";
                break;
            case 2:
                frontResult = "어린이";
                break;
            case 3:
                frontResult = "아기";
                break;
            default:
                frontResult = "없음";
        }
        binding.mainRearrText.setText((back == 0) ? "없음" : "탑승함");
        binding.mainFrontText.setText(frontResult);
        if (door == 0)
            dialog.show();
        else dialog.dismiss();


        if (temp > 35) {
            childTempDialog.show();
            if(!audio_play.isPlaying()) {
                audio_play.start();
            }
        } else if (temp <= 35) {
            childTempDialog.dismiss();
            if (audio_play.isPlaying()) {
                audio_play.stop();
                try {
                    audio_play.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                new MaterialDialog.Builder(this)
                        .title("로그아웃")
                        .content("로그아웃합니다.")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                LoginManager.getInstance().logOut();
                                DataManager d = new DataManager();
                                d.initializeManager(getApplicationContext());
                                d.removeAllData();
                                startActivity(new Intent(getApplicationContext(), AuthActivity.class));
                                finish();
                            }
                        })
                        .positiveText("확인")
                        .positiveColor(getResources().getColor(R.color.colorPrimary))
                        .show();
        }
        return super.onOptionsItemSelected(item);
    }
}