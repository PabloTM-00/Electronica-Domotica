/*
 * ModbusDomoboard.cpp
 *
 *  Created on: 11 mar. 2020
 *      Author: jctejero
 *
 *  Modified on: 17/03/2025
 */

#include "ModbusDomoboard.h"
#include "debuglog.h"

/****************************************************************************/
/***        Variables Locales                                             ***/
/****************************************************************************/

/***************************************************
 * Definición Bancos de registros usados en ModBus *
 ***************************************************/
uint16_t	Cregs[MB_O_COILS];		//Registros para "Dicrete Output Coils"
uint16_t	Dregs[MB_I_CONTATCS];	//Registros para "Dicrete Input Contacts"
uint16_t	Aregs[MB_A_REGS];		//Registros para "Analog Output Holding Registers"

uint16_t 	Iregs[MB_IN_REGS];

ModbusDomoboard mbDomoboard;

ModbusDomoboard::ModbusDomoboard():DomoBoard() {
	//*****  Initialize ModBus Sensors  ****

	//Initialize BOTON1 for ModBus
	BOTON1.Sensor = &(DomoBoard::BOTON1);
	BOTON1.mbReg = &Dregs[MB_BOTON1];
	*(BOTON1.mbReg) = BOTON1.Sensor->valor;		//Actualizamos el registro ModBus con el estado del sensor

	listmbSensors.push(&BOTON1);

	//Initialize BOTON2 for ModBus
	BOTON2.Sensor = &(DomoBoard::BOTON2);
	BOTON2.mbReg = &Dregs[MB_BOTON2];
	*(BOTON2.mbReg) = BOTON2.Sensor->valor;		//Actualizamos el registro ModBus con el estado del sensor

	listmbSensors.push(&BOTON2);


	//Initialize Pulsador Optocoplado for ModBus
	BTN_OPT.Sensor = &(DomoBoard::BTN_OPT);
	BTN_OPT.mbReg = &Dregs[MB_BTNOPT];
	*(BTN_OPT.mbReg) = BTN_OPT.Sensor->valor;	//Actualizamos el registro ModBus con el estado del sensor

	listmbSensors.push(&BTN_OPT);

	//Inicializamos el registros ModBus Sensor PIR
	PIR_MOV.Sensor 			= &(DomoBoard::PIR_MOV);
	PIR_MOV.Sensor->Activo 	= false;					    //Inicialmente, Configuramos el sensor como no activo
	Cregs[MB_ACTPIR] 		= 0x00; 					//Actualizamos el registro ModBus que monitoriza el PIR
	PIR_MOV.mbReg 			= &Dregs[MB_PIRMOV];
	Dregs[MB_PIRMOV] 		= PIR_MOV.Sensor->valor;	//Actualizamos el registro ModBus con el estado del sensor
	Aregs[MB_TMP_PIR] 		= 0x03;						//Configuración inicial 3 Segundos activo

	listmbSensors.push(&PIR_MOV);

	// conectar el sensor fisico con el registro modbus respectivo
	// Iregs[MB_POT ]= POT . valor
	POT1.Sensor = &(DomoBoard::POT1);
	POT1.mbReg = &Iregs[MB_POT1];
	// actualizar el registro modbus con el estado del sensor
	*(POT1.mbReg) = POT1.Sensor->valor;
	listmbSensors.push(&POT1);

	POT2.Sensor = &(DomoBoard::POT2);
	POT2.mbReg = &Iregs[MB_POT2];
	*(POT2.mbReg) = POT2.Sensor->valor;
	listmbSensors.push(&POT2);
	

	RELE.actuator 	= &(DomoBoard::RELE);
	RELE.mbReg 		= &Cregs[MB_RELE];

	TRIAC.actuator 	= &(DomoBoard::TRIAC);
	TRIAC.mbReg 	= &Cregs[MB_TRIAC];
}

void ModbusDomoboard::leerAllSensor(void){
	for(uint8_t i = 0; i < listmbSensors.count(); i++){
		leerSensor(listmbSensors.peek(i));
	}
}

void ModbusDomoboard::leerSensor(TpmbSensor Sensor){

	DomoBoard::leerSensor(Sensor->Sensor);

	//compueba si el valor leído por el sensor difiere del valor almacenado en el registro correspondiente
	//del banco de registros
	if((int16_t)(*(Sensor->mbReg)) != Sensor->Sensor->valor){
		//Estado Sensor ha cambiado
		//Se actualiza el registro correspondiente con el nuevo valor leído en el sensor.
		*(Sensor->mbReg) = Sensor->Sensor->valor;
		//Se inícia el evento asociado a la actualización del banco de registro correpondiente
		if(Sensor->mbSensorEvent != NULL){
			Sensor->mbSensorEvent(Sensor);
		}
	}else{
		if((Sensor->asyncWait != NULL) && (Sensor->mbSensorEvent != NULL)){   //Para gestión de acciones temporizadas
			Sensor->mbSensorEvent(Sensor);
		}
	}
}

void	ModbusDomoboard::Clear_SensorsConfiguration(){
	DomoBoard::Clear_SensorsConfiguration();

	for(uint8_t i = 0; i < listmbSensors.count(); i++){
		listmbSensors.peek(i)->mbActuators.clear();
		listmbSensors.peek(i)->mbSensorEvent = NULL;
		listmbSensors.peek(i)->asyncWait = NULL;
	}	

}


void  ModbusDomoboard::setmbActuator(TmbActuator *Actuator, TStateDigitalDev val){
	bool newVal = (bool)val;

	if(val == TOGGLE){		
		newVal = (*Actuator->mbReg) > 0 ? false : true;
	}

	if(*(Actuator->mbReg) != newVal){
		*(Actuator->mbReg) = newVal;

		setActuator(Actuator->actuator, *(Actuator->mbReg));

	}
}

void ModbusDomoboard::manager_mbActuators(TmbActuators *Actuators, TStateDigitalDev val){
	for(int n = 0; n < Actuators->count(); n++)
		setmbActuator(Actuators->peek(n), val);

}

void leeSensoresmb(void){
	mbDomoboard.leerAllSensor();
}
