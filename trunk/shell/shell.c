/* shell.c */


#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <pwd.h>
#include <ctype.h>

#include <sys/types.h>
#include <sys/wait.h>

#include "shell.h"
#include "input.h"
#include "parser.h"
#include "builtins.h"
#include "builtinmap.h"
#include "history.h"
#include "redirect.h"

static int shell_init(void);

static int print_prompt(void);

static int check_back(char *cmd, int *isback);

static int set_env(void);

static int initialized = 0;

//for test 
/*
static void print_args(char * args[]){
	int i;
	printf("====ARGS====\n");
	for(i =0; args[i]!= NULL ; i++){
		printf("arg_%d: %s\n", i,args[i]);
	}
}
*/
//end for test

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
	//free(xsh_info.cwd);
	//free(xsh_info.current_user->user_name);
	//free(xsh_info.current_user->shell);
	//free(xsh_info.current_user->home_dir);
	//free(xsh_info.current_user);
	//deinit builtins
	
	history_close(his);
	end_builtins();	
		
	initialized = 0;
	return 0;
}

int shell_run(){
	char * cmd = NULL;
	char * args[16];
	int isback = 0;
	BUILTIN_HANDLER builtin_handler = NULL;
	if(!initialized){
		return -1;
	}
	while(1){
		int i;
		int redirect_fds[3];
		int redirect_flags[3];
		print_prompt();
		if((cmd = get_cmd_line()) ==NULL){
			perror("Error while getting command line!");
			return -1;
		}
		if(strlen(cmd)== 0)
			continue;
		//parse the command
		check_back(cmd, &isback);
		check_redirect(cmd, redirect_fds, redirect_flags);
		parse_cmd(cmd, args, 16);
		//first find command in builtins
		if((builtin_handler = builtins_find(args[0]))!=NULL){ //find builtin
			builtin_handler(args);
		}else{		// builtin not found, search other commands
			int pid;
			int status;
			if((pid = fork())==0){		//child
				int rt;
				int i;
				//do redirect
				for(i = 0; i<3; i++){
					if(redirect_flags[i]){
						if(redirect_fds[i] <0){
							perror("xsh");
							exit(-1);
						}
						if(redirect_fds[i] != i){	//not standard, dup
							printf("FUCK");
							if(dup2(redirect_fds[i], i) != i){
								perror("xsh");
								exit(-1);
							}
							close(redirect_fds[i]);
						}
					}
				}
				//end redirect
				
				rt = execvp(args[0], args);
				if(rt != 0){
					perror(args[0]);
				}
			}else{				//parent
				//close the fds for redirection
				int i;
				for(i=0;i<3;i++){
					if(redirect_flags[i] && redirect_fds[i]>0){
						close(redirect_fds[i]);
					}
				}
				if(isback==0)
					waitpid(pid, &status, 0);
			}
			//excmd();
		}
		//free args
		for(i=0;args[i]!= NULL; i++){
			free(args[i]);
		}
		free(cmd);
	}
	
	return 0;
}

static int shell_init(){
	user_info_t * ui = NULL;
	static struct passwd *pwd;
	pwd = getpwuid(getuid());
	if(pwd == NULL){
		perror("failed to get user infomation");
		return -1;
	}
	if((ui = (user_info_t *)malloc(sizeof(user_info_t))) == NULL){
		perror("cannot malloc user_info struct");
		return -1;
	}
	
	ui->user_name = pwd->pw_name;
	ui->shell = pwd->pw_shell;
	ui->home_dir = pwd->pw_dir;
	xsh_info.current_user = ui;
	xsh_info.cwd = getcwd(NULL, 512);

	his = history_open();	
	//init builtins	
	init_builtins();
	//init env
	set_env();
	return 0;
} 


static int print_prompt(void){
	printf("[%s:%s]$", xsh_info.current_user->user_name, xsh_info.cwd);
	return 0;
}

static int set_env(void){
	char *oldenv = getenv("PATH");
	char *toadd = "/home/devin/courses/LinuxPractice/linuxpractice/shell/bin";
	char *buf = (char *)malloc((strlen(oldenv)+strlen(toadd)+10)*sizeof(char));
	if(buf == NULL)
		return -1;
	strcpy(buf, toadd);
	buf[strlen(toadd)]= ':';
	strcpy(buf+strlen(toadd)+1, oldenv);
	printf("env:[%s]", buf);
	setenv("PATH", buf, 1);
	return 0;
}

static int check_back(char *cmd, int *isback){
	char *c = cmd;
	while(*c != '\0'){
		if(*c == '&'){
			if(c != cmd && isspace(*(c-1))){
				*isback = 1;
				*c = '\0';
			}
		}
		c++;
	}
	return 0;
}
