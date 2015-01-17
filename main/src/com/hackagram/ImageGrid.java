package com.hackagram;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageGrid extends BaseAdapter{
	private Context mContext;
	
	ImageGrid(Context c){
		mContext = c;
	}
	
	public Integer mImageThumbs[] = {
			R.drawable.pic_1,
			R.drawable.pic_2,
			R.drawable.pic_3,
			R.drawable.pic_4,
			R.drawable.pic_5,
			R.drawable.pic_6,
			R.drawable.pic_7,
			R.drawable.pic_8,
			R.drawable.pic_9,
			R.drawable.pic_10,
			R.drawable.pic_11,
			R.drawable.pic_12, 
		};	//3 x 4 grid thumbs

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mImageThumbs.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mImageThumbs[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ImageView i = new ImageView(mContext);
		i.setImageResource(mImageThumbs[arg0]);
		i.setScaleType(ImageView.ScaleType.CENTER);
		i.setLayoutParams(new GridView.LayoutParams(90,90));
		
		return i;
	}
}
