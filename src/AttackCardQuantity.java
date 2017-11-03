
/**
 * @author vgagin
 *
 */
public class AttackCardQuantity {

    public final AttackCard attackCard;
    public final int quantity;

    public AttackCardQuantity(AttackCard attackCard, int quantity) {
        this.attackCard = attackCard;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return attackCard.name() + " (x" + quantity + ")";
    }
}
