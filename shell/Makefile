CFLAGS = -Wall -g
INCLUDES = -Iinclude

all: xsh

xsh: main.c shell.o history.o parser.o
	gcc $(CFLAGS) $(INCLUDES) -o xsh $^

history.o: history.c include/history.h
	gcc $(CFLAGS) $(INCLUDES) -c history.c

shell.o: shell.c include/shell.h
	gcc $(CFLAGS) $(INCLUDES) -c shell.c

parser.o: parser.c include/parser.h
	gcc $(CFLAGS) $(INCLUDES) -c parser.c

clean:
	rm xsh history.o shell.o parser.o

