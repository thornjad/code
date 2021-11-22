/* brainfuck interpreter, using a C intermediary file
 *
 * Copyright (c) 2017, 2019 Jade M Thornton
 * Free for use under the terms of the ISC license
 *
 * The interpreter takes a single .bf file as an argument and runs it. All
 * illegal brainfuck characters are treated as whitespace.
 *
 * To compile (to C) without running, pass --compile-only as the second argument.
 *
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

char* mapSymbol(char bfChar) {
	switch(bfChar) {
		case '>':
			return "++p;";
		case '<':
			return "--p;";
		case '+':
			return "++*p;";
		case '-':
			return "--*p;";
		case '.':
			return "putchar(*p);";
		case ',':
			return "*p=getchar();";
		case '[':
			return "while(*p){";
		case ']':
			return "}";
		default:
			return "";
	}
}

#define MACHINE_SETUP "#include<stdio.h>\n#include <stdlib.h>\nint main(void){char*a=calloc(120000,sizeof(int));if(a==NULL){perror(\"Alloc failed\");}char*p=a;"
#define MACHINE_BREAKDOWN "free(a);}"

FILE* putMachineSetup(FILE *file) {
	fputs(MACHINE_SETUP, file);
	return file;
}

FILE* putMachineBreakdown(FILE *file) {
	fputs(MACHINE_BREAKDOWN, file);
	return file;
}

FILE* transpile(FILE *from, FILE *to) {
	char c;
	while ((c = fgetc(from)) != EOF) {
		char* ct = mapSymbol(c);
		if (ct[0] != '\0') {
			fputs(mapSymbol(c), to);
		}
	}
	return to;
}

FILE* openFile(char const *path, char* dir) {
	FILE *file = fopen(path, dir);
	if (file == 0) {
		perror("Could not open file");
		exit(1);
	}
	return file;
}

void compile(char const path[]) {
	FILE *srcFile = openFile(path, "r");
	FILE *outFile = openFile("bfa.c", "w");

	outFile = putMachineSetup(outFile);
	outFile = transpile(srcFile, outFile);
	outFile = putMachineBreakdown(outFile);

	fclose(srcFile);
	fclose(outFile);
}

int main(int argc, char const *argv[]) {
	if (argc <= 1) {
		printf("Usage: bf <file> [--compile-only]\n");
		return 1;
	}

	compile(argv[1]);

	// trail with --compile or --compile-only to only compile
	if (argc <= 2 || strncmp(argv[2], "--compile", 9)) {
		system("cc -Wall bfa.c -o bfa && ./bfa && rm -f bfa bfa.c");
	}

	return 0;
}
