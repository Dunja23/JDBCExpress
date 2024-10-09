CREATE TRIGGER trg_DeleteOffersOnPackageInsert
ON Paket
AFTER UPDATE
AS
BEGIN
    DECLARE @IdZP INT;
	DECLARE @Vreme DATE;

    DECLARE @cursor CURSOR 
	
	SET @cursor = CURSOR FOR
    SELECT IdZP, VremePrihv
    FROM inserted;

    OPEN @cursor;

    FETCH NEXT FROM @cursor INTO @IdZP, @Vreme;

    WHILE @@FETCH_STATUS = 0
    BEGIN

        DELETE FROM Ponuda
        WHERE IdZP = @IdZP and @Vreme IS NOT NULL;

        FETCH NEXT FROM @cursor INTO @IdZP, @Vreme;
    END;

    CLOSE @cursor;
    DEALLOCATE @cursor;
END;
GO

