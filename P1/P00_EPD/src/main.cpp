#include <Arduino.h>
#include "DomoBoard.h"

DomoBoard board;

volatile bool flag = false;
bool light = false;
int last10 = HIGH;
int last11 = HIGH;

void isr3() {
  flag = true;
}

void setup() {
  board.begin();
  attachInterrupt(digitalPinToInterrupt(board.P3), isr3, FALLING);
}

void loop() {
  bool change = false;

  if (flag) {
    delay(50);
    if (digitalRead(board.P3) == LOW) {
      light = !light;
      change = true;
    }
    flag = false;
  }

  int curr10 = board.read10();
  if (curr10 != last10) {
    if (curr10 == LOW) {
      light = !light;
      change = true;
    }
    last10 = curr10;
    delay(50);
  }

  int curr11 = board.read11();
  if (curr11 != last11) {
    if (curr11 == LOW) {
      light = !light;
      change = true;
    }
    last11 = curr11;
    delay(50);
  }

  if (change) {
    if (light) {
      Serial.println("Luz: ON");
    } else {
      Serial.println("Luz: OFF");
    }
  }
}