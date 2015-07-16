initiates_at(alert(first)=situation('DM treatment is not efective'),T):-
	    last_two_weeks_ago(Tago,T),
        not query_kd(happens_at(alert(first,'DM treatment is not efective'),Tev0), [Tago, T]),
	    more_or_equals_to(2,(
		        query_kd(happens_at(glucose(Value1),Tev1), [Tago, T]),
		        Value1 >= 10
	    )),
        more_or_equals_to(1,(
                query_kd(happens_at(weight(Value2),Tev2), [Tago, T]),
                Value2 >= 87
        )).

initiates_at(alert(second)=situation('Brittle diabetes'),T):-
        six_hours_ago(Tago,T),
        not query_kd(happens_at(alert(second,'Brittle diabetes'),Tev0), [Tago, T]),
        query_kd(happens_at(glucose(Value1),Tev1), [Tago, T]),
        query_kd(happens_at(glucose(Value2),Tev2), [Tago, T]),
        Value1 =< 3.8,
        Value2 >= 8,
        Tev2 > Tev1.

initiates_at(alert(third)=situation('pre-hypertension, consider lifestyle modification'),T):-
        more_or_equals_to(2,(
                last_week_ago(Tago,T),
                not query_kd(happens_at(alert(third,'pre-hypertension, consider lifestyle modification'),Tev0), [Tago, T]),
                query_kd(happens_at(blood_pressure(Sys,Dias),Tev), [Tago, T]),
                Sys >= 130,
                Dias >= 80
        )).