package fr.uge.ymca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class House {
	private final List<Resident> villages = new ArrayList<Resident>();
	
	private final Map<Kind, Integer> discounts = new HashMap<Kind, Integer>();
	
	
	public House() {}
	
	public void add(Resident resident) {
		Objects.requireNonNull(resident);
		villages.add(resident);
	}
	
	public double averagePrice() {
		return villages.stream().mapToInt(resident -> getPrice(resident)).average().orElse(Double.NaN);
	}
	public void addDiscount(Kind kind) {
		Objects.requireNonNull(kind);
		discounts.put(kind, 80);
	}
	public void removeDiscount(Kind kind) {
		Objects.requireNonNull(kind);
		if(!discounts.containsKey(kind)) throw new IllegalStateException("Discount deosn't exist");
		discounts.remove(kind);
	}
	public void addDiscount(Kind kind, int percent) {
		Objects.requireNonNull(kind);
		if(percent < 0 || percent > 100) throw new IllegalArgumentException("Percent must be between 0 and 100");
		discounts.put(kind, percent);

	}
	private int discountOf(Kind kind) {
		return discounts.getOrDefault(kind, 0);
	}
	
	public Map<Integer, Integer> priceByDiscount() {
	    var result = new HashMap<Integer, Integer>();

		// Ajout des VillagePeople avec discount
	    discounts
			.forEach((k, v) -> 
				result.put(v, 
					villages.stream()
						.filter(resident -> resident instanceof VillagePeople)
						.map(resident -> (VillagePeople) resident)
						.filter(villagePeople -> discountOf(villagePeople.kind()) == v)
						.mapToInt(this::getPrice)
						.sum()
	    ));

		// Récupération des VillagePeople sans discount
		var list = villages.stream()
				.filter(resident -> resident instanceof VillagePeople)
				.map(resident -> (VillagePeople) resident)
				.filter(villagePeople -> !discounts.containsKey(villagePeople.kind()))
				.collect(Collectors.toList());

		// Comptage des MINION dans la maison
		int minionCount = (int)villages.stream().filter(resident -> resident instanceof Minion).count();

		// Ajout des VillagePeople sans discount et des MINION dans la valeur 0
		if(list.size() > 0 || minionCount > 0) {
			result.put(0, list.stream().mapToInt(resident -> getPrice(resident)).sum() + minionCount);
		}

	    return result;
	}
	
	private int getPrice(Resident resident) {
		return switch(resident) {
			case VillagePeople p -> 100 - discountOf(p.kind());
			case Minion m -> 1;
		};
	}
	
	@Override
	public String toString() {
		return villages.isEmpty() 
				? "Empty House" 
				: "House with " + villages.stream()
									.map(Resident::name)
									.sorted()
									.collect(Collectors.joining(", "));
	}
	
	
}
