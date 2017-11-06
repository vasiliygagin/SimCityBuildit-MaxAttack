import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MaxAttack {

	public static void main(String[] args) {

		WarItemStock stock = new WarItemStock();
		stock.set(WarItem.Ammo, 21);
		stock.set(WarItem.Anvil, 20);
		stock.set(WarItem.RubberDuck, 14);
		stock.set(WarItem.RubberBoots, 10);
		stock.set(WarItem.Megaphone, 9);
		stock.set(WarItem.Binoculars, 8);
		stock.set(WarItem.Pliers, 7);
		stock.set(WarItem.Gasolline, 6);
		stock.set(WarItem.Plunger, 5);
		stock.set(WarItem.Propeller, 5);
		stock.set(WarItem.FireHydrant, 2);
//		AttackCard[] availableAttacks = AttackCard.AVAILABLE;
		AttackCard[] availableAttacks = AttackCard.ENERGY_EFFICIENT;

		// stock.set(WarItem.Anvil, 2);
		// stock.set(WarItem.RubberDuck, 2);
		// stock.set(WarItem.Plunger, 5);
		// stock.set(WarItem.Ammo, 0);
		// stock.set(WarItem.Gasolline, 0);
		// stock.set(WarItem.RubberBoots, 0);
		// stock.set(WarItem.Megaphone, 5);
		// stock.set(WarItem.Propeller, 0);
		// stock.set(WarItem.Binoculars, 5);
		// stock.set(WarItem.Pliers, 3);
		// stock.set(WarItem.FireHydrant, 7);
		// AttackCard[] availableAttacks = new AttackCard[] { AttackCard.ComicHand,
		// AttackCard.ShrinkRay, AttackCard.TentacleVortex, AttackCard.SixteenTonns };

		Damage maxDamage = calcMaxDamage(availableAttacks, stock, 0);
		System.out.println("Max Damage: " + maxDamage.damage);
		System.out.println("Max Points: " + maxDamage.points);
		System.out.println("Energy cost: " + maxDamage.damage);
		System.out.print("Cards: ");
		for (AttackCardQuantity attackCardQuantity : maxDamage.cardQuantities) {
			System.out.print(attackCardQuantity + ", ");
		}
		System.out.println();

		WarItemStock leftover = stock.remove(maxDamage.cost);
		System.out.println("Initial war items:  " + stock);
		System.out.println("Used war items:     " + maxDamage.cost);
		System.out.println("Leftover war items: " + leftover);

		Set<WarItem> notNeededItems = new HashSet<>(Arrays.asList(WarItem.values()));
		for (WarItem warItem : WarItem.values()) {
			int itemQuantity = leftover.getQuantity(warItem);
			for (AttackCard attackCard : AttackCard.AVAILABLE) {
				int cardItemQuantity = attackCard.warItemQuantities.getQuantity(warItem);
				if (itemQuantity < cardItemQuantity || itemQuantity < 4) {
					notNeededItems.remove(warItem);
					break;
				}
			}
		}
		System.out.println("Not needed Items: " + notNeededItems);
	}

	private static Damage calcMaxDamage(AttackCard[] availableAttacks, WarItemStock stock, int cardIndex) {

		if (cardIndex == availableAttacks.length) {
			return Damage.NONE;
		}
		AttackCard attackCard = availableAttacks[cardIndex];

		Damage maxDamage = Damage.NONE;
		for (int numberOfCards = 0; true; ++numberOfCards) {
			AttackCardQuantity attackCardQuantity = new AttackCardQuantity(attackCard, numberOfCards);
			WarItemStock newStock = stock.remove(attackCardQuantity);
			if (newStock == null) {
				break;
			}
			Damage damage = calcMaxDamage(availableAttacks, newStock, cardIndex + 1).prepend(attackCardQuantity);
			maxDamage = max(maxDamage, damage);
		}

		return maxDamage;

	}

	private static Damage max(Damage damage1, Damage damage2) {

		if (damage1.points > damage2.points) {
			return damage1;
		}
		if (damage2.points > damage1.points) {
			return damage2;
		}
		if (damage1.damage > damage2.damage) {
			return damage1;
		}
		if (damage2.damage > damage1.damage) {
			return damage2;
		}
		if (damage1.energy < damage2.energy) {
			return damage1;
		}
		if (damage2.energy < damage1.energy) {
			return damage2;
		}
		return damage1;
	}
}
