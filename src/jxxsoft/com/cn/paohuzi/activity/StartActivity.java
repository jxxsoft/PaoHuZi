package jxxsoft.com.cn.paohuzi.activity;

import jxxsoft.com.cn.paohuzi.R;
import jxxsoft.com.cn.paohuzi.R.layout;
import jxxsoft.com.cn.paohuzi.app.MainApplication;
import jxxsoft.com.cn.paohuzi.util.DialogUtil;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 游戏开始界面
 * @author Administrator
 *
 */
public class StartActivity extends BaseActivity implements OnClickListener {

	//获得MainApplication实例
	private MainApplication app=MainApplication.getInstance();
	private TextView textview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//加载布局
		setContentView(R.layout.activity_start);
		//绑定按钮单击事件
		findViewById(R.id.start_screen_start).setOnClickListener(this);
		findViewById(R.id.start_screen_feedback).setOnClickListener(this);
		findViewById(R.id.start_screen_exit).setOnClickListener(this);
		//播放背景音乐
		app.playbgMusic("MusicEx_Welcome.ogg");
	}
	

	/**
	 * 加载菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	/**
	 * 菜单选项被选择触发
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//播放音乐特效
		app.play("SpecOk.ogg");
		
		switch (item.getItemId()) {
			case R.id.action_settings:
				//选择设置菜单，弹出设置对话框
				DialogUtil.setupDialog(this,1);
				break;
	
			case R.id.action_exits:
				//选择退出菜单，弹出退出对话框
				DialogUtil.exitSystemDialog(this);
				break;
		}
		return true;
	}

	

	//返回按钮,退出系统，弹出退出对话框
	@Override
	public void onBackPressed() {
		//播放音乐特效
		app.play("SpecOk.ogg");
		//系统退出对话框
		DialogUtil.exitSystemDialog(this);
		
	}


	/**
	 * 按钮的单击处理方法
	 */
	@Override
	public void onClick(View v) {
		//播放音乐特效
		app.play("SpecOk.ogg");
		switch (v.getId()) {
			case R.id.start_screen_start:
				//单击游戏开始按钮，进行游戏选择界面
				startActivity(new Intent(this,SelectActivity.class ));
				break;
			case R.id.start_screen_feedback:
					//单击游戏反馈结果，弹出对话框，提交反馈信息	
				break;
			case R.id.start_screen_exit:
				//单击退出游戏，弹出系统退出对话框
				DialogUtil.exitSystemDialog(this);
				break;
		}
		
	}

}
