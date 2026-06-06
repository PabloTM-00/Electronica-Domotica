/*
 * Gest_Modbus.cpp
 *
 *  Created on: 16/05/2014
 *      Author: jctejero
 *
 *  Modified on: 16/03/2025
 */

/****************************************************************************/
/***        Include files                                                 ***/
/****************************************************************************/
#include	<Arduino.h>
#include	<EEPROM.h>
#include	"Gest_Modbus.h"
#include 	"utils_domoBoard.h"
#include    "config_practicas.h"
#include	"debuglog.h"

/****************************************************************************/
/***        Variables Locales                                             ***/
/****************************************************************************/
/* First step MBS: create an instance */
ModbusSlave mbs;

/****************************************************************************/
/***                 Functions                                            ***/
/****************************************************************************/
void Init_RTU_Modbus()
{
	/* configure modbus communication
	 * 115200 bps, 8E1, two-device network */
	/* Second step MBS: configure */
	/* the Modbus slave configuration parameters */
	const unsigned char 	SLAVE 	= ADDR_SLAVE;		//Address SLAVE
	const long 				BAUD 	= SERIAL_BPS;
	const char 				PARITY 	= SERIAL_PARITY;
	const char 				TXENPIN = 0; //EN_485;

	//Inicialmente configuramos 485 para recibir
	/*
	if(TXENPIN != 0)
		digitalWrite(EN_485, LOW);
	else digitalWrite(EN_485, HIGH);
	*/

	//Para la conexión 485/ModBus usamos
	Serial485 = &Serial;

	//We configure the ModBus Register Banks
	mbs.set_BankRegisters(Cregs, MB_O_COILS, BANK_COILS);
	mbs.set_BankRegisters(Dregs, MB_I_CONTATCS, BANK_DISCRETE_INPUTS);
	mbs.set_BankRegisters(Aregs, MB_A_REGS, BANK_HOLDING_REGS);

	// nuevo banco de input registers
	mbs.set_BankRegisters(Iregs, MB_IN_REGS, BANK_INPUT_REGS);

	mbs.configure(SLAVE,BAUD,PARITY,TXENPIN);
}

/*
 *
 */

 void writecoil(){

	for (int addrReg = 0; addrReg < MB_O_COILS; addrReg++) {		
		switch (addrReg) {
		case MB_RELE:
			mbDomoboard.setActuator(mbDomoboard.RELE.actuator, Cregs[MB_RELE] != 0x00);
			break;

		case MB_TRIAC:
			mbDomoboard.setActuator(mbDomoboard.TRIAC.actuator, Cregs[MB_TRIAC] != 0x00);
			break;	

		case   MB_ACTPIR:
			mbDomoboard.PIR_MOV.Sensor->Activo = Cregs[addrReg]!=0x00;
			EEPROM.update(ADDR_ACTPIR, Cregs[addrReg] != 0x00);
			break;
		}
	}

}

/*
 *
 */

 void writeholdingregister(){
	//char msg[50];

	for (int addrReg = 0; addrReg < MB_A_REGS; addrReg++) {
		switch (addrReg) {
		case MB_SELPRACT:
			//Save pratice selected
			EEPROM.update(ADDR_SELPRACT, Aregs[ADDR_SELPRACT]&0xFF);
			
			//Go To Selecction Configuration
			SelectionConfiguration((uint8_t) (Aregs[MB_SELPRACT] & 0xFF));
			break;

		case MB_TMP_PIR:
			//Almacenar Tiempo de activación Sensor PIR (Big Endian)
			EEPROM.update(ADDR_TIEMPO_PIR_1, (Aregs[MB_TMP_PIR]>>8)&0xFF);
			EEPROM.update(ADDR_TIEMPO_PIR_2, (Aregs[MB_TMP_PIR]&0xFF));
			break;
		}
	}
}


void RTU_ModBus()
{
	if(mbs.update()){
		writecoil();

		writeholdingregister();
	}
}

/*
 * void load_config().- load initial configuration for arduino aplication
 */
void load_Config(){
	//Leemos configuración Actual "Selección Práctica"
	Aregs[MB_SELPRACT] = EEPROM.read(ADDR_SELPRACT);	//Read EEPROM

	Aregs[MB_SELPRACT] = (Aregs[MB_SELPRACT] == 0xFF) ? 0x11 : Aregs[MB_SELPRACT];	//Seleccionamos la práctica 1 apartado 1 por defecto

	SelectionConfiguration((uint8_t) (Aregs[MB_SELPRACT] & 0xFF));

	/*
	 * Config PIR
	 */
	//Leer activación del PIR
	Cregs[MB_ACTPIR] = EEPROM.read(ADDR_ACTPIR);

	//DEBUGLOGLN("PIR activado: %d", Cregs[MB_ACTPIR]);

	//Leer Tiempo activación PIR
	Aregs[MB_TMP_PIR] = ((EEPROM.read(ADDR_TIEMPO_PIR_1)&0xFF) << 8) + (EEPROM.read(ADDR_TIEMPO_PIR_2)&0xFF) ;

	//DEBUGLOGLN("Tiempo Activación del PIR: %d Segundos", Aregs[MB_TMP_PIR]);
}
