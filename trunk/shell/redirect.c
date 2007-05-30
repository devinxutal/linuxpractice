/* redirect.c */

#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <ctype.h>
#include <fcntl.h>

#include "redirect.h"

char * find_file_name(char * cmd, int from, int * index){
	int i;
	int start = -1;
	int end =-1;
	for(i = from; cmd[i] != '\0'; i++){
		if(!isspace(cmd[i])){
			if(start <0){
				start = i;
			}
		}else{
			if(start<0)
				continue;
			else{
				end = i;
				cmd[end] = '\0';
				*index = end+1;
				return cmd + start;
			}
		}
	}
	if(start > 0){
		return cmd+from;
	}
	return NULL;	
}

int check_redirect(char * cmd, int *fds, int *flags){
	int i;
	int first_occur_index = -1;
	for(i=0;i<3;i++){
		flags[i] = 0;
	}
	for(i=0; cmd[i]!= '\0'; i++){
		if(cmd[i] == '>'){
			int error_stream = 0;
			if(first_occur_index<0)
				first_occur_index = i;
			if( i!=0 && cmd[i-1]== '0'+2){
				if(i>1 && isspace(cmd[i-2])){
					error_stream = 1;
					first_occur_index--;
				}
			}
			if(cmd[i+1] =='>'){	//append mode
				char * filename = find_file_name(cmd, ++i +1, &i);
				int fd = open(filename, O_WRONLY|O_CREAT, 0644);
				fds[1+error_stream] = fd;
				flags[1+error_stream] = 1;
				lseek(fd, 0, SEEK_END);
				printf("redirect : %d to %d\n", fd, 1+error_stream);
			}else{		//overwrite mode
				char * filename = find_file_name(cmd, i+1, &i);
				int fd = open(filename, O_WRONLY|O_CREAT, 0644);
				fds[1+error_stream] = fd;
				flags[1+error_stream] = 1;
				printf("redirect : %d to %d\n", fd, 1+error_stream);
			}
		}
		if(cmd[i] == '<'){
			char * filename = find_file_name(cmd, i+1, &i);
			int fd = open(filename, O_RDONLY);
			if(first_occur_index<0)
				first_occur_index = i;
			
			fds[0] = fd;
			flags[0] = 1;
			
			printf("redirect : %d to %d", fd, 0);
		}
	}
	if(first_occur_index >0){
		cmd[first_occur_index] = '\0';
	}
		
	return 0;
}
