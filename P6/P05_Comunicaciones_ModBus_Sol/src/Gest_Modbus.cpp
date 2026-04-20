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
#include	"Gest_Modbus.h"
#include 	"utils_domoBoard.h"
#include	"debuglog.h"

/****************************************************************************/
/***        Variables Locales                                             ***/
/****************************************************************************/
/* First step MBS: create an instance */
ModbusSlave mbs;

/***************************************************
 * Definición Bancos de registros usados en ModBus *
 ***************************************************/
uint16_t	Cregs[MB_O_COILS];		//Registros para "Dicrete Output Coils"

uint16_t Cregs[MB_O_COILS];		
uint16_t Iregs[MB_I_DISCRETE]; 

/****************************************************************************/
/***                 Definición de funciones                              ***/
/****************************************************************************/
void writecoil(unsigned int addrReg);

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
	Iregs[MB_BTN1] = domoboard.BOTON1.valor;
	Iregs[MB_BTN2] = domoboard.BOTON2.valor;
	Iregs[MB_BTN_OPT] = domoboard.BTN_OPT.valor;

	mbs.set_BankCoils(Cregs, MB_O_COILS);
    
	mbs.set_BankDiscreteInputs(Iregs, MB_I_DISCRETE); 

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
		}
	}

}


void RTU_ModBus()
{
	if(mbs.update()){
		writecoil();
	}
}

