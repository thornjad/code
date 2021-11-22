(* spider interpreter, using a very naïve method

  Copyright 2018 Jade M Thornton
  Free for use under the terms of the ISC license

  The interpreter takes a single .spider file as an argument and runs it.
  Illegal characters are a syntax error, unlike some similar languages like
  brainfuck.
 *)

open Printf

exception Spider_SyntaxError of string

(* find file, warn if not .spider *)
let spiderlangver = "0.1.0" in
let defaultFilename = "a.ml" in
let filename = Sys.argv.(1) in
let outfilename = Sys.argv.(1) in
let run =
  match filename with
  | None -> printf "Spider version %s\n" spiderlangver
  | _ ->
    let srcTokens = parseFile filename in
    let outTokens = mapTokens srcTokens in
    let outfile =
      match outfilename with
      | None -> writeFile defaultFilename outTokens
      | _ -> writeFile outfilename outTokens
      in
    let finalfile = compileFile outfile in
    runSpider finalfile

let parseFile filename =
  let srcTokens = ref [] in
  let ic = open_in filename in
  try
    while true do
      let line = input_line ic in
      let chunks = Str.split (Str.regexp " +") in
      srcTokens := grabTokens chunks !srcTokens
    done
  with End_of_file ->
    close_in ic;
    srcTokens

let rec grabTokens chunks tokens =
  let len = List.length chunks in
  match chunks with
  | [] -> tokens
  | hd :: tl ->
    if Str.string_match (Str.regexp "//.*") hd then tokens (* skip comments *)
    else grabTokens tl (tokens :: hd)

let mapTokens tokens = (* TODO *)
let writeFile filename tokens = (* TODO *)
let writeMachineSetup filename = (* TODO *)

let compileFile filename = (* TODO *)
let runSpider filename = (* TODO *)
