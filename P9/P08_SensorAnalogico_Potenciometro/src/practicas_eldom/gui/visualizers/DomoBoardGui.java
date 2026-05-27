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


public class DomoBoardGui extends JPanel implements Visualizer {

	private       MouseAdapter   ma_lightBulb;
	private final String         category;
	private final boolean        isCategory  = true;
	private final CommTransport  sn_Transport;
	private final int            address;

	// ── Outputs digitales ──────────────────────────────────────────────────────
	private LightBulb  lightBulb1;
	private LightBulb  lightBulb2;

	// ── Indicadores pulsadores ─────────────────────────────────────────────────
	private Led ledBtn1;
	private Led ledBtn2;
	private Led ledBtnOpt;

	// ── PIR ───────────────────────────────────────────────────────────────────
	private JCheckBox  cbActPIR;
	private Led        ledPIR;
	private JTextField tiempoPIR;

	// ── Photo Resistencia ─────────────────────────────────────────────────────
	private Radial2Top gaugeFotor;
	private JCheckBox  cbActNRC;
	private JTextField txtHighLevel;
	private JTextField txtLowLevel;

	// ── Temperatura (display digital) ─────────────────────────────────────────
	private JLabel lblTempValue;

	// ── Photo Transistor ──────────────────────────────────────────────────────
	private Radial2Top gaugeFotot;
	private JCheckBox  cbActDetec;
	private JTextField txtNivelDetec;

	// ── Potenciómetros ────────────────────────────────────────────────────────
	private Radial2Top gaugePot1;
	private Radial2Top gaugePot2;

	// ── Bancos de registros Modbus ────────────────────────────────────────────
	private int Iregs[];
	private int Cregs[];
	private int Dregs[];
	private int Aregs[];

	private boolean stActualize = true;


