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
import practicas_eldom.config.MB_Registers.MB_Analog_Input_Register;
import practicas_eldom.config.MB_Registers.MB_Analog_Output_Holding;
import practicas_eldom.config.MB_Registers.MB_Discrete_Input_Contacts;
import practicas_eldom.config.MB_Registers.MB_Discrete_Output_Coils;
import practicas_eldom.config.MB_Registers.TSwitchState;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.border.BevelBorder;
import javax.swing.SwingConstants;
import eu.hansolo.steelseries.extras.Led;
import javax.swing.border.LineBorder;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import eu.hansolo.steelseries.gauges.Radial2Top;
import eu.hansolo.steelseries.tools.GaugeType;
import eu.hansolo.steelseries.tools.LcdColor;
import eu.hansolo.steelseries.tools.LedColor;
import eu.hansolo.steelseries.tools.PointerType;

public class DomoBoardGui extends JPanel implements Visualizer {

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
	private         JCheckBox 			cbActPIR;
	private			Led 				ledPIR;
	
	private			Radial2Top 			r2T_Pot1;
	private			Radial2Top 			r2T_Pot2;
	
	private			Radial2Top 			gaugeFotor;
	private         JCheckBox 			cbActNRC;
	private         JTextField 			txtHighLevel;
	private         JTextField 			txtLowLevel;
	private			JLabel 				lblTempValue;
	private			Radial2Top 			gaugeFotot;
	private         JCheckBox 			cbActDetec;
	private         JTextField 			txtNivelDetec;
	
	private  		int 				Cregs[];
	private  		int 				Dregs[];
	private         int 				Aregs[];
	private         int 				Iregs[];
	
	private			boolean     		stActualize = true;
	private 		JTextField 			tiempoPIR;
	
