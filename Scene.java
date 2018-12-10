import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by marsha38 on 10/30/18.
 */
public class Scene extends BasicShape {
    private JLabel label;
    private String image; //for GUI
    private String description;
    private int sceneNum;
    private int budget;
    private ArrayList<Role> stars = new ArrayList<>();
    private ArrayList<JButton> starButtons = new ArrayList<>();

    public Scene(String name, String img, int budget) {
        this.name = name;
        this.image = img;
        this.budget = budget;
    }


    public void addSceneInfo(int sceneNum, String cardInfo) {
        this.sceneNum = sceneNum;
        this.description = cardInfo;
    }


    public void addRole(Role newRole) {
        stars.add(newRole);
    }

    public String toString() {
        String whole = "\nScene: " + name + "\nBudget: " + budget + "\nSceneNum: " + sceneNum + "\nDescription: "
                + description.replace("\t", "");
        whole = whole + stringRoles();
        return whole;
    }

    private String stringRoles() {
        String all = "Roles are: \n";
        for(int i = 0; i < this.stars.size(); i++) {
            Role role = this.stars.get(i);
            all = all + role.toString() + "\n";
        }
        return all;
    }

    public int getBudget() {
        return this.budget;
    }

    //returns true if any players on scene
    public boolean hasPlayers() {
        for(int i = 0; i < stars.size(); i++) {
            if(stars.get(i).getPlayer() != null) {
                return true;
            }
        }
        return false;
    }


    public ArrayList<Role> getStars() {
        return stars;
    }

    //prints all available roles on scene
    public void availableRoles() {
        System.out.println("Available Starring roles: ");
        for (int i = 0; i < stars.size(); i++) {
            Role extra = stars.get(i);
            if (extra.getPlayer() == null) {
                System.out.println(extra);
            }
        }
    }

    public String getImage() {return this.image;}

    public void setLabel(JLabel newLabel) {
        this.label = newLabel;
    }

    public JLabel getLabel() {
        return this.label;
    }

    public void addButton(JButton button) {
        starButtons.add(button);
    }

    public ArrayList<JButton> getButtons() {
        return starButtons;
    }

}
