/*
 * utils_domoBoard.h
 *
 *  Created on: 10/03/2015
 *      Author: jctejero
 */

#ifndef UTILS_DOMOBOARD_H_
#define UTILS_DOMOBOARD_H_

/****************************************************************************/
/***        Include files                                                 ***/
/****************************************************************************/
#include	"Arduino.h"
#include	"domoBoard.h"

/****************************************************************************/
/***        Macro Definitions                                             ***/
/****************************************************************************/
#define 	ON   				HIGH
#define     OFF					LOW

/****************************************************************************/
/***        Exported Functions                                            ***/
/****************************************************************************/
void Interruptor(void *Sensor);
void Pulsado_Soltado(void *Sensor);
void conmutador(void *Sensor);
void conmutador_sal(void *Sensor);

void Pulsado_Soltado_Rele(void *Sensor);
void Pulsado_Soltado_Triac(void *Sensor);
void Interruptor_Rele(void *Sensor);
void Interruptor_Triac(void *Sensor);
void conmutador_Triac(void *Sensor);
void conmutador_Rele_Triac(void *Sensor);

#endif /* UTILS_DOMOBOARD_H_ */
