IF EXISTS (SELECT 1
   FROM   sysobjects
   WHERE  name = 'ProknjiziPopis' AND type = 'P')
   DROP PROCEDURE ProknjiziPopis
GO

IF EXISTS (SELECT 1
   FROM   sysobjects
   WHERE  name = 'ProknjiziPromet' AND type = 'P')
   DROP PROCEDURE ProknjiziPromet
GO

IF EXISTS (SELECT 1
   FROM   sysobjects
   WHERE  name = 'ZakljuciGodinu' AND type = 'P')
   DROP PROCEDURE ZakljuciGodinu
GO

IF EXISTS (SELECT 1
   FROM   sysobjects
   WHERE  name = 'StornirajPopis' AND type = 'P')
   DROP PROCEDURE StornirajPopis
GO

IF EXISTS (SELECT 1
   FROM   sysobjects
   WHERE  name = 'StornirajPromet' AND type = 'P')
   DROP PROCEDURE StornirajPromet
GO

IF EXISTS (SELECT 1
   FROM   sysobjects
   WHERE  name = 'Nivelacija' AND type = 'P')
   DROP PROCEDURE Nivelacija
GO

IF EXISTS (SELECT 1
   FROM   sysobjects
   WHERE  name = 'OtvaranjePocetnog' AND type = 'P')
   DROP PROCEDURE OtvaranjePocetnog
GO

CREATE PROCEDURE StornirajPopis
(
	@Id int,
	@Datum char(10),
	@RetVal int OUTPUT
)
AS
DECLARE
	@count int = 0,
	@id_magacina int,
	@id_godine int

IF NOT EXISTS (SELECT 1 FROM Popisni_dokument WHERE id_popisnog_dokumenta = @Id AND status_popisnog = 'proknjizen')
BEGIN
	SET @RetVal = 1
	RAISERROR('Nije moguće stornirati dokument koji nije proknjižen.', 11, 2)
	RETURN
END
SELECT @id_magacina = id_jedinice, @id_godine = id_poslovne_godine FROM Popisni_dokument WHERE id_popisnog_dokumenta = @Id AND status_popisnog = 'proknjizen'
BEGIN TRANSACTION
	DECLARE
		@popisana_kolicina numeric(12),
		@kolicina_po_knjigama numeric(12),
		@cena decimal(15,2),
		@id_artikla int

	DECLARE cursor_proknjizi_popis CURSOR FOR SELECT id_artikla, popisana_kolicina, kolicina_po_knjigama, prosecna_cena_popis FROM Stavka_popisa WHERE id_popisnog_dokumenta = @Id
	OPEN cursor_proknjizi_popis
	FETCH NEXT FROM cursor_proknjizi_popis INTO @id_artikla, @popisana_kolicina, @kolicina_po_knjigama, @cena
	WHILE @@FETCH_STATUS = 0
	BEGIN
		IF(@popisana_kolicina = @kolicina_po_knjigama)
		BEGIN
			FETCH NEXT FROM cursor_proknjizi_popis INTO @id_artikla, @popisana_kolicina, @kolicina_po_knjigama, @cena
		END
		ELSE --
		BEGIN
			DECLARE
				@id_magacinske int,
				@id_prometa int,
				@vrednost decimal(15,2),
				@redni_broj int
			SELECT @id_prometa = id_prometa FROM Vrsta_prometa WHERE sifra_prometa = 'KP'
			SELECT @id_magacinske = id_magacinske_kartice FROM Magacinska_kartica WHERE id_jedinice = @Id_magacina AND id_poslovne_godine = @Id_godine AND id_artikla = @id_artikla
			SET @vrednost = (@kolicina_po_knjigama - @popisana_kolicina)*@cena
			SELECT @redni_broj = MAX(redni_broj) FROM Analitika_magacinske_kartice WHERE id_magacinske_kartice = @id_magacinske
			INSERT INTO Analitika_magacinske_kartice VALUES (@id_magacinske, @id_prometa, null, @redni_broj+1, @datum, 'U', @kolicina_po_knjigama - @popisana_kolicina, @cena, @vrednost, 1)
			UPDATE Magacinska_kartica SET kolicina_ulaza = (kolicina_ulaza + @kolicina_po_knjigama - @popisana_kolicina), vrednost_ulaza = (vrednost_ulaza + @vrednost), prosecna_cena = ((vrednost_pocetnog_stanja + vrednost_ulaza - vrednost_izlaza + @vrednost) / (kolicina_pocetnog_stanja + kolicina_ulaza - kolicina_izlaza + @kolicina_po_knjigama - @popisana_kolicina)) WHERE id_magacinske_kartice = @id_magacinske
			FETCH NEXT FROM cursor_proknjizi_popis INTO @id_artikla, @popisana_kolicina, @kolicina_po_knjigama, @cena
		END
	END
	CLOSE cursor_proknjizi_popis
	DEALLOCATE cursor_proknjizi_popis
	SET @RetVal = 0
	UPDATE Popisni_dokument SET status_popisnog = 'storniran' WHERE id_popisnog_dokumenta=@Id
