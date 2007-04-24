/* builtinmap */

#ifndef BUILTINMAP_H_
#define BUILTINMAP_H_

#define BUILTINMAP_CAP 64

typedef int(*BUILTIN_HANDLER)(char * args[]);

typedef BUILTIN_HANDLER val_t;

typedef struct map_entry{
	char * key;
	val_t val;
} map_entry_t;

typedef struct builtinmap{
	map_entry_t *entries;
	int cap;
	int len;
} builtinmap_t;

builtinmap_t * builtinmap_new();

int builtinmap_free(builtinmap_t * map);

int builtinmap_add(builtinmap_t * map,  char *key, val_t val);

val_t builtinmap_find(builtinmap_t * map, char *key);

int builtinmap_sort(builtinmap_t * map);
#endif /* BUILTINMAP_H_ */
