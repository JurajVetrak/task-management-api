# Zadanie - Task Management API

## Cieľ zadania

Vytvorte REST API pre jednoduchý systém správy úloh pomocou Java a Spring Boot.

Aplikácia má umožňovať autentifikáciu používateľov a správu úloh podľa ich oprávnení. Cieľom zadania je overiť praktickú znalosť Spring Boot, Spring Security, práce s databázou, návrhu REST API a celkovej kvality backendového riešenia.

## Doménový model

Implementujte REST API s nasledovnými doménovými objektmi.

### User

Používateľ reprezentuje osobu, ktorá sa vie prihlásiť do aplikácie a pracovať s úlohami.

Odporúčané atribúty:

- `id`
- `firstName`
- `surname`
- `email`
- `password`
- `role`

### Role

Rola určuje oprávnenia používateľa v systéme.

Dostupné používateľské role:

- `ADMIN`
- `USER`

### Task

Úloha reprezentuje pracovnú položku patriacu konkrétnemu používateľovi.

Odporúčané atribúty:

- `id`
- `title`
- `description`
- `status`
- `createdAt`
- `updatedAt`
- `owner`

Dostupné stavy úlohy:

- `NEW`
- `IN_PROGRESS`
- `DONE`

## Požadovaná funkcionalita

Implementujte CRUD operácie nad entitou `Task`.

API musí umožňovať:

- vytvorenie úlohy
- získanie detailu úlohy
- získanie zoznamu úloh so stránkovaním, filtrovaním a vyhľadávaním podľa názvu
- úpravu úlohy
- zmazanie úlohy
- zmenu stavu úlohy
- priradenie úlohy inému používateľovi

Pri vytvorení úlohy používateľom s rolou `USER` sa ako vlastník úlohy automaticky nastaví aktuálne prihlásený používateľ.

Používateľ s rolou `ADMIN` môže pri vytvorení alebo úprave úlohy určiť, ktorému používateľovi bude úloha priradená.

Implementujte aj CRUD operácie nad entitou `User`.

API musí umožňovať:

- registrácia nového používateľa
- získanie detailu používateľa
- získanie zoznamu používateľov
- úpravu používateľa
- zmazanie používateľa

Registrácia nového používateľa je verejne dostupná bez prihlásenia.

## Autentifikácia

Použite Spring Security.

Používateľ sa musí vedieť prihlásiť pomocou:

- `email`
- `password`

Spôsob autentifikácie nechávame na vás. Môžete použiť napríklad JWT alebo Basic Authentication. Zvolený spôsob krátko popíšte v `README`.

## Autorizácia

Implementujte nasledovné pravidlá prístupu.

### USER

Používateľ s rolou `USER` môže:

- vytvárať vlastné úlohy
- zobrazovať svoje úlohy
- upravovať svoje úlohy
- meniť stav svojej úlohy
- mazať svoje úlohy
- zobraziť detail svojho používateľského účtu
- upraviť svoj používateľský účet

Používateľ s rolou `USER` nemôže:

- pristupovať k úlohám iných používateľov
- upravovať alebo mazať úlohy iných používateľov
- meniť priradenie úlohy na iného používateľa
- upravovať alebo mazať používateľské účty iných používateľov
- zobrazovať zoznam všetkých používateľov

### ADMIN

Používateľ s rolou `ADMIN` môže:

- vykonávať CRUD operácie nad všetkými úlohami
- zobrazovať všetky úlohy
- zmeniť priradenie úlohy na iného používateľa
- vykonávať CRUD operácie nad všetkými používateľmi
- zobrazovať všetkých používateľov

## REST API

Navrhnite endpointy podľa bežných REST konvencií. Pri návrhu dbajte najmä na:

- zmysluplné HTTP metódy
- správne HTTP status kódy
- validáciu vstupných dát
- zrozumiteľné chybové odpovede
- nepoužívanie interných databázových štruktúr tam, kde je vhodné použiť DTO

## Databáza

Použite relačnú databázu.

Preferovaná databáza:

- PostgreSQL

Riešenie by malo obsahovať inicializačné dáta, minimálne:

- jeden používateľ s rolou `ADMIN`
- jeden používateľ s rolou `USER`
- niekoľko ukážkových úloh

## Odovzdanie

Projekt odovzdajte ako Git repozitár alebo .zip so zdrojovými kódmi.

Riešenie by malo obsahovať:

- zdrojové kódy aplikácie
- `README.md` s návodom na spustenie
- informáciu o použitej databáze a autentifikácii
- inicializačné dáta alebo postup na ich vytvorenie
- príklady prihlasovacích údajov pre testovacích používateľov

Voliteľne môžete dodať aj:

- Postman kolekciu
- OpenAPI/Swagger dokumentáciu

## Bonusové časti

Bonusové časti nie sú povinné, ale pomôžu nám lepšie posúdiť kvalitu riešenia.

Môžete doplniť napríklad:

- globálny exception handler
- jednotnú štruktúru chybových odpovedí
- databázové migrácie
- unit testy
- integračné testy
- základné logovanie

## Využitie AI nástrojov

Na vypracovanie zadania môžte používať akékoľvek nástroje, ktoré uznáte za vhodné, vrátane využitia AI nástrojov. V prípade využitia AI vás ale chceme poprosiť, aby bola celá metodika viditeľná alebo zdokumentovaná v repozitári spolu so zdrojovým kódom (rule files, definície agentov...). Zároveň je očakávané, že budete vedieť popísať a vysvetliť fungovanie celého zdrojového kódu, vrátane generovaných súčastí.

## Záver

Nie je potrebné implementovať zbytočne komplikované riešenie. Dôležité je, aby bol kód zrozumiteľný, spustiteľný a aby bolo jasné, aké rozhodnutia ste pri implementácii urobili.
