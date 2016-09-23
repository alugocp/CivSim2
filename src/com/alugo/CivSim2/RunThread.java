package com.alugo.CivSim2;

class RunThread extends Thread{
	float time=0f;
	@Override
	public void run(){
		while(time<60f){
			GUI.s.forEachCity();
			GUI.s.forEachEmperor();			
			try{
				Thread.sleep(250);
			}catch(InterruptedException interrupted){
				System.out.println("interrupted");
			}
			GUI.gui.repaint();
			try{
				Thread.sleep(250);
			}catch(InterruptedException interrupted){
				System.out.println("interrupted");
			}
			time+=0.5f;
		}
		System.out.println("All done!");
	}
}
