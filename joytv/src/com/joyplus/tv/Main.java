package com.joyplus.tv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.tv.Adapters.MainHotItemAdapter;
import com.joyplus.tv.Adapters.MainYueDanItemAdapter;
import com.joyplus.tv.Service.Return.ReturnMainHot;
import com.joyplus.tv.Service.Return.ReturnTops;
import com.joyplus.tv.Service.Return.ReturnUserPlayHistories;
import com.joyplus.tv.Video.VideoPlayerActivity;
import com.joyplus.tv.entity.HotItemInfo;
import com.joyplus.tv.entity.ShiPinInfo;
import com.joyplus.tv.entity.YueDanInfo;
import com.joyplus.tv.ui.CustomGallery;
import com.joyplus.tv.ui.MyScrollLayout;
import com.joyplus.tv.ui.MyScrollLayout.OnViewChangeListener;
import com.umeng.analytics.MobclickAgent;


public class Main extends Activity implements OnItemSelectedListener, OnItemClickListener{
	private String TAG = "Main";
	private App app;
	private AQuery aq;
	private Map<String, String> headers;
	private Handler handler = new Handler();
	ObjectMapper mapper = new ObjectMapper();
	private List<View> hot_contentViews = new ArrayList<View>();
	private List<View> yuedan_contentViews = new ArrayList<View>();
	private List<HotItemInfo> hot_list = new ArrayList<HotItemInfo>();
	private List<YueDanInfo> yuedan_list = new ArrayList<YueDanInfo>();
	private int isHotLoadedFlag = 0;
	private int isYueDanLoadedFlag = 0;

//	private int[] resouces = {
//			R.drawable.test1,
//			R.drawable.test2,
//			R.drawable.test3,
//			R.drawable.test4,
//			R.drawable.test5,
//			R.drawable.test6
//		};
	
	private int [] resouces_lib_nomal = {
			R.drawable.movie_normal,
			R.drawable.episode_normal,
			R.drawable.cartoon_normal,
			R.drawable.variety_normal,
			R.drawable.search_normal
		};
	private int [] resouces_lib_active = {
			R.drawable.movie_active,
			R.drawable.episode_active,
			R.drawable.cartoon_active,
			R.drawable.variety_active,
			R.drawable.search_active,
		};
	
	private int [] resouces_my_nomal = {
			R.drawable.follow_normal,
			R.drawable.recent_normal,
			R.drawable.down_normal,
			R.drawable.system_normal
		};
	private int [] resouces_my_active = {
			R.drawable.follow_active,
			R.drawable.recent_active,
			R.drawable.down_active,
			R.drawable.system_active,
		};
	
	private CustomGallery gallery1;
	private float density;
	private int displayWith;
	private MyScrollLayout titleGroup;
	private FrameLayout itemFram;
	private ImageView highlightImageView;
	private ImageView playIcon;
	private LinearLayout contentLayout;
	private TextView noticeView;
	
//	private View hotView;
//	private View yeuDanView;
	private View kuView;
	private View myView;
	
//	private TextView hot_name_tv;
//	private TextView hot_score_tv;
//	private TextView hot_directors_tv;
//	private TextView hot_starts_tv;
//	private TextView hot_introduce_tv;
	
	private Map<Integer, Integer> indexCaces = new HashMap<Integer, Integer>();
	
	private Animation alpha_appear;
	private Animation alpha_disappear;
	
	private TranslateAnimation leftTranslateAnimationStep1;
	private TranslateAnimation leftTranslateAnimationStep2;
	private TranslateAnimation rightTranslateAnimationStep1;
	private TranslateAnimation rightTranslateAnimationStep2;
	
//	private Handler mHandler = new Handler();
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        gallery1 = (CustomGallery) findViewById(R.id.gallery);
        contentLayout = (LinearLayout) findViewById(R.id.contentlayout);
        noticeView = (TextView) findViewById(R.id.notice_text);
        titleGroup = (MyScrollLayout) findViewById(R.id.group);
        titleGroup.setFocusable(false);
        titleGroup.setFocusableInTouchMode(false);
        
        
        
