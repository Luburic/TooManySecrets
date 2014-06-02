EXEC sp_MSForEachTable 'ALTER TABLE ? NOCHECK CONSTRAINT ALL' 
GO
EXEC sp_MSForEachTable 'TRUNCATE TABLE ?' 
GO
EXEC sp_MSForEachTable 'ALTER TABLE ? CHECK CONSTRAINT ALL' 
GO

INSERT INTO Korisnik VALUES ('admin', '8f5d06831a28eee4787ae2d868d1a60b410f898865fad956213cb9998e7b47a3', '_FR7:#~1cPbj''F+4<;0-', 'A', 1)

INSERT INTO Drzava VALUES ('SRB', 'Srbija', 1)
INSERT INTO Drzava VALUES ('ENG', 'Engleska', 1)
INSERT INTO Drzava VALUES ('HUN', 'Mađarska', 1)
INSERT INTO Drzava VALUES ('NED', 'Holandija', 1)
INSERT INTO Drzava VALUES ('GER', 'Nemačka', 1)

INSERT INTO Mesto VALUES (1, '21000', 'Novi Sad', 1)
INSERT INTO Mesto VALUES (1, '11000', 'Beograd', 1)
INSERT INTO Mesto VALUES (1, '24000', 'Subotica', 1)
INSERT INTO Mesto VALUES (2, '99999', 'London', 1)
INSERT INTO Mesto VALUES (2, '88888', 'New Castle', 1)
INSERT INTO Mesto VALUES (2, '77777', 'Blackburn', 1)
INSERT INTO Mesto VALUES (3, '12345', 'Budimpešta', 1)
INSERT INTO Mesto VALUES (3, '54321', 'Segedin', 1)

INSERT INTO Preduzece VALUES (1, 'Bokić DOO', '021999999', 'Maksima Gorkog 10', 1)
INSERT INTO Preduzece VALUES (2, 'Boban i sinovi', '011111111', 'Bobanova 4c', 1)
INSERT INTO Preduzece VALUES (2, 'Univerexport', '011555555', 'Beogradska 9', 1)

INSERT INTO Poslovna_godina VALUES (1, '2012', 1, 1)
INSERT INTO Poslovna_godina VALUES (1, '2013', 1, 1)
INSERT INTO Poslovna_godina VALUES (1, '2014', 0, 1)
INSERT INTO Poslovna_godina VALUES (2, '2013', 1, 1)
INSERT INTO Poslovna_godina VALUES (2, '2014', 0, 1)

INSERT INTO Organizaciona_jedinica VALUES (0, 1, 'Sektor 1', 0, 1)
INSERT INTO Organizaciona_jedinica VALUES (1, 1, 'Magacin 1', 1, 1)
INSERT INTO Organizaciona_jedinica VALUES (1, 1, 'Magacin 2', 1, 1)
INSERT INTO Organizaciona_jedinica VALUES (1, 1, 'Magacin 3', 1, 1)

INSERT INTO Poslovni_partner VALUES (2, 1, 'Bokić Beograd PP', 'Beogradska 5', 'K', 1)
INSERT INTO Poslovni_partner VALUES (4, 1, 'Bokić London PP', 'Talična 10', 'D', 1)
INSERT INTO Poslovni_partner VALUES (3, 1, 'Bokić Subotica PP', 'Srebrna 11', 'O', 1)

INSERT INTO Radnik VALUES (1, 1, 'Boban', 'Bokić', '1234567891234', 'Braće Ribnikara 10', 1)
INSERT INTO Radnik VALUES (1, 1, 'Mađija', 'Stole', '9999999999999', 'Telepska 10', 1)
INSERT INTO Radnik VALUES (1, 1, 'Kale', 'Gospodar Vremena', '8888888888888', 'Bulevar Oslobođenja 10', 1)

INSERT INTO Grupa_artikla VALUES (0, 'Prehrambeni proizvodi', 1)
INSERT INTO Grupa_artikla VALUES (1, 'Mlečni proizvodi', 1)
INSERT INTO Grupa_artikla VALUES (1, 'Meso', 1)
INSERT INTO Grupa_artikla VALUES (3, 'Suhomesnati proizvodi', 1)
INSERT INTO Grupa_artikla VALUES (3, 'Vlažnomesnati proizvodi', 1)

INSERT INTO Artikal VALUES (4, '12345678', '1.00', 'kg', 'Pileća prsa, 1kg', 1)
INSERT INTO Artikal VALUES (5, '23456789', '1.00', 'kg', 'Pečeno pile', 1)
INSERT INTO Artikal VALUES (2, '99999999', '1.00', 'kom', 'Mleko, 1l', 1)
INSERT INTO Artikal VALUES (1, '88888888', '20.00', 'kom', 'Pivo, gajba', 1)

INSERT INTO Vrsta_prometa VALUES ('OT', 'Otprema', 1)
INSERT INTO Vrsta_prometa VALUES ('NA', 'Nabavka', 1)
INSERT INTO Vrsta_prometa VALUES ('MM', 'Međumagacinsko', 1)
INSERT INTO Vrsta_prometa VALUES ('KP', 'Korekcija po popisu', 1)
INSERT INTO Vrsta_prometa VALUES ('NI', 'Nivelacija', 1)