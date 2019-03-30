(***************************************************************
*
* CSCI 305 - ML Programming Lab
*
* <firstname> <lastname>
* <email-address>
*
***************************************************************)

(* Define your data type and functions here *)

datatype 'element set = Empty | Set of 'element * 'element set;

fun isMember e set = if set = Empty then false else
  let
    val Set (item, rest) = set
  in
    if e = item then true else isMember e rest
  end;


fun list2Set ([]) = Empty
  | list2Set (x::xs) = if null xs then Set(x, Empty) else
  let
    val new_set = list2Set(xs);
  in
    if isMember x new_set then new_set else Set (x, new_set)
  end;

fun union set1 set2 = if set1 = Empty then set2 else
  let
    val Set (item, rest) = set1
  in
    if isMember item set2 then union rest set2 else union rest (Set (item, set2))
  end;

fun intersect set1 set2 = if set1 = Empty then set1 else
  let
    val Set (item, rest) = set1
  in
    if isMember item set2 then Set(item, intersect rest set2) else intersect rest set2
  end;


fun f[] = [] (* a *)
  | f (x::xs) = (x+1) :: (f xs); (* b *)

(* Simple function to stringify the contents of a Set of characters *)
fun stringifyCharSet Empty = ""
  | stringifyCharSet (Set(y, ys)) = Char.toString(y) ^ " " ^ stringifyCharSet(ys);

(* Simple function to stringify the contents of a Set of ints *)
fun stringifyIntSet Empty = ""
  | stringifyIntSet (Set(w, ws)) = Int.toString(w) ^ " " ^ stringifyIntSet(ws);

(* Simple function to stringify the contents of a Set of strings *)
fun stringifyStringSet Empty = ""
  | stringifyStringSet (Set(z, zs)) = z ^ " " ^ stringifyStringSet(zs);

(* Simple function that prints a set of integers *)
fun print_int x = print ("{ " ^ stringifyIntSet(x) ^ "}\n");

(* Simple function that prints a set of strings *)
fun print_str x = print ("{ " ^ stringifyStringSet(x) ^ "}\n");

(* Simple function that prints a set of characters *)
fun print_chr x = print ("{ " ^ stringifyCharSet(x) ^ "}\n");

list2Set [1, 3, 2];
list2Set [#"a", #"b", #"c"];
list2Set [];
list2Set [6, 2, 2];
list2Set ["x", "y", "z", "x"];

(* Question 1 *)
f [3, 1, 4, 1, 5, 9];

(* Question 5 *)
val quest5 = isMember "one" (list2Set ["1", "2", "3", "4"]);
print ("\nQuestion 5: " ^ Bool.toString(quest5) ^ "\n");

(* Question 7 *)
val quest7 = list2Set ["it", "was", "the", "best", "of", "times,", "it", "was", "the", "worst", "of", "times"];
print "\nQuestion 7: ";
print_str quest7;
print "\n";

(* Question 9 *)
print "\nQuestion 9: ";
print_str (union (list2Set ["green", "eggs", "and"]) (list2Set ["ham"]));

(* Question 10 *)
print "\nQuestion 10: ";
print_str (intersect (list2Set ["stewed", "tomatoes", "and", "macaroni"]) (list2Set ["macaroni", "and", "cheese"]));
