# Concept de programmation en Java
[Les slides](https://www-igm.univ-mlv.fr/~forax/ens/java-avance/cours/pdf/1-concept.pdf)


### Ecrire des classes non-mutables 
Par exemple, s'assurer que la liste n'est pas mutable, on peut utiliser `List.copyOf`
```java
class Person {
    private final String name;
    private final List<String> pets;
    public Person(String name, List<String> pets) {
        this.name = name;
        this.pets = List.copyOf(pets); // defensive copy
        // La copy se fait que si `pets` est mutable.
    }

    public String pets() {
        return pets; // pas besoin de faire de List.copyOf car this.pets n'est pas mutable
        // Si on avait fait return List.copyOf(pets), on aurait fait une copy inutile car elle est déjà dans le constructeur
    }
}
var person = new Person("John", List.of("Garfield"));
person.pets().add("Garfield"); // exception ! 
```

### Programmation par contrat
Un objet doit toujours être valide. (Ses champs et ses méthodes ont des valeurs toujlours corrects). Exemple, pas de init() mais bien utiliser un constructeur.
De plus, cela permet d'éviter de vérifier plusieurs fois si un champs n'est pas à null.
- Faire de la lecture avant l'écriture (et pas pendant de lecture pendant l'écriture)
- Eviter au plus un setter car il faudra dupliquer du code du constructeur


### Différencier les Exception
- `IllegalArgumentException` : Si un argument des paramètres n'est pas bon
- `IllegalStateException`: Si un champ de l'objet n'est pas bon pour l'exécution de la méthode. (exemple : fichier fermé alors qu'on veut lire dedans)

### Le typage
Le typage est une information à la compilation
- A la compilation : `Point point` 
- A l'exécution : `= new Point(...)`

### Autoboxing
Permet de convertir un objet en type primitif vers un type objet. (et inversement)
```java
// auto-box
int i = 3;
Integer big = i;

// Auto-unbox
Integer big = ... // plante sur la valeur si null
int i = big
```
- Le "==" fonctionne donc que sur les `int` et non sur les `Integer`.
- Quand on a des champs d'une classe, on vas préférer stocker des type primitifs  

### Interfaces/Encapsulation
- Les interfaces ne servent qu'à la compilation.
L'instruction `vehicule.drive()` donne type `Vehicule::drive()` à la compilation tandis qu'à l'exécution, appel de `Car::drive()` ou `Bus::drive()`.

### Référence & mémoire
- Tas : alloue les objets avec des new (le free est géré par un algo nommé 'garbage collector')
- Pile d'exécution contient les références des objets
- vtable : comment est divisé un objet en mémoire (deux objets de la même classes on la même vtable) Le premier élément de la vtable est une référence vers l'objet lui-même. Chaque éléments de la vrable sont des pointeurs de fonctions ayant un paramètre implicite this.
- Polymorphisme : `vehicule.toString()` devient `vehicule.vtable[index]`. Les méthodes identiques sont sur les mêmes index.

### Header
Un objet n'est jamais vide, il a au moins un header. Il contient au moins un **hashCode** un **pointeur sur la classe** (récupérable avec getClass()) -> **64 bits**. 

### L'héritage
Une classe hérité hérite de tous les champs et méthodes de la classe parente. Certaines fois, les sous-classes n'ont pas besoin de tous ces champs.
- On peut utiliser des classes abstraite en private car pas visible de l'extérieur et donc maintenable plus facilement.

### La programmation orienté donnée
Laison tardive : (plus simple d'ajouter des sous-type que la cascade d'instance of)
```java
interface Vehicle {
 void drive();
}
record Car() implements Vehicle {
 public void drive() { /* 1 */ }
}
record Bus() implements Vehicle {
 public void drive() { /* 2 */ }
}
```

Cascade de instanceof : (permet de voir dans un seul fichier le calcul du prix de toutes les implémentations -> les données sont plus importantes que le code)
```java
sealed interface Vehicle
permits Car, Bus { }
record Car() implements Vehicle { }
record Bus() implements Vehicle { }
public static void drive(Vehicle vehicle) {
    switch(vehicle) {
        case Car car -> /* 1 */
        case Bus bus -> /* 2 */
        // Plante s'il manque une implémentation de Vehicle
    }
}
```

Pour la POD, on privilégie
- Les types fermées (record, enum, final, sealed)
- le pattern-matching (switch exaustif (pas de default), record pattern )

Possibilité de switch :
```java
switch(point) {
    case null - > … // on match null
    case Point(int x, int y) when x == y -> … // seulement si x == y
    case Point(int x, int y) -> … // dans les autres cas
}
```
Pareil pour le instance of
```java	
if (vehicle instanceof Car(var seats, var weight)) {
 // on peut utiliser seats et weight ici !
}
// un switch peut renvoyer une valeur
var text = switch(object) {
 case String s -> s;
 case Object o -> {
 yield o.toString(); // on utilise yield pour indiquer la valeur
 } // d’un bloc
}
```