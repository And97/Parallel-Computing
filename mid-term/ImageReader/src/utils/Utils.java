package utils;

import java.io.File;
import javax.swing.JOptionPane;

/*
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
Classe contente metodi di utilità
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
*/
public class Utils {

	public static File[] walkDirectory(String directory) {
		File folder = new File(directory);
		File[] listOfFiles = folder.listFiles();
		return listOfFiles;

	}

	public static int countFiles(String directory) {
		return walkDirectory(directory).length;
	}

	public static int getThreadNumber() {
		return Runtime.getRuntime().availableProcessors();
	}

	public static void infoBox(String infoMessage, String titleBar) {
		JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
	}

	public static void errorBox(String infoMessage, String titleBar) {
		JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.ERROR_MESSAGE);
	}

}
