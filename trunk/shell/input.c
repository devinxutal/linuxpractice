/* input.c */

#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <stdio.h>

#include "input.h"

#define BUF_SIZE 256

char *get_cmd_line(void){
	int buf_size = BUF_SIZE;
	char * buf = NULL;
	int len = 0;
	char c;
	//malloc initial buf
	if((buf = (char *)malloc((buf_size +1) *sizeof(char))) == NULL){
		perror("Error while malloc buf in func get_cmd_line");
		return NULL;
	}
	
	while((c = getchar()) != '\n'){
		if(len == buf_size){
			buf_size *= 2;
			if((buf = realloc(buf, (buf_size+1)*sizeof(char))) == NULL){
				perror("Error while malloc buf in fuc get_cmd_line");
				return NULL;
			}
		}
		buf[len++] = c;
	}
	buf[len] = '\0';
	return buf;
}

