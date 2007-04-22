/* history.h */

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

history_t * history_open();

int  history_close(history_t * his);

int history_flush(history_t * his);

history_iterator_t * history_iterator_get(history_t * his);

int history_iterator_destroy(history_iterator_t * itr);

int history_iterator_has_next(history_iterator_t * itr);

char * history_iterator_next(history_iterator_t * itr);

