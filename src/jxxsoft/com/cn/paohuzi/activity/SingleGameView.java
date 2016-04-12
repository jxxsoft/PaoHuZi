package jxxsoft.com.cn.paohuzi.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import jxxsoft.com.cn.paohuzi.R;
import jxxsoft.com.cn.paohuzi.app.MainApplication;
import jxxsoft.com.cn.paohuzi.bean.Card;
import jxxsoft.com.cn.paohuzi.bean.GameGrab;
import jxxsoft.com.cn.paohuzi.bean.GameStep;
import jxxsoft.com.cn.paohuzi.bean.Player;
import jxxsoft.com.cn.paohuzi.bean.PlayerStatus;
import jxxsoft.com.cn.paohuzi.bean.ScreenType;
import jxxsoft.com.cn.paohuzi.util.ImageUtil;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 
 * 单击游戏视图
 * 继承SurfaceView，实现SurfaceHolder.Callback和Runnable
 * 
 *实现Runnable  作用：用于绘制界面线程
 *
 */
public class SingleGameView extends SurfaceView implements SurfaceHolder.Callback,Runnable  {

	//获得MainApplication实例
	private MainApplication app=MainApplication.getInstance();
	//获得Asset资源管理器
	private AssetManager assetManager;
	//分辨率大小
	private ScreenType screenType;
	//视图控制器
	private SurfaceHolder surfaceHolder=null;
	//Handler对象
	private Handler handler=null;
	//画布
	private Canvas canvas=null;
	//背景图像
	private Bitmap bgBitmap=null;
	//初始化是的玩家头像图标
	private Bitmap initHeadBitmap=null;
	//退出图标
	private Bitmap exitBitmap=null;
	//设置图标
	private Bitmap setupBitmap=null;
	//牌背景图标
	private Bitmap cardBgBitmap=null;
	//牌背景图标
	private Bitmap cardbeforeBitmap=null;
	//准备按钮文字
	private Bitmap prepareButtontextBitmap=null;
	//当前准备按钮背景
	public Bitmap prepareButtonbgBitmap=null;
	//准备没有按下按钮背景
	public Bitmap prepareButtonupbgBitmap=null;
	//准备按下按钮背景
	public Bitmap prepareButtondownbgBitmap=null;
	//准备好图标
	public Bitmap prepareButtonokBitmap=null;
	//头像图标
	public List<Bitmap> HeaderBitmaps=new ArrayList<Bitmap>();
	//数字图标
	public List<Bitmap> numberBitmaps=new ArrayList<Bitmap>();
	
	//牌个数数字图标
	public List<Bitmap> cardNumberBitmaps=new ArrayList<Bitmap>();
		
	//倍字图像
	public Bitmap beiBitmap=null;
	
	
	// 屏幕宽度和高度
	private	int screen_height=0;
	private	int screen_width=0;
	//游戏线程
	private Thread gameThread=null;
	//绘图线程
	private Thread drawThread=null;
	
	
	////轮流
	private int turn=-1;
	
	//是否重绘
	public Boolean repaint=false;
	//是否开始游戏
	private Boolean start=false;
	
	//上下文
	private Context appContext=null;
	
	//游戏状态
	public GameStep gameStep=GameStep.init;
	
	// 牌图片资源
	private Bitmap[] cardnumblackBitmap = new Bitmap[13];//黑色数字
	private Bitmap[] cardnumredBitmap = new Bitmap[13];//红色数字
	private Bitmap[] cardlogoBitmap = new Bitmap[4];//图标
	private Bitmap  dwBitmap;//大王
	private Bitmap xwBitmap;//小王
	
	private Bitmap  dwtopBitmap;//大王 三張牌
	private Bitmap xwtopBitmap;//小王 三張牌
	
	private Bitmap playCardBitmap;//玩家1 3的牌背景
	private Bitmap cardFaceBitmap;//玩家2的 牌正背景
	// 牌对象
	private Card card[] = new Card[54];
	//地主牌
	private List<Card> dizhuList=new ArrayList<Card>();

	//玩家1、3是否已经进入出牌动作
	public  boolean player1out=false;
	public  boolean player3out=false;
	
	//玩家2 自己的状态
	public PlayerStatus pstatus=PlayerStatus.none;
	public  boolean player2out=false;
	
	//玩家信息 ：左边 电脑 ，中间 自己 ， 右边 电脑
	public Player player1=new Player(1,false);
	public Player player2=new Player(2,true);//设置自己
	public Player player3=new Player(3,false);
	
	//下注倍数
	public	int dizhubei=1;
	//轮到谁抢地主
	public int grabindex=0;
	//是否确定第一个叫地主的
	private boolean firstGrab=false;
	//设置循环抢地址
	private int[] nextGrab={1,2,0,1};
	private	Player[] players={player1,player2,player3};
	//设置玩家2是否已经抢地主
	public boolean player2grab=false;
	
	//文字\按钮背景图像
	private Bitmap[] gramTextBitmap = new Bitmap[19];//图标
	
	//游戏结束的谁输谁赢祝贺
	private Bitmap[] overGameBitmaps=new Bitmap[4];
	private Bitmap overGamecurrBitmap=null;
	
	/**
	 * 构造方法
	 * @param context 上下文
	 * @param handler handler对象
	 */
	public SingleGameView(Context context,Handler handler,ScreenType screenType) {
		super(context);
		assetManager=context.getAssets();
		//当前视图获得焦点
		setFocusable(true);
		//赋值
		this.screenType=screenType;
		this.appContext=context;
		this.handler=handler;
		//获得视图控制器，赋值
		surfaceHolder = this.getHolder();
		//给视图控制器添加监听
		surfaceHolder.addCallback(this);
	}
	
	/**
	 * 初始化牌  创建54张扑克牌
	 */
	public void  initCard(){
		
		//A 2
		card[0]=new Card(cardnumblackBitmap[0], cardlogoBitmap[0],"c1", 14);
		card[1]=new Card(cardnumblackBitmap[0], cardlogoBitmap[1],"c2", 14);
		card[2]=new Card(cardnumblackBitmap[0], cardlogoBitmap[2],"c3", 14);
		card[3]=new Card(cardnumblackBitmap[0], cardlogoBitmap[3],"c4", 14);
		
		card[4]=new Card(cardnumblackBitmap[1], cardlogoBitmap[0],"c5", 15);
		card[5]=new Card(cardnumblackBitmap[1], cardlogoBitmap[1],"c6", 15);
		card[6]=new Card(cardnumblackBitmap[1], cardlogoBitmap[2],"c7", 15);
		card[7]=new Card(cardnumblackBitmap[1], cardlogoBitmap[3],"c8", 15);
		
		//从3到K
		for(int i=2;i<13;i++){
			for(int j=0;j<4;j++){
				if(j<2){
					card[i*4+j]=new Card(cardnumblackBitmap[i], cardlogoBitmap[j],"c"+(4*i+j+1), i+1);
				}else{
					card[i*4+j]=new Card(cardnumredBitmap[i], cardlogoBitmap[j],"c"+(4*i+j+1), i+1);
				}
			}
		}
		
		
		
		//最后小王，大王
		card[52]=new Card(xwBitmap, xwBitmap, "c"+53,16);
		card[53]=new Card(dwBitmap, dwBitmap, "c"+54,17);

	}

	public void initLowBitMap(){

		//背景
		bgBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.background);
		

