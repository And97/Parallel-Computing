package main;

import core.Image;
import core.Worker;
import utils.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/*
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
Classe che si occupa dell'avvio del programma.
è necessario specificare la directory da cui prendere le immagini e il 
numero di thread da utilizzare.
Successivamente viene creato e fatto partire il thread runWorker che si 
occuperà di generare, avviare e far completare i task.
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
*/

public class Main {

	public static void createEmptyImageArrayList(List<Image> images, String directory) {
		for (int i = 0; i < Utils.countFiles(directory); i++) {
			images.add(new Image());
		}

	}

	public static void main(String[] args) {

		List<Image> sharedBuffer = Collections.synchronizedList(new ArrayList<Image>());
		String directory = "C:\\Users\\Andrea\\Desktop\\mid-term\\test2";
		int threadNumber = 12;
		File[] images = Utils.walkDirectory(directory);
		createEmptyImageArrayList(sharedBuffer, directory);

		Thread runWorker = new Thread(() -> {
			Worker forkJoinWorker = new Worker(directory, sharedBuffer, images);
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
		for (Image im : sharedBuffer) {
			System.out.println(y + "--->" + im.getPath());
			y++;
		}

	}
}