	public DomoBoardGui(String category, int address, CommTransport sn_Transport) {
		super();
		this.category 		= category;
		this.address		= address;
		this.sn_Transport 	= sn_Transport;
		this.setLayout(null);
		
		Cregs = new int [MB_Registers.MB_Discrete_Output_Coils.MB_O_COILS.getReg()];
		Dregs = new int [MB_Registers.MB_Discrete_Input_Contacts.MB_I_REGS.getReg()];
		Aregs = new int [MB_Registers.MB_Analog_Output_Holding.MB_AREGS.getReg()];
		
		Iregs = new int [5]; 
		
		ma_lightBulb = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ONOFF_Bulb(((LightBulb)e.getComponent()));
			}
		};
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(0, 0, 255), new Color(0, 255, 0), Color.BLUE, Color.MAGENTA));
		panel.setBounds(10, 10, 172, 100);
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
		label_1.setBounds(11, 43, 36, 14);
		panel.add(label_1);
		
		JLabel label_2 = new JLabel("BTN 2");
		label_2.setForeground(Color.BLUE);
		label_2.setBounds(67, 43, 36, 14);
		panel.add(label_2);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 255), 2));
		panel_1.setBounds(190, 10, 195, 100);
		add(panel_1);
		panel_1.setLayout(null);
		
		lightBulb1 = new LightBulb();
		lightBulb1.setOn(false);
		lightBulb1.setGlowColor(Color.RED);
		lightBulb1.setBounds(10, 5, 65, 65);
		panel_1.add(lightBulb1);
		lightBulb1.addMouseListener(ma_lightBulb);
		
		JLabel lblNewLabel = new JLabel("RELÉ");
		lblNewLabel.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		lblNewLabel.setBounds(20, 72, 60, 24);
		panel_1.add(lblNewLabel);
		
		lightBulb2 = new LightBulb();
		lightBulb2.setOn(false);
		lightBulb2.setGlowColor(Color.YELLOW);
		lightBulb2.setBounds(111, 5, 65, 65);
		panel_1.add(lightBulb2);
		lightBulb2.addMouseListener(ma_lightBulb);
		
		JLabel lblRel = new JLabel("TRIAC");
		lblRel.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		lblRel.setBounds(119, 72, 60, 24);
		panel_1.add(lblRel);
		
		JPanel panelPhotoR = new JPanel();
		panelPhotoR.setBorder(new LineBorder(Color.BLUE, 2));
		panelPhotoR.setBounds(395, 10, 245, 100);
		panelPhotoR.setLayout(null);
		add(panelPhotoR);
	
		JLabel lblPhotoR = new JLabel("LDR (Farola)");
		lblPhotoR.setFont(new Font("Times New Roman", Font.BOLD, 12));
		lblPhotoR.setBounds(5, 2, 100, 18);
		panelPhotoR.add(lblPhotoR);
	
		gaugeFotor = new Radial2Top();
		gaugeFotor.setMaxValue(100);
		gaugeFotor.setBounds(5, 20, 110, 75);
		panelPhotoR.add(gaugeFotor);
	
		cbActNRC = new JCheckBox("Control Automático LDR");
		cbActNRC.setBounds(120, 15, 110, 20);
		cbActNRC.setSelected(true);
		panelPhotoR.add(cbActNRC);
	
		JLabel lblHigh = new JLabel("Luz Alta:");
		lblHigh.setBounds(120, 45, 60, 14);
		panelPhotoR.add(lblHigh);
	
		txtHighLevel = new JTextField("60");
		txtHighLevel.setBounds(180, 42, 50, 20);
		panelPhotoR.add(txtHighLevel);
	
		JLabel lblLow = new JLabel("Luz Baja:");
		lblLow.setBounds(120, 70, 60, 14);
		panelPhotoR.add(lblLow);
	
		txtLowLevel = new JTextField("40");
		txtLowLevel.setBounds(180, 67, 50, 20);
		panelPhotoR.add(txtLowLevel);
	
		JPanel panel_1_1 = new JPanel();
		panel_1_1.setLayout(null);
		panel_1_1.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(0, 0, 255), new Color(0, 255, 0), Color.BLUE, Color.MAGENTA));
		panel_1_1.setBounds(10, 120, 220, 100);
		add(panel_1_1);
		
		ledPIR = new Led();
		ledPIR.setBounds(0, 2, 95, 95);
		panel_1_1.add(ledPIR);
		
		JLabel label_4 = new JLabel("PIR");
		label_4.setHorizontalAlignment(SwingConstants.CENTER);
		label_4.setForeground(Color.RED);
		label_4.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		label_4.setBounds(0, 73, 220, 24);
		panel_1_1.add(label_4);
		
		cbActPIR = new JCheckBox("Activar PIR");
		cbActPIR.setSelected(true);
		cbActPIR.setBounds(101, 12, 110, 23);
		panel_1_1.add(cbActPIR);
		
		tiempoPIR = new JTextField();
		tiempoPIR.setColumns(10);
		tiempoPIR.setBounds(105, 42, 37, 20);
		panel_1_1.add(tiempoPIR);
		
		JLabel label_5 = new JLabel("Tiempo (s)");
		label_5.setBounds(152, 45, 80, 14);
		panel_1_1.add(label_5);
	
		JPanel panelTemp = new JPanel();
		panelTemp.setBorder(new LineBorder(Color.BLUE, 2));
		panelTemp.setBounds(240, 120, 145, 100);
		panelTemp.setLayout(null);
		add(panelTemp);
	
		JLabel lblTempTitle = new JLabel("Temperatura");
		lblTempTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTempTitle.setFont(new Font("Times New Roman", Font.BOLD, 14));
		lblTempTitle.setBounds(0, 10, 145, 20);
		panelTemp.add(lblTempTitle);
	
		lblTempValue = new JLabel(" _ °C");
		lblTempValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblTempValue.setFont(new Font("Arial", Font.BOLD, 30));
		lblTempValue.setForeground(new Color(0, 100, 0));
		lblTempValue.setBounds(0, 40, 145, 40);
		panelTemp.add(lblTempValue);
	
		JPanel panelPhotoT = new JPanel();
		panelPhotoT.setBorder(new LineBorder(Color.BLUE, 2));
		panelPhotoT.setBounds(395, 120, 245, 100);
		panelPhotoT.setLayout(null);
		add(panelPhotoT);
	
		JLabel lblPhotoT = new JLabel("Fototransistor");
		lblPhotoT.setFont(new Font("Times New Roman", Font.BOLD, 12));
		lblPhotoT.setBounds(5, 2, 100, 18);
		panelPhotoT.add(lblPhotoT);
	
		gaugeFotot = new Radial2Top();
		gaugeFotot.setMaxValue(100);
		gaugeFotot.setBounds(5, 20, 110, 75);
		panelPhotoT.add(gaugeFotot);
	
		cbActDetec = new JCheckBox("Bloqueo FTT");
		cbActDetec.setSelected(true);
		cbActDetec.setBounds(120, 25, 118, 20);
		panelPhotoT.add(cbActDetec);
	
		JLabel lblNivel = new JLabel("Umbral:");
		lblNivel.setBounds(120, 55, 60, 14);
		panelPhotoT.add(lblNivel);
	
		txtNivelDetec = new JTextField("10");
		txtNivelDetec.setBounds(180, 52, 50, 20);
		panelPhotoT.add(txtNivelDetec);
		
		r2T_Pot1 = new Radial2Top();
		r2T_Pot1.setTitle("Pot. 1");
		r2T_Pot1.setMaxValue(100); 
		r2T_Pot1.setBounds(10, 230, 190, 190);
		add(r2T_Pot1);
		
		r2T_Pot2 = new Radial2Top();
		r2T_Pot2.setTitle("Pot. 2");
		r2T_Pot2.setMaxValue(100); 
		r2T_Pot2.setBounds(210, 230, 190, 190);
		add(r2T_Pot2);
		
		if(sn_Transport.isConnected())
			leerConfiguracionInicial();
	}
	
	private void leerConfiguracionInicial(){
		ModBus_Communications.readMultipleRegisters(address, MB_Registers.MB_Analog_Output_Holding.MB_TMP_PIR.getReg(), 1, sn_Transport, this::UpdateElements , Aregs);
		buildModBus(address, Const_Modbus.READ_COILS, MB_Registers.MB_Discrete_Output_Coils.MB_ACTPIR.getReg(), 1, Cregs);
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
		return "Control de Iluminación y Sensores - Nodo " + address; 
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
		if(stActualize) {	
			buildModBus(address, Const_Modbus.READ_COILS, MB_Registers.MB_Discrete_Output_Coils.MB_RELE.getReg(), MB_Registers.MB_Discrete_Output_Coils.MB_O_COILS.getReg(), Cregs);
			buildModBus(address, Const_Modbus.READ_INPUT_DISCRETES, MB_Registers.MB_Discrete_Input_Contacts.MB_BTN1.getReg(),MB_Registers.MB_Discrete_Input_Contacts.MB_I_REGS.getReg(), Dregs);
			
			buildModBus(address, 4, 0, 5, Iregs);
		}
	}
	
	public void buildModBus(int address, int func, int iReg, int nReg, int[] bReg) {
		String[] args = {String.valueOf(address), String.valueOf(func), String.valueOf(iReg), String.valueOf(nReg)};
		ModBus_Communications.InitModbusComunication(args, sn_Transport, this::UpdateElements, bReg);
	}
	
	public void UpdateElements(final ModBusEvent e){
		int addr = Integer.parseInt(e.get_Args()[2]);
		int nReg = Integer.parseInt(e.get_Args()[3]);
					
		switch(Integer.parseInt(e.get_Args()[1])){
		case Const_Modbus.READ_MULTIPLE_REGISTERS:
			for(int i = addr;i<(addr+nReg); i++){
				switch(MB_Analog_Output_Holding.values()[i]){
				case MB_TMP_PIR:
					tiempoPIR.setText(Integer.toString(e.getRegs()[i-addr]));
					break;
				default: break;
				}
			}
			break;
		
		case Const_Modbus.READ_COILS:	
			for(int i = addr;i<(addr+nReg); i++){
				switch(MB_Discrete_Output_Coils.values()[i]){
				case MB_RELE:							
					lightBulb1.setOn((e.getRegs()[i - addr] == 1));
					break;
							
				case MB_TRIAC:							
					lightBulb2.setOn((e.getRegs()[i - addr] == 1));
					break;
					
				case MB_ACTPIR:
					cbActPIR.setSelected((e.getRegs()[i - addr] == 1));
					break;
				default: 
					break;
				}
			}
			break;
						
		case Const_Modbus.READ_INPUT_DISCRETES:		
			MB_Discrete_Input_Contacts mbDIC; 
			for(int i = addr;i<(addr+nReg); i++){
				mbDIC = MB_Discrete_Input_Contacts.values()[i];
				switch(mbDIC){
				case MB_BTN1:
					ledBtn1.setLedOn((e.getRegs()[i - addr] != mbDIC.getDefaultValue()));
					break;
								
				case MB_BTN2:
					ledBtn2.setLedOn((e.getRegs()[i - addr] != mbDIC.getDefaultValue()));
					break;
							
				case MB_OPT:
					ledBtnOpt.setLedOn((e.getRegs()[i - addr] != mbDIC.getDefaultValue()));
					break;
					
				case MB_PIR:
					ledPIR.setLedOn((e.getRegs()[i - addr] == 1));
					break;
				default: 
					break;
				}
			}
			break;
			
		case Const_Modbus.READ_INPUT_REGISTERS: 
			if (e.getRegs() != null && e.getRegs().length >= 5) {
			    
			    r2T_Pot1.setValue(e.getRegs()[0]);
			    r2T_Pot2.setValue(e.getRegs()[1]);
			    
			    int ldr = e.getRegs()[2];
			    double temp = e.getRegs()[3] / 10.0;
			    int ftt = e.getRegs()[4];
	
			    if (gaugeFotor != null) gaugeFotor.setValue(ldr);
			    if (gaugeFotot != null) gaugeFotot.setValue(ftt);
	
			    if (lblTempValue != null) lblTempValue.setText(String.format("%.1f ºC", temp));
	
			    System.out.println(String.format("Monitorizacion -> Temp: %.1f C | LDR: %d | FTT: %d", temp, ldr, ftt));
	
			    if (cbActNRC != null && cbActNRC.isSelected()) {
			    	try {
				        int low = Integer.parseInt(txtLowLevel.getText());
				        int high = Integer.parseInt(txtHighLevel.getText());
				        
				        if (ldr < low && !lightBulb1.isOn()) {
				        	buildModBus(address, Const_Modbus.WRITE_COIL, MB_Registers.MB_Discrete_Output_Coils.MB_RELE.getReg(), 1, Cregs);
				        } else if (ldr > high && lightBulb1.isOn()) {
				        	buildModBus(address, Const_Modbus.WRITE_COIL, MB_Registers.MB_Discrete_Output_Coils.MB_RELE.getReg(), 0, Cregs);
				        }
			    	} catch (Exception ex) {}
			    }
			    
			    if (cbActDetec != null && cbActDetec.isSelected()) {
			    	try {
			    		int detec = Integer.parseInt(txtNivelDetec.getText());
			    		
			    		if (ftt < detec && !lightBulb2.isOn()) {
			    			buildModBus(address, Const_Modbus.WRITE_COIL, MB_Registers.MB_Discrete_Output_Coils.MB_TRIAC.getReg(), 1, Cregs);
			    		}
			    	} catch (Exception ex) {}
			    }
			}
			break;
		}				
	}
	
	@Override
	public void setActualize(boolean st) { stActualize = st; }
	
	@Override
	public boolean getActualize() { return stActualize; }
	
	@Override
	public void vlog(String message) {}

}