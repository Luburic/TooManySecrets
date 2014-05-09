/*==============================================================*/
/* DBMS name:      Microsoft SQL Server 2008                    */
/* Created on:     09-May-14 11:56:33 AM                        */
/*==============================================================*/


if exists (select 1
            from  sysindexes
           where  id    = object_id('Analitika_magacinske_kartice')
            and   name  = 'Relationship_28_FK'
            and   indid > 0
            and   indid < 255)
   drop index Analitika_magacinske_kartice.Relationship_28_FK
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Analitika_magacinske_kartice')
            and   name  = 'Stavke_FK'
            and   indid > 0
            and   indid < 255)
   drop index Analitika_magacinske_kartice.Stavke_FK
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Analitika_magacinske_kartice')
            and   name  = 'Vrsta_FK'
            and   indid > 0
            and   indid < 255)
   drop index Analitika_magacinske_kartice.Vrsta_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Analitika_magacinske_kartice')
            and   type = 'U')
   drop table Analitika_magacinske_kartice
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
            from  sysindexes
           where  id    = object_id('Clan_komisije')
            and   name  = 'U komisiji_FK'
            and   indid > 0
            and   indid < 255)
   drop index Clan_komisije."U komisiji_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Clan_komisije')
            and   name  = 'Relationship_19_FK'
            and   indid > 0
            and   indid < 255)
   drop index Clan_komisije.Relationship_19_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Clan_komisije')
            and   type = 'U')
   drop table Clan_komisije
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Drzava')
            and   type = 'U')
   drop table Drzava
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Grupa_artikla')
            and   name  = 'Podgrupa_FK'
            and   indid > 0
            and   indid < 255)
   drop index Grupa_artikla.Podgrupa_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Grupa_artikla')
            and   type = 'U')
   drop table Grupa_artikla
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Korisnik')
            and   type = 'U')
   drop table Korisnik
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Magacinska_kartica')
            and   name  = 'Artikli kartice_FK'
            and   indid > 0
            and   indid < 255)
   drop index Magacinska_kartica."Artikli kartice_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Magacinska_kartica')
            and   name  = 'Kartice godine_FK'
            and   indid > 0
            and   indid < 255)
   drop index Magacinska_kartica."Kartice godine_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Magacinska_kartica')
            and   name  = 'Kartice_FK'
            and   indid > 0
            and   indid < 255)
   drop index Magacinska_kartica.Kartice_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Magacinska_kartica')
            and   type = 'U')
   drop table Magacinska_kartica
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
           where  id    = object_id('Organizaciona_jedinica')
            and   name  = 'Podsektor_FK'
            and   indid > 0
            and   indid < 255)
   drop index Organizaciona_jedinica.Podsektor_FK
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Organizaciona_jedinica')
            and   name  = 'Magacini_FK'
            and   indid > 0
            and   indid < 255)
   drop index Organizaciona_jedinica.Magacini_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Organizaciona_jedinica')
            and   type = 'U')
   drop table Organizaciona_jedinica
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Popisni_dokument')
            and   name  = 'Popis magacina_FK'
            and   indid > 0
            and   indid < 255)
   drop index Popisni_dokument."Popis magacina_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Popisni_dokument')
            and   name  = 'Godina popisa_FK'
            and   indid > 0
            and   indid < 255)
   drop index Popisni_dokument."Godina popisa_FK"
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Popisni_dokument')
            and   type = 'U')
   drop table Popisni_dokument
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Poslovna_godina')
            and   name  = 'Godine_FK'
            and   indid > 0
            and   indid < 255)
   drop index Poslovna_godina.Godine_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Poslovna_godina')
            and   type = 'U')
   drop table Poslovna_godina
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Poslovni_partner')
            and   name  = 'Mesto poslovnog_FK'
            and   indid > 0
            and   indid < 255)
   drop index Poslovni_partner."Mesto poslovnog_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Poslovni_partner')
            and   name  = 'Partneri_FK'
            and   indid > 0
            and   indid < 255)
   drop index Poslovni_partner.Partneri_FK
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Poslovni_partner')
            and   type = 'U')
   drop table Poslovni_partner
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
           where  id    = object_id('Prometni_dokument')
            and   name  = 'Preko partnera_FK'
            and   indid > 0
            and   indid < 255)
   drop index Prometni_dokument."Preko partnera_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Prometni_dokument')
            and   name  = 'Relationship_26_FK'
            and   indid > 0
            and   indid < 255)
   drop index Prometni_dokument.Relationship_26_FK
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Prometni_dokument')
            and   name  = 'Vrsta prometa_FK'
            and   indid > 0
            and   indid < 255)
   drop index Prometni_dokument."Vrsta prometa_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Prometni_dokument')
            and   name  = 'Iz magacina_FK'
            and   indid > 0
            and   indid < 255)
   drop index Prometni_dokument."Iz magacina_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Prometni_dokument')
            and   name  = 'U magacin_FK'
            and   indid > 0
            and   indid < 255)
   drop index Prometni_dokument."U magacin_FK"
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Prometni_dokument')
            and   type = 'U')
   drop table Prometni_dokument
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
           where  id    = object_id('Stavka_popisa')
            and   name  = 'Artikal popisa_FK'
            and   indid > 0
            and   indid < 255)
   drop index Stavka_popisa."Artikal popisa_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Stavka_popisa')
            and   name  = 'Stavke popisa_FK'
            and   indid > 0
            and   indid < 255)
   drop index Stavka_popisa."Stavke popisa_FK"
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Stavka_popisa')
            and   type = 'U')
   drop table Stavka_popisa
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Stavka_prometa')
            and   name  = 'Artikli prometa_FK'
            and   indid > 0
            and   indid < 255)
   drop index Stavka_prometa."Artikli prometa_FK"
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Stavka_prometa')
            and   name  = 'Stavke prometa_FK'
            and   indid > 0
            and   indid < 255)
   drop index Stavka_prometa."Stavke prometa_FK"
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Stavka_prometa')
            and   type = 'U')
   drop table Stavka_prometa
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Vrsta_prometa')
            and   type = 'U')
   drop table Vrsta_prometa
