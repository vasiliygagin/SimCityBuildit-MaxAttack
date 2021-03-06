import java.util.Formatter;

public class WarItemStock {

    private int[] quantities = new int[WarItem.values().length];

    public WarItemStock() {
    }

    public WarItemStock(WarItemStock source) {
        System.arraycopy(source.quantities, 0, quantities, 0, quantities.length);
    }

    public void set(WarItem warItem, int quantity) {
        quantities[warItem.ordinal()] = quantity;
    }

    public WarItemStock remove(AttackCardQuantity attackCardQuantity) {

        AttackCard attackCard = attackCardQuantity.attackCard;
        int numberOfCards = attackCardQuantity.quantity;

        return remove(attackCard, numberOfCards);
    }

    public WarItemStock remove(AttackCard attackCard, int numberOfCards) {

        WarItemStock warItemQuantities = attackCard.warItemQuantities;
        WarItemStock newStock = new WarItemStock(this);
        for (int i = 0; i < quantities.length; ++i) {
            int leftover = quantities[i] - warItemQuantities.quantities[i] * numberOfCards;
            if (leftover < 0) {
                return null;
            }
            newStock.quantities[i] = leftover;
        }
        return newStock;
    }

    public void add(AttackCardQuantity cardQuantity) {

        AttackCard attackCard = cardQuantity.attackCard;
        int numberOfCards = cardQuantity.quantity;
        add(attackCard, numberOfCards);
    }

    public void add(AttackCard attackCard, int numberOfCards) {

        WarItemStock cardWarItemQuantities = attackCard.warItemQuantities;
        for (int i = 0; i < quantities.length; ++i) {
            quantities[i] += cardWarItemQuantities.quantities[i] * numberOfCards;
        }
    }

    public void add(WarItemStock stock) {

        for (int i = 0; i < quantities.length; ++i) {
            quantities[i] += stock.quantities[i];
        }
    }
    public WarItemStock remove(WarItemStock cost) {

        WarItemStock newStock = new WarItemStock(this);
        for (int i = 0; i < quantities.length; ++i) {
            newStock.quantities[i] -= cost.quantities[i];
        }
        return newStock;
    }

    @Override
    public String toString() {

        StringBuilder formatSB = new StringBuilder(10000);
        for (WarItem warItem : WarItem.values()) {
            formatSB.append(warItem.name()).append("=%3d, ");
        }
        String format = formatSB.toString();

        Object[] quantities = new Object[WarItem.values().length];
        for (WarItem warItem : WarItem.values()) {
            quantities[warItem.ordinal()] = this.quantities[warItem.ordinal()];
        }

        StringBuilder sb = new StringBuilder(10000);

        try (Formatter formatter = new Formatter(sb)) {
            formatter.format(format, quantities);
            return sb.toString();
        }
    }

    public int getQuantity(WarItem warItem) {
        return quantities[warItem.ordinal()];
    }
}
