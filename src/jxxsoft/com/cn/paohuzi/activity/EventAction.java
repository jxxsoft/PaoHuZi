package jxxsoft.com.cn.paohuzi.activity;

import java.util.List;
import java.util.Random;

import jxxsoft.com.cn.paohuzi.app.MainApplication;
import jxxsoft.com.cn.paohuzi.bean.Card;
import jxxsoft.com.cn.paohuzi.bean.GameGrab;
import jxxsoft.com.cn.paohuzi.bean.GameStep;
import jxxsoft.com.cn.paohuzi.bean.PlayerStatus;
import jxxsoft.com.cn.paohuzi.util.DialogUtil;


import android.content.Context;
import android.view.MotionEvent;

/**
 * 事件处理类
 * @author Administrator
 *
 */
public class EventAction {

	private MotionEvent event;
	private SingleGameView gameView;
	private Context context;
	public EventAction(Context context,SingleGameView view, MotionEvent event) {
		this.context=context;
		this.event = event;
		this.gameView = view;
	}
	
	/**
	 * 判断是否按退出 按钮，进行相应业务处理
	 * @param sx 起始x坐标
	 * @param sy 起始y坐标
	 * @param ex 结束x坐标
	 * @param ey 结束y坐标
	 */
	public void exitButton(int sx,int sy,int ex,int ey){
		float x=event.getX();
		float y=event.getY();
		if((x>sx)&&(y>sy)&&(x<ex)&&(y<ey)){
			MainApplication.getInstance().play("SpecOk.ogg");
			System.out.println("退出按钮被点击：");
			
			DialogUtil.exitGameDialog(context);
			
		}
	}
	
	
	
	/**
	 * 判断是否按设置 按钮，进行相应业务处理
	 * @param sx 起始x坐标
	 * @param sy 起始y坐标
	 * @param ex 结束x坐标
	 * @param ey 结束y坐标
	 */
	public void setButton(int sx,int sy,int ex,int ey){
		float x=event.getX();
		float y=event.getY();
		if((x>sx)&&(y>sy)&&(x<ex)&&(y<ey)){
			MainApplication.getInstance().play("SpecOk.ogg");
			System.out.println("设置按钮被点击");
			DialogUtil.setupDialog(context,2);
		}
	}
	
