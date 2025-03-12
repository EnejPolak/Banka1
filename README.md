Dokumentacija aplikacije Izracunava Prihodkov / Odhokov 
1. E-R Model
Aplikacija temelji na podatkovni bazi z naslednjimi ključnimi entitetami:
•	uporabniki: vsebuje informacije o uporabnikih (id, ime, email, geslo, kraj_id)
•	kraji: vsebuje kraje z ID-jem in poštno številko
•	kategorije: kategorije transakcij, vezane na uporabnike
•	transakcije: beleži finančne transakcije uporabnikov
•	log_transakcij: beleži operacije, izvedene nad transakcijami
•	transakcije_audit: spremlja spremembe transakcij
Relacije med entitetami so:
•	uporabniki -> kraji (vsak uporabnik pripada določenemu kraju)
•	kategorije -> uporabniki (vsaka kategorija pripada določenemu uporabniku)
•	transakcije -> uporabniki (vsaka transakcija pripada določenemu uporabniku)
•	transakcije -> kategorije (vsaka transakcija je dodeljena določeni kategoriji)
•	log_transakcij -> uporabniki (dnevnik operacij nad transakcijami)
•	transakcije_audit -> transakcije (spremljanje sprememb transakcij)

2. Opis strežniških podprogramov
Funkcije in procedure v PostgreSQL
1. sp_registriraj_uporabnika(p_ime, p_email, p_geslo_hash, p_kraj_id)
•	Ta funkcija preveri, ali e-poštni naslov že obstaja v tabeli uporabniki.
•	Če e-pošta obstaja, funkcija vrne 1 (napaka: uporabnik že obstaja).
•	Če e-pošta še ne obstaja, se v bazo vstavi nov uporabnik s podanimi podatki.
•	Funkcija vrne 0, če je registracija uspešna, ali -1, če pride do druge napake.
2. sp_prijava(p_email, p_geslo_hash)
•	Sprejme e-poštni naslov in hash gesla ter preveri, ali obstaja ustrezen uporabnik.
•	Če uporabnik obstaja, vrne njegov id.
•	Če prijava ni uspešna, vrne -1.
3. sp_dodaj_transakcijo(p_uporabnik_id, p_kategorija_id, p_znesek, p_tip, p_opis, p_datum_transakcije)
•	Vstavi novo transakcijo v tabelo transakcije.
•	Shranijo se podatki o uporabniku, kategoriji, znesku, tipu (prihodek/odhodek), opisu in datumu transakcije.
•	Funkcija vrne TRUE, če je bila transakcija uspešno dodana, ali FALSE, če ni bila vstavljena.
4. sp_izbrisi_transakcijo(p_id)
•	Briše transakcijo na podlagi podanega id.
•	Če transakcija obstaja in je uspešno izbrisana, funkcija vrne TRUE, sicer FALSE.
5. sp_dobi_transakcije_30dni(p_uporabnik_id)
•	Pridobi vse transakcije uporabnika za zadnjih 30 dni.
•	Iz podatkovne baze se izberejo transakcije, razvrščene po datumu v padajočem vrstnem redu.
6. Triggerji
•	trg_check_positive_znesek: preprečuje vstavljanje negativnih ali ničelnih zneskov v transakcije.
•	trg_prepreciBrisanjeStarihTransakcij: preprečuje brisanje transakcij, starejših od 30 dni.
•	trg_log_transakcije_changes: beleži vse spremembe v transakcije_audit.

3. Opis delovanja aplikacije
Aplikacija omogoča uporabnikom upravljanje njihovih transakcij preko spletnega vmesnika, kjer lahko registrirajo svoj račun, se prijavijo, dodajajo transakcije, jih pregledajo in brišejo. Podatkovna baza hrani vse potrebne informacije o uporabnikih, transakcijah, kategorijah in krajih, pri čemer so posamezne entitete povezane med seboj s tujimi ključi.

