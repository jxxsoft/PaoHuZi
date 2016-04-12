package jxxsoft.com.cn.paohuzi.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import jxxsoft.com.cn.paohuzi.bean.Card;
import jxxsoft.com.cn.paohuzi.bean.CardModel;

/**
 * 
 为每一种牌型定义权值的大小：
单张                              1
danpai,//单牌。任意一张单牌

对子                              2
duipai,//对牌。任意两张点数相同的牌。

三带                              3
sanbudai,//3不带。任意三张点数相同的牌，如888。
sandaiyi,//3带1。点数相同的三张牌+一张单牌。如：333+6 
sandaier,//3带2。点数相同的三张牌+一对牌。如：444+99。

单顺                              4  (每多一张牌权值+1)
danshun,//单顺。任意五张或五张以上点数相连的牌，如：45678或78910JQK。不包括 2和双王。

双顺                              5（每多一对牌，权值+2）
shuangshun,//双顺。三对或更多的连续对牌，如：334455、7788991010JJ。不包括 2 点和双王。

飞机                              7（每对以飞机，权值在基础上+3）
feiji,//飞机。二个或更多的连续三张牌，如：333444 、555666777888。不包括 2 点和双王。
feijidaidan,//飞机带单排.444555+79
feiijidaiduizi,//飞机带对子.333444555+7799JJ

炸弹                             10（包括对王在内） 
zhadan,//炸弹。四张点数相同的牌，如：7777。
huojian,//火箭 即双王（大王和小王），最大的牌。


sidaier,//4带2个单，或者一对 ,四张牌＋两手牌。如：5555＋3＋8 。
sidaierdui,//4带2对 四张牌＋两对手牌 4444＋55＋77 


error//不能出牌
 *
 */
public class LandlordsUtil {

	/**
	 * 获得所有的对子
	 * @param list
	 * @param model
	 */
	public static void getDuiZi(List<Card> list, CardModel model) {
		// 连续2张相同
		for (int i = 0, len = list.size(); i < len; i++) {
			if (i + 1 < len&& list.get(i).getName() == list.get(i + 1).getName()) {
				String s = list.get(i).getCid() + ","+list.get(i + 1).getCid();
				model.duizi.add(s);
				i = i + 1;
			}
		}
	}
	
	/**
	 * 获得所有的3带
	 * @param list
	 * @param model
	 */
	public static void getThree(List<Card> list, CardModel model) {
		// 连续3张相同
		for (int i = 0, len = list.size(); i < len; i++) {
			if (i + 2 < len&&list.get(i).getName() == list.get(i + 2).getName()) {
				String s = list.get(i).getCid() + ",";
				s += list.get(i + 1).getCid() + ",";
				s += list.get(i + 2).getCid();
				model.sandai.add(s);
				i = i + 2;
			}
		}
	}
	
	/**
	 * 获得炸弹
	 * @param list 排序好的牌集合
	 * @param model
	 */
	public static void getBoomb(List<Card> list, CardModel model) {
		if(list.size()<1)
			return;
		// 王炸
		if (list.size() >= 2 && (list.get(list.size()-1)).getName() == 16&& list.get(list.size()-2).getName() == 17) {
			model.zhadan.add(list.get(0).getName() + "," + list.get(1).getName()); // 按名字加入
		}
		// 一般的炸弹
		for (int i = 0, len = list.size(); i < len; i++) {
			if (i + 3 < len&& list.get(i).getName() == list.get(i + 3).getName()) {
				String s = list.get(i).getCid() + ",";
				s += list.get(i + 1).getCid() + ",";
				s += list.get(i + 2).getCid() + ",";
				s += list.get(i + 3).getCid();
				model.zhadan.add(s);
				i = i + 3;
			}
		}
	}
	
	/**
	 * 获得双顺 ,从对子中寻找
	 * 三对或更多的连续对牌，如：334455、7788991010JJ。不包括 2 点和双王。
	 * @param list
	 * @param model
	 */
	public static void getshuangshun(List<Card> list,CardModel model) {
		
		List<String> l = model.duizi;
		if (l.size() < 3){
			return;
		}
		String[] ids = new String[l.size()];
		int[] names=new int[l.size()];
		for (int i = 0, len = l.size(); i < len; i++) {
			ids = l.get(i).split(",");
			for(int j=0,n=list.size();j<n;j++){
				if(list.get(j).getCid().equals(ids[0])){
					names[i]=list.get(j).getName();
					break;
				}
			}
		}
		
		// 1,2,3,4 13,9,8,7,6
		for (int i = 0, len = l.size(); i < len; i++) {
			int k = i;
			for (int j = i; j < len; j++) {
				if (names[i] - names[j] == j - i){
					k = j;
				}
			}
			
			// k=4 i=1
			if (k - i >= 2){
				// 说明从i到k是连队
				String ss = "";
				for (int j = i; j < k; j++) {
					ss += l.get(j) + ",";
				}
				ss += l.get(k);
				model.shuangshun.add(ss);
				i = k;
			}
		}
	}
	
