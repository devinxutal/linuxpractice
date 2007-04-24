/* shell.c */


#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "shell.h"
#include "input.h"
#include "parser.h"


static int shell_init(void);

static int print_prompt(void);

static int initialized = 0;

//for test 

static void print_args(char * args[]){
	int i;
	printf("====ARGS====\n");
	for(i =0; args[i]!= NULL ; i++){
		printf("arg_%d: %s\n", i,args[i]);
	}
}
//end for test
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
	char * cmd = NULL;
	char * args[16];
	int isback = 0;
	if(!initialized){
		return -1;
	}
	while(1){
		print_prompt();
		if((cmd = get_cmd_line()) ==NULL){
			perror("Error while getting command line!");
			return -1;
		}
		//parse the command
		parse_cmd(cmd, args, 16, &isback);
		print_args(args);
		printf("is back? %d\n", isback);
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
	getcwd(cwd,256);
	xsh_info.cwd = cwd;
	return 0;
} 


static int print_prompt(void){
	
	printf("[%s:%s]$", xsh_info.current_user->user_name, xsh_info.cwd);
	return 0;
}
