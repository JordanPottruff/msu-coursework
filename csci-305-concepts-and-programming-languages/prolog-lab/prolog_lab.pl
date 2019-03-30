
mother(M,C) :- parent(M,C), female(M).

father(F,C) :- parent(F,C), male(F).

spouse(A,B) :- married(B,A).
spouse(A,B) :- married(A,B).

child(C,P) :- parent(P,C).

son(S,P) :- child(S,P), male(S).

daughter(D,P) :- child(D,P), female(D).

sibling(A,B) :- father(F,A), father(F,B), A \== B.

brother(B,O) :- sibling(B,O), male(B).

sister(S,O) :- sibling(S,O), female(S).

uncle(U,O) :- parent(P,O), brother(U,P).
uncle(U,O) :- parent(P,O), sister(S,P), spouse(S,U).

aunt(A,O) :- parent(P,O), sister(A,P).
aunt(A,O) :- parent(P,O), brother(B,P), spouse(B,A).

grandparent(GP,GC) :- parent(P,GC), parent(GP,P).

grandfather(GF,GC) :- grandparent(GF,GC), male(GF).

grandmother(GM,GC) :- grandparent(GM,GC), female(GM).

grandchild(GC, GP) :- grandparent(GP, GC).

ancestor(X,Y) :- parent(X,Y).
ancestor(X,Y) :- parent(Z,Y), ancestor(X,Z).

descendant(X,Y) :- ancestor(Y,X).

older(X,Y) :- born(X,XB), born(Y,YB), XB < YB.

younger(X,Y) :- born(X,XB), born(Y,YB), XB > YB.

regentWhenBorn(R,B) :-
  born(B,DOB),
  reigned(R,RS,RE),
  DOB >= RS,
  DOB =< RE.

cousin(X,Y) :-
  spouse(A,B),
  grandfather(A,X),
  grandfather(A,Y),
  grandmother(B,X),
  grandmother(B,Y),
  X \== Y,
  not(sibling(X,Y)).
