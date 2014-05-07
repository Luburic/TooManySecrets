/*==============================================================*/
/* DBMS name:      Microsoft SQL Server 2008                    */
/* Created on:     07-May-14 5:29:38 PM                         */
/*==============================================================*/


if exists (select 1
            from  sysindexes
           where  id    = object_id('"Analitika magacinske kartice"')
            and   name  = 'Relationship_28_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Analitika magacinske kartice".Relationship_28_FK
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Analitika magacinske kartice"')
            and   name  = 'Stavke_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Analitika magacinske kartice".Stavke_FK
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Analitika magacinske kartice"')
            and   name  = 'Vrsta_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Analitika magacinske kartice".Vrsta_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('"Analitika magacinske kartice"')
            and   type = 'U')
   drop table "Analitika magacinske kartice"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Artikal')
            and   name  = 'Artikli_FK'
            and   indid > 0
            and   indid < 255)
   drop index Artikal.Artikli_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Artikal')
            and   type = 'U')
   drop table Artikal
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Drzava')
            and   type = 'U')
   drop table Drzava
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Grupa artikla"')
            and   name  = 'Podgrupa_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Grupa artikla".Podgrupa_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('"Grupa artikla"')
            and   type = 'U')
   drop table "Grupa artikla"
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Korisnik')
            and   type = 'U')
   drop table Korisnik
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Magacinska kartica"')
            and   name  = 'Artikli kartice_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Magacinska kartica"."Artikli kartice_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Magacinska kartica"')
            and   name  = 'Kartice godine_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Magacinska kartica"."Kartice godine_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Magacinska kartica"')
            and   name  = 'Kartice_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Magacinska kartica".Kartice_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('"Magacinska kartica"')
            and   type = 'U')
   drop table "Magacinska kartica"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Mesto')
            and   name  = 'Mesta_FK'
            and   indid > 0
            and   indid < 255)
   drop index Mesto.Mesta_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Mesto')
            and   type = 'U')
   drop table Mesto
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Organizaciona jedinica"')
            and   name  = 'Podsektor_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Organizaciona jedinica".Podsektor_FK
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Organizaciona jedinica"')
            and   name  = 'Magacini_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Organizaciona jedinica".Magacini_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('"Organizaciona jedinica"')
            and   type = 'U')
   drop table "Organizaciona jedinica"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Popisni dokument"')
            and   name  = 'Popis magacina_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Popisni dokument"."Popis magacina_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Popisni dokument"')
            and   name  = 'Godina popisa_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Popisni dokument"."Godina popisa_FK"
go

if exists (select 1
            from  sysobjects
           where  id = object_id('"Popisni dokument"')
            and   type = 'U')
   drop table "Popisni dokument"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Poslovna godina"')
            and   name  = 'Godine_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Poslovna godina".Godine_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('"Poslovna godina"')
            and   type = 'U')
   drop table "Poslovna godina"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Poslovni partner"')
            and   name  = 'Mesto poslovnog_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Poslovni partner"."Mesto poslovnog_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Poslovni partner"')
            and   name  = 'Partneri_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Poslovni partner".Partneri_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('"Poslovni partner"')
            and   type = 'U')
   drop table "Poslovni partner"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Preduzece')
            and   name  = 'Sediste_FK'
            and   indid > 0
            and   indid < 255)
   drop index Preduzece.Sediste_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Preduzece')
            and   type = 'U')
   drop table Preduzece
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Prometni dokument"')
            and   name  = 'Preko partnera_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Prometni dokument"."Preko partnera_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Prometni dokument"')
            and   name  = 'Relationship_26_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Prometni dokument".Relationship_26_FK
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Prometni dokument"')
            and   name  = 'Vrsta prometa_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Prometni dokument"."Vrsta prometa_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Prometni dokument"')
            and   name  = 'Iz magacina_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Prometni dokument"."Iz magacina_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Prometni dokument"')
            and   name  = 'U magacin_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Prometni dokument"."U magacin_FK"
go

