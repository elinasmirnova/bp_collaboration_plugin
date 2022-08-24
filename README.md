
# Kolaborační modul Freeplane
Projekt se skládá z následujících složek a souborů: 

- **server** - zdrojový kód Spring Boot serveru kolaboračního modulu
- **client** - zdrojový kód webové aplikace v React 
- **freeplane** - zdrojový kód Freeplane aplikace verze 1.9 rozšířený o kolaborační plugin
- **postman.json** - sada dotazů pro testovácí nástroj Postman

### Server 
- Otevřete složku *server* ve vývojovém prostředí (doporučeno je používat IntelliJ IDEA Ultimate Edition s podporou Spring Boot)
- Projekt se automaticky zbuildí buildovacím systémem Maven
- Spustíte server z hlavní třídy *ServerApplication*, aplikace poběží na lokální adrese http://localhost:8080.

### Client 
- V terminálu se přesuňte do složky client
- Zadejte příkaz '*yarn install*'
- Zadejte příkaz '*yarn start*', který zkompiluje a spustí aplikaci na lokální adrese http://localhost:3000.

 ### Freeplane 
- Celý návod pro spuštení desktopové Freeplane aplikace je dostupný na stránkách Freeplane https://www.freeplane.org/wiki/index.php/IDE_setup.

#### Návod pro spuštění Freeplane v IntelliJ IDEA:
- Otevřete složku *freeplane* v IntelliJ IDEA
- Zadejte příkaz '*gradle build*'
- Nakopírujte konfigurace pro spuštění aplikace ze složky *freeplane_debughelper* (*freeplane* -> *freeplane_debughelper* -> *idea* -> *runConfugurations*)
- Vložte celou složku 'runConfugurations' do složky *.idea*
- Proveďte restart IntelliJ IDEA
- Po restartu by se měly zobrazit 3 nové konfigurace pro spuštění (Run/Debug Configurations)


### Přihlašovací udaje
 #### Admin 
 	e-mail: test@test.cz
 	password: 1234
 ####  User
 	e-mail: bob@test.cz
 	password: 1234

### Kolaborace
 Pro simulaci kolaborace je nutné spustit desktopovou aplikaci Freeplane ve dvou oknech, tzn. spustit aplikace 'freeplane' a 'freeplane_copy'. 

 - Připojíte se k serveru ve dvou oknách přes záložku *Server* -> *Connect* v horním menu
 - Následně pro spuštění kolaborace zmáčknetě *Open mind map from server*
 - Zobrazí se seznam myšlenkových map
 - Pro kolaboraci je nutné si vybrat mapu, ke které má přístup i druhý uživatel. V našem případě uživatele mají přístup k mapě s ID *10* a názvem *kolaborace_pokus*
 - V druhém okně by se mělo zobrazit modální okno s hláškou, že mapa je momentálně užamčena. 
 - V prvním okně uložte mapu na server přes záložku *Save mind map on server*
 - Když přejděte znovu na druhé okno a zmačkěte OK v modálním okně, měla by se načíst aktualizováná mapa ze serveru.

### Mind maps management
 Pro správu uživatelů a kolaboratorů, přehled myšlenkových map je k dispozici webová aplikace. 
 Na stránky této aplikace se dá přejít přes záložku *Server* -> *Open mind maps management*. 
 Aplikace přesměruje Vás na stránku v prohlížeči.