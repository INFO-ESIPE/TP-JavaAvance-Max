package fr.uge.ymca;

sealed interface Resident permits Minion, VillagePeople{
	String name();
	//int price();
}
