package com.liris.datamatrixedcamera.app.traitement;

public class Pair <U,V> {

	private U first;
	private V second;
	
	
	public Pair(U first, V second){
		this.first=first;
		this.second=second;
	}
	
	
	
	public U getfirst (){
		return first;
	}
	
	
	public V getsecond (){
		return second;
	}
	
}
