# TP7 Java Avancé - [ Liste persistante (fonctionnelle) ](https://monge.univ-mlv.fr/ens/IR/IR2/2023-2024/JavaAvance/td09.php)
#### Max Ducoudré - INFO2


## Exercice 2 - Seq

1. Dans un premier temps, on va définir une classe SeqImpl qui est une implantation de l'interface Seq dans le même package que Seq.
Écrire le constructeur dans la classe SeqImpl ainsi que la méthode from(list) dans l'interface Seq sachant que, comme indiqué ci-dessus, SeqImpl contient une liste non mutable.
Expliquer pourquoi le constructeur ne doit pas être public ?
Puis déclarer les méthodes size et get() dans l'interface Seq et implanter ces méthodes dans la classe SeqImpl.
Vérifier que les tests marqués "Q1" passent.
Attention : la méthode from est une méthode publique qui prend un type paramétré en paramètre !
<br>

*L classe `SeqImpl` a son constructeur en `private` car l'utilisateur n'a pas besoin de connaître l'implémentation de `Seq`, il vas utiliser la méthode `Seq.from`.*

```java
public interface Seq<E> {
	int size();
	E get(int index);
	
	public static <E> Seq<E> from(List<? extends E> list) {
		Objects.requireNonNull(list);
		return new SeqImpl<E>(list);
	}
	
	final class SeqImpl<E> implements Seq<E> {
		private final List<E> list;

		private SeqImpl(List<? extends E> list) {
			Objects.requireNonNull(list);
			this.list = List.copyOf(list);	
		}

		@Override
		public int size() {
			return list.size();
		}

		@Override
		public E get(int index) {
			Objects.checkIndex(index, size());
			return list.get(index);
		}	
	}	
}
```

2. On souhaite écrire une méthode d'affichage permettant d'afficher les valeurs d'un Seq séparées par des virgules (suivies d'un espace), l'ensemble des valeurs étant encadré par des chevrons ('<' et '>').
Par exemple, avec le Seq créé précédemment, on obtient :
        System.out.println(seq);  // <78, 56, 34, 23>

Modifier votre code en conséquence.
Vérifier que les tests marqués "Q2" passent.
<br>

*On implémente la méthode `toString` dans la classe `SeqImpl` :*
```java
final class SeqImpl<E> implements Seq<E> {
	/* [...] */
	@Override
		public String toString() {
			return list.stream()
					.map(Object::toString)
					.collect(Collectors.joining(", ", "<", ">"));
		}
}
```

3. On souhaite écrire une méthode map qui prend en paramètre une fonction à appliquer à chaque élément d'un Seq pour créer un nouveau Seq. On souhaite avoir une implantation paresseuse, c'est-à-dire une implantation qui ne fait pas de calcul si ce n'est pas nécessaire. Par exemple, tant que personne n'accède à un élément du nouveau Seq il n'est pas nécessaire d'appliquer la fonction. L'idée est de stoker les anciens éléments ainsi que la fonction et de l'appliquer seulement si c'est nécessaire.
Bien sûr, cela va nous obliger à changer l'implantation déjà existante de SeqImpl car maintenant tous les Seq vont stocker une liste d'éléments ainsi qu'une fonction de transformation (de mapping).
Exemple d'utilisation
         var seq2 = seq.map(String::valueOf); // String.valueOf() est pas appelée
         System.out.println(seq2.get(0));     // "78", String.valueOf a été appelée 1 fois
                                              // car on demande explicitement la valeur
       

Avant de se lancer dans l'implantation de map, quelle doit être sa signature ? Quel doit être le type des éléments de la liste ? Et le type de la fonction stockée ? Dans SeqImpl, ajouter un champ correspondant à la fonction prise en paramètre par map, sans pour l'instant écrire la méthode map. On appelle cela faire un refactoring, c'est-à-dire préparer la classe pour une fonctionnalité future.
Vérifier que les tests des questions précédentes continuent de fonctionner.
Déclarer la méthode map dans l'interface Seq et écrire le code de map dans l'implantation SeqImpl.
Vérifier que les tests marqués "Q3" passent.
Note : le code doit fonctionner si l'on appelle map deux fois successivement.
<br>