COMMIT TRANSACTION
GO

CREATE PROCEDURE ProknjiziPopis
(
	@Id int,
	@Datum char(10),
	@Id_magacina int,
	@Id_godine int,
	@RetVal int OUTPUT
)
AS
DECLARE @count int
SET @count = 0
SELECT @count = COUNT(*) FROM Popisni_dokument WHERE id_popisnog_dokumenta = @Id AND status_popisnog = 'u fazi formiranja'
IF(@count = 0)
BEGIN
	SET @RetVal = 1
	RAISERROR('Nije dozvoljeno knjiženje dokumenta koji nije u fazi formiranja.', 11, 2)
RETURN
END
  
SELECT @count = COUNT(*) FROM Stavka_popisa WHERE id_popisnog_dokumenta = @Id AND popisana_kolicina is null
IF(@count <> 0)
BEGIN
	SET @RetVal = 2
	RAISERROR('Popisni dokument se može proknjižiti samo kada se unesu sve količine artikla.', 11, 2)
	RETURN
END

SELECT @count = COUNT(*) FROM Clan_komisije WHERE id_popisnog_dokumenta = @Id AND vrsta_clana = 'P'
IF(@count <> 1)
BEGIN
	SET @RetVal = 3
	RAISERROR('Popisni dokument se može proknjižiti samo ako ima tačno jednog člana komisije koji je predsednik.', 11, 2)
	RETURN
END

BEGIN TRANSACTION
	DECLARE
		@popisana_kolicina numeric(12),
		@kolicina_po_knjigama numeric(12),
		@cena decimal(15,2),
		@id_artikla int

	DECLARE cursor_proknjizi_popis CURSOR FOR SELECT id_artikla, popisana_kolicina, kolicina_po_knjigama, prosecna_cena_popis FROM Stavka_popisa WHERE id_popisnog_dokumenta = @Id
	OPEN cursor_proknjizi_popis
	FETCH NEXT FROM cursor_proknjizi_popis INTO @id_artikla, @popisana_kolicina, @kolicina_po_knjigama, @cena
	WHILE @@FETCH_STATUS = 0
	BEGIN
		IF(@popisana_kolicina = @kolicina_po_knjigama)
		BEGIN
			FETCH NEXT FROM cursor_proknjizi_popis INTO @id_artikla, @popisana_kolicina, @kolicina_po_knjigama, @cena
		END
		ELSE --
		BEGIN
			DECLARE
				@id_magacinske int,
				@id_prometa int,
				@vrednost decimal(15,2),
				@redni_broj int
			SELECT @id_prometa = id_prometa FROM Vrsta_prometa WHERE sifra_prometa = 'KP'
			SELECT @id_magacinske = id_magacinske_kartice FROM Magacinska_kartica WHERE id_jedinice = @Id_magacina AND id_poslovne_godine = @Id_godine AND id_artikla = @id_artikla
			SET @vrednost = (@popisana_kolicina - @kolicina_po_knjigama)*@cena
			SELECT @redni_broj = MAX(redni_broj) FROM Analitika_magacinske_kartice WHERE id_magacinske_kartice = @id_magacinske
			INSERT INTO Analitika_magacinske_kartice VALUES (@id_magacinske, @id_prometa, null, @redni_broj+1, @Datum, 'U', @popisana_kolicina - @kolicina_po_knjigama, @cena, @vrednost, 1)
			UPDATE Magacinska_kartica SET kolicina_ulaza = (kolicina_ulaza + @popisana_kolicina - @kolicina_po_knjigama), vrednost_ulaza = (vrednost_ulaza + @vrednost), prosecna_cena = ((vrednost_pocetnog_stanja + vrednost_ulaza - vrednost_izlaza + @vrednost) / (kolicina_pocetnog_stanja + kolicina_ulaza - kolicina_izlaza + @popisana_kolicina - @kolicina_po_knjigama)) WHERE id_magacinske_kartice = @id_magacinske
			FETCH NEXT FROM cursor_proknjizi_popis INTO @id_artikla, @popisana_kolicina, @kolicina_po_knjigama, @cena
		END
	END
	CLOSE cursor_proknjizi_popis
	DEALLOCATE cursor_proknjizi_popis
	SET @RetVal = 0
	UPDATE Popisni_dokument SET status_popisnog = 'proknjizen', datum_knjizenja=@Datum WHERE id_popisnog_dokumenta=@Id
