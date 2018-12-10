import javax.swing.*;

/**
 * Created by lucasj8 on 11/7/18.
 */
public class Take extends BasicShape {
    private int takeNumber;
    private boolean active;
    JLabel take;
    public Take(int takeNum) {
        this.takeNumber = takeNum;
        this.active = true;
    }

    public boolean isActive() {
        return this.active;
    }

    public void reset() {
        this.active = true;
    }

    public void makeInactive() {
        take.setVisible(false);
    }

    public void makeActive() {
        take.setVisible(true);
    }
    public int getNum() {
        return this.takeNumber;
    }

    public void setTake(JLabel take) {
        this.take = take;
    }

    public JLabel getTake() {
        return this.take;
    }
}