*On ajoute la méthode `map` dans l'interface `Seq`. Cette méthode vas renvoyer une instance de  `Seq` renvoyant un nouveau type `R`. Avec le `mapper`, depuis le type de base `E`, on obtient un nouvel élément de type `R` *
```java
public interface Seq<E> {

	<R> Seq<R> map(Function<? super E, ? extends R> mapper);
	/* [...] */
}
```

*La classe SeqImpl vas être paramétrée par 2 types : `E` qui est le type "résulat" et `T` qui est le type qui sera contenu dans le tableau. On vas stocker le `mapper` pour passer d'un type `T` (celui du tableau) à un type `E`. Fonction de mapping qui sera utilisé lorsqu'on voudra lire dans le `Seq` avec un `get` par exemple.* <br>

```java
final class SeqImpl<E, T> implements Seq<E> {
	private final List<T> list;

	/* Mapper pour passer du type de la liste au type du Seq */
	private final Function<? super T, ? extends E> mapper;

	private SeqImpl(List<? extends T> list, Function<? super T, ? extends E> mapper) {
		Objects.requireNonNull(list);
		Objects.requireNonNull(mapper);
		this.mapper = mapper;
		this.list = List.copyOf(list);	
	}

	/* Utilise le mapper pour renvoyer l'élément du bon type */
	@Override
	public E get(int index) {
		Objects.checkIndex(index, size());
		return mapper.apply(list.get(index));
	}

	@Override
	public <R> Seq<R> map(Function<? super E, ? extends R> mapper) {
		Objects.requireNonNull(mapper);
		return new SeqImpl<R, T>(list, this.mapper.andThen(mapper)); // On concatène les mappers entre eux.
	}

	/* [...] */
}
```
*On pense à modifier la méthode `from` avec la fonction identité pour utiliser un mapper par défaut :*
```java
public interface Seq<E> {
	public static <E> Seq<E> from(List<? extends E> list) {
		Objects.requireNonNull(list);
		return new SeqImpl<E, E>(list, Function.identity());
	}

	/* [...] */
}
```

4. On souhaite avoir une méthode findFirst qui renvoie le premier élément du Seq si celui-ci existe.
Quel doit être le type de retour ?
Déclarer la méthode findFirst dans l'interface et implanter celle-ci dans la classe SeqImpl.
Vérifier que les tests marqués "Q4" passent.
Rappel: il existe une méthode findFirst sur un Stream.
<br>

*On ajoute une méthdoe findFirst par défaut dans l'interface `Seq` qui renvoie une Optional de E :*
```java
public interface Seq<E> {
	
	/* [...] */
	default Optional<E> findFirst() {
		if(size() == 0) return Optional.empty();
		return Optional.ofNullable(get(0));
	}
}
```

5. On souhaite implanter la méthode stream() qui renvoie un Stream des éléments du Seq. Pour cela, on va commencer par implanter un Spliterator. Ici, on a deux façon d'implanter le Spliterator : soit on utilise le Spliterator de la liste sous-jacente, soit on utilise des indices. Expliquer dans quel cas on utilise l'un ou l'autre, sachant que nos données sont stockées dans une List.
Ensuite, on peut créer la classe correspondant au Spliterator à deux endroits : soit comme une classe interne de la classe SeqImpl, soit comme une classe anonyme d'une méthode spliterator(start, end), quelle est à votre avis le meilleur endroit ?
Écrire les 4 méthodes du Spliterator.
Puis déclarer la méthode stream dans l'interface et implanter celle-ci dans SeqImpl sachant qu'il existe la méthode StreamSupport.stream qui permet de créer un Stream à partir de ce Spliterator.
Vérifier que les tests marqués "Q5" passent.
<br>


