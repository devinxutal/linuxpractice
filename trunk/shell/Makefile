CFLAGS = -Wall -g
INCLUDES = -Iinclude

all: xsh

xsh: main.c shell.o history.o
	gcc $(CFLAGS) $(INCLUDES) -o xsh $^

history.o: history.c include/history.h
	gcc $(CFLAGS) $(INCLUDES) -c history.c

shell.o: shell.c include/shell.h
	gcc $(CFLAGS) $(INCLUDES) -c shell.c

clean:
	rm xsh history.o shell.o

