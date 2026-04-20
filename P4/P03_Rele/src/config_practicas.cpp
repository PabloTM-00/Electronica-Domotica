/*
 * config_practicas.cpp
 *
 *  Created on: 10/03/2015
 *      Author: jctejero
 */

/****************************************************************************/
/***        Include files                                                 ***/
/****************************************************************************/
#include	"config_practicas.h"

/****************************************************************************/
/***                 Functions                                            ***/
/****************************************************************************/
void config_practica1_apt_1(){
	domoboard.BOTON1.SensorEvent = Pulsado_Soltado;

	domoboard.BOTON2.SensorEvent = Pulsado_Soltado;

	domoboard.BTN_OPT.SensorEvent = Pulsado_Soltado;
}

void config_practica1_apt_2(){
	domoboard.BOTON1.SensorEvent = Interruptor;
	domoboard.BOTON1.Aux = OFF;

	domoboard.BOTON2.SensorEvent = Interruptor;
	domoboard.BOTON2.Aux = OFF;

	domoboard.BTN_OPT.SensorEvent = Interruptor;
	domoboard.BTN_OPT.Aux = OFF;
}

void config_practica1_apt_3(){
	domoboard.BOTON1.SensorEvent = conmutador;

	domoboard.BOTON2.SensorEvent = conmutador;

	domoboard.BTN_OPT.SensorEvent = conmutador;
}

void config_practica3_apt_2(){
	domoboard.BOTON1.SensorEvent = conmutador_sal;

	domoboard.BOTON2.SensorEvent = conmutador_sal;

	domoboard.BTN_OPT.SensorEvent = conmutador_sal;
}

void config_practica4_apt_2(){
	domoboard.BOTON1.SensorEvent = Pulsado_Soltado_Rele;
	domoboard.BOTON2.SensorEvent = Pulsado_Soltado_Triac;
	domoboard.BTN_OPT.SensorEvent = NULL; 
}

void config_practica4_apt_3(){
	domoboard.BOTON1.SensorEvent = Interruptor_Rele;
	domoboard.BOTON1.Aux = OFF;
	domoboard.BOTON2.SensorEvent = Interruptor_Triac;
	domoboard.BOTON2.Aux = OFF;
	domoboard.BTN_OPT.SensorEvent = NULL;
}

void config_practica4_apt_4(){
	domoboard.BOTON1.SensorEvent = conmutador_Triac;
	domoboard.BOTON2.SensorEvent = conmutador_Triac;
	domoboard.BTN_OPT.SensorEvent = Interruptor_Rele;
	domoboard.BTN_OPT.Aux = OFF;
}

void config_practica4_apt_5(){
	domoboard.BOTON1.SensorEvent = conmutador_Rele_Triac;
	domoboard.BOTON2.SensorEvent = conmutador_Rele_Triac;
	domoboard.BTN_OPT.SensorEvent = conmutador_Rele_Triac;
}