package jxxsoft.com.cn.paohuzi.activity;

import jxxsoft.com.cn.paohuzi.R;
import jxxsoft.com.cn.paohuzi.app.MainApplication;
import jxxsoft.com.cn.paohuzi.util.DialogUtil;
import jxxsoft.com.cn.paohuzi.util.NetworkUtil;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

/**
 * 游戏选择界面
 * @author Administrator
 *
 */
public class SelectActivity extends BaseActivity implements OnClickListener{

	//获得MainApplication对象
	private MainApplication app=MainApplication.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//加载布局
		setContentView(R.layout.activity_select);
		//绑定相应按钮单击事件
		findViewById(R.id.choose_game_exit).setOnClickListener(this);
		findViewById(R.id.choose_game_btn_multi_play).setOnClickListener(this);
		findViewById(R.id.choose_game_btn_single_play).setOnClickListener(this);
	}

	/**
	 * 按钮的单击处理方法
	 */
	@Override
	public void onClick(View v) {
		//播放音乐特效
		app.play("SpecOk.ogg");
		switch (v.getId()) {
			case R.id.choose_game_exit:
				//关闭当前界面，返回开始界面
				this.finish();
				break;
			case R.id.choose_game_btn_multi_play:
				//多人局域网对战 判断wifi连通
				if(NetworkUtil.isWifiConnected()){
					//连通的话，进入多人游戏界面
					startActivity(new Intent(this, Multi_Game_Join_Activity.class));
				}else{
					//没有连通的话，设置wifi对话框
					DialogUtil.wifiSetDialog(this);
				}
				break;
			case R.id.choose_game_btn_single_play:
				//进入单机游戏
				startActivity(new Intent(this, SingleGameActivity.class));
				break;
		}
	}
	
	
	
	
	
}
