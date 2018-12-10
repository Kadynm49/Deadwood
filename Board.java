import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Collections;
import java.util.Scanner;

/**
 * Created by marsha38 on 10/30/18.
 */

public class Board {
    private Player[] players = new Player[2];
    private int day;
    private ArrayList<BoardSquare> allSquares = new ArrayList<>();
    private ArrayList<Scene> unusedScenes = new ArrayList<>();
    //scenes left: the number of scenes left on the board
    private int scenesLeft;
    private boolean playerMoved = false;
    private boolean actionsComplete = false;


    public Board(int playerNum) throws ParserConfigurationException {
        Parser.parseXML(allSquares, unusedScenes);
        convertAllAdj();
        Collections.shuffle(unusedScenes);
        initScenes();
        this.day = 1; //instantly updated to 1 when newDay is called
        createPlayers(playerNum);
    }

    public void playGame() {
        while(this.day < 4) {
            newDay();
            playDay();
            this.day++;
        }
        presentWinners();
    }



    private void presentWinners() {
        ArrayList<Player> winners = GameManager.calcWinner(this.players);
        if(winners.size() == 1) {
            System.out.println("The winner is: " + winners.get(0) + " with score " + winners.get(0).getFinalScore());
        }
        else {
            System.out.println("It's a tie! The winners are: ");
            for(int i = 0; i < winners.size(); i++) {
                Player winner = winners.get(i);
                System.out.println(winner + " with score " + winner.getFinalScore());
            }
        }
    }


    private void playDay() {
        while(this.scenesLeft > 1){
            for(int i = 0; i < this.players.length; i++) {
                Player player = this.players[i];
                playerTurn(player);
                if(this.scenesLeft <= 1) {
                    System.out.println("There is only 1 scene left.  The game day is over");
                    break;
                }
            }
        }
    }


    private void playerTurn(Player player) {
        this.playerMoved = false;
        this.actionsComplete = false;
        boolean turnOver = false;
        System.out.println("\nThe current player is " + player);
        System.out.println("Possible commands: 'location', 'move newLocation', 'act', 'rehearse', 'take role newRole',\n"
                + "'end turn', 'neighbors', 'current role', 'available roles', 'scene', 'player info',\n"
                + "'practice chips', 'shots', 'upgrade currencyType newLevel', 'all player locations'");
        Scanner command = new Scanner(System.in);
        while(!turnOver) {
            turnOver = performCommand(command.nextLine(), player);
        }

    }

