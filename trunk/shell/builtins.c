/* command.c */

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <ctype.h>
#include "builtins.h"
#include "builtinmap.h"
#include "shell.h"

#define OK 0
#define TRUE 1
#define FALSE 0

builtinmap_t * builtinmap = NULL;

static int builtin_cd(char *args[]);
static int builtin_quit(char *args[]);


int init_builtins(){
	if(builtinmap)
		return 0;
	builtinmap = builtinmap_new();
	


	//TODO add builtins
	builtinmap_add(builtinmap, "cd", builtin_cd);
	builtinmap_add(builtinmap, "quit", builtin_quit);
	//
	builtinmap_sort(builtinmap);
	return 0;
}


val_t builtins_find(char * builtin){
	return builtinmap_find(builtinmap, builtin);
}

int end_builtins(void){
	builtinmap_free(builtinmap);
	builtinmap = NULL;
	return 0;
}




static int builtin_cd(char *args[]){
	int rt;
	if(args[1] == NULL || strlen(args[1]) == 0)
		return 0;
	if(strcmp(args[1], "~")== 0){
		rt = chdir(xsh_info.current_user->home_dir);
	}else{
		rt = chdir(args[1]);
	}
	if(rt == 0){	//cd succeed, change the cwd;
		free(xsh_info.cwd);
		xsh_info.cwd = getcwd(NULL, 512);
		return 0;
	}else{
		char buf[512];
		sprintf(buf, "xsh: cd: %s",args[1] );
		perror(buf);
		return -1;
	}
	return 0;

}

static int builtin_quit(char *args[]){
	printf("byebye\n");
	shell_end();
	exit(0);
}