go

/*==============================================================*/
/* Table: Analitika_magacinske_kartice                          */
/*==============================================================*/
create table Analitika_magacinske_kartice (
   id_analitike         int                  identity,
   id_magacinske_kartice int                  not null,
   id_prometa           int                  not null,
   id_stavke_prometa    int                  null,
   "redni broj"         int                  not null,
   smer                 char(1)              not null
      constraint CKC_SMER_ANALITIK check (smer in ('U','I')),
   kolicina             numeric(12)          not null,
   cena                 decimal(15,2)        not null,
   vrednost             decimal(15,2)        not null,
   constraint PK_ANALITIKA_MAGACINSKE_KARTIC primary key nonclustered (id_analitike)
)
go

/*==============================================================*/
/* Index: Vrsta_FK                                              */
/*==============================================================*/
create index Vrsta_FK on Analitika_magacinske_kartice (
id_prometa ASC
)
go

/*==============================================================*/
/* Index: Stavke_FK                                             */
/*==============================================================*/
create index Stavke_FK on Analitika_magacinske_kartice (
id_magacinske_kartice ASC
)
go

/*==============================================================*/
/* Index: Relationship_28_FK                                    */
/*==============================================================*/
create index Relationship_28_FK on Analitika_magacinske_kartice (
id_stavke_prometa ASC
)
go

/*==============================================================*/
/* Table: Artikal                                               */
/*==============================================================*/
create table Artikal (
   id_artikla           int                  identity,
   id_grupe             int                  not null,
   sifra_artikla        char(8)              not null,
   naziv_artikla        varchar(30)          not null,
   constraint PK_ARTIKAL primary key nonclustered (id_artikla)
)
go

/*==============================================================*/
/* Index: Artikli_FK                                            */
/*==============================================================*/
create index Artikli_FK on Artikal (
id_grupe ASC
)
go

/*==============================================================*/
/* Table: Clan_komisije                                         */
/*==============================================================*/
create table Clan_komisije (
   id_clana_komisije    int                  identity,
   id_radnika           int                  not null,
   id_popisnog_dokumenta int                  not null,
   vrsta_clana          char(1)              not null
      constraint CKC_VRSTA_CLANA_CLAN_KOM check (vrsta_clana in ('P','Z','O')),
   constraint PK_CLAN_KOMISIJE primary key nonclustered (id_clana_komisije)
)
go

