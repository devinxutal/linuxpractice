/* ls.c */
//#define _XOPEN_SOURCE

#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <dirent.h>
#include <grp.h>
#include <pwd.h>
#include <time.h>
#include <locale.h>

#include <sys/types.h>
#include <sys/stat.h>

typedef struct fent{
	char fname[256];
	struct stat f_stat;
} fent_t;

typedef struct eginfo{
	int u_max;
	int g_max;
	int l_max;
	int s_max;
} eginfo_t;

static int get_ent_count(DIR *dp);

static fent_t * get_ents(DIR *dp, int max, int *count);

static int print_ents(fent_t * ents, int count);

static int get_eg_info(fent_t *ents, int count, eginfo_t * info);

static int calc_num_len(long num);


static int generate_mode_string(char *buf, struct stat * stats);

int compare(const void * a, const void * b);

int main(int argc, char *argv[]){
	DIR *dp;
	fent_t * ents;
	int dircount;
	int actualcount;
	

	if(argc != 1){
		if(chdir(argv[1]) != 0){
			perror("ls");
			return -1;
		}
	}
	if((dp = opendir(".")) == NULL){
		perror("ls");
		return -1;
	}
	dircount = get_ent_count(dp);
	ents = get_ents(dp, dircount, &actualcount);
	print_ents(ents,dircount);
	free(ents);
	return 0;
}


static  int get_ent_count(DIR *dp){
	int count=0;
	if(dp == NULL)
		return -1;
	rewinddir(dp);
	
	while(readdir(dp)!= NULL)
		count ++;
	return count;

}

static fent_t * get_ents(DIR *dp, int max, int *count){
	fent_t * ents = NULL;	
	struct dirent * entry;
	int i;
	if(dp == NULL)
		return NULL;
	rewinddir(dp);
	if((ents = (fent_t *)malloc(sizeof(fent_t) * max+1)) == NULL)
		return NULL;
	i = 0;
	while((entry = readdir(dp)) != NULL){
		if(lstat(entry->d_name, &(ents[i].f_stat))!= 0){
			free(ents);
			return NULL;
		}
		strcpy(ents[i].fname, entry->d_name);
		i++;	
	}
	if(count!= NULL)
		*count = i;
	return ents;
}


static int get_eg_info(fent_t *ents, int count, eginfo_t * info){
	int i;
	info->g_max = 0;
	info->u_max = 0;
	info->s_max = 0;
	info->l_max = 0;
	for(i = 0; i<count; i++){
		int u,g,s,l;
		struct passwd * pwd;
		struct group * grp;
		if((pwd = getpwuid(ents[i].f_stat.st_uid)) == NULL){
			continue;
		}
		if((grp = getgrgid(ents[i].f_stat.st_gid)) == NULL){
			continue;
		}
		//check user name len
		u = strlen(pwd->pw_name);
		if(u > info->u_max)
			info->u_max = u;
		//check group name len
		g = strlen(grp->gr_name);
		if(g > info->g_max)
			info->g_max = g;
		//check link count len
		l = calc_num_len((long)ents[i].f_stat.st_nlink);
		if(l > info->l_max)
			info->l_max = l;
		//check size len
		s = calc_num_len((long)ents[i].f_stat.st_size);
		if(s > info->s_max)
			info->s_max = s;
	}
	return 0;
}

static int calc_num_len(long num){
	int i = 1;
	while((num = num/10))
		i++;
	return i;
}

static int generate_mode_string(char *buf, struct stat * stats){
	int i;
	int mod = stats->st_mode;
	char sig[3] = {'r','w','x'};
	
	
	if ( S_ISREG(mod) ){ 	//is it a regular file?
		buf[0] = '-';
	}else if(S_ISDIR(mod)){	//directory?
		buf[0] = 'd';
	}else if(S_ISCHR(mod)){	//character device?
		buf[0] = 'c';
	}else if(S_ISBLK(mod)){	//block device?
		buf[0] = 'b';
	}else if(S_ISFIFO(mod)){	//FIFO (named pipe)?
		buf[0] = 'f';
	}else if(S_ISLNK(mod)){	//symbolic link? (Not in POSIX.1-1996.)
		buf[0] = 'l';
	}else if(S_ISSOCK(mod)){	//socket? (Not in POSIX.1-1996.)
		buf[0] = 's';
	}
	
	for(i = 0;i<9;i++){
		if(i != 0){
			mod = mod >> 1;
		}
		if((mod & 1)){
			buf[9-i] = sig[2 - i%3];
		}else{
			buf[9-i] = '-';
		}
	}

	buf[10] = '\0';
	return 0;
}

static int print_ents(fent_t * ents, int count){
	int i;
	eginfo_t info;
	char * fmt_buf[512];
	get_eg_info(ents, count, &info);
	qsort(ents, count, sizeof(fent_t), compare);	//sort the entries
	//generate the format string
	sprintf(fmt_buf, "%%10s %%%dd %%%ds %%%ds %%%dld %%s %%s\n", info.l_max, info.u_max, info.g_max, info.s_max);
	
	for(i = 0; i<count; i++){
		struct passwd * pwd;
		struct group * grp;
		char mod_buf[11];
		char time[64];
		struct tm tim;
	
		localtime_r(&(ents[i].f_stat.st_ctime), &tim);
		strftime(time, 64,"%Y-%m-%d %H:%M",&tim);
		
		if((pwd = getpwuid(ents[i].f_stat.st_uid)) == NULL){
			continue;
		}
		if((grp = getgrgid(ents[i].f_stat.st_gid)) == NULL){
			continue;
		}
		generate_mode_string(mod_buf, &(ents[i].f_stat));
		
		printf(fmt_buf,
				mod_buf, 
				ents[i].f_stat.st_nlink, 
				pwd->pw_name,
				grp->gr_name,
				ents[i].f_stat.st_size,
				time,
				ents[i].fname
				);
	}
	
	return 0;
}


int compare(const void * a, const void * b){
	return strcmp( ((fent_t *)a)->fname, ((fent_t *)b)->fname);
}