    //returns whether or not the turn is over
    private boolean performCommand(String command, Player player) {
        boolean endturn = false;
        boolean validCommand = false;
        if(command.equals("location")) {
            printLocation(player);
            validCommand = true;
        }
        else if(command.split(" ")[0].equals("move")) {
            if (!playerMoved) {
                playerMoved = moveTry(command, player);
            } else {
                System.out.println("You can't move twice!");
            }
            validCommand = true;
        }
        else if(command.equals("end turn")) {
            System.out.println("Turn ended");
            endturn = true;
            validCommand = true;
        }
        else if(command.equals("neighbors")) {
            player.getLocation().printNeighbors();
            validCommand = true;
        }
        else if(command.equals("available roles")) {
            if(player.isOnFilm()) {
                if(player.getFilmLocation().isActive()) {
                    player.getFilmLocation().availableRoles();
                }
                else {
                    System.out.println("This Film Room has been wrapped up!");
                }
            }
            else {
                System.out.println("You aren't on a Film Room.");
            }
            validCommand = true;
        }
        else if(command.equals("current role")) {
            if(player.isOnRole()) {
                System.out.println(player.getRole());
            }
            else {
                System.out.println("You are not on a Role.");
            }
            validCommand = true;
        }
        else if(command.equals("practice chips")) {
            if(player.isOnRole()) {
                System.out.println("You have " + player.getRole().getChips() + " practice chips");
            } else {
                System.out.println("You are not on a role.");
            }
            validCommand = true;
        }
        else if(command.equals("scene")) {
            if(player.isOnFilm()) {
                if(player.getFilmLocation().isActive()) {
                    System.out.println(player.getFilmLocation().getScene());
                }
                else {
                    System.out.println("This Film Room has been wrapped up!");
                }
            }
            else {
                System.out.println("You aren't on a Film Room.");
            }
            validCommand = true;
        }
        else if(command.equals("player info")) {
            player.printPlayerInfo();
            validCommand = true;
        }
        else if(command.equals("shots")) {
            if (player.isOnFilm()) {
                System.out.println("shots left in room: " + player.getFilmLocation().getShots());
            }
            else {
                System.out.println("You are not in a film room.");
            }
            validCommand = true;
        }
        else if(command.equals("cheat add dollars")) {
            player.addDollars(100);
            validCommand = true;
        }
        else if(command.equals("cheat add credits")) {
            player.addCredits(100);
            validCommand = true;
        }
        else if(command.length() > 15  &&  command.substring(0,15).equals("cheat teleport ")) {
            playerMoved = cheatMoveTry(command, player);
            validCommand = true;
        }
        else if(command.equals("cheat end day")) {
            this.scenesLeft = 1;
            endturn = true;
            validCommand = true;
        }
        else if(command.equals("cheat end scene")) {
            if(player.isOnRole()) {
                player.getFilmLocation().cheatEndScene();
                GameManager.bonusPayout(player.getFilmLocation());
                player.getFilmLocation().endRoom();
                validCommand = true;
                this.scenesLeft--;
            }
        }
        else if(command.length() > 19 && command.substring(0, 19).equals("cheat upgrade rank ")) {
            player.cheatSetRank(Integer.parseInt(command.substring(19)));
            validCommand = true;
        }
        else if(command.equals("all player locations")) {
            printAllLocations(player);
            validCommand = true;
        }
        else if(actionsComplete != true) {

            if(command.equals("act")) {
                actionsComplete = actTry(player);
                validCommand = true;
            }
            else if(command.equals("rehearse")) {
                actionsComplete = rehearseTry(player);
                validCommand = true;
            }
            else if(command.split(" ").length > 2 &&  command.split(" ")[0].equals("take")  &&  command.split(" ")[1].equals("role")) {
                actionsComplete = takeRoleTry(command, player);
                validCommand = true;
            }
            else if(command.length() > 8 && command.substring(0, 8).equals("upgrade ")) {
                actionsComplete = upgradeTry(player, command.substring(8));
                validCommand = true;
            }
        }
        if(validCommand == false) {
            System.out.println("You entered an invalid command!");
        }
        return endturn;
    }

    public boolean upgradeTry(Player player, String info) {
        String[] arrInfo = info.split(" ");
        if(arrInfo.length < 2) {
            System.out.println("Invalid input");
            return false;
        }
        String currency = arrInfo[0];
        int level;
        try{
            level = Integer.parseInt(arrInfo[1]);
        }
        catch(NumberFormatException e) {
            System.out.println("Invalid input");
            return false;
        }
        if(level > 6  ||  level < 2) {
            System.out.println("That's not a valid upgrade level");
            return false;
        }
        if(player.isOnCasting()) {
            if(currency.toLowerCase().equals("dollars")  ||  currency.toLowerCase().equals("credits")) {
                if (CastingOffice.canUpgrade(player, currency, level)) {
                    player.upgrade(currency, CastingOffice.getUpgradeAmount(currency, level), level);
                    System.out.println("New level: " + player.getRank());
                    return true;
                }
                else {
                    System.out.println("You cannot afford this upgrade");
                }
            }
            else{
                System.out.println("You must use dollars or credits for your currency");
            }
        }
        else {
            System.out.println("You're not on the casting office");
        }
        return false;
    }


    //prints the location of the current player -- notifying user who the current player is, and all other players
    private void printAllLocations(Player player) {
        for(int i = 0; i < this.players.length; i++) {
            if(this.players[i] == player) {
                System.out.print("Current Player: ");
            }
            System.out.print(this.players[i] + " is located at " + this.players[i].getLocation().getName());
            if(this.players[i].getRole() != null) {
                System.out.print(" and is acting in " + this.players[i].getRole().getName());
            }

            System.out.println();
        }
    }

    //prints only location of current player
    private void printLocation(Player player) {
        System.out.println(player.getLocation());
    }



    /*returns true if the player was able to take the role, false otherwise
    * player must be on an active film to take a role
     */
    private boolean takeRoleTry(String command, Player player) {
        boolean roleTaken = false;
        String[] wholeLine = command.split(" ");
        String restCommand = "";
        for(int i = 2; i < wholeLine.length; i++) {
            restCommand = restCommand + wholeLine[i];
            if (i != wholeLine.length - 1) {
                restCommand = restCommand + " ";
            }
        }
        if(player.isOnFilm()) {
            if(player.getFilmLocation().isActive()) {
                FilmRoom film = player.getFilmLocation();
                Role newRole = getRoleFromStr(restCommand, film);
                roleTaken = player.takeRole(newRole);
            }
            else {
                System.out.println("Scene has already been wrapped up!");
            }
        }
        else{
            System.out.println("You can't take a role when you're not on a film square");
        }
        return roleTaken;
    }