	/**
	 * 获得飞机  从model里面的3带找
	 * 三顺(飞机)：二个或更多的连续三张牌，如：333444 、555666777888。不包括 2 点和双王。
	 * 飞机带翅膀：三顺＋同数量的单牌或同数量的对牌。如：444555+79 或333444555+7799JJ
	 */
	public static void getPlane(List<Card> list, CardModel model) {
		List<String> l = model.sandai;
		if (l.size() < 2){
			return;
		}
		
		String[] ids = new String[l.size()];
		int[] names=new int[l.size()];
		for (int i = 0, len = l.size(); i < len; i++) {
			ids = l.get(i).split(",");
			for(int j=0,n=list.size();j<n;j++){
				if(list.get(j).getCid().equals(ids[0])){
					names[i]=list.get(j).getName();
					break;
				}
			}
		}
		
		for (int i = 0, len = l.size(); i < len; i++) {
			int k = i;
			for (int j = i; j < len; j++) {
				if (names[i] - names[j] == j - i)
					k = j;
			}
			if (k != i) {// 说明从i到k是飞机
				String ss = "";
				for (int j = i; j < k; j++) {
					ss += l.get(j) + ",";
				}
				ss += l.get(k);
				model.feiji.add(ss);
				i = k;
			}
		}
		
	}
	
	
	/**
	 * 获得连子（单顺）
	 * @param list
	 * @param model
	 */
	public static void getdanshun(List<Card> list, CardModel model) {
		if (list.size() < 5){
			return;
		}
		// 先要把所有不重复的牌归为一类，防止3带，对子影响
		List<Card> list2 = new ArrayList<Card>(list);
		List<Card> temp = new ArrayList<Card>();
		
		List<Integer> integers = new ArrayList<Integer>();
		
		for (Card card : list2) {
			if (integers.indexOf(card.getName()) < 0&&card.getName()<=15) {
				integers.add(card.getName());
				temp.add(card);
			}
		}
		Collections.sort(temp);
		for (int i = 0, len = temp.size(); i < len; i++) {
			int k = i;
			for (int j = i; j < len; j++) {
				if (temp.get(i).getName() - temp.get(j).getName() == j- i) {
					k = j;
				}
			}
			
			if (k - i >= 4) {
				String s = "";
				for (int j = i; j < k; j++) {
					s += temp.get(j).getCid() + ",";
				}
				s += temp.get(k).getCid();
				model.danshun.add(s);
				i = k;
			}
		}
	}
	
	/**
	 * 获得所有单牌
	 * @param list
	 * @param model
	 */
	public static void getSingle(List<Card> list, CardModel model){
		
		for (int i = 0, len = list.size(); i < len; i++) {
			model.danzi.add(list.get(i).getCid());
		}
		//删除对子
		delSingle(model.duizi,model);
		//删除3带
		delSingle(model.sandai,model);
		//炸弹
		delSingle(model.zhadan,model);
		//删除连子
		delSingle(model.danshun,model);
		//删除双顺
		delSingle(model.shuangshun,model);
		//删除飞机
		delSingle(model.feiji,model);
	}
	
	/**
	 * 删除其他牌
	 * @param list
	 * @param model
	 */
	public static void delSingle(List<String> list,CardModel model){
		for(int i=0,len=list.size();i<len;i++)
		{
			String s[]=list.get(i).split(",");
			for(int j=0;j<s.length;j++){
				model.danzi.remove(s[j]);
			}
		}
	}
	
	/**
	 * 计算手数
	 * @param model
	 * @return
	 */
	public static int getTimes(CardModel model){
		int count=0;
		count+=model.zhadan.size()+model.sandai.size()+model.duizi.size();
		count+=model.feiji.size()+model.shuangshun.size()+model.danshun.size();
		
		int temp=0;
		temp=model.danzi.size()-model.sandai.size()*2-model.zhadan.size()*3-model.feiji.size()*3;
		count+=temp;
		return count;
	}
	
	/**
	 * 计算权值   单1 对子2 带3 炸弹10 飞机7 双顺5 顺子4
	 * @param model
	 * @return
	 */
	public static int getCountValues(CardModel model){
		int count=0;
		count+=model.danzi.size()+model.duizi.size()*2+model.sandai.size()*3;
		count+=model.zhadan.size()*10+model.feiji.size()*7+model.shuangshun.size()*5+model.danshun.size()*4;
		return count;
	}
}
