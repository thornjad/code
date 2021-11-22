# makecoffee

[![pipeline status](https://gitlab.com/thornjad/makecoffee/badges/master/pipeline.svg)](https://gitlab.com/thornjad/makecoffee/commits/master)
[![coverage report](https://gitlab.com/thornjad/makecoffee/badges/master/coverage.svg)](https://gitlab.com/thornjad/makecoffee/commits/master)
[![npm version](http://img.shields.io/npm/v/makecoffee.svg?style=flat)](https://npmjs.org/package/makecoffee "View this project on npm")

Compile Javascript into Coffeescript

## Install

```sh
npm install --global makecoffee
makecoffee --help
```

Also available via CDN (`window.makecoffee`):

```
<script src="https://unpkg.com/thornjad/makecoffee/dist/makecoffee.js"></script>
```

## Command line

The command line utility accepts both filenames or stdin.

```sh
$ makecoffee file.js [file2.js ...]
$ cat file.js | makecoffee
```

## JavaScript API

Available via npm (`require('makecoffee')`), or via CDN in the browser (as `window.makecoffee`):

```js
result = makecoffee.build(source);

result.code     // code string
result.ast      // transformed AST
result.map      // source map
result.warnings // array of warnings
```

Errors are in this format:

```js
catch (e) {
  e.message       // "index.js:3:1: Unexpected INDENT\n\n   3   var\n   ---^"
  e.description   // "Unexpected INDENT"
  e.start         // { line: 1, column: 4 }
  e.end           // { line: 1, column: 10 }
  e.sourcePreview // '...'
}
```

Warnings are in this format:

```js
result.warnings.forEach((warn) => {
  warn.description   // "Variable 'x' defined twice
  warn.start         // { line: 1, column: 4 }
  warn.end           // { line: 1, column: 9 }
  warn.filename      // "index.js"
})
```

## Docs

 - [Hacking guide](notes/Hacking.md) - want to contribute? here are tips to get you started.

 - [AST format](notes/AST.md) - technical description of the CoffeeScript AST format.

 - [Special cases](notes/Special_cases.md) - a list of edge cases that makecoffee accounts for.

 - [Compatibility mode](notes/Special_cases.md#compatibilitymode) - list of tweaks that compatibility mode (`--compat`) addresses.

 - [Goals](notes/Goals.md) - outline of the project's goals.

 - [Specs](notes/Specs.md) - examples of how JavaScript compiles to CoffeeScript.

## Thanks

Copyright (c) 2019 Jade Michael Thornton. Released under the [MIT License](./LICENSE)

This project is based on work done years ago in [js2cofee](https://github.com/js2coffee/js2coffee) by Rico Sta. Cruz, Anton Wilhelm and Benjamin Lupton
