package com.android.imooc.goo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * @描述         主页
 * @项目名称      App_imooc
 * @包名         com.android.imooc.goo
 * @类名         GooActivity
 * @author      chenlin
 * @date        2015年6月2日 下午8:57:16
 * @version     1.0
 */

public class GooActivity extends Activity {
	private GooView mGooView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		mGooView = new GooView(this);


		setContentView(mGooView);
	}
}