        kuView = LayoutInflater.from(Main.this).inflate(R.layout.layout_lib, null);
        myView = LayoutInflater.from(Main.this).inflate(R.layout.layout_my, null);
        titleGroup.SetOnViewChangeListener(new OnViewChangeListener() {
			
			@Override
			public void OnViewChange(int index) {
				// TODO Auto-generated method stub
				gallery1.startAnimation(alpha_appear);
				switch (index) {
				case 1:
					if(isHotLoadedFlag == 2){
						contentLayout.removeAllViews();
						View hotView = hot_contentViews.get(indexCaces.get(index));
						hotView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
						contentLayout.startAnimation(alpha_appear);
						contentLayout.addView(hotView);
						gallery1.setAdapter(new MainHotItemAdapter(Main.this, hot_list));
						if(indexCaces.get(index) == null){
							gallery1.setSelection(0);
						}else{
							gallery1.setSelection(indexCaces.get(index));
						}
//						changeContent(0);
						ImageView img = (ImageView) gallery1.findViewWithTag(hot_list.get(gallery1.getSelectedItemPosition()).prod_pic_url);
						if(img != null){
							if(img.getDrawable()!=null){
								highlightImageView.setImageDrawable(img.getDrawable());
							}else{
								aq.id(highlightImageView).image(hot_list.get(gallery1.getSelectedItemPosition()).prod_pic_url,true,true);
							}
						}else{
							aq.id(highlightImageView).image(hot_list.get(gallery1.getSelectedItemPosition()).prod_pic_url,true,true);
						}
//						aq.id(highlightImageView).image(hot_list.get(gallery1.getSelectedItemPosition()).prod_pic_url);
//						noticeView.setText(gallery1.getSelectedItemPosition()+1 + "/" + hot_list.size());
						
					}else{
						hot_list.clear();
						hot_contentViews.clear();
						gallery1.setAdapter(null);
						contentLayout.removeAllViews();
						getHistoryServiceData();
						getHotServiceData();
					}
					playIcon.setVisibility(View.VISIBLE);
					break;
				case 2:
					playIcon.setVisibility(View.INVISIBLE);
					if(isYueDanLoadedFlag==2){
						View yeuDanView = yuedan_contentViews.get(indexCaces.get(index));
						contentLayout.removeAllViews();
						yeuDanView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
						contentLayout.startAnimation(alpha_appear);
						contentLayout.addView(yeuDanView);
						gallery1.setAdapter(new MainYueDanItemAdapter(Main.this, yuedan_list));
						if(indexCaces.get(index) == null){
							gallery1.setSelection(0);
						}else{
							gallery1.setSelection(indexCaces.get(index));
						}
						ImageView img2 = (ImageView) gallery1.findViewWithTag(yuedan_list.get(gallery1.getSelectedItemPosition()).pic_url);
						if(img2 != null){
							if(img2.getDrawable()!=null){
								highlightImageView.setImageDrawable(img2.getDrawable());
							}else{
								aq.id(highlightImageView).image(yuedan_list.get(gallery1.getSelectedItemPosition()).pic_url,true,true);
							}
						}else{
							aq.id(highlightImageView).image(yuedan_list.get(gallery1.getSelectedItemPosition()).pic_url,true,true);
						}
						noticeView.setText(gallery1.getSelectedItemPosition()+1 + "/" + yuedan_list.size());
					}else{
						yuedan_list.clear();
						yuedan_contentViews.clear();
						gallery1.setAdapter(null);
						contentLayout.removeAllViews();
						itemFram.setVisibility(View.INVISIBLE);
						getMovieYueDanServiceData();
						getTVYueDanServiceData();
					}
					
					break;
				case 3:
					contentLayout.removeAllViews();
					kuView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
					contentLayout.startAnimation(alpha_appear);
					contentLayout.addView(kuView);
					gallery1.setAdapter(new LibAdapter());
					if(indexCaces.get(index) == null){
						gallery1.setSelection(0);
					}else{
						gallery1.setSelection(indexCaces.get(index));
					}
					playIcon.setVisibility(View.INVISIBLE);
					highlightImageView.setImageResource(resouces_lib_active[gallery1.getSelectedItemPosition()]);
					noticeView.setText(gallery1.getSelectedItemPosition()+1 + "/" + resouces_lib_active.length);
					break;
				case 4:
					contentLayout.removeAllViews();
					myView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
					contentLayout.startAnimation(alpha_appear);
					contentLayout.addView(myView);
					gallery1.setAdapter(new MyAdapter());
					if(indexCaces.get(index) == null){
						gallery1.setSelection(0);
					}else{
						gallery1.setSelection(indexCaces.get(index));
					}
					playIcon.setVisibility(View.INVISIBLE);
					highlightImageView.setImageResource(resouces_my_active[gallery1.getSelectedItemPosition()]);
					noticeView.setText(gallery1.getSelectedItemPosition() +1+ "/" + resouces_my_active.length);
					break;
				}
			}
		});
        itemFram = (FrameLayout) findViewById(R.id.itemFram);
//        clock = (ClockTextView) findViewById(R.id.clock);
        highlightImageView = (ImageView) findViewById(R.id.highlight_img);
        playIcon = (ImageView) findViewById(R.id.play_icon);
//        MarginLayoutParams mlp = (MarginLayoutParams) gallery1.getLayoutParams();
        DisplayMetrics metrics = new DisplayMetrics();
        density = metrics.density;
        Display display = getWindowManager().getDefaultDisplay();
        displayWith = display.getWidth();
//        Toast.makeText(this, "widthPixels = " + display.get, 100).show();
//        Toast.makeText(this, "topMargin = " + mlp.topMargin, 100).show();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        mlp.setMargins(-displayWith+displayWith/2, 
//                       mlp.topMargin, 
//                       mlp.rightMargin, 
//                       mlp.bottomMargin
//        );
        gallery1.setAdapter(new MainHotItemAdapter(Main.this, hot_list));
//        gallery1.setCallbackDuringFling(false);
        gallery1.setOnItemSelectedListener(this);
        gallery1.setOnItemClickListener(this);
        gallery1.setSelection(1);
        aq = new AQuery(this);
        MarginLayoutParams mlp2 = (MarginLayoutParams) titleGroup.getLayoutParams();
        mlp2.setMargins((displayWith-40)/6+15, 
		        		mlp2.topMargin, 
		        		mlp2.rightMargin, 
		        		mlp2.bottomMargin);
        MarginLayoutParams mlp3 = (MarginLayoutParams) noticeView.getLayoutParams();
        mlp3.setMargins((displayWith-40)/6+15, 
        		mlp3.topMargin, 
        		mlp3.rightMargin, 
        		mlp3.bottomMargin);
//        MarginLayoutParams mlp4 = (MarginLayoutParams) contentLayout.getLayoutParams();
//        mlp4.setMargins((displayWith-40)/6+15, 
//        		mlp4.topMargin, 
//        		mlp4.rightMargin, 
//        		mlp4.bottomMargin);
        LayoutParams param = itemFram.getLayoutParams();
        param.height = 2*displayWith/9+3;
        param.width = displayWith/6+3;
        itemFram.setVisibility(View.INVISIBLE);
        alpha_appear = AnimationUtils.loadAnimation(this, R.anim.alpha_appear);
        alpha_disappear = AnimationUtils.loadAnimation(this, R.anim.alpha_disappear);
		app = (App) getApplicationContext();
		headers = new HashMap<String, String>();
		headers.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			headers.put("version", pInfo.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		headers.put("app_key", Constant.APPKEY);
		headers.put("client","android");
		app.setHeaders(headers);

		if(!Constant.TestEnv)
			ReadLocalAppKey();
 
		checkLogin();
		getHotServiceData();
		getHistoryServiceData();
    }
    
