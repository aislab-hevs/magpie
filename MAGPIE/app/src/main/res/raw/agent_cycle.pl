%
% in order to specify the predicates with initiates and terminates it is
% necessary, due to how the indexing works to always enclose arguments
% wihin a predicate as follows holds_at(alert(N)=predicatename(Argument),T)
% The indexing uses the predicate, so it would not work otherwise.
%

perceive(P,T):-
        (P=glucose(Value),
        add(happensAt(glucose(Value),T)));
        (P=weight(Value),
        add(happensAt(weight(Value),T)));
        (P=blood_pressure(Sys,Dias),
	    add(happensAt(blood_pressure(Sys,Dias),T)));
	    (P=heart_rate(Value),
	    add(happensAt(heart_rate(Value),T))).

% The ID of the patient is in the environment

act(A,T):-
	    holds_at(alert(Number)=situation(Something),T),
        A = act(produce_alert(Number,Something)),
        not Something = no_alert,
        revise_me(A,T).


revise_me(A,T):-!,
        A = act(produce_alert(Number,Something)),
        add(happensAt(alert(Number,Something),T)).

initiates_at(alert(Number)=situation(no_alert),T):-
        holds_at(alert(Number)=situation(Something),T),
        happens_at(alert(Number,Something),T).


terminates_at(alert(Number)=situation(V1),T):-
        holds_at(alert(Number)=situation(V1),T),
        initiates_at(alert(Number)=situation(V2),T),
        not V2 =V1.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

more_or_equals_to(Number,Expr):-
	    findall(_,Expr,List),
	    length(List,Val),
	    Val >= Number.

last_minute_ago(Tago,Tfinal):-
        Tago is Tfinal - 60 * 1000.

last_six_hours(Tev,Tfinal):-
        Tinit is Tfinal - 6 * 3600 * 1000,
        Tev =< Tfinal,
        Tev >= Tinit.

six_hours_ago(Tago,Tfinal):-
        Tago is Tfinal - 6 * 3600 * 1000.

last_day(Tev,Tfinal):-
        Tinit is Tfinal - 24 * 3600 * 1000,
	    Tev =< Tfinal,
	    Tev >= Tinit.

last_week(Tev,Tfinal):-
        Tinit is Tfinal - 7 * 24 * 3600 * 1000,
        Tev =< Tfinal,
	    Tev >= Tinit.

last_week_ago(Tago,Tfinal):-
        Tago is Tfinal - 7 * 24 * 3600 * 1000.

last_two_weeks_ago(Tago,Tfinal):-
        Tago is Tfinal - 14 * 24 * 3600 * 1000.

last_two_weeks(Tev,Tfinal):-
        Tinit is Tfinal - 2 * 7 * 24 * 3600 * 1000,
        Tev =< Tfinal,
	    Tev >= Tinit.