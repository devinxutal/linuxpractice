/* shell.c */


#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "shell.h"

static int shell_init(void);

static int print_promote(void);

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
	printf("Welcome, boy!\n");
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

int shell_run(){
	if(!initialized){
		return -1;
	}
	while(1){
		print_promote();
		sleep(1);
	}
	return 0;
}

static int shell_init(){
	user_info_t * ui = NULL;
	static char user_name[256];
	static char shell[256];
	static char home_dir[256];
	static char cwd[256];
	strcpy(user_name, "devin");
	strcpy(shell, "/bin/bash");
	strcpy(cwd, "/home/devin");
	strcpy(home_dir, "/home/devin");
	
	if((ui = (user_info_t *)malloc(sizeof(user_info_t))) == NULL){
		perror("cannot malloc user_info struct");
		return -1;
	}
	ui->user_name = user_name;
	ui->shell = shell;
	ui->home_dir = home_dir;
	xsh_info.current_user = ui;
	xsh_info.cwd = cwd;
	return 0;
} 


static int print_promote(void){
	
	printf("[%s:%s]$\n", xsh_info.current_user->user_name, xsh_info.cwd);
	return 0;
}
