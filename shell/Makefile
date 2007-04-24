CFLAGS = -Wall -g
INCLUDES = -Iinclude

all: xsh

xsh: main.c shell.o history.o parser.o input.o builtins.o builtinmap.o
	gcc $(CFLAGS) $(INCLUDES) -o xsh $^

history.o: history.c include/history.h
	gcc $(CFLAGS) $(INCLUDES) -c history.c

shell.o: shell.c include/shell.h
	gcc $(CFLAGS) $(INCLUDES) -c shell.c

parser.o: parser.c include/parser.h
	gcc $(CFLAGS) $(INCLUDES) -c parser.c

input.o: input.c include/input.h
	gcc $(CFLAGS) $(INCLUDES) -c input.c

builtins.o: builtins.c include/builtins.h
	gcc $(CFLAGS) $(INCLUDES) -c builtins.c
	
builtinmap.o: builtinmap.c include/builtinmap.h
	gcc $(CFLAGS) $(INCLUDES) -c builtinmap.c

clean:
	rm xsh history.o shell.o parser.o input.o builtins.o builtinmap.o

