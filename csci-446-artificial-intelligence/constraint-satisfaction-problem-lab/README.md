Free Flow CSP Lab
Author(s): Jordan Pottruff
Language(s): Java
Description: In this lab, I used my knowledge of software engineering to develop a program that implements two solvers for the game "Flow Free", which is a constraint satisfaction problem. My basic solver uses simple backtracking, where a color is assigned to each open square until an assignment fails and is "backtracked" until the correct series of assignments is found. This basic solver was able to handle game boards of size 10x10 or smaller in a reasonable amount of time. For larger boards, my advanced solver adds on additional heuristics and forward checking. Cell assignment is prioritized by the smallest remaining values for that cell. At any point if an assignment changes an open cell's domain size to 0, then a backtrack is commenced as well. These additions provided solutions to small game boards at a fraction of the time of the simple solver, and was capable of solving much larger game boards in a realistic time frame. In addition, my program uses a strategy pattern to allow the solvers to be interchanged at runtime.
Files
mazes/*: this directory stores example mazes of different sizes.
solutions/*: this directory contains the solutions to the example mazes.
src/*: these are the source files for the program, which is run from the file FlowFreeSolver.java.
REPORT.pdf: my report that details the implementation of the program and my analysis of its performance.
