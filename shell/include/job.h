/* job.h */

#ifndef JOB_H_
#define JOB_H_

#include <unistd.h>


#define MAX_JOBS 64

typedef struct job{
	int job_no;
	pid_t pgid;
} job_t;


int init_job(void);

int job_clear(void);
job_t * job_add(void);
job_t * job_get(int index);
job_t * job_first(void);
job_t * job_find_by_pgid(int pgid);
int job_remove(int job_no);
int job_remove_first(void);

#endif /* JOB_H_ */
