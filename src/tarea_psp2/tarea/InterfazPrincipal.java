package tarea_psp2.tarea;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class InterfazPrincipal {

	private JTextField campoUrl;

	private JList<String> urlLista;
	private DefaultListModel<String> listaModelo;

	private JButton btnEliminaUrl;
	private JButton btnAbrirUrl;
	private JButton btnSeleccionarDirectorio;
	private JButton btnAppIzquierda;
	private JButton btnAppCentro;
	private JButton btnAppDerecha;

	private JPanel panelBotones;
	private JPanel panelNavegacion;
	private JPanel panelAreaTexto;

	private JScrollPane jScrollPane;

	private JLabel etiquetaUrl;
	private JLabel etiquetaTitulo;

	private GestorUrls gestorUrls;
	private LanzadorNavegador lanzadorNavegador;
	private LanzadorAplicacione lanzadorAplicacione;
	private String rutaFichero;

	public InterfazPrincipal() {
		this.gestorUrls = new GestorUrls();
		this.lanzadorNavegador = new LanzadorNavegador();
		this.listaModelo = new DefaultListModel<String>();
		this.lanzadorAplicacione = new LanzadorAplicacione();
	}

	private void initialize() {
		JFrame frame = new JFrame("Gestor de URLs");
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		panelBotones = new JPanel();
		panelBotones.setBounds(0, 0, 786, 150);
		panelBotones.setLayout(null);

		etiquetaTitulo = new JLabel("Gestion app");
		etiquetaTitulo.setBounds(300, 20, 200, 30);
		panelBotones.add(etiquetaTitulo);

		btnAppIzquierda = new JButton("Note Pad");

		btnAppIzquierda.setBounds(50, 70, 150, 69);
		btnAppIzquierda.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lanzadorAplicacione.lanzaAplicacion("notepad");

			}
		});
		panelBotones.add(btnAppIzquierda);

		btnAppCentro = new JButton("Paint");
		btnAppCentro.setBounds(220, 70, 150, 69);
		btnAppCentro.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lanzadorAplicacione.lanzaAplicacion("mspaint");

			}
		});
		panelBotones.add(btnAppCentro);

		btnAppDerecha = new JButton("Calculadora");
		btnAppDerecha.setBounds(394, 70, 150, 69);
		btnAppDerecha.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lanzadorAplicacione.lanzaAplicacion("calc");

			}
		});
		panelBotones.add(btnAppDerecha);
		panelNavegacion = new JPanel();
		panelNavegacion.setBounds(0, 145, 786, 105);
		panelNavegacion.setLayout(null);

		etiquetaUrl = new JLabel("URL:");
		etiquetaUrl.setBounds(38, 59, 50, 30);
		panelNavegacion.add(etiquetaUrl);

		campoUrl = new JTextField();
		campoUrl.setBounds(84, 59, 465, 30);
		panelNavegacion.add(campoUrl);

		btnAbrirUrl = new JButton("Abrir URL");
		btnAbrirUrl.setBounds(601, 59, 150, 30);
		panelNavegacion.add(btnAbrirUrl);

		btnAbrirUrl.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String url = campoUrl.getText().toLowerCase();
				if (!url.startsWith("http://") && !url.startsWith("https://")) {
					url = "http://" + url;
				}

				String urlPattern = "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";
				Pattern pattern = Pattern.compile(urlPattern);
				Matcher matcher = pattern.matcher(url);

				if (url.isEmpty() && !matcher.matches()) {
					JOptionPane.showMessageDialog(frame, "Por favor, ingresa una URL válida.", "Error url",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (!matcher.matches()) {
					JOptionPane.showMessageDialog(frame, "Por favor, ingresa una URL válida.", "Error url",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				lanzadorNavegador.abrirNavegadorConUrl(url);
				gestorUrls.guardarUrl(url);
				campoUrl.setText("");
				cargarListaUrls();
			}
		});

		panelAreaTexto = new JPanel();
		panelAreaTexto.setBounds(0, 250, 786, 300);
		panelAreaTexto.setLayout(null);

		urlLista = new JList<>(listaModelo);

		urlLista.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!urlLista.isSelectionEmpty()) {
					if (e.getClickCount() == 1) {
						btnEliminaUrl.setEnabled(true);
					} else if (e.getClickCount() == 2) {
						String selectedUrl = urlLista.getSelectedValue();
						campoUrl.setText(selectedUrl);

						campoUrl.requestFocus();

						campoUrl.selectAll();
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (urlLista.locationToIndex(e.getPoint()) == -1) {
					urlLista.clearSelection();
					btnEliminaUrl.setEnabled(false);
				}
			}
		});

		urlLista.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				btnEliminaUrl.setEnabled(!urlLista.isSelectionEmpty());
			}
		});
		jScrollPane = new JScrollPane(urlLista);
		jScrollPane.setBounds(50, 30, 500, 200);
		panelAreaTexto.add(jScrollPane);

		btnEliminaUrl = new JButton("Eliminar URL");
		btnEliminaUrl.setBounds(602, 30, 150, 30);
		btnEliminaUrl.setEnabled(false);
		panelAreaTexto.add(btnEliminaUrl);

		btnEliminaUrl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String urlSeleccionada = urlLista.getSelectedValue();
				if (urlSeleccionada != null) {
					gestorUrls.eliminarUrl(urlSeleccionada);
					cargarListaUrls();
				}
			}
		});

		frame.getContentPane().add(panelBotones);

		JButton btnAbreFichero = new JButton("Abrir Fichero");
		btnAbreFichero.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (rutaFichero == null) {
					new JOptionPane();
					JOptionPane.showMessageDialog(null, "Error selecionar el fichero",
							"ERROR", JOptionPane.WARNING_MESSAGE);
				} else {
					lanzadorAplicacione.lanzaAplicacion(rutaFichero);
				}

			}
		});
		
		btnAbreFichero.setBounds(604, 70, 150, 30);
		btnAbreFichero.setEnabled(false);
		panelBotones.add(btnAbreFichero);

		JButton btnSeleccionarFichero = new JButton("Seleccionar fichero");
		btnSeleccionarFichero.setBounds(604, 111, 150, 30);
		btnSeleccionarFichero.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser ficheroEligido = new JFileChooser();
				ficheroEligido.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int resultado = ficheroEligido.showOpenDialog(frame);
				if (resultado == JFileChooser.APPROVE_OPTION) {
					String ficheroSelecionado = ficheroEligido.getSelectedFile().toString();
					rutaFichero = ficheroSelecionado;
					btnSeleccionarFichero.setEnabled(true);
				}
			}
		});
		panelBotones.add(btnSeleccionarFichero);
		frame.getContentPane().add(panelNavegacion);
		frame.getContentPane().add(panelAreaTexto);

		btnSeleccionarDirectorio = new JButton("Seleccionar Directorio");
		btnSeleccionarDirectorio.setBounds(602, 96, 150, 30);
		panelAreaTexto.add(btnSeleccionarDirectorio);

		btnSeleccionarDirectorio.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser directorioEligido = new JFileChooser();
				directorioEligido.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int resultado = directorioEligido.showOpenDialog(frame);
				if (resultado == JFileChooser.APPROVE_OPTION) {
					File directorioSeleccionado = directorioEligido.getSelectedFile();
					gestorUrls.establecerUrl(directorioSeleccionado);
				}
			}
		});

		frame.setVisible(true);

		gestorUrls.inicializarArchivo();

		cargarListaUrls();
	}

	private void cargarListaUrls() {
		listaModelo.clear();
		Set<String> urls = gestorUrls.cargarUrls();
		for (String url : urls) {
			listaModelo.addElement(url);
		}
	}

	public static void main(String[] args) {

		new InterfazPrincipal().initialize();
	}
}

