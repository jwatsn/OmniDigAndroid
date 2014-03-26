package com.jwatson.omnigame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;

public abstract class Growable extends UpdatableItem {
	
	GrowableManager manager;
	List<Vector2> LeafPos = new ArrayList<Vector2>();
	public boolean hasLeaf = true;
	public boolean hasMobs = false;
	public boolean isAggressive = false;
	public int mobCounter = 0;
	
	public Growable(int tickspeed, GrowableManager man) {
		super(tickspeed);
		m_TickSpeed = tickspeed;
		this.manager = man;
		
		if(MathUtils.random(100) < 25) {
			hasMobs = true;
		}

		
	}
	public void set(int x, int y,int x2, int y2) {
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;
		matrix = new Matrix3();
	}
	
	protected float percent() {
		return (((float)m_CurrentTick/m_Size) * 100f);
	}

	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		
		if(m_CurrentTick >= m_Size) {
			OnRemove();
			remove = true;
			manager.finished.add(this);
			return;
		}
		OnTick();
		m_CurrentTick++;
	}
	
	protected int id;
	protected int x;
	protected int y;
	protected int x2;
	protected int y2;
	protected  int m_TickSpeed;
	protected int m_Size;
	protected int m_CurrentTick;
	protected boolean finished;
	Matrix3 matrix;
	public boolean instant;
	public abstract void OnTick();
	public abstract void OnRemove();

}
