# WarOfSquirrels

Features :

- Players, Cities implemented
- Player auto-created at connection
- Cities -> Create, Delete, Set diplomacy/Mayor
- Cities assistant grade, Set perms
- Invitation Ally / Join City
- Party pour déclarer des guerres
- guerres + rollback (a tester)

ToDo :

- Verifier les deco (Seems OK -- Check leave pauline)
- Verifier /war membres du grp dans la ville att ou ally
- PeaceTime (Value present -- need command et blocage war)
- Distance City-City / City-AP (normalement OK -- NEED CHECK)
- Capture & Target System
- Save RollbackList en BDD (en cas de crash)
- Reincarnation (X seconds while u are OS by everything)

Tests :

1. creer/supprimer ville
2. claim/unclaim chunk wilderness - homeblock - chunk d'une autre city
3. set ally/neutral/enemy & assistant/mayor & spawn
4. permissions sur les chunks (build, destroy, container)