/* builtinmap.c */

#include "builtinmap.h"
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

static void check_full(builtinmap_t * map);

int compare(const void *, const void *);

//static int b_find(map_entry_t *, int, int, char *);

builtinmap_t * builtinmap_new(){
	builtinmap_t * map = NULL;
	map_entry_t * entries = NULL;
	
	if((map = (builtinmap_t *)malloc(sizeof(builtinmap_t))) == NULL){
		fputs("Out of memory while allocating builtinmap!",stderr);
		exit(1);
	}
	if((entries = (map_entry_t *)malloc(sizeof(map_entry_t) * CMDMAP_CAP)) == NULL){
		fputs("Out of memory while allocating builtinmap!",stderr);
		exit(1);
	}

	map->entries = entries;
	map->len = 0;
	map->cap = CMDMAP_CAP;

	return map;
}

int builtinmap_free(builtinmap_t * map){
	free(map->entries);
	free(map);
	return 0;
}

int builtinmap_add(builtinmap_t * map, char *key, val_t val){
	
	check_full(map);

	map->entries[map->len].key = key;
	map->entries[map->len].val = val;
	map->len++;
	return 0;
}

val_t builtinmap_find(builtinmap_t * map, char *key){
	map_entry_t * entry = NULL;
	map_entry_t key_entry;
	key_entry.key = key;

       	entry = bsearch( &key_entry, map->entries, map->len, sizeof(map_entry_t), compare);
	if( entry == NULL){
		return NULL;
	}else{
		val_t val = entry->val; 
		return val;
	}
}

int builtinmap_sort(builtinmap_t * map){
	qsort(map->entries, map->len, sizeof(map_entry_t), compare);
	return 0;
}

static void check_full(builtinmap_t * map){
	map_entry_t * ptr;
	
	if( map->len < map->cap){
		return ;
	}

	if((ptr = (map_entry_t *)realloc(map->entries, map->cap*2*sizeof(map_entry_t))) == NULL){
		fputs("Out of memory while allocating builtinmap!", stderr);
		exit(1);
	}
	map->cap *= 2;
	map->entries = ptr;
}


int compare(const void * a, const void * b){
	return strcmp( ((map_entry_t * )a)->key, ((map_entry_t *)b)->key);
}

