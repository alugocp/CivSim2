package com.alugo.CivSim2;
import java.util.ArrayList;
import java.awt.Color;

public class Simulation{
	//the container class for all the object-less functions in the program
	static final int dimension=50;
	ArrayList<Emperor> emperors=new ArrayList<Emperor>();
	int[][] fertility=new int[dimension][dimension];
	City[][] cities=new City[dimension][dimension];
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
							new Emperor(x,y);
						}
					}
					fertility[x][y]=f;
				}
				if(fertility[x][y]==-1){
					fertility[x][y]=0;
				}
			}
		}
	}
	public float distance(int[] one,int[] two){
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
	public Color getColor(int nation){
		int r=(nation*40)%255;
		int g=(255-(nation*20))%255;
		while(g<0){
			g+=255;
		}
		int b=((60*nation)+100)%255;
		return new Color(r,g,b);
	}
	public Emperor getEmperor(int n){
		if(n>Emperor.nextNation/2){
			for(int a=emperors.size()-1;a>=0;a--){
				if(emperors.get(a).nation==n){
					return emperors.get(a);
				}
			}
		}else{
			for(int a=0;a<emperors.size();a++){
				if(emperors.get(a).nation==n){
					return emperors.get(a);
				}
			}
		}
		return null;
	}
	public void forEachCity(){
		for(int x=0;x<cities.length;x++){
			for(int y=0;y<cities[x].length;y++){
				City c=cities[x][y];
				if(c!=null){
					c.civilians+=random(-2,3);
					c.soldiers+=random(-2,3);
					secedeAction(c);
					foodRequests(c);
					surroundingsRequests(c);
				}
			}
		}
	}
	public void secedeAction(City c){
		if(c.loyalty<=0){
			Emperor e=getEmperor(c.nation);
			if(e.x==c.x && e.y==c.y){
				periodOfWarringStates(e,null);
			}else{
				//new Request(Emperor.SECEDED,c);
				new Emperor(c);
			}
		}
	}
	public void foodRequests(City c){
		int f=fertility[c.x][c.y];
		f=(f*c.civilians)-(c.civilians+c.soldiers);
		if(f<=0){
			new Request(Emperor.MORE_CIV,c);
			c.loyalty--;
		}
		c.food+=f;
		if(c.food<0){
			c.soldiers+=c.food;
			c.food=0;
			if(c.soldiers<0){
				c.civilians+=c.soldiers;
				c.soldiers=0;
				if(c.civilians<0){
					c.civilians=0;
				}
			}
			new Request(Emperor.STARVING,c);
			c.loyalty--;
		}
	}
	public void surroundingsRequests(City c){
		ArrayList<Object> s=getSurroundings(c);
		for(int a=0;a<s.size();a++){
			if(s.get(a) instanceof City){
				City c1=(City)s.get(a);
				if(c1.nation!=c.nation){
					if(c1.soldiers>=c.soldiers){
						new Request(Emperor.MORE_SOL,c);
						c.loyalty--;
					}else{
						new Request(Emperor.ATTACK,c).target=new int[]{c1.x,c1.y};
					}
				}
			}else{
				new Request(Emperor.FOUND_CITY,c).target=(int[])s.get(a);
			}
		}
	}
	public ArrayList<Object> getSurroundings(City c){
		ArrayList<Object> s=new ArrayList<Object>();
		for(int x=c.x-1;x<=c.x+1;x++){
			for(int y=c.y-1;y<=c.y+1;y++){
				try{
					if(cities[x][y]!=null){
						s.add(cities[x][y]);
					}else if(fertility[x][y]>0){
						s.add(new int[]{x,y});
					}
				}catch(ArrayIndexOutOfBoundsException nothingThere){}
			}
		}
		return s;
	}
	public void forEachEmperor(){
		for(int a=emperors.size()-1;a>=0;a--){
			Emperor e=emperors.get(a);
			if(e.cities==0){
				emperors.remove(a);
			}else{
				//System.out.println("Cities: "+e.cities+", Requests: "+((e.cities/2)+1)+"/"+e.requests.size());
				for(int b=0;b<(e.cities/2)+1 && b<e.requests.size();b++){
					appeaseRequest(e.requests.get(b));
				}
				e.requests.clear();
				if(e.cities<e.lastCities){
					e.setParameters();
					//System.out.println("Nation "+e.nation+" rewired");
				}
				e.lastCities=e.cities;
				//System.out.print(e.cities+",");
			}
		}
		//System.out.println("");
	}
	public void appeaseRequest(Request r){
		if(r.type==Emperor.STARVING){
			r.city.food+=100;
			r.city.loyalty+=2;
		}else if(r.type==Emperor.MORE_CIV){
			r.city.civilians+=10;
			r.city.loyalty+=2;
		}else if(r.type==Emperor.MORE_SOL){
			r.city.soldiers+=10;
			r.city.loyalty+=2;
		}else if(r.type==Emperor.FOUND_CITY){
			new City(r.target[0],r.target[1],r.city.nation);
		}else{
			ArrayList<Object> o=getSurroundings(r.city);
			if(r.type==Emperor.ATTACK){
				City c=cities[r.target[0]][r.target[1]];
				if(c.soldiers<r.city.soldiers){
					//r.city.soldiers-=c.soldiers/2;
					//c.soldiers/=2;
					Emperor e=getEmperor(c.nation);
					if(e.x==c.x && e.y==c.y){
						periodOfWarringStates(e,getEmperor(r.city.nation));
					}else{
						changeNation(c,r.city.nation);
					}
				}
			}/*else if(r.type==Emperor.SECEDED){
				
			}else if(r.type==Emperor.CAPTURED){
				
			}*/
		}
	}
	public void changeNation(City c,int n){
		getEmperor(c.nation).cities--;
		c.nation=n;
		c.color=getColor(n);
		getEmperor(n).cities++;
	}
	public void periodOfWarringStates(Emperor e,Emperor conqueror){
		ArrayList<City> ecs=new ArrayList<City>();
		for(int x=0;x<cities.length;x++){
			for(int y=0;y<cities[x].length;y++){
				City c=cities[x][y];
				if(c!=null && c.nation==e.nation && (c.x!=e.x || c.y!=e.y)){
					boolean add=true;
					for(int a=0;a<ecs.size();a++){
						if(ecs.get(a).food<=c.food){
							ecs.add(a,c);
							add=false;
							break;
						}
					}
					if(add){
						ecs.add(c);
					}
				}
				if(ecs.size()==e.cities-1){
					break;
				}
			}
			if(ecs.size()==e.cities-1){
				break;
			}
		}
		ecs.add(0,cities[e.x][e.y]);
		int n=(ecs.size()/5)+1;
		if(n>3){
			n=3;
		}
		int[][] capitals=new int[n][];
		for(int a=0;a<n;a++){
			City cap=ecs.get(a);
			if(a==0 && conqueror!=null){
				changeNation(cap,conqueror.nation);
			}else{
				new Emperor(cap);
			}
			capitals[a]=new int[]{cap.x,cap.y};
		}
		for(int a=n;a<ecs.size();a++){
			City c=ecs.get(a);
			changeNation(c,ecs.get((int)closestPoint(new int[]{c.x,c.y},capitals)[0]).nation);
		}
	}
}
