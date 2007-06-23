/* job.c */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "job.h"


static job_t * job_new();
static int job_free(job_t *jb);

static job_t * jobs[MAX_JOBS];
static int job_inuse[MAX_JOBS];
int initialized = 0;

int init_job(void){
	if(initialized){
		return 0;
	}else{
		int i;
		initialized = 1;
		for(i=0;i<MAX_JOBS;i++){
			jobs[i] = NULL;
			job_inuse[i] = 0;
		}
	}
	return 0;
}

int job_clear(void){
	int i;
	for(i=0;i<MAX_JOBS;i++){
		if(jobs[i]!= NULL){
			free(jobs);
			jobs[i] = NULL;
		}
		job_inuse[i] = 0;
	}
	return 0;
}

job_t * job_add(void){
	int i;
	for(i = 0; i<MAX_JOBS; i++){
		if(!job_inuse[i]){
			job_inuse[i] = 1;
			jobs[i] = job_new();
			jobs[i]->job_no = i+1;
			return jobs[i];
		}
	}
	return NULL;
}

job_t * jog_get(int index){
	if(index >= MAX_JOBS){
		return NULL;
	}else{
		if(job_inuse[index]){
			return jobs[index];
		}else{
			return 0;
		}
	}
}
job_t * job_first(void){
	int i;
	for(i = 0; i<MAX_JOBS; i++){
		if(job_inuse[i]){
			return jobs[i];
		}
	}
	return NULL;

}

job_t * job_find_by_pgid(int pgid){
	int i;
	for(i = 0; i<MAX_JOBS; i++){
		if(job_inuse[i]){
			if(jobs[i]->pgid == pgid){
				return jobs[i];
			}
		}
	}
	return NULL;
	
}
int job_remove(int job_no){
	job_no = job_no -1;
	if(job_inuse[job_no] != 1){
		return 0;
	}else{
		job_free(jobs[job_no]);
		jobs[job_no] = NULL;
		job_inuse[job_no] = 0;
		return job_no;
	}
}

int job_remove_first(void){
	int i;
	for(i = 0; i<MAX_JOBS; i++){
		if(job_inuse[i]){
			job_inuse[i] = 0;
			job_free(jobs[i]);
			jobs[i] = NULL;
			return i;
		}
	}
	return -1;
}

static job_t * job_new(){
	job_t *jb;
	jb = (job_t *)malloc(sizeof(job_t));
	return jb;
}

static int job_free(job_t *jb){
	if(jb != NULL){
		free(jb);
	}
	return 0;
}
