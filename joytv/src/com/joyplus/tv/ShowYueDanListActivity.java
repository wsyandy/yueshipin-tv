package com.joyplus.tv;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyplus.tv.Service.Return.ReturnTVBangDanList;
import com.joyplus.tv.Service.Return.ReturnTops;
import com.joyplus.tv.entity.MovieItemData;
import com.joyplus.tv.entity.ShiPinInfoParcelable;
import com.joyplus.tv.entity.YueDanInfo2;
import com.joyplus.tv.ui.MyMovieGridView;
import com.joyplus.tv.utils.BangDanKey;
import com.joyplus.tv.utils.JieMianConstant;
import com.joyplus.tv.utils.MyKeyEventKey;

public class ShowYueDanListActivity extends Activity implements
		View.OnKeyListener, MyKeyEventKey, JieMianConstant,BangDanKey, View.OnClickListener {

	private String TAG = "ShowYueDanListActivity";
	private AQuery aq;
	private App app;

	private EditText searchEt;
	private MyMovieGridView dinashijuGv;

	private Button zuijinguankanBtn, zhuijushoucangBtn, lixianshipinBtn;
	
	private TextView yuedanListTv;

	private View firstFloatView;

	private View beforeView, activeView;

	private boolean isSelectedItem = true;// GridView中参数是否真正初始化

	private int popWidth, popHeight;

	private boolean isGridViewUp = false;

	private int[] beforeFirstAndLastVible = { 0, 9 };

	private View beforeGvView = null;

	private ArrayList<MovieItemData> movieList = new ArrayList<MovieItemData>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_yuedan_list);

		aq = new AQuery(this);
		app = (App) getApplication();

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		String name = bundle.getString("NAME");
		String id = bundle.getString("ID");
		
		if(name != null && id != null && !name.equals("")
				&& !id.equals("")) {
			initView();
			
			initState();

			yuedanListTv.setText(name);
			getServiceData(StatisticsUtils.getTopItemURL(TOP_ITEM_URL, id, 1 + "", 50 + ""));
			dinashijuGv.setAdapter(movieAdapter);
			dinashijuGv.setSelected(true);
			dinashijuGv.requestFocus();
			dinashijuGv.setSelection(0);
			
		} else {
			
			finish();
		}

	}

	private void initView() {

		searchEt = (EditText) findViewById(R.id.et_search);
		dinashijuGv = (MyMovieGridView) findViewById(R.id.gv_movie_show);

		zuijinguankanBtn = (Button) findViewById(R.id.bt_zuijinguankan);
		zhuijushoucangBtn = (Button) findViewById(R.id.bt_zhuijushoucang);
		lixianshipinBtn = (Button) findViewById(R.id.bt_lixianshipin);

		firstFloatView = findViewById(R.id.inclue_movie_show_item);
		yuedanListTv = (TextView) findViewById(R.id.tv_yuedanlist_name);
		
		beforeView = zuijinguankanBtn;
		activeView = zuijinguankanBtn;

		addListener();

	}

	private void initState() {

		searchEt.setFocusable(false);// 搜索焦点消失

		zuijinguankanBtn.setPadding(0, 0, WENZI_PADDING_RIGHT, 0);
		zhuijushoucangBtn.setPadding(0, 0, WENZI_PADDING_RIGHT, 0);
		lixianshipinBtn.setPadding(0, 0, WENZI_PADDING_RIGHT, 0);

	}

	private int beforepostion = 0;

	private void addListener() {

		zuijinguankanBtn.setOnKeyListener(this);
		zhuijushoucangBtn.setOnKeyListener(this);
		lixianshipinBtn.setOnKeyListener(this);

		zuijinguankanBtn.setOnClickListener(this);
		zhuijushoucangBtn.setOnClickListener(this);
		lixianshipinBtn.setOnClickListener(this);

		searchEt.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				int action = event.getAction();
				if (action == KeyEvent.ACTION_UP) {
					if (keyCode == KEY_UP) {

						turnToGridViewState();
					}
				}
				return false;
			}
		});

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

						turnToGridViewState();
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

		dinashijuGv
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
//						Intent intent = new Intent();
//						Log.i(TAG, "ID:" + movieList.get(position).getMovieID());
						String pro_type = movieList.get(position).getMovieProType();
						Log.i(TAG, "pro_type:" + pro_type);
						if(pro_type != null && !pro_type.equals("")) {
							
							if(pro_type.equals("2")) {
								Log.i(TAG, "pro_type:" + pro_type + "   --->2");
								Intent intent = new Intent(ShowYueDanListActivity.this,
										ShowXiangqingTv.class);
								intent.putExtra("ID", movieList.get(position).getMovieID());
								startActivity(intent);
//								startActivity();
							} else if(pro_type.equals("1")) {
								Log.i(TAG, "pro_type:" + pro_type + "   --->1");
								Intent intent = new Intent(ShowYueDanListActivity.this,
										ShowXiangqingMovie.class);
								intent.putExtra("ID", movieList.get(position).getMovieID());
								startActivity(intent);
//								startActivity();
							} 
//							else if(pro_type.equals("131")) {
//								
//								intent.putExtra("ID", movieList.get(position).getProd_id());
//								startActivity(intent);
//								startActivity(new Intent(ShowYueDanListActivity.this,
//										ShowXiangqingDongman.class));
//							}
						}
					}
				});

		dinashijuGv
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							final View view, int position, long id) {
						// TODO Auto-generated method stub
						// if (BuildConfig.DEBUG)
						Log.i(TAG, "Positon:" + position + " View:" + view
								+ " beforGvView:" + beforeGvView);

						if (view == null) {

							isSelectedItem = false;
							return;
						}

						final float x = view.getX();
						final float y = view.getY();

						boolean isSmoonthScroll = false;

						boolean isSameContent = position >= beforeFirstAndLastVible[0]
								&& position <= beforeFirstAndLastVible[1];
						if (position >= 5 && !isSameContent) {

							if (beforepostion >= beforeFirstAndLastVible[0]
									&& beforepostion <= beforeFirstAndLastVible[0] + 4) {

								if (isGridViewUp) {

									dinashijuGv
											.smoothScrollBy(-popHeight, 1000);
									isSmoonthScroll = true;
								}
							} else {

								if (!isGridViewUp) {

									dinashijuGv.smoothScrollBy(popHeight,
											1000 * 2);
									isSmoonthScroll = true;

								}
							}

						}

						// if (!isSmoonthScroll) {// 没有强行拖动时候的动画效果

						if (beforeGvView != null) {

							ImageView iv = (ImageView) beforeGvView
									.findViewById(R.id.item_layout_dianying_reflact);
							iv.setVisibility(View.VISIBLE);
							beforeGvView.setBackgroundColor(getResources()
									.getColor(android.R.color.transparent));
							ScaleAnimation outScaleAnimation = StatisticsUtils.getOutScaleAnimation();
							beforeGvView.startAnimation(outScaleAnimation);

						} else {

							ScaleAnimation outScaleAnimation = StatisticsUtils.getOutScaleAnimation();
							firstFloatView.startAnimation(outScaleAnimation);

							firstFloatView.setVisibility(View.GONE);
						}
						
						ImageView iv2 = (ImageView) view
								.findViewById(R.id.item_layout_dianying_reflact);
						iv2.setVisibility(View.GONE);
						ScaleAnimation inScaleAnimation = StatisticsUtils.getInScaleAnimation();

						view.setPadding(GRIDVIEW_ITEM_PADDING, GRIDVIEW_ITEM_PADDING,
								GRIDVIEW_ITEM_PADDING, GRIDVIEW_ITEM_PADDING);
						view.setBackgroundColor(getResources().getColor(
								R.color.text_active));
						view.startAnimation(inScaleAnimation);
						// }

						if (y == 0 || y - popHeight == 0) {// 顶部没有渐影

							if (!isSmoonthScroll) {

								beforeFirstAndLastVible[0] = dinashijuGv
										.getFirstVisiblePosition();
								beforeFirstAndLastVible[1] = dinashijuGv
										.getFirstVisiblePosition() + 9;
							} else {

								beforeFirstAndLastVible[0] = dinashijuGv
										.getFirstVisiblePosition() - 5;
								beforeFirstAndLastVible[1] = dinashijuGv
										.getFirstVisiblePosition() + 9 - 5;
							}

						} else {// 顶部有渐影

							if (!isSmoonthScroll) {

								beforeFirstAndLastVible[0] = dinashijuGv
										.getLastVisiblePosition() - 9;
								beforeFirstAndLastVible[1] = dinashijuGv
										.getLastVisiblePosition();
							} else {

								beforeFirstAndLastVible[0] = dinashijuGv
										.getLastVisiblePosition() - 9 + 5;
								beforeFirstAndLastVible[1] = dinashijuGv
										.getLastVisiblePosition() + 5;
							}

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

					ScaleAnimation outScaleAnimation = StatisticsUtils.getOutScaleAnimation();
					if (beforeGvView != null) {
						ImageView iv = (ImageView) beforeGvView
								.findViewById(R.id.item_layout_dianying_reflact);
						iv.setVisibility(View.VISIBLE);
						beforeGvView.setBackgroundColor(getResources()
								.getColor(android.R.color.transparent));
						beforeGvView.startAnimation(outScaleAnimation);
					} else {

						firstFloatView.setVisibility(View.GONE);
					}
				} else {

					ScaleAnimation inScaleAnimation = StatisticsUtils.getInScaleAnimation();
					if (beforeGvView != null) {

						ImageView iv = (ImageView) beforeGvView
								.findViewById(R.id.item_layout_dianying_reflact);
						iv.setVisibility(View.GONE);
						beforeGvView.setPadding(GRIDVIEW_ITEM_PADDING, GRIDVIEW_ITEM_PADDING,
								GRIDVIEW_ITEM_PADDING, GRIDVIEW_ITEM_PADDING);
						beforeGvView.setBackgroundColor(getResources()
								.getColor(R.color.text_active));

						beforeGvView.startAnimation(inScaleAnimation);

					} else {
						initFirstFloatView();
					}
				}
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	private void beforeViewFoucsStateBack() {

		if (beforeView instanceof LinearLayout) {

			LinearLayout tempLinearLayout = (LinearLayout) beforeView;
			if (beforeView.getId() == activeView.getId()) {
				linearLayoutToActiveState(tempLinearLayout);
			} else {
				linearLayoutToPTState(tempLinearLayout);
			}
		} else if (beforeView instanceof Button) {

			Button tempButton = (Button) beforeView;
			if (beforeView.getId() == activeView.getId()) {
				buttonToActiveState(tempButton);
			} else {
				buttonToPTState(tempButton);
			}
		}
	}

	private void beforeViewActiveStateBack() {
		if (activeView instanceof LinearLayout) {

			LinearLayout tempLinearLayout = (LinearLayout) activeView;
			linearLayoutToPTState(tempLinearLayout);
		} else if (activeView instanceof Button) {

			Button tempButton = (Button) activeView;
			buttonToPTState(tempButton);
		}
	}

	// 转到类似Gridview组件上
	private void turnToGridViewState() {

		if (beforeView.getId() == activeView.getId()) {

			if (activeView instanceof LinearLayout) {

				LinearLayout tempLinearLayout = (LinearLayout) activeView;
				linearLayoutToActiveState(tempLinearLayout);
			} else if (activeView instanceof Button) {

				Button tempButton = (Button) activeView;
				buttonToActiveState(tempButton);
			}
		} else {
			beforeViewFoucsStateBack();
		}

	}

	private void linearLayoutToPTState(LinearLayout linearLayout) {

		Button tempButton = (Button) linearLayout.getChildAt(0);
		linearLayout.setBackgroundResource(R.drawable.text_drawable_selector);
		tempButton.setTextColor(getResources().getColorStateList(
				R.color.text_color_selector));
		tempButton.setCompoundDrawablesWithIntrinsicBounds(getResources()
				.getDrawable(R.drawable.side_hot_normal), null, null, null);
	}

	private void buttonToPTState(Button button) {

		button.setBackgroundResource(R.drawable.text_drawable_selector);
		button.setTextColor(getResources().getColorStateList(
				R.color.text_color_selector));
	}

	private void linearLayoutToActiveState(LinearLayout linearLayout) {

		Button tempButton = (Button) linearLayout.getChildAt(0);
		linearLayout.setBackgroundResource(R.drawable.menubg);
		linearLayout.setPadding(0, 0, WENZI_PADDING_RIGHT, 0);
		tempButton.setTextColor(getResources().getColor(R.color.text_active));
		tempButton.setCompoundDrawablesWithIntrinsicBounds(getResources()
				.getDrawable(R.drawable.side_hot_active), null, null, null);
	}

	private void buttonToActiveState(Button button) {

		button.setBackgroundResource(R.drawable.menubg);
		button.setPadding(0, 0, WENZI_PADDING_RIGHT, 0);
		button.setTextColor(getResources().getColor(R.color.text_active));
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		if (action == KeyEvent.ACTION_UP) {

			v.setOnClickListener(null);

			if (v instanceof LinearLayout) {
				LinearLayout linearLayout = (LinearLayout) v;
				Button button = (Button) linearLayout.getChildAt(0);

				if (keyCode == KEY_UP || keyCode == KEY_LEFT
						|| keyCode == KEY_DOWN) {
					beforeViewFoucsStateBack();
					button.setTextColor(getResources().getColor(
							R.color.text_foucs));
					button.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(
									R.drawable.side_hot_active), null, null,
							null);
				}
			} else if (v instanceof Button) {
				Button button = (Button) v;
				if ((keyCode == KEY_UP || keyCode == KEY_LEFT || keyCode == KEY_DOWN)) {
					searchEt.setFocusable(true);// 能够获取焦点
					beforeViewFoucsStateBack();
					button.setTextColor(getResources().getColor(
							R.color.text_foucs));
					button.setBackgroundResource(R.drawable.text_drawable_selector);
				}
			}

			v.setOnClickListener(this);
		}
		beforeView = v;
		return false;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (aq != null)
			aq.dismiss();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.i("Yangzhg", "onClick");

		if (activeView.getId() == v.getId()) {

			return;
		}
		
		switch (v.getId()) {
		case R.id.bt_zuijinguankan:
			startActivity(new Intent(this, HistoryActivity.class));
			break;
		case R.id.bt_zhuijushoucang:
			startActivity(new Intent(this, ShowShoucangHistoryActivity.class));
			break;

		default:
			break;
		}

		v.setOnKeyListener(null);
		if (v instanceof LinearLayout) {
			LinearLayout linearLayout = (LinearLayout) v;
			if (v.getId() != activeView.getId()) {
				beforeViewActiveStateBack();
				linearLayoutToActiveState(linearLayout);
				activeView = v;
			}
		} else if (v instanceof Button) {
			Button button = (Button) v;
			if (v.getId() != activeView.getId()) {
				beforeViewActiveStateBack();
				buttonToActiveState(button);
				activeView = v;
			}
		}

		beforeGvView = null;
		v.setOnKeyListener(this);
	}
	

	private ObjectMapper mapper = new ObjectMapper();

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
			// hot_list.clear();
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
//				movieItemData.setMoviePicUrl(result.items[i].big_prod_pic_url);
				movieItemData.setMovieScore(result.items[i].score);
				movieItemData.setMovieID(result.items[i].prod_id);
				movieItemData.setMovieCurEpisode(result.items[i].cur_episode);
				movieItemData.setMovieMaxEpisode(result.items[i].max_episode);
				movieItemData.setMovieProType(result.items[i].prod_type);
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

	private void initFirstFloatView() {

		firstFloatView.setX(0);
		firstFloatView.setY(0);
		firstFloatView.setLayoutParams(new FrameLayout.LayoutParams(popWidth,
				popHeight));
		firstFloatView.setVisibility(View.VISIBLE);

		TextView movieName = (TextView) firstFloatView.findViewById(R.id.tv_item_layout_name);
		TextView movieScore = (TextView) firstFloatView.findViewById(R.id.tv_item_layout_score);
		aq = new AQuery(firstFloatView);
//		aq.id(R.id.iv_item_layout_haibao).image(
//				movieList.get(0).getBig_prod_pic_url());
		aq.id(R.id.iv_item_layout_haibao).image(movieList.get(0).getMoviePicUrl(), 
				true, true,0, R.drawable.post_active);
		movieName.setText(movieList.get(0).getMovieName());
		movieScore.setText(movieList.get(0).getMovieScore());
		
		if(movieList.get(0).getMovieProType().equals("1")) {
			
			String duration = movieList.get(0).getMovieDuration();
			if(duration != null && !duration.equals("")) {
				
				TextView movieDuration = (TextView) firstFloatView
						.findViewById(R.id.tv_item_layout_other_info);
				movieDuration.setText(duration);
			}
		} else if(movieList.get(0).getMovieProType().equals("2")){
			
			String curEpisode = movieList.get(0).getMovieCurEpisode();
			String maxEpisode = movieList.get(0).getMovieMaxEpisode();
			
			if(curEpisode == null || curEpisode.equals("0") || 
					curEpisode.compareTo(maxEpisode) >= 0) {
				
				TextView movieUpdate = (TextView) firstFloatView
						.findViewById(R.id.tv_item_layout_other_info);
				movieUpdate.setText(
						maxEpisode + getString(R.string.dianshiju_jiquan));
				} else if(maxEpisode.compareTo(curEpisode) > 0) {
					
					TextView movieUpdate = (TextView) firstFloatView
							.findViewById(R.id.tv_item_layout_other_info);
					movieUpdate.setText(getString(R.string.zongyi_gengxinzhi) + 
							curEpisode);
			}
		}
		
		firstFloatView.setPadding(GRIDVIEW_ITEM_PADDING, GRIDVIEW_ITEM_PADDING,
				GRIDVIEW_ITEM_PADDING, GRIDVIEW_ITEM_PADDING);
		firstFloatView.setBackgroundColor(getResources()
				.getColor(R.color.text_active));
		ScaleAnimation inScaleAnimation = StatisticsUtils.getInScaleAnimation();
		
		firstFloatView.startAnimation(inScaleAnimation);
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
			convertView.setPadding(GRIDVIEW_ITEM_PADDING, GRIDVIEW_ITEM_PADDING,
					GRIDVIEW_ITEM_PADDING, GRIDVIEW_ITEM_PADDING);

			viewItemHodler.nameTv.setText(movieList.get(position).getMovieName());
//			viewItemHodler.scoreTv.setText(movieList.get(position).getMovieScore());
			
			if(movieList.get(position).getMovieProType().equals("1")) {
				
				String duration = movieList.get(position).getMovieDuration();
				viewItemHodler.scoreTv.setText(movieList.get(position).getMovieScore());
				if(duration != null && !duration.equals("")) {
					
					viewItemHodler.otherInfo.setText(duration);
				}
			} else if(movieList.get(position).getMovieProType().equals("2")){
				
				viewItemHodler.scoreTv.setText(movieList.get(position).getMovieScore());
				
				String curEpisode = movieList.get(position).getMovieCurEpisode();
				String maxEpisode = movieList.get(position).getMovieMaxEpisode();
				
				if(curEpisode == null || curEpisode.equals("0") || 
						curEpisode.compareTo(maxEpisode) >= 0) {
					
					viewItemHodler.otherInfo.setText(
							maxEpisode + getString(R.string.dianshiju_jiquan));
					} else if(maxEpisode.compareTo(curEpisode) > 0) {
						
						viewItemHodler.otherInfo.setText(getString(R.string.zongyi_gengxinzhi) + 
								curEpisode);
				}
			}

			if (width != 0) {

				popWidth = width;
				popHeight = height;
				// Log.i(TAG, "Width:" + popWidth);
			}

			aq = new AQuery(convertView);
//			aq.id(R.id.iv_item_layout_haibao).image(
//					movieList.get(position).getBig_prod_pic_url());
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
			return movieList.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return movieList.size();
		}
	};
	
	 private class GridViewItemHodler {
			
		TextView nameTv;
		TextView scoreTv;
		TextView otherInfo;
	}

}
