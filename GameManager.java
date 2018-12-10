/**
 * Created by marsha38 on 10/30/18.
 */
import javax.swing.*;
import java.util.*;

public class GameManager {


    /*returns an arrayList of the winners --
    * it's an arrayList because there could be a tie between any number of players
    * every time a new high score is found the arrayList resets to only include the owner of the new high score
     */
    public static ArrayList<Player> calcWinner(Player[] players) {
        ArrayList<Player> winners = new ArrayList<>();
        Player topPlayer = players[0];
        winners.add(topPlayer);
        for(int i = 1; i < players.length; i++) {
            Player player = players[i];
            if(player.getFinalScore() > topPlayer.getFinalScore()) {
                topPlayer = player;
                winners = new ArrayList<>();
                winners.add(topPlayer);
            }
            else if(player.getFinalScore() == topPlayer.getFinalScore()) {
                winners.add(player);
            }
        }
        return winners;
    }



    // after scene completed provides the bonus payout to both players on and off the scene in the film room
    public static void bonusPayout(FilmRoom room) {
        String msg = "The scene has wrapped.\n";
        if(room.getScene().hasPlayers()) {
            int budget = room.getScene().getBudget();
            int[] diceRolls = new int[budget];
            for(int i = 0; i<budget; i++) {
                diceRolls[i] = (int)(Math.floor((Math.random() * 6)+1));
            }
            Arrays.sort(diceRolls);
            msg = msg + "The dice rolls are: " + Arrays.toString(diceRolls) + "\nStar payout: ";
            msg = msg + payPlayersOnScene(diceRolls, room.getScene().getStars());
        }
        if(room.hasExtraPlayers()) {
            msg = msg + "\nExtra payout: " + payPlayersOffScene(room.getExtras());
        }
        JOptionPane.showMessageDialog(null, msg);
    }

    //pays the stars on the film
    private static String payPlayersOnScene(int[] diceRolls, ArrayList<Role> stars) {
        int role = (stars.size()-1);
        String msg = "";
        for(int i = (diceRolls.length-1); i>=0 ; i--) {
            if(stars.get(role).getPlayer() != null) {
                Player player = stars.get(role).getPlayer();
                player.addDollars(diceRolls[i]);
                msg = msg + "\n" + player + " has received " + diceRolls[i] + " Dollar(s).";
            }
            if (role == 0) {
                role = (stars.size() - 1);
            } else {
                role--;
            }
        }
        return msg;
    }

    //pays the extras on the film
    private static String payPlayersOffScene(ArrayList<Role> extras) {
        String msg = "";
        for(int i = 0; i < extras.size(); i++) {
            if(extras.get(i).getPlayer() != null) {
                extras.get(i).getPlayer().addDollars(extras.get(i).getRank());
                msg = msg + "\n" + extras.get(i).getPlayer() + " has received " + extras.get(i).getRank() + " Dollar(s).";
            }
        }
        return msg;
    }
}
