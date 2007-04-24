/* main.c */

#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>

#include "shell.h"


int main(){
	shell_start();
	shell_run();
	shell_end();
	exit(0);
}



