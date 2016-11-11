holds_at(F=V,T):-
    query_kd(mholds_for(F=V,[Tstart,Tend]),T),
    T>Tstart,
    T<Tend.

happens_at(Ev,T):-
    query_kd(happens_at(Ev,_), [T,T]).

query_kd(mholds_for(F=V,[Tstart,Tend]),T):-
    not var(T),
    number(T),
    intersect_query(F=V,[Tstart,Tend],T).

query_kd(happens_at(Ev,T), [WTstart, WTend]):-
    var(T),
    not var(Ev),
    functor(Ev,Name,Arity),
    Ev=.. [Name|Arguments],
    retrieve_range(Ev, Name, Arity, Arguments, T, [WTstart, WTend]).