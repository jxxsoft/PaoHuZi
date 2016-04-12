package jxxsoft.com.cn.paohuzi.activity;

import jxxsoft.com.cn.paohuzi.R;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

/**
 * 多人游戏加入界面
 * @author Administrator
 *
 */
public class Multi_Game_Join_Activity extends BaseActivity implements OnClickListener {

	private AlertDialog helpDialog=null;//帮助提示对话框
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multi_game_join);
		findViewById(R.id.multi_game_exit).setOnClickListener(this);
		findViewById(R.id.multi_game_help).setOnClickListener(this);
		findViewById(R.id.multi_game_btn_user_info).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.multi_game_exit:
				//退出
				this.finish();
				break;
			case R.id.multi_game_help:
				//帮助提示
				helpDialogShow();
				break;
			case R.id.multi_game_btn_user_info:
				//修改用户头像名称
				startActivityForResult(new Intent(this, Person_info_Activity.class),0x01);
				break;
		}
		
	}
	
	
	//修改头像姓名返回
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void helpDialogShow(){
		//创建对话框
		helpDialog=new AlertDialog.Builder(this).create();
		//显示对话框
		helpDialog.show();
		//获得对话框窗口
		Window window=helpDialog.getWindow();
		//设置窗口的视图
		window.setContentView(R.layout.multigame_help);
	}

}