COMMIT TRANSACTION
GO

CREATE PROCEDURE ZakljuciGodinu
(
	@Id_zakljucivane_godine int,
	@Id_preduzeca int
)
AS
IF NOT EXISTS (SELECT 1 FROM Poslovna_godina WHERE id_poslovne_godine = @Id_zakljucivane_godine AND zakljucena = 0)
BEGIN
	RAISERROR('Data godina ne postoji ili je već zaključena.',11,2)
	RETURN
END

IF NOT EXISTS(SELECT 1 FROM Poslovna_godina WHERE zakljucena = 0 AND id_poslovne_godine <> @Id_zakljucivane_godine AND id_preduzeca = @Id_preduzeca)
BEGIN
	RAISERROR('Mora se otvoriti nova poslovna godina pre nego što se stara može zaključiti.',11,2)
	RETURN
END

IF EXISTS(SELECT 1 FROM Poslovna_godina god JOIN Popisni_dokument pop ON god.id_poslovne_godine = pop.id_poslovne_godine
JOIN Prometni_dokument pro ON god.id_poslovne_godine = pro.id_poslovne_godine WHERE god.id_poslovne_godine = @Id_zakljucivane_godine AND (pop.status_popisnog = 'u fazi formiranja' OR pro.status_prometnog = 'u fazi formiranja'))
BEGIN
	RAISERROR('U godini koja se zaključuje ne sme biti dokumenata u fazi formiranja.',11,2)
	RETURN
END

DECLARE
	@id_nove_godine int,
	@id_iteriranog_magacina int

SELECT @id_nove_godine = id_poslovne_godine FROM Poslovna_godina WHERE zakljucena = 0 AND id_poslovne_godine <> @Id_zakljucivane_godine AND id_preduzeca = @Id_preduzeca
DECLARE cursor_zakljucenje CURSOR FOR SELECT id_jedinice FROM Organizaciona_jedinica WHERE id_preduzeca = @Id_preduzeca
OPEN cursor_zakljucenje
FETCH NEXT FROM cursor_zakljucenje INTO @id_iteriranog_magacina
WHILE @@FETCH_STATUS = 0
BEGIN
	IF EXISTS (SELECT 1 FROM Magacinska_kartica WHERE id_jedinice = @id_iteriranog_magacina AND id_poslovne_godine = @Id_zakljucivane_godine)
	BEGIN
		IF NOT EXISTS (SELECT 1 FROM Magacinska_kartica WHERE id_jedinice = @id_iteriranog_magacina AND id_poslovne_godine = @id_nove_godine)
		BEGIN
			RAISERROR('Nisu kreirana nova stanja za sve magacine.',11,2)
			RETURN
		END
	END
	FETCH NEXT FROM cursor_zakljucenje INTO @id_iteriranog_magacina	
END
CLOSE cursor_zakljucenje
DEALLOCATE cursor_zakljucenje

BEGIN TRANSACTION
	UPDATE Poslovna_godina SET zakljucena = '1' WHERE id_poslovne_godine = @Id_zakljucivane_godine
COMMIT TRANSACTION
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

SET @RetVal = 0

IF(@count = 0)
BEGIN
	SET @RetVal = 1
	RAISERROR('Nije dozvoljeno knjiženje dokumenta koji nije u fazi formiranja.', 11, 2)
	RETURN 
END

SELECT @count = COUNT(*) FROM Stavka_prometa WHERE id_prometnog_dokumenta = @Id
IF(@count = 0)
BEGIN
	SET @RetVal = 2
	RAISERROR('Prometni dokument se može proknjižiti samo ako ima bar jednu stavku.', 11, 2)
	RETURN
