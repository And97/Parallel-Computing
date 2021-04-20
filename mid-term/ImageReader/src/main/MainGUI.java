package main;

import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.Point;
import java.awt.Toolkit;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import core.Image;
import core.Worker;
import utils.Utils;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;

/*
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
Classe che si occupa dell'avvio del programma. In particolare in questa 
classe si genererà anche una finestra in cui l'utente potrà scegliere 
la directory da cui caricare le immagini e il numero di thread da 
utilizzare.
Successivamente viene creato e fatto partire il thread runWorker che si 
occuperà di generare, avviare e far completare i task.

L'utente potrà aprire le immagini con un doppio click, nel caso in cui 
l'immagine non sia ancora in memoria, riceverà un messaggio di errore
– – – – – – – – – – – – – – – – – – – – – – – – – – – – – - - - - - - -
*/

public class MainGUI {

	private JFrame ImageReader;
	private JTable table;
	private JScrollPane scrollPane;
	private JTextField txtBaseFolder;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGUI window = new MainGUI();
					window.ImageReader.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainGUI() {
		initialize();
	}

	public static void createEmptyImageArrayList(List<Image> images, String directory) {
		for (int i = 0; i < Utils.countFiles(directory); i++) {
			images.add(new Image());
		}

	}
	//metodo per dimensionare le colonne all'interno della tabella
	public static void setJTableColumnsWidth(JTable table, int tablePreferredWidth, double... percentages) {
		double total = 0;
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			total += percentages[i];
		}

		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth((int) (tablePreferredWidth * (percentages[i] / total)));
		}
	}

	private void initialize() {

		
		ImageReader = new JFrame();
		ImageReader.setIconImage(
				Toolkit.getDefaultToolkit().getImage("C:\\Users\\Andrea\\eclipse-workspace\\mid-term\\icon.png"));
		ImageReader.setFont(new Font("Arial", Font.BOLD, 14));
		ImageReader.setTitle("Image Reader");
		ImageReader.getContentPane().setBackground(Color.WHITE);
		ImageReader.setBounds(100, 100, 800, 600);
		ImageReader.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImageReader.getContentPane().setLayout(null);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - ImageReader.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - ImageReader.getHeight()) / 2);
		ImageReader.setLocation(x, y);

		
		txtBaseFolder = new JTextField();
		txtBaseFolder.setFont(new Font("Arial", Font.PLAIN, 14));
		txtBaseFolder.setEditable(false);
		txtBaseFolder.setBounds(220, 30, 383, 35);
		ImageReader.getContentPane().add(txtBaseFolder);
		txtBaseFolder.setColumns(10);

	
		

		JLabel lblFolder = new JLabel("Select the image folder:");
		lblFolder.setFont(new Font("Arial", Font.BOLD, 14));
		lblFolder.setBounds(31, 42, 179, 14);
		ImageReader.getContentPane().add(lblFolder);
		
		JButton btnSelectDirectory = new JButton("Open");
		btnSelectDirectory.setForeground(SystemColor.text);
		btnSelectDirectory.setFont(new Font("Arial", Font.BOLD, 14));
		btnSelectDirectory.setBackground(SystemColor.textHighlight);
		btnSelectDirectory.setBounds(655, 28, 107, 43);
		ImageReader.getContentPane().add(btnSelectDirectory);
	
		DefaultTableModel model = new DefaultTableModel();
		scrollPane = new JScrollPane();
		scrollPane.setBounds(31, 163, 731, 337);
		ImageReader.getContentPane().add(scrollPane);
		model.addColumn("ID");
		model.addColumn("Filename");
		table = new JTable(model);
		scrollPane.setViewportView(table);
		//dimensionamento delle colonne
		setJTableColumnsWidth(table, 480, 15, 85);

		
		JComboBox<Integer> cmbThreadNumber = new JComboBox<Integer>();
		cmbThreadNumber.setFont(new Font("Arial", Font.BOLD, 14));

		String[] listThread = new String[12];
		for (int i = 0; i < 12; i++) {
			listThread[i] = String.valueOf(i + 1);
		}

		cmbThreadNumber.setModel(new DefaultComboBoxModel(listThread));
		cmbThreadNumber.setBounds(271, 109, 104, 35);
		ImageReader.getContentPane().add(cmbThreadNumber);
		
		JButton btnLoadImages = new JButton("Load");
		btnLoadImages.setForeground(SystemColor.text);
		btnLoadImages.setBackground(SystemColor.textHighlight);
		btnLoadImages.setFont(new Font("Arial", Font.BOLD, 14));
		btnLoadImages.setBounds(437, 103, 107, 43);
		ImageReader.getContentPane().add(btnLoadImages);
		btnLoadImages.setEnabled(false);

		JLabel lblThreadNumber = new JLabel("Select the number of threads");
		lblThreadNumber.setFont(new Font("Arial", Font.BOLD, 14));
		lblThreadNumber.setBounds(31, 125, 215, 14);
		ImageReader.getContentPane().add(lblThreadNumber);

		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.info);
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(31, 522, 227, 31);
		ImageReader.getContentPane().add(panel);

		JLabel lblThread = new JLabel("Number of threads:");
		lblThread.setVerticalAlignment(SwingConstants.TOP);
		lblThread.setHorizontalAlignment(SwingConstants.CENTER);
		lblThread.setFont(new Font("Arial", Font.BOLD, 14));
		panel.add(lblThread);

		JLabel lblThreadNumber_2 = new JLabel("");
		lblThreadNumber_2.setFont(new Font("Arial", Font.BOLD, 14));
		lblThreadNumber_2.setForeground(new Color(255, 0, 0));
		panel.add(lblThreadNumber_2);
		lblThreadNumber_2.setText(Integer.toString(Runtime.getRuntime().availableProcessors()));

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBackground(SystemColor.info);
		panel_1.setBounds(540, 522, 222, 31);
		ImageReader.getContentPane().add(panel_1);

		JLabel lblFile = new JLabel("Number of file:");
		lblFile.setVerticalAlignment(SwingConstants.TOP);
		lblFile.setHorizontalAlignment(SwingConstants.CENTER);
		lblFile.setFont(new Font("Arial", Font.BOLD, 14));
		panel_1.add(lblFile);

		JLabel lblNumberFile = new JLabel("0");
		lblNumberFile.setForeground(Color.RED);
		lblNumberFile.setFont(new Font("Arial", Font.BOLD, 14));
		panel_1.add(lblNumberFile);

		// metodo per il riempimento della tabella
		btnSelectDirectory.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				model.setRowCount(0); 
				JFileChooser f = new JFileChooser();
				f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				f.showSaveDialog(null);
				txtBaseFolder.setText(f.getSelectedFile().toString());
				lblNumberFile.setText(String.valueOf(Utils.countFiles(f.getSelectedFile().toString())));
				btnLoadImages.setEnabled(true);

				File[] images = Utils.walkDirectory(f.getSelectedFile().toString()); 
				//calcolo del totale dei file presenti nella directory scelta
				int i = 0;
				for (File f1 : images) {
					model.addRow(new String[] { Integer.toString(i), f1.toString() });
					i = i + 1;
				}

			}
		});

		List<Image> sharedBuffer = Collections.synchronizedList(new ArrayList<Image>());
	
		//alla pressione del tasto load questo metodo creerà  il thread runWorker che si 
		//occuperà di generare, avviare e far completare i task
		
		btnLoadImages.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				String directory = txtBaseFolder.getText();
				int threadNumber = cmbThreadNumber.getSelectedIndex() + 1;
				File[] images = Utils.walkDirectory(directory); 
				createEmptyImageArrayList(sharedBuffer, directory);

				Thread runWorker = new Thread(() -> {
					Worker forkJoinWorker = new Worker(directory, sharedBuffer, images);
					forkJoinWorker.setThreadNumber(threadNumber);
					forkJoinWorker.start();
				});

				runWorker.start();

			}
		});

		table.addMouseListener(new MouseAdapter() {
			//al doppio click su una riga della tabella, questo metodo permette di visualizzare l'immagine
			//nel caso in cui l'immagine non sia ancora caricata l'utente riceverà un errore
			public void mousePressed(MouseEvent mouseEvent) {
				JTable table = (JTable) mouseEvent.getSource();
				Point point = mouseEvent.getPoint();
				int row = table.rowAtPoint(point);
				if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
					//System.out.println(table.getModel().getValueAt(row, 1));
					Image chosen = sharedBuffer.get(row); 
					if (!chosen.getPath().equals("empty")) {
						chosen.draw();
					} else {
						Utils.errorBox("The image was not uploaded!\nTry again soon...", "Error");
					}

				}
			}
		});

	}
}
