package Utilidades.modbus;

import CommTransport.CommTransport;
import Utilidades.threadpool.DefaultExecutorSupplier;
import modbus.Const_Modbus;
import modbus.Modbus;
import practicas_eldom.config.MB_Registers.TSwitchState;

public class ModBus_Communications {
	
	public static void writeCoil(int SlaveAddress, int ModBusRegister, TSwitchState coilState, CommTransport sn_Transport) {
		
		String[] args = {Integer.toString(SlaveAddress), String.valueOf(Const_Modbus.WRITE_COIL), 
				Integer.toString(ModBusRegister), 
				TSwitchState.ToNumberString(coilState)};
		
		//Iniciamos Comunicación
		InitModbusComunication(args, sn_Transport);	
	}
	
	public static void InitModbusComunication(String[] args, CommTransport sCon) {
		
		//Iniciamos Comunicación
		
		DefaultExecutorSupplier.getInstance().forBackgroundTasks().execute(new Runnable() {

			@Override
			public void run() {
				Modbus.InitComunication(args, sCon);
			}
			
		});	
	}
}