Ko uporabnik odpre stran za registracijo (Registracija.html), se mu prikaže obrazec s polji za vnos imena, e-poštnega naslova, izbiro kraja in gesla. Seznam krajev se pridobi iz podatkovne baze s pomočjo servleta DobiKrajeServlet, ki pokliče funkcijo sp_dobi_kraje, ta pa vrne seznam vseh krajev, shranjenih v tabeli kraji. Ti podatki se nato dinamično izpišejo v dropdown meni, kar omogoča uporabniku izbiro ustreznega kraja. Ko uporabnik vnese vse potrebne podatke in pritisne gumb za registracijo, se ti podatki pošljejo v RegistracijaServlet, ki izvede klic funkcije sp_registriraj_uporabnika. Ta funkcija najprej preveri, ali e-pošta že obstaja v bazi. Če e-pošta že obstaja, se uporabniku prikaže napaka. Če e-pošta še ni registrirana, se podatki shranijo v tabelo uporabniki in uporabnik je preusmerjen na stran za prijavo.

Ob prijavi uporabnik odpre stran Prijava.html, kjer vnese e-pošto in geslo. Ko pritisne gumb za prijavo, se podatki pošljejo v PrijavaServlet, ki izvede preverjanje pristnosti uporabnika s pomočjo funkcije sp_prijava. Ta funkcija preveri, ali v tabeli uporabniki obstaja zapis s podanim e-poštnim naslovom in ustreznim hashom gesla. Če preverjanje uspe, funkcija vrne uporabnikov ID, ki se shrani v sejo in uporabnika preusmeri na glavno stran aplikacije. Če podatki niso pravilni, se prikaže napaka.

Po uspešni prijavi se uporabniku naloži glavna stran ndex.html, kjer so prikazane njegove pretekle transakcije in obrazec za dodajanje novih. Ob nalaganju strani DobiTransakcijeServlet pokliče funkcijo sp_dobi_transakcije_30dni, ki pridobi vse transakcije uporabnika v zadnjih 30 dneh. Te transakcije se nato prikažejo na strani, ločeno glede na tip (prihodek ali odhodek). Poleg tega uporabnik na strani vidi možnost dodajanja nove transakcije, kjer izbere kategorijo iz dropdown menija, vnese znesek, datum in opis transakcije. Seznam kategorij se pridobi s pomočjo DobiKategorijeServlet, ki pokliče funkcijo sp_dobi_kategorije_za_uporabnika in vrne seznam uporabnikovih kategorij. Če uporabnik želi dodati novo kategorijo, se ta ustvari s pomočjo funkcije sp_dobi_ali_naredi_kategorijo.

Ko uporabnik pritisne gumb za dodajanje transakcije, se podatki pošljejo v DodajTransakcijoServlet, ki izvede klic funkcije sp_dodaj_transakcijo. Ta funkcija vstavi nov zapis v tabelo transakcije in vrne TRUE, če je bila transakcija uspešno dodana. Po uspešnem dodajanju se posodobi prikaz transakcij na strani, tako da uporabnik takoj vidi novo transakcijo.

Če uporabnik želi izbrisati transakcijo, pritisne gumb "Izbriši" poleg želene transakcije. Ta zahteva se pošlje v IzbrisiTransakcijoServlet, ki izvede klic funkcije sp_izbrisi_transakcijo. Pred brisanjem preveri, ali transakcija ni starejša od 30 dni, saj funkcija trg_prepreciBrisanjeStarihTransakcij preprečuje brisanje starejših transakcij. Če je brisanje dovoljeno, se transakcija odstrani iz baze in se uporabniku posodobi prikaz.

Ko želi uporabnik zapustiti aplikacijo, pritisne gumb za odjavo. Ta akcija sproži klic na LogoutServlet, ki prekine uporabniško sejo in uporabnika preusmeri nazaj na prijavno stran. S tem se zagotovi, da uporabnikova seja ni več aktivna in da do občutljivih podatkov ne more dostopati nihče drug.

Celoten sistem zagotavlja varno shranjevanje uporabniških podatkov, ustrezno preverjanje pri registraciji in prijavi ter omogoča uporabniku učinkovito upravljanje njegovih transakcij. 