if exists (select 1
            from  sysobjects
           where  id = object_id('"Prometni dokument"')
            and   type = 'U')
   drop table "Prometni dokument"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Radnik')
            and   name  = 'Zaposlen_FK'
            and   indid > 0
            and   indid < 255)
   drop index Radnik.Zaposlen_FK
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Radnik')
            and   name  = 'Prebivaliste_FK'
            and   indid > 0
            and   indid < 255)
   drop index Radnik.Prebivaliste_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Radnik')
            and   type = 'U')
   drop table Radnik
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Stavka popisa"')
            and   name  = 'Artikal popisa_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Stavka popisa"."Artikal popisa_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Stavka popisa"')
            and   name  = 'Stavke popisa_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Stavka popisa"."Stavke popisa_FK"
go

if exists (select 1
            from  sysobjects
           where  id = object_id('"Stavka popisa"')
            and   type = 'U')
   drop table "Stavka popisa"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Stavka prometa"')
            and   name  = 'Artikli prometa_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Stavka prometa"."Artikli prometa_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Stavka prometa"')
            and   name  = 'Stavke prometa_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Stavka prometa"."Stavke prometa_FK"
go

if exists (select 1
            from  sysobjects
           where  id = object_id('"Stavka prometa"')
            and   type = 'U')
   drop table "Stavka prometa"
go

if exists (select 1
            from  sysobjects
           where  id = object_id('"Vrsta prometa"')
            and   type = 'U')
   drop table "Vrsta prometa"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Clan komisije"')
            and   name  = 'U komisiji_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Clan komisije"."U komisiji_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('"Clan komisije"')
            and   name  = 'Relationship_19_FK'
            and   indid > 0
            and   indid < 255)
   drop index "Clan komisije".Relationship_19_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('"Clan komisije"')
            and   type = 'U')
   drop table "Clan komisije"
go

/*==============================================================*/
/* Table: "Analitika magacinske kartice"                        */
/*==============================================================*/
create table "Analitika magacinske kartice" (
   "id analitike"       int                  not null,
   "id magacinske kartice" int                  not null,
   "id prometa"         int                  not null,
   "id stavke prometa"  int                  null,
   "redni broj"         int                  not null,
   smer                 char(1)              not null
      constraint CKC_SMER_ANALITIK check (smer in ('U','I')),
   kolicina             numeric(12)          not null,
   cena                 decimal(15,2)        not null,
   vrednost             decimal(15,2)        not null,
   constraint "PK_ANALITIKA MAGACINSKE KARTIC" primary key nonclustered ("id analitike")
)
go

/*==============================================================*/
/* Index: Vrsta_FK                                              */
/*==============================================================*/
create index Vrsta_FK on "Analitika magacinske kartice" (
"id prometa" ASC
)
go

/*==============================================================*/
/* Index: Stavke_FK                                             */
/*==============================================================*/
create index Stavke_FK on "Analitika magacinske kartice" (
"id magacinske kartice" ASC
)
go

/*==============================================================*/
/* Index: Relationship_28_FK                                    */
/*==============================================================*/
create index Relationship_28_FK on "Analitika magacinske kartice" (
"id stavke prometa" ASC
)
go

/*==============================================================*/
/* Table: Artikal                                               */
/*==============================================================*/
create table Artikal (
   "id artikla"         int                  not null,
   "id grupe"           int                  not null,
   "sifra artikla"      char(8)              not null,
   "naziv artikla"      varchar(30)          not null,
   constraint PK_ARTIKAL primary key nonclustered ("id artikla")
)
go

/*==============================================================*/
/* Index: Artikli_FK                                            */
/*==============================================================*/
create index Artikli_FK on Artikal (
"id grupe" ASC
)
go

/*==============================================================*/
/* Table: Drzava                                                */
/*==============================================================*/
create table Drzava (
   "id drzave"          int                  not null,
   "naziv drzave"       varchar(20)          not null,
   constraint PK_DRZAVA primary key nonclustered ("id drzave")
)
go

/*==============================================================*/
/* Table: "Grupa artikla"                                       */
/*==============================================================*/
create table "Grupa artikla" (
   "id grupe"           int                  not null,
   "Gru_id grupe"       int                  null,
   "naziv grupe"        varchar(30)          not null,
   constraint "PK_GRUPA ARTIKLA" primary key nonclustered ("id grupe")
)
go

/*==============================================================*/
/* Index: Podgrupa_FK                                           */
/*==============================================================*/
create index Podgrupa_FK on "Grupa artikla" (
"Gru_id grupe" ASC
)
go

