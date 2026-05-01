package practicas_eldom.gui.visualizers;

import java.awt.Component;
import java.awt.Color;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import CommTransport.CommTransport;
import Utilidades.modbus.ModBus_Communications;
import eu.hansolo.steelseries.extras.LightBulb;
import modbus.Const_Modbus;
import modbus.ModBusEvent;
import practicas_eldom.config.MB_Registers;
import practicas_eldom.config.MB_Registers.MB_Discrete_Input_Contacts;
import practicas_eldom.config.MB_Registers.MB_Discrete_Output_Coils;
import practicas_eldom.config.MB_Registers.TSwitchState;

import javax.swing.JLabel;

import java.awt.Font;
import javax.swing.border.BevelBorder;
import javax.swing.SwingConstants;
import eu.hansolo.steelseries.extras.Led;
import javax.swing.border.LineBorder;

public class DomoBoardGui extends JPanel implements Visualizer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8619767299083215147L;
	private 		MouseAdapter 		ma_lightBulb;
	private final 	String 				category;
	private final 	boolean				isCategory = true;
	private final	CommTransport 		sn_Transport;
	private final 	int					address;
	private			LightBulb 			lightBulb1;
	private			LightBulb 			lightBulb2;
	private			Led 				ledBtn1;
	private			Led 				ledBtn2;
	private			Led 				ledBtnOpt;
	
	
	// variables pir
	private Led ledPir;               // movimiento
	private javax.swing.JCheckBox chkPirEnable; // interruptor
	private javax.swing.JSlider sldPirTimer;    // slider tiempo
	private JLabel lblPirTimerValue;
	
	//Banco de registros para mantener sincronizada la comunicaci�n Modbus 
	private  		int 				Cregs[];
	private  		int 				Dregs[];
	
	private			 boolean     		stActualize = true;

	public DomoBoardGui(String category, int address, CommTransport sn_Transport) {
		
		super();
		
		this.category 		= category;
		this.address		= address;
		this.sn_Transport 	= sn_Transport;
		
		this.setLayout(null);
		
		//Crea Banco de registros para mantener sincronizada la comunicaci�n Modbus 
		Cregs = new int [MB_Registers.MB_Discrete_Output_Coils.MB_O_COILS.getReg()];
		Dregs = new int [MB_Registers.MB_Discrete_Input_Contacts.MB_I_REGS.getReg()];
		
		ma_lightBulb = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ONOFF_Bulb(((LightBulb)e.getComponent()));
			}
		};
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(0, 0, 255), new Color(0, 255, 0), Color.BLUE, Color.MAGENTA));
		panel.setBounds(10, 22, 172, 100);
		add(panel);
		
		JLabel label = new JLabel("Estado Pulsadores");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setForeground(Color.RED);
		label.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		label.setBounds(0, 75, 166, 14);
		panel.add(label);
		
		ledBtn1 = new Led();
		ledBtn1.setBounds(11, 11, 36, 36);
		panel.add(ledBtn1);
		
		ledBtn2 = new Led();
		ledBtn2.setBounds(67, 11, 36, 36);
		panel.add(ledBtn2);
		
		ledBtnOpt = new Led();
		ledBtnOpt.setBounds(120, 11, 36, 36);
		panel.add(ledBtnOpt);
		
		JLabel label_1 = new JLabel("BTN 1");
		label_1.setForeground(Color.BLUE);
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		label_1.setBounds(11, 43, 36, 14);
		panel.add(label_1);
		
		JLabel label_2 = new JLabel("BTN 2");
		label_2.setForeground(Color.BLUE);
		label_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		label_2.setBounds(67, 43, 36, 14);
		panel.add(label_2);
		
		JLabel label_3 = new JLabel("BTN_OPT");
		label_3.setForeground(Color.BLUE);
		label_3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		label_3.setBounds(110, 43, 56, 14);
		panel.add(label_3);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 255), 2));
		panel_1.setBounds(199, 11, 215, 123);
		add(panel_1);
		panel_1.setLayout(null);
		
		lightBulb1 = new LightBulb();
		lightBulb1.setOn(true);
		lightBulb1.setGlowColor(Color.RED);
		lightBulb1.setBounds(10, 11, 78, 78);
		panel_1.add(lightBulb1);
		lightBulb1.addMouseListener(ma_lightBulb);
		
		JLabel lblNewLabel = new JLabel("REL\u00C9");
		lblNewLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		lblNewLabel.setBounds(20, 92, 76, 24);
		panel_1.add(lblNewLabel);
		
		lightBulb2 = new LightBulb();
		lightBulb2.setOn(true);
		lightBulb2.setGlowColor(Color.YELLOW);
		lightBulb2.setBounds(111, 11, 78, 78);
		panel_1.add(lightBulb2);
		lightBulb2.addMouseListener(ma_lightBulb);
		
		JLabel lblRel = new JLabel("TRIAC");
		lblRel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		lblRel.setBounds(119, 92, 70, 24);
		panel_1.add(lblRel);
		
		
		
		
		JPanel panelPir = new JPanel();
		panelPir.setBorder(new LineBorder(Color.RED, 2));
		panelPir.setBounds(10, 140, 404, 80); 
		panelPir.setLayout(null);
		add(panelPir);

		JLabel lblPir = new JLabel("Control PIR (P7)");
		lblPir.setFont(new Font("Times New Roman", Font.BOLD, 16));
		lblPir.setBounds(10, 10, 150, 20);
		panelPir.add(lblPir);

		// led que avisa del movimiento
		ledPir = new Led();
		ledPir.setBounds(150, 5, 30, 30);
		panelPir.add(ledPir);

		// caja para encender/apagar el pir
		chkPirEnable = new javax.swing.JCheckBox("PIR Activado");
		chkPirEnable.setSelected(true);
		chkPirEnable.setBounds(10, 45, 120, 20);
		panelPir.add(chkPirEnable);
		
		// si hacemos clic escribimos en el Aregs
		chkPirEnable.addActionListener(e -> {
			int val = chkPirEnable.isSelected() ? 1 : 0;
			// usamos funcion 06 write single register para enviar orden
			ModBus_Communications.writeSingleRegister(address, MB_Registers.MB_Analog_Output_Holding.MB_PIR_ENABLE.getReg(), val, sn_Transport);
		});

		JLabel lblTimer = new JLabel("Temporizador:");
		lblTimer.setBounds(130, 45, 90, 20);
		panelPir.add(lblTimer);

		// slider del tiempo de 1-30s y 3 por defecto
		sldPirTimer = new javax.swing.JSlider(1, 30, 3);
		sldPirTimer.setBounds(220, 45, 120, 20);
		panelPir.add(sldPirTimer);
		
		lblPirTimerValue = new JLabel("3 s");
		lblPirTimerValue.setBounds(350, 45, 40, 20);
		panelPir.add(lblPirTimerValue);

		// solo actualiza el texto para no saturar el sistema con demasiadas ordenes modbus
		sldPirTimer.addChangeListener(e -> {
			lblPirTimerValue.setText(sldPirTimer.getValue() + " s");
		});
		
		sldPirTimer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// se manda la orden al arduino solo al soltar el raton
				ModBus_Communications.writeSingleRegister(address, MB_Registers.MB_Analog_Output_Holding.MB_PIR_TIMER.getReg(), sldPirTimer.getValue(), sn_Transport);
			}
		});
	}
	
	private void ONOFF_Bulb(LightBulb lightBulb){
		
		int vBulb;
		int vReg;
		
		lightBulb.setOn(!lightBulb.isOn());
		
		if(lightBulb.isOn()) vBulb = TSwitchState.ToNumber(TSwitchState.ON);
		else vBulb = TSwitchState.ToNumber(TSwitchState.OFF);
		
		if(lightBulb == lightBulb1) vReg = MB_Registers.MB_Discrete_Output_Coils.MB_RELE.getReg();
		else vReg = MB_Registers.MB_Discrete_Output_Coils.MB_TRIAC.getReg();
		
		
		buildModBus(address, Const_Modbus.WRITE_COIL, vReg, vBulb, Cregs);
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public String getTitle() {
		return "Address : "+address;
	}

	@Override
	public Component getPanel() {
		return this;
	}

	@Override
	public boolean isCategory() {
		return isCategory;
	}

	@Override
	public void Actualize() {
		
		// Actualizar dispositivos modbus
		
		if(stActualize) {	
			
			//Read OutputCoils
			buildModBus(1, Const_Modbus.READ_COILS, MB_Registers.MB_Discrete_Output_Coils.MB_RELE.getReg(), MB_Registers.MB_Discrete_Output_Coils.MB_O_COILS.getReg(), Cregs);
			
			//Read Discrete Inputs
			buildModBus(1, Const_Modbus.READ_INPUT_DISCRETES, MB_Registers.MB_Discrete_Input_Contacts.MB_BTN1.getReg(),MB_Registers.MB_Discrete_Input_Contacts.MB_I_REGS.getReg(), Dregs);
			
		}
	}
	
	public void buildModBus(int address, int func, int iReg, int nReg, int[] bReg) {
		String[] args = {String.valueOf(address), String.valueOf(func), 
				String.valueOf(iReg), String.valueOf(nReg)};

		ModBus_Communications.InitModbusComunication(args, sn_Transport, this::UpdateElements, bReg);
		
	}
	
	
	// polling de lectura	

	public void UpdateElements(final ModBusEvent e){

		int addr = Integer.parseInt(e.get_Args()[2]);
		int nReg = Integer.parseInt(e.get_Args()[3]);
					
		switch(Integer.parseInt(e.get_Args()[1])){
		case Const_Modbus.READ_COILS:
			
			for(int i = addr;i<(addr+nReg); i++){
				switch(MB_Discrete_Output_Coils.values()[i]){
				case MB_RELE:							
					lightBulb1.setOn((e.getRegs()[i] == 1));
					break;
							
				case MB_TRIAC:							
					lightBulb2.setOn((e.getRegs()[i] == 1));
					break;
				default:
					break;
				
				}
			}
			break;
						
			
			
			
		case Const_Modbus.READ_INPUT_DISCRETES:	
			
			MB_Discrete_Input_Contacts mbDIC; //= MB_Discrete_Input_Contacts.values()[addr];
			
			for(int i = addr;i<(addr+nReg); i++){
				mbDIC = MB_Discrete_Input_Contacts.values()[i];
				switch(mbDIC){
				case MB_BTN1:
					ledBtn1.setLedOn((e.getRegs()[i] != mbDIC.getDefaultValue()));
					break;
								
				case MB_BTN2:
					ledBtn2.setLedOn((e.getRegs()[i] != mbDIC.getDefaultValue()));
					break;
							
				case MB_OPT:
					ledBtnOpt.setLedOn((e.getRegs()[i] != mbDIC.getDefaultValue()));
					break;
				
					
				case MB_PIR:
					// lee el DRegs que le manda el arduino para encender el nuevo led
					ledPir.setLedOn((e.getRegs()[i] != mbDIC.getDefaultValue()));
					break;

				
				default:
					break;
				}
			}
			break;
		}				
	}
	
	@Override
	public void setActualize(boolean st) {
		stActualize = st;		
	}

	@Override
	public boolean getActualize() {
		
		return stActualize;
	}

	@Override
	public void vlog(String message) {
		// TODO Auto-generated method stub
		
	}
}
