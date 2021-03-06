package com.joyplus.tv.utils;

import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.tv.App;
import com.joyplus.tv.Constant;
import com.joyplus.tv.R;
import com.joyplus.tv.Service.Return.ReturnTVBangDanList;
import com.joyplus.tv.Service.Return.ReturnTops;
import com.joyplus.tv.Service.Return.ReturnUserFavorities;
import com.joyplus.tv.Service.Return.ReturnUserPlayHistories;
import com.joyplus.tv.entity.HotItemInfo;
import com.joyplus.tv.entity.MovieItemData;
import com.joyplus.tv.entity.ReturnFilterMovieSearch;
import com.joyplus.utils.Log;

public class UtilTools implements JieMianConstant, BangDanConstant {

	private static final String TAG = "UtilTools";
	
	public static final String ACTION_PLAY_END_MAIN = "action_play_end_main";
	public static final String ACTION_PLAY_END_HISTORY = "action_play_end_history";
	
	public static final String TV_SETTING_XML = "tv_setting_xml";
	public static final String TV_ADKEY_CONFIG_XML = "tv_adkey_config_xml";
	public static final String TV_VIP_LOGIN_XML = "tv_vip_login_xml";

	/**
	 * 用来统计用户点击播放视屏后正常跳转的次数 有可能跳转到播放器，也有可能跳转到浏览器
	 * 
	 * 数据从服务器上获取
	 * 
	 * @param aq
	 * @param prod_id
	 * @param prod_name
	 * @param prod_subname
	 * @param pro_type
	 */
	public static void StatisticsClicksShow(AQuery aq, App app, String prod_id,
			String prod_name, String prod_subname, int pro_type) {
		Log.i(TAG, "StatisticsClicksShow--->");

		String url = Constant.BASE_URL + "program/recordPlay";

		Map<String, Object> params = new HashMap<String, Object>();
		// params.put("client","android");
		// params.put("version", "0.9.9");
		// params.put("app_key", Constant.APPKEY);// required string //
		// 申请应用时分配的AppKey。

		params.put("prod_id", prod_id);// required string // 视频id

		params.put("prod_name", prod_name);// required // string 视频名字

		params.put("prod_subname", prod_subname);// required // string 视频的集数
													// 电影的subname为空

		params.put("prod_type", pro_type);// required int 视频类别
											// 1：电影，2：电视剧，3：综艺，4：视频

		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.SetHeader(app.getHeaders());
		cb.params(params).url(url).type(JSONObject.class);

		aq.ajax(cb);
	}

	public static ScaleAnimation getOutScaleAnimation() {

		ScaleAnimation outScaleAnimation = new ScaleAnimation(
				OUT_ANIMATION_FROM_X, OUT_ANIMATION_TO_X, OUT_ANIMATION_FROM_Y,
				OUT_ANIMATION_TO_Y, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);

		outScaleAnimation.setDuration(80);
		outScaleAnimation.setFillAfter(false);

		return outScaleAnimation;
	}

