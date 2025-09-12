# Auth Service

Microservizio **Spring Boot** per la gestione di autenticazione e autorizzazione della piattaforma.  
Fornisce:

- Registrazione e gestione utenti
- Login con email e password
- Emissione e validazione di token JWT
- Gestione di ruoli e permessi

Il servizio utilizza **PostgreSQL** (schema dedicato `auth`) ed è distribuito come immagine Docker.

---

## Avvio in locale (dev)

1. **Avvia il database Postgres** (con schemi `auth` e `note`):
   ```bash
   docker compose -f utils/docker-compose.db.yml up -d

## Configura le variabili ambiente:

2. **Crea il file `.env` a partire da `.env.example` e modifica i valori se necessario**:
   ```bash
   cp .env.example .env
   ```

3. **Avvia il servizio**:
   ```bash
   docker compose up -f docker-compose.dev.yml -d --build
   ```
4. **Verifica che il servizio sia attivo**:
    ```bash
    curl http://localhost:8081/actuator/health
    ```

---

## Rilascio in produzione (CI/CD)

Il servizio viene rilasciato automaticamente tramite **GitHub Actions** e **GitHub Container Registry** ad ogni push sul
branch `prod`.
La pipeline si trova in `.github/workflows/auth-service.yml`.

**Flusso CI/CD**

1. Esegue i test unitari con profilo `test`
2. Compila e crea il JAR
3. Costruisce l'immagine Docker su GHCR
4. Aggiorna automaticamente il servizio in produzione (server remoto) tramite SSH
5. Controlla lo stato del servizio `/actuator/health`:
    - Se non risponde, effettua il rollback all'immagine precedente
    - Se risponde, elimina l'immagine precedente -> deploy riuscito

6. Ogni tag Git `vX.Y.Z` crea una release su GitHub e un'immagine Docker con tag `tag-vX.Y.Z`.

## Variabili d'ambiente

Il servizio legge le configurazioni da `.env` (generato dalla pipeline in produzione).