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


