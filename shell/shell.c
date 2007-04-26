/* shell.c */


#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <pwd.h>

#include <sys/types.h>
#include <sys/wait.h>

#include "shell.h"
#include "input.h"
#include "parser.h"
#include "builtins.h"
#include "builtinmap.h"

static int shell_init(void);

static int print_prompt(void);

//static int exists(const char *cmd);

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
	free(xsh_info.cwd);
	free(xsh_info.current_user->user_name);
	free(xsh_info.current_user->shell);
	free(xsh_info.current_user->home_dir);
	free(xsh_info.current_user);
	//deinit builtins
	
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
		print_prompt();
		if((cmd = get_cmd_line()) ==NULL){
			perror("Error while getting command line!");
			return -1;
		}
		if(strlen(cmd)== 0)
			continue;
		//parse the command
		parse_cmd(cmd, args, 16, &isback);
		//first find command in builtins
		if((builtin_handler = builtins_find(args[0]))!=NULL){ //find builtin
			builtin_handler(args);
		}else{		// builtin not found, search other commands
			int pid;
			int status;
			if((pid = fork())==0){
				int rt;
				rt = execvp(args[0], args);
				if(rt != 0){
					perror(args[0]);
				}
			}else{
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
/*
static int exists(const char * cmd){
	char *path, *p;
	int i;

	i = 0;

	path = getenv("PATH");
	p = path;
	while( *p != '\0'){
		if(*p != ':')
			buffer[i++] = *p;
		}else{
			buffer[i++] = '/';
			buffer[i] = '\0';
			strcat(buffer, cmd);
	
			if(access(buffer, F_OK) == 0)
				return 0;
			else
				i = 0;
	
		}
		p++;
	}
	return -1;
}
*/

static int set_env(void){
	char *oldenv = getenv("PATH");
	char *toadd = "./bin";
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
