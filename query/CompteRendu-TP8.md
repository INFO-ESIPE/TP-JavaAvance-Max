# TP8 Java Avancé - [ Query (fonctionnelle) ](https://monge.univ-mlv.fr/ens/IR/IR2/2023-2024/JavaAvance/td10.php)
#### Max Ducoudré - INFO2


## Exercice 2 - Query
1. implantation possible.
Ce n'est pas très beau comme design, mais cela fait un seul fichier ce qui est plus pratique pour la correction.
L'interface Query doit posséder une méthode fromList qui permet de créer une Query comme expliqué ci-dessus. De plus, il doit être possible d'afficher les éléments d'une Query avec la méthode toString() qui effectue le calcul des éléments et les affiche. L'affichage contient tous les éléments présents (ceux pour qui la fonction prise en second paramètre renvoie un élément présent) séparés par le symbole " |> ".
Attention : il ne faut pas faire le calcul des éléments (savoir si ils sont présent ou non) à la création du Query, mais uniquement lorsque l'affichage est demandé.
Écrire le fichier Query.java avec l'interface et la classe d'implantation.
Vérifier que les tests unitaires "Q1" passent.
<br>

*On écrit une interface scéllée `Query<E>` qui permet uniquement `QueryImpl`. `QueryImpl<E, T>` prend 2 types paramétrées : E est le type des données qui est stocké et T est le type des données qui sera renvoyé avec l'interface. On stocke également une fonction depuis le constructeur qui permet de passer d'un type à un autre.*

```java
public sealed interface Query<E> {
	
	public static <E, T> Query<T> fromList(
			List<E> list, 
			Function<? super E, ? extends Optional<? extends T>> filter) {
		
		Objects.requireNonNull(list);
		Objects.requireNonNull(filter);
		
		return new QueryImpl<E, T>(list, filter);
	}
	
	final class QueryImpl<E, T> implements Query<T> {
		private final List<E> elements;
		private final Function<? super E, ? extends Optional<? extends T>> filter;
		
		private QueryImpl(List<E> elements, Function<? super E, ? extends Optional<? extends T>> filter) {
			Objects.requireNonNull(elements);
			Objects.requireNonNull(filter);
			this.elements = Collections.unmodifiableList(elements);
			this.filter = filter;
		}
		
		
		@Override
		public String toString() {
			return Arrays.stream(elements)
					.filter(e -> filter.apply(e).isPresent())
					.map(e -> filter.apply(e).get().toString())
					.collect(Collectors.joining(" |> "));
		}
	}
}
```

*Le constructeur est en privé car on souhaite que `QueryImpl` soit uniquement appellée depuis une méthode `from` de l'interface. On remarque également que le calcul du type de retour de l'interface est fait qu'au moment au `toString`.*


2. On souhaite ajouter une méthode toList à l'interface Query dont le but est de renvoyer dans une liste non-modifiable les éléments présents.
Écrire la méthode toList.
<br>

*La méthode toList vas renvoyer une liste non-mutable des éléments de du Query. La méthode vas donc utiliser la fonction permettant de transformer les types E (stockés) vers T.
Pour cela, il faut penser a retirer les Optionals vides.*

```java
public sealed interface Query<E> {
	
	public List<E> toList();

	/* [...] */

	final class QueryImpl<E, T> implements Query<T> {

		/* [...] */	
		
		@Override
		public List<T> toList() {
			var list = new ArrayList<T>();
			elements.forEach(e -> filter.apply(e).ifPresent(list::add));
			return Collections.unmodifiableList(list);
		}
	}	
}
```

3. On souhaite maintenant ajouter une méthode toStream qui renvoie un Stream des éléments présents dans une Query.
Note : ici, on ne vous demande pas de créer un Spliterator, il existe déjà une méthode stream() sur l'interface List.
Écrire la méthode toStream.
<br>

```java
public sealed interface Query<E> {
	
	public Stream<E> toStream();

	/* [...] */

	final class QueryImpl<E, T> implements Query<T> {

		/* [...] */	
		
		@Override
		public Stream<T> toStream() {
			return elements.stream().flatMap(e -> filter.apply(e).stream());

		}
	}	
}
```

4. On souhaite ajouter une méthode toLazyList qui renvoie une liste non-modifiable dont les éléments sont calculés dans une liste modifiable sous-jacente uniquement si on demande la taille et/ou les éléments de la liste.
Note : il existe une classe java.util.AbstractList qui peut vous servir de base pour implanter la liste paresseuse demandée.
Attention : vous veillerez à ne pas demander plusieurs fois si un même élément est présent, une seule fois devrait suffire.
Écrire la méthode toLazyList.

*La méthode `toLazyList` renvoie une AbstractList qui utilisera un cache et un iterator pour calculer les valeur au fur et à mesure des appels à `get` et `size` :*

```java
public sealed interface Query<E> {
	
	public List<E> toLazyList();

	/* [...] */

	final class QueryImpl<E, T> implements Query<T> {

		/* [...] */	
		
		@Override
		public List<T> toLazyList() {
			return new AbstractList<T>() {
				private final Iterator<E> iterator = elements.iterator();
				private final List<T> list = new ArrayList<>();

				@Override
				public T get(int index) {	
				    if (index >= list.size()) {
						while (iterator.hasNext()) {
							filter.apply(iterator.next())
								.ifPresent(list::add);
							if(index < list.size()) {
								break;
							}
						}
				    }

					Objects.checkIndex(index, list.size());
					return list.get(index);	
				}

				@Override
				public int size() {
					while(iterator.hasNext()) {
						filter.apply(iterator.next())
						.ifPresent(list::add);
					}
					
					return list.size();
				}
			};
		}
	}	
}
```
