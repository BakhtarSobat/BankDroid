package bankdroid.demo.com.bankdroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import bankdroid.demo.com.bankdroid.receivers.BankResultReceiver;
import bankdroid.demo.com.bankdroid.service.BankService;

public class MainActivity extends AppCompatActivity {
    TextView label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        label = (TextView) findViewById(R.id.label);
        (findViewById(R.id.depositButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deposit();
            }
        });
        (findViewById(R.id.withdrawButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                withdraw();
            }
        });
        info();
    }

    private void withdraw() {
        BankService.startServiceToWithdraw(this, 10, new TransferMoneyResultReceiver(this, false));
        info();
    }

    private void deposit() {
        BankService.startServiceToDeposit(this, 10, new TransferMoneyResultReceiver(this, true));
        info();
    }

    private void info(){
        BankService.startServiceForBalance(this, new AccountInfoResultReceiver(this));

    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private static class TransferMoneyResultReceiver implements BankResultReceiver.ResultReceiverCallBack<Boolean> {
        private WeakReference<MainActivity> activityRef;
        private boolean deposit;
        public TransferMoneyResultReceiver(MainActivity activity, boolean deposit){
            activityRef = new WeakReference<MainActivity>(activity);
            this.deposit = deposit;
        }

        @Override
        public void onSuccess(Boolean data) {
            if(activityRef != null && activityRef.get() != null) {
                activityRef.get().showMessage(deposit ? "Deposited" : "Withdrew");
            }
        }

        @Override
        public void onError(Exception exception) {
            if(activityRef != null && activityRef.get() != null) {
                activityRef.get().showMessage(exception != null ? exception.getMessage() : "Error");
            }
        }
    }

    private static class AccountInfoResultReceiver implements BankResultReceiver.ResultReceiverCallBack<Integer> {
        private WeakReference<MainActivity> activityRef;
        public AccountInfoResultReceiver(MainActivity activity){
            activityRef = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void onSuccess(Integer data) {
            if(activityRef != null && activityRef.get() != null) {
                activityRef.get().label.setText("Your balance: "+data);
            }
        }

        @Override
        public void onError(Exception exception) {
            activityRef.get().showMessage("Account info failed");
        }
    }
}
