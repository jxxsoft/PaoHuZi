package jxxsoft.com.cn.paohuzi.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {

	private int pid;//玩家编号
	private boolean dizhuflag;//是否是地主
	private GameGrab status;//抢地主姿态
	private int currself;//当前抢地主的倍数
	private boolean person;//是不是人 ：true，电脑 false
	private List<Card> cards=new ArrayList<Card>();//当前手里的牌
	private List<Card> outcards=new ArrayList<Card>();//每次出牌
	private boolean play;//是否出牌或者跟牌
	
	public Player(int pid,boolean person){
		this.pid=pid;
		this.person=person;
		init();
	}
	
	public void init(){
		this.currself=0;
		this.dizhuflag=false;
		this.status=GameGrab.none;
		this.play=false;
		clear();
	}
	
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public GameGrab getStatus() {
		return status;
	}
	public void setStatus(GameGrab status) {
		this.status = status;
	}
	public boolean isDizhuflag() {
		return dizhuflag;
	}
	public void setDizhuflag(boolean dizhuflag) {
		this.dizhuflag = dizhuflag;
	}

	public List<Card> getCards() {
		return cards;
	}

	/**
	 * 添加
	 * @param card
	 */
	public void addCards(Card card) {
		this.cards.add(card);
	}
	/**
	 * 移除
	 * @return
	 */
	public void removeCards(List<Card> outcards){
		this.cards.removeAll(outcards);
	}
	

	public List<Card> getOutcards() {
		return outcards;
	}

	/**
	 * 发牌记录牌
	 * @param outcard
	 */
	public void addOutcards(Card outcard) {
		this.outcards.add(outcard);
	}
	
	public int getCurrself() {
		return currself;
	}

	public void setCurrself(int currself) {
		this.currself = currself;
	}

	public boolean isPerson() {
		return person;
	}

	/**
	 * 清空所有牌数据
	 */
	public void clear(){
		this.cards.clear();
		this.outcards.clear();
	}
	/**
	 * 清空出牌集合
	 */
	public void clearOut(){
		this.outcards.clear();
	}
	/**
	 * 排序
	 */
	public void sort(){
		Collections.sort(this.cards);
	}
	
	/**
	 * 出牌排序
	 */
	public void outSort(){
		Collections.sort(this.outcards);
	}
	/**
	 * 当前牌的数目
	 * @return
	 */
	public int size(){
		return this.cards.size();
	}
	
	public int outSize(){
		return this.outcards.size();
	}
	
	/**
	 * 获得某张牌
	 * @param index
	 * @return
	 */
	public Card getCard(int index){
		return this.cards.get(index);
	}
	
	public Card getOutCard(int index){
		return this.outcards.get(index);
	}

	public boolean isPlay() {
		return play;
	}

	public void setPlay(boolean play) {
		this.play = play;
	}
	
	
}
