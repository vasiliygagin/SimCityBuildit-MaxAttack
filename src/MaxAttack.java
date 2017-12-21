import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("boxing")
public class MaxAttack {

    public static void main(String[] args) throws Exception {

        WarItemStock stock = new WarItemStock();
        AttackCard[] availableAttacks;
        boolean useOnlyEnergyEfficientCards;
        int usedEnergy;

        Date warStart;
        boolean vasiliy = true;
        if (vasiliy) {
            availableAttacks = new AttackCard[] { AttackCard.ComicHand, AttackCard.ShrinkRay, AttackCard.GiantRockMonster, AttackCard.TentacleVortex,
                    AttackCard.FlyingVuRobot, AttackCard.DiscoTwister, AttackCard.PlantMonster, AttackCard.SixteenTons, AttackCard.BuildingPortal };
            useOnlyEnergyEfficientCards = true;

            warStart = parse("12/19/2017 10:54");
            usedEnergy = 134;

            stock.set(WarItem.Binoculars, 12);
            stock.set(WarItem.RubberBoots, 12);

            stock.set(WarItem.Anvil, 28);
            stock.set(WarItem.Megaphone, 22);
            stock.set(WarItem.Pliers, 17);
            stock.set(WarItem.RubberDuck, 12);
            stock.set(WarItem.Propeller, 7);
            stock.set(WarItem.Ammo, 6);
            stock.set(WarItem.Gasolline, 5);
            stock.set(WarItem.Plunger, 2);
            stock.set(WarItem.FireHydrant, 4);

        } else {
            availableAttacks = new AttackCard[] { AttackCard.ComicHand, AttackCard.ShrinkRay, AttackCard.TentacleVortex, AttackCard.PlantMonster,
                    AttackCard.SixteenTons };
            useOnlyEnergyEfficientCards = false;

            warStart = parse("12/02/2017 05:37");
            usedEnergy = 42;

            stock.set(WarItem.FireHydrant, 6);
            stock.set(WarItem.Binoculars, 5);
            stock.set(WarItem.Anvil, 1);
            stock.set(WarItem.Pliers, 19);
            stock.set(WarItem.Plunger, 4);
            stock.set(WarItem.Megaphone, 1);
            stock.set(WarItem.RubberDuck, 3);
            stock.set(WarItem.RubberBoots, 1);
            stock.set(WarItem.Ammo, 0);
            stock.set(WarItem.Gasolline, 0);
            stock.set(WarItem.Propeller, 0);

        }

        Energy energy = new Energy(warStart);
        int currentEnergy = energy.getGeneratedEnergy() - usedEnergy;
        int maxEnergy = energy.getFutureEnergy() + currentEnergy;
        System.out.println("Current energy: " + currentEnergy);
        System.out.println("    Max energy: " + maxEnergy);

        AttackCard[] energyEfficientCards = AttackCard.filterEnergyEfficient(availableAttacks);
        AttackCard[] cardsToUse = useOnlyEnergyEfficientCards ? energyEfficientCards : availableAttacks;

        System.out.println("                  " + consiseDamageHeader(availableAttacks));
        System.out.println("Initial war items:                                           " + consiseWarItemStock(stock));
        Damage maxDamage = calcMaxDamage(availableAttacks, stock, 0, Integer.MAX_VALUE);
        System.out.println("Unlimited energy: " + consiseDamageReport(maxDamage, availableAttacks));
        Damage maxEfficientDamage = calcMaxDamage(energyEfficientCards, stock, 0, Integer.MAX_VALUE);
        System.out.println("Unlimited Effici: " + consiseDamageReport(maxEfficientDamage, availableAttacks));

        // Damage efficientDamage = maxDamage;
        // while (efficientDamage.energy > efficientDamage.damage) {
        // efficientDamage = calcMaxDamage(cardsToUse, stock, 0, efficientDamage.energy
        // - 1);
        // }
        Damage efficientDamage = calcMaxDamage(cardsToUse, stock, 0, maxEnergy);

        WarItemStock leftoverItems = stock.remove(efficientDamage.cost);
        Damage leftoverDamage = calcMaxDamage(availableAttacks, leftoverItems, 0, maxEnergy - efficientDamage.energy);

        Damage totalDamage = new Damage(efficientDamage, leftoverDamage);
        System.out.println("Available energy: " + consiseDamageReport(efficientDamage, availableAttacks));
        System.out.println("Leftover items:   " + consiseDamageReport(leftoverDamage, availableAttacks));
        System.out.println("Total damage:     " + consiseDamageReport(totalDamage, availableAttacks));

        WarItemStock leftoverStock = stock.remove(totalDamage.cost);
        System.out.println("Leftover war items:                                          " + consiseWarItemStock(leftoverStock));
        printNeededItems(leftoverStock, cardsToUse);

        int maxUsableEnergy = analyseLimitedEnerg(stock, cardsToUse, maxDamage);

        evaluateLimitedEnergyScenario(stock, cardsToUse, maxUsableEnergy);
    }

    private static Date parse(String source) throws ParseException {
        return new SimpleDateFormat("MM/dd/yyyy hh:mm").parse(source);
    }

