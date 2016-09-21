import java.awt.Frame;
import java.awt.Graphics;

public class GUI extends Frame{
	Simulation s=new Simulation();
	public static void main(String[] args){
		new GUI();
	}
	public GUI(){
		setVisible(true);
		setSize(500,500);
		setTitle("Alugo's Civilization Simulator 2.0");
		//invalidate();
	}
	@Override
	public void paint(Graphics g){
		g.translate(0,25);
		int scale=5;
		for(int x=0;x<s.fertility.length;x++){
			for(int y=0;y<s.fertility[x].length;y++){
				if(s.fertility[x][y]==1){
					g.fillRect(x*scale,y*scale,scale,scale);
				}
			}
		}
	}
}
