# TP7 Java Avancé - [ Liste persistante (fonctionnelle) ](https://monge.univ-mlv.fr/ens/IR/IR2/2023-2024/JavaAvance/td07.php)
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



