/* history.c */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "history.h"

#define MAX_STRING 1024
/*
#define MAX_COMMANDS 50

#define HISTORY_PATH "~/.myshell"


typedef struct history{
	char * commands[MAX_COMMANDS];
	int count;
} history_t;

typedef struct history_iterator{
	history_t * his;
	int index;
} history_iterator_t;
*/
history_t * history_open(){
	FILE *fp = NULL;
	history_t *his;
	char buffer[MAX_STRING];
	
	if((fp = fopen(HISTORY_PATH, "r"))== NULL ){
		return NULL;
	}
	
	if((his = (history_t *)malloc(sizeof(history_t))) == NULL){
		fclose(fp);
		return NULL;
	}
	his->count = 0;

	while(fgets(buffer,MAX_STRING, fp) != NULL){
		char *cmd;
		if(buffer[0] == '\n')
			break;
		if((cmd = (char *)malloc((strlen(buffer)+1) * sizeof(char))) == NULL){
			//error
			int i;
			fprintf(stderr, "Error while allocating history struct.");
			//TODO some cleanup stuff
			for(i = 0;i<his->count; i++){
				free(his->commands[i]);
			}
			free(his);
			return NULL;
		}
		strcpy(cmd, buffer);
		cmd[strlen(cmd) - 2] = '\0';
		his->count ++;
		if(his->count == MAX_COMMANDS)
			break;
	}
	fclose(fp);
	return his;

}

int  history_close(history_t * his){
	int i ;
	FILE *file;
	if (his == NULL) return -1;
	file = fopen(HISTORY_PATH, "w");
	if(file == NULL){
		fprintf(stderr, "Open history file failed.");
		return -1;
	}

	for(i =0; i<his->count; i++){
		fprintf(file, "%s\n",his->commands[i]);
		free(his->commands[i]);
	}
	fclose(file);
	//free
	free(his);
	return 0;
}

int history_flush(history_t * his){
	int i ;
	FILE *file;
	if (his == NULL) return -1;
	file = fopen(HISTORY_PATH, "w");
	if(file == NULL){
		fprintf(stderr, "Open history file failed.");
		return -1;
	}

	for(i =0; i<his->count; i++){
		fprintf(file, "%s\n",his->commands[i]);
	}
	fclose(file);
	return 0;
}

int history_add(history_t * his, char * command){
	if(his == NULL)
		return -1;
	
	if(his->count >= MAX_COMMANDS){
		int i;
		//move away the first command and add
		free(his->commands[0]);
		for(i=0;i<MAX_COMMANDS -1;i++){
			his->commands[i] = his->commands[i+1];
		}
		his->count = MAX_COMMANDS - 1;
		history_add(his, command);
	}else{
		char *cmd;
		if((cmd = (char *)malloc((strlen(command)+1) * sizeof(char))) == NULL){
			
			fprintf(stderr, "Error while allocating history struct.");
			return -1;
		}
		strcpy(cmd, command);
		his->commands[his->count] = cmd;
		his->count ++;
	}
	return 0;	
}

history_iterator_t * history_iterator_get(history_t * his){
	history_iterator_t *itr;
	
	if (his == NULL)
		return NULL;
		 
	if((itr = (history_iterator_t *)malloc(sizeof(history_iterator_t))) == NULL){
		return NULL;
	}
	itr->his = his;
	itr->index = 0;

	return itr;

}

int history_iterator_destroy(history_iterator_t * itr){
	if(itr == NULL)
		return -1;
	
	free(itr);
	return 0;
}

int history_iterator_has_next(history_iterator_t * itr){
	if(itr == NULL) return 0;
	return (itr->his->commands[itr->index] != NULL);
}

char * history_iterator_next(history_iterator_t * itr){
	if(history_iterator_has_next(itr)){
		char * temp = itr->his->commands[itr->index];
		itr->index ++;
		if(itr->index >= itr->his->count){
			itr->index = 0;
		}
		return temp;
	}else{
		return NULL;
	}
}
