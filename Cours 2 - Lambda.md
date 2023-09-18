# Lambda
[Les slides](https://www-igm.univ-mlv.fr/~forax/ens/java-avance/cours/pdf/2-lambda.pdf)


### Exemple du sort
`List.sort(comparator)`
- Un algo de trie 
- Une fonction de comparaison utilisé par le trie
-> bas besoin de plusieurs implémentation de sort.


### Interface fonctionelle
-> Un type fonction est une interface d'une méthode ayant une signature particulière.
-> Pouvoir typer une fonction (donner une signature)
```java
class MyComparators {
 static int compareByLength(String s1, String s2) { … }
}
list.sort(MyComparators::compareByLength);
```
Ou :
```java
interface Comparator<T> { int compare(T t1, T t2); }
void sort(Comparator<String> comparator) {/*Call comparator*/}
```


### Exemple d'interface fonctionelle :
```java
public interface BiFunction<T, U, V> {
 public V apply(T t, U u);
}
```
représente le type (T, U) → V
```java
BiFunction<String, Integer, Double> fun = (String s, Integer i) -> s.length() + i + 5.0;
```

Les interfaces fonctionelles pré-définies :
**–> de 0 à 2 paramètres**
- Runnable (nothing)
- Supplier (argument)
- Consumer (return)
- Function (Argument, return)
- BiFunction (2 arguments, return)

**–> spécialisé pour les types primitifs**
- IntSupplier () → int, LongSupplier () → long,
- DoubleSupplier () → double
- Predicate<T> (T) → boolean
- IntFunction<T> (int) → T
- ToIntFunction<T> (T) → int

**-> spécialisé si même type en paramètre et type de retour**
- UnaryOperator (T) → T ou BinaryOperator (T, T) → T
- DoubleBinaryOperator (double, double) → double


### Fonction de comparaison

**-> spécifier un type de fonction :** 
- Interface fonctionelle
interface Comparator<T> { int compare(T t1, T t2); }
void sort(Comparator<String> comparator) {
}

### Méthode comme fonction :
- Opérateur `::` (method reference)
```java
class MyComparators {
 static int compareByLength(String s1, String s2) { … }
}
...
list.sort(& compareByLength);
list.sort(MyComparators::compareByLength) // IntBinaryOperator
```

- Le :: permet de voir une méthode comme une interface fonctionelle
Soit `BiPredicate<String, String> function = Text::startsWith;` où 
`class Text { static boolean startsWith(String text, String prefix) {[...]}}`

- Exemple : 
```java
static void foo(ToIntFunction<String> f) { … }
static void bar(Object f) { … }
foo(Integer::parseInt); // Ok !
bar(Integer::parseInt); // compile pas
```

### Différents opérateurs ::
- Statique :
ToDoubleFunction<String> parseDouble = Double::parseDouble;

- Instance : (prend toujours this en premier paramètre)
ToIntFunction<String> stringLength = String::length; (lenght n'est pas statique)

- Constructeur :
Function<String, Person> factory = Person::new;

- Construction de Tableau
IntFunction<String[]> arrayFactory = String[]::new; -> Prend un int en paramètre (taille)

- Méthode d'instance + receveur attaché (bound)
```java
String s = "hello";
IntSupplier helloLength = s::length; 
```
->
`"hello"::length; // ne prend rien en paramètre et renvoi un entier` -> IntSupplier

- Exemple : 
```java
Person p1;
Person p2;
p1.isOlderThan(p2); 
// Same as
BiPredicate<Person, Person> older = Person::isOlderThan;
older.test(p1, p2);
```
On peut aussi faire avec un receveur attaché :
```java	
var john = new Person("John", 37);
Predicate<Person> olderThanJohn = john::isOlderThan;
olderThanJogn.test(new Person("Jane", 42));
```


### Les lambdas

Passer de :
```java
class Utils {
 static boolean startsWithAStar(String s) {
  return s.startsWith("*");
 }
}
Predicate<String> predicate = Utils::startsWithAStar;
```
à : (on l'utilise quand le code est court)
```java
Predicate<String> predicate = (String s) -> s.startsWith("*");
```




- Exemple : 
```java
record Link(int value, Link next, Link down) {
    private void printAll(UnaryOperator<Link> newLink) {
        for(var link = this; link != null; link = newLink.apply(link)) {
            System.out.println(link.value);
        }
    }
    public void printAllNext() {
        printAll(Link::next);
    }
    public void printAllDown() {
        printAll(Link:down);
    }
}
```



// lecture avec stream (fermeture automatique)
```java
//Files.lines(path) renvoie les lignes d’un fichier
try(var stream = Files.lines(path)) {
 stream. ...
}
//Files.list(path) renvoie les fichiers d’un répertoire
try(var stream = Files.list(path)) {
 stream. ...
}
```

reduce(T initial, BinaryOperatot<T> combiner)
combiner les élements deux à deux
stream.mapToInt(Person::salary)
 .reduce(0, Integer::sum)

 -> Comem si on modifiait une variable à chaque tour de boucle




 ### API des collecteur pour remplacer un foreach.
 -> à la fin du'un stream, placer les éléments dans une collection mutable