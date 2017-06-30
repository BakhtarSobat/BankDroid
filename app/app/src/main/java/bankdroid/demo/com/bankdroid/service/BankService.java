package bankdroid.demo.com.bankdroid.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import bankdroid.demo.com.bankdroid.exception.FunctionalException;
import bankdroid.demo.com.bankdroid.receivers.BankResultReceiver;

public class BankService extends IntentService {
    private static int balance = 10;

    private enum Actions {
        BALANCE, DEPOSIT, WITHDRAW
    }
    private enum PARAM {
        AMOUNT, RESULT_RECEIVER
    }

    public BankService(String name) {
        super(name);
    }
    public BankService() {
        super(BankService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver resultReceiver = intent.getParcelableExtra(PARAM.RESULT_RECEIVER.name());

        if (intent != null) {
            final String action = intent.getAction();
            if (Actions.BALANCE.name().equals(action)) {
                handleRetreiveBalance(resultReceiver);
            } else if (Actions.DEPOSIT.name().equals(action)) {
                final int amount = intent.getIntExtra(PARAM.AMOUNT.name(), 0);
                handleDeposit(resultReceiver, amount);
            } else if (Actions.WITHDRAW.name().equals(action)) {
                final int amount = intent.getIntExtra(PARAM.AMOUNT.name(), 0);
                handleWithdraw(resultReceiver, amount);
            }
        }
    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleWithdraw(ResultReceiver resultReceiver, int amount) {
        Bundle bundle = new Bundle();
        int code;

        //Just add sleep to simulate network latency
        sleep(1000);

        if(balance < amount){
            code = BankResultReceiver.RESULT_CODE_ERROR;
            bundle.putSerializable(BankResultReceiver.PARAM_EXCEPTION, new FunctionalException("Not enough credit"));
        } else {
            code = BankResultReceiver.RESULT_CODE_OK;
            balance = balance - amount;
            bundle.putSerializable(BankResultReceiver.PARAM_RESULT, true);
        }
        if(resultReceiver != null){
            resultReceiver.send(code, bundle);
        }
    }


    private void handleDeposit(ResultReceiver resultReceiver, int amount) {
        Bundle bundle = new Bundle();
        int code;
        //Just add sleep to simulate network latency
        sleep(1000);
        if(amount < 0){
            code = BankResultReceiver.RESULT_CODE_ERROR;
            bundle.putSerializable(BankResultReceiver.PARAM_EXCEPTION, new FunctionalException("Negative amount"));
        } else {
            code = BankResultReceiver.RESULT_CODE_OK;
            balance = balance + amount;
            bundle.putSerializable(BankResultReceiver.PARAM_RESULT, true);
        }
        if(resultReceiver != null){
            resultReceiver.send(code, bundle);
        }

    }

    private void handleRetreiveBalance(ResultReceiver resultReceiver) {
        Bundle bundle = new Bundle();
        int code = BankResultReceiver.RESULT_CODE_OK;
        //Just add sleep to simulate network latency
        sleep(500);
        bundle.putSerializable(BankResultReceiver.PARAM_RESULT, balance);
        if(resultReceiver != null){
            resultReceiver.send(code, bundle);
        }

    }

    public static void startServiceForBalance(Context context, BankResultReceiver.ResultReceiverCallBack resultReceiverCallBack){
        BankResultReceiver bankResultReceiver = new BankResultReceiver(new Handler(context.getMainLooper()));
        bankResultReceiver.setReceiver(resultReceiverCallBack);

        Intent intent = new Intent(context, BankService.class);
        intent.setAction(Actions.BALANCE.name());
        intent.putExtra(PARAM.RESULT_RECEIVER.name(), bankResultReceiver);
        context.startService(intent);
    }

    public static void startServiceToDeposit(Context context, int amount,  BankResultReceiver.ResultReceiverCallBack resultReceiverCallBack){
        BankResultReceiver bankResultReceiver = new BankResultReceiver(new Handler(context.getMainLooper()));
        bankResultReceiver.setReceiver(resultReceiverCallBack);

        Intent intent = new Intent(context, BankService.class);
        intent.setAction(Actions.DEPOSIT.name());
        intent.putExtra(PARAM.AMOUNT.name(), amount);
        intent.putExtra(PARAM.RESULT_RECEIVER.name(), bankResultReceiver);
        context.startService(intent);
    }

    public static void startServiceToWithdraw(Context context, int amount, BankResultReceiver.ResultReceiverCallBack resultReceiverCallBack){
        BankResultReceiver bankResultReceiver = new BankResultReceiver(new Handler(context.getMainLooper()));
        bankResultReceiver.setReceiver(resultReceiverCallBack);

        Intent intent = new Intent(context, BankService.class);
        intent.setAction(Actions.WITHDRAW.name());
        intent.putExtra(PARAM.AMOUNT.name(), amount);
        intent.putExtra(PARAM.RESULT_RECEIVER.name(), bankResultReceiver);
        context.startService(intent);
    }

}