/*==============================================================*/
/* Index: Relationship_19_FK                                    */
/*==============================================================*/
create index Relationship_19_FK on Clan_komisije (
id_popisnog_dokumenta ASC
)
go

/*==============================================================*/
/* Index: "U komisiji_FK"                                       */
/*==============================================================*/
create index "U komisiji_FK" on Clan_komisije (
id_radnika ASC
)
go

/*==============================================================*/
/* Table: Drzava                                                */
/*==============================================================*/
create table Drzava (
   id_drzave            int                  identity,
   sifra_drzave         char(3)              not null,
   naziv_drzave         varchar(20)          not null,
   constraint PK_DRZAVA primary key nonclustered (id_drzave)
)
go

/*==============================================================*/
/* Table: Grupa_artikla                                         */
/*==============================================================*/
create table Grupa_artikla (
   id_grupe             int                  identity,
   Gru_id_grupe         int                  null,
   naziv_grupe          varchar(30)          not null,
   constraint PK_GRUPA_ARTIKLA primary key nonclustered (id_grupe)
)
go

/*==============================================================*/
/* Index: Podgrupa_FK                                           */
/*==============================================================*/
create index Podgrupa_FK on Grupa_artikla (
Gru_id_grupe ASC
)
go

/*==============================================================*/
/* Table: Korisnik                                              */
/*==============================================================*/
create table Korisnik (
   id_korisnika         int                  identity,
   username             varchar(30)          not null,
   password             varchar(65)          not null,
   password_salt        varchar(20)          not null,
   role                 char(1)              not null
      constraint CKC_ROLE_KORISNIK check (role in ('A','R')),
   constraint PK_KORISNIK primary key nonclustered (id_korisnika)
)
go

/*==============================================================*/
/* Table: Magacinska_kartica                                    */
/*==============================================================*/
create table Magacinska_kartica (
   id_magacinske_kartice int                  identity,
   id_poslovne_godine   int                  not null,
   id_artikla           int                  not null,
   id_jedinice          int                  not null,
   prosecna_cena        decimal(15,2)        not null,
   kolicina_pocetnog_stanja numeric(12)          not null,
   kolicina_ulaza       numeric(12)          not null,
   kolicina_izlaza      numeric(12)          not null,
   vrednost_pocetnog_stanja decimal(15,2)        not null,
   vrednost_ulaza       decimal(15,2)        not null,
   vrednost_izlaza      decimal(15,2)        not null,
   constraint PK_MAGACINSKA_KARTICA primary key nonclustered (id_magacinske_kartice)
)
go

/*==============================================================*/
/* Index: Kartice_FK                                            */
/*==============================================================*/
create index Kartice_FK on Magacinska_kartica (
id_jedinice ASC
)
go

/*==============================================================*/
/* Index: "Kartice godine_FK"                                   */
/*==============================================================*/
create index "Kartice godine_FK" on Magacinska_kartica (
id_poslovne_godine ASC
)
go

/*==============================================================*/
/* Index: "Artikli kartice_FK"                                  */
/*==============================================================*/
create index "Artikli kartice_FK" on Magacinska_kartica (
id_artikla ASC
)
go

/*==============================================================*/
/* Table: Mesto                                                 */
/*==============================================================*/
create table Mesto (
   id_mesta             int                  identity,
   id_drzave            int                  not null,
   zip_kod              varchar(10)          not null,
   naziv_mesta          varchar(20)          not null,
   constraint PK_MESTO primary key nonclustered (id_mesta)
)
go

/*==============================================================*/
/* Index: Mesta_FK                                              */
/*==============================================================*/
create index Mesta_FK on Mesto (
id_drzave ASC
)
go

/*==============================================================*/
/* Table: Organizaciona_jedinica                                */
/*==============================================================*/
create table Organizaciona_jedinica (
   id_jedinice          int                  identity,
   Org_id_jedinice      int                  null,
   id_preduzeca         int                  not null,
   naziv_jedinice       varchar(30)          not null,
   magacin              bit                  not null,
   constraint PK_ORGANIZACIONA_JEDINICA primary key nonclustered (id_jedinice)
)
go

/*==============================================================*/
/* Index: Magacini_FK                                           */
/*==============================================================*/
create index Magacini_FK on Organizaciona_jedinica (
id_preduzeca ASC
)
go

