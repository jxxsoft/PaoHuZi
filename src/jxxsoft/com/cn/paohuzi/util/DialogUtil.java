package jxxsoft.com.cn.paohuzi.util;

import jxxsoft.com.cn.paohuzi.R;
import jxxsoft.com.cn.paohuzi.app.MainApplication;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * 对话框操作类
 * @author Administrator
 *
 */
public class DialogUtil {

	/**
	 * 退出整个游戏
	 * @param mycontext
	 */
	public static  void exitSystemDialog(Context mycontext){
		//创建对话框Builer对象
		AlertDialog.Builder builder=new AlertDialog.Builder(mycontext);
		//取消点击屏幕、返回键关闭对话框
		builder.setCancelable(false);
		//显示对话框，并返回对话框对象
		final AlertDialog dialog=builder.show();
		//加载信息布局
		View view=LayoutInflater.from(mycontext).inflate(R.layout.message_box_exit_game, null);
		//设置对话框的界面
		dialog.getWindow().setContentView(view);
		//给对话框的界面取消按钮绑定单击事件
		view.findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//播放音效
				MainApplication.getInstance().play("SpecOk.ogg");
				//关闭对话框
				dialog.dismiss();				
			}
		});
		//给对话框的界面确定按钮绑定单击事件
		view.findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {
						
				@Override
				public void onClick(View v) {
					//播放音效
					MainApplication.getInstance().play("SpecOk.ogg");
					//关闭对话框
					dialog.dismiss();
					//退出游戏软件
					MainApplication.getInstance().exit();
				}
		});
	}
	/**
	 * 退出游戏窗口对话框
	 * @param mycontext
	 */
	public static  void exitGameDialog(Context mycontext){
		//创建对话框Builer对象
		AlertDialog.Builder builder=new AlertDialog.Builder(mycontext);
		//取消点击屏幕、返回键关闭对话框
		builder.setCancelable(false);
		//显示对话框，并返回对话框对象
		final AlertDialog dialog=builder.show();
		//加载信息布局
		View view=LayoutInflater.from(mycontext).inflate(R.layout.message_box_exit_game, null);
		//设置对话框的界面
		dialog.getWindow().setContentView(view);
		//给对话框的界面取消按钮绑定单击事件
		view.findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//播放音效
				MainApplication.getInstance().play("SpecOk.ogg");
				//关闭对话框
				dialog.dismiss();					
			}
		});
		//给对话框的界面确定按钮绑定单击事件
		view.findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							MainApplication app=MainApplication.getInstance();
							//播放音效
							app.play("SpecOk.ogg");
							//关闭对话框
							dialog.dismiss();
							//退出游戏
							app.getActivityList().get(app.getActivityList().size()-1).finish();
							//切换背景音乐
							app.playbgMusic("MusicEx_Welcome.ogg");
						}
		});
	}
	
	
	
	
	/**
	 * 游戏设置对话框
	 * @param mycontext
	 * site: 1表示在欢迎界面，2表示在单机游戏界面
	 */
	public static  void setupDialog(final Context mycontext,final int site){
		//创建对话框Builer对象
		AlertDialog.Builder builder=new AlertDialog.Builder(mycontext);
		//显示对话框，并返回对话框对象
		final AlertDialog dialog=builder.show();
		//加载信息布局
		View view=LayoutInflater.from(mycontext).inflate(R.layout.setting_panel, null);
		//获得对话框的窗口对象
		Window window=dialog.getWindow();
		//设置对话框的界面
		window.setContentView(view);
		//加载配置信息，绑定到控件
		//获得SharedPreferencesUtil实例对象
		SharedPreferencesUtil spu=SharedPreferencesUtil.getInstance(mycontext);
		//获得界面的相关控件
		RadioButton male=(RadioButton)view.findViewById(R.id.male_check);
		RadioButton female=(RadioButton)view.findViewById(R.id.female_check);
		CheckBox effect=(CheckBox)view.findViewById(R.id.effect_check);
		CheckBox music=(CheckBox)view.findViewById(R.id.music_check);
		SeekBar game_speed=(SeekBar)view.findViewById(R.id.game_speed_bar);
		//判断性别选择 。绑定控件
		if(spu.getPreferencesInt("sex")==1){
			male.setChecked(true);
		}else if(spu.getPreferencesInt("sex")==2){
			female.setChecked(true);
		}
		//设置音效选择
		effect.setChecked(spu.getPreferencesBoolean("effectmusic"));
		//设置背景音乐选择
		music.setChecked(spu.getPreferencesBoolean("bgmusic"));
		//设置游戏速度
		game_speed.setProgress(spu.getPreferencesInt("speed"));
		//保存信息，通过给每个控件添加监听器，当选择改变时候，就保存值，同时同步MainApplication的相应值
		
		//当选项改变时候，就保存性别
		((RadioGroup)view.findViewById(R.id.radioGroupSex)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				//性别保存 0表示为选择，1表示男，2表示女
				//播放音效
				MainApplication.getInstance().play("SpecOk.ogg");
				switch (checkedId) {
					case R.id.male_check:
						//同时同步MainApplication的相应值
						MainApplication.getInstance().setSex(1);
						//保存
						SharedPreferencesUtil.getInstance(mycontext).savePreferences("sex", 1);
						break;
					case R.id.female_check:
						//同时同步MainApplication的相应值
						MainApplication.getInstance().setSex(2);
						//保存
						SharedPreferencesUtil.getInstance(mycontext).savePreferences("sex", 2);
						break;
				}
				
			}
		});
		
		effect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				//播放音效
				MainApplication.getInstance().play("SpecOk.ogg");
				//同步设置
				MainApplication.getInstance().setEffectmusic(isChecked);
				//保存值
				SharedPreferencesUtil.getInstance(mycontext).savePreferences("effectmusic", isChecked);				
			}
		});
		
		music.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//记录信息
				MainApplication app=MainApplication.getInstance();
				//播放音效
				app.play("SpecOk.ogg");
				//同步设置
				app.setBgmusic(isChecked);
				//判断是否关闭音乐，开启音乐
				if(isChecked==false){
					//关闭所有音乐
					app.stopbgMusic();
				}else{
					//进行音乐播放
					if(site==1){
						app.playbgMusic("MusicEx_Welcome.ogg");
					}else if(site==2){
						app.playbgMusic("MusicEx_Normal2.ogg");
					}
				}
				//保存值
				SharedPreferencesUtil.getInstance(mycontext).savePreferences("bgmusic", isChecked);				
			}
		});
		//游戏速度监听
		game_speed.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			//当进度条改变以后
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				//播放音效
				MainApplication.getInstance().play("SpecOk.ogg");
				//同步设置
				MainApplication.getInstance().setSpeed(progress);
				//保存值
				SharedPreferencesUtil.getInstance(mycontext).savePreferences("speed", progress);
				
			}
		});
		
		 
		
	}
	
	/**
	 * 网络wifi设置提示对话框
	 * @param context
	 */
	public static void wifiSetDialog(final Context context){
		//创建对话框
		final AlertDialog wifiDialog=new AlertDialog.Builder(context).create();
		//显示对话框
		wifiDialog.show();
		//获得对话框窗口
		Window window=wifiDialog.getWindow();
		//设置窗口的视图
		window.setContentView(R.layout.message_box_set_wifi);
		//取消按钮绑定单击事件
		window.findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//取消wifi设置
				wifiDialog.dismiss();
				
			}
		});
		//设置按钮绑定单击事件
		window.findViewById(R.id.btn_set_wifi).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//打开wifi设置界面
				context.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
				wifiDialog.dismiss();
				
			}
		});
	}
}