END
ELSE
BEGIN TRANSACTION
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
			@prosecna_cena decimal(15,2),

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
			BEGIN
				SET @novi_ulaz = @kolicina_prometa
				SET @nova_vrednost_ulaza = @vrednost_prometa
			END
		END

		IF EXISTS (SELECT 1 FROM Magacinska_kartica JOIN Analitika_magacinske_kartice ON Magacinska_kartica.id_magacinske_kartice = Analitika_magacinske_kartice.id_magacinske_kartice WHERE id_artikla = @id_artikla AND id_jedinice = @Id_magacina AND id_poslovne_godine = @Id_godine)
		BEGIN
			SELECT @id_magacinske = Magacinska_kartica.id_magacinske_kartice, @prosecna_cena = prosecna_cena, @kolicina_ulaza = kolicina_ulaza, @vrednost_ulaza = vrednost_ulaza, 
				@vrednost_izlaza = vrednost_izlaza, @kolicina_izlaza = kolicina_izlaza, @vrednost_pocetna = vrednost_pocetnog_stanja, @kolicina_pocetna = kolicina_pocetnog_stanja 
				FROM Magacinska_kartica JOIN Analitika_magacinske_kartice ON Magacinska_kartica.id_magacinske_kartice = Analitika_magacinske_kartice.id_magacinske_kartice WHERE id_artikla = @id_artikla AND id_jedinice = @Id_magacina AND id_poslovne_godine = @Id_godine
			DECLARE @redni_broj int
			SELECT @redni_broj = MAX(redni_broj) FROM Analitika_magacinske_kartice WHERE id_magacinske_kartice = @id_magacinske
			INSERT INTO Analitika_magacinske_kartice VALUES (@id_magacinske, @Id_prometa, @id_stavke_prometa, @redni_broj+1, @Datum, @smer, @kolicina_prometa, @cena_prometa, @vrednost_prometa, 1)
			IF(@smer = 'U')
			BEGIN
				UPDATE Magacinska_kartica SET kolicina_ulaza = (@kolicina_ulaza + @kolicina_prometa), vrednost_ulaza = (@vrednost_ulaza + @vrednost_prometa), prosecna_cena = ((@vrednost_pocetna + @vrednost_ulaza - @vrednost_izlaza + @vrednost_prometa) / (@kolicina_pocetna + @kolicina_ulaza - @kolicina_izlaza + @kolicina_prometa)) WHERE id_magacinske_kartice = @id_magacinske
			END
			ELSE
			BEGIN
				UPDATE Magacinska_kartica SET kolicina_izlaza = (@kolicina_izlaza + @kolicina_prometa), vrednost_izlaza = (@vrednost_izlaza + @vrednost_prometa) WHERE id_magacinske_kartice = @id_magacinske
			END
		END
		ELSE
		BEGIN
			IF(@smer = 'I')
			BEGIN
				SET @RetVal = 4
			END
			ELSE
			BEGIN
				INSERT INTO Magacinska_kartica VALUES (@Id_godine, @id_artikla, @Id_magacina, @cena_prometa, 0, @novi_ulaz, @novi_izlaz, 0, @nova_vrednost_ulaza, @nova_vrednost_izlaza, 1)
				INSERT INTO Analitika_magacinske_kartice VALUES (SCOPE_IDENTITY(), @Id_prometa, @id_stavke_prometa, 1, @Datum, @smer, @kolicina_prometa, @cena_prometa, @vrednost_prometa, 1)
			END
		END
		--Ukoliko imamo medjumagacinski promet, kreiramo stavku i u drugom magacinu
		IF(@Id_drugog_magacina <> 0)
		BEGIN
			IF EXISTS (SELECT 1 FROM Magacinska_kartica JOIN Analitika_magacinske_kartice ON Magacinska_kartica.id_magacinske_kartice = Analitika_magacinske_kartice.id_magacinske_kartice WHERE id_artikla = @id_artikla AND id_jedinice = @Id_drugog_magacina AND id_poslovne_godine = @Id_godine)
			BEGIN
				SELECT @id_magacinske = Magacinska_kartica.id_magacinske_kartice, @prosecna_cena = prosecna_cena, @kolicina_ulaza = kolicina_ulaza, @vrednost_ulaza = vrednost_ulaza, 
				@vrednost_izlaza = vrednost_izlaza, @kolicina_izlaza = kolicina_izlaza, @vrednost_pocetna = vrednost_pocetnog_stanja, @kolicina_pocetna = kolicina_pocetnog_stanja 
				FROM Magacinska_kartica JOIN Analitika_magacinske_kartice ON Magacinska_kartica.id_magacinske_kartice = Analitika_magacinske_kartice.id_magacinske_kartice WHERE id_artikla = @id_artikla AND id_jedinice = @Id_drugog_magacina AND id_poslovne_godine = @Id_godine
				SELECT @redni_broj = MAX(redni_broj) FROM Analitika_magacinske_kartice WHERE id_magacinske_kartice = @id_magacinske
				INSERT INTO Analitika_magacinske_kartice VALUES (@Id_drugog_magacina, @Id_prometa, @id_stavke_prometa, @redni_broj+1, @Datum, 'U', -@kolicina_prometa, @cena_prometa, -@vrednost_prometa, 1)
				UPDATE Magacinska_kartica SET kolicina_ulaza = (@kolicina_ulaza - @kolicina_prometa), vrednost_ulaza = (@vrednost_ulaza - @vrednost_prometa), prosecna_cena = ((@vrednost_pocetna + @vrednost_ulaza - @vrednost_izlaza - @vrednost_prometa) / (@kolicina_pocetna + @kolicina_ulaza - @kolicina_izlaza - @kolicina_prometa))
			END
			ELSE
			BEGIN
				SET @RetVal = 4
			END
		END
		IF (@RetVal <> 0)
		BEGIN
			CLOSE cursor_proknjizi
			DEALLOCATE cursor_proknjizi		
			RAISERROR('Zalihe datog artikla ne postoje u magacinu.', 11, 2)
			ROLLBACK TRANSACTION
			RETURN
		END
		FETCH NEXT FROM cursor_proknjizi INTO @id_stavke_prometa, @id_artikla, @kolicina_prometa, @cena_prometa, @vrednost_prometa
	END
	CLOSE cursor_proknjizi
    DEALLOCATE cursor_proknjizi
	UPDATE Prometni_dokument SET status_prometnog = 'proknjizen', datum_knjizenja_prometnog=@Datum WHERE id_prometnog_dokumenta=@Id
	SET @RetVal = 0
	COMMIT TRANSACTION
