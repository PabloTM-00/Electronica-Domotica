#include <Arduino.h>
#include "debuglog.h"
#include "cfg_loop.h"
#include "types.h"
#include "QueueList.h"  
#include "domoBoard.h"
#include "config_practicas.h"

/*******************************************************/
/*** 					Variables                    ***/
/*******************************************************/
	
QueueList<void_callback_f> _loop_callbacks;

/*********************************************************************/
/*** 					Prototipos de funciones                    ***/
/*********************************************************************/
void main_loop();
void leerPuertoSerie();  

void epdRegisterLoop(void_callback_f callback) {
    _loop_callbacks.push(callback);
}

void setup() {
    Serial.begin(115200);

    config_practica4_apt_2();


    epdRegisterLoop(leeSensores);
    epdRegisterLoop(leerPuertoSerie); 

    DEBUGLNF("P04");
}

void loop() {
    EXECUTELOOP(){

        UPDATELOOP();

        main_loop();
    }
}

void main_loop(){
    for (uint8_t i = 0; i < _loop_callbacks.count(); i++) {
        (_loop_callbacks.peek(i))();
    }
}

void leerPuertoSerie() {
    if (Serial.available() >= 2) {
        char cabecera = Serial.read();
        
        if (cabecera == '#') {
            char comando = Serial.read();
            
            switch (comando) {
                case '1':
                    config_practica4_apt_2();
                    Serial.println("Apartado 2");
                    break;
                case '2':
                    config_practica4_apt_3();
                    Serial.println("Apartado 3");
                    break;
                case '3':
                    config_practica4_apt_4();
                    Serial.println("Apartado 4");
                    break;
                case '4':
                    config_practica4_apt_5();
                    Serial.println("Apartado 5");
                    break;
            }
        }
    }
}