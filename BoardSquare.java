import java.util.ArrayList;

/**
 * Created by marsha38 on 10/30/18.
 */
public class BoardSquare extends BasicShape {
    private int s1x; //spot 1 x
    private int s1y;
    private int s2x;
    private int s2y;
    private ArrayList<String> adjroomNames = new ArrayList<>();
    private ArrayList<BoardSquare> adjacentRooms = new ArrayList<>();
    ArrayList<Player> playersInRoom = new ArrayList<>();


    public void addAdjacent(String add) {
        adjroomNames.add(add);
    }

    /*takes the saved strings of adjacent rooms(from XML) and converts them to a list of type BoardSquare
    * the BoardSquare list adjacentRooms is what is actually used throughout the code
     */
    public void convertAdjacent(ArrayList<BoardSquare> allRooms) {
        for(int i = 0; i < allRooms.size(); i++) {
            BoardSquare room = allRooms.get(i);
            if(adjroomNames.contains(room.getName())) {
                adjacentRooms.add(room);
            }
        }
    }

    public void addPlayer(Player player) {
        if(playersInRoom != null) {
            playersInRoom.add(player);
        }
    }

    public void removePlayer(Player player) {
        playersInRoom.remove(player);
    }

    public boolean hasPlayers() {
        return !playersInRoom.isEmpty();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setS1(int x, int y) {
        s1x = x;
        s1y = y;
    }

    public void setS2(int x, int y) {
        s2x = x;
        s2y = y;
    }

    public int getS1x() {return s1x;}

    public int getS1y() {return s1y;}

    public int getS2x() {return s2x;}

    public int getS2y() {return s2y;}

    //name is the name of a possible adjacent room
    public boolean isAdjacent(String name) {

        if(adjroomNames.contains(name)) {
            return true;
        }
        return false;
    }

    public String getName() {
        return this.name;
    }

    //player calls this when they want to get the square to move to
    public BoardSquare getSingleAdj(String name) {
        BoardSquare adj = null;
        for(int i = 0; i < adjacentRooms.size(); i++) {
            BoardSquare room = adjacentRooms.get(i);
            if(room.getName().equals(name)) {
                adj = room;
                break;
            }
        }
        return adj;
    }

    public String toString() {
        String room = "name: " + this.name + "\nPlayers in Room:\n";
        for(int i = 0; i < playersInRoom.size(); i++) {
            room = room + playersInRoom.get(i) + "\n";
        }
        return room;
    }

    public void printNeighbors() {
        System.out.println("Neighbors: ");
        for(int i = 0; i < adjroomNames.size(); i++) {
            System.out.println(adjroomNames.get(i));
        }
    }


}