/*==============================================================*/
/* Index: Podsektor_FK                                          */
/*==============================================================*/
create index Podsektor_FK on Organizaciona_jedinica (
Org_id_jedinice ASC
)
go

/*==============================================================*/
/* Table: Popisni_dokument                                      */
/*==============================================================*/
create table Popisni_dokument (
   id_popisnog_dokumenta int                  identity,
   id_jedinice          int                  not null,
   id_poslovne_godine   int                  not null,
   broj_popisnog_dokumenta int                  not null,
   datum_otvaranja      datetime             not null,
   datum_knjizenja      datetime             null,
   status_popisnog      char(1)              not null
      constraint CKC_STATUS_POPISNOG_POPISNI_ check (status_popisnog in ('F','P')),
   constraint PK_POPISNI_DOKUMENT primary key nonclustered (id_popisnog_dokumenta)
)
go

/*==============================================================*/
/* Index: "Godina popisa_FK"                                    */
/*==============================================================*/
create index "Godina popisa_FK" on Popisni_dokument (
id_poslovne_godine ASC
)
go

/*==============================================================*/
/* Index: "Popis magacina_FK"                                   */
/*==============================================================*/
create index "Popis magacina_FK" on Popisni_dokument (
id_jedinice ASC
)
go

/*==============================================================*/
/* Table: Poslovna_godina                                       */
/*==============================================================*/
create table Poslovna_godina (
   id_poslovne_godine   int                  identity,
   id_preduzeca         int                  not null,
   godina               char(4)              not null,
   zakljucena           bit                  not null default 0,
   constraint PK_POSLOVNA_GODINA primary key nonclustered (id_poslovne_godine)
)
go

/*==============================================================*/
/* Index: Godine_FK                                             */
/*==============================================================*/
create index Godine_FK on Poslovna_godina (
id_preduzeca ASC
)
go

/*==============================================================*/
/* Table: Poslovni_partner                                      */
/*==============================================================*/
create table Poslovni_partner (
   id_poslovnog_partnera int                  identity,
   id_mesta             int                  not null,
   id_preduzeca         int                  not null,
   naziv_poslovnog_partnera varchar(30)          not null,
   adresa_poslovnog_partnera varchar(30)          not null,
   vrsta_poslovnog_partnera char(1)              not null
      constraint CKC_VRSTA_POSLOVNOG_P_POSLOVNI check (vrsta_poslovnog_partnera in ('K','D','O')),
   constraint PK_POSLOVNI_PARTNER primary key nonclustered (id_poslovnog_partnera)
)
go

/*==============================================================*/
/* Index: Partneri_FK                                           */
/*==============================================================*/
create index Partneri_FK on Poslovni_partner (
id_preduzeca ASC
)
go

/*==============================================================*/
/* Index: "Mesto poslovnog_FK"                                  */
/*==============================================================*/
create index "Mesto poslovnog_FK" on Poslovni_partner (
id_mesta ASC
)
go

/*==============================================================*/
/* Table: Preduzece                                             */
/*==============================================================*/
create table Preduzece (
   id_preduzeca         int                  identity,
   id_mesta             int                  not null,
   naziv_preduzeca      varchar(30)          not null,
   broj_telefona_preduzeca varchar(15)          null,
   adresa_preduzeca     varchar(30)          null,
   constraint PK_PREDUZECE primary key nonclustered (id_preduzeca)
)
go

/*==============================================================*/
/* Index: Sediste_FK                                            */
/*==============================================================*/
create index Sediste_FK on Preduzece (
id_mesta ASC
)
go

/*==============================================================*/
/* Table: Prometni_dokument                                     */
/*==============================================================*/
create table Prometni_dokument (
   id_prometnog_dokumenta int                  identity,
   id_poslovne_godine   int                  not null,
   id_jedinice          int                  null,
   id_prometa           int                  not null,
   Org_id_jedinice      int                  not null,
   id_poslovnog_partnera int                  null,
   broj_prometnog_dokumenta int                  not null,
   datum_prometnog      datetime             not null,
   datum_knjizenja_prometnog datetime             null,
   status_prometnog     char(1)              not null
      constraint CKC_STATUS_PROMETNOG_PROMETNI check (status_prometnog in ('F','P')),
   constraint PK_PROMETNI_DOKUMENT primary key nonclustered (id_prometnog_dokumenta)
)
go

