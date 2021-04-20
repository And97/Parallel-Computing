package main;


import core.Image;
import core.Worker;
import utils.Utils;
import java.io.File;
import java.util.ArrayList;


public class Main {
	
	public static void main(String[] args) {
		
		ArrayList<Image> buffer=new ArrayList<>();
		String directory = "C:\\Users\\Andrea\\Desktop\\mid-term\\test2";
		int threadNumber = 12;
		File[] images = Utils.walkDirectory(directory); 
	
		Thread runWorker = new Thread(() -> {
			Worker forkJoinWorker = new Worker(directory, buffer, images);
			forkJoinWorker.setThreadNumber(threadNumber);
			forkJoinWorker.start();
		});

		runWorker.start();
		try {
			runWorker.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int y = 0;
		for (Image im : buffer) {
			System.out.println(y + "--->" + im.getPath());
			y++;
		}
	}
}
