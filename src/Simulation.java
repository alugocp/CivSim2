
public class Simulation{
	//the container class for all the object-less functions in the program
	static final int dimension=50;
	int[][] fertility=new int[dimension][dimension];
	/*public static void main(String[] args){
		new Simulation().spawnContinent();
	}*/
	public Simulation(){
		spawnContinent();
	}
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
			int fertile=random(-1,1);
			if(fertile==0){
				fertile++;
			}
			fertility[x][y]=fertile;
			seeds[s]=new int[]{x,y,fertile};
		}
		for(int y=0;y<dimension;y++){
			for(int x=0;x<dimension;x++){
				if(fertility[x][y]==0){
					fertility[x][y]=seeds[closestPointIndex(new int[]{x,y},seeds)][2];
				}
				if(fertility[x][y]==-1){
					fertility[x][y]=0;
				}
				//System.out.print(fertility[x][y]+" ");
			}
			//System.out.println("");
		}
	}
	private double distance(int[] one,int[] two){
		return Math.sqrt(Math.pow(one[0]-two[0],2)+Math.pow(one[1]-two[1],2));
	}
	public int closestPointIndex(int[] point,int[]...set){
		double distance=distance(point,set[0]);
		int index=0;
		for(int a=1;a<set.length;a++){
			double d=distance(point,set[a]);
			if(d<distance){
				distance=d;
				index=a;
			}
		}
		return index;
	}
	public void useFood(City c){
		int f=fertility[c.x][c.y];
		c.food+=(f*c.civilians)-(c.civilians+c.soldiers);
		//if it's too low, send a request to the emperor
	}
}