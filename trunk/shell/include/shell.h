/* shell.h */

#ifndef SHELL_H_
#define SHELL_H_

int shell_start();
int shell_end();

typedef struct user_info {
	uid_t uid, euid;
	gid_t gid, egid;	
	char *user_name;	// user name 
	char *shell;		// shell from the password file
	char *home_dir;		// home directory
} user_info_t;

typedef struct shell_info{
	char * cwd;		// current working directory
	user_info *current_user;// current user information
}

#endif /* SHELL_H_

