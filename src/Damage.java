public class Damage {

    public static final Damage NONE = new Damage(0, 0, 0, new AttackCardQuantity[0]);

    public final int damage;
    public final int points;
    public final int energy;
    public final AttackCardQuantity[] cardQuantities;
    public final WarItemStock cost = new WarItemStock();

    public Damage(int damage, int points, int energy, AttackCardQuantity[] cardQuantities) {
        this.damage = damage;
        this.points = points;
        this.energy = energy;
        this.cardQuantities = cardQuantities;
        for (AttackCardQuantity cardQuantity : cardQuantities) {
            cost.add(cardQuantity);
        }
    }

    public Damage prepend(AttackCardQuantity attackCardQuantity) {

        AttackCard attackCard = attackCardQuantity.attackCard;
        int numberOfCards = attackCardQuantity.quantity;
        int newDamage = damage + attackCard.damage * numberOfCards;
        int newPoints = points + attackCard.points * numberOfCards;
        int newEnergy = energy + attackCard.energy * numberOfCards;
        AttackCardQuantity[] newCardQuantitiess = new AttackCardQuantity[cardQuantities.length + 1];
        System.arraycopy(cardQuantities, 0, newCardQuantitiess, 1, cardQuantities.length);
        newCardQuantitiess[0] = attackCardQuantity;
        return new Damage(newDamage, newPoints, newEnergy, newCardQuantitiess);
    }
}
