mymember(X,[X|_]).
mymember(X,[_|T]) :- mymember(X,T).

set([],[]).
set([H|T],[H|Out]) :-
    not(mymember(H,T)),
    set(T,Out).
set([H|T],Out) :-
    mymember(H,T),
    set(T,Out).


/**
*
This add is the core of the indexing problem. The assumption here is that
The Ev template is a structure, with a name and a set of arguments.
We will need to have for such structure a unique id, which is not the time, to avoid
duplicates, then we need to dismantle the term Ev in arity, name and attributes in order to index it

The assumption here is that Ev is not a variable.
*/


add(happensAt(Ev,T)):-
		\+ var(Ev),
		functor(Ev,Name,Ar),
		Ev =.. [Name|Arguments],
		index(happensAt(Ev,T),Name, Ar, 1, Arguments, T),
		findall(period(A=V,Ts), terminates_at(A=V,T), ListToAddPeriods),
		findall(period(A=V,T), initiates_at(A=V,T), ListToAddInfPeriods),
		set(ListToAddPeriods, ListOut),
		set(ListToAddInfPeriods,ListOut2),
		addAll(ListToAdd,T),
		addPeriods(ListOut,T),
		addInfPeriods(ListOut2,T),!.

addAll([],T).

addAll([Head|Tail],T):-
		\+ var(Head),
		functor(Head,Name,Ar),
		Head =.. [Name|Arguments],
		%indexer<-store5DPoint(Head, Name, Ar,0, Name ,T),
		index(Head,Name, Ar, 1, Arguments, T),
		addAll(Tail,T).

addAll([H|T],Time):-
		addAll(T,Time).

addPeriods([],T).


addPeriods([Head|Tail],T):-
		Head= period(A=V,_),
		range_query(A=V,[Ts1,inf]),
		delete_point1(A,V,Ts1,inf),
		index(A,V,Ts1,T),
		addPeriods(Tail,T).

addPeriods([Head|Tail],T):-
		addPeriods(Tail,T).

addInfPeriods([],T).

addInfPeriods([H|Tail],T):-
	H= period(A=V,T),
	%text_term(Vtext,V),
	%\+ (range_query(A=V,[Ts1,inf]),Ts1<T),
	\+ holds_at(A=V,T),
	index(A,V,T,inf),
	addInfPeriods(Tail,T).



addInfPeriods([Head|Tail],T):-
		addInfPeriods(Tail,T).




delete_point1(A,V,Ts1,Ts):-
		indexer<-delete_point(A,V,Ts1,Ts).



index(A,V,Ts,inf):-
	%max long here
	text_term(Vtext,V),
	indexer<-storePeriod(A,Vtext,Ts,inf).
	%functor(A,Name,Ar),
	%A =.. [Name|Arguments],
	%Arguments = [Head|Tail],
	%indexer<-storeMDPeriod(A,Name,Ar,Head,Vtext,Ts,inf).

index(A,V,Ts,T):-
	text_term(Vtext,V),
	indexer<-storePeriod(A,Vtext,Ts,T).
	%functor(A,Name,Ar),
	%A =.. [Name|Arguments],
	%Arguments = [Head|Tail],
	%text_term(Vtext,V),
	%indexer<-storeMDPeriod(A,Name,Ar,Head,Vtext,Ts,T).


index(Ev, Name, Ar, Ar, [Head],T):-
	text_term(HeadText,Head),
	indexer<-store5DPoint(Ev, Name, Ar, Ar, HeadText,T).

index(Ev, Name, Ar, Idx, [Head|Tail],T):-
	text_term(HeadText,Head),
	indexer<-store5DPoint(Ev, Name, Ar,Idx, HeadText ,T).
	%NewIdx is Idx +1,
	%index(Ev, Name, Ar, NewIdx, Tail,T).



%only ground for the moment.

intersect_query(A=V,[Ts,Tend], T):-
	ground(A),
	ground(V),
	var(Ts),
	var(Tend),
	text_term(Vtextform,V),
	indexer<-intersect_query0(A,Vtextform,T) returns Iterator, member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List).


intersect_query(A=V,[Ts,Tend], T):-
	ground(A),
	not ground(V),
	var(Ts),
	var(Tend),
	indexer<-intersect_query1(A,T) returns Iterator, member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List).

