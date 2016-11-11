hours_ago(Num,Tago,Tfinal):-
    Tago is Tfinal - Num * 3600 * 1000.

within_hours(Num,Tev,Tfinal):-
    Tinit is Tfinal - Num * 3600 * 1000,
    Tev =< Tfinal,
	Tev >= Tinit.

days_ago(Num,Tago,Tfinal):-
    Tago is Tfinal - Num * 24 * 3600 * 1000.

within_days(Num,Tev,Tfinal):-
    Tinit is Tfinal - Num * 24 * 3600 * 1000,
    Tev =< Tfinal,
	Tev >= Tinit.

weeks_ago(Num,Tago,Tfinal):-
    Tago is Tfinal - Num * 7 * 24 * 3600 * 1000.

within_weeks(Num,Tev,Tfinal):-
    Tinit is Tfinal - Num * 7 * 24 * 3600 * 1000,
    Tev =< Tfinal,
	Tev >= Tinit.