/*==============================================================*/
/* Table: Korisnik                                              */
/*==============================================================*/
create table Korisnik (
   "id korisnika"       int                  not null,
   username             varchar(30)          not null,
   password             varchar(65)          not null,
   "password salt"      varchar(20)          not null,
   role                 char(1)              not null
      constraint CKC_ROLE_KORISNIK check (role in ('A','R')),
   constraint PK_KORISNIK primary key nonclustered ("id korisnika")
)
go

/*==============================================================*/
/* Table: "Magacinska kartica"                                  */
/*==============================================================*/
create table "Magacinska kartica" (
   "id magacinske kartice" int                  not null,
   "id poslovne godine" int                  not null,
   "id artikla"         int                  not null,
   "id jedinice"        int                  not null,
   "prosecna cena"      decimal(15,2)        not null,
   "kolicina pocetnog stanja" numeric(12)          not null,
   "kolicina ulaza"     numeric(12)          not null,
   "kolicina izlaza"    numeric(12)          not null,
   "vrednost pocetnog stanja" decimal(15,2)        not null,
   "vrednost ulaza"     decimal(15,2)        not null,
   "vrednost izlaza"    decimal(15,2)        not null,
   constraint "PK_MAGACINSKA KARTICA" primary key nonclustered ("id magacinske kartice")
)
go

/*==============================================================*/
/* Index: Kartice_FK                                            */
/*==============================================================*/
create index Kartice_FK on "Magacinska kartica" (
"id jedinice" ASC
)
go

/*==============================================================*/
/* Index: "Kartice godine_FK"                                   */
/*==============================================================*/
create index "Kartice godine_FK" on "Magacinska kartica" (
"id poslovne godine" ASC
)
go

/*==============================================================*/
/* Index: "Artikli kartice_FK"                                  */
/*==============================================================*/
create index "Artikli kartice_FK" on "Magacinska kartica" (
"id artikla" ASC
)
go

/*==============================================================*/
/* Table: Mesto                                                 */
/*==============================================================*/
create table Mesto (
   "id mesta"           int                  not null,
   "id drzave"          int                  not null,
   "zip kod"            varchar(10)          not null,
   "naziv mesta"        varchar(20)          not null,
   constraint PK_MESTO primary key nonclustered ("id mesta")
)
go

/*==============================================================*/
/* Index: Mesta_FK                                              */
/*==============================================================*/
create index Mesta_FK on Mesto (
"id drzave" ASC
)
go

/*==============================================================*/
/* Table: "Organizaciona jedinica"                              */
/*==============================================================*/
create table "Organizaciona jedinica" (
   "id jedinice"        int                  not null,
   "Org_id jedinice"    int                  null,
   "id preduzeca"       int                  not null,
   "naziv jedinice"     varchar(30)          not null,
   magacin              bit                  not null,
   constraint "PK_ORGANIZACIONA JEDINICA" primary key nonclustered ("id jedinice")
)
go

/*==============================================================*/
/* Index: Magacini_FK                                           */
/*==============================================================*/
create index Magacini_FK on "Organizaciona jedinica" (
"id preduzeca" ASC
)
go

/*==============================================================*/
/* Index: Podsektor_FK                                          */
/*==============================================================*/
create index Podsektor_FK on "Organizaciona jedinica" (
"Org_id jedinice" ASC
)
go

/*==============================================================*/
/* Table: "Popisni dokument"                                    */
/*==============================================================*/
create table "Popisni dokument" (
   "id popisnog dokumenta" int                  not null,
   "id jedinice"        int                  not null,
   "id poslovne godine" int                  not null,
   "broj popisnog dokumenta" int                  not null,
   "datum otvaranja"    datetime             not null,
   "datum knjizenja"    datetime             null,
   "status popisnog"    char(1)              not null
      constraint "CKC_STATUS POPISNOG_POPISNI" check ("status popisnog" in ('F','P')),
   constraint "PK_POPISNI DOKUMENT" primary key nonclustered ("id popisnog dokumenta")
)
go

/*==============================================================*/
/* Index: "Godina popisa_FK"                                    */
/*==============================================================*/
create index "Godina popisa_FK" on "Popisni dokument" (
"id poslovne godine" ASC
)
go