	@Override
	protected void onDestroy() {
		if (aq != null)
			aq.dismiss();
		super.onDestroy();
	}
	public void ReadLocalAppKey() {
	// online 获取APPKEY
	MobclickAgent.updateOnlineConfig(this);
	String OnLine_Appkey = MobclickAgent.getConfigParams(this, "APPKEY");
	if (OnLine_Appkey != null && OnLine_Appkey.length() >0) {
		Constant.APPKEY = OnLine_Appkey;
		headers.remove("app_key");
		headers.put("app_key", OnLine_Appkey);
		app.setHeaders(headers);
	}
}
public boolean checkLogin() {
	String UserInfo = null;
	UserInfo = app.GetServiceData("UserInfo");
	if (UserInfo == null) {
		// 1. 在客户端生成一个唯一的UUID
		String macAddress = null;
		WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = (null == wifiMgr ? null : wifiMgr
				.getConnectionInfo());
		if (info != null) {
			macAddress = info.getMacAddress();
			// 2. 通过调用 service account/generateUIID把UUID传递到服务器
			String url = Constant.BASE_URL + "account/generateUIID";

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("uiid", macAddress);
			params.put("device_type", "Android");

			AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
			cb.header("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
			cb.header("app_key", Constant.APPKEY);

			cb.params(params).url(url).type(JSONObject.class)
					.weakHandler(this, "CallServiceResult");
			aq.ajax(cb);
		}
	} else {
		JSONObject json;
		try {
			json = new JSONObject(UserInfo);
			app.UserID = json.getString("user_id").trim();
			headers.put("user_id", app.UserID);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	return false;
}
    
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1,final int arg2,
			long arg3) {
		final int positon1 = gallery1.getSelectedItemPosition();
		if(leftTranslateAnimationStep2 == null){
			leftTranslateAnimationStep2  = new TranslateAnimation(itemFram.getLeft()+itemFram.getWidth()/5-itemFram.getWidth(), 
					itemFram.getLeft()-itemFram.getWidth(), 
					0, 
					0);
			leftTranslateAnimationStep2.setDuration(250);
			leftTranslateAnimationStep2.setInterpolator(new AccelerateInterpolator(0.1f));
		}
		if(rightTranslateAnimationStep2 == null){
			rightTranslateAnimationStep2  = new TranslateAnimation(itemFram.getLeft()-itemFram.getWidth()/4-itemFram.getWidth(), 
					itemFram.getLeft()-itemFram.getWidth(), 
					0, 
					0);
			rightTranslateAnimationStep2.setDuration(250);
			rightTranslateAnimationStep2.setInterpolator(new AccelerateInterpolator(0.1f));
		}
		switch (titleGroup.getSelectedTitleIndex()) {
		case 1:
			if(arg2<hot_list.size()&&arg2>=0){
				ImageView img = (ImageView) gallery1.findViewWithTag(hot_list.get(arg2).prod_pic_url);
				if(img != null){
					if(img.getDrawable()!=null){
						highlightImageView.setImageDrawable(img.getDrawable());
					}else{
						aq.id(highlightImageView).image(hot_list.get(arg2).prod_pic_url,true,true);
					}
				}else{
					aq.id(highlightImageView).image(hot_list.get(arg2).prod_pic_url,true,true);
				}
				noticeView.setText(arg2+ 1 + "/" + hot_list.size());
				handler.removeCallbacksAndMessages(null);
				handler.postDelayed((new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Log.d(TAG, "index = " + arg2 + "lengh = " +  hot_contentViews.size());
						contentLayout.removeAllViews();
						View hotView = hot_contentViews.get(arg2);
						hotView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
						contentLayout.startAnimation(alpha_appear);
						contentLayout.addView(hotView);
					}
				}),280);
			}
			break;
		case 2:
			if(arg2<yuedan_list.size()&&arg2>=0){
				if("0".equals(yuedan_list.get(arg2).prod_type)){
					if("-1".equals(yuedan_list.get(arg2).id)){
						highlightImageView.setImageResource(R.drawable.more_movie_active);
					}else if("-2".equals(yuedan_list.get(arg2).id)){
						highlightImageView.setImageResource(R.drawable.more_episode_active);
					}
				}else{
					ImageView img = (ImageView) gallery1.findViewWithTag(yuedan_list.get(arg2).pic_url);
					if(img != null){
						if(img.getDrawable()!=null){
							highlightImageView.setImageDrawable(img.getDrawable());
						}else{
							aq.id(highlightImageView).image(yuedan_list.get(arg2).pic_url,true,true);
						}
					}else{
						aq.id(highlightImageView).image(yuedan_list.get(arg2).pic_url,true,true);
					}
					noticeView.setText(arg2+ 1 + "/" + yuedan_list.size());
//					handler.removeCallbacksAndMessages(null);
//					handler.postDelayed((new Runnable() {
//						
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							Log.d(TAG, "index = " + arg2 + "lengh = " +  hot_contentViews.size());
//							contentLayout.removeAllViews();
//							View hotView = hot_contentViews.get(arg2);
//							hotView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
//							contentLayout.startAnimation(alpha_appear);
//							contentLayout.addView(hotView);
//						}
//					}),280);
				}
			}
				
			break;
		case 3:
			if(positon1<resouces_lib_active.length-1){
				highlightImageView.setImageResource(resouces_lib_active[positon1+1]);
				itemFram.startAnimation(leftTranslateAnimationStep2);
				noticeView.setText(positon1+2 + "/" + resouces_lib_active.length);
			}
			break;
		case 4:
			if(positon1<resouces_my_active.length-1){
				highlightImageView.setImageResource(resouces_my_active[positon1+1]);
				itemFram.startAnimation(leftTranslateAnimationStep2);
				noticeView.setText(positon1+2 + "/" + resouces_my_active.length);
			}
			break;
		
		}
		if(indexCaces.get(titleGroup.getSelectedTitleIndex())!=null&&indexCaces.get(titleGroup.getSelectedTitleIndex())<arg2){
			itemFram.startAnimation(leftTranslateAnimationStep2);
		}
		if(indexCaces.get(titleGroup.getSelectedTitleIndex())!=null&&indexCaces.get(titleGroup.getSelectedTitleIndex())>arg2){
			itemFram.startAnimation(rightTranslateAnimationStep2);
		} 
		indexCaces.put(titleGroup.getSelectedTitleIndex(), arg2);
		// TODO Auto-generated method stub
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
//		Toast.makeText(this, "key code = " + keyCode, 100).show();
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			titleGroup.selectPreTitle();
			return true;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			titleGroup.selectNextTitle();
			return true;
		
		case KeyEvent.KEYCODE_DPAD_RIGHT:
//			final int positon1 = gallery1.getSelectedItemPosition();
//			if(leftTranslateAnimationStep2 == null){
//				leftTranslateAnimationStep2  = new TranslateAnimation(itemFram.getLeft()+itemFram.getWidth()/5-itemFram.getWidth(), 
//						itemFram.getLeft()-itemFram.getWidth(), 
//						0, 
//						0);
//				leftTranslateAnimationStep2.setDuration(250);
//				leftTranslateAnimationStep2.setInterpolator(new AccelerateInterpolator(0.1f));
//			}
//			switch (titleGroup.getSelectedTitleIndex()) {
//			case 1:
//				if(positon1<hot_list.size()-1){
////					aq.id(highlightImageView).image(hot_list.get(positon1+1).prod_pic_url,true,true);
////					itemFram.startAnimation(leftTranslateAnimationStep2);
////					noticeView.setText(positon1+2 + "/" + hot_list.size());
//					changeContent(1);
//				}
//				break;
//			case 2:
//				if(positon1<resouces_lib_active.length-1){
////					highlightImageView.setImageResource(resouces_lib_active[positon1+1]);
////					itemFram.startAnimation(leftTranslateAnimationStep2);
////					noticeView.setText(positon1+2 + "/" + resouces_lib_active.length);
//					changeContent(1);
//				}
//				break;
//			case 3:
//				if(positon1<resouces_lib_active.length-1){
//					highlightImageView.setImageResource(resouces_lib_active[positon1+1]);
//					itemFram.startAnimation(leftTranslateAnimationStep2);
//					noticeView.setText(positon1+2 + "/" + resouces_lib_active.length);
//				}
//				break;
//			case 4:
//				if(positon1<resouces_my_active.length-1){
//					highlightImageView.setImageResource(resouces_my_active[positon1+1]);
//					itemFram.startAnimation(leftTranslateAnimationStep2);
//					noticeView.setText(positon1+2 + "/" + resouces_my_active.length);
//				}
//				break;
//			
//			}
			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
//			
//			
//			final int positon2 = gallery1.getSelectedItemPosition();
//			if(rightTranslateAnimationStep2 == null){
//				rightTranslateAnimationStep2  = new TranslateAnimation(itemFram.getLeft()-itemFram.getWidth()/4-itemFram.getWidth(), 
//						itemFram.getLeft()-itemFram.getWidth(), 
//						0, 
//						0);
//				rightTranslateAnimationStep2.setDuration(250);
//				rightTranslateAnimationStep2.setInterpolator(new AccelerateInterpolator(0.1f));
//			}
//			
//			
//		switch (titleGroup.getSelectedTitleIndex()) {
//		case 1:
////			if(positon2>0){
////				highlightImageView.setImageResource(resouces[positon2]);
////				itemFram.startAnimation(rightTranslateAnimationStep2);
////				noticeView.setText(positon2 + "/" + resouces.length);
////			}
//			if(positon2>0){
////				aq.id(highlightImageView).image(hot_list.get(positon2-1).prod_pic_url,true,true);
////				itemFram.startAnimation(rightTranslateAnimationStep2);
////				noticeView.setText(positon2 + "/" + hot_list.size());
//				changeContent(-1);
//			}
//			break;
//		case 2:
//			if(positon2>0){
////				highlightImageView.setImageResource(resouces_lib_active[positon2-1]);
////				itemFram.startAnimation(leftTranslateAnimationStep2);
////				noticeView.setText(positon2 + "/" + resouces_lib_active.length);
//				changeContent(-1);
//			}
//			break;
//		case 3:
//			if(positon2>0){
//				highlightImageView.setImageResource(resouces_lib_active[positon2-1]);
//				itemFram.startAnimation(leftTranslateAnimationStep2);
//				noticeView.setText(positon2 + "/" + resouces_lib_active.length);
//			}
//			break;
//		case 4:
//			if(positon2>0){
//				highlightImageView.setImageResource(resouces_my_active[positon2-1]);
//				itemFram.startAnimation(leftTranslateAnimationStep2);
//				noticeView.setText(positon2 + "/" + resouces_my_active.length);
//			}
//			break;
//		
//		}
//			
			return true;
		}
		return false;
	}
	
	class LibAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return resouces_lib_nomal.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView == null){
				ImageView view = new ImageView(Main.this);
				view.setPadding(20, 30, 20, 20);
				view.setLayoutParams(new android.widget.Gallery.LayoutParams(displayWith/6,2*displayWith/9));
				convertView = view;
			}
			((ImageView)convertView).setImageResource(resouces_lib_nomal[position]);
			return convertView;
		}
    	
    }
	
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return resouces_my_nomal.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView == null){
				ImageView view = new ImageView(Main.this);
				view.setPadding(20, 30, 20, 20);
				view.setLayoutParams(new android.widget.Gallery.LayoutParams(displayWith/6,2*displayWith/9));
				convertView = view;
			}
			((ImageView)convertView).setImageResource(resouces_my_nomal[position]);
			return convertView;
		}
    	
    }



	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "item click index = " + titleGroup.getSelectedTitleIndex()+"[" + index + "]", 100).show();
		switch (titleGroup.getSelectedTitleIndex()) {
		case 1:
			String str0 = "1004602";
			String str1 = "贤妻";
//			String str2 = "http://115.238.173.139:80/play/7B5E398971A46DD67535DBC0A7CD770D27036204.mp4";
			String str2 = "http://221.130.179.68/29/51/83/kingsoft/movie/657D7A4D3E64B8532E41CADC9D9CAEE5-huobiteren1.mp4?crypt=74086bf4aa7f2e300&b=800&gn=132&nc=1&bf=30&p2p=1&video_type=mp4&check=0&tm=1364180400&key=9d5cb867ee5942a6739730a0241725fd&opck=1&lgn=letv&proxy=3702889383&cipi=2026698610&tsnp=1&tag=ios&tag=kingsoft&sign=coopdown&realext=.mp4&test=m3u8";

			Intent intent = new Intent(this, VideoPlayerActivity.class);
			intent.putExtra("prod_url", str2);
			intent.putExtra("title", str1);

			startActivity(intent);

//			startActivity(new Intent(this,VideoPlayerActivity.class));
			break;
		case 2:
			switch (index) {
			case 0:
				startActivity(new Intent(this,ShowMovieActivity.class));
				break;
			case 1:
				startActivity(new Intent(this,ShowTVActivity.class));
				break;
			case 2:
				startActivity(new Intent(this,ShowDongManActivity.class));
				break;
			case 3:
				startActivity(new Intent(this,ShowZongYiActivity.class));
				break;
			case 4:
				startActivity(new Intent(this,ShowSearchActivity.class));
				break;
			}
			break;
		case 3:
			switch (index) {
			case 0:
				startActivity(new Intent(this,ShowMovieActivity.class));
				break;
			case 1:
				startActivity(new Intent(this,ShowTVActivity.class));
				break;
			case 2:
				startActivity(new Intent(this,ShowDongManActivity.class));
				break;
			case 3:
				startActivity(new Intent(this,ShowZongYiActivity.class));
				break;
			case 4:
				startActivity(new Intent(this,ShowSearchActivity.class));
				break;
			}
			break;
		case 4:
			switch (index) {
			case 0:
//				startActivity(new Intent(this,ShowMovieActivity.class));
				break;
			case 1:
				startActivity(new Intent(this,HistoryActivity.class));
				break;
			case 2:
//				startActivity(new Intent(this,ShowDongManActivity.class));
				break;
			case 3:
//				startActivity(new Intent(this,ShowZongYiActivity.class));
				break;
			case 4:
//				startActivity(new Intent(this,ShowSearchActivity.class));
				break;
			}
			break;
		default:
			break;
		}
	}
	
	
	public void getHotServiceData() {
		String url = Constant.BASE_URL + "tv_net_top" +"?page_num=1&page_size=10";

//		String url = Constant.BASE_URL;
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "initHotData");

		cb.SetHeader(app.getHeaders());
		aq.ajax(cb);
	}
	
	public void getHistoryServiceData() {
		String url = Constant.BASE_URL + "user/playHistories" +"?page_num=1&page_size=10&userid=4742";

//		String url = Constant.BASE_URL;
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "initHistoryData");

		cb.SetHeader(app.getHeaders());
		aq.ajax(cb);
	}
	
	public void getMovieYueDanServiceData() {
		String url = Constant.BASE_URL + "tops" +"?page_num=1&page_size=3&topic_type=1";

//		String url = Constant.BASE_URL;
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "initYueDanData");

		cb.SetHeader(app.getHeaders());
		aq.ajax(cb);
	}
	public void getTVYueDanServiceData() {
		String url = Constant.BASE_URL + "tops" +"?page_num=1&page_size=3&topic_type=2";
		
//		String url = Constant.BASE_URL;
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "initYueDanData");
		
		cb.SetHeader(app.getHeaders());
		aq.ajax(cb);
	}
	
	public synchronized void initYueDanData(String url, JSONObject json, AjaxStatus status){
		if (status.getCode() == AjaxStatus.NETWORK_ERROR)  {
//			aq.id(R.id.ProgressText).invisible();
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			return;
		}
		Log.d(TAG, "history data = " + json.toString());
		try {
			ReturnTops result  = mapper.readValue(json.toString(), ReturnTops.class);
			for(int i=0; i<result.tops.length; i++){
				YueDanInfo yuedanInfo = new YueDanInfo();
				List<ShiPinInfo> shiPinInfos = new ArrayList<ShiPinInfo>();
				yuedanInfo.name = result.tops[i].name;
				yuedanInfo.id = result.tops[i].id;
				yuedanInfo.prod_type = result.tops[i].prod_type;
				yuedanInfo.pic_url = result.tops[i].pic_url;
				yuedanInfo.num = result.tops[i].num;
				yuedanInfo.content = result.tops[i].content;
				for (int j = 0; j < result.tops[i].items.length; j++) {
					ShiPinInfo shipinInfo = new ShiPinInfo();
					shipinInfo.setArea(result.tops[i].items[j].area);
					shipinInfo.setBig_prod_pic_url(result.tops[i].items[j].big_prod_pic_url);
					shipinInfo.setCur_episode(result.tops[i].items[j].cur_episode);
//					shipinInfo.setCur_item_name(result.tops[i].items[j].cur_i);
					shipinInfo.setDefinition(result.tops[i].items[j].definition);
					shipinInfo.setDirectors(result.tops[i].items[j].directors);
					shipinInfo.setDuration(result.tops[i].items[j].duration);
					shipinInfo.setFavority_num(result.tops[i].items[j].favority_num);
					shipinInfo.setId(result.tops[i].items[j].id);
					shipinInfo.setMax_episode(result.tops[i].items[j].max_episode);
					shipinInfo.setProd_id(result.tops[i].items[j].prod_id);
					shipinInfo.setProd_name(result.tops[i].items[j].prod_name);
					shipinInfo.setProd_pic_url(result.tops[i].items[j].prod_pic_url);
					shipinInfo.setProd_type(result.tops[i].items[j].prod_type);
					shipinInfo.setPublish_date(result.tops[i].items[j].publish_date);
					shipinInfo.setScore(result.tops[i].items[j].score);
					shipinInfo.setStars(result.tops[i].items[j].stars);
					shipinInfo.setSupport_num(result.tops[i].items[j].support_num);
					shiPinInfos.add(shipinInfo);
				}
				yuedanInfo.shiPinList = shiPinInfos;
				if("1".equals(yuedanInfo.prod_type)){
					yuedan_list.add(i,yuedanInfo);
					View yueDanView = LayoutInflater.from(Main.this).inflate(R.layout.layout_list, null);
					
				}else{
					yuedan_list.add(yuedanInfo);
				}
			}
			if(isYueDanLoadedFlag==1){
				isYueDanLoadedFlag = 2;
			}else{
				isYueDanLoadedFlag = 1;
			}
			if(isYueDanLoadedFlag ==2){
				YueDanInfo yuedanInfo1 = new YueDanInfo();
				yuedanInfo1.prod_type = "0";   
				yuedanInfo1.id = "-1";
				yuedan_list.add(yuedanInfo1);
				YueDanInfo yuedanInfo2 = new YueDanInfo();
				yuedanInfo2.prod_type = "0";
				yuedanInfo2.id = "-2";
				yuedan_list.add(yuedanInfo2);
//				itemFram.setVisibility(View.VISIBLE);
				itemFram.setVisibility(View.VISIBLE);
				gallery1.setAdapter(new MainYueDanItemAdapter(Main.this, yuedan_list));
				gallery1.setSelection(0);
				return ;
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void initHistoryData(String url, JSONObject json, AjaxStatus status){
		if (status.getCode() == AjaxStatus.NETWORK_ERROR)  {
//			aq.id(R.id.ProgressText).invisible();
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			return;
		}
		Log.d(TAG, "history data = " + json.toString());
		try {
			ReturnUserPlayHistories result  = mapper.readValue(json.toString(), ReturnUserPlayHistories.class);
			HotItemInfo item  =  new HotItemInfo();
			if(result.histories.length == 0){
				if(isHotLoadedFlag == 1){
					isHotLoadedFlag = 2;	
					itemFram.setVisibility(View.VISIBLE);
					gallery1.setAdapter(new MainHotItemAdapter(Main.this, hot_list));
					gallery1.setSelection(1);
				}else{
					isHotLoadedFlag = 1;
				}
				return ;
			}
			item.type = 0;
			item.id = result.histories[0].id;
			item.prod_id = result.histories[0].prod_id;
			item.prod_name = result.histories[0].prod_name;
			item.prod_type = result.histories[0].prod_type;
			item.prod_pic_url = result.histories[0].prod_pic_url;
			item.stars = result.histories[0].stars;
			item.directors = result.histories[0].directors;
			item.favority_num = result.histories[0].favority_num;
			item.support_num = result.histories[0].support_num;
			item.publish_date = result.histories[0].publish_date;
			item.score = result.histories[0].score;
			item.area = result.histories[0].area;
			item.cur_episode = result.histories[0].cur_episode;
			item.definition = result.histories[0].definition;
			item.prod_summary = result.histories[0].prod_summary;
			item.duration = result.histories[0].duration;
			item.playback_time = result.histories[0].playback_time;
			hot_list.add(0,item);
			View hotView = LayoutInflater.from(Main.this).inflate(R.layout.layout_hot, null);
			TextView hot_name_tv = (TextView) hotView.findViewById(R.id.hot_content_name);
			TextView hot_score_tv = (TextView) hotView.findViewById(R.id.hot_content_score);
			TextView hot_directors_tv = (TextView) hotView.findViewById(R.id.hot_content_directors);
			TextView hot_starts_tv = (TextView) hotView.findViewById(R.id.hot_content_stars);
			TextView hot_introduce_tv = (TextView) hotView.findViewById(R.id.hot_content_introduce);
			
			hot_name_tv.setText(item.prod_name);
			hot_score_tv.setText(item.score);
			hot_directors_tv.setText(item.directors);
			hot_starts_tv.setText(item.stars);
			hot_introduce_tv.setText(item.prod_summary);
			hot_contentViews.add(0,hotView);
			Log.d(TAG, "lengh = " + hot_contentViews.size());
			if(isHotLoadedFlag ==1 ){
				itemFram.setVisibility(View.VISIBLE);
				gallery1.setAdapter(new MainHotItemAdapter(Main.this, hot_list));
				gallery1.setSelection(1);
				isHotLoadedFlag = 2;
				return;
			}
			isHotLoadedFlag = 1;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void initHotData(String url, JSONObject json, AjaxStatus status){
		if (status.getCode() == AjaxStatus.NETWORK_ERROR)  {
//			aq.id(R.id.ProgressText).invisible();
			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			return;
		}
		try {
			Log.d(TAG, json.toString());
			ReturnMainHot result  = mapper.readValue(json.toString(), ReturnMainHot.class);
//			hot_list.clear();
			for(int i=0; i <result.items.length; i++){
				HotItemInfo item  =  new HotItemInfo();
				item.type = 1;
				item.id = result.items[i].id;
				item.prod_id = result.items[i].prod_id;
				item.prod_name = result.items[i].prod_name;
				item.prod_type = result.items[i].prod_type;
				item.prod_pic_url = result.items[i].prod_pic_url;
				item.stars = result.items[i].stars;
				item.directors = result.items[i].directors;
				item.favority_num = result.items[i].favority_num;
				item.support_num = result.items[i].support_num;
				item.publish_date = result.items[i].publish_date;
				item.score = result.items[i].score;
				item.area = result.items[i].area;
				item.cur_episode = result.items[i].cur_episode;
				item.definition = result.items[i].definition;
				item.prod_summary = result.items[i].prod_summary;
				item.duration = result.items[i].duration;
				item.playback_time = "";
				hot_list.add(item);
				View hotView = LayoutInflater.from(Main.this).inflate(R.layout.layout_hot, null);
				TextView hot_name_tv = (TextView) hotView.findViewById(R.id.hot_content_name);
				TextView hot_score_tv = (TextView) hotView.findViewById(R.id.hot_content_score);
				TextView hot_directors_tv = (TextView) hotView.findViewById(R.id.hot_content_directors);
				TextView hot_starts_tv = (TextView) hotView.findViewById(R.id.hot_content_stars);
				TextView hot_introduce_tv = (TextView) hotView.findViewById(R.id.hot_content_introduce);
				
				hot_name_tv.setText(item.prod_name);
				hot_score_tv.setText(item.score);
				hot_directors_tv.setText(item.directors);
				hot_starts_tv.setText(item.stars);
				hot_introduce_tv.setText(item.prod_summary);
				hot_contentViews.add(hotView);
			}
//			Log.d
			
			if(isHotLoadedFlag == 1){
				itemFram.setVisibility(View.VISIBLE);
				gallery1.setAdapter(new MainHotItemAdapter(Main.this, hot_list));
				gallery1.setSelection(1);
				isHotLoadedFlag = 2;
				return ;
			}
			isHotLoadedFlag = 1;
//			aq.id(highlightImageView).image(hot_list.get(gallery1.getSelectedItemPosition()).prod_pic_url,true,true);
//			noticeView.setText(gallery1.getSelectedItemPosition()+1 + "/" + hot_list.size());
//			changeContent(0);
//			hot_list.add(arg0);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
//	private void changeContent1(int dx){
//		final int positon = gallery1.getSelectedItemPosition();
//		switch (titleGroup.getSelectedTitleIndex()) {
//		case 1:
//			
////			hot_name_tv = (TextView) hotView.findViewById(R.id.hot_content_name);
////	        hot_score_tv = (TextView) hotView.findViewById(R.id.hot_content_score);
////	        hot_directors_tv = (TextView) hotView.findViewById(R.id.hot_content_directors);
////	        hot_starts_tv = (TextView) hotView.findViewById(R.id.hot_content_stars);
////	        hot_introduce_tv = (TextView) hotView.findViewById(R.id.hot_content_introduce);
////			Log.d(TAG, "------------------------");
//			
////			aq.id(highlightImageView).image(hot_list.get(positon+dx).prod_pic_url,true,true); 
//			
//			ImageView img = (ImageView) gallery1.findViewWithTag(hot_list.get(positon+dx).prod_pic_url);
//			if(img != null){
//				highlightImageView.setImageDrawable(img.getDrawable());
//			}else{
//				aq.id(highlightImageView).image(hot_list.get(positon+dx).prod_pic_url,true,true);
//			}
////			if(dx==1){
////				itemFram.startAnimation(leftTranslateAnimationStep2);
////			}
////			if(dx==-1){
////				itemFram.startAnimation(rightTranslateAnimationStep2);
////			} 
//			
//			if(indexCaces.get(1)!=null&&indexCaces.get(1)<positon){
//				itemFram.startAnimation(leftTranslateAnimationStep2);
//			}
//			Log.d(TAG, "positon = " + positon + "laset = " + indexCaces.get(1));
//			if(indexCaces.get(1)!=null&&indexCaces.get(1)>positon){
//				itemFram.startAnimation(rightTranslateAnimationStep2);
//			} 
//			
////			itemFram.setVisibility(View.GONE);
//			handler.removeCallbacksAndMessages(null);
//			handler.postDelayed((new Runnable() {
//				
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					contentLayout.removeAllViews();
//					View hotView = hot_contentViews.get(positon);
//					hotView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
//					contentLayout.startAnimation(alpha_appear);
//					contentLayout.addView(hotView);
//				}
//			}),280);
//			
//			
////			contentLayout.startAnimation(alpha_appear);
////			hotView.invalidate();
//			
////			aq.id(R.id.hot_content_name).text(hot_list.get(positon+dx).prod_name);
////			aq.id(R.id.hot_content_score).text(hot_list.get(positon+dx).score);
////			aq.id(R.id.hot_content_directors).text(hot_list.get(positon+dx).directors);
////			aq.id(R.id.hot_content_stars).text(hot_list.get(positon+dx).stars);
////			aq.id(R.id.hot_content_introduce).text(hot_list.get(positon+dx).prod_summary);
//			break;
//		case 2:
//			highlightImageView.setImageResource(resouces_lib_active[positon]);
//			noticeView.setText(positon+1 + "/" + resouces_lib_active.length);
//			contentLayout.removeAllViews();
//			yeuDanView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
//			contentLayout.startAnimation(alpha_appear);
//			contentLayout.addView(yeuDanView);
//			break;
//		case 3:
//			highlightImageView.setImageResource(resouces_lib_active[positon]);
//			noticeView.setText(positon+1 + "/" + resouces_lib_active.length);
//			break;
//		case 4:
//			highlightImageView.setImageResource(resouces_my_active[positon]);
//			noticeView.setText(positon+1 + "/" + resouces_my_active.length);
//			break;
//		default:
//			break;
//		}
//	}

}