	/**
	 * 准备按钮
	 * @param sx
	 * @param sy
	 * @param ex
	 * @param ey
	 */
	public void setPrepareButtont(int sx,int sy,int ex,int ey){
		if(gameView.gameStep!=GameStep.ready){
			return;
		}
		float x=event.getX();
		float y=event.getY();
		if((x>sx)&&(y>sy)&&(x<ex)&&(y<ey)){
			MainApplication.getInstance().play("SpecOk.ogg");
			System.out.println("准备按钮被点击 :"+event.getAction());
			
			
			
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				gameView.prepareButtonbgBitmap=gameView.prepareButtondownbgBitmap;
			}else if(event.getAction()==MotionEvent.ACTION_UP){
				gameView.prepareButtonbgBitmap=gameView.prepareButtonupbgBitmap;
				//发牌状态
				gameView.gameStep=GameStep.deal;
			}
			gameView.repaint=true;
		}
		if(event.getAction()==MotionEvent.ACTION_MOVE){
			if(((x>sx)&&(y>sy)&&(x<ex)&&(y<ey))==false){
				gameView.repaint=true;
				gameView.prepareButtonbgBitmap=gameView.prepareButtonupbgBitmap;
			}
		}
	}
	
	/**
	 * 每张牌单击
	 */
	public void setCard(){
		if(gameView.gameStep!=GameStep.landlords){
			return;
		}
		System.out.println("点击扑克牌");
		float x=event.getX();
		float y=event.getY();
		List<Card> cards=gameView.player2.getCards();
		boolean selectstatus=false;
		for(Card card:cards){
			if((x>card.getX())&&(y>card.getY())&&(x<card.getX()+card.getSw())&&(y<card.getY()+card.getHeight())){
				card.setClicked(!card.isClicked());
				gameView.repaint=true;
				MainApplication.getInstance().play("SpecSelectCard.ogg");
			}
			if(card.isClicked()){
				selectstatus=true;
			}
		}
		if(selectstatus){
			gameView.pstatus=PlayerStatus.select;
			gameView.repaint=true;
		}else{
			gameView.pstatus=PlayerStatus.none;
			gameView.repaint=true;
		}
		
		
		
	}
	
	/**
	 * 不抢、不叫按钮
	 * @param sx
	 * @param sy
	 * @param ex
	 * @param ey
	 */
	public void setGrabGameBQButton(int sx,int sy,int ex,int ey){
		if(gameView.gameStep!=GameStep.grab){
			return;
		}
		float x=event.getX();
		float y=event.getY();
		if((x>sx)&&(y>sy)&&(x<ex)&&(y<ey)){
			//标示玩家2已经确定不叫或者不抢动作
			gameView.player2grab=true;
			if(gameView.dizhubei==0){
				gameView.player2.setStatus(GameGrab.bj);
				MainApplication.getInstance().play("Woman_NoOrder.mp3");
			}else{
				gameView.player2.setStatus(GameGrab.bq);
				MainApplication.getInstance().play("Woman_NoRob.mp3");
			}
			System.out.println("点击不抢");
			
			
		}
	}
	/**
	 * 抢、叫地主按钮
	 * @param sx
	 * @param sy
	 * @param ex
	 * @param ey
	 */
	public void setGrabGameQDZButton(int sx,int sy,int ex,int ey){
		if(gameView.gameStep!=GameStep.grab){
			return;
		}
		float x=event.getX();
		float y=event.getY();
		if((x>sx)&&(y>sy)&&(x<ex)&&(y<ey)){
			gameView.player2grab=true;
			if(gameView.dizhubei==0){
				gameView.dizhubei=1;
				gameView.player2.setStatus(GameGrab.jdz);
				MainApplication.getInstance().play("Woman_Order.mp3");
			}else{
				gameView.dizhubei=gameView.dizhubei*2;
				gameView.player2.setStatus(GameGrab.qdz);
				MainApplication.getInstance().play("Woman_Rob3.mp3");
			}
			System.out.println("点击抢");
			gameView.player2.setCurrself(gameView.dizhubei);
			
		}
	}
	
	/**
	 * 斗地主  出牌按钮
	 * @param sx
	 * @param sy
	 * @param ex
	 * @param ey
	 */
	public void setlandlordsGameQDZButton(int sx,int sy,int ex,int ey){
		if(gameView.gameStep!=GameStep.landlords){
			return;
		}
		float x=event.getX();
		float y=event.getY();
		if((x>sx)&&(y>sy)&&(x<ex)&&(y<ey)){
			if((gameView.player2out==false)&&(gameView.pstatus==PlayerStatus.select)){
				MainApplication.getInstance().play("SpecOk.ogg");
				List<Card> cards=gameView.player2.getCards();
				gameView.player2.clearOut();
				
				for(Card card:cards){
					if(card.isClicked()){
						System.out.println("选择牌为:"+card.getName());
						gameView.player2.addOutcards(card);
						
					}
					
				}
				gameView.player2.removeCards(gameView.player2.getOutcards());
				gameView.repaint=true;
				gameView.pstatus=PlayerStatus.send;
				gameView.player2out=true;
				gameView.player2.setPlay(true);
			}
		}
	}
	
	/**
	 * 
	 * 提示按钮
	 * @param sx
	 * @param sy
	 * @param ex
	 * @param ey
	 */
	public void setHintGameQDZButton(int sx,int sy,int ex,int ey){
		if(gameView.gameStep!=GameStep.landlords){
			return;
		}
		float x=event.getX();
		float y=event.getY();
		if((x>sx)&&(y>sy)&&(x<ex)&&(y<ey)){
			if(gameView.player2out==false){
				MainApplication.getInstance().play("SpecOk.ogg");
				List<Card> cards=gameView.player2.getCards();
				gameView.player2.clearOut();
				
				for(Card card:cards){
					card.setClicked(false);
					
				}
				//需智能算法
				if(cards.size()>0){
					cards.get(new Random().nextInt(cards.size())).setClicked(true);
				}
				gameView.pstatus=PlayerStatus.send;
				gameView.repaint=true;
			}
		}
	}
	
	/**
	 * 重选选择按钮
	 * @param sx
	 * @param sy
	 * @param ex
	 * @param ey
	 */
	public void setResetGameQDZButton(int sx,int sy,int ex,int ey){
		if(gameView.gameStep!=GameStep.landlords){
			return;
		}
		float x=event.getX();
		float y=event.getY();
		if((x>sx)&&(y>sy)&&(x<ex)&&(y<ey)){
			if((gameView.player2out==false)&&(gameView.pstatus==PlayerStatus.select)){
				MainApplication.getInstance().play("SpecOk.ogg");
				List<Card> cards=gameView.player2.getCards();
				for(Card card:cards){
					card.setClicked(false);					
				}
				gameView.repaint=true;
			}
		}
	}
	/**
	 * 不出牌按钮
	 * @param sx
	 * @param sy
	 * @param ex
	 * @param ey
	 */
	public void setNotLandlordsGameQDZButton(int sx,int sy,int ex,int ey){
		if(gameView.gameStep!=GameStep.landlords){
			return;
		}
		float x=event.getX();
		float y=event.getY();
		if((x>sx)&&(y>sy)&&(x<ex)&&(y<ey)){
			if(gameView.player2out==false){
				MainApplication.getInstance().play("SpecOk.ogg");
				gameView.pstatus=PlayerStatus.send;
				gameView.player2out=true;
				gameView.player2.setPlay(false);
				gameView.repaint=true;
			}
		}
	}
	
	
}
