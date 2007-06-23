CFLAGS = -Wall -g
INCLUDES = -Iinclude

all: utils

utils: ls wc

ls: ls.c
	gcc $(CFLAGS) $(INCLUDES) -o ls ls.c
wc: wc.c
	gcc $(CFLAGS) $(INCLUDES) -lpthread -o wc wc.c
clean:
	rm ls wc

