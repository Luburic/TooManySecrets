CREATE PROCEDURE ProknjiziPopis
(
	@Id int,
	@Datum char(10)
	@RetVal int OUTPUT
)
AS
DECLARE @count int
SELECT @count = 0
SELECT @count = COUNT(*) FROM Popisni_dokument WHERE id_popisnog_dokumenta = @Id

IF(@count = 0)
  BEGIN
    PRINT 'Ne postoji dati dokument'
  END
ELSE
  BEGIN
	SELECT @count = COUNT(*) FROM Stavka_popisa WHERE id_popisnog_dokumenta = @Id AND popisana_kolicina = NULL
	IF(@count = 0)
		BEGIN
			@RetVal = 1
			PRINT 'Popisni dokument se može proknjižiti samo kada se unesu sve količine artikla.'
		END
	ELSE
		BEGIN
			@RetVal = 0
			UPDATE Popisni_dokument SET status_popisnog = 'proknjizen', datum_knjizenja=@Datum WHERE id_popisnog_dokumenta=@Id
		END
  END
GO

CREATE PROCEDURE ZakljuciGodinu
(
	@Id int
	@RetVal int OUTPUT
)
AS
DECLARE @count int
SELECT @count = 0
SELECT @count = COUNT(*) FROM Poslovna_godina WHERE id_poslovne_godine = @Id

IF(@count = 0)
  BEGIN
    PRINT 'Ne postoji dati dokument'
  END
ELSE
  BEGIN
	SELECT @count = COUNT(*) FROM Poslovna_godina WHERE zakljucena = '0' AND id_poslovne_godine <> @Id
	IF(@count = 0)
		BEGIN
			PRINT 'Mora se otvoriti nova poslovna godina pre nego što se stara može zaključiti.'
			@RetVal = 1
		END
	ELSE
		BEGIN
			SELECT @count = COUNT(*) FROM Poslovna_godina god JOIN Popisni_dokument pop ON god.id_poslovne_godine = pop.id_poslovne_godine
			JOIN Prometni_dokument pro ON god.id_poslovne_godine = pro.id_poslovne_godine WHERE id_poslovne_godine = @Id AND (pop.status_popisnog = 'u fazi formiranja' OR pro.status_prometnog = 'u fazi formiranja')
			IF(@count = 0)
				BEGIN
					PRINT 'U godini koja se zaključuje ne sme biti dokumenata u fazi formiranja.'
					@RetVal = 2
				END
			ELSE
				BEGIN
					@RetVal = 0
					UPDATE Poslovna_godina SET zakljucena = '1' WHERE id_poslovne_godine =@Id
				END
		END
  END
GO
