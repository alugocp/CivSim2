package com.alugo.CivSim2;
import java.awt.Color;

public class City{
	static final int INITIAL_LOYALTY=20;
	int x,y,nation;
	int soldiers=10,civilians=20;
	int food=0,loyalty=INITIAL_LOYALTY;
	Color color;
	public City(int x,int y,int nation){
		GUI.s.cities[x][y]=this;
		this.x=x;
		this.y=y;
		this.nation=nation;
		color=GUI.s.getColor(nation);
		Emperor e=GUI.s.getEmperor(nation);
		e.cities++;
		e.focus++;
		/*if(GUI.s.random(100)==0){
			soldiers+=100;
			civilians+=200;
			System.out.println("God mode activate");
		}*/
	}
}