	public static ScaleAnimation getInScaleAnimation() {

		ScaleAnimation inScaleAnimation = new ScaleAnimation(
				IN_ANIMATION_FROM_X, IN_ANIMATION_TO_X, IN_ANIMATION_FROM_Y,
				IN_ANIMATION_TO_Y, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		inScaleAnimation.setDuration(80);
		inScaleAnimation.setFillAfter(false);

		return inScaleAnimation;
	}

	public static TranslateAnimation getTranslateAnimation(View v) {

		TranslateAnimation translateAnimation = new TranslateAnimation(
				v.getX(), v.getX(), v.getY(), v.getY() - 200);
		translateAnimation.setDuration(150);
		translateAnimation.setFillAfter(false);

		return translateAnimation;
	}

	public static String getUserId(Context c) {
		SharedPreferences sharedata = c.getSharedPreferences("userData", 0);
		return sharedata.getString("userId", null);
	}

	public static String MD5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	public static String formateZongyi(String curEpisode,Context context) {

		if (curEpisode != null && !curEpisode.equals("")
				&& !curEpisode.equals("0")) {

			if (isNumber(curEpisode)) {

				if (curEpisode.length() > 2 && curEpisode.length() <= 9) {

					return context.getString(R.string.zongyi_gengxinzhi,curEpisode);
				}
			}
		}

		return "";
	}

	public static boolean isNumber(String str) {
		java.util.regex.Pattern pattern = java.util.regex.Pattern
				.compile("[0-9]*");
		java.util.regex.Matcher match = pattern.matcher(str);
		if (match.matches() == false) {
			return false;
		} else {
			return true;
		}
	}

	public static void clearList(List list) {

		if (list != null && !list.isEmpty()) {

			list.clear();
		}
	}

	public static final String EMPTY = "EMPTY";

	public static List<MovieItemData> returnFilterMovieSearch_TVJson(String json)
			throws JsonParseException, JsonMappingException, IOException {

		if (json == null || json.equals("")) {

			return new ArrayList<MovieItemData>();
		}
		ObjectMapper mapper = new ObjectMapper();

		ReturnFilterMovieSearch result = mapper.readValue(json.toString(),
				ReturnFilterMovieSearch.class);

		List<MovieItemData> list = new ArrayList<MovieItemData>();

		// Log.i(TAG, "returnFilterMovieSearch_TVJson-->" +

		for (int i = 0; i < result.results.length; i++) {

			MovieItemData movieItemData = new MovieItemData();
			movieItemData.setMovieName(result.results[i].prod_name);
			String bigPicUrl = result.results[i].big_prod_pic_url;
			if (bigPicUrl == null || bigPicUrl.equals("")
					|| bigPicUrl.equals(EMPTY)) {

				bigPicUrl = result.results[i].prod_pic_url;
			}
			movieItemData.setMoviePicUrl(bigPicUrl);
			movieItemData.setMovieScore(result.results[i].score);
			movieItemData.setMovieID(result.results[i].prod_id);
			movieItemData.setMovieDuration(result.results[i].duration);
			movieItemData.setMovieCurEpisode(result.results[i].cur_episode);
			movieItemData.setMovieMaxEpisode(result.results[i].max_episode);
			movieItemData.setMovieProType(result.results[i].prod_type);

			movieItemData.setStars(result.results[i].star);
			movieItemData.setDirectors(result.results[i].director);
			movieItemData.setSummary(result.results[i].prod_sumary);
			movieItemData.setSupport_num(result.results[i].support_num);
			movieItemData.setFavority_num(result.results[i].favority_num);
			movieItemData.setDefinition(result.results[i].definition);
			list.add(movieItemData);
		}

		return list;

	}

	public static List<MovieItemData> returnTopsJson(String json)
			throws JsonParseException, JsonMappingException, IOException {

		if (json == null || json.equals("")) {

			return new ArrayList<MovieItemData>();
		}
		ObjectMapper mapper = new ObjectMapper();

		ReturnTops result = mapper.readValue(json.toString(), ReturnTops.class);

		List<MovieItemData> list = new ArrayList<MovieItemData>();

		for (int i = 0; i < result.tops.length; i++) {
			MovieItemData movieItemData = new MovieItemData();
			movieItemData.setMovieName(result.tops[i].name);
			movieItemData.setMovieID(result.tops[i].id);
			movieItemData.setMovieProType(result.tops[i].prod_type);
			String bigPicUrl = result.tops[i].big_pic_url;
			if (bigPicUrl == null || bigPicUrl.equals("")
					|| bigPicUrl.equals(EMPTY)) {

				bigPicUrl = result.tops[i].pic_url;
			}
			movieItemData.setMoviePicUrl(bigPicUrl);
			movieItemData.setNum(result.tops[i].num);
			movieItemData.setMovieProType(result.tops[i].prod_type);
			// yuedanInfo.content = result.tops[i].content;
			list.add(movieItemData);

		}

		return list;
	}

	public static List<MovieItemData> returnTVBangDanList_YueDanListJson(
			String json) throws JsonParseException, JsonMappingException,
			IOException {

		if (json == null || json.equals("")) {

			return new ArrayList<MovieItemData>();
		}
		ObjectMapper mapper = new ObjectMapper();

		ReturnTVBangDanList result = mapper.readValue(json,
				ReturnTVBangDanList.class);

		List<MovieItemData> list = new ArrayList<MovieItemData>();

		for (int i = 0; i < result.items.length; i++) {

			MovieItemData movieItemData = new MovieItemData();
			movieItemData.setMovieName(result.items[i].prod_name);
			String bigPicUrl = result.items[i].big_prod_pic_url;
			if (bigPicUrl == null || bigPicUrl.equals("")
					|| bigPicUrl.equals(EMPTY)) {

				bigPicUrl = result.items[i].prod_pic_url;
			}
			movieItemData.setMoviePicUrl(bigPicUrl);
			movieItemData.setMovieScore(result.items[i].score);
			movieItemData.setMovieID(result.items[i].prod_id);
			movieItemData.setMovieCurEpisode(result.items[i].cur_episode);
			movieItemData.setMovieMaxEpisode(result.items[i].max_episode);
			movieItemData.setMovieProType(result.items[i].prod_type);

			movieItemData.setStars(result.items[i].stars);
			movieItemData.setDirectors(result.items[i].directors);
			movieItemData.setSupport_num(result.items[i].support_num);
			movieItemData.setFavority_num(result.items[i].favority_num);
			movieItemData.setMovieDuration(result.items[i].duration);
			movieItemData.setDefinition(result.items[i].definition);
			list.add(movieItemData);
		}

		return list;
	}

	// public static List<MovieItemData> returnUserFavoritiesJson(String json)
	// throws JsonParseException, JsonMappingException, IOException {
	//
	// if(json == null || json.equals("")) {
	//
	// return new ArrayList<MovieItemData>();
	// }
	// ObjectMapper mapper = new ObjectMapper();
	//
	// ReturnUserFavorities result = mapper.readValue(json.toString(),
	// ReturnUserFavorities.class);
	// List<MovieItemData> list = new ArrayList<MovieItemData>();
	// for(int i=0; i<result.favorities.length; i++){
	// MovieItemData movieItemData = new MovieItemData();
	// movieItemData.setMovieID(result.favorities[i].content_id);
	// movieItemData.setMovieName(result.favorities[i].content_name);
	// movieItemData.setMovieProType(result.favorities[i].content_type);
	// String bigPicUrl = result.favorities[i].big_content_pic_url;
	// if(bigPicUrl == null || bigPicUrl.equals("")
	// ||bigPicUrl.equals(EMPTY)) {
	//
	// bigPicUrl = result.favorities[i].content_pic_url;
	// }
	// movieItemData.setMoviePicUrl(bigPicUrl);
	// movieItemData.setMovieScore(result.favorities[i].score);
	// list.add(movieItemData);
	// }
	//
	// return list;
	// }

	public static List<HotItemInfo> returnUserFavoritiesJson(String json)
			throws JsonParseException, JsonMappingException, IOException {

		if (json == null || json.equals("")) {

			return new ArrayList<HotItemInfo>();
		}
		ObjectMapper mapper = new ObjectMapper();

		ReturnUserFavorities result = mapper.readValue(json.toString(),
				ReturnUserFavorities.class);
		List<HotItemInfo> list = new ArrayList<HotItemInfo>();
		for (int i = 0; i < result.favorities.length; i++) {
			HotItemInfo item = new HotItemInfo();
			// item.id = result.favorities[i].id;
			item.prod_id = result.favorities[i].content_id;
			item.prod_name = result.favorities[i].content_name;
			item.prod_type = result.favorities[i].content_type;
			// item.prod_pic_url = result.favorities[i].big_content_pic_url;
			String bigPicUrl = result.favorities[i].big_content_pic_url;
			if (bigPicUrl == null || bigPicUrl.equals("")
					|| bigPicUrl.equals(UtilTools.EMPTY)) {

				bigPicUrl = result.favorities[i].content_pic_url;
			}
			item.prod_pic_url = bigPicUrl;
			item.stars = result.favorities[i].stars;
			item.directors = result.favorities[i].directors;
			item.favority_num = result.favorities[i].favority_num;
			item.support_num = result.favorities[i].support_num;
			item.publish_date = result.favorities[i].publish_date;
			item.score = result.favorities[i].score;
			item.area = result.favorities[i].area;
			item.duration = result.favorities[i].duration;
			item.cur_episode = result.favorities[i].cur_episode;
			item.max_episode = result.favorities[i].max_episode;
			list.add(item);
		}

		return list;
	}

	// 存储历史数据
	public static List<HotItemInfo> returnUserHistoryJson(String json)
			throws JsonParseException, JsonMappingException, IOException {

		if (json == null || json.equals("")) {

			return new ArrayList<HotItemInfo>();
		}
		ObjectMapper mapper = new ObjectMapper();

		ReturnUserPlayHistories result = mapper.readValue(json.toString(),
				ReturnUserPlayHistories.class);
		List<HotItemInfo> list = new ArrayList<HotItemInfo>();
		for (int i = 0; i < result.histories.length; i++) {
			HotItemInfo item = new HotItemInfo();
			item.id = result.histories[i].id;
			item.prod_id = result.histories[i].prod_id;
			item.prod_name = result.histories[i].prod_name;
			item.prod_type = result.histories[i].prod_type;
			String bigPicUrl = result.histories[i].big_prod_pic_url;
			if (bigPicUrl == null || bigPicUrl.equals("")
					|| bigPicUrl.equals(UtilTools.EMPTY)) {

				bigPicUrl = result.histories[i].prod_pic_url;
			}
			item.prod_pic_url = bigPicUrl;
			item.stars = result.histories[i].stars;
			item.directors = result.histories[i].directors;
			item.favority_num = result.histories[i].favority_num;
			item.support_num = result.histories[i].support_num;
			item.publish_date = result.histories[i].publish_date;
			item.score = result.histories[i].score;
			item.area = result.histories[i].area;
			item.cur_episode = result.histories[i].cur_episode;
			item.max_episode = result.histories[i].max_episode;
			item.definition = result.histories[i].definition;
			item.prod_summary = result.histories[i].prod_summary;
			item.duration = result.histories[i].duration;
			item.video_url = result.histories[i].video_url;
			item.playback_time = result.histories[i].playback_time;
			item.prod_subname = result.histories[i].prod_subname;
			item.play_type = result.histories[i].play_type;
			list.add(item);
		}

		return list;
	}

	public static String getLastBandNotice(String lastTime) {
		// long last = Long.valueOf(lastTime);
		// long time = System.currentTimeMillis()-last;
		// if(time/(24*60*60*1000)>0){
		// int day = (int) (time/(24*60*60*1000));
		// return day+"天前";
		// }else if(time/(60*60*1000)>0){
		// int hour = (int) (time/(60*60*1000));
		// return hour+"小时前";
		// }else if(time/(60*1000)>0){
		// int minute = (int) (time/(60*1000));
		// return minute+"分钟前";
		// }else if(time/1000>0){
		// int second = (int) (time/1000);
		// return second+"秒前";
		// }else{
		// return "1秒前";
		// }
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日\tHH时mm分");
		// 例如：cc_time=1291778220
		long lcc_time = Long.valueOf(lastTime);
		re_StrTime = sdf.format(new Date(lcc_time));
		return re_StrTime;
	}

	/**
	 * 
	 * @param networkList
	 * @param dbList
	 *            此list中HotItemInfo数据不全
	 * @return
	 */
	public static List<HotItemInfo> sameList4NetWork(
			List<HotItemInfo> networkList, List<HotItemInfo> dbList) {

		List<HotItemInfo> list = new ArrayList<HotItemInfo>();

		for (HotItemInfo netWorkInfo : networkList) {

			for (HotItemInfo dbInfo : dbList) {

				if (netWorkInfo.prod_id.equals(dbInfo.prod_id)) {

					list.add(netWorkInfo);
				}
			}
		}

		return list;
	}

	/**
	 * 
	 * @param networkList
	 * @param dbList
	 *            此list中HotItemInfo数据不全
	 * @return
	 */
	public static List<HotItemInfo> sameList4DB(List<HotItemInfo> networkList,
			List<HotItemInfo> dbList) {

		List<HotItemInfo> list = new ArrayList<HotItemInfo>();

		for (HotItemInfo netWorkInfo : networkList) {

			for (HotItemInfo dbInfo : dbList) {

				if (netWorkInfo.prod_id.equals(dbInfo.prod_id)) {

					list.add(dbInfo);
				}
			}
		}

		return list;
	}

	public static List<HotItemInfo> differentList4NetWork(
			List<HotItemInfo> networkList, List<HotItemInfo> dbList) {

		List<HotItemInfo> sameList = sameList4NetWork(networkList, dbList);
		List<HotItemInfo> list = new ArrayList<HotItemInfo>();

		for (HotItemInfo netWorkInfo : networkList) {

			boolean isSame = false;

			for (HotItemInfo sameInfo : sameList) {

				if (netWorkInfo.prod_id.equals(sameInfo.prod_id)) {

					isSame = true;
				}
			}

			if (!isSame) {

				list.add(netWorkInfo);
			}
		}

		return list;
	}

	public static List<MovieItemData> getLists4TwoList(
			List<MovieItemData> list1, List<MovieItemData> list2) {
		List<MovieItemData> list = new ArrayList<MovieItemData>();
		if (list1 != null) {
			list.addAll(list1);
			if (list2 != null) {
				for (MovieItemData movieItemData2 : list2) {
					boolean isSame = false;
					for(MovieItemData movieItemData : list1) {
						if(movieItemData.getMovieID().
								equals(movieItemData2.getMovieID())) {
							isSame = true;
							break;
						}
					}
					if(!isSame) {
						list.add(movieItemData2);
					}
				}
			}
		}

		return list;
	}

	// 一进入到能够收藏的界面list排序顺序 收藏集合，将要填充的集合，文字集合（5个），其他集合
	// 判断当前位置是为不可见
	public static boolean isPostionEmpty(int position, int shoucangNum) {
		return (position > shoucangNum -1 && position < getFirstPositionQitaTitle(shoucangNum));
	}

	public static boolean isPostionShowText(int position, int shoucangNum) {
		return getFirstPositionQitaTitle(shoucangNum) == position;
	}

	public static boolean isPositionShowQitaTitleBar(int position, int shoucangNum) {
		int min = getFirstPositionQitaTitle(shoucangNum);
		return position >= min && position < min + 5 ? true:false;
	}

	//需要填充的数目
	public static int tianchongEmptyItem(int shoucangNum) {
		return getFirstPositionQitaTitle(shoucangNum) - shoucangNum + 5;
	}
	
	//其他文字位置起始点，对应于gridview position
	public static int getFirstPositionQitaTitle(int shoucangNum) {
		int chu = (shoucangNum - 1) / 5;
		return (chu + 1) * 5;
	}
	
	public static int stepToFirstInThisRow(int position) {
		int chu = position / 5;
		return chu * 5;
	}

	public static int string2Int(String str) {

		if (str == null || str.equals("")) {

			return 0;
		}

		try {
			return Integer.valueOf(str.trim());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	public static boolean is48TimeClock(Context context) {

		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);

		return sp.getBoolean("is48TimeClock", false);
	}

	public static void set48TimeClock(Context context, boolean is48TimeClock) {

		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("is48TimeClock", is48TimeClock);
		editor.commit();
	}

	/**
	 * 闹钟保存的id
	 * 
	 * @param context
	 * @param userId
	 */
	public static void setCurrentUserId(Context context, String userId) {

		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("currentUserId", userId);
		editor.commit();
	}

	/**
	 * 闹钟绑定的id
	 * 
	 * @param context
	 * @return
	 */
	public static String getCurrentUserId(Context context) {

		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);

		return sp.getString("currentUserId", "");
	}

	public static void setCancelShoucangProId(Context context, String proId) {

		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("cancelShoucangProId", proId);
		editor.commit();
	}

	public static String getCancelShoucangProId(Context context) {

		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);

		return sp.getString("cancelShoucangProId", "");
	}
	
