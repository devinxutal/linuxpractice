CFLAGS = -Wall -g
INCLUDES = -Iinclude

all: utils

utils: ls

ls: ls.c
	gcc $(CFLAGS) $(INCLUDES) -o ls ls.c

clean:
	rm ls

