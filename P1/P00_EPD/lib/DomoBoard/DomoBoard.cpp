#include "DomoBoard.h"

DomoBoard::DomoBoard() {
}

void DomoBoard::begin() {
  pinMode(P3, INPUT_PULLUP);
  pinMode(P10, INPUT);
  pinMode(P11, INPUT);
  Serial.begin(115200);
}

bool DomoBoard::read10() {
  return digitalRead(P10);
}

bool DomoBoard::read11() {
  return digitalRead(P11);
}