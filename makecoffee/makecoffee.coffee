TransformerBase = require('./lib/transforms/base')
BuilderBase = require('./lib/builder/base')
Builder = require('./lib/builder')

{ buildError } = require('./lib/helpers')

###*
# # makecoffee API
###

module.exports = makecoffee = (source, options) ->
  makecoffee.build(source, options).code

###*
# makecoffee() : makecoffee(source, [options])
# Compiles JavaScript into CoffeeScript.
#
#     output = makecoffee.build('a = 2', {});
#
#     output.code
#     output.ast
#     output.map
#     output.warnings
#
# All options are optional. Available options are:
#
# ~ filename (String): the filename, used in source maps and errors.
# ~ comments (Boolean): set to `false` to disable comments.
#
# Here's what it does:
#
# 1. Parse code into a JS AST (`.parseJS()`)
# 2. Mutate the JS AST into a CoffeeScript AST (`.transform()`)
# 3. Render the AST into CoffeeScript (`.generate()`)
###

makecoffee.build = (source, options = {}) ->
  options.filename ?= 'input.js'
  options.indent ?= 2
  options.source = source

  ast = makecoffee.parseJS(source, options)
  {ast, warnings} = makecoffee.transform(ast, options)
  {code, map} = makecoffee.generate(ast, options)
  {code, ast, map, warnings}

###*
# parseJS() : makecoffee.parseJS(source, [options])
# Parses JavaScript code into an AST via Esprima.
# Returns a JavaScript AST. Throws an error if parsing can't continue.
#
#     try
#       ast = makecoffee.parseJS('var a = 2;')
#     catch err
#       ...
###

makecoffee.parseJS = (source, options = {}) ->
  try
    Esprima = require('esprima')
    Esprima.parse(source, loc: true, range: true, comment: true)
  catch err
    throw buildError(err, source, options.filename)

###*
# transform() : makecoffee.transform(ast, [options])
# Mutates a given JavaScript syntax tree `ast` into a CoffeeScript AST.
#
#     ast = makecoffee.parseJS('var a = 2;')
#     result = makecoffee.transform(ast)
#
#     result.ast
#     result.warnings
#
# This performs a few traversals across the tree using traversal classes
# (TransformerBase subclasses).
#
# These transformations will need to be done in multiple passes. The earlier
# steps (function, comment, etc) will make drastic modifications to the tree
# that the other transformations will need to pick up.
###

makecoffee.transform = (ast, options = {}) ->
  ctx = {}
  run = (classes) -> TransformerBase.run(ast, options, classes, ctx)
  comments = not (options.comments is false)

  # Injects comments into the AST as BlockComment and LineComment nodes.
  run [
    require('./lib/transforms/comments')
  ] if comments

  # Moves named functions to the top of the scope.
  run [
    require('./lib/transforms/functions')
  ]

  # Everything else -- these can be done in one step without any side effects.
  run [
    require('./lib/transforms/exponents')
    require('./lib/transforms/ifs')
    require('./lib/transforms/iife')
    require('./lib/transforms/literals')
    require('./lib/transforms/loops')
    require('./lib/transforms/members')
    require('./lib/transforms/objects')
    require('./lib/transforms/binary')
    require('./lib/transforms/empty_statements')
    require('./lib/transforms/others')
    require('./lib/transforms/precedence')
    require('./lib/transforms/returns')
    require('./lib/transforms/switches')
    require('./lib/transforms/unsupported')
  ]

  # Consolidate nested blocks -- block nesting can be a side effect of the
  # transformations above
  run [
    require('./lib/transforms/blocks')
  ]

  { ast, warnings: ctx.warnings }

###*
# generate() : makecoffee.generate(ast, [options])
# Generates CoffeeScript code from a given CoffeeScript AST. Returns an object
# with `code` (CoffeeScript source code) and `map` (source mapping object).
#
#     ast = makecoffee.parse('var a = 2;')
#     ast = makecoffee.transform(ast)
#     {code, map} = generate(ast)
###

makecoffee.generate = (ast, options = {}) ->
  new Builder(ast, options).get()

###*
# version : makecoffee.version
# The version number
###

makecoffee.version = require('./package.json').version
