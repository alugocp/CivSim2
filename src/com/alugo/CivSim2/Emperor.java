package com.alugo.CivSim2;
import java.util.ArrayList;

class Emperor{
	//request type indices
	static final int CAPTURED=0;
	static final int SECEDED=1;
	static final int MORE_CIV=2;
	static final int MORE_SOL=3;
	static final int FOUND_CITY=4;
	static final int STARVING=5;
	static final int ATTACK=6;
	//static final int TRAITOR=7;
	//static final int SUPPORT=8;

	//coefficient indices
	static final int LOYALTY=0;
	static final int DISTANCE=1;
	static final int LOYALTY_BASED=2;
	//static final int REQUEST_AGE=3;
	//static final int SAME_TYPE=4;

	static int nextNation=0;
	ArrayList<Request> requests=new ArrayList<Request>();
	ArrayList<Request> bufferRequests=new ArrayList<Request>();
	float[] requestTypes=new float[7];
	float[] coefficients=new float[3];
	int nation,x,y,cities;
	public Emperor(int x,int y){
		GUI.s.emperors.add(this);
		nation=nextNation;
		nextNation++;
		this.x=x;
		this.y=y;
		for(int a=0;a<requestTypes.length;a++){
			requestTypes[a]=(float)GUI.s.random(-50,51)/10f;
		}
		for(int a=0;a<coefficients.length;a++){
			coefficients[a]=(float)GUI.s.random(-20,21)/10f;
		}
		GUI.s.cities[x][y]=new City(x,y,nation);
	}
	public Emperor(City c){
		GUI.s.emperors.add(this);
		nation=nextNation;
		nextNation++;
		x=c.x;
		y=c.y;
		for(int a=0;a<requestTypes.length;a++){
			requestTypes[a]=(float)GUI.s.random(-50,51)/10f;
		}
		for(int a=0;a<coefficients.length;a++){
			coefficients[a]=(float)GUI.s.random(-20,21)/10f;
		}
		GUI.s.changeNation(c,nation);
	}
}
