package practicas_eldom.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;

import javax.swing.AbstractButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JButton;

import CommTransport.CommTransport;
import CommTransport.Comm.io.ConnTransportAdaption;
import SerialComm.SerialCommTransport;
import SerialComm.connectors.jssc.SerialPortList;
import SerialComm.gui.SerialConfig;
import SerialComm.net.SerialAction;
import SerialComm.net.SerialConnection;
import Utilidades.threadpool.DefaultExecutorSupplier;
import modbus.Const_Modbus;
import modbus.Modbus;
import practicas_eldom.ConstantesApp;
import practicas_eldom.config.ConfigProperties;
import practicas_eldom.config.ConfigurationConnect;
import practicas_eldom.config.MB_Registers;
import practicas_eldom.gui.panel.Console;
import practicas_eldom.messages.Messages;

public class COMSerie_MainWindow {
	
	private 	boolean 				doExitOnRequest = true;
	private 	JCheckBoxMenuItem 		ConsoleSet;	
	private     Console					serialConsole;
	private 	JMenu 					mnMenuSerie;
	private		JLabel 					statusBar_msgLeft;	
	
	private 	JFrame 					window;
	private 	CommTransport 			sn_Transport;
	
	private 	ConfigurationConnect	configConnect;
	
	public COMSerie_MainWindow() {
		
		configConnect = new ConfigurationConnect();
		
		JFrame.setDefaultLookAndFeelDecorated(true);

		Rectangle maxSize = GraphicsEnvironment.getLocalGraphicsEnvironment()
								.getMaximumWindowBounds();
		
		window = new JFrame(ConstantesApp.WINDOW_TITLE);
		window.setAlwaysOnTop(true);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);		
		
