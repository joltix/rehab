package com.rehab.world;

public class Obstacle extends Entity {

	public Obstacle(double mass) {
		super(mass);
		// Prevent damage
		setHealth(-1);
	}

	@Override
	public void onHealthIncrease(double oldHealth, double newHealth) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHealthDecrease(double oldHealth, double newHealth) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMove(double oldX, double oldY, double newX, double newY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getZ() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onClick() {
		// TODO Auto-generated method stub
		
	}
	
}
