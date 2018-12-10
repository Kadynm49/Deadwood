/**
 * Created by marsha38 on 10/30/18.
 */
import javax.swing.*;
import java.lang.Math;

public class Role extends BasicShape{
    private String line;
    private int rank;
    private boolean isExtra;
    private Player player;
    private int chips; //the practice chips

    public Role(String name, int rank, boolean isExtra) {
        this.name = name;
        this.rank = rank;
        this.isExtra = isExtra;
        this.chips = 0;
    }

    //ONLY returns true if acting ends the scene
    public boolean act(){
        int roll;
        int budget;

        roll = (int) (Math.floor((Math.random() * 6)+1));
        budget = this.player.getFilmLocation().getScene().getBudget();

        if((roll + this.chips) < budget) { //fails to act
            if (this.isExtra) {
                JOptionPane.showMessageDialog(null, "You rolled a " + roll + ". You failed to act." +
                                                "\nYou are on a supporting role, so you receive one dollar!");
                this.player.addDollars(1);
            } else {
                JOptionPane.showMessageDialog(null, "You rolled a " + roll + ". You failed to act." +
                                                "\nYou are on a star role, so you receive nothing.");
            }
            return false;
        } else {                                                //succeeds to act
            this.player.getFilmLocation().decrementShots();
                if (this.isExtra) {
                    JOptionPane.showMessageDialog(null, "You rolled a " + roll + ". You succeeded to Act." +
                                                    "\nYou are on a supporting role, so you receive one dollar and one credit!");
                    this.player.addCredits(1);
                    this.player.addDollars(1);
                } else {
                    JOptionPane.showMessageDialog(null, "You rolled a " + roll + ". You succeeded to Act." +
                                                    "\nYou are on a main role, so you receive 2 credits!");
                    this.player.addCredits(2);
                }
            if (this.player.getFilmLocation().getShots() == 0) { //success ends the scene
                GameManager.bonusPayout(this.player.getFilmLocation());
                return true;
            }
                return false;
        }
    }

    //returns true if the player on the role was able to rehearse
    public boolean rehearse() {
        if(this.chips == (this.player.getFilmLocation().getScene().getBudget() - 1)) {
            JOptionPane.showMessageDialog(null, "You are guaranteed success in acting, so you cannot rehearse any further.");
            return false;
        } else {
            JOptionPane.showMessageDialog(null, "You have received one practice chip.  Your turn has now ended.");
            this.chips++;
            return true;
        }
    }

    //adds player to the role
    public void addPlayer(Player p) {
        this.player = p;
    }

    //removes player and reinitializes practice chips
    public void removePlayer() {
        this.chips = 0;
        this.player = null;
    }


    public void addLine(String line) {
        this.line = line;
    }

    public String toString() {
        String roleType;
        if(this.isExtra) {
            roleType = "Extra";
        }
        else{
            roleType = "Star";
        }
        String whole = "Role: " + name + "\nline: " + line + "\nrank: " + rank + "\n" + roleType +
                "\nplayer on role: " + player + "\nRehearsal chips: " + chips + "\n";
        return whole;
    }

    public boolean isExtra() {
        return this.isExtra;
    }

    public Player getPlayer() {
        return player;
    }

    public int getRank() {
        return rank;
    }

    public int getChips() {
        return this.chips;
    }

    public String getName() {
        return this.name;
    }
}