intersect_query(A=V,[Ts,Tend], T):-
	ground(V),
	not ground(A),
	var(Ts),
	var(Tend),
	text_term(Vtextform,V),
	indexer<-intersect_query2(Vtextform,T) returns Iterator, member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List).

intersect_query(A=V,[Ts,Tend], T):-
	not ground(V),
	not ground(A),
	var(Ts),
	var(Tend),
	text_term(Vtextform,V),
	indexer<-intersect_query3(T) returns Iterator, member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List).

intersect_query_test(A=V,[Ts,Tend], T):-
	not ground(V),
	not ground(A),
	var(Ts),
	var(Tend),
	text_term(Vtextform,V),
	indexer<-intersect_query3(T) returns Iterator, member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List).

range_query(A=V,[Ts,Tend], Tstarsliding, Tendliding):-
    ground(A),
	ground(V),
	var(Ts),
	var(Tend),
	indexer<-range_querySW1(A,V, Tstarsliding, Tendsliding) returns Iterator,
	member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List).


range_query(A=V,[Ts,Tend], Tstarsliding, Tendsliding):-
	\+ ground(A),
	\+ ground(V),
	var(Ts),
	var(Tend),
	indexer<-range_querySW2(Tstarsliding, Tendsliding) returns Iterator,
	member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List).


range_query(A=V,[Ts,Tend], Tstarsliding, Tendsliding):-
	ground(A),
	\+ ground(V),
	var(Ts),
	var(Tend),
	indexer<-range_querySW3(A,Tstarsliding, Tendsliding) returns Iterator,
	member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List).


range_query(A=V,[Ts,Tend], Tstarsliding, Tendsliding):-
	\+ ground(A),
	ground(V),
	var(Ts),
	var(Tend),
	indexer<-range_querySW4(V, Tstarsliding, Tendsliding) returns Iterator,
	member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List).



/*rq_test(A=V,[Ts,inf]):-
	  ground(A),
	  ground(V),
	  var(Ts),
	  indexer<-range_query6(A,V) returns Iterator, member_iterator(M,Iterator),
	  \+ var(M),
	  M<-getCoord(2) returns Ts.
*/

rq_test(A=V,[Ts,Tend]):-
	  ground(A),
	  ground(V),
	  var(Ts),
	  var(Tend),
	  indexer<-range_query0(A,V) returns Iterator, member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List).
	  %\+ var(M).
	  %M<-toString returns MString, text_term(MString, [A,V,Ts,Tend]).
	  %M<-getCoord(3) returns Tend.



range_query(A=V,[Ts,inf]):-
	  ground(A),
	  ground(V),
	  var(Ts),
	  indexer<-range_query6(A,V) returns Iterator, member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List).
	 % \+ var(M),
	 % M<-toString returns MString, text_term(MString, [A,V,Ts,_]).

range_query(A=V, [Ts,Tend]):-
	  ground(A),
	  ground(V),
	  var(Ts),
	   var(Tend),
	  indexer<-range_query0(A,V) returns Iterator, member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List),
 	  Tend \= 9223372036854775807.



range_query(A=V,[Ts,inf]):-
	   \+ ground(A),
	  ground(V),
	  var(Ts),
	  indexer<-range_query7(V) returns Iterator, member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List).


range_query(A=V,[Ts,inf]):-
	   \+ ground(V),
	  ground(A),
	  var(Ts),
	  indexer<-range_query8(A) returns Iterator, member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List).



range_query(A=V, [Ts,Tend]):-
	  \+ ground(A),
	  ground(V),
	  var(Ts),
	  var(Tend),
	  indexer<-range_query1(V) returns Iterator,member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List),
 	  Tend \= 9223372036854775807.

range_query(A=V, [Ts,Tend]):-
	  ground(A),
	  \+ ground(V),
	  var(Ts),
	  var(Tend),
	  indexer<-range_query2(A) returns Iterator,member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List),
	  Tend \= 9223372036854775807.

range_query(A=V, [Ts,Tend]):-
	  \+ ground(A),
	  \+ ground(V),
	  var(Ts),
	  var(Tend), %do I need to query here? I need the whole tree... O(1) + K, so K..
	  indexer<-range_query3 returns Iterator,member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List).


range_query(A=V, [Ts,Tend]):-
	ground(A),
	ground(V),
	\+ var(Ts),
	var(Tend),
	indexer<-range_query4(A,V,Ts) returns Iterator,
	member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List),
 	  Tend \= 9223372036854775807.

