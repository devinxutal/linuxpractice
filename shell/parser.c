/* parser.c */


#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <ctype.h>

#include "parser.h"


int parse_cmd(const char *cmd, char *arg[], int max, int *isback){
	int i;
	int start = -1;
	int end = -1;
	int count = 0;
	*isback = 0;
	for(i = 0; i< strlen(cmd)+1; i++){
		if(isspace(cmd[i])||cmd[i] == '\0'){
			if(start < 0){
				continue;
			}else{
				end = i;
				if((arg[count] = (char *)malloc(sizeof(char)*(end-start+1))) == NULL){
					perror("malloc failed while parsing command!");
					return PARSE_FAIL;
				}
				strncpy(arg[count],cmd + start, end - start);
				arg[count][end-start] = '\0';
				count++;
				start = -1;
			}
		}else{
			if(start<0){
				start = i;
			}
			if(cmd[i] == '&' && cmd[i+1] == '\0'){
				*isback = 1;
				start = -1;
			}
		}
	}
	arg[count] = NULL;
	return 0;
}
