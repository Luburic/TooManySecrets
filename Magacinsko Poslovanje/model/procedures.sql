CREATE PROCEDURE ProknjiziPopis
(
	@Id int,
	@Datum char(10)
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
    UPDATE Popisni_dokument SET status_popisnog = 'P', datum_knjizenja=@Datum WHERE id_popisnog_dokumenta=@Id
  END
GO