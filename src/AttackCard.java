import java.util.ArrayList;
import java.util.List;

public enum AttackCard {

    ComicHand(1, 1, 130, new WarItemQuantity(WarItem.Plunger, 1), new WarItemQuantity(WarItem.RubberDuck, 1)), //
    ShrinkRay(3, 3, 300, new WarItemQuantity(WarItem.Pliers, 1), new WarItemQuantity(WarItem.Megaphone, 2)), //
    GiantRockMonster(2, 2, 200, new WarItemQuantity(WarItem.FireHydrant, 1), new WarItemQuantity(WarItem.Binoculars, 1)), //
    NotInKansas(3, 4, 300, new WarItemQuantity(WarItem.Anvil, 1), new WarItemQuantity(WarItem.Propeller, 1)), //
    TentacleVortex(2, 3, 200, new WarItemQuantity(WarItem.Plunger, 1), //
            new WarItemQuantity(WarItem.RubberDuck, 1), new WarItemQuantity(WarItem.Propeller, 2)), //
    FlyingVuRobot(4, 4, 400, new WarItemQuantity(WarItem.Ammo, 2), new WarItemQuantity(WarItem.Gasolline, 2)), //
    DiscoTwister(6, 6, 600, new WarItemQuantity(WarItem.Megaphone, 1), new WarItemQuantity(WarItem.Pliers, 1),
            new WarItemQuantity(WarItem.Propeller, 2)), //
    PlantMonster(4, 4, 400, new WarItemQuantity(WarItem.Plunger, 2), new WarItemQuantity(WarItem.Gasolline, 2),
            new WarItemQuantity(WarItem.RubberBoots, 2)), //
    Fishaster(7, 7, 700, new WarItemQuantity(WarItem.RubberDuck, 1), new WarItemQuantity(WarItem.RubberBoots, 3),
            new WarItemQuantity(WarItem.FireHydrant, 3)), //
    AncientCurse(8, 8, 800, new WarItemQuantity(WarItem.RubberBoots, 1), new WarItemQuantity(WarItem.Megaphone, 2),
            new WarItemQuantity(WarItem.Binoculars, 3)), //
    SixteenTons(6, 6, 660, new WarItemQuantity(WarItem.Pliers, 1), new WarItemQuantity(WarItem.FireHydrant, 2),
            new WarItemQuantity(WarItem.Anvil, 4)), //
    Spiders(9, 9, 900, new WarItemQuantity(WarItem.Gasolline, 1), new WarItemQuantity(WarItem.Binoculars, 2), new WarItemQuantity(WarItem.Ammo, 5));

    public final int damage;
    public final int energy;
    public final int points;
    public final WarItemStock warItemQuantities = new WarItemStock();

    private AttackCard(int damage, int energy, int points, WarItemQuantity... warItemQuantities) {
        this.damage = damage;
        this.energy = energy;
        this.points = points;
        for (WarItemQuantity warItemQuantity : warItemQuantities) {
            this.warItemQuantities.set(warItemQuantity.warItem, warItemQuantity.quantity);
        }
    }

    public static AttackCard[] filterEnergyEfficient(AttackCard[] cards) {

        List<AttackCard> result = new ArrayList<>();
        for (AttackCard card : cards) {
            if (card.damage >= card.energy) {
                result.add(card);
            }
        }
        return result.toArray(new AttackCard[0]);
    }
}
