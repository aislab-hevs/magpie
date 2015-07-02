add(holdsAtSDFluent( F=Value,  Time )):-
	F =.. [Name|Args],
	append(Args,[Value],ArgsNew),
	NewF =.. [Name|ArgsNew],
	add(happensAt(NewF,Time)).


holdsForSI(A=V,Result, between(T1,T2)):-
	\+ var(T1),
	\+ var(T2),
	query_kd(mholds_for(A=V, Result), [T1,T2]).
	%((Period = [Ts,inf], Result = since(Ts));
	%(Period = [Ts,9223372036854775807], Result = since(Ts));
	%(Period=[Tstart,Tend], Tend \= 9223372036854775807, Tend\=inf, Result = Period)).


holdsForSI(A=V,Result):-
	query_kdt(mholds_for(A=V,Result)).
	%((Period = [Ts,inf], Result = since(Ts));(Period = [Ts,9223372036854775807], Result = since(Ts)); (Period=[Tstart,Tend], Tend \= 9223372036854775807, Tend\=inf, Result = Period)).


holdsFor(U,PeriodList):-
	findall(Period, holdsForSI(U,Period), PeriodList).


%holdsAt(U,T):-
 %\+ ground(U),
  %holdsForSI(U,[Ts,Tend]),println(here(holdsForSI(U,[Ts,Tend]), T)), T>Ts, T=< Tend.


holds_at(F=V,T):-
    query_kd(mholds_for(F=V,[Ts,Tend]), T),
    T > Ts,
    T<Tend. %this simply finds a period that intersects T, where Ts is a near as possible to T.


%for queries
query_kdt(mholds_for(F=V,[Ts,Tend])):-
				rq_test(F=V, [Ts,Tend]).


%returns only one happens, the nearest to T.
query_kd(happens_at(Ev,Ts),T):-
		var(Ts),
		\+ var(Ev),
		number(T),
		functor(Ev,Name,Ar),
		Ev =.. [Name|Arguments],
		nearest_event(Ev, Name, Ar, 1, Arguments, Ts, T).


happens_at(Ev,Ts):-
	query_kd(happens_at(Ev,_), [Ts,Ts]).


%returns the happens in the window.
query_kd(happens_at(Ev,Ts), [WTstart, WTend]):-
 		var(Ts),
		\+ var(Ev),
 		functor(Ev,Name,Ar),
		Ev =.. [Name|Arguments],
		retrieve_range(Ev,Name, Ar, 1, Arguments, Ts, [WTstart, WTend]).


%returns all the periods
query_kd(mholds_for(F=V,[Ts,Tend])):-
				range_query(F=V, [Ts,Tend]).


%returns the periods intersecting T?
query_kd(mholds_for(F=V,[Ts,Tend]),T):-
			%to be defined.
			not var(T),
			number(T),
			intersect_query(F=V,[Ts,Tend],T). %println(working(F=V,[Ts,Tend],T)).


%returns all the periods INTERSECTING the time window
query_kd(mholds_for(F=V,[Ts,Tend]),[WTstart, WTend]):-
					range_query(F=V, [Ts,Tend],WTstart, WTend).


%%%%%  fluent atoms %%%%%%%%

fluent_atom(F) :- fluent_constant(F), \+ dom(F,_).
fluent_atom(F=_) :- fluent_constant(F), \+ \+ dom(F,_).
fluent_atom(F=_) :- fluent_constant(F,_Dom).

fluent_list(FluentList) :-
   fluent_atom(_), !,
   findall(Atom, fluent_atom(Atom), FluentList).
fluent_list([_]).


%%%%%%%  top level (show history)

show_history :-
  findall(T,happensAt(_,T),List),
  max_list(List,N),
  Max is N + 1,
  show_history(Max).


show_history(Max) :- show_history(Max,event).


% mode can be 'step' or 'event'
show_history(Max,Mode) :-
   timepointlist(Max,0,Max,[],Times,Mode),
   fluent_list(FluentList),  % generates fluents in a fixed order
    member(T, Times),
   format('time = ~w', [T]), nl,
   show_holds(FluentList,T),
   write('***'),nl,
   show_happens(T),
   fail.
show_history(_,_).

show_holds(FluentList,T) :-
  member(U,FluentList),
  holdsAt(U,T),
  format('~q  ', [U]),
  fail.
show_holds(_FluentList,T) :-
  show_what_user_wants(T).    % user defines
show_holds(_,_) :- nl.


% for backward compatability:

show_holds(T) :-
   fluent_list(FluentList),
   show_holds(FluentList,T).

show_happens(T) :-
  happens2(E,T),
  format('  ~q', [E]), nl,
  fail.
show_happens(_) :- nl.

timepointlist(Min,Min,_,X,[Min|X],_) :- !.
timepointlist(N,Min,Max,X,Final,Mode) :-
  N > 0,
  M is N - 1,
  (significant_timepoint(Mode, Max, N) -> Y = [N|X]; Y = X),
  timepointlist(M,Min,Max, Y, Final,Mode).


significant_timepoint(step, _, _).
significant_timepoint(event, Max, Max).
significant_timepoint(event, _, N) :-
   happensAt(_,N).