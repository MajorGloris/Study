UPDATE Silver
SET
      Name = (SELECT KanjiRTK.field2
                            FROM KanjiRTK
                            WHERE KanjiRTK.field1 = Silver.Character )
    
WHERE
    EXISTS (
        SELECT *
        FROM KanjiRTK
        WHERE KanjiRTK.field1 = Silver.Character
    )