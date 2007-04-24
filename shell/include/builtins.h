/* builtins.h */

#ifndef BUILTINS_H_
#define BUILTINS_H_

#include "builtinmap.h"


int init_builtins(void);
int end_builtins(void);

val_t builtins_find(char *builtins);

#endif /*BUILTINS_H_ */
