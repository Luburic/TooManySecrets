IF EXISTS (SELECT name
   FROM   sysobjects
   WHERE  name = 'ProknjiziPopis' AND type = 'P')
   DROP PROCEDURE ProknjiziPopis
GO

IF EXISTS (SELECT name
   FROM   sysobjects
   WHERE  name = 'ProknjiziPromet' AND type = 'P')
   DROP PROCEDURE ProknjiziPromet
GO

IF EXISTS (SELECT name
   FROM   sysobjects
   WHERE  name = 'ZakljuciGodinu' AND type = 'P')
   DROP PROCEDURE ZakljuciGodinu
GO

CREATE PROCEDURE ProknjiziPopis
(
	@Id int,
	@Datum char(10),
	@RetVal int OUTPUT
)
AS
DECLARE @count int
SET @count = 0
SELECT @count = COUNT(*) FROM Popisni_dokument WHERE id_popisnog_dokumenta = @Id AND status_popisnog = 'u fazi formiranja'
IF(@count = 0)
  BEGIN
	SET @RetVal = 1
    PRINT 'Nije dozvoljeno knjiženje dokumenta koji nije u fazi formiranja.'
	RETURN
  END
  
SELECT @count = COUNT(*) FROM Stavka_popisa WHERE id_popisnog_dokumenta = @Id AND popisana_kolicina = ''
IF(@count <> 0)
	BEGIN
		SET @RetVal = 2
		PRINT 'Popisni dokument se može proknjižiti samo kada se unesu sve količine artikla.'
	END
ELSE
	BEGIN
		SET @RetVal = 0
		UPDATE Popisni_dokument SET status_popisnog = 'proknjizen', datum_knjizenja=@Datum WHERE id_popisnog_dokumenta=@Id
	END
GO

CREATE PROCEDURE ZakljuciGodinu
(
	@Id int,
	@Id_preduzeca int,
	@RetVal int OUTPUT
)
AS
DECLARE @count int
SELECT @count = 0
SELECT @count = COUNT(*) FROM Poslovna_godina WHERE id_poslovne_godine = @Id AND zakljucena = 0

IF(@count = 0)
  BEGIN
	SET @RetVal = 1
    PRINT 'Data godina ne postoji ili je već zaključena.'
	RETURN
  END

SELECT @count = COUNT(*) FROM Poslovna_godina WHERE zakljucena = 0 AND id_poslovne_godine <> @Id AND id_preduzeca = @Id_preduzeca
IF(@count = 0)
	BEGIN
		PRINT 'Mora se otvoriti nova poslovna godina pre nego što se stara može zaključiti.'
		SET @RetVal = 2
	END
ELSE
	BEGIN
		SELECT @count = COUNT(*) FROM Poslovna_godina god JOIN Popisni_dokument pop ON god.id_poslovne_godine = pop.id_poslovne_godine
		JOIN Prometni_dokument pro ON god.id_poslovne_godine = pro.id_poslovne_godine WHERE id_poslovne_godine = @Id AND (pop.status_popisnog = 'u fazi formiranja' OR pro.status_prometnog = 'u fazi formiranja')
		IF(@count <> 0)
			BEGIN
				PRINT 'U godini koja se zaključuje ne sme biti dokumenata u fazi formiranja.'
				SET @RetVal = 3
			END
		ELSE
			BEGIN
				SET @RetVal = 0
				UPDATE Poslovna_godina SET zakljucena = '1' WHERE id_poslovne_godine =@Id
			END
	END
GO

CREATE PROCEDURE ProknjiziPromet
(
	@Id int,
	@Id_godine int,
	@Id_magacina int,
	@Id_drugog_magacina int,
	@Id_prometa int,
	@Sifra_vrste char(2),
	@Datum char(10),
	@RetVal int OUTPUT
)
AS
DECLARE @count int
SELECT @count = 0
SELECT @count = COUNT(*) FROM Prometni_dokument WHERE id_prometnog_dokumenta = @Id AND status_prometnog = 'u fazi formiranja'

IF(@count = 0)
BEGIN
	SET @RetVal = 1
	PRINT 'Nije dozvoljeno knjiženje dokumenta koji nije u fazi formiranja.'
	RETURN 
