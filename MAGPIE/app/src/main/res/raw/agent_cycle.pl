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
	    add(happensAt(blood_pressure(Sys,Dias),T))).

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
      holds_at(alert(Number)=situation(V1),T), println(terminating1),
      initiates_at(alert(Number)=situation(V2),T), println(terminating2),
      not V2 =V1, println(terminating3(V2,V1)).



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
/*
initiates_at(alert(first)='DM treatment is not efective',T):-
	more_or_equals_to(2,(
		happens_at(glucose(Value1),Tev1),
		Value1 >= 10,
		last_two_weeks(Tev1,T)
	)),
        more_or_equals_to(1,(
                happens_at(weight(Value2),Tev2),
                Value2 >= 80,
                last_two_weeks(Tev2,T)
    )),
    findall(Tev1,(happens_at(glucose(Value1),Tev1),Value1 >= 10),List1),
	findall(Tev2,(happens_at(weight(Value2),Tev2),Value2 >= 80),List2),
	(member(T,List1);
	member(T,List2)).
*/

initiates_at(alert(second)=situation('Brittle diabetes'),T):-
        six_hours_ago(Tago,T),
        query_kd(happens_at(glucose(Value1),Tev1), [Tago, T]),
        query_kd(happens_at(glucose(Value2),Tev2), [Tago, T]),
        Value1 =< 3.8,
        Value2 >= 8,
        Tev2 > Tev1.

initiates_at(alert(third)=situation('pre-hypertension, consider lifestyle modification'),T):-
        more_or_equals_to(2,(
                last_week_ago(Tago,T),
                query_kd(happens_at(blood_pressure(Sys,Dias),Tev), [Tago, T]),
                Sys >= 130,
                Dias >= 80
        )).

/*
initiates_at(alert(fourth)='Gaining weight',T):-
        happens_at(weight(Value1),Tev1),
        happens_at(weight(Value2),Tev2),
        Value1 =< 93.7,
        Value2 >= 94.6,
        Tev2 > Tev1,
        last_week(Tev1,Tev2),
        Tev2 = T.
*/
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

more_or_equals_to(Number,Expr):-
	findall(_,Expr,List),
	length(List,Val),
	Val >= Number.

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

last_two_weeks(Tev,Tfinal):-
        Tinit is Tfinal - 2 * 7 * 24 * 3600 * 1000,
        Tev =< Tfinal,
	Tev >= Tinit.