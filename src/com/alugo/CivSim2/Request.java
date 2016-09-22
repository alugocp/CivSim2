package com.alugo.CivSim2;

public class Request{
	float priority;
	int type;
	City city;
	public Request(int type,City c){
		this.type=type;
		Emperor e=GUI.s.getEmperor(c.nation);
		priority=e.requestTypes[type];
		priority+=e.coefficients[Emperor.LOYALTY]*c.loyalty;
		priority+=e.coefficients[Emperor.DISTANCE]*(float)GUI.s.distance(new int[]{c.x,c.y},new int[]{e.x,e.y});
		if(type==Emperor.MORE_CIV || type==Emperor.MORE_SOL || type==Emperor.STARVING){
			priority+=e.coefficients[Emperor.LOYALTY_BASED];
		}else{
			priority-=e.coefficients[Emperor.LOYALTY_BASED];
		}
		city=c;
		for(int a=0;a<e.requests.size();a++){
			if(e.requests.get(a).priority<priority){
				e.requests.add(a,this);
				return;
			}
		}
		e.requests.add(this);
	}
}