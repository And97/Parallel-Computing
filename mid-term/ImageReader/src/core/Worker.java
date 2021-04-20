package core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import utils.Utils;

/*
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
Questa è la classe che si occupa di prendere in carico tutto il lavoro
e dividerlo nei vari task, seguendo il paradigma fork-join.

Attributi:
	-> path(String) percorso del filesystem da cui caricare le immagini
	-> sharedBuffer(List<image>) buffer condiviso tra tutti i Task
	-> images(File[]) array contenente tutte le foto da caricare nel 
		buffer
	-> threadNumber(int) numero di thread da utilizzare durante 
		l'esecuzione del programma
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
*/

public class Worker {
	private static String path;
	private static List<Image> sharedBuffer;
	private static File[] images;
	private static int threadNumber;

	public Worker(String path, List<Image> sharedBuffer, File[] images) {
		Worker.path = path;
		Worker.sharedBuffer = sharedBuffer;
		Worker.images = images;
	}

	public void setThreadNumber(int coreNumber) {
		Worker.threadNumber = coreNumber;
	}

	/*
	– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
	Metodo che si occupa di dividere il carico di lavoro tra i vari thread.
	Lo schema di esecuzione del metodo è il seguente:
		1) si controlla con quanti thread dovrà essere eseguito il programma
		2) se il numero di thread è maggiore di uno si procede alla 
			divisione dell'insieme delle immagini da caricare in N(numero 
			di thread) sottoinsiemi disgiunti di immagini. 
			Nel caso in cui l'insieme non sia divisibile per il numero di 
			thread, il thread N-1 riceverà più immagini da caricare.
			Nel caso di un solo thread l'intero insieme verrà assegnato 
			a quel thread.
		3)  si eseguono in successione le funzioni join e fork per ogni 
			thread e si calcola il tempo di esecuzione.
	– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
	*/
	public void start() {

		int totalFile = Utils.countFiles(path);

		if (threadNumber == 1) { //esecuzione single thread
			Task singleTask = new Task(0, images, sharedBuffer, 0, totalFile);
			long startTime = System.currentTimeMillis();
			singleTask.fork();
			singleTask.join();
			long endTime = System.currentTimeMillis();

			Utils.infoBox("All tasks are completed! \nThe images were loaded in: " + (endTime - startTime) + " ms.",
					"job finished");
			return;
		} else { //esecuzione multi thread 
			int chunkDimension = totalFile / threadNumber; //dimensione di ogni singolo sottoinsieme di immaginni
			int remainingImage = totalFile % threadNumber; // parte restante della divisione
			int ID = 0;
			ArrayList<ForkJoinTask<Boolean>> tasks = new ArrayList<>(threadNumber); 

			for (int i = 0; i < threadNumber; i++) {
				int startIndex = i * chunkDimension;
				int endIndex = (i + 1) * chunkDimension;

				if (i != threadNumber - 1) { //assegnamento del sottoinsieme ai thread con ID compreso tra 0 e threadNumber-1
					tasks.add(new Task(ID, images, sharedBuffer, startIndex, endIndex));
					ID++;
				} else { //assegnamento al thread con ID=threadNumber
					tasks.add(new Task(ID, images, sharedBuffer, startIndex, endIndex + remainingImage));
					ID++;
				}

			}

			long startTime = System.currentTimeMillis();
			for (ForkJoinTask<Boolean> task : tasks) {
				task.fork();
			}

			for (ForkJoinTask<Boolean> task : tasks) {
				task.join();
				//System.out.println(task.invoke());
			}

			long endTime = System.currentTimeMillis();
			Utils.infoBox("All tasks are completed! \nThe images were loaded in: " + (endTime - startTime) + " ms.",
					"job finished");

		}

	}
}