Coordonator de sistem birocratic

Un sistem software capabil sa indrume si sa ajute clientii sa treaca
prin intortocheatele proceduri ale unui sistem birocratic clasic. Prin
elemente de configurare, se specifica existenta unui numar arbitrar de
birouri si un numar oarecare de documente ce pot fi cerute de catre
clienti. Nici un document nu poate fi obtinut direct; pentru a il
primi, clientul trebuie sa obtina un numar de documente intermediare.
La birouri sunt cozi, iar unele birouri au mai mult de un ghiseu care
lucreaza cu publicul. Poate sa apara situatia ca oricare din ghisee sa
fie temporar inchise, la momente neprevazute, din cauza pauzei de
cafea. Aplicatia trebuie sa simuleze tot sistemul, bazandu-se pe o
informatie de configurare care contine cel putin urmatoarele elemente:

- birourile si actele pe care le pot emite
- dependentele intre acte (de ce acte e nevoie pentru a primi un alt
act) 

Clientii vor fi simulati prin fire de executie.


TODO (ce mai e de făcut)
1) Metrice & raport (obligatoriu)
•	Colectează:
o	timp de așteptare: now - cerere.tEnqueue,
o	timp de servire: (marchează tStartService în Ghiseu înainte de sleep),
o	coadă maximă per birou (probează coada.size() la enqueue/dequeue),
o	clienți finalizați / total, throughput.
•	Stochează thread-safe (AtomicLong/LongAdder) și printează la final un raport clar.
2) Shutdown curat (verifică)
•	PauzeScheduler.stop()
•	poolClienti.shutdown() + awaitTermination
•	birou.pool.shutdownNow() + awaitTermination
•	Nicio excepție la închidere.
3) CLI / ergonomie
•	Acceptă --config=path/config.json în App.
•	(Opțional) --durationSec, --seed, --logLevel.
4) Validări suplimentare
•	Toate targets din clients există în documents.
•	(Opțional) verifică reachability: există drum (deps) către target.
5) Limitare cozi / back-pressure (opțional, util)
•	ArrayBlockingQueue(capacitate) în loc de LinkedBlockingQueue.
•	offer(req, timeout) + retry/abort cu log clar dacă e plin.
6) Logging civilizat
•	Mic util Logger (prefix [time][office|client|worker]).
•	Loguri la: enqueue/dequeue, start/end service, pauză start/end, client finalizat.
7) Scenarii & teste
•	3 fișiere: config-simple.json, config-medium.json, config-stress.json.
•	Teste:
o	unit: detector ciclu, alegere eligibil,
o	integrare: 20–50 clienți → fără deadlock, >90% finalizează.
8) README (final)
•	Ce e proiectul; cum rulezi (mvn package, java -jar ... --config ...),
•	format config.json (explicat pe scurt),
•	exemple de output + captură mică.


