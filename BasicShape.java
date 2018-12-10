

/**
 * Created by lucasj8 on 11/7/18.
 */
//for the GUI -- anything that extends this extends for GUI
public class BasicShape {
    String name;
    int x;
    int y;
    int h;
    int w;

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setW(int w) {
        this.w = w;
    }

    public void setH(int h) {
        this.h = h;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public String getName() {
        return this.name;
    }
}
