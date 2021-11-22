prefix = /usr/local
bindir = $(prefix)/bin

options ?= -g -Wall -Wextra

all:
	cc -c $(options) src/bf.c
	cc *.o -o bf
	rm -f *.o

clean:
	rm -f Out.c a.out bf *.o

install:
	mkdir -p $(bindir)
	cc -c $(options) src/bf.c
	cc *.o -o bf
	rm -f *.o
	mv bf $(bindir)
	chown root $(bindir)/bf
	chmod go-w+rx $(bindir)/bf

uninstall:
	rm -f Out.c a.out bf *.o
	rm -f $(bindir)/bf
