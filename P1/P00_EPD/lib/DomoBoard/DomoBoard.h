#ifndef DOMOBOARD_H
#define DOMOBOARD_H

#include <Arduino.h>

class DomoBoard {
  public:
    DomoBoard();
    void begin();
    bool read10();
    bool read11();
    
    static const int P3 = 3;
    static const int P10 = 10;
    static const int P11 = 11;
};

#endif