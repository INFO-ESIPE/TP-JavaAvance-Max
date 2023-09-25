# TP3 Java Avancé - [ Slices of bread ](https://monge.univ-mlv.fr/ens/IR/IR2/2023-2024/JavaAvance/td03.php)
#### Max Ducoudré - INFO2


## Exercice 2 - The Slice and The furious
Un slice est une structure de données qui permet de "virtuellement" découper un tableau en gardant des indices de début et de fin (from et to) ainsi qu'un pointeur sur le tableau. Cela évite de recopier tous les éléments du tableau, c'est donc beaucoup plus efficace.
Le concept d'array slicing est un concept très classique dans les langages de programmation, même si chaque langage vient souvent avec une implantation différente.

1. On va dans un premier temps créer une interface Slice avec une méthode array qui permet de créer un slice à partir d'un tableau en Java.
```java
	String[] array = new String[] { "foo", "bar" };
	Slice<String> slice = Slice.array(array);
```

*On créé une interface Slice qui possède deux méthodes d'instance : `size` et `get` et une méthode statique `array` :*
```java
public interface Slice<E> {

	int size();
	E get(int index);
	public static <E> Slice<E> array(T[] array) {
		Objects.requireNonNull(array);

		return new ArraySlice<E>(array);
	}

	/* [...] */


}
```
*La méthode statique array vas retourner une implantation de Slice : un SliceArray qui est une classe interne à l'interface : (L'interface Slice devient scéllée et permet uniquement la classe interne)*

```java
public sealed interface Slice<E> permits Slice.ArraySlice<E>  {
	/* [...] */

	final class ArraySlice<E> implements Slice<E> {
		private final E[] array;
		
		private ArraySlice(E[] array) {
			Objects.requireNonNull(array);
			this.array = array;
		}

		@Override
		public int size() {
			return array.length;
		}

		@Override
		public E get(int index) {
			if(index < 0 || index > size()) throw new IndexOutOfBoundsException("Index must be between 0 and " + String.valueOf(size()-1));
 			return array[index];
		}
	}
}
```
*Ici, la méthode get ne fabrique pas une copie de la valeur récupérée, ce qui permet de la modifier directement depuis le tableau d'origine*



2. On souhaite que l'affichage d'un slice affiche les valeurs séparées par des virgules avec un '[' et un ']' comme préfixe et suffixe.

*On ajoute une méthode toString à la classe ArraySlice :*
```java
public sealed interface Slice<E> permits Slice.ArraySlice<E>  {
	/* [...] */

	final class ArraySlice<E> implements Slice<E> {
		@Override
		public String toString() {
			return Arrays.asList(array)
				.stream()
				.map(e -> e == null ? "null" : e.toString())
				.collect(Collectors.joining(", ", "[", "]"));
		}

		/* [...] */
	}
}
```

3. On souhaite ajouter une surcharge à la méthode array qui, en plus de prendre le tableau en paramètre, prend deux indices from et to et montre les éléments du tableau entre from inclus et to exclus.
Par exemple
```java
	String[] array = new String[] { "foo", "bar", "baz", "whizz" };
	Slice<String> slice = Slice.array(array, 1, 3);
```

En terme d'implantation, on va créer une autre classe interne nommée SubArraySlice implantant l'interface Slice.
Vérifier que les tests JUnit marqués "Q3" passent.
Note : pour l'affichage, il existe une méthode Arrays.stream(array, from, to) dans la classe java.util.Arrays