    /*roleName -- name of desired role
    *film -- the room that the player is currently in
    *checks for all roles in the film room to see if they have the requested name
    *returns the requested role if film contains requested role, null otherwise
     */
    private Role getRoleFromStr(String roleName, FilmRoom film) {
        ArrayList<Role> extras = film.getExtras();
        ArrayList<Role> stars = film.getScene().getStars();
        for(int i = 0; i < extras.size(); i++) {
            Role role = extras.get(i);
            if(role.getName().equals(roleName)) {
                return role;
            }
        }
        for(int i = 0; i < stars.size(); i++) {
            Role role = stars.get(i);
            if(role.getName().equals(roleName)) {
                return role;
            }
        }
        return null;
    }



    //player attempts to rehearse
    private boolean rehearseTry(Player player) {
        boolean turnOver = player.rehearse();
        return turnOver;
    }


    /*player attempts to act, decrements scene if they finished the scene
    * if the player is on a role they can act and their turn always ends
     */
    private boolean actTry(Player player) {
        boolean turnOver = false;
        if(player.isOnRole()) {
            boolean sceneFinished = player.act();
            if(sceneFinished) {
                this.scenesLeft --;
            }
            turnOver = true;
        }
        else{
            System.out.println("You must be on a role to act");
        }
        return turnOver;
    }


     private boolean cheatMoveTry (String command, Player player) {
        String restCommand = command.substring(15);
        BoardSquare newRoom = null;
        for(int i = 0; i < this.allSquares.size(); i++) {
            BoardSquare room = this.allSquares.get(i);
            if(room.getName().equals(restCommand)) {
                newRoom = room;
                break;
            }
        }
        if(newRoom == null) {
            System.out.println("Invalid room");
            return false;
        }
        player.moveTo(newRoom);
        System.out.println("Moved");
        return true;
    }

    /*
    *checks if the player can move to their specified location (5 is the index where their location should start)
    *if the player can move to that location then they are moved to the location
     */
    private boolean moveTry (String command, Player player) {
        String restCommand = command.substring(5);
        if(player.move(restCommand)) {
            System.out.println("Moved to: " + restCommand);
            System.out.println("What do you want to do now?");
            return true;
        }
            return false;
    }


    //creates playerNum amount of new players with names "player1", "player2", etc
    private void createPlayers(int playerNum) {
        this.players = new Player[playerNum];
        for(int i = 0; i < playerNum; i++) {
            Player p = new Player("player" + (i + 1));
            players[i] = p;
        }
    }


    //initializes the adjacent squares for every square
    private void convertAllAdj() {
        for(int i = 0; i < allSquares.size(); i++) {
            BoardSquare square = allSquares.get(i);
            square.convertAdjacent(allSquares);
        }
    }


    //puts scenes in films -- scenes have already been shuffled
    private void initScenes() {
        this.scenesLeft = 0;
        for(int i = 0; i < allSquares.size(); i++) {
            BoardSquare square = allSquares.get(i);
            if (square instanceof FilmRoom) {
                FilmRoom room = (FilmRoom) square;
                Scene scene = unusedScenes.remove(0);
                room.newScene(scene);
                this.scenesLeft++;
            }
        }
    }



    /*new day -- ensures that no scenes have players on them anymore
    * moves all players to the trailer
     */
    private void newDay() {
        BoardSquare trailer = null;
        for(int i = 0; i < allSquares.size(); i++) {
            BoardSquare square = allSquares.get(i);
            if (square.getName().equals("trailer")) {
                trailer = square;
                break;
            } else if (square instanceof FilmRoom) {
                FilmRoom room = (FilmRoom) square;
                if (room.getScene() != null) {
                    room.endRoom();
                }
            }
        }
        initScenes();
        for(int i = 0; i < players.length; i++) {
            Player player = players[i];
            player.moveTo(trailer);
        }
        System.out.printf("The day is %d.  All players are now in the trailer", this.day);
    }



    public static void main(String[] args) throws ParserConfigurationException {
        Board Deadwood = new Board(2);
        Deadwood.playGame();
    }
}
