/**
 * Created by lucasj8 on 11/7/18.
 */
public class Upgrade extends BasicShape {
    String level;
    int currency;
    int amount;
    public Upgrade(String level, int currency, int amount) {
        this.level = level;
        this.currency = currency;
        this.amount = amount;
    }
}