		try {
			initHeadBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/logo_unknown.png")),(float)(1.0/3));
			
			exitBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/game_icon_exit.png")),(float)(1.0/3));
			setupBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/game_icon_setting.png")),(float)(1.0/3));
			cardBgBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/poke_back_header.png")),(float)(1.0/3));
			
			prepareButtontextBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_ready.png")),(float)(1.0/3));
			prepareButtonupbgBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/big_green_btn.png")),(float)(1.0/3));
			prepareButtonbgBitmap=prepareButtonupbgBitmap;
			prepareButtondownbgBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/big_green_btn_down.png")),(float)(1.0/3));
			prepareButtonokBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/ready.png")),(float)(1.0/3));
			//数字图片
			for(int i=0;i<10;i++){
				numberBitmaps.add(ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/beishu_"+i+".png")),(float)(1.0/3)));
			}
			
			//倍字图像
			beiBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/game_icon_bei.png")),(float)(1.0/3));
			
			for(int n=0;n<10;n++){
				cardNumberBitmaps.add(ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/card_count_"+n+".png")),(float)(1.0/3)));
			}
			
			for(int n=0;n<13;n++){
				//big_black_1.png
				cardnumblackBitmap[n]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/big_black_"+(n+1)+".png")),(float)(3.0/8));
			}
			for(int n=0;n<13;n++){
				//big_red_1.png
				cardnumredBitmap[n]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/big_red_"+(n+1)+".png")),(float)(3.0/8));
			}
			
			cardlogoBitmap[0]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/mark_grass_big.png")),(float)(3.0/8));//黑
			cardlogoBitmap[1]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/mark_peach_big.png")),(float)(3.0/8));//黑
			cardlogoBitmap[2]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/mark_heart_big.png")),(float)(3.0/8));//红
			cardlogoBitmap[3]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/mark_square_big.png")),(float)(3.0/8));//红
			
			dwBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/dawang_big.png")),(float)(3.0/8));
			xwBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/xiaowang_big.png")),(float)(3.0/8));
			cardFaceBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/poke_gb_big.png")),(float)(3.0/8));
			
			dwtopBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/dawang_header.png")),(float)(1.0/3));
			xwtopBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/xiaowang_header.png")),(float)(1.0/3));
			
			playCardBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/poke_back_small.png")),(float)(1.0/3));
			
			
			//抢地主
			gramTextBitmap[0]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/string_bu.png")),(float)(1.0/3));
			gramTextBitmap[1]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/string_chu.png")),(float)(1.0/3));
			gramTextBitmap[2]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/string_di.png")),(float)(1.0/3));
			gramTextBitmap[3]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/string_jiao.png")),(float)(1.0/3));
			gramTextBitmap[4]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/string_qiang.png")),(float)(1.0/3));
			gramTextBitmap[5]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/string_zhu.png")),(float)(1.0/3));
			
			gramTextBitmap[6]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_bj.png")),(float)(1.0/3));
			gramTextBitmap[7]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_bq.png")),(float)(1.0/3));
			gramTextBitmap[8]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_cue.png")),(float)(1.0/3));
			gramTextBitmap[9]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_jdz.png")),(float)(1.0/3));
			gramTextBitmap[10]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_pass.png")),(float)(1.0/3));
			gramTextBitmap[11]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_qdz.png")),(float)(1.0/3));
			gramTextBitmap[12]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_ready.png")),(float)(1.0/3));
			gramTextBitmap[13]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_repick.png")),(float)(1.0/3));
			gramTextBitmap[14]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_send_card.png")),(float)(1.0/3));
			
			gramTextBitmap[15]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/blue_btn.png")),(float)(1.0/3));
			gramTextBitmap[16]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/green_btn.png")),(float)(1.0/3));
			gramTextBitmap[17]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/red_btn.png")),(float)(1.0/3));
			gramTextBitmap[18]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/other_btn_disable.png")),(float)(1.0/3));
			
			//头像图标
			HeaderBitmaps.add(ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/logo_dizhu.png")),(float)(1.0/3)));
			HeaderBitmaps.add(ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/logo_dizhu_w.png")),(float)(1.0/3)));
			HeaderBitmaps.add(ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/logo_nongmin.png")),(float)(1.0/3)));
			HeaderBitmaps.add(ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/logo_nongmin_w.png")),(float)(1.0/3)));
			
			//牌正面背景
			cardbeforeBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/poke_gb_header.png")),(float)(1.0/3));
			
			overGameBitmaps[0]=BitmapFactory.decodeStream(assetManager.open("images/text_dizhu_lose.png"));
			overGameBitmaps[1]=BitmapFactory.decodeStream(assetManager.open("images/text_dizhu_win.png"));
			overGameBitmaps[2]=BitmapFactory.decodeStream(assetManager.open("images/text_nongmin_lose.png"));
			overGameBitmaps[3]=BitmapFactory.decodeStream(assetManager.open("images/text_nongmin_win.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initMiddleBitMap(){

		//背景
		bgBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.background);
		

		try {
			initHeadBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/logo_unknown.png")),(float)(2.0/3));
			
			exitBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/game_icon_exit.png")),(float)(2.0/3));
			setupBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/game_icon_setting.png")),(float)(2.0/3));
			cardBgBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/poke_back_header.png")),(float)(2.0/3));
			
			prepareButtontextBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_ready.png")),(float)(2.0/3));
			prepareButtonupbgBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/big_green_btn.png")),(float)(2.0/3));
			prepareButtonbgBitmap=prepareButtonupbgBitmap;
			prepareButtondownbgBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/big_green_btn_down.png")),(float)(2.0/3));
			prepareButtonokBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/ready.png")),(float)(2.0/3));
			//数字图片
			for(int i=0;i<10;i++){
				numberBitmaps.add(ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/beishu_"+i+".png")),(float)(2.0/3)));
			}
			
			//倍字图像
			beiBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/game_icon_bei.png")),(float)(2.0/3));
			
			for(int n=0;n<10;n++){
				cardNumberBitmaps.add(ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/card_count_"+n+".png")),(float)(2.0/3)));
			}
			
			for(int n=0;n<13;n++){
				//big_black_1.png
				cardnumblackBitmap[n]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/big_black_"+(n+1)+".png")),(float)(4.5/8));
			}
			for(int n=0;n<13;n++){
				//big_red_1.png
				cardnumredBitmap[n]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/big_red_"+(n+1)+".png")),(float)(4.5/8));
			}
			
			cardlogoBitmap[0]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/mark_grass_big.png")),(float)(4.5/8));//黑
			cardlogoBitmap[1]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/mark_peach_big.png")),(float)(4.5/8));//黑
			cardlogoBitmap[2]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/mark_heart_big.png")),(float)(4.5/8));//红
			cardlogoBitmap[3]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/mark_square_big.png")),(float)(4.5/8));//红
			
			dwBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/dawang_big.png")),(float)(4.5/8));
			xwBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/xiaowang_big.png")),(float)(4.5/8));
			cardFaceBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/poke_gb_big.png")),(float)(4.5/8));
			
			dwtopBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/dawang_header.png")),(float)(2.0/3));
			xwtopBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/xiaowang_header.png")),(float)(2.0/3));
			
			playCardBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/poke_back_small.png")),(float)(2.0/3));
			
			
			//抢地主
			gramTextBitmap[0]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/string_bu.png")),(float)(2.0/3));
			gramTextBitmap[1]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/string_chu.png")),(float)(2.0/3));
			gramTextBitmap[2]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/string_di.png")),(float)(2.0/3));
			gramTextBitmap[3]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/string_jiao.png")),(float)(2.0/3));
			gramTextBitmap[4]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/string_qiang.png")),(float)(2.0/3));
			gramTextBitmap[5]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/string_zhu.png")),(float)(2.0/3));
			
			gramTextBitmap[6]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_bj.png")),(float)(2.0/3));
			gramTextBitmap[7]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_bq.png")),(float)(2.0/3));
			gramTextBitmap[8]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_cue.png")),(float)(2.0/3));
			gramTextBitmap[9]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_jdz.png")),(float)(2.0/3));
			gramTextBitmap[10]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_pass.png")),(float)(2.0/3));
			gramTextBitmap[11]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_qdz.png")),(float)(2.0/3));
			gramTextBitmap[12]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_ready.png")),(float)(2.0/3));
			gramTextBitmap[13]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_repick.png")),(float)(2.0/3));
			gramTextBitmap[14]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/text_send_card.png")),(float)(2.0/3));
			
			gramTextBitmap[15]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/blue_btn.png")),(float)(2.0/3));
			gramTextBitmap[16]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/green_btn.png")),(float)(2.0/3));
			gramTextBitmap[17]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/red_btn.png")),(float)(2.0/3));
			gramTextBitmap[18]=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/other_btn_disable.png")),(float)(2.0/3));
			
			//头像图标
			HeaderBitmaps.add(ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/logo_dizhu.png")),(float)(2.0/3)));
			HeaderBitmaps.add(ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/logo_dizhu_w.png")),(float)(2.0/3)));
			HeaderBitmaps.add(ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/logo_nongmin.png")),(float)(2.0/3)));
			HeaderBitmaps.add(ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/logo_nongmin_w.png")),(float)(2.0/3)));
			
			//牌正面背景
			cardbeforeBitmap=ImageUtil.zoomBitmap(BitmapFactory.decodeStream(assetManager.open("images/poke_gb_header.png")),(float)(2.0/3));
			
			overGameBitmaps[0]=BitmapFactory.decodeStream(assetManager.open("images/text_dizhu_lose.png"));
			overGameBitmaps[1]=BitmapFactory.decodeStream(assetManager.open("images/text_dizhu_win.png"));
			overGameBitmaps[2]=BitmapFactory.decodeStream(assetManager.open("images/text_nongmin_lose.png"));
			overGameBitmaps[3]=BitmapFactory.decodeStream(assetManager.open("images/text_nongmin_win.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initLargeBitMap(){

		//背景
		bgBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.background);
		

		try {
			initHeadBitmap=BitmapFactory.decodeStream(assetManager.open("images/logo_unknown.png"));
			
			exitBitmap=BitmapFactory.decodeStream(assetManager.open("images/game_icon_exit.png"));
			setupBitmap=BitmapFactory.decodeStream(assetManager.open("images/game_icon_setting.png"));
			cardBgBitmap=BitmapFactory.decodeStream(assetManager.open("images/poke_back_header.png"));
			
			prepareButtontextBitmap=BitmapFactory.decodeStream(assetManager.open("images/text_ready.png"));
			prepareButtonupbgBitmap=BitmapFactory.decodeStream(assetManager.open("images/big_green_btn.png"));
			prepareButtonbgBitmap=prepareButtonupbgBitmap;
			prepareButtondownbgBitmap=BitmapFactory.decodeStream(assetManager.open("images/big_green_btn_down.png"));
			prepareButtonokBitmap=BitmapFactory.decodeStream(assetManager.open("images/ready.png"));
			//数字图片
			for(int i=0;i<10;i++){
				numberBitmaps.add(BitmapFactory.decodeStream(assetManager.open("images/beishu_"+i+".png")));
			}
			
			//倍字图像
			beiBitmap=BitmapFactory.decodeStream(assetManager.open("images/game_icon_bei.png"));
			
			for(int n=0;n<10;n++){
				cardNumberBitmaps.add(BitmapFactory.decodeStream(assetManager.open("images/card_count_"+n+".png")));
			}
			
			for(int n=0;n<13;n++){
				//big_black_1.png
				cardnumblackBitmap[n]=BitmapFactory.decodeStream(assetManager.open("images/big_black_"+(n+1)+".png"));
			}
			for(int n=0;n<13;n++){
				//big_red_1.png
				cardnumredBitmap[n]=BitmapFactory.decodeStream(assetManager.open("images/big_red_"+(n+1)+".png"));
			}
			
			cardlogoBitmap[0]=BitmapFactory.decodeStream(assetManager.open("images/mark_grass_big.png"));//黑
			cardlogoBitmap[1]=BitmapFactory.decodeStream(assetManager.open("images/mark_peach_big.png"));//黑
			cardlogoBitmap[2]=BitmapFactory.decodeStream(assetManager.open("images/mark_heart_big.png"));//红
			cardlogoBitmap[3]=BitmapFactory.decodeStream(assetManager.open("images/mark_square_big.png"));//红
			dwBitmap=BitmapFactory.decodeStream(assetManager.open("images/dawang_big.png"));
			xwBitmap=BitmapFactory.decodeStream(assetManager.open("images/xiaowang_big.png"));
			dwtopBitmap=BitmapFactory.decodeStream(assetManager.open("images/dawang_header.png"));
			xwtopBitmap=BitmapFactory.decodeStream(assetManager.open("images/xiaowang_header.png"));
			playCardBitmap=BitmapFactory.decodeStream(assetManager.open("images/poke_back_small.png"));
			cardFaceBitmap=BitmapFactory.decodeStream(assetManager.open("images/poke_gb_big.png"));
			
			//抢地主
			gramTextBitmap[0]=BitmapFactory.decodeStream(assetManager.open("images/string_bu.png"));
			gramTextBitmap[1]=BitmapFactory.decodeStream(assetManager.open("images/string_chu.png"));
			gramTextBitmap[2]=BitmapFactory.decodeStream(assetManager.open("images/string_di.png"));
			gramTextBitmap[3]=BitmapFactory.decodeStream(assetManager.open("images/string_jiao.png"));
			gramTextBitmap[4]=BitmapFactory.decodeStream(assetManager.open("images/string_qiang.png"));
			gramTextBitmap[5]=BitmapFactory.decodeStream(assetManager.open("images/string_zhu.png"));
			
			gramTextBitmap[6]=BitmapFactory.decodeStream(assetManager.open("images/text_bj.png"));
			gramTextBitmap[7]=BitmapFactory.decodeStream(assetManager.open("images/text_bq.png"));
			gramTextBitmap[8]=BitmapFactory.decodeStream(assetManager.open("images/text_cue.png"));
			gramTextBitmap[9]=BitmapFactory.decodeStream(assetManager.open("images/text_jdz.png"));
			gramTextBitmap[10]=BitmapFactory.decodeStream(assetManager.open("images/text_pass.png"));
			gramTextBitmap[11]=BitmapFactory.decodeStream(assetManager.open("images/text_qdz.png"));
			gramTextBitmap[12]=BitmapFactory.decodeStream(assetManager.open("images/text_ready.png"));
			gramTextBitmap[13]=BitmapFactory.decodeStream(assetManager.open("images/text_repick.png"));
			gramTextBitmap[14]=BitmapFactory.decodeStream(assetManager.open("images/text_send_card.png"));
			
			gramTextBitmap[15]=BitmapFactory.decodeStream(assetManager.open("images/blue_btn.png"));
			gramTextBitmap[16]=BitmapFactory.decodeStream(assetManager.open("images/green_btn.png"));
			gramTextBitmap[17]=BitmapFactory.decodeStream(assetManager.open("images/red_btn.png"));
			gramTextBitmap[18]=BitmapFactory.decodeStream(assetManager.open("images/other_btn_disable.png"));
			
			//头像图标
			HeaderBitmaps.add(BitmapFactory.decodeStream(assetManager.open("images/logo_dizhu.png")));
			HeaderBitmaps.add(BitmapFactory.decodeStream(assetManager.open("images/logo_dizhu_w.png")));
			HeaderBitmaps.add(BitmapFactory.decodeStream(assetManager.open("images/logo_nongmin.png")));
			HeaderBitmaps.add(BitmapFactory.decodeStream(assetManager.open("images/logo_nongmin_w.png")));
			
			//牌正面背景
			cardbeforeBitmap=BitmapFactory.decodeStream(assetManager.open("images/poke_gb_header.png"));
			
			overGameBitmaps[0]=BitmapFactory.decodeStream(assetManager.open("images/text_dizhu_lose.png"));
			overGameBitmaps[1]=BitmapFactory.decodeStream(assetManager.open("images/text_dizhu_win.png"));
			overGameBitmaps[2]=BitmapFactory.decodeStream(assetManager.open("images/text_nongmin_lose.png"));
			overGameBitmaps[3]=BitmapFactory.decodeStream(assetManager.open("images/text_nongmin_win.png"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 初始化 加载资源图片
	public void initBitMap() {
		
		switch (screenType) {
		case large:
				initLargeBitMap();
			break;
		case middle:
			initMiddleBitMap();
			break;
		case low:
		default:
			initLowBitMap();
			break;
		
		}
		
		
	}
	
	
	public void initData(){
		//清空基本數據
		player1.init();
		player2.init();
		player3.init();
		dizhuList.clear();
		
		
		//设置玩家1、3
		player1out=false;
		player2out=false;
		player3out=false;
		//倍数
		dizhubei=1;
		//玩家2 自己的状态
		pstatus=PlayerStatus.none;
		player2grab=false;
		
		//初始化牌信息
		initCard();
				
		//洗牌
		washCards();
		firstGrab=false;
		grabindex=0;
		
		
		
		//游戏状态设置准备状态
		gameStep=GameStep.ready;
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		//SurfaceView发生更改触发
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//SurfaceView创建成功触发
		//获得屏幕高度、宽度
		screen_height = this.getHeight();
		screen_width = this.getWidth();
		System.out.println("屏幕分辨率："+screen_width+"*"+screen_height);
		// 初始化基本信息 加载资源
		initBitMap();
		
		//游戏开始
		start=true;
		
		// 开始游戏线程，负责游戏业务流程处理
		//在游戏线程中控制绘图线程通过 rapaint标示
		gameThread=new Thread(new Runnable() {
			@Override
			public void run() {
				while(start){
					if(gameStep==GameStep.init){
						initData();
					}
					if(gameStep==GameStep.deal){
							//开始发牌
							app.play("Special_Dispatch.mp3");
							handCards();
							
					}
					
					//抢地主 开始
					if(gameStep==GameStep.grab){
						//设置第一个叫地主的
						if(firstGrab==false){
							//设置抢地主的次序
							Random rd=new Random();
							int sgrab=rd.nextInt(3);
							switch (sgrab) {
							case 0:
								nextGrab=new int[]{0,1,2,0};
								break;
							case 1:
								nextGrab=new int[]{1,2,0,1};
								break;
							case 2:
								nextGrab=new int[]{2,0,1,2};
								break;
							}
							//用于判断是否已经有人第一次叫了地主
							dizhubei=0;
							grabindex=0;
							firstGrab=true;
						}
						
						grabDiZhu();
					}
					
					//斗地主进行中
					if(gameStep==GameStep.landlords){
							switch (turn) {
								case 0:
									playergame1();
									break;
								case 1:
									playergame2();
									break;
								case 2:
									playergame3();
									break;
								default:
									break;
							}
							//判断输赢
							win();
					}
				}
			}
		});
		gameThread.start();
		
		// 开始绘图线程
		drawThread=new Thread(this);
		drawThread.start();
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		start=false;
	}
	
	/**
	 *绘图函数
	 */
	public void onGameDraw(){
		synchronized (surfaceHolder) {
			try {
				//锁定整个视图
				canvas = surfaceHolder.lockCanvas();
				// 画背景
				drawBackground();
				//绘制准备界面
				drawPrepareScreen();
				//绘制关闭、设置按钮等
				drawCommonButton();
				
				//绘制玩家1 玩家3的牌
				drawPlayer1_3();
				//绘制玩家2 自己
				drawPlayer2();
				//绘制抢地主情况
				drawGrabDiZhu();
				//绘制斗地主出牌情况
				drawDDZStatus();
				//绘制输赢情况
				drawGameOverBitmap();
				
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (canvas != null){
					//绘制完毕，进行关闭，提交刷新
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
	
	

	//主要绘图线程
	@Override
	public void run() {
		onGameDraw();
		while (start) {
			if(repaint)
			{
				//绘制界面
				onGameDraw();
				repaint=false;
			}
			//修改50毫秒
			Sleep(50);
		}
	}
	// 画背景
	public void drawBackground() {
			Rect src = new Rect(0, 0, bgBitmap.getWidth(),bgBitmap.getHeight());
			Rect dst = new Rect(0, 0, screen_width, screen_height);
			canvas.drawBitmap(bgBitmap, src, dst, null);
	}
	
	//绘制准备界面
	public void drawPrepareScreen(){
		//绘制玩家头像
		if(gameStep==GameStep.landlords){
			if(player1.isDizhuflag()){
				canvas.drawBitmap(HeaderBitmaps.get(0), 10, 10, null);
			}else{
				canvas.drawBitmap(HeaderBitmaps.get(2), 10, 10, null);
			}
			
			if(player2.isDizhuflag()){
				canvas.drawBitmap(HeaderBitmaps.get(0), 10, screen_height/2, null);
			}else{
				canvas.drawBitmap(HeaderBitmaps.get(2), 10, screen_height/2, null);
			}
			
			if(player3.isDizhuflag()){
				canvas.drawBitmap(HeaderBitmaps.get(0), screen_width-10-HeaderBitmaps.get(0).getWidth(), 10, null);			
			}else{
				canvas.drawBitmap(HeaderBitmaps.get(2), screen_width-10-HeaderBitmaps.get(0).getWidth(), 10, null);			
			}
			
			
			
		}else{
			canvas.drawBitmap(initHeadBitmap, 10, 10, null);
			canvas.drawBitmap(initHeadBitmap, screen_width-10-initHeadBitmap.getWidth(), 10, null);
			canvas.drawBitmap(initHeadBitmap, 10, screen_height/2, null);
		}
		
		
		if(gameStep==GameStep.landlords){
			//绘制三张牌
			
			drawThreeBitmap(dizhuList.get(0), screen_width/3+cardbeforeBitmap.getWidth()+5, 10);
			drawThreeBitmap(dizhuList.get(1), screen_width/3+2*cardbeforeBitmap.getWidth()+10, 10);
			drawThreeBitmap(dizhuList.get(2), screen_width/3+3*cardbeforeBitmap.getWidth()+15, 10);
			
		}else{
			//绘制三张牌
			canvas.drawBitmap(cardBgBitmap, screen_width/3+cardBgBitmap.getWidth()+5, 10, null);
			canvas.drawBitmap(cardBgBitmap, screen_width/3+2*cardBgBitmap.getWidth()+10, 10, null);
			canvas.drawBitmap(cardBgBitmap, screen_width/3+3*cardBgBitmap.getWidth()+15, 10, null);
		}
	
		
		if(gameStep==GameStep.ready){
			//绘制准备按钮
			canvas.drawBitmap(prepareButtonbgBitmap, screen_width/2-prepareButtonbgBitmap.getWidth()/2, screen_height/2, null);
			canvas.drawBitmap(prepareButtontextBitmap, screen_width/2-prepareButtontextBitmap.getWidth()/2, screen_height/2+prepareButtonbgBitmap.getHeight()/2-prepareButtontextBitmap.getHeight()/2, null);
		}
		
		if(gameStep==GameStep.ready){
			//准备ok图标
			canvas.drawBitmap(prepareButtonokBitmap, 10+initHeadBitmap.getWidth()/2-prepareButtonokBitmap.getWidth()/2, 20+initHeadBitmap.getHeight(), null);
			canvas.drawBitmap(prepareButtonokBitmap, screen_width-prepareButtonokBitmap.getWidth()-10-(initHeadBitmap.getWidth()/2-prepareButtonokBitmap.getWidth()/2),20+initHeadBitmap.getHeight(), null);
			
		}
		
		if(dizhubei<10){
			//绘制数字图标 
			canvas.drawBitmap(numberBitmaps.get(dizhubei), screen_width/3+4*cardBgBitmap.getWidth()+30, 10+cardBgBitmap.getHeight()/2-numberBitmaps.get(dizhubei).getHeight()/2, null);
			//绘制倍字 beiBitmap
			canvas.drawBitmap(beiBitmap, screen_width/3+4*cardBgBitmap.getWidth()+30+numberBitmaps.get(dizhubei).getWidth(), 10+cardBgBitmap.getHeight()/2-beiBitmap.getHeight()/2, null);
			
		}else{
			int a=dizhubei/10;
			int b=dizhubei%10;
			//绘制数字图标 
			canvas.drawBitmap(numberBitmaps.get(a), screen_width/3+4*cardBgBitmap.getWidth()+30, 10+cardBgBitmap.getHeight()/2-numberBitmaps.get(a).getHeight()/2, null);
			canvas.drawBitmap(numberBitmaps.get(b), screen_width/3+4*cardBgBitmap.getWidth()+30+numberBitmaps.get(a).getWidth(), 10+cardBgBitmap.getHeight()/2-numberBitmaps.get(a).getHeight()/2, null);
			//绘制倍字 beiBitmap
			canvas.drawBitmap(beiBitmap, screen_width/3+4*cardBgBitmap.getWidth()+30+numberBitmaps.get(a).getWidth()+numberBitmaps.get(b).getWidth(), 10+cardBgBitmap.getHeight()/2-beiBitmap.getHeight()/2, null);
			
		}
		
		
		
	}
	
	/**
	 * 绘制三张牌
	 * @param card
	 * @param left
	 * @param top
	 */
	public void drawThreeBitmap(Card card,int left,int top){
		//cardbeforeBitmap  判斷是不是地主牌
		switch (card.getName()) {
				case 16:
					canvas.drawBitmap(xwtopBitmap, left, top, null);
					break;
				case 17:
					canvas.drawBitmap(dwtopBitmap, left, top, null);
					break;
				default:
					canvas.drawBitmap(cardbeforeBitmap, left, top, null);
					canvas.drawBitmap(card.getNumbitmap(), left+cardbeforeBitmap.getWidth()/2-card.getNumbitmap().getWidth()/2, top+10, null);
					canvas.drawBitmap(card.getLogobitmap(), left+cardbeforeBitmap.getWidth()/2-card.getLogobitmap().getWidth()/2, top+10+card.getNumbitmap().getHeight(), null);

					break;
				}
		
			}
	
	
	//绘制关闭、设置按钮
	public void drawCommonButton(){
		canvas.drawBitmap(exitBitmap, screen_width/3-exitBitmap.getWidth()-10, 20, null);
		canvas.drawBitmap(setupBitmap, screen_width/3-5, 20, null);
	}
	
	//线程休眠方法
	public void Sleep(long i){
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//洗牌
	public void washCards() {
		//打乱顺序
		for(int i=0;i<100;i++){
			Random random=new Random();
			int a=random.nextInt(54);
			int b=random.nextInt(54);
			Card k=card[a];
			card[a]=card[b];
			card[b]=k;
		}
	}
	
	/**
	 * 发牌业务
	 */
	public void handCards(){
		//开始发牌
		System.out.println("开始发牌");
		int t=0;
		//清除操作
		player1.clear();
		player2.clear();
		player3.clear();
		
		for(int i=0;i<54;i++)
		{
			if(i>50)//地主牌
			{
				//放置地主牌
				dizhuList.add(card[i]);
				continue;
			}
			switch ((t++)%3) {
				case 0:
					//左边玩家
					player1.addCards(card[i]);
					break;
				case 1:
					//我
					player2.addCards(card[i]);
					
					break;
				case 2:
					//右边玩家
					player3.addCards(card[i]);
					break;
			}
			repaint=true;
			Sleep(100);
		}
		System.out.println("结束发牌");
		//重新排序
		player1.sort();
		player2.sort();
		player3.sort();
		System.out.println("排序");
		repaint=true;
		
		//进入抢地主阶段
		gameStep=GameStep.grab;
		
	}
	
	// 玩家1、3的牌
	public void drawPlayer1_3(){
		if(gameStep==GameStep.ready||gameStep==GameStep.init)
		{
			return;
		}
		//发牌图标
		canvas.drawBitmap(playCardBitmap, 5, 20+initHeadBitmap.getHeight(), null);
		//绘制玩家1
		int count=player1.size();
		if(count<10){
			Bitmap nbm=cardNumberBitmaps.get(count);
			canvas.drawBitmap(nbm, 5+playCardBitmap.getWidth()/2-nbm.getWidth()/2, 20+initHeadBitmap.getHeight()+playCardBitmap.getHeight()/2-nbm.getHeight()/2, null);
		}else{
			int x=count/10;//十位的数字
			int y=count%10;//个位的数字
			Bitmap nbm1=cardNumberBitmaps.get(x);
			Bitmap nbm2=cardNumberBitmaps.get(y);
			canvas.drawBitmap(nbm1, 5+playCardBitmap.getWidth()/2-nbm1.getWidth(), 20+initHeadBitmap.getHeight()+playCardBitmap.getHeight()/2-nbm1.getHeight()/2, null);
			canvas.drawBitmap(nbm2,  5+playCardBitmap.getWidth()/2, 20+initHeadBitmap.getHeight()+playCardBitmap.getHeight()/2-nbm1.getHeight()/2, null);
		}
		
		//绘制玩家2
		canvas.drawBitmap(playCardBitmap, screen_width-playCardBitmap.getWidth()-10-(initHeadBitmap.getWidth()/2-playCardBitmap.getWidth()/2),20+initHeadBitmap.getHeight(), null);
		count=player3.size();
		if(count<10){
			Bitmap nbm=cardNumberBitmaps.get(count);
			canvas.drawBitmap(nbm, screen_width-5-nbm.getWidth()-(playCardBitmap.getWidth()/2-nbm.getWidth()/2), 20+initHeadBitmap.getHeight()+playCardBitmap.getHeight()/2-nbm.getHeight()/2, null);
		}else{
			int x=count/10;
			int y=count%10;
			Bitmap nbm1=cardNumberBitmaps.get(x);
			Bitmap nbm2=cardNumberBitmaps.get(y);
			canvas.drawBitmap(nbm1, screen_width-5-playCardBitmap.getWidth()/2-nbm1.getWidth(), 20+initHeadBitmap.getHeight()+playCardBitmap.getHeight()/2-nbm1.getHeight()/2, null);
			canvas.drawBitmap(nbm2,  screen_width-5-playCardBitmap.getWidth()/2, 20+initHeadBitmap.getHeight()+playCardBitmap.getHeight()/2-nbm1.getHeight()/2, null);
			
		}
		
	}
	
	/**
	 * 中间对齐绘制牌 玩家2牌情况
	 */
	public void drawPlayer2(){
		if(gameStep==GameStep.ready||gameStep==GameStep.init)
		{
			return;
		}
		int count=player2.size();
		System.out.println("自己牌数目："+count);
		int w=screen_width/21;
		int span=(screen_width-w*count-(cardFaceBitmap.getWidth()-w))/2;
		Card card=null;
		for(int i=0;i<count;i++){
			card=player2.getCard(i);
			if(card.isClicked()){
				card.setLocationAndSize(span+i*w,screen_height-cardFaceBitmap.getHeight()-25,cardFaceBitmap.getWidth(),cardFaceBitmap.getHeight(),w);
			}else{
				card.setLocationAndSize(span+i*w,screen_height-cardFaceBitmap.getHeight()-5,cardFaceBitmap.getWidth(),cardFaceBitmap.getHeight(),w);
			}
			
			if(card.getName()>15){
				canvas.drawBitmap(card.getNumbitmap(), card.getX(), card.getY(), null);
			}else{
				//绘制背景
				canvas.drawBitmap(cardFaceBitmap,  card.getX(), card.getY(), null);
				
				//绘制上
				canvas.drawBitmap(card.getNumbitmap(),card.getUnx(),card.getUny(),null);
				canvas.drawBitmap(card.getLogobitmap(),card.getUlx(),card.getUly(),null);
				//绘制下
				canvas.drawBitmap(ImageUtil.createBitmapForUpDownLetRight(card.getNumbitmap()),card.getDnx(),card.getDny(),null);
				canvas.drawBitmap(ImageUtil.createBitmapForUpDownLetRight(card.getLogobitmap()),card.getDlx(),card.getDly(),null);
			}
		}
	}
	
	/**
	 * 抢地主 
	 * dizhuFlag：
	 *  0： 玩家1
	 *  1： 玩家2
	 *  2： 玩家3
	 *  
	 *  1 2 0 1   int[]
	 *  
	 *  grabindex  0,1,2,3... 第几步
	 */
	public void grabDiZhu(){
		//System.out.println("玩家"+(nextGrab[grabindex]+1)+"进行抢");
		Player player=null;
		if(grabindex==0){
			player=players[nextGrab[grabindex]];
			switch (nextGrab[grabindex]) {
				case 0://玩家一
					if(player1grabdizhu()){
						dizhubei=1;
						player.setCurrself(dizhubei);
						player.setStatus(GameGrab.jdz);
					}else{
						dizhubei=0;
						player.setCurrself(dizhubei);
						player.setStatus(GameGrab.bj);
					}
					break;
				case 1://玩家二
					while(player2grab==false){
						Sleep(100);
					}
					player2grab=false;
					break;
				case 2://玩家三
					if(player3grabdizhu()){
						dizhubei=1;
						player.setCurrself(dizhubei);
						player.setStatus(GameGrab.jdz);
					}else{
						dizhubei=0;
						player.setCurrself(dizhubei);
						player.setStatus(GameGrab.bj);
					}
					break;
			}
			repaint=true;
			grabindex++;
			Sleep(2000);
		}else if(grabindex==1||grabindex==2){		
			player=players[nextGrab[grabindex]];
			if(dizhubei==0){
				//还没有谁叫地主
				switch (nextGrab[grabindex]) {
					case 0://玩家一
						if(player1grabdizhu()){
							dizhubei=1;
							player.setCurrself(dizhubei);
							player.setStatus(GameGrab.jdz);
						}else{
							dizhubei=0;
							player.setCurrself(dizhubei);
							player.setStatus(GameGrab.bj);
						}
						break;
					case 1://玩家二
						while(player2grab==false){
							Sleep(100);
						}
						player2grab=false;
						break;
					case 2://玩家三
						if(player3grabdizhu()){
							dizhubei=1;
							player.setCurrself(dizhubei);
							player.setStatus(GameGrab.jdz);
						}else{
							dizhubei=0;
							player.setCurrself(dizhubei);
							player.setStatus(GameGrab.bj);
						}
						break;
				}
				repaint=true;
				grabindex++;
				Sleep(2000);
				
			}else{
				switch (nextGrab[grabindex]) {
				case 0://玩家一
					if(player1grabdizhu()){
						dizhubei=dizhubei*2;
						player.setCurrself(dizhubei);
						player.setStatus(GameGrab.qdz);
					}else{
						player.setStatus(GameGrab.bq);
					}
					break;
				case 1://玩家二
					while(player2grab==false){
						Sleep(100);
					}
					player2grab=false;
					break;
				case 2://玩家三
					if(player3grabdizhu()){
						dizhubei=dizhubei*2;
						player.setCurrself(dizhubei);
						player.setStatus(GameGrab.qdz);
					}else{
						player.setStatus(GameGrab.bq);
					}
					break;
				}
				repaint=true;
				grabindex++;
				Sleep(2000);
			}
				
		}else if(grabindex==3){
			player=players[nextGrab[grabindex]];
			if((dizhubei==player.getCurrself())&&(dizhubei!=0)){
				//当前倍数跟自己第一次相同，则，其他没有抢
				grabindex++;
				return;
			}
			
			if(dizhubei==0){
				//还没有谁叫地主
				switch (nextGrab[grabindex]) {
					case 0://玩家一
						player.setStatus(GameGrab.none);
						repaint=true;
						Sleep(500);
						if(player1grabdizhu()){
							dizhubei=1;
							player.setCurrself(dizhubei);
							player.setStatus(GameGrab.jdz);
						}else{
							dizhubei=0;
							player.setCurrself(dizhubei);
							player.setStatus(GameGrab.bj);
						}
						break;
					case 1://玩家二
						while(player2grab==false){
							Sleep(100);
						}
						player2grab=false;
						break;
					case 2://玩家三
						player.setStatus(GameGrab.none);
						repaint=true;
						Sleep(500);
						if(player3grabdizhu()){
							dizhubei=1;
							player.setCurrself(dizhubei);
							player.setStatus(GameGrab.jdz);
						}else{
							dizhubei=0;
							player.setCurrself(dizhubei);
							player.setStatus(GameGrab.bj);
						}
						break;
				}
				repaint=true;
				grabindex++;
				Sleep(2000);
				
			}else{
				switch (nextGrab[grabindex]) {
				case 0://玩家一
					player.setStatus(GameGrab.none);
					repaint=true;
					Sleep(500);
					if(player1grabdizhu()){
						dizhubei=dizhubei*2;
						player.setCurrself(dizhubei);
						player.setStatus(GameGrab.qdz);
					}else{
						player.setStatus(GameGrab.bq);
					}
					break;
				case 1://玩家二
					while(player2grab==false){
						Sleep(100);
					}
					player2grab=false;
					break;
				case 2://玩家三
					player.setStatus(GameGrab.none);
					repaint=true;
					Sleep(500);
					if(player3grabdizhu()){
						dizhubei=dizhubei*2;
						player.setCurrself(dizhubei);
						player.setStatus(GameGrab.qdz);
					}else{
						player.setStatus(GameGrab.bq);
					}
					break;
				}
				repaint=true;
				grabindex++;
				Sleep(2000);
			}
			
			
		}else{
			Sleep(1000);
			if(dizhubei==0){
				repaint=true;
				//设置游戏状态，出牌
				gameStep=GameStep.init;
				return;
			}
			
			//判断当前地主是谁，轮到谁先出牌
			Player dizhu=null;
			if(player1.getCurrself()>player2.getCurrself()&&player1.getCurrself()>player3.getCurrself()){
				player1.setDizhuflag(true);
				turn=0;//轮到谁出牌
				dizhu=player1;
				System.out.println("玩家1地主");
			}else if(player2.getCurrself()>player1.getCurrself()&&player2.getCurrself()>player3.getCurrself()){
				player2.setDizhuflag(true);
				turn=1;
				dizhu=player2;
				System.out.println("玩家2地主");
			}else{
				turn=2;
				dizhu=player3;
				player3.setDizhuflag(true);
				System.out.println("玩家3地主");
			}
			//地主牌交个地址
			dizhu.addCards(dizhuList.get(0));
			dizhu.addCards(dizhuList.get(1));
			dizhu.addCards(dizhuList.get(2));
			//牌进行排序
			dizhu.sort();
			repaint=true;
			//设置游戏状态，出牌
			gameStep=GameStep.landlords;
		}
		
	}
	
	/**
	 * 绘制抢地主情况
	 */
	public void drawGrabDiZhu(){
		//是不是抢地主界面
		if(GameStep.grab==gameStep){
			//输出玩家1
			switch (player1.getStatus()) {
				case jdz:
					canvas.drawBitmap(gramTextBitmap[3],10+playCardBitmap.getWidth(),20+initHeadBitmap.getHeight(),null);
					canvas.drawBitmap(gramTextBitmap[2],10+playCardBitmap.getWidth()+gramTextBitmap[3].getWidth(),20+initHeadBitmap.getHeight(),null);
					canvas.drawBitmap(gramTextBitmap[5],10+playCardBitmap.getWidth()+gramTextBitmap[3].getWidth()+gramTextBitmap[2].getWidth(),20+initHeadBitmap.getHeight(),null);
					break;
	
				case qdz:
					canvas.drawBitmap(gramTextBitmap[4],10+playCardBitmap.getWidth(),20+initHeadBitmap.getHeight(),null);
					canvas.drawBitmap(gramTextBitmap[2],10+playCardBitmap.getWidth()+gramTextBitmap[4].getWidth(),20+initHeadBitmap.getHeight(),null);
					canvas.drawBitmap(gramTextBitmap[5],10+playCardBitmap.getWidth()+gramTextBitmap[4].getWidth()+gramTextBitmap[2].getWidth(),20+initHeadBitmap.getHeight(),null);
					break;
				case bj:
					canvas.drawBitmap(gramTextBitmap[0],10+playCardBitmap.getWidth(),20+initHeadBitmap.getHeight(),null);
					canvas.drawBitmap(gramTextBitmap[3],10+playCardBitmap.getWidth()+gramTextBitmap[3].getWidth(),20+initHeadBitmap.getHeight(),null);
					break;
				case bq:
					canvas.drawBitmap(gramTextBitmap[0],10+playCardBitmap.getWidth(),20+initHeadBitmap.getHeight(),null);
					canvas.drawBitmap(gramTextBitmap[4],10+playCardBitmap.getWidth()+gramTextBitmap[0].getWidth(),20+initHeadBitmap.getHeight(),null);
					break;
				default:
					
					break;
			}
			
			//输出玩家2
			if(grabindex<4&&nextGrab[grabindex]==1){
				if((grabindex==0&&nextGrab[0]==1)||dizhubei==0){
					//不叫
					canvas.drawBitmap(gramTextBitmap[17], screen_width/2-gramTextBitmap[17].getWidth(), screen_height/2, null);
					canvas.drawBitmap(gramTextBitmap[6], screen_width/2-gramTextBitmap[17].getWidth()+(gramTextBitmap[17].getWidth()/2-gramTextBitmap[6].getWidth()/2), screen_height/2+(gramTextBitmap[17].getHeight()/2-gramTextBitmap[6].getHeight()/2), null);
					//叫地主
					canvas.drawBitmap(gramTextBitmap[15], screen_width/2+20, screen_height/2, null);
					canvas.drawBitmap(gramTextBitmap[9], screen_width/2+20+(gramTextBitmap[15].getWidth()/2-gramTextBitmap[9].getWidth()/2), screen_height/2+(gramTextBitmap[15].getHeight()/2-gramTextBitmap[9].getHeight()/2), null);
				}else{
					//不抢
					canvas.drawBitmap(gramTextBitmap[17], screen_width/2-gramTextBitmap[17].getWidth(), screen_height/2, null);
					canvas.drawBitmap(gramTextBitmap[7], screen_width/2-gramTextBitmap[17].getWidth()+(gramTextBitmap[17].getWidth()/2-gramTextBitmap[7].getWidth()/2), screen_height/2+(gramTextBitmap[17].getHeight()/2-gramTextBitmap[7].getHeight()/2), null);
					//抢地主
					canvas.drawBitmap(gramTextBitmap[15], screen_width/2+20, screen_height/2, null);
					canvas.drawBitmap(gramTextBitmap[11], screen_width/2+20+(gramTextBitmap[15].getWidth()/2-gramTextBitmap[11].getWidth()/2), screen_height/2+(gramTextBitmap[15].getHeight()/2-gramTextBitmap[11].getHeight()/2), null);

				}
			}else{
				switch (player2.getStatus()) {
					case jdz:
						canvas.drawBitmap(gramTextBitmap[3],screen_width/2-gramTextBitmap[3].getWidth(),screen_height/2,null);
						canvas.drawBitmap(gramTextBitmap[2],screen_width/2,screen_height/2,null);
						canvas.drawBitmap(gramTextBitmap[5],screen_width/2+gramTextBitmap[2].getWidth(),screen_height/2,null);	
						break;
					case qdz:
						canvas.drawBitmap(gramTextBitmap[4],screen_width/2-gramTextBitmap[4].getWidth(),screen_height/2,null);
						canvas.drawBitmap(gramTextBitmap[2],screen_width/2,screen_height/2,null);
						canvas.drawBitmap(gramTextBitmap[5],screen_width/2+gramTextBitmap[2].getWidth(),screen_height/2,null);
						break;
					case bj:
						canvas.drawBitmap(gramTextBitmap[0],screen_width/2-gramTextBitmap[0].getWidth(),screen_height/2,null);
						canvas.drawBitmap(gramTextBitmap[3],screen_width/2,screen_height/2,null);
						break;
					case bq:
						canvas.drawBitmap(gramTextBitmap[0],screen_width/2-gramTextBitmap[0].getWidth(),screen_height/2,null);
						canvas.drawBitmap(gramTextBitmap[4],screen_width/2,screen_height/2,null);
						break;
					default:
						
						break;
				}
			}
			
			
			//输出玩家3
			switch (player3.getStatus()) {
				case jdz:
					canvas.drawBitmap(gramTextBitmap[3],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[3].getWidth()-gramTextBitmap[2].getWidth()-gramTextBitmap[5].getWidth(),20+initHeadBitmap.getHeight(),null);
					canvas.drawBitmap(gramTextBitmap[2],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[2].getWidth()-gramTextBitmap[5].getWidth(),20+initHeadBitmap.getHeight(),null);
					canvas.drawBitmap(gramTextBitmap[5],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[5].getWidth(),20+initHeadBitmap.getHeight(),null);	
					break;
				case qdz:
					canvas.drawBitmap(gramTextBitmap[4],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[4].getWidth()-gramTextBitmap[2].getWidth()-gramTextBitmap[5].getWidth(),20+initHeadBitmap.getHeight(),null);
					canvas.drawBitmap(gramTextBitmap[2],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[2].getWidth()-gramTextBitmap[5].getWidth(),20+initHeadBitmap.getHeight(),null);
					canvas.drawBitmap(gramTextBitmap[5],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[5].getWidth(),20+initHeadBitmap.getHeight(),null);
					break;
				case bj:
					canvas.drawBitmap(gramTextBitmap[0],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[3].getWidth()-gramTextBitmap[0].getWidth(),20+initHeadBitmap.getHeight(),null);
					canvas.drawBitmap(gramTextBitmap[3],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[3].getWidth(),20+initHeadBitmap.getHeight(),null);
					break;
				case bq:
					canvas.drawBitmap(gramTextBitmap[0],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[4].getWidth()-gramTextBitmap[0].getWidth(),20+initHeadBitmap.getHeight(),null);
					canvas.drawBitmap(gramTextBitmap[4],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[4].getWidth(),20+initHeadBitmap.getHeight(),null);
					break;
				default:
					
					break;
			}
			
			
		}
	}
	
	/**
	 * 玩家1是否确定叫 、抢地主
	 * @return
	 */
	public boolean player1grabdizhu(){
		return new Random().nextBoolean();
	}
	/**
	 * 玩家3是否确定叫 、抢地主
	 * @return
	 */
	public boolean player3grabdizhu(){
		return new Random().nextBoolean();
	}
	
	/**
	 * 绘制扑克牌出牌情况图
	 */
	public void drawDDZStatus(){
		if(gameStep!=GameStep.landlords){
			return;
		}
		player1OutCard();
		player2OutCard();
		player3OutCard();
	}

	/**
	 * 玩家1出牌
	 */
	public void player1OutCard(){
		if(gameStep==GameStep.over){
			return;
		}
		
		if(player1out==false){
			return;
		}
		if(player1.isPlay()){
			//显示出牌
			player1.outSort();//排序一下
			int count=player1.outSize();
			if(count==0){
				return;
			}
			System.out.println("palyer1出牌数目："+count);
			int w=(screen_width-2*playCardBitmap.getWidth()-20)/20;
		//	int span=((screen_width-2*playCardBitmap.getWidth()-20)-w*count-(cardFaceBitmap.getWidth()-w))/2;
			Card card=null;
			for(int i=0;i<count;i++){
				card=player1.getOutCard(i);
				card.setLocationAndSize(playCardBitmap.getWidth()+i*w+5,initHeadBitmap.getHeight()+10,cardFaceBitmap.getWidth(),cardFaceBitmap.getHeight(),w);
				
				if(card.getName()>15){
					canvas.drawBitmap(card.getNumbitmap(), card.getX(), card.getY(), null);
				}else{
					//绘制背景
					canvas.drawBitmap(cardFaceBitmap,  card.getX(), card.getY(), null);
					
					//绘制上
					canvas.drawBitmap(card.getNumbitmap(),card.getUnx(),card.getUny(),null);
					canvas.drawBitmap(card.getLogobitmap(),card.getUlx(),card.getUly(),null);
					//绘制下
					canvas.drawBitmap(ImageUtil.createBitmapForUpDownLetRight(card.getNumbitmap()),card.getDnx(),card.getDny(),null);
					canvas.drawBitmap(ImageUtil.createBitmapForUpDownLetRight(card.getLogobitmap()),card.getDlx(),card.getDly(),null);
				}
			}
		}else{
			//绘制不出牌图标
			canvas.drawBitmap(gramTextBitmap[0],10+playCardBitmap.getWidth(),20+initHeadBitmap.getHeight(),null);
			canvas.drawBitmap(gramTextBitmap[1],10+playCardBitmap.getWidth()+gramTextBitmap[1].getWidth(),20+initHeadBitmap.getHeight(),null);

		}
		
				
	}
	
	/**
	 * 玩家2出牌情况
	 */
	public void player2OutCard(){
		if(gameStep==GameStep.over){
			return;
		}
		//已经出牌
		if((player2out==false)&&(turn==1)){
			//未出牌，显示出牌按钮
			
			switch (pstatus) {
				case none:
					//绘制出牌按钮 不可用
					canvas.drawBitmap(gramTextBitmap[18], screen_width-gramTextBitmap[18].getWidth()-10, screen_height-cardFaceBitmap.getHeight()-gramTextBitmap[18].getHeight()-30, null);
					canvas.drawBitmap(gramTextBitmap[14],screen_width-gramTextBitmap[18].getWidth()-10+(gramTextBitmap[18].getWidth()-gramTextBitmap[14].getWidth())/2,screen_height-cardFaceBitmap.getHeight()-30-gramTextBitmap[18].getHeight()+(gramTextBitmap[18].getHeight()-gramTextBitmap[14].getHeight())/2,null);
					//提示按钮
					canvas.drawBitmap(gramTextBitmap[15], screen_width-2*gramTextBitmap[18].getWidth()-20, screen_height-cardFaceBitmap.getHeight()-gramTextBitmap[15].getHeight()-30, null);
					canvas.drawBitmap(gramTextBitmap[8],screen_width-2*gramTextBitmap[18].getWidth()-20+(gramTextBitmap[15].getWidth()-gramTextBitmap[8].getWidth())/2,screen_height-cardFaceBitmap.getHeight()-30-gramTextBitmap[15].getHeight()+(gramTextBitmap[15].getHeight()-gramTextBitmap[8].getHeight())/2,null);
					//重选按钮
					canvas.drawBitmap(gramTextBitmap[18], screen_width-3*gramTextBitmap[18].getWidth()-30, screen_height-cardFaceBitmap.getHeight()-gramTextBitmap[16].getHeight()-30, null);
					canvas.drawBitmap(gramTextBitmap[13],screen_width-3*gramTextBitmap[18].getWidth()-30+(gramTextBitmap[18].getWidth()-gramTextBitmap[13].getWidth())/2,screen_height-cardFaceBitmap.getHeight()-30-gramTextBitmap[18].getHeight()+(gramTextBitmap[18].getHeight()-gramTextBitmap[13].getHeight())/2,null);
					break;
				case select:
					//绘制出牌按钮 可用
					canvas.drawBitmap(gramTextBitmap[16], screen_width-gramTextBitmap[16].getWidth()-10, screen_height-cardFaceBitmap.getHeight()-gramTextBitmap[16].getHeight()-30, null);
					canvas.drawBitmap(gramTextBitmap[14],screen_width-gramTextBitmap[16].getWidth()-10+(gramTextBitmap[16].getWidth()-gramTextBitmap[14].getWidth())/2,screen_height-cardFaceBitmap.getHeight()-30-gramTextBitmap[16].getHeight()+(gramTextBitmap[16].getHeight()-gramTextBitmap[14].getHeight())/2,null);
					//提示按钮
					canvas.drawBitmap(gramTextBitmap[15], screen_width-2*gramTextBitmap[18].getWidth()-20, screen_height-cardFaceBitmap.getHeight()-gramTextBitmap[15].getHeight()-30, null);
					canvas.drawBitmap(gramTextBitmap[8],screen_width-2*gramTextBitmap[18].getWidth()-20+(gramTextBitmap[15].getWidth()-gramTextBitmap[8].getWidth())/2,screen_height-cardFaceBitmap.getHeight()-30-gramTextBitmap[15].getHeight()+(gramTextBitmap[15].getHeight()-gramTextBitmap[8].getHeight())/2,null);
					//重选按钮
					canvas.drawBitmap(gramTextBitmap[15], screen_width-3*gramTextBitmap[18].getWidth()-30, screen_height-cardFaceBitmap.getHeight()-gramTextBitmap[16].getHeight()-30, null);
					canvas.drawBitmap(gramTextBitmap[13],screen_width-3*gramTextBitmap[18].getWidth()-30+(gramTextBitmap[18].getWidth()-gramTextBitmap[13].getWidth())/2,screen_height-cardFaceBitmap.getHeight()-30-gramTextBitmap[18].getHeight()+(gramTextBitmap[18].getHeight()-gramTextBitmap[13].getHeight())/2,null);
					break;
				default:
					break;
			}
			if(player1.isPlay()||player3.isPlay()){
				//绘制不出按钮
				canvas.drawBitmap(gramTextBitmap[17], screen_width-4*gramTextBitmap[17].getWidth()-40, screen_height-cardFaceBitmap.getHeight()-gramTextBitmap[17].getHeight()-30, null);
				canvas.drawBitmap(gramTextBitmap[10],screen_width-4*gramTextBitmap[17].getWidth()-40+(gramTextBitmap[17].getWidth()-gramTextBitmap[10].getWidth())/2,screen_height-cardFaceBitmap.getHeight()-30-gramTextBitmap[17].getHeight()+(gramTextBitmap[17].getHeight()-gramTextBitmap[10].getHeight())/2,null);

			}
			
		}else{
			
			if(player2.isPlay()){
				//绘制出牌情况
				player2.outSort();//排序一下
				int count=player2.outSize();
				if(count<=0){
					return;
				}
				System.out.println("player2出牌数目："+count);
				int w=screen_width/21;
				int span=(screen_width-w*count-(cardFaceBitmap.getWidth()-w))/2;
				Card card=null;
				for(int i=0;i<count;i++){
					card=player2.getOutCard(i);
					card.setLocationAndSize(span+i*w,screen_height-2*cardFaceBitmap.getHeight()-5,cardFaceBitmap.getWidth(),cardFaceBitmap.getHeight(),w);
					if(card.getName()>15){
						canvas.drawBitmap(card.getNumbitmap(), card.getX(), card.getY(), null);
					}else{
						//绘制背景
						canvas.drawBitmap(cardFaceBitmap,  card.getX(), card.getY(), null);
						
						//绘制上
						canvas.drawBitmap(card.getNumbitmap(),card.getUnx(),card.getUny(),null);
						canvas.drawBitmap(card.getLogobitmap(),card.getUlx(),card.getUly(),null);
						//绘制下
						canvas.drawBitmap(ImageUtil.createBitmapForUpDownLetRight(card.getNumbitmap()),card.getDnx(),card.getDny(),null);
						canvas.drawBitmap(ImageUtil.createBitmapForUpDownLetRight(card.getLogobitmap()),card.getDlx(),card.getDly(),null);
					}
				}
			}
			if(player2out&&(player2.isPlay()==false)){
				//绘制不出牌图标
				canvas.drawBitmap(gramTextBitmap[0],screen_width/2-gramTextBitmap[0].getWidth(),screen_height-cardFaceBitmap.getHeight()-gramTextBitmap[0].getHeight()-30,null);
				canvas.drawBitmap(gramTextBitmap[1],screen_width/2,screen_height-cardFaceBitmap.getHeight()-gramTextBitmap[1].getHeight()-30,null);
			}
		}
		
		
	}
	
	/**
	 * 玩家三出牌
	 * 
	 */
	public void player3OutCard(){
		if(gameStep==GameStep.over){
			return;
		}
		if(player3out==false){
			return;
		}
		if(player3.isPlay()){
			//显示出牌
			player3.outSort();//排序一下
			int count=player3.outSize();
			if(count==0){
				return;
			}
			System.out.println("player3出牌数目："+count);
			int w=(screen_width-2*playCardBitmap.getWidth()-20)/20;
			int span=screen_width-playCardBitmap.getWidth()-(cardFaceBitmap.getWidth()-w)-w*count-10;
			Card card=null;
			for(int i=0;i<count;i++){
				card=player3.getOutCard(i);
				card.setLocationAndSize(span+i*w+10,initHeadBitmap.getHeight()+10,cardFaceBitmap.getWidth(),cardFaceBitmap.getHeight(),w);
				if(card.getName()>15){
					canvas.drawBitmap(card.getNumbitmap(), card.getX(), card.getY(), null);
				}else{
					//绘制背景
					canvas.drawBitmap(cardFaceBitmap,  card.getX(), card.getY(), null);
					
					//绘制上
					canvas.drawBitmap(card.getNumbitmap(),card.getUnx(),card.getUny(),null);
					canvas.drawBitmap(card.getLogobitmap(),card.getUlx(),card.getUly(),null);
					//绘制下
					canvas.drawBitmap(ImageUtil.createBitmapForUpDownLetRight(card.getNumbitmap()),card.getDnx(),card.getDny(),null);
					canvas.drawBitmap(ImageUtil.createBitmapForUpDownLetRight(card.getLogobitmap()),card.getDlx(),card.getDly(),null);
				}
			}
		}else{
			//绘制不出牌图标
			canvas.drawBitmap(gramTextBitmap[0],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[0].getWidth()-gramTextBitmap[1].getWidth(),20+initHeadBitmap.getHeight(),null);
			canvas.drawBitmap(gramTextBitmap[1],screen_width-10-playCardBitmap.getWidth()-gramTextBitmap[1].getWidth(),20+initHeadBitmap.getHeight(),null);

		}
	}
	
	//玩家1出牌
	public void playergame1(){
		
		
		Sleep(5000-app.getSpeed());
		player1.clearOut();
		int count=0;
		while((count=new Random().nextInt(player1.size()+1))>5){
			
		}
		
		for(int i=0;i<count;i++){
			player1.addOutcards(player1.getCard(0));
			player1.removeCards(player1.getOutcards());
		}
		player1.setPlay(true);
		if(count==0){
			player1.setPlay(false);
		}
	
		repaint=true;
		player1out=true;
		Sleep(200);
		player2out=false;
		nextTurn();
	}
	//玩家2出牌
	public void playergame2(){
		repaint=true;
		while(player2out==false){
			Sleep(100);
		}
		repaint=true;
		System.out.println("退出等待循环");
		nextTurn();
	}
	//玩家3出牌
	public void playergame3(){
		
		
		Sleep(5000-app.getSpeed());
		player3.clearOut();
		int count=0;
		while((count=new Random().nextInt(player1.size()+1))>5){
			
		}
		for(int i=0;i<count;i++){
			player3.addOutcards(player3.getCard(0));
			player3.removeCards(player3.getOutcards());
		}
		player3.setPlay(true);
		if(count==0){
			player3.setPlay(false);
		}
		repaint=true;
		player3out=true;
		Sleep(200);
		nextTurn();
	}
	
	//下一个玩家
	public void nextTurn(){
		turn=(turn+1)%3;
		repaint=true;
	}
	
	public void  drawGameOverBitmap(){
		if(gameStep==GameStep.over){
			//overGamecurrBitmap
			canvas.drawBitmap(overGamecurrBitmap, screen_width/2-overGamecurrBitmap.getWidth()/2, screen_height/2-overGamecurrBitmap.getHeight(), null);
		}
	}
	
	//判断谁赢
	public void win(){
		if(player1.size()==0){
			gameStep=GameStep.over;
			if(player1.isDizhuflag()){
				overGamecurrBitmap=overGameBitmaps[2];//农民失败
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Lose.ogg");
				Sleep(1000);
				overGamecurrBitmap=overGameBitmaps[1];//地主胜利
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Win.ogg");
				
			}else{
				overGamecurrBitmap=overGameBitmaps[0];//地主失败
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Lose.ogg");
				Sleep(1000);
				overGamecurrBitmap=overGameBitmaps[3];//农民胜利
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Win.ogg");
			}
			Sleep(2000);
			gameStep=GameStep.init;
			repaint=true;
		}
		
		if(player2.size()==0){
			gameStep=GameStep.over;
			if(player2.isDizhuflag()){
				overGamecurrBitmap=overGameBitmaps[2];//农民失败
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Lose.ogg");
				Sleep(1000);
				overGamecurrBitmap=overGameBitmaps[1];//地主胜利
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Win.ogg");
				
			}else{
				overGamecurrBitmap=overGameBitmaps[0];//地主失败
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Lose.ogg");
				Sleep(1000);
				overGamecurrBitmap=overGameBitmaps[3];//农民胜利
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Win.ogg");
			}
			Sleep(2000);
			gameStep=GameStep.init;
			repaint=true;
		}
		
		if(player3.size()==0){
			gameStep=GameStep.over;
			if(player3.isDizhuflag()){
				overGamecurrBitmap=overGameBitmaps[2];//农民失败
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Lose.ogg");
				Sleep(1000);
				overGamecurrBitmap=overGameBitmaps[1];//地主胜利
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Win.ogg");
				
			}else{
				overGamecurrBitmap=overGameBitmaps[0];//地主失败
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Lose.ogg");
				Sleep(1000);
				overGamecurrBitmap=overGameBitmaps[3];//农民胜利
				repaint=true;
				MainApplication.getInstance().play("MusicEx_Win.ogg");
				
			}
			Sleep(2000);
			gameStep=GameStep.init;
			repaint=true;
			
			
		}
		
	}

	

	/**
	 * down 按下
	 * move 移动
	 * up 松开
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//按钮事件
		EventAction eventAction=new EventAction(appContext,this,event);
		//监听准备按钮是否按下
		eventAction.setPrepareButtont(screen_width/2-prepareButtonbgBitmap.getWidth()/2, screen_height/2, screen_width/2+prepareButtonbgBitmap.getWidth()/2, screen_height/2+prepareButtonbgBitmap.getHeight()/2);
		
		//只接受按下事件
		if(event.getAction()!=MotionEvent.ACTION_UP){
			return true;
		}
		//不抢、不叫按钮
		eventAction.setGrabGameBQButton(screen_width/2-gramTextBitmap[17].getWidth(),screen_height/2,screen_width/2,screen_height/2+gramTextBitmap[17].getHeight());
		//抢、叫地主
		eventAction.setGrabGameQDZButton(screen_width/2+20,screen_height/2,screen_width/2+20+gramTextBitmap[15].getWidth(),screen_height/2+gramTextBitmap[15].getHeight());
		//出牌 
		eventAction.setlandlordsGameQDZButton(screen_width-gramTextBitmap[18].getWidth()-10, screen_height-cardFaceBitmap.getHeight()-gramTextBitmap[18].getHeight()-30, screen_width-10, screen_height-cardFaceBitmap.getHeight()-30);
		//提示按钮
		eventAction.setHintGameQDZButton(screen_width-2*gramTextBitmap[18].getWidth()-20, screen_height-cardFaceBitmap.getHeight()-gramTextBitmap[18].getHeight()-30, screen_width-20-gramTextBitmap[18].getWidth(), screen_height-cardFaceBitmap.getHeight()-30);
		//重选按钮
		eventAction.setResetGameQDZButton(screen_width-3*gramTextBitmap[18].getWidth()-30, screen_height-cardFaceBitmap.getHeight()-gramTextBitmap[18].getHeight()-30, screen_width-30-gramTextBitmap[18].getWidth()*2, screen_height-cardFaceBitmap.getHeight()-30);
		//不出按钮
		eventAction.setNotLandlordsGameQDZButton(screen_width-4*gramTextBitmap[18].getWidth()-40, screen_height-cardFaceBitmap.getHeight()-gramTextBitmap[18].getHeight()-30, screen_width-40-gramTextBitmap[18].getWidth()*3, screen_height-cardFaceBitmap.getHeight()-30);
		//牌的监听
		eventAction.setCard();
		//监听退出按钮、设置按钮是否按下。
		eventAction.exitButton(screen_width/3-exitBitmap.getWidth()-10,20,screen_width/3-10,20+exitBitmap.getHeight());
		eventAction.setButton(screen_width/3-5,20,screen_width/3-5+setupBitmap.getWidth(),20+setupBitmap.getHeight());
		
		return true;
		
	}
	
	
}
