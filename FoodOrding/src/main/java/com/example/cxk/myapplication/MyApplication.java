package com.example.cxk.myapplication;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		//初始化ImageLoader
		ImageLoaderConfiguration configuration=ImageLoaderConfiguration.createDefault(getApplicationContext());
		ImageLoader.getInstance().init(configuration);
	}
}
