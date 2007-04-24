/* command.c */

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <ctype.h>
#include "builtins.h"
#include "builtinmap.h"

#define OK 0
#define TRUE 1
#define FALSE 0

builtinmap_t * builtinmap = NULL;

int init_builtins(){
	if(builtinmap)
		return 0;
	builtinmap = builtinmap_new();

	//TODO add builtins
	//
	//
	builtinmap_sort(builtinmap);
	return 0;
}


val_t builtin_builtins_find(char * builtin){
	return builtinmap_find(builtinmap, builtin);
}

val_t builtins_find(char *builtin){
	return builtinmap_find(builtinmap, builtin);
}

int end_builtins(void){
	builtinmap_free(builtinmap);
	builtinmap = NULL;
	return 0;
}

