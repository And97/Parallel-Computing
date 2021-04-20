package core;

import java.io.File;
import java.util.List;
import java.util.concurrent.RecursiveTask;

/*
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
La classe estende RecursiveTask che permette di implementare e definire 
un singolo Task, ovvero un compito all'interno del paradigma Fork-Join.
Ogni Task ha il compito di caricare un certo numero di immagini 
predefinito all'interno di un buffer condiviso tra tutti i task.

Attributi:
	-> ID (int) rappresenta l'identificativo del Task
	-> path(File[]) l'array condiviso tra tutti i task che contiene tutte 
		le immagini da caricare (cartella del file system). Ogni task 
		accederà solo alla porzione assegnata.
	-> SharedBuffer(List<Image>) buffer condiviso tra tutti i task in 
		cui verranno caricate le immagini
	-> startIndex(int), endIndex(int) rispettivamente l'indice iniziale 
		e finale del sottoinsieme delle immagini che dovrà caricare uno 
		specifico Task.
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
*/


public class Task extends RecursiveTask<Boolean> {

	private static final long serialVersionUID = 1L;
	private int ID;
	private File[] path;
	private List<Image> sharedBuffer;
	private int startIndex;
	private int endIndex;

	public Task(int ID, File[] path, List<Image> sharedBuffer, int startIndex, int endIndex) {

		this.ID = ID;
		this.path = path;
		this.sharedBuffer = sharedBuffer;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	
	/*
	– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
	Override del metodo compute() ereditato dalla classe RecursiveTask,
	questo è il metodo che si occuperà di caricare effettivamente le 
	le immagini all'interno del buffer condiviso tra i Task sharedBuffer
	– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
	*/
	@Override
	protected Boolean compute() {
		for (int i = startIndex; i < endIndex; i++) {
			this.sharedBuffer.set(i, new Image(path[i].getPath()));
		}
		System.out.println("[Thread " + this.ID + "]: " + "finished");

		return true;
	}
}
