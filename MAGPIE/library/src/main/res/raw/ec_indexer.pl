add(happensAt(Ev,T)):-
    not var(Ev),
    functor(Ev,Name,Arity),
    Ev =.. [Name|Arguments],
    index(happensAt(Ev,T), Name, Arity, Arguments, T),
    findall(period(A=V,Ts), terminates_at(A=V,T), ListTerminatesPeriods),
    findall(period(A=V,T), initiates_at(A=V,T), ListInitiatesPeriods),
    set(ListTerminatesPeriods,SetTerminatesPeriods),
    set(ListInitiatesPeriods,SetInitiatesPeriods),
    addTerminatesPeriods(SetTerminatesPeriods,T),
    addInitiatesPeriods(SetInitiatesPeriods,T),!.

addTerminatesPeriods([],T).

addTerminatesPeriods([Head|Tail],T):-
    Head=period(A=V,_),
    range_query(A=V,[Tstart,infinite]),
    delete_period(A,V,Tstart,infinite),
    index(A,V,Tstart,T),
    addTerminatesPeriods(Tail,T).

addTerminatesPeriods([Head|Tail],T):-
    addTerminatesPeriods(Tail,T).

addInitiatesPeriods([],T).

addInitiatesPeriods([Head|Tail],T):-
    Head=period(A=V,T),
    not holds_at(A=V,T),
    index(A,V,T,infinite),
    addInitiatesPeriods(Tail,T).

addInitiatesPeriods([Head|Tail],T):-
    addInitiatesPeriods(Tail,T).

delete_period(A,V,Tstart,Tend):-
    indexer<-deletePeriod(A,V,Tstart,Tend).

% Predicates index/4 are to store periods
index(A,V,Tstart,infinite):-
    text_term(Vtext,V),
    indexer<-storePeriod(A,Vtext,Tstart,infinite).

index(A,V,Tstart,Tend):-
    text_term(Vtext,V),
    indexer<-storePeriod(A,Vtext,Tstart,Tend).


% Predicates index/5 are to store events
index(Ev, Name, Arity, [Head], T):-
    text_term(HeadText, Head),
    indexer<-store4DPoint(Ev, Name, Arity, HeadText, T).

index(Ev, Name, Arity, [Head|Tail], T):-
    text_term(HeadText, Head),
    indexer<-store4DPoint(Ev, Name, Arity, HeadText, T).

range_query(A=V,[Tstart,infinite]):-
    ground(A),
    ground(V),
    var(Tstart),
    indexer<-queryOpenPeriods(A,V) returns Iterator,
    member_iterator_special(20,Iterator,List),
    member([A,V,Tstart,Tend],List).

% Predicates intersect_query/3 are related to mholds_for/2
% The goal is to find periods comprised between Tstart and Tend, where the timestamp T is within these periods
intersect_query(A=V,[Tstart,Tend],T):-
    ground(A),
    ground(V),
    var(Tstart),
    var(Tend),
    text_term(Vtext,V),
    indexer<-retrievePeriodsIntersectingT(A,Vtext,T) returns Iterator,
    member_iterator_special(20,Iterator,List),
    member([A,V,Tstart,Tend],List).

intersect_query(A=V,[Tstart,Tend],T):-
    ground(A),
    not ground(V),
    var(Tstart),
    var(Tend),
    indexer<-retrievePeriodsIntersectingT(A,T) returns Iterator,
    member_iterator_special(20,Iterator,List),
    member([A,V,Tstart,Tend],List).

intersect_query(A=V,[Tstart,Tend],T):-
    not ground(A),
    not ground(V),
    var(Tstart),
    var(Tend),
    indexer<-retrievePeriodsIntersectingT(T) returns Iterator,
    member_iterator_special(20,Iterator,List),
    member([A,V,Tstart,Tend],List).


% Predicates retrieve_range/6 are related to happens_at/2
% The goal is to find those events happening within a time window comprised between WTstart and WTend
retrieve_range(Ev, Name, Arity, [Head|Tail], Thappens, [WTstart, WTend]):-
    var(Head),
    indexer<-retrieveEventsInRange(Name, Arity, WTstart, WTend) returns Iterator,
    member_iterator_special(20,Iterator,List),
    member([_,_,_,_,happensAt(Ev,Thappens)],List).

retrieve_range(Ev, Name, Arity, [Head|Tail], Thappens, [WTstart, WTend]):-
    not var(Head),
    text_term(HeadText,Head),
    indexer<-retrieveEventsInRange(Name, Arity, HeadText, WTstart, WTend) returns Iterator,
    member_iterator_special(20,Iterator,List),
    member([_,_,_,_,happensAt(Ev,Thappens)],List).


% Predicates set/2 and mymember/2 are used to remove duplicates in a list
mymember(X,[X|_]).
mymember(X,[_|T]) :- mymember(X,T).

set([],[]).
set([H|T],[H|Out]) :-
    not(mymember(H,T)),
    set(T,Out).
set([H|T],Out) :-
    mymember(H,T),
    set(T,Out).

member_iterator_special(Number,Iterator,List):-
    Iterator<-hasNext,
    Iterator<-getListOfPoints(Number) returns ListString,
    text_term(ListString,List).

member_iterator_special(Number,Iterator,List):-
    Iterator<-hasNext,
    member_iterator_special(Number,Iterator,List).