# TP11 Java Avancé - [ Structure de données spécialisée pour les types primitifs
 ](https://monge.univ-mlv.fr/ens/IR/IR2/2023-2024/JavaAvance/td11.php)
#### Max Ducoudré - INFO2


## Exercice 2 - NumericVec
1. Dans la classe fr.uge.numeric.NumericVec, on souhaite écrire une méthode longs qui prend en paramètre des entiers longs séparés par des virgules qui permet de créer un NumericVec vide contenant les valeurs prises en paramètre. Cela doit être la seule façon de pouvoir créer un NumericVec.
Écrire la méthode longs puis ajouter les méthodes, get(index) et size.
<br>

*On créer une classe `NumericVec` avec un constructeur privé. Cette classe vas stocker un tableau de `long`. La méthode statique `longs` vas créer un `NumericVec` paramétrée en `Long` et luis passer en argument une lambda pour convertir un `long` en `Long` et une lambda pour convertir un `Long` en `long` qui seront utilisé au moment du `get` et du `add` :*

```java

public class NumericVec<T> {
	private final long[] elements;
	
	private final LongFunction<T> fromLong;
	private final ToLongFunction<T> toLong;
			
	public static NumericVec<Long> longs(long ... longs) {
		Objects.requireNonNull(longs);
		return new NumericVec<Long>(
				Arrays.copyOf(longs, longs.length), 
				(e) -> (long) e, 
				(e) -> (Long) e
			);
	}
	
	private NumericVec(long[] longs, ToLongFunction<T> toLong, LongFunction<T> fromLong) {
		if(longs.length < 0) throw new IllegalArgumentException();
		Objects.requireNonNull(toLong);
		Objects.requireNonNull(fromLong);
		
		this.toLong = toLong;
		this.fromLong = fromLong;
		this.elements = longs;
	}

	public int size() {
		return elements.length;
	}
	
	public T get(int index) {
		Objects.checkIndex(index, size());
		return fromLong.apply(elements[index]);
	}
}
```

2. On souhaite ajouter une méthode add(element) qui permet d'ajouter un élément. Le tableau utilisé par NumericVec doit s'agrandir dynamiquement pour permettre d'ajouter un nombre arbitraire d'éléments.
Note : agrandir un tableau une case par une case est très inefficace !
<br>

```java
public class NumericVec<T> {

	private int size = 0;

	private NumericVec(long[] longs, ToLongFunction<T> toLong, LongFunction<T> fromLong) {
		/* [...] */		
		this.size = longs.length;
	}

	public int size() {
		return size;
	}

	public void add(T element) {
		Objects.requireNonNull(element);	
		if(size == elements.length) {
			elements = Arrays.copyOf(elements, elements.length * 2);
		}
		elements[size++] = toLong.applyAsLong(element);
	}
}
```


3. On veut maintenant ajouter les 2 méthodes ints, doubles qui permettent respectivement de créer des NumericVec d'int ou de double en prenant en paramètre des valeurs séparées par des virgules.
En termes d'implantation, l'idée est de convertir les int ou les double en long avant de les insérer dans le tableau. Et dans l'autre sens, lorsque l'on veut lire une valeur, c'est à dire quand on prend un long dans le tableau, on le convertit en le type numérique attendu. Pour cela, l'idée est de stocker dans chaque NumericVec une fonction into qui sait convertir un élément en long, et une fonction from qui sait convertir un long vers un élément.
<br>

*On ajoute 2 méthodes statiques pour construire des `NumericVec` de `Integer` et de `Double` :*

```java
public class NumericVec<T> {

	public static NumericVec<Integer> ints(int ... ints) {
		long[] longs = new long[ints.length];
		for(int i = 0; i < longs.length; i++) {
			longs[i] = ints[i];
		}
		return new NumericVec<Integer>(
				longs,
				(e) -> Integer.valueOf(e), 
				(e) -> (int) e	
			);
	}
	
	public static NumericVec<Double> doubles(double ... doubles) {
		long[] longs = new long[doubles.length];
		for(int i = 0; i < longs.length; i++) {
			longs[i] =  Double.doubleToRawLongBits(doubles[i]);
		}
		return new NumericVec<Double>(
				longs,
				Double::doubleToRawLongBits,
				Double::longBitsToDouble
			);	
	}
}
```

4. On souhaite écrire une méthode stream() qui renvoie un stream des éléments du NumericVec dans l'ordre d'insertion. Pour cela, on va créer une classe implantant l'interface Spliterator. Puis on utilisera StreamSupport.stream() pour créer le Stream à partir du Spliterator.
Note : s'l y a moins de 1024 éléments, on n'essaiera pas de couper le Spliterator.
Écrire la méthode stream qui renvoie un Stream parallélisable.
<br>


```java
public class NumericVec<T> {
	/* [...] */

	
	private Spliterator<T> fromArray(int start, int end) {
		return new Spliterator<T>() {
			private int i = start;

			@Override
			public Spliterator<T> trySplit() {
				if (size < 1024) {
					return null;
				}
				
				var middle = (i + end) >>> 1;
				if (middle == i) {
					return null;
				}
				var spliterator = fromArray(i, middle);
				i = middle;
				return spliterator;
			}

			@Override
			public boolean tryAdvance(Consumer<? super T> consumer) {
				if (i < end) {
					consumer.accept(fromLong.apply(elements[i++]));
					return true;
				}
				return false;
			}

			@Override
			public int characteristics() {
				var characteristics = SIZED | ORDERED | NONNULL | IMMUTABLE;
				return size < 1024 ?  characteristics : characteristics |SUBSIZED;
			}

			@Override
			public long estimateSize() {
				return end - i;
			}
		};
	}

	public Stream<T> stream() {
		return StreamSupport.stream(fromArray(0, size), false);
	}
}
```


5. On souhaite qu'un NumericVec implante l'interface java.util.List.
Modifier le code pour que NumericVec soit une liste.
<br>

*On fait hériter `NumericVec` de `AbstractList` et on modifie notre implémentation de add en le faisant retourner un `boolean` (toujours à `true`) :*

```java
public class NumericVec<T> extends AbstractList<T>{
	/* [...] */
	public boolean add(T element) {
		Objects.requireNonNull(element);
		if (size == elements.length) {
			elements = Arrays.copyOf(elements, (elements.length + 1) * 2);
		}
		elements[size++] = toLong.applyAsLong(element);
		return true;
	}

	@Override 
	public Spliterator<T> spliterator() {
		return fromArray(0, size);
	}
}
```
6. On souhaite ajouter une méthode addAll qui permet d'ajouter une collection à un NumericVec déjà existant. Techniquement, l'implantation de addAll que l'on reçoit de AbstractList marche déjà, mais ici, on va faire une implantation plus efficace dans le cas où le paramètre est aussi un NumericVec.
Écrire le code de la méthode addAll qui optimise le cas où le paramètre est aussi un NumericVec.
Note : que se passe-t-il si on fait un addAll() avec deux NumericVec qui n'ont pas le même type ?
<br>

