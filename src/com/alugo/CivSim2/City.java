package com.alugo.CivSim2;
import java.awt.Color;

public class City{
	int x,y,nation;
	int soldiers=0,civilians=10;
	int food=0,loyalty=5;
	Color color;
	public City(int x,int y,int nation){
		GUI.s.cities[x][y]=this;
		this.x=x;
		this.y=y;
		this.nation=nation;
		color=GUI.s.getColor(nation);
	}
}
