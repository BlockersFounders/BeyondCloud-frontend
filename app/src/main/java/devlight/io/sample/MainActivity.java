package devlight.io.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.samsung.android.sdk.blockchain.ListenableFutureTask;
import com.samsung.android.sdk.blockchain.SBlockchain;
import com.samsung.android.sdk.blockchain.exception.SsdkUnsupportedException;
import com.samsung.android.sdk.blockchain.wallet.HardwareWallet;
import com.samsung.android.sdk.blockchain.wallet.HardwareWalletType;

import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity implements View.OnClickListener {

    Boolean connectBool = false;
    String connectionMsg;

    Button connectBtn;
    EditText loginTst;
    Button testSearchBtn;

    private SBlockchain sBlockchain;
    private HardwareWallet hardwareWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

        sBlockchain = new SBlockchain();

        try{
            sBlockchain.initialize(this); //블록체인 초기화
        }catch (SsdkUnsupportedException e){
            e.printStackTrace();
        }

        connectBtn = findViewById(R.id.connect_btn);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = connect();
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        testSearchBtn = findViewById(R.id.test_search_btn);
        testSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SearchActivity.class );
                startActivity(intent);
            }
        });

    }

    private String connect(){
        sBlockchain.getHardwareWalletManager() //cold wallet 리턴
                .connect(HardwareWalletType.SAMSUNG, true)
                .setCallback(new ListenableFutureTask.Callback<HardwareWallet>() { //비동기 (oncreate 안의 함수들이 동작했는지 확인하기 위해! 개별적인 함수 connect, generate, .. 각각의 함수가 성공 후 성공했다고 리턴)
                    @Override
                    public void onSuccess(HardwareWallet w) {
                        hardwareWallet = w; // callback 에서 생성된 hard wallet 사용
                        Log.d("SUCCESS TAG", "connection success");
                        connectionMsg = "connection success";

                        connectBool = true;
                    }

                    @Override
                    public void onFailure(ExecutionException e) {
                        Log.d("ERROR TAG", e.toString());
                        connectionMsg = "connection failure";

                    }

                    @Override
                    public void onCancelled(InterruptedException e) {

                    }
                });

        return connectionMsg;

    }

    private void initUI() {
        final View btnHorizontalNtb = findViewById(R.id.btn_horizontal_ntb);
        btnHorizontalNtb.setOnClickListener(this);
    }

// Go to BeyondCloud
    @Override
    public void onClick(final View v) {

        if(!connectBool){
            String msg = "wallet connection failure";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            return;
        }

        ViewCompat.animate(v)
                .setDuration(200)
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setInterpolator(new CycleInterpolator())
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(final View view) {

                    }

                    @Override
                    public void onAnimationEnd(final View view) {
                        switch (v.getId()) {
                            // main > Go to BeyondCloud
                            case R.id.btn_horizontal_ntb:
                                startActivity(
                                        new Intent(MainActivity.this, HorizontalNtbActivity.class)
                                );
                                break;

                            default:
                                break;
                        }
                    }

                    @Override
                    public void onAnimationCancel(final View view) {

                    }
                })
                .withLayer()
                .start();
    }

    private class CycleInterpolator implements android.view.animation.Interpolator {

        private final float mCycles = 0.5f;

        @Override
        public float getInterpolation(final float input) {
            return (float) Math.sin(2.0f * mCycles * Math.PI * input);
        }
    }
}
