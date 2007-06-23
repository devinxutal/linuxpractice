/* shell.c */


#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <pwd.h>
#include <ctype.h>

#include <sys/types.h>
#include <sys/fcntl.h>
#include <sys/wait.h>

#include "shell.h"
#include "input.h"
#include "parser.h"
#include "builtins.h"
#include "builtinmap.h"
#include "history.h"
#include "redirect.h"
#include "job.h"

#define TERM_PATH 	"/dev/tty"
#define TERM_OPT	O_RDWR
static int shell_init(void);

static int print_prompt(void);

static int check_back(char *cmd, int *isback);

static int set_env(void);

static int job_run_cmd(char ** cmds, int index, job_t *jb);

static int create_job(char *cmd);

static void exec_cmd(char *cmd);

static char** split_cmds(char * cmd);

static int check_builtin(char * cmd);

static int register_signal_handler();

static int check_jobs();
///////////////////////////////////
// signal handlers
///////////////////////////////////
static void sig_term_handler(int sig);
static void sig_quit_handler(int sig);
static void sig_tstp_handler(int sig);
static void sig_int_handler(int sig);
///////////////////////////////////
static int initialized = 0;

static int term_fd;

int shell_start(){

	if(initialized){
		return 0;
	}
	if(shell_init() != 0){
		exit(-1);
	}
	printf("\n---------------------------------------------------\n");
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

	if(!initialized){
		return -1;
	}
	while(1){
		//check if there is some jobs whose status has changed.
		check_jobs();
		//print prompt
		print_prompt();
		if((cmd = get_cmd_line()) ==NULL){
			perror("Error while getting command line!");
			return -1;
		}
		if(strlen(cmd)== 0)
			continue;
		create_job(cmd);
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
	//register signal handlers
	register_signal_handler();
	//init job control module
	init_job();
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

static int create_job(char *cmd){
	int isback = 0;
	int i;
	char ** cmds;
	//parse the command
	check_back(cmd, &isback);
	cmds = split_cmds(cmd);
	if(cmds[0] == NULL){
		free(cmds);
		return 0;
	}
	if(!check_builtin(cmds[0])){ //builtin not found, this is a normal job.
		int pid;
		int pgid;
		job_t * jb;	//create job
		if((jb = job_add()) ==NULL){
			perror("xsh: create job failed");
		}
		if((pid=fork())<0){
			perror("xsh:");
		}else if(pid == 0){	//child 
			setpgid(getpid(), getpid());
			job_run_cmd(cmds, 0, jb);
		}else{			//parent
			setpgid(pid, pid);	//set the child process to its own process group
			pgid = pid;
			// do nothing, the following code will be execute
		}

		if(!isback){
			int status;
			term_fd = open(TERM_PATH, TERM_OPT);
			tcsetpgrp(term_fd, pgid);
			close(term_fd);
			waitpid(pid, &status, WUNTRACED);
			if(WIFSTOPPED(status)){
			}
		}else{
			term_fd = open(TERM_PATH, TERM_OPT);
			tcsetpgrp(term_fd, getpid());
			printf("[%d] %d\n",jb->job_no, jb->pgid);
			close(term_fd);
		}

	}
	//free cmds;
	for(i = 0;cmds[i]!= NULL; i++){
		free(cmds[i]);
	}
	free(cmds);
	return 0;
}

static int job_run_cmd(char ** cmds, int index, job_t *jb){
	pid_t pid;
	int fd[2];

	if(cmds[index+1] == NULL){	//this is the last command
		exec_cmd(cmds[index]);
		close(fd[0]);
		close(fd[1]);
	}else{				//not the last command, fork and run
		pipe(fd);
		if((pid=fork())<0){
			perror("xsh:");
			exit(-1);
		}else if(pid == 0){	//child
			// do redirect stuff 
			close(fd[0]);
			if(fd[1]!= STDOUT_FILENO){
				dup2(fd[1],STDOUT_FILENO);
			}
			close(fd[1]);
			//execute the cmd;
			exec_cmd(cmds[index]);
		}else{
			// do redirect stuff 
			close(fd[1]);
			if(fd[0]!= STDIN_FILENO){
				dup2(fd[0],STDIN_FILENO);
			}
			close(fd[0]);
			job_run_cmd(cmds, index + 1, jb);	//call itself recursively
		}
	}
	return 0;
}

static void exec_cmd(char *cmd){
	char * args[16];
	int i;
	int redirect_fds[3];
	int redirect_flags[3];
	int rt;
	split_cmds(cmd);
	//parse the command
	check_redirect(cmd, redirect_fds, redirect_flags);
	parse_cmd(cmd, args, 16);
	//first find command in builtins

	//do redirect
	for(i = 0; i<3; i++){
		if(redirect_flags[i]){
			if(redirect_fds[i] <0){
				perror("xsh");
				exit(-1);
			}
			if(redirect_fds[i] != i){	//not standard, dup
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
	//free args
	for(i=0;args[i]!= NULL; i++){
		free(args[i]);
	}
}
static char** split_cmds(char * cmd){
	char **cmds;
	int i;
	int count = 0;
	int start = 0;
	cmds = (char **)malloc(65* sizeof(char *));
	for(i = 0; ;i++){
		if(cmd[i] == '|' || cmd[i] == '\0'){
			int len = i - start;
			
			if(len>0){
				cmds[count++] = (char*)malloc((len+1)*sizeof(char));
				strncpy(cmds[count - 1], cmd + start, len);
				cmds[count-1][len] = '\0';

				start = i+1;
			}
		}
		if(cmd[i] == '\0'){
			break;
		}

	}
	cmds[count] = NULL;
	return cmds;
}

static int check_builtin(char * cmd){
	int i;
	int result = 0;
	char * args[16];
	BUILTIN_HANDLER builtin_handler = NULL;
	parse_cmd(cmd, args, 16);
	if((builtin_handler = builtins_find(args[0]))!=NULL){ //find builtin
		builtin_handler(args);
		result = 1;
	}else{
		result = 0;
	}
	
	//free args
	for(i=0;args[i]!= NULL; i++){
		free(args[i]);
	}
	return result;
}

static int register_signal_handler(){
	signal(SIGTSTP, sig_tstp_handler);
	signal(SIGINT, sig_int_handler);
//	signal(SIGQUIT, sig_quit_handler);

	return 0;
}
static void sig_term_handler(int sig){
	printf("%d", sig);
}
static void sig_quit_handler(int sig){
}
static void sig_tstp_handler(int sig){
	int shell_pgid = getpid();
	int term_fd = open(TERM_PATH, TERM_OPT);
	int fg_pgid = tcgetpgrp(term_fd);
	
	if(shell_pgid == fg_pgid){
		close(term_fd);
		perror("shell is already the fg process");
		return;
	}else{	//do job schedule
		job_t * jb = job_find_by_pgid(fg_pgid);
		if(jb == NULL){
			close(term_fd);
			perror("cannot find the job");
			return;
		}
		tcsetpgrp(term_fd, shell_pgid);
		printf("[%d] %d Stopped\n", jb->job_no, jb->pgid);
	}
	close(term_fd);
}
static void sig_int_handler(int sig){
	exit(-1);
}



static int check_jobs(){
	
	return 0;
}