class GestorUrls {

	private String rutaArchivo = "url.txt";

	public void establecerUrl(File directorioSeleccionado) {
		this.rutaArchivo = directorioSeleccionado + "\\urls.txt";
	}

	public void inicializarArchivo() {
		File archivo = new File(rutaArchivo);
		if (!archivo.exists()) {
			try {
				archivo.createNewFile();
			} catch (IOException e) {
				new JOptionPane();
				JOptionPane.showMessageDialog(null, "Error al inizializar el fichero",
						"ERROR", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	public void guardarUrl(String url) {
		try (BufferedWriter escritor = new BufferedWriter(new FileWriter(rutaArchivo, true))) {
			escritor.write(url);
			escritor.newLine();
		} catch (IOException e) {
			new JOptionPane();
			JOptionPane.showMessageDialog(null, "Error al actualizar el fichero",
					"ERROR", JOptionPane.WARNING_MESSAGE);
		}
	}

	private void actualizarUrls(Set<String> urls) {
		try (BufferedWriter escritor = new BufferedWriter(new FileWriter(rutaArchivo))) {
			for (String u : urls) {
				escritor.write(u);
				escritor.newLine();
			}
		} catch (FileNotFoundException e) {
			new JOptionPane();
			JOptionPane.showMessageDialog(null, "Error al actualizar el fichero",
					"ERROR", JOptionPane.WARNING_MESSAGE);
		} catch (IOException e) {
			new JOptionPane();
			JOptionPane.showMessageDialog(null, "Error al actualizar el fichero",
					"ERROR", JOptionPane.WARNING_MESSAGE);
		}
	}

	public Set<String> cargarUrls() {
		Set<String> urls = new HashSet<>();

		try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
			String linea;
			while ((linea = lector.readLine()) != null) {
				urls.add(linea);
			}
		} catch (FileNotFoundException e) {
			new JOptionPane();
			JOptionPane.showMessageDialog(null, "Error al cargar el fichero",
					"ERROR", JOptionPane.WARNING_MESSAGE);
		} catch (IOException e) {
			new JOptionPane();
			JOptionPane.showMessageDialog(null, "Error al cargar el fichero",
					"ERROR", JOptionPane.WARNING_MESSAGE);
		}

		return urls;
	}

	public void eliminarUrl(String url) {
		Set<String> urls = cargarUrls();

		if (urls.remove(url)) {
			actualizarUrls(urls);
		}
	}

}

class LanzadorNavegador {

	public void abrirNavegadorConUrl(String url) {
		ProcessBuilder processBuilder = new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url);

		try {
			Process proceso = processBuilder.start();

			int codigoSalida = proceso.waitFor();

			if (codigoSalida != 0) {
				new JOptionPane();
				JOptionPane.showMessageDialog(null, "Error al abrir el navegador. Código de salida: " + codigoSalida,
						"ERROR", JOptionPane.WARNING_MESSAGE);
			}
		} catch (IOException e) {
			new JOptionPane();
			JOptionPane.showMessageDialog(null, "Error al ejecutar el comando: " + e.getMessage(), "ERROR",
					JOptionPane.WARNING_MESSAGE);
		} catch (InterruptedException e) {
			new JOptionPane();
			JOptionPane.showMessageDialog(null, "El proceso fue interrumpido: " + e.getMessage(), "ERROR",
					JOptionPane.WARNING_MESSAGE);
		}
	}
}

class LanzadorAplicacione {
	public void lanzaAplicacion(String app) {
		ProcessBuilder procesoBuilder = new ProcessBuilder("CMD", "/c", app);

		try {
			Process proceso = procesoBuilder.start();

			int codigoSalida = proceso.waitFor();

			if (codigoSalida != 0) {
				new JOptionPane();
				JOptionPane.showMessageDialog(null, "Error al abrir la applicacion. Código de salida: " + codigoSalida,
						"ERROR", JOptionPane.WARNING_MESSAGE);
			}
		} catch (IOException e) {
			new JOptionPane();
			JOptionPane.showMessageDialog(null, "Error al ejecutar el comando: " + e.getMessage(), "ERROR",
					JOptionPane.WARNING_MESSAGE);
		} catch (InterruptedException e) {
			new JOptionPane();
			JOptionPane.showMessageDialog(null, "El proceso fue interrumpido: " + e.getMessage(), "ERROR",
					JOptionPane.WARNING_MESSAGE);
		}

	}
}
