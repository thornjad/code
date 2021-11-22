Preliminary notes:

Registers:

A : accumulator
B : auxiliary (not addressable)
D : [data] tape pointer (not directly addressable)

Right-infinite data tape, 0-indexed

Operations:

```
~spider+  // inc A
~spider-  // dec A

spider!  // neg A
spiders  // add *D to A

#spider  // swap A B
spider  // flip A *D

>spider<  // read into A
<spider>  // print from A

|spider|  // inc D
_spider_  // dec D

spider?  // jump to matching spider. if A is 0
spider.  // end loop

¯\_(ツ)_/¯  // HCF; program must end with this
```
