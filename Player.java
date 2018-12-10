import javax.swing.*;
import java.util.Observable;

/**
 * Created by marsha38 on 10/30/18.
 */
public class Player {

    private int rank = 1;
    private int dollars = 0;
    private int credits = 0;
    private BoardSquare location;
    private Role role;
    private String name;
    String img;

    public Player(String name) {
        this.name = name;
    }

    /*returns whether or not player has taken newRole
    * it is already verified beforehand that newRole is within the player's film location
    * prints a variety of error messages if player can't take the role
     */
    public boolean takeRole(Role newRole) {
        FilmRoom film = (FilmRoom) location;
        if(!film.isActive()) {
            return false;
        }
        boolean roleTaken = false;
        if(film.getExtras().contains(newRole)  ||  film.getScene().getStars().contains(newRole)) {
            if (newRole != null) {
                if (!this.isOnRole()) {
                    if (this.rank >= newRole.getRank()) {
                        if (newRole.getPlayer() == null) {
                            this.role = newRole;
                            this.role.addPlayer(this);
                            roleTaken = true;
                        }
                    }
                }
            }
        }
        return roleTaken;
    }


    /*the validity of the upgrade has already been checked
    * the player is upgrade to the new rank level, and loses amount money from the currency they're using
     */
    public void upgrade(String currency, int amount, int level) {
        if(currency.toLowerCase().equals("dollars")) {
            this.dollars = this.dollars - amount;
        }
        else if(currency.toLowerCase().equals("credits")) {
            this.credits = this.credits - amount;
        }
        this.rank = level;
    }

    public void loseRole() {
        role = null;
    }

    /* move
     * checks to see if the string matches the name
     * of any rooms adjacent to the players current
     * location, then moves them there with moveTo.
     */
    public boolean move(String newSquare) {
        if(this.location.isAdjacent(newSquare)){
            if (this.role == null) {
                moveTo(location.getSingleAdj(newSquare));
                return true;
            }
        }
        return false;
    }


    /* moveTo
     * Moves the player directly to newSquare
     * without checking to see if the room is adjacent (assumes this has already been checked)
     */
    public void moveTo(BoardSquare newSquare) {
        if(this.location != null) {
            this.location.removePlayer(this); //remove from old location
        }
        this.role = null;
        this.location = newSquare;        //set players new location
        this.location.addPlayer(this);    //add to new location
    }

    public BoardSquare getLocation() {
        return this.location;
    }

    public FilmRoom getFilmLocation() {
        return (FilmRoom)this.location;
    }

    public CastingOffice getCastingLocation() {
        return (CastingOffice)this.location;
    }

    public int getRank() {
        return this.rank;
    }

    public Role getRole() { return role; }

    public int getDollars() {
        return this.dollars;
    }

    public int getCredits() {
        return this.credits;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImgString() {
        return this.img;
    }

    public void addDollars(int add) {
        this.dollars += add;
    }

    public void addCredits(int add) {
        this.credits += add;
    }

    public void subtractDollars(int s) {
        this.dollars -= s;
    }

    public void subtractCredits(int s) {
        this.credits -= s;
    }

    public boolean isOnFilm() {
        if (this.location instanceof FilmRoom) {
            return true;
        }
        return false;
    }

    public void cheatSetRank(int level) {
        if(level < 7) {
            this.rank = level;
        }
    }

    public boolean isOnRole() {
        if(this.role != null) {
            return true;
        }
        return false;
    }

    public boolean isOnCasting() {
        if(this.location instanceof CastingOffice) {
            return true;
        }
        return false;
    }

    //returns true if acting ends scene
    public boolean act() {
        return this.role.act();
    }

    public boolean rehearse() {
        if(this.role != null) {
            return role.rehearse();
        }
        JOptionPane.showMessageDialog(null, "You can't rehearse if you're not on a role");
        return false;
    }

    //calculates player's final score
    public int getFinalScore() {
        return this.dollars + this.credits + (5*this.rank);
    }

    public String toString() {
        return this.name;
    }

    public void printPlayerInfo() {
         System.out.println("Player: " + this.name + "\nRank: " + this.rank + "\nLocation: " + this.location.getName() +
                "\nDollars: " + this.dollars + "\nCredits: " + this.credits);
    }
}
