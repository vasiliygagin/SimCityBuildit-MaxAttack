import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MaxAttack {

    public static void main(String[] args) {

        WarItemStock stock = new WarItemStock();
        AttackCard[] availableAttacks;
        boolean vasiliy = true;
        if (vasiliy) {
            availableAttacks = new AttackCard[] { AttackCard.ComicHand, AttackCard.ShrinkRay, AttackCard.GiantRockMonster, AttackCard.TentacleVortex,
                    AttackCard.PlantMonster };
            availableAttacks = AttackCard.filterEnergyEfficient(availableAttacks);

            stock.set(WarItem.Ammo, 23);
            stock.set(WarItem.Anvil, 21);
            stock.set(WarItem.Propeller, 9);
            stock.set(WarItem.RubberDuck, 8);
            stock.set(WarItem.Binoculars, 5);

            stock.set(WarItem.Gasolline, 9);
            stock.set(WarItem.RubberBoots, 6);
            stock.set(WarItem.Plunger, 3);
            stock.set(WarItem.Pliers, 7);
            stock.set(WarItem.FireHydrant, 3);
            stock.set(WarItem.Megaphone, 7);

        } else {
            availableAttacks = new AttackCard[] { AttackCard.ComicHand, AttackCard.ShrinkRay, AttackCard.TentacleVortex, AttackCard.SixteenTonns };
            stock.set(WarItem.FireHydrant, 7);
            stock.set(WarItem.Binoculars, 5);
            stock.set(WarItem.Plunger, 5);
            stock.set(WarItem.Anvil, 3);
            stock.set(WarItem.RubberDuck, 2);
            stock.set(WarItem.Ammo, 0);
            stock.set(WarItem.Gasolline, 0);
            stock.set(WarItem.RubberBoots, 0);
            stock.set(WarItem.Megaphone, 1);
            stock.set(WarItem.Propeller, 0);
            stock.set(WarItem.Pliers, 3);
        }

        int maxEnergy = Integer.MAX_VALUE;
         Damage maxDamage = calcMaxDamage(availableAttacks, stock, 0, maxEnergy);

//        WarItemStock leftoverStock = stock.remove(AttackCard.ComicHand, 5);
//        Damage maxDamage = calcMaxDamage(availableAttacks, leftoverStock, 0, maxEnergy);
//        maxDamage = new Damage(maxDamage, AttackCard.ComicHand, 5);

        System.out.println("Max Damage: " + maxDamage.damage);
        System.out.println("Max Points: " + maxDamage.points);
        System.out.println("Energy cost: " + maxDamage.damage);
        System.out.print("Cards: ");
        for (AttackCard attackCard : AttackCard.values()) {
            int quantity = maxDamage.cardQuantities[attackCard.ordinal()];
            if (quantity > 0) {
                System.out.print(attackCard.name() + " (x" + quantity + "), ");
            }
        }
        System.out.println();

        WarItemStock leftover = stock.remove(maxDamage.cost);
        System.out.println("Initial war items:  " + stock);
        System.out.println("Used war items:     " + maxDamage.cost);
        System.out.println("Leftover war items: " + leftover);

        Set<WarItem> neededItems = new HashSet<>();
        for (WarItem warItem : WarItem.values()) {
            int itemQuantity = leftover.getQuantity(warItem);
            for (AttackCard attackCard : availableAttacks) {
                int cardItemQuantity = attackCard.warItemQuantities.getQuantity(warItem);
                if (itemQuantity < cardItemQuantity || itemQuantity < 4) {
                    neededItems.add(warItem);
                    break;
                }
            }
        }
        System.out.println("Needed Items: " + neededItems);

        evaluateLimitedEnergyScenario(stock, availableAttacks, maxDamage);
    }

    static void evaluateLimitedEnergyScenario(WarItemStock stock, AttackCard[] availableAttacks, Damage maxDamage) {

        try (Formatter formatter = new Formatter(System.out)) {

            formatter.format("Dmg Point Eng CH SR GR NK TV FR DT PM Fi AC 16 Sp\n");
            for (int i = maxDamage.energy; i > 0; --i) {
                int limitedEnergy = i;
                Damage limitedDamage = calcMaxDamage(availableAttacks, stock, 0, limitedEnergy);

                Map<AttackCard, Integer> cardQuantities = new HashMap<>();
                for (AttackCard attackCard : AttackCard.values()) {
                    cardQuantities.put(attackCard, 0);
                }
                for (AttackCard attackCard : AttackCard.values()) {
                    cardQuantities.put(attackCard, limitedDamage.cardQuantities[attackCard.ordinal()]);
                }
                formatter.format("%3d %5d %3d %2d %2d %2d %2d %2d %2d %2d %2d %2d %2d %2d %2d\n", limitedDamage.damage, limitedDamage.points,
                        limitedDamage.energy, //
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
        }
    }

    private static Damage calcMaxDamage(AttackCard[] availableAttacks, WarItemStock stock, int cardIndex, int maxEnergy) {

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
