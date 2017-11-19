import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MaxAttack {

	public static void main(String[] args) throws Exception {

		Date endOfWar = null;
		long energySnapshot = 0;
		int currentEnergy = 0;

		WarItemStock stock = new WarItemStock();
		AttackCard[] availableAttacks;
		boolean vasiliy = true;
		if (vasiliy) {
			availableAttacks = new AttackCard[] { AttackCard.ComicHand, AttackCard.ShrinkRay,
					AttackCard.GiantRockMonster, AttackCard.TentacleVortex, AttackCard.PlantMonster };
			availableAttacks = AttackCard.filterEnergyEfficient(availableAttacks);

			endOfWar = parse("11/19/2017 17:00");
			energySnapshot = parse("11/19/2017 14:05").getTime();
			currentEnergy = 4;

			stock.set(WarItem.Ammo, 22);
			stock.set(WarItem.Anvil, 21);
			stock.set(WarItem.Propeller, 9);

			stock.set(WarItem.Megaphone, 20);
			stock.set(WarItem.Pliers, 17);

			stock.set(WarItem.RubberBoots, 13);
			stock.set(WarItem.Plunger, 12);
			stock.set(WarItem.Gasolline, 15);
			stock.set(WarItem.RubberDuck, 4);

			stock.set(WarItem.Binoculars, 11);
			stock.set(WarItem.FireHydrant, 6);

		} else {
			availableAttacks = new AttackCard[] { AttackCard.ComicHand, AttackCard.ShrinkRay, AttackCard.TentacleVortex,
					AttackCard.PlantMonster, AttackCard.SixteenTonns };
			stock.set(WarItem.FireHydrant, 6);
			stock.set(WarItem.Binoculars, 5);
			stock.set(WarItem.Anvil, 1);
			stock.set(WarItem.Pliers, 2);
			stock.set(WarItem.Plunger, 4);
			stock.set(WarItem.Megaphone, 1);
			stock.set(WarItem.RubberDuck, 3);
			stock.set(WarItem.RubberBoots, 1);
			stock.set(WarItem.Ammo, 0);
			stock.set(WarItem.Gasolline, 0);
			stock.set(WarItem.Propeller, 0);
		}

		int maxEnergy = Integer.MAX_VALUE;
		if (endOfWar != null) {
			long x3Millis = endOfWar.getTime() - (2 * 60 * 60 * 1000);
			int energyToGenerate;
			if (energySnapshot < x3Millis) {
				energyToGenerate = (int)((x3Millis - energySnapshot) / (15 * 60 * 1000) + 24);
			} else {
				energyToGenerate = (int)((endOfWar.getTime() - energySnapshot) / (5 * 60 * 1000) + 24);
			}
			System.out.println(energyToGenerate);
			maxEnergy = energyToGenerate + currentEnergy;
		}

		System.out.println("                  " + consiseDamageHeader());
		System.out.println(
				"Initial war items:                                                    " + consiseWarItemStock(stock));
		Damage maxDamage = calcMaxDamage(availableAttacks, stock, 0, maxEnergy);
		System.out.println("Unlimited energy: " + consiseDamageReport(maxDamage));

		WarItemStock leftover = stock.remove(maxDamage.cost);
		System.out.println("Leftover war items:                                                   "
				+ consiseWarItemStock(leftover));

		analyzeEfficientEnenrgy(stock, availableAttacks, maxDamage);

		int maxUsableEnergy = analyseLimitedEnerg(stock, availableAttacks, maxDamage);

		evaluateLimitedEnergyScenario(stock, availableAttacks, maxUsableEnergy);
	}

	private static Date parse(String source) throws ParseException {
		return new SimpleDateFormat("MM/dd/yyyy hh:mm").parse(source);
	}

	private static void analyzeEfficientEnenrgy(WarItemStock stock, AttackCard[] availableAttacks, Damage maxDamage) {
		Damage efficientDamage = maxDamage;
		while (efficientDamage.energy > efficientDamage.damage) {
			efficientDamage = calcMaxDamage(availableAttacks, stock, 0, efficientDamage.energy - 1);
		}

		Damage leftoverDamage = calcMaxDamage(availableAttacks, stock.remove(efficientDamage.cost), 0,
				Integer.MAX_VALUE);

		Damage totalDamage = new Damage(efficientDamage, leftoverDamage);
		System.out.println("                  " + consiseDamageHeader());
		System.out.println("Efficient energy: " + consiseDamageReport(efficientDamage));
		System.out.println("Leftover items:   " + consiseDamageReport(leftoverDamage));
		System.out.println("Total damage:     " + consiseDamageReport(totalDamage));

		WarItemStock leftoverStock = stock.remove(totalDamage.cost);
		System.out.println("Leftover war items:                                                   "
				+ consiseWarItemStock(leftoverStock));
		printNeededItems(leftoverStock, availableAttacks);
	}

	private static int analyseLimitedEnerg(WarItemStock stock, AttackCard[] availableAttacks, Damage maxDamage) {
		int maxUsableEnergy = maxDamage.energy;
		int targetEnergy = maxUsableEnergy - Integer.min(8, (int) (maxUsableEnergy * 0.15));

		Damage limitedDamage = calcMaxDamage(availableAttacks, stock, 0, targetEnergy);
		WarItemStock leftoverStock = stock.remove(limitedDamage.cost);
		Damage leftoverDamage = calcMaxDamage(availableAttacks, leftoverStock, 0, Integer.MAX_VALUE);
		Damage totalDamage = new Damage(limitedDamage, leftoverDamage);
		System.out.println("                  " + consiseDamageHeader());
		System.out.println("Limited energy:   " + consiseDamageReport(limitedDamage));
		System.out.println("Leftover items:   " + consiseDamageReport(leftoverDamage));
		System.out.println("Total damage:     " + consiseDamageReport(totalDamage));
		return maxUsableEnergy;
	}

	private static void printCards(Damage totalDamage) {
		System.out.print("Cards: ");
		for (AttackCard attackCard : AttackCard.values()) {
			int quantity = totalDamage.cardQuantities[attackCard.ordinal()];
			if (quantity > 0) {
				System.out.print(attackCard.name() + " (x" + quantity + "), ");
			}
		}
		System.out.println();
	}

	private static void printNeededItems(WarItemStock leftoverStock, AttackCard[] availableAttacks) {
		Set<WarItem> neededItems = new HashSet<>();
		for (WarItem warItem : WarItem.values()) {
			int itemQuantity = leftoverStock.getQuantity(warItem);
			for (AttackCard attackCard : availableAttacks) {
				int cardItemQuantity = attackCard.warItemQuantities.getQuantity(warItem);
				if (cardItemQuantity > 0 && (itemQuantity < cardItemQuantity || itemQuantity < 4)) {
					neededItems.add(warItem);
					break;
				}
			}
		}
		System.out.println("Needed Items: " + neededItems);
	}

	private static void evaluateLimitedEnergyScenario(WarItemStock stock, AttackCard[] availableAttacks,
			int maxEnergy) {
		try (Formatter formatter = new Formatter(System.out)) {

			System.out.println(
					"|              Limited Energy Damage              -                 Leftover Damage                 |");
			String damageHeader = consiseDamageHeader();
			System.out.print(damageHeader);
			System.out.print(" - ");
			System.out.print(damageHeader);
			System.out.println();
			for (int i = maxEnergy; i > 0; --i) {
				int limitedEnergy = i;
				Damage limitedDamage = calcMaxDamage(availableAttacks, stock, 0, limitedEnergy);
				WarItemStock leftoverStock = stock.remove(limitedDamage.cost);
				Damage leftoverDamage = calcMaxDamage(availableAttacks, leftoverStock, 0, Integer.MAX_VALUE);

				System.out.println(consiseDamageReport(limitedDamage) + " - " + consiseDamageReport(leftoverDamage));
				System.out.println();
			}
		}
	}

	private static String consiseDamageHeader() {
		return "Dmg Point Eng  CH SR GR NK TV FR DT PM Fi AC 16 Sp  Pg RD Pl Mg FH Bi An Pr Am Ga RB";
	}

	private static CharSequence consiseDamageReport(Damage damage) {

		StringBuilder sb = new StringBuilder(1000);
		try (Formatter f = new Formatter(sb);) {

			Map<AttackCard, Integer> cardQuantities = new HashMap<>();
			for (AttackCard attackCard : AttackCard.values()) {
				cardQuantities.put(attackCard, 0);
			}

			for (AttackCard attackCard : AttackCard.values()) {
				int numberOfCards = damage.cardQuantities[attackCard.ordinal()];
				cardQuantities.put(attackCard, numberOfCards);
			}

			f.format("%3d %5d %3d  %2d %2d %2d %2d %2d %2d %2d %2d %2d %2d %2d %2d  ", //
					damage.damage, damage.points, //
					damage.energy, //
					cardQuantities.get(AttackCard.ComicHand), //
					cardQuantities.get(AttackCard.ShrinkRay), //
					cardQuantities.get(AttackCard.GiantRockMonster), //
					cardQuantities.get(AttackCard.NotInKansas), //
					cardQuantities.get(AttackCard.TentacleVortex), //
					cardQuantities.get(AttackCard.FlyingVuRobot), //
					cardQuantities.get(AttackCard.DiscoTwister), //
					cardQuantities.get(AttackCard.PlantMonster), //
					cardQuantities.get(AttackCard.Fishaster), //
					cardQuantities.get(AttackCard.AncientCurse), //
					cardQuantities.get(AttackCard.SixteenTonns), //
					cardQuantities.get(AttackCard.Spiders) //
			);
		}
		sb.append(consiseWarItemStock(damage.cost));
		return sb;
	}

	private static CharSequence consiseWarItemStock(WarItemStock stock) {

		StringBuilder sb = new StringBuilder(1000);
		try (Formatter f = new Formatter(sb);) {

			f.format("%2d %2d %2d %2d %2d %2d %2d %2d %2d %2d %2d", //
					stock.getQuantity(WarItem.Plunger), //
					stock.getQuantity(WarItem.RubberDuck), //
					stock.getQuantity(WarItem.Pliers), //
					stock.getQuantity(WarItem.Megaphone), //
					stock.getQuantity(WarItem.FireHydrant), //
					stock.getQuantity(WarItem.Binoculars), //
					stock.getQuantity(WarItem.Anvil), //
					stock.getQuantity(WarItem.Propeller), //
					stock.getQuantity(WarItem.Ammo), //
					stock.getQuantity(WarItem.Gasolline), //
					stock.getQuantity(WarItem.RubberBoots) //
			);
		}
		return sb;
	}

	private static Damage calcMaxDamage(AttackCard[] availableAttacks, WarItemStock stock, int cardIndex,
			int maxEnergy) {

		if (cardIndex == availableAttacks.length) {
			return Damage.NONE;
		}
		AttackCard attackCard = availableAttacks[cardIndex];

		Damage maxDamage = Damage.NONE;
		for (int numberOfCards = 0; true; ++numberOfCards) {

			int newMaxEnergy = maxEnergy - attackCard.energy * numberOfCards;
			if (newMaxEnergy < 0) {
				break;
			}

			WarItemStock newStock = stock.remove(attackCard, numberOfCards);
			if (newStock == null) {
				break;
			}
			Damage maxDamageWithoutThisCard = calcMaxDamage(availableAttacks, newStock, cardIndex + 1, newMaxEnergy);
			Damage damage = new Damage(maxDamageWithoutThisCard, attackCard, numberOfCards);
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
