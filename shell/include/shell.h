/* shell.h */

#ifndef SHELL_H_
#define SHELL_H_

#include <unistd.h>


int shell_start();
int shell_end();
int shell_run();

typedef struct user_info {
	uid_t uid, euid;
	gid_t gid, egid;	
	char *user_name;	// user name 
	char *shell;		// shell from the password file
	char *home_dir;		// home directory
} user_info_t;

typedef struct shell_info{
	char * cwd;		// current working directory
	user_info_t *current_user;// current user information
} shell_info_t;

#endif /* SHELL_H_ */

