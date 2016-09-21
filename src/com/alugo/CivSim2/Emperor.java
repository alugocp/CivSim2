package com.alugo.CivSim2;

class Emperor{
	//request type indices
	static final int CAPTURED=0;
	static final int SECEDED=1;
	static final int MORE_CIV=2;
	static final int MORE_SOL=3;
	static final int TRAITOR=4;
	static final int FOUND_CITY=5;
	static final int STARVING=6;
	static final int ATTACK=7;
	static final int SUPPORT=8;

	//coefficient indices
	static final int LOYALTY=0;
	static final int REQUEST_AGE=1;
	static final int SAME_TYPE=2;
	static final int DISTANCE=3;
	static final int LOYALTY_BASED=4;

	static int nextNation=0;
	int[] requestTypes=new int[9];
	int[] coefficients=new int[6];
	int nation,x,y;
	public Emperor(int x,int y){
		GUI.s.emperors.add(this);
		nation=nextNation;
		nextNation++;
		this.x=x;
		this.y=y;
	}
}
