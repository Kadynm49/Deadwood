import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by marsha38 on 10/30/18.
 */
public class CastingOffice extends BoardSquare{
    //ist index represents rank, 2nd represents either dollars or credits
    //NOTE: simple upgrades makes a lot of sense for our text-based version
    //but I don't know if it will translate well to GUI
    private static int [] [] simpleUpgrades = initUpgrades();
    private static final int DOLLARS = 0;
    private static final int CREDITS = 1;


    //not yet in use -- will probably be used in GUI -- contains information, including size, taken from the XML
    private ArrayList<Upgrade> upgrades = new ArrayList<>();

    public CastingOffice() {

    }


    //returns true if the player can upgrade, false otherwise
    public static boolean canUpgrade(Player player, String paymentType, int requestedRank) {
        if(player.getRank() < requestedRank) {

            if(paymentType.equals("credits")) {

                if(player.getCredits() >= simpleUpgrades[requestedRank][CREDITS]) {
                    return true;
                }
                else{
                    JOptionPane.showMessageDialog(null, "You do not have enough credits to afford this rank");
                    return false;
                }
            }

            else{

                if(player.getDollars() >= simpleUpgrades[requestedRank][DOLLARS]) {
                    return true;
                }
                else{
                    JOptionPane.showMessageDialog(null, "You do not have enough dollars to afford this rank");
                    return false;
                }
            }

        }
        else{
            JOptionPane.showMessageDialog(null, "Your rank must be higher than your current rank.");
            return false;
        }
    }


    //for XML Parsing, only to be used in GUI
    public void addUpgrade(Upgrade upgrade) {
        upgrades.add(upgrade);
    }


    //returns the price of an upgrade
    public static int getUpgradeAmount(String currency, int newLevel) {
        if(currency.equals("dollars")) {
            return simpleUpgrades[newLevel][DOLLARS];
        }
        return simpleUpgrades[newLevel][CREDITS];
    }



    //prints all upgrades in a reader friendly form for the player
    private String stringUpgrades() {
        String all = "";
        for(int i = 2; i < this.simpleUpgrades.length; i++) {
            all = all + "rank: " + i + ", Dollars: " + simpleUpgrades[i][DOLLARS] + ", Credits: "
                    + simpleUpgrades[i][CREDITS]
            + "\n";
        }
        return all;
    }

    //setes the upgrade prices
    private static int [] [] initUpgrades() {
        int [] [] simpleUpgrades = new int [7][2];
        simpleUpgrades[2][DOLLARS] = 4;
        simpleUpgrades[2][CREDITS] = 5;
        simpleUpgrades[3][DOLLARS] = 10;
        simpleUpgrades[3][CREDITS] = 10;
        simpleUpgrades[4][DOLLARS] = 18;
        simpleUpgrades[4][CREDITS] = 15;
        simpleUpgrades[5][DOLLARS] = 28;
        simpleUpgrades[5][CREDITS] = 20;
        simpleUpgrades[6][DOLLARS] = 40;
        simpleUpgrades[6][CREDITS] = 25;
        return simpleUpgrades;
    }

    public String toString() {
        String office = "Name: " + this.name + "\nUpgrades:\n" + stringUpgrades();
        return office;
    }


}
