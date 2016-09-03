package kr.edcan.acar.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import kr.edcan.acar.R;
import kr.edcan.acar.views.SeekArc;

public class MainActivity extends AppCompatActivity {

    SeekArc seekArc;
    TextView progressText;
    int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekArc = (SeekArc) findViewById(R.id.main_percent_exp);
        progressText = (TextView) findViewById(R.id.mainPercentText);
        seekArc.setProgress(0);
        seekArc.setMax(100);
        setProgress();
    }


    void setProgress() {
        seekArc.setProgress(progress++);
        progressText.setText(progress + "");
        if (progress < 50) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setProgress();
                }
            }, 10);
        }
    }
}
