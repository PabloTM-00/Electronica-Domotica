/*
 * utils_domoBoard.cpp
 *
 *  Created on: 10/03/2015
 *      Author: jctejero
 * 
 *  modified on: 10/03/2025
 */

/****************************************************************************/
/***        Include files                                                 ***/
/****************************************************************************/
#include "utils_domoBoard.h"
#include "HardwareSerial.h"
#include "debuglog.h"


#include "Gest_Modbus.h" 
extern uint16_t Cregs[]; 
extern void writecoil(); 

void Interruptor(void *Sensor)
{
	if(((ptsSensor)Sensor)->valor_Df == ((ptsSensor)Sensor)->valor){
		Serial.print(((ptsSensor)Sensor)->name);
		Serial.print(F(" : Interruptor --> "));
		if(((ptsSensor)Sensor)->Aux == OFF){
			((ptsSensor)Sensor)->Aux = ON;
			Serial.println(F("ON"));
		}else{
			((ptsSensor)Sensor)->Aux = OFF;
			Serial.println(F("OFF"));
		}
	}
}

void Pulsado_Soltado(void *Sensor){
	ptsSensor sensor = reinterpret_cast<ptsSensor>(Sensor);

	Serial.print(sensor->name);
	if(sensor->valor_Df == sensor->valor){
		Serial.println(F(" --> Soltado"));
		sensor->Aux = OFF;
	}else{
		Serial.println(F(" --> Pulsado"));
		sensor->Aux = ON;
	}
}

void conmutador(void *Sensor)
{
	static bool valor = OFF;

	ptsSensor sensor = reinterpret_cast<ptsSensor>(Sensor);

	if(sensor->valor_Df == sensor->valor){
		Serial.print(F("Conmutador --> "));
		if(valor == OFF){
			valor = ON;
			Serial.println(F("ON"));
		}else{
			valor = OFF;
			Serial.println(F("OFF"));
		}
	}

	sensor->Aux = valor;
}

void conmutador_sal_P03(void *Sensor)
{
	conmutador(Sensor);
	
	//Actualiza Actuadores
	ptsSensor sensor = reinterpret_cast<ptsSensor>(Sensor);

	//Actualiza Actuadores
	DomoBoard::setActuator(&domoboard.RELE, sensor->Aux);
}

void Pulsado_Soltado_sal(void *Sensor)
{
	Pulsado_Soltado(Sensor);

	//Actualiza Actuadores
	for(uint8_t i = 0; i < ((ptsSensor)Sensor)->managerActuators.count(); i++){
		DomoBoard::setActuator(((ptsSensor)Sensor)->managerActuators.peek(i), ((ptsSensor)Sensor)->Aux);
	}
}

void interruptor_sal(void *Sensor)
{
	Interruptor(Sensor);

	//Actualiza Actuadores
	for(uint8_t i = 0; i < ((ptsSensor)Sensor)->managerActuators.count(); i++){
		DomoBoard::setActuator(((ptsSensor)Sensor)->managerActuators.peek(i), ((ptsSensor)Sensor)->Aux);
	}
}

void conmutador_sal(void *Sensor)
{
	conmutador(Sensor);

	//Actualiza Actuadores
	for(uint8_t i = 0; i < ((ptsSensor)Sensor)->managerActuators.count(); i++){
		DomoBoard::setActuator(((ptsSensor)Sensor)->managerActuators.peek(i), ((ptsSensor)Sensor)->Aux);
	}
}

void Interruptor_ModBus_Rele(void *Sensor){
	ptsSensor sensor = reinterpret_cast<ptsSensor>(Sensor);
	if(sensor->valor_Df == sensor->valor){
		Cregs[MB_RELE] = (Cregs[MB_RELE] == 0) ? 1 : 0;
		writecoil(); 
	}
}

void Interruptor_ModBus_Triac(void *Sensor){
	ptsSensor sensor = reinterpret_cast<ptsSensor>(Sensor);
	if(sensor->valor_Df == sensor->valor){
		// Invertimos el valor en la memoria ModBus
		Cregs[MB_TRIAC] = (Cregs[MB_TRIAC] == 0) ? 1 : 0;
		writecoil(); 
	}
}