		window.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {		
				Dimension windowSize = window.getSize();
				configConnect.setProperty(ConfigProperties.PROP_WINSIZE, windowSize.width +", "+windowSize.height);		
				serialConsoleLocation();	
			}
			@Override
			public void componentMoved(ComponentEvent arg0) {
				Point WinLocation = window.getLocation();
				configConnect.setProperty(ConfigProperties.PROP_WINLOCATION, WinLocation.x + ", " + WinLocation.y);
				serialConsoleLocation();
			}			
		});
				
		window.setLocationByPlatform(true);
		if (maxSize != null) {
			window.setMaximizedBounds(maxSize);
		}
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setToolTipText("");
		menuBar.setForeground(Color.WHITE);
		menuBar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		menuBar.setMargin(new Insets(5, 5, 5, 5));
		menuBar.setBackground(Color.ORANGE);
		window.setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu(Messages.ARCHIVO);
		menuBar.add(mnNewMenu);

		JMenuItem ExitItem = new JMenuItem(Messages.SALIR);
		ExitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		mnNewMenu.add(ExitItem);
		
		JMenu mnNewMenu_1 = new JMenu(Messages.COMUNICACIONES);
		mnNewMenu_1.setForeground(Color.BLACK);
		mnNewMenu_1.setBackground(Color.BLACK);
		menuBar.add(mnNewMenu_1);
		
		initSerialTransport(mnMenuSerie);
		
		genMenuSerie(mnNewMenu_1);		
		
		ConsoleSet = new JCheckBoxMenuItem(Messages.CONSOLE);
		mnNewMenu_1.add(ConsoleSet);
		
		ConsoleSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				AbstractButton aButton = (AbstractButton) paramActionEvent.getSource();
		        boolean selected = aButton.getModel().isSelected();
		        
		        if(selected){
		        	serialConsole = new Console();
		        	serialConsole.addActionListener(new ActionListener() {
		    			public void actionPerformed(ActionEvent e) {
		    				ConsoleMessage(e.getActionCommand());
		    			}
		    		});
		        	serialConsole.setVisible(true);
		        	
		        	serialConsoleLocation();
		        }
		        else{ 
		        	serialConsole.Close();
		        }
		        
		        configConnect.setProperty(ConfigProperties.PROP_SERIALCONSOLE,String.valueOf(selected));
		        configConnect.SaveConfig();
			}
		});
		window.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel statusBar = new JPanel();
		window.getContentPane().add(statusBar, BorderLayout.SOUTH);
		
		statusBar_msgLeft = new JLabel(" " + "Good Day!", JLabel.LEFT);
		statusBar_msgLeft.setForeground(Color.black);
		statusBar.add(statusBar_msgLeft);
		
		JLabel welcomedate = new JLabel();
        welcomedate.setOpaque(true);
        welcomedate.setBackground(Color.black);
        welcomedate.setForeground(Color.WHITE);
        statusBar.add(welcomedate);
        
        statusBar.setLayout(new BorderLayout());
        statusBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        statusBar.setBackground(Color.LIGHT_GRAY);
        statusBar.add(statusBar_msgLeft, BorderLayout.WEST);
        statusBar.add(welcomedate, BorderLayout.EAST);
        
        Timer timer = new Timer (1000, new ActionListener ()
		{
			public void actionPerformed(ActionEvent e)
		    {	java.util.Date now = new java.util.Date();
            String ss = DateFormat.getDateTimeInstance().format(now);
            welcomedate.setText(ss);
            welcomedate.setToolTipText("Welcome, Today is " + ss);
		    }
		});
		
		timer.start();
		
		statusBar_msgLeft.setText(Messages.PORTSELTOCOMM);
		
		// --- PANEL DE BOTONES ---
		JPanel panel_1 = new JPanel();
		window.getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(null);
		
		JButton btnOnRele = new JButton("ON RELE");
		btnOnRele.setBounds(30, 30, 120, 30);
		btnOnRele.addActionListener(e -> enviarComandoModBus(MB_Registers.MB_RELE, "1"));
		panel_1.add(btnOnRele);

		JButton btnOffRele = new JButton("OFF RELE");
		btnOffRele.setBounds(160, 30, 120, 30);
		btnOffRele.addActionListener(e -> enviarComandoModBus(MB_Registers.MB_RELE, "0"));
		panel_1.add(btnOffRele);

		JButton btnOnTriac = new JButton("ON TRIAC");
		btnOnTriac.setBounds(30, 80, 120, 30);
		btnOnTriac.addActionListener(e -> enviarComandoModBus(MB_Registers.MB_TRIAC, "1"));
		panel_1.add(btnOnTriac);

		JButton btnOffTriac = new JButton("OFF TRIAC");
		btnOffTriac.setBounds(160, 80, 120, 30);
		btnOffTriac.addActionListener(e -> enviarComandoModBus(MB_Registers.MB_TRIAC, "0"));
		panel_1.add(btnOffTriac);
	} 
	
	// --- MÉTODO MODBUS FUERA DEL CONSTRUCTOR ---
	private void enviarComandoModBus(int registro, String valor) {
		String slaveAdd = "1";
		String reg = Integer.toString(registro);
		String[] args = {slaveAdd, String.valueOf(Const_Modbus.WRITE_COIL), reg, valor};
		
		DefaultExecutorSupplier.getInstance().forCommListen().execute(new Runnable() {
			@Override
			public void run() {
				Modbus.InitComunication(args, sn_Transport);
			}
		});
	}
	
	// --- MÉTODOS ORIGINALES DE CONEXIÓN ---
	public void genMenuSerie(JMenu mnMenu){
		mnMenuSerie = new JMenu(Messages.SERIE);
		
		mnMenuSerie.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				log(Messages.LOOKPORTS + "\n\s");
				SetMenu_SerialPorts(mnMenuSerie);
				SetMenu_PortConfig(mnMenuSerie);
			}
		});
		
		SetMenu_SerialPorts(mnMenuSerie);
		SetMenu_PortConfig(mnMenuSerie);
		mnMenu.add(mnMenuSerie);
		mnMenu.addSeparator();
	}
	
	private void SetMenu_PortConfig(JMenu mnMenu) {
		mnMenu.addSeparator();
		
		JMenuItem mntmNewMenuItem = new JMenuItem(Messages.OPT_SERIE);
		mnMenu.add(mntmNewMenuItem);
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				SerialConfig frame = new SerialConfig(configConnect.getProperties(), ConfigProperties.CONFIG_FILE);
				frame.addWindowListener(new WindowAdapter(){
					public void windowClosed(WindowEvent e) {
						window.setAlwaysOnTop(true);
					}
				});
				window.setAlwaysOnTop(false);
		        frame.setVisible(true); 
				frame.setAlwaysOnTop(true);
		        frame.toFront();
			}
		});
	}

	public int SetMenu_SerialPorts(JMenu mnMenu){
		SerialConnection serialConnection = ((SerialCommTransport)sn_Transport).getSerialConnection();
		String[] ports = SerialPortList.getPortNames();
		
		if(ports.length == 0){
			mnMenu.setEnabled(false);
		}
		else {	
			mnMenu.setEnabled(true);
			mnMenu.removeAll();
		
			for(String port : ports){
				JRadioButtonMenuItem mntmPrueba = new JRadioButtonMenuItem(port);
				mnMenu.add(mntmPrueba);
				mntmPrueba.addActionListener(new SerialAction("Connect to serial", serialConnection));
			}
		}
		return ports.length;
	}
	
	private void initSerialTransport(JMenu mnMenu){
		sn_Transport = new SerialCommTransport(ConstantesApp.SERIALCONNECTION);
		sn_Transport.loadConfig(configConnect.getProperties());
		sn_Transport.addTransportListener(new ConnTransportAdaption(){
			@Override
			public void logTransport(String message){
				log(message);
			}
			@Override 
			public void SystemMessage(String message){
				setSystemMessage(message);
			}
			@Override 
			public void CT_Opened(String message){
				statusBar_msgLeft.setText(ConstantesApp.STATUSBAR_MSGLEFT +  message);
			}
			@Override
			public void CT_Closed() {
				statusBar_msgLeft.setText(ConstantesApp.STATUSBAR_MSGLEFT);
			}
		});
	}
	
	protected void setSystemMessage(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (message == null) {
					window.setTitle(ConstantesApp.WINDOW_TITLE);
				} else {
					window.setTitle(ConstantesApp.WINDOW_TITLE + " (" + message + ')');
				}
			}
		});
	}
		
	private void log(String Msg){
		if((serialConsole == null)||(!serialConsole.isVisible())){
			System.out.print(Msg);
		}
		else
			serialConsole.log(Msg);
	}
	
	private void exit() {
		if (doExitOnRequest) {
			System.exit(0);
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}

	private void serialConsoleLocation(){
		if(serialConsole != null){
			if(serialConsole.isVisible()){ 
				Dimension windowSize = window.getSize();
				serialConsole.setSize(serialConsole.getWidth(), windowSize.height);
				Point WinLocation = window.getLocation(); 
				serialConsole.setLocation(WinLocation.x + windowSize.width, WinLocation.y);
			}
		}
	}

	private void ConsoleMessage(final String message){
		if(message == "Close"){
			ConsoleSet.setState(false);
		}
	}
	
	public void start() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String s = configConnect.getProperty(ConfigProperties.PROP_WINSIZE);
				String l = configConnect.getProperty(ConfigProperties.PROP_WINLOCATION);
				
				window.setVisible(true);
				
				if(s != null){
					String[] r = s.split(", ");
					window.setSize(new Dimension(Integer.parseInt(r[0]), Integer.parseInt(r[1])));
				} else window.setSize(new Dimension(799, 535)); 
				
				if(l != null){
					String[] r = l.split(", ");
					window.setLocation(Integer.parseInt(r[0]), Integer.parseInt(r[1]));
				} 
				
				if(Boolean.valueOf(configConnect.getProperty(ConfigProperties.PROP_SERIALCONSOLE))){
					ConsoleSet.doClick();
				}
			}
		});
	}
}