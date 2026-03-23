
# MouseBot

MouseBot je jednostavna aplikacija za crtanje s mnoštvo opcija razvijena koristeći programski jezik Java i okvir [JavaFX](https://openjfx.io/). 

Ovaj projekt je razvijen tijekom srednje škole, a to znači da programski
kod nije baš na kvalitetnoj razini.

Posebnost aplikacije je što se nacrtani crtež može pretvoriti u niz pomaka i pritisaka miša te tako precrtati u nekoj drugoj aplikaciji za crtanje (npr. Microsoft Bojanje).

Primjer rada aplikacije: https://youtu.be/R0ltH5MjjHM

Značajke aplikacije:
* crtanje raznih geometrijskih oblika,
* pomicanje, uređivanje, kopiranje postojećih oblika,
* interaktivni radni prostor (pomicanje, zumiranje),
* podjela crteža na različite slojeve,
* promjena redoslijeda kojim se iscrtava crtež,
* pohranjivanje crtaža u datoteku i ponovno učitavanje,
* lokalizacija aplikacije na više jezika (hrvatski, engleski),
* poništi-ponovi (eng. undo-redo) način rada.

## Pokretanje

```shell script
mvn javafx:run
```
