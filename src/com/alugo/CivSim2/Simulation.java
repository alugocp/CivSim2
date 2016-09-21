package com.alugo.CivSim2;
import java.util.ArrayList;
import java.awt.Color;

public class Simulation{
	//the container class for all the object-less functions in the program
	static final int dimension=50;
	ArrayList<Emperor> emperors=new ArrayList<>();
	int[][] fertility=new int[dimension][dimension];
	City[][] cities=new City[dimension][dimension];
	/*public Simulation(){
		spawnContinent();
	}*/
	private double random(){
		return Math.random();
	}
	public int random(int min,int max){
		return (int)(Math.floor(random()*(max-min)))+min;
	}
	public int random(int max){
		return random(0,max);
	}
	public void spawnContinent(){
		int[][] seeds=new int[40][];
		for(int s=0;s<seeds.length;s++){
			int x=random(dimension);
			int y=random(dimension);
			int fertile=1;
			if(random(4)==0){
				fertile=-1;
			}
			fertility[x][y]=fertile;
			seeds[s]=new int[]{x,y,fertile};
		}
		for(int y=0;y<dimension;y++){
			for(int x=0;x<dimension;x++){
				if(fertility[x][y]==0){
					float[] closest=closestPoint(new int[]{x,y},seeds);
					int f=seeds[(int)closest[0]][2];
					if(f==1){
						f=(int)(closest[1]*2);
						if(random(150)==0){
							new City(x,y,new Emperor(x,y).nation);
						}
					}
					fertility[x][y]=f;
				}
				if(fertility[x][y]==-1){
					fertility[x][y]=0;
				}
				//System.out.print(fertility[x][y]+" ");
			}
			//System.out.println("");
		}
	}
	private float distance(int[] one,int[] two){
		return (float)Math.sqrt(Math.pow(one[0]-two[0],2)+Math.pow(one[1]-two[1],2));
	}
	public float[] closestPoint(int[] point,int[]...set){//returns index and distance
		float distance=distance(point,set[0]);
		int index=0;
		for(int a=1;a<set.length;a++){
			float d=distance(point,set[a]);
			if(d<distance){
				distance=d;
				index=a;
			}
		}
		return new float[]{index,distance};
	}
	public void useFood(City c){
		int f=fertility[c.x][c.y];
		c.food+=(f*c.civilians)-(c.civilians+c.soldiers);
		//if it's too low, send a request to the emperor
	}
	public Color getColor(int nation){
		int r=(nation*40)%255;
		int g=(255-(nation*20))%255;
		while(g<0){
			g+=255;
		}
		int b=((60*nation)+100)%255;
		return new Color(r,g,b);
	}
}
