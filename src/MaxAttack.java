public class MaxAttack {

    public static void main(String[] args) {

        WarItemStock stock = new WarItemStock();
        stock.set(WarItem.Anvil, 19);
        stock.set(WarItem.RubberDuck, 18);
        stock.set(WarItem.Plunger, 16);
        stock.set(WarItem.Ammo, 11);
        stock.set(WarItem.Gasolline, 11);
        stock.set(WarItem.RubberBoots, 11);
        stock.set(WarItem.Megaphone, 12);
        stock.set(WarItem.Propeller, 10);
        stock.set(WarItem.Binoculars, 9);
        stock.set(WarItem.Pliers, 5);
        stock.set(WarItem.FireHydrant, 4);

        Damage maxDamage = calcMaxDamage(stock, 0);
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
    }

    private static Damage calcMaxDamage(WarItemStock stock, int cardIndex) {

        if (cardIndex == AttackCard.AVAILABLE.length) {
            return Damage.NONE;
        }
        AttackCard attackCard = AttackCard.AVAILABLE[cardIndex];

        Damage maxDamage = Damage.NONE;
        for (int numberOfCards = 0; true; ++numberOfCards) {
            AttackCardQuantity attackCardQuantity = new AttackCardQuantity(attackCard, numberOfCards);
            WarItemStock newStock = stock.remove(attackCardQuantity);
            if (newStock == null) {
                break;
            }
            Damage damage = calcMaxDamage(newStock, cardIndex + 1).prepend(attackCardQuantity);
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