GO

CREATE PROCEDURE StornirajPromet
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
SET @RetVal = 0
IF NOT EXISTS (SELECT 1 FROM Prometni_dokument WHERE id_prometnog_dokumenta = @Id AND status_prometnog = 'proknjizen')
BEGIN
	SET @RetVal = 1
	RAISERROR('Nije dozvoljeno storniranje dokumenta koji nije proknjižen.', 11, 2)
	RETURN 
END

BEGIN TRANSACTION
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
			@prosecna_cena decimal(15,2),

			@novi_ulaz numeric(12) = 0,
			@novi_izlaz numeric(12) = 0,
			@nova_vrednost_ulaza decimal(15, 2) = 0,
			@nova_vrednost_izlaza decimal(15, 2) = 0,
			@smer character(1)
		IF(@Sifra_vrste = 'OT')
		BEGIN
			SET @smer = 'I'
		END
		ELSE
		BEGIN
			SET @smer = 'U'
		END


		SELECT @id_magacinske = Magacinska_kartica.id_magacinske_kartice, @prosecna_cena = prosecna_cena, @kolicina_ulaza = kolicina_ulaza, @vrednost_ulaza = vrednost_ulaza, 
			@vrednost_izlaza = vrednost_izlaza, @kolicina_izlaza = kolicina_izlaza, @vrednost_pocetna = vrednost_pocetnog_stanja, @kolicina_pocetna = kolicina_pocetnog_stanja 
			FROM Magacinska_kartica JOIN Analitika_magacinske_kartice ON Magacinska_kartica.id_magacinske_kartice = Analitika_magacinske_kartice.id_magacinske_kartice WHERE id_artikla = @id_artikla AND id_jedinice = @Id_magacina AND id_poslovne_godine = @Id_godine
		DECLARE @redni_broj int
		SELECT @redni_broj = MAX(redni_broj) FROM Analitika_magacinske_kartice WHERE id_magacinske_kartice = @id_magacinske
		INSERT INTO Analitika_magacinske_kartice VALUES (@id_magacinske, @Id_prometa, @id_stavke_prometa, @redni_broj+1, @Datum, @smer, -@kolicina_prometa, @cena_prometa, -@vrednost_prometa, 1)
		IF(@smer = 'U')
		BEGIN
			UPDATE Magacinska_kartica SET kolicina_ulaza = (@kolicina_ulaza - @kolicina_prometa), vrednost_ulaza = (@vrednost_ulaza - @vrednost_prometa), prosecna_cena = ((@vrednost_pocetna + @vrednost_ulaza - @vrednost_izlaza - @vrednost_prometa) / (@kolicina_pocetna + @kolicina_ulaza - @kolicina_izlaza - @kolicina_prometa)) WHERE id_magacinske_kartice = @id_magacinske
		END
		ELSE
		BEGIN
			UPDATE Magacinska_kartica SET kolicina_izlaza = (@kolicina_izlaza - @kolicina_prometa), vrednost_izlaza = (@vrednost_izlaza - @vrednost_prometa) WHERE id_magacinske_kartice = @id_magacinske
		END

		--Ukoliko imamo medjumagacinski promet, kreiramo stavku i u drugom magacinu
		IF(@Id_drugog_magacina <> 0)
		BEGIN
			SELECT @id_magacinske = Magacinska_kartica.id_magacinske_kartice, @prosecna_cena = prosecna_cena, @kolicina_ulaza = kolicina_ulaza, @vrednost_ulaza = vrednost_ulaza, 
			@vrednost_izlaza = vrednost_izlaza, @kolicina_izlaza = kolicina_izlaza, @vrednost_pocetna = vrednost_pocetnog_stanja, @kolicina_pocetna = kolicina_pocetnog_stanja 
			FROM Magacinska_kartica JOIN Analitika_magacinske_kartice ON Magacinska_kartica.id_magacinske_kartice = Analitika_magacinske_kartice.id_magacinske_kartice WHERE id_artikla = @id_artikla AND id_jedinice = @Id_drugog_magacina AND id_poslovne_godine = @Id_godine
			SELECT @redni_broj = MAX(redni_broj) FROM Analitika_magacinske_kartice WHERE id_magacinske_kartice = @id_magacinske
			INSERT INTO Analitika_magacinske_kartice VALUES (@Id_drugog_magacina, @Id_prometa, @id_stavke_prometa, @redni_broj+1, @Datum, 'U', @kolicina_prometa, @cena_prometa, @vrednost_prometa, 1)
			UPDATE Magacinska_kartica SET kolicina_ulaza = (@kolicina_ulaza + @kolicina_prometa), vrednost_ulaza = (@vrednost_ulaza + @vrednost_prometa), prosecna_cena = ((@vrednost_pocetna + @vrednost_ulaza - @vrednost_izlaza + @vrednost_prometa) / (@kolicina_pocetna + @kolicina_ulaza - @kolicina_izlaza + @kolicina_prometa))
		END
		IF (@RetVal <> 0)
		BEGIN
			CLOSE cursor_proknjizi
			DEALLOCATE cursor_proknjizi		
			RAISERROR('Zalihe datog artikla ne postoje u magacinu.', 11, 2)
			ROLLBACK TRANSACTION
			RETURN
		END
		FETCH NEXT FROM cursor_proknjizi INTO @id_stavke_prometa, @id_artikla, @kolicina_prometa, @cena_prometa, @vrednost_prometa
	END
	CLOSE cursor_proknjizi
    DEALLOCATE cursor_proknjizi
	UPDATE Prometni_dokument SET status_prometnog = 'storniran' WHERE id_prometnog_dokumenta=@Id
	SET @RetVal = 0
	COMMIT TRANSACTION
