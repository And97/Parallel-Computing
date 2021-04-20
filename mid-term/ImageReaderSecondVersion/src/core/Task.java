package core;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;
/*
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
La classe estende RecursiveTask, che permette di implementare e definire 
un singolo Task, ovvero un compito all'interno del paradigma Fork-Join.
Ogni Task ha il compito di caricare un certo numero di immagini 
predefinito all'interno di un buffer privato.

Attributi:
	-> ID (int) rappresenta l'identificativo del Task
	-> path(File[]) l'array condiviso tra tutti i task che contiene tutte 
		le immagini da caricare (cartella del file system). Ogni task 
		accederà solo alla porzione assegnata.
	-> imageToLoad(List<Image>) buffer privato in cui il task caricherà 
		le immagini
	-> startIndex(int), endIndex(int) rispettivamente l'indice iniziale 
		e finale del sottoinsieme delle immagini che dovrà caricare il 
		Task.
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
*/


public class Task extends RecursiveTask<ArrayList<Image>> {
	
	private static final long serialVersionUID=1L;
	private int ID;
	private File[] path;
	private ArrayList<Image> imageToLoad;
	private int startIndex;
	private int endIndex;
	
	
	public Task(int ID, File[] path, int startIndex, int endIndex) {
	
		this.ID=ID;
		this.path=path;
		this.imageToLoad=new ArrayList<>();
		this.startIndex=startIndex;
		this.endIndex=endIndex;
	}
	
	public int getID() {
		return this.ID;
	}
	
	/**
	 * @return the imageToLoad
	 */
	public ArrayList<Image> getImageToLoad() {
		return imageToLoad;
	}
	/*
	– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
	Override del metodo compute() ereditato dalla classe RecursiveTask,
	questo è il metodo che si occuperà di caricare effettivamente le 
	le immagini all'interno del buffer privato
	– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
	*/
	@Override
	protected ArrayList<Image> compute() {
		for(int i=startIndex; i<endIndex; i++) {
			this.imageToLoad.add(new Image(path[i].getPath()));
		}
		System.out.println("[Thread "+this.ID+"]: " +"finished");
		
		return imageToLoad;
	}
}	