    private static int analyseLimitedEnerg(WarItemStock stock, AttackCard[] availableAttacks, Damage maxDamage) {
        int maxUsableEnergy = maxDamage.energy;
        int targetEnergy = maxUsableEnergy - Integer.min(8, (int) (maxUsableEnergy * 0.15));

        Damage limitedDamage = calcMaxDamage(availableAttacks, stock, 0, targetEnergy);
        WarItemStock leftoverStock = stock.remove(limitedDamage.cost);
        Damage leftoverDamage = calcMaxDamage(availableAttacks, leftoverStock, 0, Integer.MAX_VALUE);
        Damage totalDamage = new Damage(limitedDamage, leftoverDamage);
        System.out.println("                  " + consiseDamageHeader(availableAttacks));
        System.out.println("Limited energy:   " + consiseDamageReport(limitedDamage, availableAttacks));
        System.out.println("Leftover items:   " + consiseDamageReport(leftoverDamage, availableAttacks));
        System.out.println("Total damage:     " + consiseDamageReport(totalDamage, availableAttacks));
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
                if (cardItemQuantity > 0 && (itemQuantity < cardItemQuantity * 3)) {
                    neededItems.add(warItem);
                    break;
                }
            }
        }
        System.out.println("Needed Items: " + neededItems);
    }

    private static void evaluateLimitedEnergyScenario(WarItemStock stock, AttackCard[] availableAttacks, int maxEnergy) {
        try (Formatter formatter = new Formatter(System.out)) {

            System.out.println("|              Limited Energy Damage              -                 Leftover Damage                 |");
            String damageHeader = consiseDamageHeader(availableAttacks);
            System.out.print(damageHeader);
            System.out.print(" - ");
            System.out.print(damageHeader);
            System.out.println();
            for (int i = maxEnergy; i > 0; --i) {
                int limitedEnergy = i;
                Damage limitedDamage = calcMaxDamage(availableAttacks, stock, 0, limitedEnergy);
                WarItemStock leftoverStock = stock.remove(limitedDamage.cost);
                Damage leftoverDamage = calcMaxDamage(availableAttacks, leftoverStock, 0, Integer.MAX_VALUE);

                System.out.println(
                        consiseDamageReport(limitedDamage, availableAttacks) + " - " + consiseDamageReport(leftoverDamage, availableAttacks));
                System.out.println();
            }
        }
    }

    private static String consiseDamageHeader(AttackCard[] availableAttacks) {
        return "Dmg Point Eng " + formatConsiseAttackCardHeader(availableAttacks) + " " + " Pg RD Pl Mg FH Bi An Pr Am Ga RB";
    }

    private static CharSequence formatConsiseAttackCardHeader(AttackCard[] attackCards) {

        StringBuilder sb = new StringBuilder(100);
        for (AttackCard attackCard : attackCards) {
            sb.append(' ').append(consiseCardName(attackCard));
        }
        return sb;
    }

    private static String consiseCardName(AttackCard attackCard) {
        switch (attackCard) {
            case AncientCurse:
                return "AC";
            case Blizzaster:
                return "Bl";
            case BuildingPortal:
                return "BP";
            case ComicHand:
                return "CH";
            case DanceShoes:
                return "DS";
            case DiscoTwister:
                return "DT";
            case Fishaster:
                return "Fi";
            case FlyingVuRobot:
                return "FR";
            case GiantRockMonster:
                return "GR";
            case HandsOfDoom:
                return "HD";
            case Magnetism:
                return "Ma";
            case MovieMonster:
                return "MM";
            case NotInKansas:
                return "NK";
            case PlantMonster:
                return "PM";
            case ShrinkRay:
                return "SR";
            case SixteenTons:
                return "16";
            case Spiders:
                return "Sp";
            case TentacleVortex:
                return "TV";
            default:
                throw new UnsupportedOperationException();
        }
    }

    private static CharSequence consiseDamageReport(Damage damage, AttackCard[] availableAttacks) {

        StringBuilder sb = new StringBuilder(1000);
        try (Formatter f = new Formatter(sb);) {

            f.format("%3d %5d %3d " + consiseAttacksFormat(availableAttacks) + "  ", //
                    merge(new Object[] { damage.damage, damage.points, damage.energy }, //
                            asArray(damage, availableAttacks)));
        }
        sb.append(consiseWarItemStock(damage.cost));
        return sb;
    }

    private static Object[] asArray(Damage damage, AttackCard[] availableAttacks) {

        List<Integer> quantities = new ArrayList<>();
        for (AttackCard attackCard : availableAttacks) {
            int numberOfCards = damage.cardQuantities[attackCard.ordinal()];
            quantities.add(numberOfCards);
        }
        return quantities.toArray();
    }

    private static Object[] merge(Object[] array1, Object[] array2) {

        Object[] result = new Object[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    private static CharSequence consiseAttacksFormat(AttackCard[] attackCards) {

        StringBuilder sb = new StringBuilder(100);
        for (int i = 0; i < attackCards.length; ++i) {
            sb.append(" %2d");
        }
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
