package Utilidades.modbus;

import CommTransport.CommTransport;
import Utilidades.threadpool.DefaultExecutorSupplier;
import modbus.Const_Modbus;
import modbus.Modbus;
import practicas_eldom.config.MB_Registers.TSwitchState;

public class ModBus_Communications {
	
	public static void writeCoil(int SlaveAddress, int ModBusRegister, TSwitchState coilState, CommTransport sn_Transport) {
		String[] args = {Integer.toString(SlaveAddress), String.valueOf(Const_Modbus.WRITE_COIL), 
				Integer.toString(ModBusRegister), TSwitchState.ToNumberString(coilState)};
		InitModbusComunication(args, sn_Transport);	
	}

	public static void readCoils(int SlaveAddress, int startReg, int numRegs, CommTransport sn_Transport) {
		String[] args = {Integer.toString(SlaveAddress), String.valueOf(Const_Modbus.READ_COILS), 
				Integer.toString(startReg), Integer.toString(numRegs)};
		InitModbusComunication(args, sn_Transport);
	}

	public static void readInputDiscrete(int SlaveAddress, int startReg, int numRegs, CommTransport sn_Transport, int[] resultados, Runnable alTerminar) {
		String[] args = {Integer.toString(SlaveAddress), "2", Integer.toString(startReg), Integer.toString(numRegs)};
		
		DefaultExecutorSupplier.getInstance().forBackgroundTasks().execute(new Runnable() {
			@Override
			public void run() {
				try {
					Modbus.InitComunication(args, sn_Transport, resultados);
					
					if(alTerminar != null){
						javax.swing.SwingUtilities.invokeLater(alTerminar);
					}
				} catch (Exception e) {}
			}
		});	
	}
	
	public static void InitModbusComunication(String[] args, CommTransport sCon) {
		DefaultExecutorSupplier.getInstance().forBackgroundTasks().execute(new Runnable() {
			@Override
			public void run() {
				try {
					Modbus.InitComunication(args, sCon);
				} catch (Exception e) {}
			}
		});	
	}
}