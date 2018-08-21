# GNU/JEL

[Project page](https://www.gnu.org/software/jel/)

Licensed under [GNU GPLv3](https://www.gnu.org/software/jel/license.txt).

Using [this fork](https://github.com/Bluexin/gnu-jel).

## Summary
(from the official documentation)

JEL is the library for evaluating a simple single line expressions in Java.
There is certain class of tasks where user should be given a possibility
to enter an expression of his own into the program. Typical examples
include : plotting user-defined functions, fitting the data by the
arbitrary user-defined functions, calculation of integrals and solving
differential equations involving the user-defined functions... and
many many more.... The key term above is the "evaluation of the user-defined
functions", which is exactly what JEL is supposed to do for You.

The key feature of JEL is the fact that it is a compiler ! To write
the interpreter for expressions is the task, close to trivial but incurs
a HUGE perfomance penalty due to the fact that Java is interpreted language
itself. JEL, in contrary, compiles expressions directly to Java bytecodes,
allowing their fast evaluation. Moreover, if user's Java virtual mashine
has JIT compiler, expressions are transparently compiled into the native
machine code, resulting in performance HIGHER that for most C written
interpreters (plus JEL do not require recompilation when transferred
from platform to platform). 