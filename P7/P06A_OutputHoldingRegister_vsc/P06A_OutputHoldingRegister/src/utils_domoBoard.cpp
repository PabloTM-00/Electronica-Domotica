/*
 * utils_domoBoard.cpp
 *
 *  Created on: 10/03/2015
 *      Author: jctejero
 * 
 *  modified on: 17/03/2025
 */

/****************************************************************************/
/***        Include files                                                 ***/
/****************************************************************************/
#include "utils_domoBoard.h"
#include "debuglog.h"

bool stateConmutador = OFF;

void Interruptor(void *Sensor)
{
	if(((ptsSensor)Sensor)->valor_Df == ((ptsSensor)Sensor)->valor){
		DEBUG(((ptsSensor)Sensor)->name);
		DEBUG(F(" : Interruptor --> "));
		if(((ptsSensor)Sensor)->Aux == OFF){
			((ptsSensor)Sensor)->Aux = ON;
			DEBUGLNF("ON");
		}else{
			((ptsSensor)Sensor)->Aux = OFF;
			DEBUGLNF("OFF");
		}
	}
}

void Pulsado_Soltado(void *Sensor){
	ptsSensor sensor = reinterpret_cast<ptsSensor>(Sensor);

	DEBUG(sensor->name);
	if(sensor->valor_Df == sensor->valor){
		DEBUGLNF(" --> Soltado");
		sensor->Aux = OFF;
	}else{
		DEBUGLNF(" --> Pulsado");
		sensor->Aux = ON;
	}
}

void conmutador(void *Sensor)
{
	//static bool valor = OFF;

	ptsSensor sensor = reinterpret_cast<ptsSensor>(Sensor);

	if(sensor->valor_Df == sensor->valor){
		DEBUG(F("Conmutador --> "));
		if(stateConmutador == OFF){
			stateConmutador = ON;
			DEBUGLN(F("ON"));
		}else{
			stateConmutador = OFF;
			DEBUGLN(F("OFF"));
		}
	}

	sensor->Aux = stateConmutador;
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

void mbInterruptor(void *mbSensor)
{
	TpmbSensor sensor = reinterpret_cast<TpmbSensor>(mbSensor);

	if(sensor->Sensor->valor_Df == sensor->Sensor->valor){
		mbDomoboard.manager_mbActuators(&(sensor->mbActuators), TOGGLE);
	}
}

// definimos pin del pir y variables globales nuevas
#define PIR_PIN 13 
unsigned long tiempo_ultimo_movimiento = 0; // cronometro
bool triac_encendido_por_pir = false;

void Init_PIR() {
    pinMode(PIR_PIN, INPUT);
    // valores por defecto
    Aregs[MB_PIR_ENABLE] = 1; // PIR esta activado por defecto
    Aregs[MB_PIR_TIMER] = 3;  // 3 segundos por defecto
}

void loop_practica7() {
	// leer sensor y guardar estado en Dregs para java
    bool estado_pir = digitalRead(PIR_PIN);
    Dregs[MB_PIR] = estado_pir ? 1 : 0;

	// comprobar si el pir esta habilitado desde java y gestionar el pir/triac
    if (Aregs[MB_PIR_ENABLE] == 1) {
        if (estado_pir == HIGH) {
			// si detecta movimiento enciende triac y resetea el cronometro
            mbDomoboard.setmbActuator(&(mbDomoboard.TRIAC), sON);
            tiempo_ultimo_movimiento = millis();
            triac_encendido_por_pir = true;
        }

        // temporizador de apagado automatico si no detecta movimiento em un tiempo 
        if (triac_encendido_por_pir && estado_pir == LOW) {
            // transforma los segundos mandados por java a milisegundos porque 
			// millis() funciona en eso
            unsigned long tiempo_limite = Aregs[MB_PIR_TIMER] * 1000UL;

			// apagar si la hora actual menos tiempo ult movimiento es >= limite
            if (millis() - tiempo_ultimo_movimiento >= tiempo_limite) {
                mbDomoboard.setmbActuator(&(mbDomoboard.TRIAC), sOFF);
                triac_encendido_por_pir = false;
            }
        }
    } else {
		// apagar si se pide desde java
		if (triac_encendido_por_pir) {
            mbDomoboard.setmbActuator(&(mbDomoboard.TRIAC), sOFF);
            triac_encendido_por_pir = false;
        }
    }
}