	/**
	 * 存储广告是否显示
	 * @param context
	 * @param isShow
	 */
	public static void setIsShowAd(Context context,boolean isShow) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("isShowAd", isShow);
		editor.commit();
	}
	
	public static boolean getIsShowAd(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);
		
		return sp.getBoolean("isShowAd", false);
	}
	
	public static void setIsJoyPlusApp(Context context,boolean isJoyPlusApp) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("isJoyPlusApp", isJoyPlusApp);
		editor.commit();
	}
	
	public static boolean getIsJoyPlusApp(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);
		
		return sp.getBoolean("isJoyPlusApp", true);
	}
	
	public static void setLogoUrl(Context context , String logoUrl) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("logoUrl", logoUrl);
		editor.commit();
	}
	
	public static String getLogoUrl(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);
		
		return sp.getString("logoUrl","");
	}
	
	public static void setDisclaimerVisible(Context context,boolean isDisclaimerVisible){
		
		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("isDisclaimerVisible", isDisclaimerVisible);
		editor.commit();
	}
	
	public static boolean getDisclaimerVisible(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);
		
		return sp.getBoolean("isDisclaimerVisible",false);
	}
	
	public static void setWpBaiduLocalParse(Context context,boolean isDWpBaiduLocalParse){
		
		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("isDWpBaiduLocalParse", isDWpBaiduLocalParse);
		editor.commit();
	}
	
	public static boolean getWpBaiduLocalParse(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);
		
		return sp.getBoolean("isDWpBaiduLocalParse",false);
	}
	
	public static void setWpBaiduLocalParseInit(Context context,boolean isDWpBaiduLocalParseInit){
		
		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("isDWpBaiduLocalParseInit", isDWpBaiduLocalParseInit);
		editor.commit();
	}
	
	public static boolean getWpBaiduLocalParseInit(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);
		
		return sp.getBoolean("isDWpBaiduLocalParseInit",false);
	}
	
	public static void setP2PMD5(Context context , String p2pMd5) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("p2pMd5", p2pMd5);
		editor.commit();
	}
	
	public static String getP2PMD5(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_SETTING_XML,
				Context.MODE_PRIVATE);
		
		return sp.getString("p2pMd5","");
	}
	
	public  static boolean isSame4Str(String str1, String str2){
		if(str1==null||str2==null){
			return false;
		}
		if(str1.equalsIgnoreCase(str2)){
			return true;
		}else{
			if(str1.trim().equalsIgnoreCase(str2.trim())){
				return true;
			}else{
				if(str1.length()>=str2.length()){
					if(str1.startsWith(str2)){
						return true;
					}else{
						return false;
					}
				}else{
					if(str2.startsWith(str1)){
						return true;
					}else{
						return false;
					}
				}
			}
		}
	}
	
	public static void recycleBitmap(Bitmap bitmap) {
		
		if(bitmap != null) {
			
			if(!bitmap.isRecycled()) {
				
				bitmap.recycle();
			}
			
			bitmap = null;
		}
	}
	
	public static void setLogoPic(Context context,AQuery aq,ImageView iv) {
		
		if(Constant.isJoyPlus) {
			
			iv.setImageResource(R.drawable.logo);
		} else {//否则不是本身应用
			
			String url = UtilTools.getLogoUrl(context);
			
			if(url != null && !url.equals("")) {
				
				aq.id(iv).image(url, false, true, 0,R.drawable.logo_custom);
			} else {
				
				iv.setImageResource(R.drawable.logo_custom);
			}
		}
	}
	
	public static void setLoadingAdvID(Context context,String loadingAdvID) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_ADKEY_CONFIG_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("loadingAdvID", loadingAdvID);
		editor.commit();
	}
	
	public static String getLoadingAdvID(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_ADKEY_CONFIG_XML,
				Context.MODE_PRIVATE);
		
		return sp.getString("loadingAdvID", "");
	}
	
	public static void setMainAdvID(Context context,String mainAdvID) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_ADKEY_CONFIG_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("mainAdvID", mainAdvID);
		editor.commit();
	}
	
	public static String getMainAdvID(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_ADKEY_CONFIG_XML,
				Context.MODE_PRIVATE);
		
		return sp.getString("mainAdvID", "");
	}
	
	public static void setPlayerAdvID(Context context,String playerAdvID) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_ADKEY_CONFIG_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("playerAdvID", playerAdvID);
		editor.commit();
	}
	
	public static String getPlayerAdvID(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_ADKEY_CONFIG_XML,
				Context.MODE_PRIVATE);
		
		return sp.getString("playerAdvID", "");
	}
	
	public static void setReplenishAdvID(Context context,String replenishAdvID){
		
		SharedPreferences sp = context.getSharedPreferences(TV_ADKEY_CONFIG_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("replenishAdvID", replenishAdvID);
		editor.commit();
	}
	
	public static String getReplenishAdvID(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_ADKEY_CONFIG_XML,
				Context.MODE_PRIVATE);
		
		return sp.getString("replenishAdvID", "");
	}
	
	public static void setUmengChannel(Context context,String channel) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_ADKEY_CONFIG_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("channel", channel);
		editor.commit();
	}
	
	public static String getUmengChannel(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(TV_ADKEY_CONFIG_XML,
				Context.MODE_PRIVATE);
		
		return sp.getString("channel", "");
	}
	
	public static void setVIPUserName(Context context,String userName){
		SharedPreferences sp = context.getSharedPreferences(TV_VIP_LOGIN_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("userName", userName);
		editor.commit();
	}
	
	public static String getVIPUserName(Context context){
		SharedPreferences sp = context.getSharedPreferences(TV_VIP_LOGIN_XML,
				Context.MODE_PRIVATE);
		return sp.getString("userName", "");
	}
	
	public static void setVIPPasswd(Context context,String passWd){
		SharedPreferences sp = context.getSharedPreferences(TV_VIP_LOGIN_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("passWd", passWd);
		editor.commit();
	}
	
	public static String getVIPPasswd(Context context){
		SharedPreferences sp = context.getSharedPreferences(TV_VIP_LOGIN_XML,
				Context.MODE_PRIVATE);
		return sp.getString("passWd", "");
	}
	
	public static void setVIP_ID(Context context,String vipID){
		SharedPreferences sp = context.getSharedPreferences(TV_VIP_LOGIN_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("vipID", vipID);
		editor.commit();
	}
	
	public static String getVIP_ID(Context context){
		SharedPreferences sp = context.getSharedPreferences(TV_VIP_LOGIN_XML,
				Context.MODE_PRIVATE);
		return sp.getString("vipID", "");
	}
	
	public static void setVIP_NickName(Context context,String nickName){
		SharedPreferences sp = context.getSharedPreferences(TV_VIP_LOGIN_XML,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("nickName", nickName);
		editor.commit();
	}
	
	public static String getVIP_NickName(Context context){
		SharedPreferences sp = context.getSharedPreferences(TV_VIP_LOGIN_XML,
				Context.MODE_PRIVATE);
		return sp.getString("nickName", "");
	}
	
	public static void saveVIPNameAndPasswd(Context context,String userName,String passWd){
		setVIPUserName(context, userName);
		setVIPPasswd(context, passWd);
	}
	
	public static void saveVIPNickNameAndID(Context context,String nickName,String vipID){
		setVIP_NickName(context, nickName);
		setVIP_ID(context, vipID);
	}
	
	public static void clearVIPNameAndPasswd(Context context){
		setVIPUserName(context, "");
		setVIPPasswd(context, "");
	}
	
	public static void clearVIPNickNameAndID(Context context){
		setVIP_NickName(context, "");
		setVIP_ID(context, "");
	}
	
	public static void clearVIPData(Context context){
		clearVIPNameAndPasswd(context);
		clearVIPNickNameAndID(context);
	}
	
}
