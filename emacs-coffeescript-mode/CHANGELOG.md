# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

# [Unreleased]

# [0.6.3] 2016/03/29

## Added
- Implement auto-fill function(#337, #342 Thanks Danny McClanahan)
- Add custom variable for switching compile buffer(#331 Thanks Danny McClanahan)

## Changed
- Refactoring attributes(#345)
- Improve package comment and meta comment(#344 Thanks Steve Purcell)
- Fix coffee-compile command if buffer/region don't end with newline case(#343)
- Better indentation(#335, #338)
- Set '--no-header' by default in compiling commands
- Fix lambda expression regexp(#330 Thanks Danny McClanahan)

# [0.6.2] 2015/12/10

## Added
- Implement python like indentation(#323)
- Add toggle arrow function command(#329 Thanks jasonm23)

## Changed
- Fix assignment highlight issue(#326)

# [0.6.1] 2015/10/01

## Changed
- Fix byte-compile warning(#308)
- Improve sending multiple lines to REPL(#316, #318 Thanks Danny McClanaha)
- Fix version comparison issue(#321 Reported by crackhopper)

# [0.6.0] 2015/03/19

## Changed
- Fix highlighting regexp literal issue(#302)
- Fix highlighting class member issue(#304)

# [0.5.9] 2015/02/23

## Added
- Implement highlighting multiple lines regexp literal(Experimental)
- Re-implement string interpolation for string nested string interpolation
- Implement indent region command

## Changed
- Fix indent command for 'unless' block
- Fix #287, #289(slash after string interpolation breaks syntax highlighting)
- Fix #285(double quote in string interpolation breaks syntax highlighting)

# [0.5.8] 2015/01/04

## Changed
- Fix highlighting boolean keyword(#281 Thanks Wilfred)
- Fix first line indentation

# [0.5.7] 2014/12/07

## Changed
- Improve for tramp
- Fix coffee-comment-dwim same as other major-mode
- Fix highlighting class attribute issue(#272 Thanks no0)
- Fix highlighting issue similar to #272

# [0.5.6] 2014/11/06

## Added
- Support newer coffeescript sourcemap generation
- Support sending multiple line to REPL
- Highlight 'yield' as keyword

## Changed
- Fix not pop-up compiled buffer issue
- Improve else, catch, finally indentation
- Fix passing parameter to sourcemap.el
- Correct '_'(underscore) syntax property and remove work-around(Thanks dotemacs)

# [0.5.5] 2014/08/04

## Changed
- Improve compiling command(Thanks Gunnar Wrobel)

# [0.5.4] 2014-07-14

## Changed
- Fix js2coffee-replace-region issue(Thanks Mihir Rege)

# [0.5.3] 2014-06-27

## Support
- Support CSON file

# [0.5.2] 2014-05-25

## Changed
- Fix new line and indentation issue

# [0.5.1] 2014-05-19

## Changed
- Many bug fixes
- Fix highlighting issues
- Fix defun command
- Fix indentation issue

# [0.5.0] 2013-12-14

## Added
- Support block comment and triple quote

## Changed
- Many bug fix

## Support
- Drop Emacs 23 support

# [0.4.0] 2013-04-21

## Changed
- Bug fixes

# [0.3.0] 2013-03-07

## Added
- Compile command
- Documentation

# [0.2.0] 2013-03-07

## Added
- Support imenu

# [0.1.0] 2013-03-07
- Initial commits
