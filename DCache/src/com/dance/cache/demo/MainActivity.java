package com.dance.cache.demo;

import java.io.File;
import java.io.Serializable;

import com.dance.cache.R;
import com.dance.cache.R.id;
import com.dance.cache.R.layout;
import com.dance.cache.lib.DCache;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{
	TextView text;
	File file = new File(Environment.getExternalStorageDirectory()+"/com.dance.cache/cache");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		text = (TextView) findViewById(R.id.text);
		
		//init cache config
		DCache.get(this).cacheDir(file)
						.cacheDuration(1000*5);
		
		text.setText("已经存入数据...稍后取出缓存数据!");
						
//		cacheString();
		cacheObject();
	}
	
	private void cacheString(){
		DCache.get(this).putString("test1", "一支穿云箭，千军万马来相见！");
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				String data = DCache.get(MainActivity.this).getString("test1");
				text.setText(data==null?"缓存数据失效":data);
			}
		}, 4000);
	}
	
	private void cacheObject(){
		DCache.get(this).putObject("InnerStu", new InnerStu("李晓俊", 25));
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				InnerStu data = (InnerStu) DCache.get(MainActivity.this).getObject("InnerStu");
				text.setText(data==null?"缓存数据失效":data.toString());
			}
		}, 4000);
	}
	
	
	static class InnerStu implements Serializable{
		private static final long serialVersionUID = -1795909556508872340L;
		private String name;
		private int age;
		public InnerStu(String name, int age) {
			super();
			this.name = name;
			this.age = age;
		}
		@Override
		public String toString() {
			return "Stu [name=" + name + ", age=" + age + "]";
		}
		
	}
}