GO

CREATE PROCEDURE Nivelacija
(
	@id_kartice int,
	@Datum char(10),
	@godinaZakljucena int
)
AS
IF(@godinaZakljucena = 1)
BEGIN
	RAISERROR('Nivelacija se ne može vršiti nad magacinskim karticama iz zaključenih godina.',11,2)
	RETURN
END
DECLARE
	@ukupna_kolicina decimal(15,2),
	@ukupna_vrednost decimal(15,2),
	@prosecna_cena decimal(15,2),
	@id_prometa int,
	@redni_broj int
BEGIN TRANSACTION
	SELECT @ukupna_kolicina = (kolicina_pocetnog_stanja + kolicina_ulaza - kolicina_izlaza), @ukupna_vrednost = (vrednost_pocetnog_stanja + vrednost_ulaza - vrednost_izlaza), @prosecna_cena = prosecna_cena FROM Magacinska_kartica WHERE id_magacinske_kartice = @id_kartice
	SELECT @id_prometa = id_prometa FROM Vrsta_prometa WHERE sifra_prometa = 'NI'
	SELECT @redni_broj = MAX(redni_broj) FROM Analitika_magacinske_kartice WHERE id_magacinske_kartice = @id_kartice
	INSERT INTO Analitika_magacinske_kartice VALUES (@id_kartice, @id_prometa, null, @redni_broj+1, @Datum, 'U', 0, @prosecna_cena, (@ukupna_kolicina * @prosecna_cena - @ukupna_vrednost), 1)
	UPDATE Magacinska_kartica SET vrednost_ulaza = (vrednost_ulaza + (@ukupna_kolicina * @prosecna_cena - @ukupna_vrednost)) WHERE id_magacinske_kartice = @id_kartice
