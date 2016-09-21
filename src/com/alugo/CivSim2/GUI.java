package com.alugo.CivSim2;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Color;

public class GUI extends Frame{
	static Simulation s=new Simulation();
	public static void main(String[] args){
		new GUI();
		/*new City(5,5);
		new City(5,7);
		new City(10,10);*/
	}
	public GUI(){
		setVisible(true);
		setSize(500,500);
		setTitle("Alugo's Civilization Simulator 2.0");
		s.spawnContinent();
		//invalidate();
	}
	@Override
	public void paint(Graphics g){
		g.translate(0,50);
		int scale=7;
		for(int x=0;x<s.fertility.length;x++){
			for(int y=0;y<s.fertility[x].length;y++){
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
	}
}
