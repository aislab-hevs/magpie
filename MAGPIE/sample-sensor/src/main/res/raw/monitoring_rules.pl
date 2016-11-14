% Example of a sequential rule
initiates_at(alert(first)=situation('Brittle diabetes'),T):-
        hours_ago(6,Tago,T),
        not query_kd(happens_at(alert(first,'Brittle diabetes'),Tev0), [Tago, T]),
        query_kd(happens_at(glucose(Value1),Tev1), [Tago, T]),
        query_kd(happens_at(glucose(Value2),Tev2), [Tago, T]),
        Value1 =< 3.8,
        Value2 >= 8,
        Tev2 > Tev1.

% Example of a complex rule
initiates_at(alert(second)=situation('Pre-hypertension'),T):-
        weeks_ago(1,Tago,T),
        not query_kd(happens_at(alert(second,'Pre-hypertension'),Tev0), [Tago, T]),
        more_or_equals_to(2,(
                query_kd(happens_at(blood_pressure(Sys,Dias),Tev), [Tago, T]),
                Sys >= 130,
                Dias >= 80,
                within_weeks(1,Tev,T)
        )).

initiates_at(alert(third)=situation('Gaining weight'),T):-
        weeks_ago(1,Tago,T),
        not query_kd(happens_at(alert(third,'Gaining weight'),Tev0), [Tago, T]),
        query_kd(happens_at(weight(Value1),Tev1), [Tago, T]),
        query_kd(happens_at(weight(Value2),Tev2), [Tago, T]),
        Value1 =< 93.7,
        Value2 >= 94.6,
        Tev2 > Tev1.

initiates_at(alert(fourth)=situation('DM treatment is not effective'),T):-
	    weeks_ago(4,Tago,T),
        not query_kd(happens_at(alert(fourth,'DM treatment is not effective'),Tev0), [Tago, T]),
	    more_or_equals_to(2,(
		        query_kd(happens_at(glucose(Value1),Tev1), [Tago, T]),
		        Value1 >= 10,
		        within_weeks(4,Tev1,T)
	    )),
        more_or_equals_to(1,(
                query_kd(happens_at(weight(Value2),Tev2), [Tago, T]),
                Value2 >= 87,
                within_weeks(4,Tev2,T)
        )).