package com.alugo.CivSim2;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Color;

public class GUI extends Frame{
	static Simulation s=new Simulation();
	static GUI gui;
	public static void main(String[] args){
		new GUI();
		new RunThread().start();
	}
	public GUI(){
		setVisible(true);
		setSize(500,500);
		setTitle("Alugo's Civilization Simulator 2.0");
		s.spawnContinent();
		//s.forEachCity();
		/*Emperor e=new Emperor(10,10);
		new City(11,10,e.nation);
		new City(12,10,e.nation);
		new City(10,11,e.nation);
		new City(11,11,e.nation);
		new City(12,11,e.nation).food=1;
		new City(12,13,e.nation);
		new City(14,10,e.nation);
		new City(13,13,e.nation);
		new City(9,11,e.nation);
		s.periodOfWarringStates(e);
		invalidate();*/
		gui=this;
	}
	@Override
	public void paint(Graphics g){
		g.translate(50,50);
		int scale=5;
		for(int x=0;x<s.dimension;x++){
			for(int y=0;y<s.dimension;y++){
				if(s.cities[x][y]!=null){
					g.setColor(s.cities[x][y].color);
				}else if(s.fertility[x][y]==0){
					g.setColor(Color.BLUE);
				}else{
					float saturation=0.2f+((float)s.fertility[x][y]/20f);
					if(saturation>1f){
						saturation=1f;
					}
					Color c=Color.getHSBColor(0.33f,saturation,0.5f);
					g.setColor(c);
				}
				g.fillRect(x*scale,y*scale,scale,scale);
			}
		}
		/*g.setColor(Color.BLACK);
		for(int a=0;a<s.emperors.size();a++){
			Emperor e=s.emperors.get(a);
			g.fillRect(e.x*scale,e.y*scale,scale,scale);
		}*/
	}
}
