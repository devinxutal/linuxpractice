/* parser.h */

#ifndef PARSER_H_
#define PARSER_H_


#define PARSE_OK 0
#define PARSE_FAIL 1
#define PARSE_INCOMPLETE 2

int parse_cmd(const char *cmd, char *arg[], int max);

#endif /* PAESER_H_ */
