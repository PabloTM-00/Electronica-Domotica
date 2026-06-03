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
uint16_t	Iregs[MB_I_REGS];		//Registros para "Analog Input Registers"

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

	//Initialize ModBus Analog sensors
	POT1.Sensor = &(DomoBoard::POT1);
	POT1.mbReg = &Iregs[MB_POT1];

	listmbSensors.push(&POT1);

	POT2.Sensor = &(DomoBoard::POT2);
	POT2.mbReg = &Iregs[MB_POT2];
	
	listmbSensors.push(&POT2);

	FOTOR.Sensor = &(DomoBoard::FOTOR);
	FOTOR.mbReg = &Iregs[MB_FOTOR];
	*(FOTOR.mbReg) = FOTOR.Sensor->valor;
	listmbSensors.push(&FOTOR);

	TEMP.Sensor = &(DomoBoard::TEMP);
	TEMP.mbReg = &Iregs[MB_TEMP];
	*(TEMP.mbReg) = TEMP.Sensor->valor;
	listmbSensors.push(&TEMP);

	FOTOT.Sensor = &(DomoBoard::FOTOT);
	FOTOT.mbReg = &Iregs[MB_FOTOT];
	*(FOTOT.mbReg) = FOTOT.Sensor->valor;
	listmbSensors.push(&FOTOT);

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
		// cambia e estado del sensor
		// se actualiza el registro correspondiente con el nuevo valor del sensor
		*(Sensor->mbReg) = Sensor->Sensor->valor;
		if(Sensor->mbSensorEvent != NULL){
			Sensor->mbSensorEvent(Sensor);
		}
	}else{
		if((Sensor->asyncWait != NULL) && (Sensor->mbSensorEvent != NULL)){  
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

	Iregs[MB_POT1] = map(Iregs[MB_POT1], 0, 1023, 0, 100);
	Iregs[MB_POT2] = map(Iregs[MB_POT2], 0, 1023, 0, 100);

	Iregs[MB_FOTOR] = map(Iregs[MB_FOTOR], 0, 1023, 0, 100);
	Iregs[MB_FOTOT] = map(Iregs[MB_FOTOT], 0, 1023, 0, 100);

	float tension = (Iregs[MB_TEMP] / 1023.0) * 5.0;
	float celsius = (tension - 0.5) * 100.0;
	
	Iregs[MB_TEMP] = (uint16_t)(celsius * 10.0);
}
