CREATE TRIGGER KreirajPopis ON Popisni_dokument FOR INSERT
AS
BEGIN TRANSACTION
	DECLARE
		@Id_popisa int,
		@Id_jedinice int,
		@Id_godine int,
		@id_artikla int,
		@kolicina_pocetna numeric(12),
		@kolicina_ulaza numeric(12),
		@kolicina_izlaza numeric(12),
		@cena decimal(15,2)

	SELECT @Id_popisa = id_popisnog_dokumenta, @Id_jedinice = id_jedinice, @Id_godine = id_poslovne_godine FROM INSERTED

	DECLARE cursor_popis CURSOR FOR SELECT id_artikla, kolicina_pocetnog_stanja, kolicina_ulaza, kolicina_izlaza, prosecna_cena FROM Magacinska_kartica WHERE id_jedinice = @Id_jedinice AND id_poslovne_godine = @Id_godine
	OPEN cursor_popis
	FETCH NEXT FROM cursor_popis INTO @id_artikla, @kolicina_pocetna, @kolicina_ulaza, @kolicina_izlaza, @cena
	WHILE @@FETCH_STATUS = 0
	BEGIN
		INSERT INTO Stavka_popisa VALUES (@id_artikla, @Id_popisa, null, @kolicina_pocetna+@kolicina_ulaza-@kolicina_izlaza, @cena, 1)
		FETCH NEXT FROM cursor_popis INTO @id_artikla, @kolicina_pocetna, @kolicina_ulaza, @kolicina_izlaza, @cena
	END
COMMIT TRANSACTION
GO

CREATE TRIGGER ObrisiPopis ON Popisni_dokument FOR DELETE
AS
BEGIN TRANSACTION
	DECLARE @Id_popisa int

	SELECT @Id_popisa = id_popisnog_dokumenta FROM DELETED

	DELETE FROM Stavka_popisa WHERE id_popisnog_dokumenta = @Id_popisa
	DELETE FROM Clan_komisije WHERE id_popisnog_dokumenta = @Id_popisa
COMMIT TRANSACTION
GO
