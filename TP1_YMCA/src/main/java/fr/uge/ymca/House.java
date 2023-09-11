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
		
	    discounts.forEach((k, v) -> {
	    	if(!result.containsKey(v)) result.put(v, 0);
	    	
	    	
	    	villages.forEach(resident -> {
	    		//if(v == getPrice(resident)) result.put(v, result.get(v)+getPrice(resident));
	    		switch(resident) {
					case VillagePeople p -> {
						if(discountOf(p.kind()) == v) result.put(v, result.get(v)+getPrice(resident));
					}
					case Minion m -> {
						if(v == 0) result.put(v, result.get(v)+getPrice(resident));
					}
	    		};
	    	});
	    });
		
		
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
