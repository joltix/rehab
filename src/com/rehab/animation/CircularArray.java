package com.rehab.animation;

import java.util.ArrayList;

public class CircularArray<E> extends ArrayList<E> {

	@Override
	public E get(int index) {
		// TODO Auto-generated method stub
		//if the index we want to get is less than zero
		//then set index to be index+size
		
		if(index < 0) {  index = index + size();}
		// Index too big to handle so wrap around
		else if (index >= size()) index = index % size;

		return super.get(index);
	}
	
	public static void main(String [ ] args){
		CircularArray reels = new CircularArray();
		reels.add(10); //index =0
		reels.add(20); //i=1
		reels.add(30); //i=2
		reels.add(40); //i=3
		
		System.out.println(reels.get(4));
		
	}
}
