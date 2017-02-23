% In order to specify the predicates with initiates and terminates it is necessary, due to how the
% indexing works to always enclose arguments within a predicate as follows:
% holds_at(alert(N)=predicatename(Argument),T)
% The indexing uses the predicate, so it would not work otherwise.

% perceive/2 and act/2 are the predicates called by the PrologAgentMind
perceive(P,T):-
    (P=glucose(Value),
    add(happensAt(glucose(Value),T)));
    (P=weight(Value),
    add(happensAt(weight(Value),T)));
    (P=blood_pressure(Sys,Dias),
	add(happensAt(blood_pressure(Sys,Dias),T)));
	(P=heart_rate(Value),
	add(happensAt(heart_rate(Value),T))).

act(A,T):-
    holds_at(alert(Number)=situation(Something),T),
    A = Something,
    not Something = no_alert,
    revise_me(A,Number,T).


revise_me(A,Number,T):-!,
    A = Something,
    add(happensAt(alert(Number,Something),T)).

initiates_at(alert(Number)=situation(no_alert),T):-
    holds_at(alert(Number)=situation(Something),T),
    happens_at(alert(Number,Something),T).

terminates_at(alert(Number)=situation(V1),T):-
    holds_at(alert(Number)=situation(V1),T),
    initiates_at(alert(Number)=situation(V2),T),
    not V2 = V1.

% more_or_equals_to/2 is used with the complex rules to count ocurrences of events in a time window
more_or_equals_to(Number,Expr):-
	findall(_,Expr,List),
	length(List,Val),
	Val >= Number.