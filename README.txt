I MIEI MONDI — versione 1.0

Contenuto: app web statica (PWA) per diario personale, in italiano.

COME AVVIARLA
1. Pubblica TUTTI i file della cartella su un hosting statico HTTPS (per esempio GitHub Pages o Netlify).
2. Apri l'indirizzo HTTPS con Chrome su Android.
3. Nel menu del browser scegli "Aggiungi a schermata Home" o "Installa app", se disponibile.
4. Crea una password di almeno 10 caratteri e custodiscila.

Non aprire index.html direttamente con file://: Web Crypto e le funzionalità PWA richiedono un contesto sicuro.

PRIVACY
- Il contenuto viene cifrato con AES-GCM e chiave derivata con PBKDF2 (310.000 iterazioni, SHA-256).
- Salt e dati cifrati vengono conservati in IndexedDB sul dispositivo.
- Nessun server riceve le annotazioni, se l'hosting statico è configurato senza script esterni.
- Il backup JSON è cifrato e comprende anche le foto salvate nelle annotazioni.
- Senza password NON è possibile recuperare i dati. Non è presente recupero password.
- Un dispositivo già sbloccato o compromesso può esporre dati; questa è una prima versione, non un'app sottoposta a audit di sicurezza.
- Il backup è manuale. Conservare copie fuori dal telefono.

LIMITI V1
- Il calendario mostra il mese corrente; le voci di altre date restano raggiungibili dalla ricerca o dai diari.
- Non c'è sincronizzazione tra dispositivi, PIN separato, notifiche o statistiche.
- Non è ancora stato effettuato un test su un dispositivo Android reale.
- Le immagini sono compresse e limitate a 6 per voce.
- Il backup viene esportato in JSON cifrato, non in ZIP.
