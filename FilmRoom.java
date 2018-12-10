import java.util.ArrayList;

/**
 * Created by marsha38 on 10/30/18.
 */
public class FilmRoom extends BoardSquare {
    private boolean isVisited = false;
    private Scene scene;
    private ArrayList<Role> extras = new ArrayList<>();
    private ArrayList<Take> takes = new ArrayList<>();
    int lowestTake;
    private int currentShots;
    private int totalShots;

    public FilmRoom() {
        this.totalShots = 0;
        this.currentShots = 0;
        this.lowestTake = 1;
    }



    //remove scene card and all players from film
    public void endRoom() {
        scene = null;
        for(int i = 0; i < extras.size(); i++) {
            extras.get(i).removePlayer();
        }
        for(int i = 0; i < playersInRoom.size(); i++) {
            playersInRoom.get(i).loseRole();
        }
    }

    public void newScene(Scene scene) {
        this.scene = scene;
    }

    //decrements shot counters by 1 -- the stuff with the takes is for the GUI and has no impact yet
    public void decrementShots() {
        this.currentShots--;
        for(int i = 0; i < takes.size(); i++) {
            Take take = takes.get(i);
            if(take.getNum() == lowestTake) {
                take.makeInactive();
                lowestTake ++;
                break;
            }
        }

    }

    public Scene getScene() {
        return this.scene;
    }


    public void addTakes(Take newTake) {
        this.totalShots++;
        takes.add(newTake);
    }

    public ArrayList<Take> getTakes() {
        return this.takes;
    }


    public void addExtras(Role newPart) {
        extras.add(newPart);
    }

    public boolean getIsVisited() {
        return isVisited;
    }

    public void setIsVisited() {
        this.isVisited = true;
    }

    public void setNotVisited() {this.isVisited = false;}

    public ArrayList<Role> getExtras() {
        return extras;
    }

    public int getShots() {
        return this.currentShots;
    }

    public String toString() {
        String s;
        if(scene == null) {
            s = "The scene in this room has been wrapped up";
        } else {
            s = scene.toString();
        }
        String filmRoom = "Room Name: " + this.name + "\nShots:" + currentShots + "\n" + s + "\nExtra Roles: " + stringRoles();
        return filmRoom;
    }

    private String stringRoles() {
        String all = "Roles are: \n";
        for(int i = 0; i < this.extras.size(); i++) {
            Role role = this.extras.get(i);
            all = all + role.toString() + "\n";
        }
        return all;
    }

    //returns true if the scene hasn't wrapped yet
    public boolean isActive() {
        if(this.scene == null) {
            return false;
        }
        return true;
    }

    //prints all available roles on both the extras and the starring parts
    public void availableRoles() {
        System.out.println("Available extra roles: ");
        for(int i = 0; i < extras.size(); i++) {
            Role extra = extras.get(i);
            if(extra.getPlayer() == null) {
                System.out.println(extra);
            }
        }
        this.scene.availableRoles();
    }

    public void cheatEndScene() {
        this.currentShots = 0;
    }

    //checks if there are any players on extra roles
    public boolean hasExtraPlayers() {
        for(int i = 0; i < this.extras.size(); i++) {
            if(this.extras.get(i).getPlayer() != null) {
                return true;
            }
        }
        return false;
    }

    //resets scenes
    public void resetLowest(){
        this.lowestTake = 1;
    }

    public void resetShots() {
        this.currentShots = this.totalShots;
    }
}