range_query(A=V, [Ts,Tend]):-
	ground(A),
	ground(V),
	var(Ts),
	\+ var(Tend),
	Tend \=inf,
	indexer<-range_query5(A,V,Tend) returns Iterator,
	member_iterator_special(20,Iterator, List),member([A,V,Ts,Tend],List).


retrieve_range(Ev, Name,Ar,Idx,[Head|Tail], Thappens,[Ts,Tend]):-
			var(Head),
			indexer<-range_query(Name,Ar,Idx, Ts,Tend) returns Iterator,
			member_iterator_special(20,Iterator,List),
			member([_,_,_,_,_,happensAt(Ev,Thappens)],List).


retrieve_range(Ev,Name,Ar,Idx,[Head|Tail],Thappens, [Ts,Tend]):-
			\+ var(Head),
			text_term(HeadText,Head),
			indexer<-range_query(Name,Ar,Idx,HeadText, Ts,Tend) returns Iterator,
			member_iterator_special(20,Iterator,List),
			member([_,_,_,_,_,happensAt(Ev,Thappens)],List).




retrieve_arguments(Ev, Name, Ar, Ar1, [], Thappens,Tcall):- Ar1 is Ar+1.



nearest_event(Ev, Name,Ar,Idx,[Head|Tail], Thappens,Tcall):-
			var(Head),
			indexer<-retrieve_point(Name,Ar,Idx,Tcall) returns Point, \+ var(Point),
			text_term(Point, [_,_,_,_,_,happensAt(Ev,Thappens)]).


nearest_event(Ev, Name,Ar,Idx,[Head|Tail], Thappens,Tcall):-
			\+ var(Head),
			text_term(HeadText,Head),
			indexer<-retrieve_point(Name,Ar,Idx,HeadText,Tcall) returns Point, \+ var(Point),
			text_term(Point, [_,_,_,_,_,happensAt(Ev,Thappens)]).




optimize:- indexer<-optimize.

showTree:- indexer<-printTrees.


member_iterator_special(Number,Iterator,List):-
	Iterator<-hasNext,
	Iterator<-nextMulti(Number) returns ListString,
	text_term(ListString,List).

member_iterator_special(Number,Iterator,List):-
	Iterator<-hasNext,
	member_iterator_special(Number, Iterator,List).



member_iterator(M, Iterator):-
	Iterator<-hasNext,
	Iterator<-next returns M.

member_iterator(M, Iterator):-
	Iterator<-hasNext,
	member_iterator(M, Iterator).



member_iterator_for(M, Iterator):-
	Iterator<-hasNext,
	preprocess(Iterator,3,List),
	member(M,List).

member_iterator_for(M, Iterator):-
	Iterator<-hasNext,
	member_iterator_for(M, Iterator).



preprocess(Iterator, 0, List, ListUtil):-
			ListUtil<-toString returns ListText, ListUtil<-clear,
			text_term(ListText,List).

preprocess(Iterator, N, List):-
	java_object('java.util.LinkedList', [], ListUtil),
	preprocess(Iterator,N,List,ListUtil).

preprocess(Iterator, N, List):-
	Iterator<-hasNext,
	preprocess(Iterator, N, List).

preprocess(Iterator,N,List,ListUtil):-
	Iterator<-hasNext,
	Iterator<-next returns El,
	ListUtil<-add(El),
	Nnew is N-1,
	preprocess(Iterator,Nnew,List,ListUtil).


preprocess(Iterator,N,List,ListUtil):-
	java_call(ListUtil,toString,ListText), ListUtil<-clear,
	text_term(ListText,List).



compare(point(Type,ID,Attr,Val,T1),Member):-
	Member<-getCoord(0) returns IDM, text_term(IDM,ID),
	Member<-getCoord(1) returns AttrM,text_term(AttrM,Attr),
	Member<-getCoord(2) returns ValM,text_term(ValM,Val),
	Member<-getCoord(3) returns TM,text_term(TM,T1),
	Member<-getCoord(4) returns TypeM, text_term(TypeM,Type).


compare(point(Type,ID,Class,T1),Member):-
	Member<-getCoord(0) returns IDM, text_term(IDM,ID),
	Member<-getCoord(1) returns ClassM,text_term(ClassM,Class),
	Member<-getCoord(2) returns TM,text_term(TM,T1),
	Member<-getCoord(3) returns TypeM, text_term(TypeM,Type).