package core;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/*
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
La classe si occupa della creazione di un oggetto di tipo Image
con due attributi:

	-> path (String) che rappresenta il path assoluto di un'immagine
		all'interno del file system
	-> icon (ImageIcon) utile per inserire il titolo nel frame che 
	mostrerà l'immagine.
	
L'inizializzazione del path con la stringa empty è utile per riuscire 
a capire se un'immagine è stata realmente caricata o se ancora non lo 
è.
Oltre alla creazione dell'oggetto, la classe si occupa di visualizzare un'immagine
quando viene richiesta dall'utente con il metodo draw()
	
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
*/

public class Image {
	private String path;
	private ImageIcon icon;
	
	public Image(String path) {
		this.path=path;
		this.icon=new ImageIcon(path);
	}
	
	public Image() {
		this.path="empty";
		this.icon=new ImageIcon("empty");
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return  this.getClass().getName()+"[path=" + path + "]";
	}
	
	/*metodo non utilizzato, manca la GUI
	public void draw() {
		JFrame frame = new JFrame();
		JLabel label = new JLabel(icon);
		frame.add(label);
		frame.pack();
		frame.setTitle(path);
		frame.setVisible(true);
	}*/
}
	
	
	
	
	
	
	

	