	// ══════════════════════════════════════════════════════════════════════════
	//  Constructor
	// ══════════════════════════════════════════════════════════════════════════
	public DomoBoardGui(String category, int address, CommTransport sn_Transport) {

		super();
		this.category     = category;
		this.address      = address;
		this.sn_Transport = sn_Transport;
		this.setLayout(null);

		Cregs = new int[MB_Registers.MB_Discrete_Output_Coils.MB_O_COILS.getReg()];
		Dregs = new int[MB_Registers.MB_Discrete_Input_Contacts.MB_I_REGS.getReg()];
		Aregs = new int[MB_Registers.MB_Analog_Output_Holding.MB_AREGS.getReg()];
		Iregs = new int[5];

		ma_lightBulb = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ONOFF_Bulb(((LightBulb) e.getComponent()));
			}
		};

		// ── FILA 1 ─────────────────────────────────────────────────────────────

		// ── Panel Estado Pulsadores ────────────────────────────────────────────
		JPanel panelPulsadores = new JPanel();
		panelPulsadores.setLayout(null);
		panelPulsadores.setBorder(new BevelBorder(BevelBorder.LOWERED,
				new Color(0, 0, 255), new Color(0, 255, 0), Color.BLUE, Color.MAGENTA));
		panelPulsadores.setBounds(10, 22, 172, 100);
		add(panelPulsadores);

		ledBtn1 = new Led();
		ledBtn1.setBounds(11, 11, 36, 36);
		panelPulsadores.add(ledBtn1);

		ledBtn2 = new Led();
		ledBtn2.setBounds(67, 11, 36, 36);
		panelPulsadores.add(ledBtn2);

		ledBtnOpt = new Led();
		ledBtnOpt.setBounds(120, 11, 36, 36);
		panelPulsadores.add(ledBtnOpt);

		JLabel lblBtn1 = new JLabel("BTN 1");
		lblBtn1.setForeground(Color.BLUE);
		lblBtn1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblBtn1.setBounds(11, 43, 36, 14);
		panelPulsadores.add(lblBtn1);

		JLabel lblBtn2 = new JLabel("BTN 2");
		lblBtn2.setForeground(Color.BLUE);
		lblBtn2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblBtn2.setBounds(67, 43, 36, 14);
		panelPulsadores.add(lblBtn2);

		JLabel lblBtnOpt = new JLabel("BTN_OPT");
		lblBtnOpt.setForeground(Color.BLUE);
		lblBtnOpt.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblBtnOpt.setBounds(110, 43, 56, 14);
		panelPulsadores.add(lblBtnOpt);

		JLabel lblEstado = new JLabel("Estado Pulsadores");
		lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
		lblEstado.setForeground(Color.RED);
		lblEstado.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		lblEstado.setBounds(0, 75, 166, 14);
		panelPulsadores.add(lblEstado);

		// ── Panel RELÉ + TRIAC ─────────────────────────────────────────────────
		JPanel panelBulbs = new JPanel();
		panelBulbs.setBorder(new LineBorder(new Color(0, 0, 255), 2));
		panelBulbs.setBounds(192, 11, 195, 123);
		add(panelBulbs);
		panelBulbs.setLayout(null);

		lightBulb1 = new LightBulb();
		lightBulb1.setOn(true);
		lightBulb1.setGlowColor(Color.RED);
		lightBulb1.setBounds(10, 11, 78, 78);
		panelBulbs.add(lightBulb1);
		lightBulb1.addMouseListener(ma_lightBulb);

		JLabel lblRele = new JLabel("RELÉ");
		lblRele.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		lblRele.setBounds(20, 92, 76, 24);
		panelBulbs.add(lblRele);

		lightBulb2 = new LightBulb();
		lightBulb2.setOn(true);
		lightBulb2.setGlowColor(Color.YELLOW);
		lightBulb2.setBounds(105, 11, 78, 78);
		panelBulbs.add(lightBulb2);
		lightBulb2.addMouseListener(ma_lightBulb);

		JLabel lblTriac = new JLabel("TRIAC");
		lblTriac.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		lblTriac.setBounds(112, 92, 70, 24);
		panelBulbs.add(lblTriac);

		// ── Panel Photo Resistencia ────────────────────────────────────────────
		JPanel panelPhotoR = new JPanel();
		panelPhotoR.setBorder(new LineBorder(Color.BLUE, 2));
		panelPhotoR.setBounds(398, 11, 252, 123);
		panelPhotoR.setLayout(null);
		add(panelPhotoR);

		JLabel lblPhotoR = new JLabel("Photo Resistencia");
		lblPhotoR.setFont(new Font("Times New Roman", Font.BOLD, 14));
		lblPhotoR.setForeground(Color.BLUE);
		lblPhotoR.setBounds(5, 2, 160, 18);
		panelPhotoR.add(lblPhotoR);

		gaugeFotor = new Radial2Top();
		gaugeFotor.setTitle("LDR");
		gaugeFotor.setMaxValue(1023);
		gaugeFotor.setBounds(5, 20, 120, 95);
		panelPhotoR.add(gaugeFotor);

		cbActNRC = new JCheckBox("Activar NRC");
		cbActNRC.setFont(new Font("Tahoma", Font.PLAIN, 11));
		cbActNRC.setBounds(130, 20, 115, 20);
		cbActNRC.setSelected(true);
		panelPhotoR.add(cbActNRC);

		JLabel lblHigh = new JLabel("High Level");
		lblHigh.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblHigh.setBounds(130, 48, 65, 14);
		panelPhotoR.add(lblHigh);

		txtHighLevel = new JTextField("500");
		txtHighLevel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtHighLevel.setBounds(130, 63, 50, 20);
		panelPhotoR.add(txtHighLevel);

		JLabel lblLow = new JLabel("Low Level");
		lblLow.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblLow.setBounds(130, 88, 65, 14);
		panelPhotoR.add(lblLow);

		txtLowLevel = new JTextField("400");
		txtLowLevel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtLowLevel.setBounds(130, 103, 50, 20);
		panelPhotoR.add(txtLowLevel);

		// ── FILA 2 ─────────────────────────────────────────────────────────────

		// ── Panel PIR ─────────────────────────────────────────────────────────
		JPanel panelPIR = new JPanel();
		panelPIR.setLayout(null);
		panelPIR.setBorder(new BevelBorder(BevelBorder.LOWERED,
				new Color(0, 0, 255), new Color(0, 255, 0), Color.BLUE, Color.MAGENTA));
		panelPIR.setBounds(10, 145, 220, 100);
		add(panelPIR);

		ledPIR = new Led();
		ledPIR.setBounds(0, 2, 90, 90);
		panelPIR.add(ledPIR);

		cbActPIR = new JCheckBox("Activar PIR");
		cbActPIR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TSwitchState vSel;
				if (((AbstractButton) arg0.getSource()).getModel().isSelected())
					vSel = TSwitchState.ON;
				else
					vSel = TSwitchState.OFF;
				buildModBus(address, Const_Modbus.WRITE_COIL,
						MB_Registers.MB_Discrete_Output_Coils.MB_ACTPIR.getReg(),
						TSwitchState.ToNumber(vSel), Cregs);
			}
		});
		cbActPIR.setSelected(true);
		cbActPIR.setFont(new Font("Tahoma", Font.PLAIN, 11));
		cbActPIR.setBounds(95, 10, 110, 20);
		panelPIR.add(cbActPIR);

		tiempoPIR = new JTextField();
		tiempoPIR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int newval = Integer.parseInt(tiempoPIR.getText());
					if (newval != 0)
						ModBus_Communications.writeSingleRegister(address,
								MB_Registers.MB_Analog_Output_Holding.MB_TMP_PIR.getReg(),
								newval, sn_Transport);
				} catch (NumberFormatException ex) {
				}
			}
		});
		tiempoPIR.setColumns(4);
		tiempoPIR.setBounds(95, 38, 37, 20);
		panelPIR.add(tiempoPIR);

		JLabel lblTiempo = new JLabel("Tiempo (Segs.)");
		lblTiempo.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblTiempo.setBounds(135, 41, 80, 14);
		panelPIR.add(lblTiempo);

		JLabel lblPIR = new JLabel("PIR");
		lblPIR.setHorizontalAlignment(SwingConstants.CENTER);
		lblPIR.setForeground(Color.RED);
		lblPIR.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		lblPIR.setBounds(0, 73, 220, 24);
		panelPIR.add(lblPIR);

		// ── Panel Temperatura (display digital) ────────────────────────────────
		JPanel panelTemp = new JPanel();
		panelTemp.setBorder(new LineBorder(Color.BLUE, 2));
		panelTemp.setBounds(242, 145, 145, 100);
		panelTemp.setLayout(null);
		add(panelTemp);

		JLabel lblTempTitle = new JLabel("Temperatura");
		lblTempTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTempTitle.setFont(new Font("Times New Roman", Font.BOLD, 14));
		lblTempTitle.setForeground(Color.BLUE);
		lblTempTitle.setBounds(0, 8, 141, 20);
		panelTemp.add(lblTempTitle);

		lblTempValue = new JLabel("-- °C");
		lblTempValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblTempValue.setFont(new Font("Arial", Font.BOLD, 30));
		lblTempValue.setForeground(new Color(0, 100, 0));
		lblTempValue.setBounds(0, 32, 141, 50);
		panelTemp.add(lblTempValue);

		// ── Panel Photo Transistor ─────────────────────────────────────────────
		JPanel panelPhotoT = new JPanel();
		panelPhotoT.setBorder(new LineBorder(Color.BLUE, 2));
		panelPhotoT.setBounds(398, 145, 252, 100);
		panelPhotoT.setLayout(null);
		add(panelPhotoT);

		JLabel lblPhotoT = new JLabel("Photo Transistor");
		lblPhotoT.setFont(new Font("Times New Roman", Font.BOLD, 14));
		lblPhotoT.setForeground(Color.BLUE);
		lblPhotoT.setBounds(5, 2, 160, 18);
		panelPhotoT.add(lblPhotoT);

		gaugeFotot = new Radial2Top();
		gaugeFotot.setTitle("FTT");
		gaugeFotot.setMaxValue(1023);
		gaugeFotot.setBounds(5, 15, 120, 80);
		panelPhotoT.add(gaugeFotot);

		cbActDetec = new JCheckBox("Activar Detec...");
		cbActDetec.setFont(new Font("Tahoma", Font.PLAIN, 11));
		cbActDetec.setSelected(true);
		cbActDetec.setBounds(130, 15, 118, 20);
		panelPhotoT.add(cbActDetec);

		JLabel lblNivel = new JLabel("Nivel Detección");
		lblNivel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNivel.setBounds(130, 45, 115, 14);
		panelPhotoT.add(lblNivel);

		txtNivelDetec = new JTextField("50");
		txtNivelDetec.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtNivelDetec.setBounds(130, 62, 50, 20);
		panelPhotoT.add(txtNivelDetec);

		// ── FILA 3 ─────────────────────────────────────────────────────────────

		// ── Panel Potenciómetros ───────────────────────────────────────────────
		JPanel panelPot = new JPanel();
		panelPot.setBorder(new LineBorder(Color.BLUE, 2));
		panelPot.setBounds(10, 257, 640, 115);
		panelPot.setLayout(null);
		add(panelPot);

		JLabel lblPot = new JLabel("Potenciómetros (P8)");
		lblPot.setFont(new Font("Times New Roman", Font.BOLD, 16));
		lblPot.setBounds(10, 4, 220, 20);
		panelPot.add(lblPot);

		gaugePot1 = new Radial2Top();
		gaugePot1.setTitle("Pot-1");
		gaugePot1.setMaxValue(1023);
		gaugePot1.setBounds(15, 25, 295, 82);
		panelPot.add(gaugePot1);

		gaugePot2 = new Radial2Top();
		gaugePot2.setTitle("Pot-2");
		gaugePot2.setMaxValue(1023);
		gaugePot2.setBounds(330, 25, 295, 82);
		panelPot.add(gaugePot2);

		if (sn_Transport.isConnected())
			leerConfiguracionInicial();
	}


	// ══════════════════════════════════════════════════════════════════════════
	//  Lógica Modbus
	// ══════════════════════════════════════════════════════════════════════════

	private void leerConfiguracionInicial() {
		ModBus_Communications.readMultipleRegisters(address,
				MB_Registers.MB_Analog_Output_Holding.MB_TMP_PIR.getReg(),
				1, sn_Transport, this::UpdateElements, Aregs);

		buildModBus(address, Const_Modbus.READ_COILS,
				MB_Registers.MB_Discrete_Output_Coils.MB_ACTPIR.getReg(), 1, Cregs);
	}

	private void ONOFF_Bulb(LightBulb lightBulb) {
		int vBulb;
		int vReg;

		lightBulb.setOn(!lightBulb.isOn());

		if (lightBulb.isOn())
			vBulb = TSwitchState.ToNumber(TSwitchState.ON);
		else
			vBulb = TSwitchState.ToNumber(TSwitchState.OFF);

		if (lightBulb == lightBulb1)
			vReg = MB_Registers.MB_Discrete_Output_Coils.MB_RELE.getReg();
		else
			vReg = MB_Registers.MB_Discrete_Output_Coils.MB_TRIAC.getReg();

		buildModBus(address, Const_Modbus.WRITE_COIL, vReg, vBulb, Cregs);
	}

	@Override
	public void Actualize() {
		if (stActualize) {
			// Coils de salida
			buildModBus(1, Const_Modbus.READ_COILS,
					MB_Registers.MB_Discrete_Output_Coils.MB_RELE.getReg(),
					MB_Registers.MB_Discrete_Output_Coils.MB_O_COILS.getReg(), Cregs);

			// Entradas discretas
			buildModBus(1, Const_Modbus.READ_INPUT_DISCRETES,
					MB_Registers.MB_Discrete_Input_Contacts.MB_BTN1.getReg(),
					MB_Registers.MB_Discrete_Input_Contacts.MB_I_REGS.getReg(), Dregs);

			// Registros analógicos: pot1, pot2, fotor, tempRaw, fotot  (función 4)
			buildModBus(1, 4, 0, 5, Iregs);
		}
	}

	public void buildModBus(int address, int func, int iReg, int nReg, int[] bReg) {
		String[] args = { String.valueOf(address), String.valueOf(func),
				String.valueOf(iReg), String.valueOf(nReg) };
		ModBus_Communications.InitModbusComunication(args, sn_Transport, this::UpdateElements, bReg);
	}

	public void UpdateElements(final ModBusEvent e) {

		int addr = Integer.parseInt(e.get_Args()[2]);
		int nReg = Integer.parseInt(e.get_Args()[3]);

		switch (Integer.parseInt(e.get_Args()[1])) {

		case Const_Modbus.READ_MULTIPLE_REGISTERS:
			for (int i = addr; i < (addr + nReg); i++) {
				switch (MB_Analog_Output_Holding.values()[i]) {
				case MB_TMP_PIR:
					tiempoPIR.setText(Integer.toString(e.getRegs()[i - addr]));
					break;
				default:
					break;
				}
			}
			break;

		case Const_Modbus.READ_COILS:
			for (int i = addr; i < (addr + nReg); i++) {
				switch (MB_Discrete_Output_Coils.values()[i]) {
				case MB_RELE:
					lightBulb1.setOn((e.getRegs()[i] == 1));
					break;
				case MB_TRIAC:
					lightBulb2.setOn((e.getRegs()[i] == 1));
					break;
				case MB_ACTPIR:
					cbActPIR.setSelected((e.getRegs()[i] == 1));
					break;
				default:
					break;
				}
			}
			break;

		case Const_Modbus.READ_INPUT_DISCRETES:
			MB_Discrete_Input_Contacts mbDIC;
			for (int i = addr; i < (addr + nReg); i++) {
				mbDIC = MB_Discrete_Input_Contacts.values()[i];
				switch (mbDIC) {
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
					ledPIR.setLedOn((e.getRegs()[i] == 1));
					break;
				default:
					break;
				}
			}
			break;

		// ── Función 4: registros analógicos de entrada ─────────────────────────
		case 4:
			if (e.getRegs() != null && e.getRegs().length >= 5) {

				// Potenciómetros
				gaugePot1.setValue(e.getRegs()[0]);
				gaugePot2.setValue(e.getRegs()[1]);

				// Photo Resistencia (LDR) y Photo Transistor
				gaugeFotor.setValue(e.getRegs()[2]);
				gaugeFotot.setValue(e.getRegs()[4]);

				// TMP36 → label digital
				int rawTemp = e.getRegs()[3];
				double voltaje = (rawTemp / 1023.0) * 5.0;
				double celsius = (voltaje - 0.5) * 100.0;
				lblTempValue.setText(String.format("%.1f °C", celsius));
			}
			break;
		}
	}

	@Override
	public String getCategory() { return category; }

	@Override
	public String getTitle() { return "Address : " + address; }

	@Override
	public Component getPanel() { return this; }

	@Override
	public boolean isCategory() { return isCategory; }

	@Override
	public void setActualize(boolean st) { stActualize = st; }

	@Override
	public boolean getActualize() { return stActualize; }

	@Override
	public void vlog(String message) { }
}