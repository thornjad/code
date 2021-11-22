#include <stdio.h>

#define VERSION "0.1.0"
#define SYNTAX "<program> ::= R|λ|<program><program>|(<program>)"

void printVersion();

int main(int argc, char const *argv[]) {
  printVersion();
  if (argc <= 1) {
    printf("No arguments supplied\n");
    return 1;
  }
  // TODO check for valid file format. For now, call local file
  // char *path[(sizeof argv / sizeof *argv)] = argv[1];
  // TODO pass all args to cc

  // TODO parser/translate to c

  // TODO call cc on c file

  return 0;
}

void printVersion() {
  printf("pc v%s\n", VERSION);
}

void printSyntax() {

}