COMMIT TRANSACTION
GO

CREATE PROCEDURE OtvaranjePocetnog
(
	@Id_magacina int,
	@Id_godine int,
	@Id_preduzeca int,
	@Datum char(10)
)
AS
DECLARE
	@id_nove_godine int
IF NOT EXISTS (SELECT 1 FROM Poslovna_godina WHERE zakljucena = '0' AND id_poslovne_godine != @Id_godine AND id_preduzeca = @Id_preduzeca)
BEGIN
	RAISERROR('Mora postojati druga nezaključena poslovna godina kako bi se otvorilo novo stanje magacina.',11,2)
	RETURN
END
SELECT @id_nove_godine = id_poslovne_godine FROM Poslovna_godina WHERE zakljucena = '0' AND id_poslovne_godine != @Id_godine AND id_preduzeca = @Id_preduzeca
IF EXISTS (SELECT 1 FROM Magacinska_kartica WHERE id_jedinice = @Id_magacina AND id_poslovne_godine = @id_nove_godine)
BEGIN
	RAISERROR('Početno stanje za dati magacin je već otvoreno.',11,2)
	RETURN
END

BEGIN TRANSACTION
	DECLARE
		@id_artikla int,
		@kolicina_pocetnog numeric(12),
		@kolicina_ulaza numeric(12),
		@kolicina_izlaza numeric(12),
		@prosecna_cena decimal(15,2),
		@pocetna_kolicina numeric(12),
		@id_prometa int
	SELECT @id_prometa = id_prometa FROM Vrsta_prometa WHERE sifra_prometa = 'PO'
	DECLARE cursor_novo_stanje CURSOR FOR SELECT id_artikla, kolicina_pocetnog_stanja, kolicina_ulaza, kolicina_izlaza, prosecna_cena FROM Magacinska_kartica WHERE id_jedinice = @Id_magacina AND id_poslovne_godine = @Id_godine AND ((kolicina_pocetnog_stanja+kolicina_ulaza-kolicina_izlaza)*prosecna_cena) > 0
	OPEN cursor_novo_stanje
	FETCH NEXT FROM cursor_novo_stanje INTO @id_artikla, @kolicina_pocetnog, @kolicina_ulaza, @kolicina_izlaza, @prosecna_cena
	WHILE @@FETCH_STATUS = 0
	BEGIN
		SET @pocetna_kolicina = @kolicina_pocetnog + @kolicina_ulaza - @kolicina_izlaza
		INSERT INTO Magacinska_kartica VALUES (@id_nove_godine, @id_artikla, @Id_magacina, @prosecna_cena, @pocetna_kolicina, 0, 0, @pocetna_kolicina*@prosecna_cena, 0, 0, 1)
		INSERT INTO Analitika_magacinske_kartice VALUES (SCOPE_IDENTITY(), @id_prometa, null, 1, @Datum, 'U', @pocetna_kolicina, @prosecna_cena, @pocetna_kolicina*@prosecna_cena, 1)
		FETCH NEXT FROM cursor_novo_stanje INTO @id_artikla, @kolicina_pocetnog, @kolicina_ulaza, @kolicina_izlaza, @prosecna_cena	
	END
	CLOSE cursor_novo_stanje
	DEALLOCATE cursor_novo_stanje
COMMIT TRANSACTION
GO