/*==============================================================*/
/* Index: "Popis magacina_FK"                                   */
/*==============================================================*/
create index "Popis magacina_FK" on "Popisni dokument" (
"id jedinice" ASC
)
go

/*==============================================================*/
/* Table: "Poslovna godina"                                     */
/*==============================================================*/
create table "Poslovna godina" (
   "id poslovne godine" int                  not null,
   "id preduzeca"       int                  not null,
   godina               char(4)              not null,
   zakljucena           bit                  not null default 0,
   constraint "PK_POSLOVNA GODINA" primary key nonclustered ("id poslovne godine")
)
go

/*==============================================================*/
/* Index: Godine_FK                                             */
/*==============================================================*/
create index Godine_FK on "Poslovna godina" (
"id preduzeca" ASC
)
go

/*==============================================================*/
/* Table: "Poslovni partner"                                    */
/*==============================================================*/
create table "Poslovni partner" (
   "id poslovnog partnera" int                  not null,
   "id mesta"           int                  not null,
   "id preduzeca"       int                  not null,
   "naziv poslovnog partnera" varchar(30)          not null,
   "adresa poslovnog partnera" varchar(30)          not null,
   "vrsta poslovnog partnera" char(1)              not null
      constraint "CKC_VRSTA POSLOVNOG P_POSLOVNI" check ("vrsta poslovnog partnera" in ('K','D','O')),
   constraint "PK_POSLOVNI PARTNER" primary key nonclustered ("id poslovnog partnera")
)
go

/*==============================================================*/
/* Index: Partneri_FK                                           */
/*==============================================================*/
create index Partneri_FK on "Poslovni partner" (
"id preduzeca" ASC
)
go

/*==============================================================*/
/* Index: "Mesto poslovnog_FK"                                  */
/*==============================================================*/
create index "Mesto poslovnog_FK" on "Poslovni partner" (
"id mesta" ASC
)
go

/*==============================================================*/
/* Table: Preduzece                                             */
/*==============================================================*/
create table Preduzece (
   "id preduzeca"       int                  not null,
   "id mesta"           int                  not null,
   "naziv preduzeca"    varchar(30)          not null,
   "broj telefona preduzeca" varchar(15)          null,
   "adresa preduzeca"   varchar(30)          null,
   constraint PK_PREDUZECE primary key nonclustered ("id preduzeca")
)
go

/*==============================================================*/
/* Index: Sediste_FK                                            */
/*==============================================================*/
create index Sediste_FK on Preduzece (
"id mesta" ASC
)
go

/*==============================================================*/
/* Table: "Prometni dokument"                                   */
/*==============================================================*/
create table "Prometni dokument" (
   "id prometnog dokumenta" int                  not null,
   "id poslovne godine" int                  not null,
   "id jedinice"        int                  null,
   "id prometa"         int                  not null,
   "Org_id jedinice"    int                  not null,
   "id poslovnog partnera" int                  null,
   "broj prometnog dokumenta" int                  not null,
   "datum prometnog"    datetime             not null,
   "datum knjizenja prometnog" datetime             null,
   "status prometnog"   char(1)              not null
      constraint "CKC_STATUS PROMETNOG_PROMETNI" check ("status prometnog" in ('F','P')),
   constraint "PK_PROMETNI DOKUMENT" primary key nonclustered ("id prometnog dokumenta")
)
go

/*==============================================================*/
/* Index: "U magacin_FK"                                        */
/*==============================================================*/
create index "U magacin_FK" on "Prometni dokument" (
"Org_id jedinice" ASC
)
go

/*==============================================================*/
/* Index: "Iz magacina_FK"                                      */
/*==============================================================*/
create index "Iz magacina_FK" on "Prometni dokument" (
"id jedinice" ASC
)
go

/*==============================================================*/
/* Index: "Vrsta prometa_FK"                                    */
/*==============================================================*/
create index "Vrsta prometa_FK" on "Prometni dokument" (
"id prometa" ASC
)
go

/*==============================================================*/
/* Index: Relationship_26_FK                                    */
/*==============================================================*/
create index Relationship_26_FK on "Prometni dokument" (
"id poslovne godine" ASC
)
go

