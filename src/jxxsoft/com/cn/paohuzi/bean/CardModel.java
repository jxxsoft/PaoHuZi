package jxxsoft.com.cn.paohuzi.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 一首牌，牌情况
 *
 */
public class CardModel {
	public int count;//手数
	public int value;//权值
	//一组牌
	public List<String> danzi=new ArrayList<String>(); //单张
	public List<String> duizi=new ArrayList<String>(); //对子
	public List<String> sandai=new ArrayList<String>(); //3带
	public List<String> danshun=new ArrayList<String>(); //单顺
	public List<String> shuangshun=new ArrayList<String>(); //双顺
	public List<String> feiji=new ArrayList<String>(); //飞机
	public List<String> zhadan=new ArrayList<String>(); //炸弹
}
