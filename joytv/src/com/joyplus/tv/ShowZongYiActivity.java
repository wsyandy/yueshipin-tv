package com.joyplus.tv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.tv.Service.Return.ReturnTVBangDanList;
import com.joyplus.tv.entity.GridViewItemHodler;
import com.joyplus.tv.entity.MovieItemData;
import com.joyplus.tv.entity.ReturnFilterMovieSearch;
import com.joyplus.tv.ui.MyMovieGridView;
import com.joyplus.tv.ui.NavigateView;
import com.joyplus.tv.ui.NavigateView.OnResultListener;
import com.joyplus.tv.utils.BangDanKey;
import com.joyplus.tv.utils.ItemStateUtils;
import com.joyplus.tv.utils.JieMianConstant;
import com.joyplus.tv.utils.MyKeyEventKey;

public class ShowZongYiActivity extends Activity implements View.OnKeyListener,
MyKeyEventKey, BangDanKey, JieMianConstant, View.OnClickListener,
View.OnFocusChangeListener {

	private String TAG = "ShowZongYiActivity";
	private AQuery aq;
	private App app;

	private EditText searchEt;
	private MyMovieGridView dinashijuGv;

	private Button zuijinguankanBtn, zhuijushoucangBtn,
			mFenLeiBtn;
	
	private LinearLayout topLinearLayout;
	
	private View firstFloatView ;

	private View activeView;

	private boolean isSelectedItem = true;// GridView中参数是否真正初始化

	private int popWidth, popHeight;

	private boolean isGridViewUp = false;

	private int[] beforeFirstAndLastVible = { 0, 9 };

	private View beforeGvView = null;

	private ObjectMapper mapper = new ObjectMapper();

	private List<MovieItemData> movieList = new ArrayList<MovieItemData>();
	
	private int beforepostion = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_zongyi);

		app = (App) getApplication();
		aq = new AQuery(this);

		initView();
		initState();

		dinashijuGv.setAdapter(movieAdapter);
		
		String url = StatisticsUtils.getTopItemURL(TOP_ITEM_URL, 
				REBO_ZONGYI, 1 + "", 50 + "");
		getServiceData(url);// 进入电影界面时，全部分类电影显示获取焦点，并且显示数据
		dinashijuGv.setSelected(true);
		dinashijuGv.requestFocus();
		dinashijuGv.setSelection(0);
	}

	private void initView() {

		searchEt = (EditText) findViewById(R.id.et_search);
		mFenLeiBtn = (Button) findViewById(R.id.bt_quanbufenlei);
		dinashijuGv = (MyMovieGridView) findViewById(R.id.gv_movie_show);

		zuijinguankanBtn = (Button) findViewById(R.id.bt_zuijinguankan);
		zhuijushoucangBtn = (Button) findViewById(R.id.bt_zhuijushoucang);
		
		firstFloatView = findViewById(R.id.inclue_movie_show_item);
		
		topLinearLayout = (LinearLayout) findViewById(R.id.ll_show_movie_top);
		
		dinashijuGv.setNextFocusLeftId(R.id.bt_quanbufenlei);

		addListener();

	}
	
	private void initState() {

		activeView = mFenLeiBtn;

		ItemStateUtils.buttonToActiveState(getApplicationContext(), mFenLeiBtn);
		
		ItemStateUtils.setItemPadding(zuijinguankanBtn);
		ItemStateUtils.setItemPadding(zhuijushoucangBtn);
		ItemStateUtils.setItemPadding(mFenLeiBtn);
	}

	private void addListener() {


		zuijinguankanBtn.setOnKeyListener(this);
		zhuijushoucangBtn.setOnKeyListener(this);
		mFenLeiBtn.setOnKeyListener(this);

		zuijinguankanBtn.setOnClickListener(this);
		zhuijushoucangBtn.setOnClickListener(this);
		mFenLeiBtn.setOnClickListener(this);
		
		zuijinguankanBtn.setOnFocusChangeListener(this);
		zhuijushoucangBtn.setOnFocusChangeListener(this);
		mFenLeiBtn.setOnFocusChangeListener(this);
		
		dinashijuGv.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				int action = event.getAction();

				if (keyCode == KEY_UP) {

					isGridViewUp = true;
					// isGridViewDown = false;
				} else if (keyCode == KEY_DOWN) {

					isGridViewUp = false;
					// isGridViewDown = true;
				}
				if (action == KeyEvent.ACTION_UP) {
					if (keyCode == KEY_RIGHT) {

					}
					if (!isSelectedItem) {

						 if (keyCode == KEY_RIGHT) {
						 isSelectedItem = true;
						 dinashijuGv.setSelection(1);
						 } else if (keyCode == KEY_DOWN) {
						 isSelectedItem = true;
						 dinashijuGv.setSelection(5);
						
						 }
					}

				}
				return false;
			}
		});

		dinashijuGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ShowZongYiActivity.this,
						ShowXiangqingZongYi.class);
				intent.putExtra("ID", movieList.get(position).getMovieID());
				startActivity(intent);
			}
		});

		dinashijuGv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, final View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// if (BuildConfig.DEBUG)
				Log.i(TAG, "Positon:" + position + " View:" + view + 
						" beforGvView:" + beforeGvView );

				if (view == null) {

					isSelectedItem = false;
					return;
				}

				final float y = view.getY();

				boolean isSmoonthScroll = false;

				boolean isSameContent = position >= beforeFirstAndLastVible[0]
						&& position <= beforeFirstAndLastVible[1];
				if (position >= 5 && !isSameContent) {

					if (beforepostion >= beforeFirstAndLastVible[0]
							&& beforepostion <= beforeFirstAndLastVible[0] + 4) {

						if (isGridViewUp) {

							dinashijuGv.smoothScrollBy(-popHeight, 1000);
							isSmoonthScroll = true;
						}
					} else {

						if (!isGridViewUp) {

							dinashijuGv.smoothScrollBy(popHeight, 1000 * 2);
							isSmoonthScroll = true;

						}
					}

				}

				if (beforeGvView != null) {

					ItemStateUtils.viewOutAnimation(getApplicationContext(),
							beforeGvView);
				} else {

					ItemStateUtils.floatViewOutAnimaiton(firstFloatView);
				}

				ItemStateUtils.viewInAnimation(getApplicationContext(), view);

				int[] firstAndLastVisible = new int[2];
				firstAndLastVisible[0] = dinashijuGv.getFirstVisiblePosition();
				firstAndLastVisible[1] = dinashijuGv.getLastVisiblePosition();

				if (y == 0 || y - popHeight == 0) {// 顶部没有渐影

					beforeFirstAndLastVible = ItemStateUtils
							.reCaculateFirstAndLastVisbile(
									beforeFirstAndLastVible,
									firstAndLastVisible, isSmoonthScroll, false);

				} else {// 顶部有渐影

					beforeFirstAndLastVible = ItemStateUtils
							.reCaculateFirstAndLastVisbile(
									beforeFirstAndLastVible,
									firstAndLastVisible, isSmoonthScroll, true);

				}

				beforeGvView = view;
				beforepostion = position;

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

				isSelectedItem = false;
			}
		});

		dinashijuGv.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

				if (!hasFocus) {// 如果gridview没有获取焦点，把item中高亮取消

					if (beforeGvView != null) {

						ItemStateUtils.viewOutAnimation(
								getApplicationContext(), beforeGvView);
					} else {

						// firstFloatView.setVisibility(View.GONE);
						ItemStateUtils.floatViewOutAnimaiton(firstFloatView);
					}
				} else {

					dinashijuGv.setNextFocusLeftId(activeView.getId());

					if (beforeGvView != null) {

						ItemStateUtils.viewInAnimation(getApplicationContext(),
								beforeGvView);

					} else {
						initFirstFloatView();
					}
				}
			}
		});
		
		searchEt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Editable editable = searchEt.getText();
				String searchStr = editable.toString();

				if (searchStr != null && !searchStr.equals("")) {

					String url = StatisticsUtils.getSearchURL(SEARCH_URL,
							1 + "", 30 + "", searchStr);
//					getFilterServiceData(url, false);
				}
			}
		});

		searchEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus == true) {
					((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
							.showSoftInput(v, InputMethodManager.SHOW_FORCED);

				} else { // ie searchBoxEditText doesn't have focus
					((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(v.getWindowToken(), 0);

				}
			}
		});
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub

		if (hasFocus) {

			ItemStateUtils.viewToFocusState(getApplicationContext(), v);
		} else {

			ItemStateUtils.viewToOutFocusState(getApplicationContext(), v,
					activeView);
		}

	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		return false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (aq != null)
			aq.dismiss();
		super.onDestroy();
	}

	private PopupWindow popupWindow;
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		 Log.i("Yangzhg", "onClick");

			if(activeView == null) {
				
				activeView = mFenLeiBtn;
			}
			
			if(v.getId() == R.id.bt_quanbufenlei) {
				
				if(popupWindow ==null){
					NavigateView view = new NavigateView(this);
					int [] location = new int[2];
					mFenLeiBtn.getLocationOnScreen(location);
					view.Init(getResources().getStringArray(R.array.diqu_zongyi_fenlei),
							getResources().getStringArray(R.array.leixing_zongyi_fenlei), 
							getResources().getStringArray(R.array.shijian_dianying_fenlei), 
							location[0], 
							location[1],
							mFenLeiBtn.getWidth(), 
							mFenLeiBtn.getHeight(),
							new OnResultListener() {
								
								@Override
								public void onResult(View v, boolean isBack, String[] choice) {
									// TODO Auto-generated method stub
									if(isBack){
										popupWindow.dismiss();
									}else{
										if(popupWindow.isShowing()){
											popupWindow.dismiss();
											Toast.makeText(ShowZongYiActivity.this, "selected is " + choice[0] + ","+choice[1]+","+choice[2], Toast.LENGTH_LONG).show();
											String quanbu = getString(R.string.quanbu_name);
											String quanbufenlei = getString(R.string.quanbufenlei_name);
											mFenLeiBtn.setText(StatisticsUtils.getQuanBuFenLeiName(choice, quanbufenlei, quanbu));
											String url = StatisticsUtils.getFilterURL(FILTER_URL, 1+"", 50+"", 3+"") + 
													StatisticsUtils.getFileterURL3Param(choice, quanbu);
											Log.i(TAG, "POP--->URL:" + url);
												getFilterServiceData(url);
											
										}
									}
								}
							});
					view.setLayoutParams(new LayoutParams(0,0));
//					popupWindow = new PopupWindow(view, getWindowManager().getDefaultDisplay().getWidth(),
//					getWindowManager().getDefaultDisplay().getHeight(), true);
					int width = topLinearLayout.getWidth();
					int height = topLinearLayout.getHeight();
					popupWindow = new PopupWindow(view,width,height, true);
				}
				popupWindow.showAtLocation(mFenLeiBtn.getRootView(), Gravity.LEFT | Gravity.BOTTOM, 0, 0);
			}
			
			if(activeView.getId() == v.getId()) {
				
				return;
			}
			
			switch (v.getId()) {
			case R.id.bt_quanbufenlei:
				String url7 = StatisticsUtils.getTopItemURL(TOP_ITEM_URL, 
						REBO_ZONGYI, 1 + "", 50 + "");
				app.MyToast(aq.getContext(),"bt_quanbufenlei");
				getServiceData(url7);
				break;
			case R.id.bt_zuijinguankan:
				startActivity(new Intent(this, HistoryActivity.class));
				break;
			case R.id.bt_zhuijushoucang:
				startActivity(new Intent(this, ShowShoucangHistoryActivity.class));
				break;

			default:
				break;
			}
		 
			View tempView = ItemStateUtils.viewToActive(getApplicationContext(), v,
					activeView);

			if (tempView != null) {

				activeView = tempView;
			}

		
		beforeGvView = null;
	}
	
	private void getFilterServiceData(String url) {
		
		firstFloatView.setVisibility(View.INVISIBLE);
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "initFilterData");

		cb.SetHeader(app.getHeaders());
		aq.ajax(cb);
	}
	
	public void initFilterData(String url, JSONObject json, AjaxStatus status) {

		if (status.getCode() == AjaxStatus.NETWORK_ERROR) {

			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			return;
		}
		try {
			Log.d(TAG, json.toString());
			ReturnFilterMovieSearch result = mapper.readValue(json.toString(),
					ReturnFilterMovieSearch.class);
			// hot_list.clear();
			if(movieList != null && !movieList.isEmpty()) {
				
				movieList.clear();
			}
			for (int i = 0; i < result.results.length; i++) {

				MovieItemData movieItemData = new MovieItemData();
				movieItemData.setMovieName(result.results[i].prod_name);
				String bigPicUrl = result.results[i].big_prod_pic_url;
				if(bigPicUrl == null || bigPicUrl.equals("")) {
					
					bigPicUrl = result.results[i].prod_pic_url;
				}
				movieItemData.setMoviePicUrl(bigPicUrl);
				movieItemData.setMovieScore(result.results[i].score);
				movieItemData.setMovieID(result.results[i].prod_id);
				movieItemData.setMovieDuration(result.results[i].duration);
				movieItemData.setMovieCurEpisode(result.results[i].cur_episode);
				movieItemData.setMovieMaxEpisode(result.results[i].max_episode);
				movieList.add(movieItemData);
			}
			// Log.d

			movieAdapter.notifyDataSetChanged();
			beforeGvView = null;
			initFirstFloatView();
			dinashijuGv.setFocusable(true);
			dinashijuGv.setSelected(true);
			isSelectedItem = false;
			dinashijuGv.requestFocus();
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

	private void getServiceData(String url) {

		firstFloatView.setVisibility(View.INVISIBLE);
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).weakHandler(this, "initData");

		cb.SetHeader(app.getHeaders());
		aq.ajax(cb);
	}

	public void initData(String url, JSONObject json, AjaxStatus status) {

		if (status.getCode() == AjaxStatus.NETWORK_ERROR) {

			app.MyToast(aq.getContext(),
					getResources().getString(R.string.networknotwork));
			return;
		}
		try {
			Log.d(TAG, json.toString());
			ReturnTVBangDanList result = mapper.readValue(json.toString(),
					ReturnTVBangDanList.class);

			if(movieList != null && !movieList.isEmpty()) {
				
				movieList.clear();
			}
			for (int i = 0; i < result.items.length; i++) {

				MovieItemData movieItemData = new MovieItemData();
				movieItemData.setMovieName(result.items[i].prod_name);
				String bigPicUrl = result.items[i].big_prod_pic_url;
				if(bigPicUrl == null || bigPicUrl.equals("")) {
					
					bigPicUrl = result.items[i].prod_pic_url;
				}
				movieItemData.setMoviePicUrl(bigPicUrl);
				movieItemData.setMovieCurEpisode(result.items[i].cur_episode);
				movieItemData.setMovieID(result.items[i].prod_id);
				movieList.add(movieItemData);
			}

			movieAdapter.notifyDataSetChanged();
			beforeGvView = null;
			initFirstFloatView();
			dinashijuGv.setFocusable(true);
			dinashijuGv.setSelected(true);
			isSelectedItem = false;
			dinashijuGv.requestFocus();
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
	
	private void  initFirstFloatView() {
		
		firstFloatView.setX(0);
		firstFloatView.setY(0);
		firstFloatView.setLayoutParams(new FrameLayout.LayoutParams(popWidth, popHeight));
		firstFloatView.setVisibility(View.VISIBLE);
		
		TextView movieName = (TextView) firstFloatView.findViewById(R.id.tv_item_layout_name);
		TextView otherInfo = (TextView) firstFloatView.findViewById(R.id.tv_item_active_layout_other_info);
		
		if(movieList.size() > 0 ) {
			
			aq = new AQuery(firstFloatView);
			aq.id(R.id.iv_item_layout_haibao).image(movieList.get(0).getMoviePicUrl(), 
					true, true,0, R.drawable.post_active);
			movieName.setText(movieList.get(0).getMovieName());
			otherInfo.setText(getString(R.string.zongyi_gengxinzhi) + 
					movieList.get(0).getMovieCurEpisode());
		}
	
		ItemStateUtils.floatViewInAnimaiton(getApplicationContext(),
				firstFloatView);
	}


	private BaseAdapter movieAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			GridViewItemHodler viewItemHodler = null;

			int width = parent.getWidth() / 5;
			int height = (int) (width / 1.0f / STANDARD_PIC_WIDTH * STANDARD_PIC_HEIGHT);

			if (convertView == null) {
				viewItemHodler = new GridViewItemHodler();
				convertView = getLayoutInflater().inflate(
						R.layout.show_item_layout_dianying, null);
				viewItemHodler.nameTv = (TextView) convertView.findViewById(R.id.tv_item_layout_name);
				viewItemHodler.scoreTv = (TextView) convertView.findViewById(R.id.tv_item_layout_score);
				viewItemHodler.otherInfo = (TextView) convertView.findViewById(R.id.tv_item_layout_other_info);
				convertView.setTag(viewItemHodler);
				
			} else {

				viewItemHodler = (GridViewItemHodler) convertView.getTag();
			}
			
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(
					width, height);
			convertView.setLayoutParams(params);
			convertView.setPadding(GRIDVIEW_ITEM_PADDING_LEFT, GRIDVIEW_ITEM_PADDING,
					GRIDVIEW_ITEM_PADDING_LEFT, GRIDVIEW_ITEM_PADDING);
			
			if (width != 0) {

				popWidth = width;
				popHeight = height;
				// Log.i(TAG, "Width:" + popWidth);
			}
			
			if(movieList.size() <= 0) {
				
				return convertView;
			}

			viewItemHodler.nameTv.setText(movieList.get(position).getMovieName());
			viewItemHodler.otherInfo.setText(getString(R.string.zongyi_gengxinzhi) + 
					movieList.get(0).getMovieCurEpisode());

			aq = new AQuery(convertView);
			aq.id(R.id.iv_item_layout_haibao).image(movieList.get(position).getMoviePicUrl(), 
					true, true,0, R.drawable.post_normal);
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if(movieList.size() <= 0 ) {
				
				return null;
			}
			return movieList.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(movieList.size() <= 0 ) {
				
				return DEFAULT_ITEM_NUM;
			}
			return movieList.size();
		}
	};

}