/*==============================================================*/
/* Index: "Preko partnera_FK"                                   */
/*==============================================================*/
create index "Preko partnera_FK" on "Prometni dokument" (
"id poslovnog partnera" ASC
)
go

/*==============================================================*/
/* Table: Radnik                                                */
/*==============================================================*/
create table Radnik (
   "id radnika"         int                  not null,
   "id preduzeca"       int                  not null,
   "id mesta"           int                  not null,
   ime                  varchar(30)          not null,
   prezime              varchar(30)          not null,
   jmbg                 varchar(13)          not null,
   adresa               varchar(30)          not null,
   constraint PK_RADNIK primary key nonclustered ("id radnika")
)
go

/*==============================================================*/
/* Index: Prebivaliste_FK                                       */
/*==============================================================*/
create index Prebivaliste_FK on Radnik (
"id mesta" ASC
)
go

/*==============================================================*/
/* Index: Zaposlen_FK                                           */
/*==============================================================*/
create index Zaposlen_FK on Radnik (
"id preduzeca" ASC
)
go

/*==============================================================*/
/* Table: "Stavka popisa"                                       */
/*==============================================================*/
create table "Stavka popisa" (
   "id stavke popisa"   int                  not null,
   "id artikla"         int                  not null,
   "id popisnog dokumenta" int                  not null,
   "popisana kolicina"  numeric(12)          not null,
   "kolicina po knjigama" numeric(12)          not null,
   "prosecna cena popis" decimal(15,2)        not null,
   constraint "PK_STAVKA POPISA" primary key nonclustered ("id stavke popisa")
)
go

/*==============================================================*/
/* Index: "Stavke popisa_FK"                                    */
/*==============================================================*/
create index "Stavke popisa_FK" on "Stavka popisa" (
"id popisnog dokumenta" ASC
)
go

/*==============================================================*/
/* Index: "Artikal popisa_FK"                                   */
/*==============================================================*/
create index "Artikal popisa_FK" on "Stavka popisa" (
"id artikla" ASC
)
go

/*==============================================================*/
/* Table: "Stavka prometa"                                      */
/*==============================================================*/
create table "Stavka prometa" (
   "id stavke prometa"  int                  not null,
   "id prometnog dokumenta" int                  not null,
   "id artikla"         int                  not null,
   "kolicina prometa"   numeric(12)          not null,
   "cena prometa"       decimal(15,2)        not null,
   "vrednost prometa"   decimal(15,2)        not null,
   constraint "PK_STAVKA PROMETA" primary key nonclustered ("id stavke prometa")
)
go

/*==============================================================*/
/* Index: "Stavke prometa_FK"                                   */
/*==============================================================*/
create index "Stavke prometa_FK" on "Stavka prometa" (
"id prometnog dokumenta" ASC
)
go

/*==============================================================*/
/* Index: "Artikli prometa_FK"                                  */
/*==============================================================*/
create index "Artikli prometa_FK" on "Stavka prometa" (
"id artikla" ASC
)
go

/*==============================================================*/
/* Table: "Vrsta prometa"                                       */
/*==============================================================*/
create table "Vrsta prometa" (
   "id prometa"         int                  not null,
   "sifra prometa"      char(2)              not null,
   "naziv prometa"      varchar(20)          not null,
   constraint "PK_VRSTA PROMETA" primary key nonclustered ("id prometa")
)
go

/*==============================================================*/
/* Table: "Clan komisije"                                       */
/*==============================================================*/
create table "Clan komisije" (
   "id clana komisije"  int                  not null,
   "id radnika"         int                  not null,
   "id popisnog dokumenta" int                  not null,
   "vrsta clana"        char(1)              not null
      constraint "CKC_VRSTA CLANA_CLAN KOM" check ("vrsta clana" in ('P','Z','O')),
   constraint "PK_CLAN KOMISIJE" primary key nonclustered ("id clana komisije")
)
go

/*==============================================================*/
/* Index: Relationship_19_FK                                    */
/*==============================================================*/
create index Relationship_19_FK on "Clan komisije" (
"id popisnog dokumenta" ASC
)
go

/*==============================================================*/
/* Index: "U komisiji_FK"                                       */
/*==============================================================*/
create index "U komisiji_FK" on "Clan komisije" (
"id radnika" ASC
)
go

