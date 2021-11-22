#!/usr/local/bin/tclsh

set tclCommands [info commands]

proc localProcs { namespace {excludeList ""} } {
  set localList [list]
  foreach p [namespace eval $namespace {info procs {*}}] {
    if {!($p  in  $excludeList) } {
      lappend localList $p
    }
  }
  return $localList
}
set externalProcs [localProcs {::} ]

package require flightaware
use_vhost anneleslie
package require flightaware-main

set localList [localProcs {::} $externalProcs]

# Get list of namespaces
set namespaceList [lsort [namespace children [lindex $argv 0]]]

while { [llength $namespaceList] > 0 } {
  set nSpace [lindex $namespaceList 0]
  set namespaceList [lreplace $namespaceList 0 0]
  regexp -expanded {^::([^:]*)} $nSpace match rootSpace
  if { $nSpace eq {} || $rootSpace in { oo tcl zlib } } {
    continue
  }
  if { [::itcl::is class $nSpace]} {
    puts "\nClass=$nSpace"
    set repObj [$nSpace #auto]
    #puts "[$repObj info function]"
    set localList [list {*}[$repObj info function]]
  } else {
    puts "\nnSpace=$nSpace "
    set localList [list {*}[localProcs $nSpace $externalProcs]]
  }
  foreach childSpace [namespace children $nSpace] {
    regexp -expanded {^::([^:]*)} $childSpace match rootSpace
    if { $rootSpace in { oo tcl zlib } } {
      continue
    }
    lappend namespaceList $childSpace
  }
  foreach procName [lsort $localList] {
    puts "    $procName"
    set callList [list]
    if { [info exists repObj] } {
      if { [catch {set body "[$repObj info function $procName -body]"} result] } {
        puts "result=$result procName=$procName"
        set body [info body ${nSpace}::${procName}]
      }
    } else {
      set body [info body ${nSpace}::${procName}]
    }

    foreach line [string trimleft [split "$body" "\n" ] ] {
      # Get the tcl "command" on this line.
      set pNames [regexp -all -inline -- {^[\t\s]*([^\s]*)\s} $line]
      # Look for embedded commands
      lappend pNames {*}[regexp -all -inline -- {\[\s*([^\s]*)[\s\]]} $line ]
      #puts "$pNames"
      foreach {full name} $pNames {
        set full [string trimleft $full "\t"]
        if { $full eq {} || [regexp -- {[#\}].*} $full] } {
          # skip blank lines and lines w/ leading # or \}
          continue
        }
        if { [string match "\$*" $name ] } {
          set pattern "\\${name}\\s\*(\[^\\s\]\*)"
          set method [regexp -all -inline -- "$pattern" $line]
          lappend callList "        $name [lindex $method 1]"
        } elseif { !($name in "$tclCommands") } {

          lappend callList  "        [namespace which -command $name] ($full)"
        }
      }
    }
    puts [join [lsort -unique $callList] "\n"]
  }

  if { [info exists repObj] } {
    ::itcl::delete object $repObj
    unset repObj
  }
}
