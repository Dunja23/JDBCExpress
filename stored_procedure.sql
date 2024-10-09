CREATE PROCEDURE GrantCourierRequest
    @Username NVARCHAR(100)
AS
BEGIN

    DECLARE @IdKor VARCHAR(100), @IdVoz VARCHAR(100);

    SELECT @IdKor = k.KorIme, @IdVoz = zk.RegBr
    FROM Korisnik k
    JOIN ZahtevKurir zk ON k.KorIme = zk.KorIme
    WHERE k.KorIme = @Username;

    IF @IdKor IS NULL OR @IdVoz IS NULL
    BEGIN
        RAISERROR('Request does not exist for the given username.', 16, 1);
        RETURN;
    END


    IF EXISTS (SELECT 1 FROM Kurir WHERE RegBr = @IdVoz)
    BEGIN
        RAISERROR('Vehicle is already assigned to another courier.', 16, 1);
        RETURN;
    END
     
    INSERT INTO Kurir (KorIme, BrIsporucenihPaketa, Profit, Status, RegBr)
    VALUES (@IdKor, 0, 0, 0, @IdVoz);

    DELETE FROM ZahtevKurir WHERE KorIme = @IdKor AND RegBr = @IdVoz;
END
GO
