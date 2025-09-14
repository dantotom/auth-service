Feature: Autenticazione

  Background:
    Given il database utenti è vuoto
    And esiste un utente con email "alice@example.com" e password "P@ssw0rd"

  Scenario: Login riuscito
    When effettuo login con "alice@example.com" e "P@ssw0rd"
    Then lo stato HTTP è 200
    And il body contiene un token JWT
    And il subject del token è "alice@example.com"
