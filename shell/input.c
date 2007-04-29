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
	if(len!= 0){
		history_add(his,buf);
	}
	return buf;
}

/*
void init_keyboard()
 {
     tcgetattr(0,&initial_settings);
     new_settings = initial_settings;
     new_settings.c_lflag &= ~ICANON;
     new_settings.c_lflag &= ~ECHO;
     new_settings.c_lflag &= ~ISIG;
     new_settings.c_cc[VMIN] = 1;
     new_settings.c_cc[VTIME] = 0;
     tcsetattr(0, TCSANOW, &new_settings);
 }
 void close_keyboard()
 {
     tcsetattr(0, TCSANOW, &initial_settings);
 }
4.   Now for the function that checks for the keyboard hit:
 int kbhit()
 {
     char ch;
     int nread;
     if(peek_character != -1)
         return 1;
     new_settings.c_cc[VMIN]=0;
     tcsetattr(0, TCSANOW, &new_settings);
     nread = read(0,&ch,1);
     new_settings.c_cc[VMIN]=1;
     tcsetattr(0, TCSANOW, &new_settings);
                                                            201
Chapter 5
        if(nread == 1) {
             peek_character = ch;
             return 1;
        }
        return 0;
    }
   5.   The character pressed is read by the next function, readch, which then resets peek_charac-
        ter to –1 for the next loop.
    int readch()
    {
        char ch;
        if(peek_character != -1) {
             ch = peek_character;
             peek_character = -1;
             return ch;
        }
        read(0,&ch,1);
        return ch;
    }
*/
