package com.joyplus.tv.Adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.joyplus.tv.R;
import com.joyplus.tv.StatisticsUtils;
import com.joyplus.tv.entity.GridViewItemHodler;
import com.joyplus.tv.entity.MovieItemData;
import com.joyplus.tv.entity.YueDanInfo2;
import com.joyplus.tv.utils.JieMianConstant;
import com.joyplus.tv.utils.Log;

public class YueDanAdapter extends BaseAdapter implements JieMianConstant{
	public static final String TAG = "YueDanAdapter";
	
	private List<MovieItemData> movieList = new ArrayList<MovieItemData>();
	private int popWidth,popHeight;

	public int getWidth() {
		return popWidth;
	}

	public int getHeight() {
		return popHeight;
	}

	private Context context;
	private AQuery aq;
	
	public YueDanAdapter(Context context,AQuery aq) {
		
		this.context = context;
		this.aq = aq;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (movieList.size() <= 0) {

			return DEFAULT_ITEM_NUM;
		}
		return movieList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (movieList.size() <= 0) {

			return null;
		}
		return movieList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		if (movieList.size() <= 0) {

			return DEFAULT_ITEM_NUM;
		}
		return movieList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		GridViewItemHodler viewItemHodler = null;

		int width = parent.getWidth() / 5;
		int height = (int) (width / 1.0f / STANDARD_PIC_WIDTH * STANDARD_PIC_HEIGHT);

		if (convertView == null) {
			viewItemHodler = new GridViewItemHodler();
			convertView = ((Activity)context).getLayoutInflater().inflate(
					R.layout.show_item_layout_dianying, null);
			viewItemHodler.nameTv = (TextView) convertView
					.findViewById(R.id.tv_item_layout_name);
			viewItemHodler.scoreTv = (TextView) convertView
					.findViewById(R.id.tv_item_layout_score);
			viewItemHodler.otherInfo = (TextView) convertView
					.findViewById(R.id.tv_item_layout_other_info);
			viewItemHodler.haibaoIv = (ImageView) convertView
					.findViewById(R.id.iv_item_layout_haibao);
			viewItemHodler.definition = (ImageView) convertView.findViewById(R.id.iv_item_layout_gaoqing_logo);
			convertView.setTag(viewItemHodler);
			
			convertView.setPadding(GRIDVIEW_ITEM_PADDING_LEFT,
					GRIDVIEW_ITEM_PADDING, GRIDVIEW_ITEM_PADDING_LEFT,
					GRIDVIEW_ITEM_PADDING);

		} else {

			viewItemHodler = (GridViewItemHodler) convertView.getTag();
		}

		AbsListView.LayoutParams params = new AbsListView.LayoutParams(
				width, height);
		convertView.setLayoutParams(params);
		
		if(width != 0) {
			
			popHeight = height;
			popWidth = width;
		}

		if (movieList.size() <= 0) {

			return convertView;
		}

		Log.i(TAG, "postion:" + position + " " + movieList.get(position).getMovieName());
		viewItemHodler.nameTv.setText(movieList.get(position).getMovieName());
		
		
		String num = movieList.get(position).getNum();
		if(num != null && !num.equals("") ) {
			
			viewItemHodler.otherInfo.setText(movieList.get(position).getNum() + context.getString(R.string.yingpianshu));
		} else {
			
			String definition = movieList.get(position).getDefinition();
			
			if(definition != null && !definition.equals("")) {
				viewItemHodler.definition.setVisibility(View.VISIBLE);
				switch (Integer.valueOf(definition)) {
				case 8:
					viewItemHodler.definition.setImageResource(R.drawable.icon_bd);
					break;
				case 7:
					viewItemHodler.definition.setImageResource(R.drawable.icon_hd);
					break;
				case 6:
					viewItemHodler.definition.setImageResource(R.drawable.icon_ts);
					break;
				default:
					viewItemHodler.definition.setVisibility(View.GONE);
					break;
				}
			}
			String proType = movieList.get(position).getMovieProType();
			
			if(proType != null && !proType.equals("")) {
				
				if(proType.equals("1")) {
					
					viewItemHodler.scoreTv.setText(movieList.get(position).getMovieScore());
					String duration = movieList.get(position).getMovieDuration();
					if(duration != null && !duration.equals("")) {
						
						viewItemHodler.otherInfo.setText(StatisticsUtils.formatMovieDuration(duration));
					}
				} else if(proType.equals("2") || proType.equals("131")){
					
					viewItemHodler.scoreTv.setText(movieList.get(position).getMovieScore());
					String curEpisode = movieList.get(position).getMovieCurEpisode();
					String maxEpisode = movieList.get(position).getMovieMaxEpisode();
					
					if(maxEpisode != null && !maxEpisode.equals("")) {
						
						if(curEpisode == null || curEpisode.equals("0") || 
								curEpisode.compareTo(maxEpisode) >= 0) {
							
							viewItemHodler.otherInfo.setText(
									maxEpisode + context.getString(R.string.dianshiju_jiquan));
							} else if(maxEpisode.compareTo(curEpisode) > 0) {

								viewItemHodler.otherInfo.setText(context.getString(R.string.zongyi_gengxinzhi) + 
										curEpisode);
						}
					}

				} else if(proType.equals("3")) {
					
					String curEpisode = movieList.get(position).getMovieCurEpisode();
					if(curEpisode != null && !curEpisode.equals("")) {
						
						viewItemHodler.otherInfo.setText(StatisticsUtils.formateZongyi(curEpisode, context));
					}
				}
			}
		}
		


		aq.id(viewItemHodler.haibaoIv).image(movieList.get(position).getMoviePicUrl(), 
				true, true,0, R.drawable.post_normal);
		return convertView;
	}

	public void setList(List<MovieItemData> list) {
		this.movieList = list;
	}
	
	public List<MovieItemData> getMovieList() {
		return movieList;
	}

}
