public class Damage {

    public static final Damage NONE = new Damage();

    public final int damage;
    public final int points;
    public final int energy;
    public final int[] cardQuantities = new int[AttackCard.values().length];
    public final WarItemStock cost;

    private Damage() {
        this.damage = 0;
        this.points = 0;
        this.energy = 0;
        cost = new WarItemStock();
    }

    public Damage(Damage startDamage, AttackCard attackCard, int numberOfCards) {

        this.damage = startDamage.damage + attackCard.damage * numberOfCards;
        this.points = startDamage.points + attackCard.points * numberOfCards;
        this.energy = startDamage.energy + attackCard.energy * numberOfCards;
        System.arraycopy(startDamage.cardQuantities, 0, this.cardQuantities, 0, cardQuantities.length);
        this.cardQuantities[attackCard.ordinal()] += numberOfCards;
        this.cost = new WarItemStock(startDamage.cost);
        this.cost.add(attackCard, numberOfCards);
    }   
}
