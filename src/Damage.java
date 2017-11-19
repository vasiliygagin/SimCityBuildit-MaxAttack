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

    public Damage(Damage damage1, Damage damage2) {
    	
        damage = damage1.damage + damage2.damage;
        points = damage1.points + damage2.points;
        energy = damage1.energy + damage2.energy;
        for (int i = 0; i < cardQuantities.length; ++i) {
        	cardQuantities[i] = damage1.cardQuantities[i] + damage2.cardQuantities[i];
        }
        cost = new WarItemStock(damage1.cost);
        cost.add(damage2.cost);
    }
}
