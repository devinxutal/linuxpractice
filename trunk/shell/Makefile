CFLAGS = -Wall -g
INCLUDES = -Iinclude

all: xsh

xsh: main.c
	gcc $(CFLAGS) $(INCLUDES) -lncurses -o tbsserver $^
	
clean:
	rm xsh

