package com.saulpower.fayeclient;

import java.net.URI;

import org.json.JSONException;
import org.json.JSONObject;

import com.joyplus.tv.Constant;
import com.joyplus.tv.ShowMovieActivity;
import com.joyplus.tv.ShowXiangqingMovie;
import com.joyplus.tv.StatisticsUtils;
import com.saulpower.fayeclient.FayeClient.FayeListener;

import android.R.integer;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Movie;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class FayeService extends Service implements FayeListener{

	private static final String TAG = "FayeService";
	
	public static final String SENDACTION = "chennel_send_message";
	public static final String RECIVEACTION = "chennel_receive_message";
	
	private String serverUrl;
	private String channel;
	private String phoneChannel;
	private SharedPreferences preferences;
	private FayeClient myClient;
//	private FayeClient phoneClient;
	private BroadcastReceiver receiver;
	private Handler handler = new Handler();

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		serverUrl = Constant.FAYESERVERURL;
		String macAdd = StatisticsUtils.getMacAdd(this);
		channel = Constant.FAYECHANNEL_TV_BASE + StatisticsUtils.MD5(macAdd);
		preferences = getSharedPreferences("userIdDate",0);
		IntentFilter filter = new IntentFilter(SENDACTION);
		receiver = new BroadcastReceiver(){
			@Override
	        public void onReceive(Context context, Intent intent) {
	                // TODO Auto-generated method stub
	        	if(SENDACTION.equals(intent.getAction())){
	        		String date = intent.getStringExtra("date");
	        		try {
						myClient.sendMessage(new JSONObject(date));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	                
	        }
		};
		registerReceiver(receiver, filter);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		URI url = URI.create(serverUrl);
		myClient = new FayeClient(handler, url, channel);
		myClient.setFayeListener(this);
		myClient.connectToServer(null);
		return super.onStartCommand(intent, flags, START_STICKY);
	}





	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connectedToServer() {
		// TODO Auto-generated method stub
		Log.d(TAG, "server connected----->");
		
	}

	@Override
	public void disconnectedFromServer() {
		// TODO Auto-generated method stub
		Log.w(TAG, "server disconnected!----->"); 
	}

	@Override
	public void subscribedToChannel(String subscription) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Channel subscribed success!----->" + subscription);
//		if(subscription.startsWith(Constant.FAYECHANNEL_MOBILE_BASE)){
//			JSONObject bandSuccessObj = new JSONObject();
//			try {
//				bandSuccessObj.put("push_type","32");
//				bandSuccessObj.put("result", "ok");
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			phoneClient.sendMessage(bandSuccessObj);
//		}
//		handler.postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				JSONObject obj  = new JSONObject();
//				try {
//					obj.put("date", "hello world");
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				myClient.sendMessage(obj);
//			}
//		}, 500);
	}

	@Override
	public void subscriptionFailedWithError(String error) {
		// TODO Auto-generated method stub
		Log.w(TAG, "Channel subscribed FailedWithError!----->" + error);
	}

	@Override
	public void messageReceived(JSONObject json) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Receive message:" + json.toString());
		try {
			int push_type = Integer.valueOf(json.getString("push_type")) ;
			String phoneID = null;
			Editor edit = preferences.edit();
			switch (push_type) {
			case 31://绑定
				phoneID = json.getString("user_id");
				if(!preferences.getBoolean("isBand", false)){
					Log.d(TAG, "phone id = " + phoneID);
					edit.putBoolean("isBand", true);
					edit.putString("phoneID", phoneID);
					edit.commit();
					//notify to UI 
					
					JSONObject bandSuccessObj = new JSONObject();
					bandSuccessObj.put("push_type","32");
					bandSuccessObj.put("user_id", phoneID);
					bandSuccessObj.put("result", "success");
					myClient.sendMessage(bandSuccessObj);
				}else{
					JSONObject bandSuccessObj = new JSONObject();
					bandSuccessObj.put("push_type","32");
					bandSuccessObj.put("user_id", preferences.getString("phoneID", "-1"));
					bandSuccessObj.put("result", "fail");
					myClient.sendMessage(bandSuccessObj);
				}
				
				break;
//			case 32://取消绑定
//				phoneID = json.getString("user_id");
//				if(!preferences.getBoolean("isBand", false)){
//					return ;//tv 端收到解除绑定 但是自己的状态为未绑定时不处理
//				}
//				if(phoneID.equals(preferences.getString("phoneID", "-1"))){
//					edit.putBoolean("isBand", false);
//					edit.putString("phoneID", "-1");
//					edit.commit();
//				}
//				//notify to UI 
//				JSONObject bandCancleObj = new JSONObject();
//				bandCancleObj.put("result", "ok"); 
//				bandCancleObj.put("push_type","33");
//				myClient.sendMessage(bandCancleObj);
//				break;
			case 33://取消绑定
				phoneID = json.getString("user_id");
				if(!preferences.getBoolean("isBand", false)){
					return ;//tv 端收到解除绑定 但是自己的状态为未绑定时不处理
				}else if(phoneID.equals(preferences.getString("phoneID", "-1"))){
					edit.putBoolean("isBand", false);
					edit.putString("phoneID", "-1");
					edit.commit();
				}
				//notify to UI 
				break;
			case 41://投影视频
				if(preferences.getBoolean("isBand", false)){
					Intent intent = new Intent(this,ShowXiangqingMovie.class);
//					intent.putExtra("ID", json.getString("prod_id"));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("ID", 1010460+"");
					startActivity(intent);
				}
			default:
				break;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}