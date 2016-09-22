package com.alugo.CivSim2;
import java.awt.Color;

public class City{
	int x,y,nation;
	int soldiers=GUI.s.random(11),civilians=GUI.s.random(11);
	int food=0,loyalty=5;
	Color color;
	public City(int x,int y,int nation){
		GUI.s.cities[x][y]=this;
		this.x=x;
		this.y=y;
		this.nation=nation;
		color=GUI.s.getColor(nation);
		GUI.s.getEmperor(nation).cities++;
		/*if(GUI.s.random(100)==0){
			soldiers+=100;
			civilians+=200;
			System.out.println("God mode activate");
		}*/
	}
}
