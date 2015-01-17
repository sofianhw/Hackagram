package com.hackagram;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

public class ImageGridActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_grid);
		
		GridView g = (GridView) findViewById(R.id.grid_view);
		g.setAdapter(new ImageGrid(this));
	}
}