END

SELECT @count = COUNT(*) FROM Stavka_prometa WHERE id_prometnog_dokumenta = @Id
IF(@count = 0)
BEGIN
	SET @RetVal = 2
	PRINT 'Prometni dokument se može proknjižiti samo ako ima bar jednu stavku.'
END
ELSE
BEGIN
	DECLARE
		@id_stavke_prometa int,
		@id_artikla int,
		@kolicina_prometa numeric(12),
		@cena_prometa decimal(15,2),
		@vrednost_prometa decimal(15,2)

	DECLARE cursor_proknjizi CURSOR FOR SELECT id_stavke_prometa, id_artikla, kolicina_prometa, cena_prometa, vrednost_prometa FROM Stavka_prometa WHERE id_prometnog_dokumenta = @Id
	OPEN cursor_proknjizi
	FETCH NEXT FROM cursor_proknjizi INTO @id_stavke_prometa, @id_artikla, @kolicina_prometa, @cena_prometa, @vrednost_prometa
	WHILE @@FETCH_STATUS = 0
	BEGIN
		DECLARE
			@id_magacinske int,
			@kolicina_ulaza numeric(12),
			@vrednost_ulaza decimal(15,2),
			@kolicina_izlaza numeric(12),
			@vrednost_izlaza decimal(15,2),
			@kolicina_pocetna numeric(12),
			@vrednost_pocetna decimal(15,2),
			@prosecna_cena decimal(15,2)
		SELECT @id_magacinske = Magacinska_kartica.id_magacinske_kartice, @prosecna_cena = prosecna_cena, @kolicina_ulaza = kolicina_ulaza, @vrednost_ulaza = vrednost_ulaza, 
		@vrednost_izlaza = vrednost_izlaza, @kolicina_izlaza = kolicina_izlaza, @vrednost_pocetna = vrednost_pocetnog_stanja, @kolicina_pocetna = kolicina_pocetnog_stanja 
		FROM Magacinska_kartica JOIN Analitika_magacinske_kartice ON Magacinska_kartica.id_magacinske_kartice = Analitika_magacinske_kartice.id_magacinske_kartice WHERE id_artikla = @id_artikla AND id_jedinice = @Id_magacina AND id_poslovne_godine = @Id_godine
		--Ovde ce trebati neki test da vidimo da li je ulaz ili izlaz. Ispod se nalazi test da li postoji magacinska kartica za dati artikal, poslovnu godinu i magacin. Ako ne, kreiraj je.
		DECLARE
			@novi_ulaz numeric(12) = 0,
			@novi_izlaz numeric(12) = 0,
			@nova_vrednost_ulaza decimal(15, 2) = 0,
			@nova_vrednost_izlaza decimal(15, 2) = 0,
			@smer character(1)
		IF(@Sifra_vrste = 'OT')
		BEGIN
			SET @smer = 'I'
			SET @novi_izlaz = @kolicina_prometa
			SET @nova_vrednost_izlaza = @vrednost_prometa
		END
		ELSE
		BEGIN
			SET @smer = 'U'
			IF(@Id_drugog_magacina <> 0)
			BEGIN
				SET @novi_ulaz = -@kolicina_prometa
				SET @nova_vrednost_ulaza = -@vrednost_prometa
			END
			ELSE
			BEGIN
				SET @novi_ulaz = @kolicina_prometa
				SET @nova_vrednost_ulaza = @vrednost_prometa
			END
		END

		IF(@id_magacinske is not null)
		BEGIN
			DECLARE @redni_broj int
			SELECT @redni_broj = MAX(redni_broj) FROM Analitika_magacinske_kartice WHERE id_magacinske_kartice = @id_magacinske
			INSERT INTO Analitika_magacinske_kartice VALUES (@Id_magacina, @Id_prometa, @id_stavke_prometa, @redni_broj, @smer, @kolicina_prometa, @cena_prometa, @vrednost_prometa, 1)
			IF(@smer = 'U')
			BEGIN
				UPDATE Magacinska_kartica SET kolicina_ulaza = (@kolicina_ulaza + @kolicina_prometa), vrednost_ulaza = (@vrednost_ulaza + @vrednost_prometa), prosecna_cena = ((@vrednost_pocetna + @vrednost_ulaza - @vrednost_izlaza + @vrednost_prometa) / (@kolicina_pocetna + @kolicina_ulaza - @kolicina_izlaza + @kolicina_prometa)) WHERE id_magacinske_kartice = @id_magacinske
			END
			ELSE
			BEGIN
				UPDATE Magacinska_kartica SET kolicina_izlaza = (@kolicina_izlaza + @kolicina_prometa), vrednost_izlaza = (@vrednost_izlaza + @vrednost_prometa), prosecna_cena = ((@vrednost_pocetna + @vrednost_ulaza - @vrednost_izlaza - @vrednost_prometa) / (@kolicina_pocetna + @kolicina_ulaza - @kolicina_izlaza - @kolicina_prometa)) WHERE id_magacinske_kartice = @id_magacinske
			END
		END
		ELSE
		BEGIN
			INSERT INTO Magacinska_kartica VALUES (@Id_godine, @id_artikla, @Id_magacina, @cena_prometa, 0, @novi_ulaz, @novi_izlaz, 0, @nova_vrednost_ulaza, @nova_vrednost_izlaza, 1)
			INSERT INTO Analitika_magacinske_kartice VALUES (SCOPE_IDENTITY(), @Id_prometa, @id_stavke_prometa, 1, @smer, @kolicina_prometa, @cena_prometa, @vrednost_prometa, 1)
		END
		--Ukoliko imamo medjumagacinski promet, kreiramo stavku i u drugom magacinu
		IF(@Id_drugog_magacina <> 0)
		BEGIN
			SELECT @id_magacinske = Magacinska_kartica.id_magacinske_kartice, @prosecna_cena = prosecna_cena, @kolicina_ulaza = kolicina_ulaza, @vrednost_ulaza = vrednost_ulaza, 
			@vrednost_izlaza = vrednost_izlaza, @kolicina_izlaza = kolicina_izlaza, @vrednost_pocetna = vrednost_pocetnog_stanja, @kolicina_pocetna = kolicina_pocetnog_stanja 
			FROM Magacinska_kartica JOIN Analitika_magacinske_kartice ON Magacinska_kartica.id_magacinske_kartice = Analitika_magacinske_kartice.id_magacinske_kartice WHERE id_artikla = @id_artikla AND id_jedinice = @Id_drugog_magacina AND id_poslovne_godine = @Id_godine
			IF(@id_magacinske is null)
			BEGIN
				SELECT @redni_broj = MAX(redni_broj) FROM Analitika_magacinske_kartice WHERE id_magacinske_kartice = @id_magacinske
				INSERT INTO Analitika_magacinske_kartice VALUES (@Id_drugog_magacina, @Id_prometa, @id_stavke_prometa, @redni_broj, 'U', @kolicina_prometa, @cena_prometa, @vrednost_prometa, 1)
				UPDATE Magacinska_kartica SET kolicina_ulaza = (@kolicina_ulaza + @kolicina_prometa), vrednost_ulaza = (@vrednost_ulaza + @vrednost_prometa), prosecna_cena = ((@vrednost_pocetna + @vrednost_ulaza - @vrednost_izlaza + @vrednost_prometa) / (@kolicina_pocetna + @kolicina_ulaza - @kolicina_izlaza + @kolicina_prometa))
			END
			ELSE
			BEGIN
				INSERT INTO Magacinska_kartica VALUES (@Id_godine, @id_artikla, @Id_drugog_magacina, @cena_prometa, 0, @kolicina_prometa, 0, 0, @vrednost_prometa, 0, 1)
				INSERT INTO Analitika_magacinske_kartice VALUES (SCOPE_IDENTITY(), @Id_prometa, @id_stavke_prometa, 1, 'U', @kolicina_prometa, @cena_prometa, @vrednost_prometa, 1)
			END
		END
	END
	UPDATE Prometni_dokument SET status_prometnog = 'proknjizen', datum_knjizenja_prometnog=@Datum WHERE id_prometnog_dokumenta=@Id
	SET @RetVal = 0
END
GO	