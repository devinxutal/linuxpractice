/* shell.c */


#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "shell.h"

static int shell_init(void);


static int initialized = 0;

shell_info_t xsh_info;

int shell_start(){
	if(initialized){
		return 0;
	}
	if(shell_init() != 0){
		exit(-1);
	}
	printf("\n---------------------------------------------------\n");
	printf("Welcome, boy!");
	
	initialized = 1;
	return 0;
}


int shell_end(){
	if(!initialized){
		return 0;
	}
	free(xsh_info.cwd);
	free(xsh_info.current_user->user_name);
	free(xsh_info.current_user->shell);
	free(xsh_info.current_user->home_dir);
	free(xsh_info.current_user);
	initialized = 0;
	return 0;
}


static int shell_init(){
	static char user_name[256];
	static char shell[256];
	static char home_dir[256];
	static char cwd[256];
	strcpy(user_name, "devin");
	strcpy(shell, "/bin/bash");
	strcpy(cwd, "/home/devin");
	strcpy(home_dir, "/home/devin");
	return 0;
} 
