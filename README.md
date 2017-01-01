# WarOfSquirrels

Features :

- Players, Cities implemented
- Player auto-created at connection
- Cities -> Create, Delete, Set diplomacy/Mayor
- Cities assistant grade, Set perms
- Invitation Ally / Join City
- Party pour déclarer des guerres
- guerres + rollback (a tester)

Besoin de tests :

- Prevent mob spawning in Chunks (Besoin de test)
- Save RollbackList en BDD (en cas de crash)
- Command de changement de target

ToDo :

    -- Module scoring --
- Ajouter les transferts d'argent (mort etc.)
- Définir quand le score est modifié et de combien
- Ajouter une commande admin pour give / set de l'argent (ou score)

    -- Module capture War --
- Capture System

Tests :

1. creer/supprimer ville
2. claim/unclaim chunk wilderness - homeblock - chunk d'une autre city
3. set ally/neutral/enemy & assistant/mayor & spawn
4. permissions sur les chunks (build, destroy, container)