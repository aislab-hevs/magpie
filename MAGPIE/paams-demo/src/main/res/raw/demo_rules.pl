% Glucose
initiates_at(alert(first)=situation('brittle diabetes'),T):-
        hours_ago(6,Tago,T),
        not query_kd(happens_at(alert(first,'brittle diabetes'),Tev0), [Tago, T]),
        query_kd(happens_at(glucose(Value1),Tev1), [Tago, T]),
        query_kd(happens_at(glucose(Value2),Tev2), [Tago, T]),
        Value1 =< 3.8,
        Value2 >= 8,
        Tev2 > Tev1.

% Blood Pressure
initiates_at(alert(second)=situation('pre-hypertension'),T):-
        weeks_ago(1,Tago,T),
        not query_kd(happens_at(alert(second,'Pre-hypertension'),Tev0), [Tago, T]),
        more_or_equals_to(2,(
                query_kd(happens_at(blood_pressure(Sys,Dias),Tev), [Tago, T]),
                ((Sys >= 120, Sys =< 139);(Dias >= 80, Dias =< 89)),
                within_weeks(1,Tev,T)
        )).

% Weight
initiates_at(alert(third)=situation('gaining weight'),T):-
        weeks_ago(1,Tago,T),
        not query_kd(happens_at(alert(third,'gaining weight'),Tev0), [Tago, T]),
        query_kd(happens_at(weight(Value1),Tev1), [Tago, T]),
        query_kd(happens_at(weight(Value2),Tev2), [Tago, T]),
        Value1 =< 78.4,
        Value2 >= 79.2,
        Tev2 > Tev1.
