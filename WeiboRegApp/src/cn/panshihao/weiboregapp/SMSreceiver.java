package cn.panshihao.weiboregapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSreceiver extends BroadcastReceiver {

	
	 public static final String TAG = "SMSReceiver";

	 public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		if(intent.getAction().equals(SMS_RECEIVED_ACTION)){
			
			SmsMessage[] messages = getMessagesFromIntent(intent);

	           for (SmsMessage message : messages)

	           {

	              Log.i(TAG, message.getOriginatingAddress() + " : " +

	                  message.getDisplayOriginatingAddress() + " : " +

	                  message.getDisplayMessageBody() + " : " +

	                  message.getTimestampMillis());

	           }
			
		}
		
		
	}
	
	public final SmsMessage[] getMessagesFromIntent(Intent intent)

    {

        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");

        byte[][] pduObjs = new byte[messages.length][];

 

        for (int i = 0; i < messages.length; i++)

        {

            pduObjs[i] = (byte[]) messages[i];

        }

        byte[][] pdus = new byte[pduObjs.length][];

        int pduCount = pdus.length;

        SmsMessage[] msgs = new SmsMessage[pduCount];

        for (int i = 0; i < pduCount; i++)

        {

            pdus[i] = pduObjs[i];

            msgs[i] = SmsMessage.createFromPdu(pdus[i]);

        }

        return msgs;

    }

}
