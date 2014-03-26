package com.jwatson.omnigame;

public abstract class MessageBoxButton {

	public static int TEXT = 1;
	public static int ITEM = 2;
	
	String txt;
	public int type = 1;
	//public int BagPos;
	public int maxAmt;
	public boolean isChestItem;
	public InvObject held;
	public int heldAmt;
	public boolean noClose;
	
	public MessageBoxButton(String txt) {
		// TODO Auto-generated constructor stub
		this.txt = txt;
	}
	
	public abstract void onClicked(int...args);
	

}
