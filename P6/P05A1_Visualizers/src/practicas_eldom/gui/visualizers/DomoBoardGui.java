package practicas_eldom.gui.visualizers;

import java.awt.Component;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Timer;

import CommTransport.CommTransport;
import Utilidades.modbus.ModBus_Communications;
import eu.hansolo.steelseries.extras.LightBulb;
import eu.hansolo.steelseries.extras.Led;
import eu.hansolo.steelseries.tools.LedColor;
import practicas_eldom.config.MB_Registers;
import practicas_eldom.config.MB_Registers.TSwitchState;

public class DomoBoardGui extends JPanel implements Visualizer {
	private static final long serialVersionUID = 1L;
	
	private LightBulb lightBulb1;
	private LightBulb lightBulb2;
	private Led ledBoton1, ledBoton2, ledOpto;
	
	private final String category;
	private final CommTransport sn_Transport;
	private final String address;
	private Timer pollingTimer;

	public DomoBoardGui(String category, String address, CommTransport sn_Transport) {
		super();
		this.category = category;
		this.address = address;
		this.sn_Transport = sn_Transport;
		this.setLayout(null);

		// --- Actuadores ---
		lightBulb1 = new LightBulb();
		lightBulb1.setToolTipText("RELÉ");
		lightBulb1.setGlowColor(Color.YELLOW);
		lightBulb1.setBounds(10, 11, 78, 78);
		lightBulb1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) { ONOFF_Bulb(lightBulb1); }
		});
		add(lightBulb1);
		
		lightBulb2 = new LightBulb();
		lightBulb2.setToolTipText("TRIAC");
		lightBulb2.setGlowColor(Color.RED);
		lightBulb2.setBounds(98, 11, 78, 78);
		lightBulb2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) { ONOFF_Bulb(lightBulb2); }
		});
		add(lightBulb2);

		// --- Sensores (Botones) ---
		ledBoton1 = new Led();
		ledBoton1.setLedColor(LedColor.GREEN);
		ledBoton1.setBounds(200, 20, 50, 50);
		add(ledBoton1);

		ledBoton2 = new Led();
		ledBoton2.setLedColor(LedColor.GREEN);
		ledBoton2.setBounds(260, 20, 50, 50);
		add(ledBoton2);

		ledOpto = new Led();
		ledOpto.setLedColor(LedColor.RED);
		ledOpto.setBounds(320, 20, 50, 50);
		add(ledOpto);

		// Etiquetas
		JLabel lblRele = new JLabel("RELÉ");
		lblRele.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		lblRele.setBounds(25, 92, 63, 24);
		add(lblRele);
		
		JLabel lblTriac = new JLabel("TRIAC");
		lblTriac.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		lblTriac.setBounds(110, 92, 63, 24);
		add(lblTriac);

		JLabel lblInputs = new JLabel("BTN1    BTN2    OPTO");
		lblInputs.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblInputs.setBounds(205, 80, 180, 20);
		add(lblInputs);

		iniciarPolling();
	}
	
	private void ONOFF_Bulb(LightBulb bulb){
		TSwitchState state = bulb.isOn() ? TSwitchState.OFF : TSwitchState.ON;
		bulb.setOn(state == TSwitchState.ON);
		int reg = (bulb == lightBulb1) ? MB_Registers.ModBusRegisters.MB_RELE.getReg() : MB_Registers.ModBusRegisters.MB_TRIAC.getReg();
		ModBus_Communications.writeCoil(Integer.parseInt(address), reg, state, sn_Transport);  
	}

	private void iniciarPolling() {
		// timer cada 500 ms para no bloquear el hilo de la interfaz
		pollingTimer = new Timer(500, e -> {
			try {
				int slaveAdd = Integer.parseInt(address);
				// aprovecho la firma del metodo del .jar que actua por referencia y 
				// rellena el array con la respuesta
				int[] estadosBotones = new int[3]; 
				
				ModBus_Communications.readInputDiscrete(slaveAdd, 0, 3, sn_Transport, estadosBotones, new Runnable() {
					@Override
					public void run() {
						
						// actualizar luces (dentro del hilo) asi se evita parpadeo
						ledBoton1.setLedOn(estadosBotones[0] == 1);
						ledBoton2.setLedOn(estadosBotones[1] == 1);
						ledOpto.setLedOn(estadosBotones[2] == 1);
					}
				});
			} catch (Exception ex) {
				// recogida de errores 	
			}
		});
		pollingTimer.start();
	}

	@Override public String getCategory() { return category; }
	@Override public String getTitle() { return "Address : " + address; }
	@Override public Component getPanel() { return this; }
	@Override public boolean isCategory() { return true; }
}