/*==============================================================*/
/* Index: "U magacin_FK"                                        */
/*==============================================================*/
create index "U magacin_FK" on Prometni_dokument (
Org_id_jedinice ASC
)
go

/*==============================================================*/
/* Index: "Iz magacina_FK"                                      */
/*==============================================================*/
create index "Iz magacina_FK" on Prometni_dokument (
id_jedinice ASC
)
go

/*==============================================================*/
/* Index: "Vrsta prometa_FK"                                    */
/*==============================================================*/
create index "Vrsta prometa_FK" on Prometni_dokument (
id_prometa ASC
)
go

/*==============================================================*/
/* Index: Relationship_26_FK                                    */
/*==============================================================*/
create index Relationship_26_FK on Prometni_dokument (
id_poslovne_godine ASC
)
go

/*==============================================================*/
/* Index: "Preko partnera_FK"                                   */
/*==============================================================*/
create index "Preko partnera_FK" on Prometni_dokument (
id_poslovnog_partnera ASC
)
go

/*==============================================================*/
/* Table: Radnik                                                */
/*==============================================================*/
create table Radnik (
   id_radnika           int                  identity,
   id_preduzeca         int                  not null,
   id_mesta             int                  not null,
   ime                  varchar(30)          not null,
   prezime              varchar(30)          not null,
   jmbg                 varchar(13)          not null,
   adresa               varchar(30)          not null,
   constraint PK_RADNIK primary key nonclustered (id_radnika)
)
go

/*==============================================================*/
/* Index: Prebivaliste_FK                                       */
/*==============================================================*/
create index Prebivaliste_FK on Radnik (
id_mesta ASC
)
go

/*==============================================================*/
/* Index: Zaposlen_FK                                           */
/*==============================================================*/
create index Zaposlen_FK on Radnik (
id_preduzeca ASC
)
go

/*==============================================================*/
/* Table: Stavka_popisa                                         */
/*==============================================================*/
create table Stavka_popisa (
   id_stavke_popisa     int                  identity,
   id_artikla           int                  not null,
   id_popisnog_dokumenta int                  not null,
   popisana_kolicina    numeric(12)          not null,
   kolicina_po_knjigama numeric(12)          not null,
   prosecna_cena_popis  decimal(15,2)        not null,
   constraint PK_STAVKA_POPISA primary key nonclustered (id_stavke_popisa)
)
go

/*==============================================================*/
/* Index: "Stavke popisa_FK"                                    */
/*==============================================================*/
create index "Stavke popisa_FK" on Stavka_popisa (
id_popisnog_dokumenta ASC
)
go

/*==============================================================*/
/* Index: "Artikal popisa_FK"                                   */
/*==============================================================*/
create index "Artikal popisa_FK" on Stavka_popisa (
id_artikla ASC
)
go

/*==============================================================*/
/* Table: Stavka_prometa                                        */
/*==============================================================*/
create table Stavka_prometa (
   id_stavke_prometa    int                  identity,
   id_prometnog_dokumenta int                  not null,
   id_artikla           int                  not null,
   kolicina_prometa     numeric(12)          not null,
   cena_prometa         decimal(15,2)        not null,
   vrednost_prometa     decimal(15,2)        not null,
   constraint PK_STAVKA_PROMETA primary key nonclustered (id_stavke_prometa)
)
go

/*==============================================================*/
/* Index: "Stavke prometa_FK"                                   */
/*==============================================================*/
create index "Stavke prometa_FK" on Stavka_prometa (
id_prometnog_dokumenta ASC
)
go

/*==============================================================*/
/* Index: "Artikli prometa_FK"                                  */
/*==============================================================*/
create index "Artikli prometa_FK" on Stavka_prometa (
id_artikla ASC
)
go

/*==============================================================*/
/* Table: Vrsta_prometa                                         */
/*==============================================================*/
create table Vrsta_prometa (
   id_prometa           int                  identity,
   sifra_prometa        char(2)              not null,
   naziv_prometa        varchar(20)          not null,
   constraint PK_VRSTA_PROMETA primary key nonclustered (id_prometa)
)
go

