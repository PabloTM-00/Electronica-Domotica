/*
 * ModbusDomoboard.h
 *
 *  Created on: 11 mar. 2020
 *      Author: jctejero
 *
 *  Modified on: 01/04/2025
 */

#ifndef DOMOBOARD_MODBUSDOMOBOARD_H_
#define DOMOBOARD_MODBUSDOMOBOARD_H_

/**********************************************************************/
/***                         Include Files                          ***/
/**********************************************************************/
#include <AsyncWait.h>
#include "domoBoard.h"
#include "ModbusSlave/ModBusSlave.h"

/**********************************************************************/
/***                         enums Definitions                      ***/
/**********************************************************************/
/* slave registers */

//Discrete Output Coils
enum {
	MB_RELE,
	MB_TRIAC,
	MB_ACTPIR,
	MB_O_COILS
};

//Discrete Input Contacts
enum {
	MB_BOTON1,
	MB_BOTON2,
	MB_BTNOPT,
	MB_PIRMOV,
	MB_I_CONTATCS
};

//Registros ModBus para variables analógicas "Analog Output Holding Register"
enum{
	MB_SELPRACT,		// Registro ModBus Para seleccionar la configuración del sistema
	MB_TMP_PIR,			// Registro para controlar el tiempo activo del sensor PIR (Segundos)
	MB_A_REGS			// Total de registros Analógicos
};

//Registros Analógicos de Entrada
enum{
	MB_POT1,			// POTENCIÓMETRO 1
	MB_POT2,			// POTENCIÓMETRO 2
	MB_FOTOR, 			// fotorresistencia y nuevos sensores analógicos
    MB_TEMP,  
	MB_FOTOT, 
    MB_I_REGS			// Total de registros de Entrada
};

/****************************************************************************/
/***        Type Definitions                                              ***/
/****************************************************************************/
//State Actuator
typedef enum{
	sOFF,
	sON,
	TOGGLE
}TStateDigitalDev;

typedef struct
{
	tpsActuator		actuator;
	uint16_t 		*mbReg;				//Registro asociado para comunicaciones ModBus
}TmbActuator, *TpmbActuator;

typedef 	QueueList<TpmbActuator>		TmbActuators;

typedef struct{
	ptsSensor			Sensor;					//Sensor asociado
	int					Aux;					//Variable auxiliar, a la espera de definir el banco de registros digitales de entrada
	uint16_t 			*mbReg;					//Registro asociado para comunicaciones ModBus
	TmbActuators		mbActuators;			//Listado de actuadores manejados por el sensor
	TNotifyEvent		mbSensorEvent 	= NULL;	//Evento para aplicación asociada
	AsyncWait			*asyncWait 		= NULL;	//Para controlar si el evento asociado al sensor tiene un componente temporizado
}TmbSensor, *TpmbSensor;

typedef     QueueList<TpmbSensor>		TManagermbSensors;


/*************************************************************/
/***                         CLASS                         ***/
/*************************************************************/
class ModbusDomoboard: public DomoBoard {
private:
	TManagermbSensors	listmbSensors;

public:
	//Sensores Digitales
	TmbSensor	BOTON1;
	TmbSensor	BOTON2;
	TmbSensor	BTN_OPT;
	TmbSensor	PIR_MOV;

	//Sensores Analógicos
	TmbSensor	POT1;				// Potenciómetro 1
	TmbSensor	POT2;				// Potenciómetro 2

	TmbSensor 	FOTOR;
	TmbSensor 	TEMP;
	TmbSensor 	FOTOT;

	//Sensores Actuadores
	TmbActuator RELE;
	TmbActuator TRIAC;

	ModbusDomoboard();
	virtual ~ModbusDomoboard(){};

	void 	leerAllSensor(void);
	void 	leerSensor(TpmbSensor Sensor);

	void 	setmbActuator(TmbActuator *Actuator, TStateDigitalDev val);
	void 	manager_mbActuators(TmbActuators *Actuators, TStateDigitalDev val);
	void 	setModBusSlave(ModbusSlave *mbSlave);
	void    Clear_SensorsConfiguration();
};

/*********************************************************************/
/***                    Prototipos de funciones                    ***/
/*********************************************************************/
void leeSensoresmb(void);

/****************************************************************/
/***                    Exported Variables                    ***/
/****************************************************************/
extern ModbusDomoboard mbDomoboard;
extern uint16_t	Cregs[MB_O_COILS];
extern uint16_t	Dregs[MB_I_CONTATCS];	//Registros para "Dicrete Input Contacts"
extern uint16_t	Aregs[MB_A_REGS];		//Registros para "Analog Output Holding Registers"
extern uint16_t	Iregs[MB_I_REGS];		//Registros para "Analog Input Registers"

#endif /* DOMOBOARD_MODBUSDOMOBOARD_H_ */