*On ajoute une méthode privée à la classe `SeqImpl` qui renvoie un `Spliterator` qui utilise la liste de la classe. Avec ce `SplitIterator`, on vas ensuite pouvoir créer la méthode `stream` qui utilisera `StreamSupport`:*
```java
final class SeqImpl<E, T> implements Seq<E> {

	/* [...] */

	@Override
	public Stream<E> stream() {
		var spliterator = spliterator(0, list.size());
		return StreamSupport.stream(spliterator, false);
	}
	
	private Spliterator<E> spliterator(int start, int end) {
		return new Spliterator<E>() {
			private int i = start;
			@Override
			public Spliterator<E> trySplit() { 
				var middle = (i + end) >>> 1;
				if(middle == i) {
					return null;
				}
				var spliterator = spliterator(i, middle);
				i = middle;
				return spliterator;
			}
			
			@Override
			public boolean tryAdvance(Consumer<? super E> consumer) {
				Objects.requireNonNull(consumer);
				if (i < end) {
					consumer.accept(get(i++));
					return true;
				}
				return false;
			}
			
			@Override
			public int characteristics() { 
				return ORDERED | IMMUTABLE; 
			}
			
			@Override
			public long estimateSize() { 
				return end-i; 
			}
		};
	}
}
```

6.  On souhaite ajouter une méthode of à l'interface Seq permettant d'initialiser un Seq à partir de valeurs séparées par des virgules.
Par exemple, on pourra créer le Seq de la question 2 comme ceci
         var seq = Seq.of(78, 56, 34, 23);
       

Vérifier que les tests marqués "Q6" passent.
Note : si vous avez des warnings, vous avez un problème.
Note 2 : si vous pensez un @SuppressWarnings, pensez plus fort !
<br>

*On ajoute une méthode statique à l'interface Seq qui vas prendre en argument un nombre variable d'éléments de type `E` et qui vas renvoyer un `Seq` de ces éléments :*
```java
public interface Seq<E> {
	
	/* [...] */
	
	public static <E> Seq<E> of(E ... elements) {
		return from(List.of(elements));
	}
}
```

7. On souhaite faire en sorte que l'on puisse utiliser la boucle for-each-in sur un Seq.
Par exemple,
        for(var value : seq) {
          System.out.println(value);
        }
      
Modifier votre code en conséquence.
Vérifier que les tests marqués "Q7" passent.
<br>

*Pour utiliser une boucle for-each-in, il faut que l'interface hérite d'`Iterable`. On écrit ensuite une méthode par défaut dans `Seq` qui génère un `Iterator` :*
```java
public interface Seq<E> extends Iterable<E> {
	/* [...] */

	default Iterator<E> iterator() {
		return new Iterator<E>() {
			private int i = 0;
			@Override
			public boolean hasNext() {
				return i < size();
			}

			@Override
			public E next() {
				if(!hasNext()) throw new NoSuchElementException();
				return get(i++);
			}
		};
	}
}
```

8. À la question 5, on a vu comment cacher la déclaration d'une classe à l'intérieur d'une méthode, on peut faire la même chose avec la classe SeqImpl et déclarer l'implantation sous forme d'une classe anonyme à l'intérieur d'une méthode dans l'interface.
Quelle doit être la visibilité de la méthode contenant la déclaration de la classe anonyme d'implantation ?
Modifier votre code pour que l'implantation de l'interface Seq soit dans l'interface Seq sans que cela soit visible de l'extérieur.
Vérifier que les tests marqués "Q8" passent.
<br>

*La visibilité de la méthode contenant la déclaration de la classe anonyme d'implantation doit être `public` pour que l'utilisateur puisse l'instancier. Néamoins, l'implantation de cette classe doit rester en private pour que personne ne puisse la créer directement.* 