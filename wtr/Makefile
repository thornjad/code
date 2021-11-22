TARGET = wtr
LIBS = -lcurl
CC = gcc
CFLAGS = -g -Wall
PREFIX = /usr/local

.PHONY: clean all default

default: $(TARGET)
all: default

OBJECTS = $(patsubst %.c, %.o, $(wildcard *.c))
HEADERS = $(wildcard *.h)

%.o: %.c $(HEADERS)
	$(CC) $(CFLAGS) -c $< -o $@

.PRECIOUS: $(TARGET) $(OBJECTS)

$(TARGET): $(OBJECTS)
	$(CC) $(OBJECTS) -Wall $(LIBS) -o $@

config:
	curl-config --libs

install: $(TARGET) config
	mkdir -p $(PREFIX)/bin
	cp $< $(PREFIX)/bin/$(TARGET)

uninstall:
	rm -f $(PREFIX)/bin/$(TARGET)

clean:
	-rm -f *.o
	-rm -f $(TARGET)
