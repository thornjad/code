spec_files := $(shell find specs)
browserify := ./node_modules/.bin/browserify
uglify := ./node_modules/.bin/uglifyjs

.PHONY: all
all: \
	notes/Specs.md \
	notes/Special_cases.md \
	dist \
	dev

notes/Specs.md: $(spec_files)
	./node_modules/.bin/coffee ./lib/support/report_specs.coffee > $@

notes/Special_cases.md: $(spec_files)
	./node_modules/.bin/coffee ./lib/support/report_notes.coffee > $@

.PHONY: dist
dist: dist/makecoffee.js

dist/makecoffee.js: makecoffee.coffee $(shell find lib)
	$(browserify) -t coffeeify --extension=".coffee" -s makecoffee $< | $(uglify) -m -c > $@

.PHONY: dev
dev: makecoffee.coffee $(shell find lib)
	$(browserify) -t coffeeify --extension=".coffee" -s makecoffee $< | $(uglify) -b --comments > dist/makecoffee.dev.js

.PHONY: test
test: all
	npm test
