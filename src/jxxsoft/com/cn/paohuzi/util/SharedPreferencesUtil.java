package jxxsoft.com.cn.paohuzi.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 的操作类  采用单态设计模式
 * @author Administrator
 *
 */
public class SharedPreferencesUtil {

	private static SharedPreferencesUtil instance;
	private static SharedPreferences preferences;
	
	private SharedPreferencesUtil(){
		
	}
	/**
	 * 返回SharedPreferencesUtil的实例对象
	 * @param context
	 * @return
	 */
	public static SharedPreferencesUtil getInstance(Context context){
		if(instance==null){
			preferences=context.getSharedPreferences("gameset", Context.MODE_PRIVATE);
			instance=new SharedPreferencesUtil();
		}
		return instance;
	}
	
	/**
	 * 存储字符串
	 * @param key
	 * @param value
	 */
	public void savePreferences(String key,String value){
		SharedPreferences.Editor editor=preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	/**
	 * 存储布尔值
	 * @param key
	 * @param value
	 */
	public void savePreferences(String key,boolean value){
		SharedPreferences.Editor editor=preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	/**
	 * 存储整数
	 * @param key
	 * @param value
	 */
	public void savePreferences(String key,int value){
		SharedPreferences.Editor editor=preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	/**
	 * 通过key获得整数值 如果没有返回-1
	 * @param key
	 * @return
	 */
	public int getPreferencesInt(String key){
		return preferences.getInt(key, -1);
	}
	
	/**
	 * 通过key获得字符串值，如果没有返回null
	 * @param key
	 * @return
	 */
	public String getPreferencesString(String key){
		return preferences.getString(key, "");
	}
	
	/**
	 * 通过key获得布尔值，如果没有返回false
	 * @param key
	 * @return
	 */
	public boolean getPreferencesBoolean(String key){
		return preferences.getBoolean(key, false);
	}
	
	
	
}
