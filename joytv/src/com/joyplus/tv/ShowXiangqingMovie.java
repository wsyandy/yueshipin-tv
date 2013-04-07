package com.joyplus.tv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShowXiangqingMovie extends Activity implements View.OnClickListener,
		View.OnKeyListener, MyKeyEventKey {

	private LinearLayout bofangLL;

	private Button dingBt,xiaiBt,xiazaiBt, yingpingBt;
	private Button bofangBt,gaoqingBt;

	private View beforeView;

	private PopupWindow popupWindow;
	private View popupView;

	private boolean isDing, isXiai;
	private boolean isPopupWindowShow;

	private View beforeTempPop, currentBofangViewPop;
	private LinearLayout chaoqingLL, gaoqingLL, biaoqingLL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.show_dianying_xiangxi_layout);

		initView();
	}

	private void initPopWindow() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		popupView = inflater.inflate(R.layout.show_gaoqing_item, null);

		chaoqingLL = (LinearLayout) popupView
				.findViewById(R.id.ll_gaoqing_chaoqing);
		gaoqingLL = (LinearLayout) popupView
				.findViewById(R.id.ll_gaoqing_gaoqing);
		biaoqingLL = (LinearLayout) popupView
				.findViewById(R.id.ll_gaoqing_biaoqing);

		popupWindow = new PopupWindow(popupView);
		
		currentBofangViewPop = biaoqingLL;
		beforeTempPop = biaoqingLL;
	}

	private void initView() {

		dingBt = (Button) findViewById(R.id.bt_xiangqingding);
		xiaiBt = (Button) findViewById(R.id.bt_xiangqing_xiai);
		bofangLL = (LinearLayout) findViewById(R.id.ll_xiangqing_bofang_gaoqing);
		
		bofangBt = (Button) findViewById(R.id.bt_xiangqing_bofang);
		gaoqingBt = (Button) findViewById(R.id.bt_xiangqing_gaoqing);


		xiazaiBt = (Button) findViewById(R.id.bt_xiangqing_xiazai);
		yingpingBt = (Button) findViewById(R.id.bt_xiangqing_yingping);

		addListener();

		initPopWindow();

		xiazaiBt.setFocusable(false);
		// bofangLL.setFocusable(true);

		beforeView = dingBt;
//		dingBt.setSelected(true);

	}

	private void addListener() {

		dingBt.setOnKeyListener(this);
		xiaiBt.setOnKeyListener(this);
		bofangLL.setOnKeyListener(this);

		dingBt.setOnClickListener(this);
		xiaiBt.setOnClickListener(this);
		bofangLL.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_xiangqingding:
			if (isDing) {
				isDing = false;
			} else {
				isDing = true;
			}
			dingBt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_dig_active, 0, 0,0);
			dingBt.setTextColor(getResources().getColor(R.color.text_foucs));
			break;
		case R.id.bt_xiangqing_xiai:
			if (isXiai) {
				isXiai = false;
			} else {
				isXiai = true;
			}
			xiaiBt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_dig_active, 0, 0,0);
			xiaiBt.setTextColor(getResources().getColor(R.color.text_foucs));
			break;
		case R.id.ll_xiangqing_bofang_gaoqing:
			// bofangLL.setN
			// xiaiIv.setImageResource(R.drawable.icon_fav_active);
			// xiaiTv.setTextColor(getResources().getColor(R.color.text_foucs));
			break;
		default:
			break;
		}

	}

	private void backToNormalState() {
		int id = beforeView.getId();
		switch (id) {
		case R.id.bt_xiangqingding:
			if (!isDing) {
				dingBt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ding_selector, 0, 0,0);
			}
			dingBt.setTextColor(getResources().getColor(R.color.time_color));
			break;
		case R.id.bt_xiangqing_xiai:
			if (!isXiai) {
				xiaiBt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ding_selector, 0, 0,0);
			}
			xiaiBt.setTextColor(getResources().getColor(R.color.time_color));
			break;
		case R.id.ll_xiangqing_bofang_gaoqing:
			// bofangLL.setN
			// xiaiIv.setImageResource(R.drawable.icon_fav_active);
			// xiaiTv.setTextColor(getResources().getColor(R.color.text_foucs));
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();

		if (action == KeyEvent.ACTION_UP) {

			switch (v.getId()) {
			case R.id.bt_xiangqingding:
				if (keyCode == KEY_UP || keyCode == KEY_LEFT) {
					backToNormalState();
					dingBt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_dig_active, 0, 0,0);
					dingBt.setTextColor(getResources().getColor(
							R.color.text_foucs));
				}
				break;
			case R.id.bt_xiangqing_xiai:
				if (keyCode == KEY_UP || keyCode == KEY_LEFT
						|| keyCode == KEY_RIGHT) {
					backToNormalState();
					dingBt.setSelected(false);
					xiaiBt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_dig_active, 0, 0,0);
					xiaiBt.setTextColor(getResources().getColor(
							R.color.text_foucs));
				}
				break;
			case R.id.ll_xiangqing_bofang_gaoqing:
				if (keyCode == KEY_UP || keyCode == KEY_LEFT
						|| keyCode == KEY_RIGHT) {

					if (keyCode == KEY_UP && beforeView.getId() == v.getId()
							&& !isPopupWindowShow) {
						initPopWindowData();
						int width = v.getWidth();
						int height = v.getHeight() * 3;
						int locationY = v.getHeight() * 2;
						int[] location = new int[2];
						v.getLocationOnScreen(location);
						popupWindow.setFocusable(true);
						popupWindow.setWidth(width + 10);
						popupWindow.setHeight(height + 40);
						popupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
								location[0] - 6, location[1] - locationY -40);

					} else {

						backToNormalState();
					}
					// Log.i("Yangzhg", "UPUP!!!!!!");
					// bofangLL.setN
					// xiaiIv.setImageResource(R.drawable.icon_fav_active);
					// xiaiTv.setTextColor(getResources().getColor(R.color.text_foucs));
				}
				break;
			default:
				break;
			}

			beforeView = v;
		}
		return false;
	}
	
	private void backToNormalPopView() {
		
		LinearLayout ll = (LinearLayout) beforeTempPop;
		Button button1= (Button) ll.getChildAt(0);
		Button button2= (Button) ll.getChildAt(1);
		button1.setVisibility(View.INVISIBLE);
		button2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
	}
	
	private void setLinearLayoutVisible(View v) {
		
		LinearLayout ll = (LinearLayout) v;
		Button button1= (Button) ll.getChildAt(0);
		Button button2= (Button) ll.getChildAt(1);
		button1.setVisibility(View.VISIBLE);
		button2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_play, 0);
	}

	private void initPopWindowData() {
		setLinearLayoutVisible(currentBofangViewPop);

		OnClickListener gaoqingListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int id = v.getId();
				switch (id) {
				case R.id.ll_gaoqing_chaoqing:
					gaoqingBt.setText(R.string.gaoqing_chaogaoqing);
					currentBofangViewPop = v;
					break;
				case R.id.ll_gaoqing_gaoqing:
					gaoqingBt.setText(R.string.gaoqing_gaoqing);
					currentBofangViewPop = v;
					break;
				case R.id.ll_gaoqing_biaoqing:
					gaoqingBt.setText(R.string.gaoqing_biaoqing);
					currentBofangViewPop = v;
					break;
				default:
					break;
				}

				if (popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
			}
		};
		chaoqingLL.setOnClickListener(gaoqingListener);
		gaoqingLL.setOnClickListener(gaoqingListener);
		biaoqingLL.setOnClickListener(gaoqingListener);

		OnKeyListener gaoqingKeyListener = new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				int action = event.getAction();

				if (action == KeyEvent.ACTION_UP) {

					switch (v.getId()) {
					case R.id.ll_gaoqing_chaoqing:
						if(keyCode == KEY_UP) {
							backToNormalPopView();
							setLinearLayoutVisible(v);
						}
						break;
					case R.id.ll_gaoqing_gaoqing:
						if(keyCode == KEY_UP || keyCode == KEY_DOWN) {
							backToNormalPopView();
							setLinearLayoutVisible(v);
						}
						break;
					case R.id.ll_gaoqing_biaoqing:
						if(keyCode == KEY_DOWN) {
							backToNormalPopView();
							setLinearLayoutVisible(v);
						}
						break;
					default:
						break;
					}
					beforeTempPop = v;
				}
				return false;
			}
		};
		
		chaoqingLL.setOnKeyListener(gaoqingKeyListener);
		gaoqingLL.setOnKeyListener(gaoqingKeyListener);
		biaoqingLL.setOnKeyListener(gaoqingKeyListener);

	}
}
