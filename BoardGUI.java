import java.awt.*;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.*;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by lucasj8 on 11/26/18.
 */
public class BoardGUI extends JFrame{

    private Player[] players = new Player[2];
    private int scenesLeft;
    private int day;
    //labels
    private JLabel boardlabel;
    private JLayeredPane bPane;

    //ArrayList<sceneLabel> testLabels = new ArrayList<>();
    //Buttons
    private ArrayList<JLabel> playerIcons = new ArrayList<>();
    private ArrayList<BoardSquare> allSquares = new ArrayList<>();
    private ArrayList<Scene> unusedScenes = new ArrayList<>();
    private HashMap<JButton, Role> extraMap = new HashMap<>();
    private HashMap<JButton, BoardSquare> roomMap = new HashMap<>();
    private HashMap<JButton, Role> starMap = new HashMap<>();
    private HashMap<FilmRoom, JLabel> sceneMap = new HashMap<>();
    private HashMap<FilmRoom, JLabel> cardBackMap = new HashMap<>();
    private JButton endTurn;
    private JButton act;
    private JButton rehearse;
    private JButton upgrade;
    private JLabel playerInfo;
    private boolean p1Turn;
    private boolean playerMoved;
    private boardMouseListener listener;


    //delete later
    private JButton endDay;


    public BoardGUI() throws ParserConfigurationException {
        super("Deadwood");
        //XML
        Parser.parseXML(allSquares, unusedScenes);
        convertAllAdj();
        Collections.shuffle(unusedScenes);

        //set scene coordinates
        setCoords();

        listener = new boardMouseListener();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        bPane = getLayeredPane();
        boardlabel = new JLabel();
        ImageIcon icon = new ImageIcon("board.jpg");
        boardlabel.setIcon(icon);
        boardlabel.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
        //add board label to lowest layer
        bPane.add(boardlabel, new Integer(0));
        bPane.addMouseListener(listener);
        //set size
        setSize(icon.getIconWidth() + 200, icon.getIconHeight());
        //set Buttons
        setExtraButtons();
        setRoomButtons();
        this.day = 1;
        //create players
        createPlayers(2);
        p1Turn = true;
        playerMoved = false;
        setUI();
        setTakes();
        setVisible(true);
        newDay();
    }

    //Sets the Side Buttons and Player information
    private void setUI() {
        int pInd;
        if(p1Turn) {
            pInd = 0;
        } else {
            pInd = 1;
        }

        playerInfo = new JLabel( "<html>It is Player " + (pInd+1) + "'s turn<br/><br/> Player 1 - Blue<br/>Dollars: " + players[0].getDollars() + "<br/>Credits: "
                                + players[0].getCredits() + "<br/>Rehearsal Chips: 0" + "<br/><br/>Player 2 - Red<br/>Dollars: " + players[1].getDollars() +
                                "<br/>Credits: " + players[1].getCredits() + "<br/>Rehearsal Chips: 0" + "</html>");
        playerInfo.setBounds(1225, 0, 150, 200);
        bPane.add(playerInfo, new Integer(0));


        endTurn = new JButton("End Turn");
        endTurn.addMouseListener(listener);
        endTurn.setBounds(1225, 340, 150, 60);
        bPane.add(endTurn, new Integer(0));

        act = new JButton("Act");
        act.addMouseListener(listener);
        act.setBounds(1225, 200, 150, 60);
        bPane.add(act, new Integer(0));
        act.setVisible(false);

        rehearse = new JButton("Rehearse");
        rehearse.addMouseListener(listener);
        rehearse.setBounds(1225, 270, 150, 60);
        bPane.add(rehearse, new Integer(0));
        rehearse.setVisible(false);

        upgrade = new JButton("Upgrade");
        upgrade.addMouseListener(listener);
        upgrade.setBounds(1225, 410, 150, 60);
        bPane.add(upgrade, new Integer(0));
        upgrade.setVisible(false);


        endDay = new JButton("End Day");
        endDay.addMouseListener(listener);
        endDay.setBounds(1225, 480, 150, 60);
        bPane.add(endDay, new Integer(0));
        endDay.setVisible(true);
    }

    //updates the sidebar UI by updating player information and setting buttons visibility to true or false
    private void updateUI() {
        int pInd;
        int p1chips;
        int p2chips;

        if(p1Turn) {
            pInd = 0;
        } else {
            pInd = 1;
        }

        Player player = players[pInd];

        if(players[0].isOnRole()) {
            p1chips = players[0].getRole().getChips();
        } else {
            p1chips = 0;
        }

        if(players[1].isOnRole()) {
            p2chips = players[1].getRole().getChips();
        } else {
            p2chips = 0;
        }

        playerInfo.setVisible(false);
        playerInfo = new JLabel( "<html>It is Player " + (pInd+1) + "'s turn<br/>The day is " + this.day + "<br/><br/> Player 1 - Blue<br/>Dollars: " + players[0].getDollars() + "<br/>Credits: "
                + players[0].getCredits() + "<br/>Rehearsal Chips: " + p1chips + "<br/><br/>Player 2 - Red<br/>Dollars: " + players[1].getDollars() +
                "<br/>Credits: " + players[1].getCredits() + "<br/>Rehearsal Chips: " + p2chips + "</html>");
        playerInfo.setBounds(1225, 0, 150, 200);
        bPane.add(playerInfo, new Integer(0));
        playerInfo.setVisible(true);
        repaint();

        if(player.isOnCasting()) {
            upgrade.setVisible(true);
        } else {
            upgrade.setVisible(false);
        }
        if(player.isOnRole()) {
            FilmRoom room = player.getFilmLocation();
            act.setVisible(true);
            if(player.getRole().getChips() < room.getScene().getBudget() - 1) {
                rehearse.setVisible(true);
            }
            else {
                rehearse.setVisible(false);
            }
        } else {
            act.setVisible(false);
            rehearse.setVisible(false);
        }
    }

    //Sets the shot.png images over the shot counters
    private void setTakes() {
        for(int i = 0; i < allSquares.size(); i++) {
            if(allSquares.get(i) instanceof FilmRoom) {
                FilmRoom f = (FilmRoom) allSquares.get(i);
                ArrayList<Take> takes = f.getTakes();
                for(int j = 0; j < takes.size(); j++) {
                    Take t = takes.get(j);
                    JLabel take = new JLabel();
                    ImageIcon icon = new ImageIcon("shot.png");
                    take.setIcon(icon);
                    take.setBounds(t.getX(), t.getY(), t.getW(), t.getH());
                    bPane.add(take, new Integer(1));
                    t.setTake(take);
                }
            }
        }
    }

    //Sets the invisible buttons over the extra roles
    private void setExtraButtons() {
        for (int i = 0; i < allSquares.size(); i++) {
            if (allSquares.get(i) instanceof FilmRoom) {
                ArrayList<Role> extras = ((FilmRoom) allSquares.get(i)).getExtras();
                for (int j = 0; j < extras.size(); j++) {
                    JButton extra = new JButton();
                    Role e = extras.get(j);
                    extra.addMouseListener(listener);
                    extra.setBounds(e.getX(), e.getY(), e.getW(), e.getH());
                    extraMap.put(extra, extras.get(j));
                    extra.setOpaque(false);
                    extra.setContentAreaFilled(false);
                    extra.setBorderPainted(false);
                    bPane.add(extra, new Integer(2));
                }
            }
        }
    }

    //Sets the invisible buttons over the rooms
    private void setRoomButtons() {
        for(int i = 0; i<allSquares.size(); i++) {
            JButton room = new JButton();
            BoardSquare b = allSquares.get(i);
            room.addMouseListener(listener);

            if(b.getName().equals("Train Station")) {
                room.setBounds(7, 6, 185, 437);
                JButton rb2 = new JButton();
                rb2.addMouseListener(listener);
                rb2.setBounds(192, 6, 58, 240);
                rb2.setOpaque(false);
                rb2.setContentAreaFilled(false);
                rb2.setBorderPainted(false);
                roomMap.put(rb2, b);
                bPane.add(rb2, new Integer(2));
            }
            else if(b.getName().equals("Jail")) {
                room.setBounds(262, 6, 331, 235);
            }
            else if(b.getName().equals("General Store")) {
                room.setBounds(203, 256, 390, 188);
            }
            else if(b.getName().equals("Ranch")) {
                room.setBounds(232, 459, 362, 239);
            }
            else if(b.getName().equals("Bank")) {
                room.setBounds(607, 458, 384, 187);
            }
            else if(b.getName().equals("Church")) {
                room.setBounds(607, 661, 325, 233);
            }
            else if(b.getName().equals("Hotel")) {
                room.setBounds(945, 692, 248, 201);
                JButton rb2 = new JButton();
                rb2.addMouseListener(listener);
                rb2.setBounds(976, 662, 30, 30);
                rb2.setOpaque(false);
                rb2.setContentAreaFilled(false);
                rb2.setBorderPainted(false);
                roomMap.put(rb2, b);
                bPane.add(rb2, new Integer(1));
                JButton rb3 = new JButton();
                rb3.addMouseListener(listener);
                rb3.setBounds(1006, 458, 186, 235);
                rb3.setOpaque(false);
                rb3.setContentAreaFilled(false);
                rb3.setBorderPainted(false);
                roomMap.put(rb3, b);
                bPane.add(rb3, new Integer(1));
            }
            else if(b.getName().equals("Main Street")) {
                room.setBounds(608, 8, 586, 181);
                JButton rb2 = new JButton();
                rb2.addMouseListener(listener);
                rb2.setBounds(893, 189, 300, 14);
                rb2.setOpaque(false);
                rb2.setContentAreaFilled(false);
                rb2.setBorderPainted(false);
                roomMap.put(rb2, b);
                bPane.add(rb2, new Integer(1));
                JButton rb3 = new JButton();
                rb3.addMouseListener(listener);
                rb3.setBounds(1013, 203, 181, 32);
                rb3.setOpaque(false);
                rb3.setContentAreaFilled(false);
                rb3.setBorderPainted(false);
                roomMap.put(rb3, b);
                bPane.add(rb3, new Integer(1));
            }
            else if(b.getName().equals("Saloon")) {
                room.setBounds(608, 203, 195, 241);
                JButton rb2 = new JButton();
                rb2.addMouseListener(listener);
                rb2.setBounds(803, 241, 173, 203);
                rb2.setOpaque(false);
                rb2.setContentAreaFilled(false);
                rb2.setBorderPainted(false);
                roomMap.put(rb2, b);
                bPane.add(rb2, new Integer(1));
            }
            else if(b.getName().equals("Secret Hideout")) {
                room.setBounds(7, 705, 587, 188);
                JButton rb2 = new JButton();
                rb2.addMouseListener(listener);
                rb2.setBounds(7, 683, 189, 22);
                rb2.setOpaque(false);
                rb2.setContentAreaFilled(false);
                rb2.setBorderPainted(false);
                roomMap.put(rb2, b);
                bPane.add(rb2, new Integer(1));
            }
            else {
                room.setBounds(b.getX(), b.getY(), b.getW(), b.getH());
            }


            roomMap.put(room, b);
            room.setOpaque(false);
            room.setContentAreaFilled(false);
            room.setBorderPainted(false);
            bPane.add(room, new Integer(1));
        }
    }

    //Calls functions based off of what is clicked
    class boardMouseListener implements MouseListener {
        public void mouseClicked(MouseEvent e) {
            if (extraMap.containsKey(e.getSource())) {
                takeRoleTry(extraMap.get(e.getSource()));
            }
            else if(starMap.containsKey(e.getSource())) {
                takeRoleTry(starMap.get(e.getSource()));
            }
            else if(roomMap.containsKey(e.getSource())) {
                if(playerMoved == false) {
                  moveTry(roomMap.get(e.getSource()));
                }
            }
            else if(e.getSource() == endTurn) {
                turnEnd();
            }
            else if(e.getSource() == act) {
                actTry();
            }
            else if(e.getSource() == rehearse) {
                rehearseTry();
            }
            else if(e.getSource() == upgrade) {
                upgradeTry();
            }
            else if(e.getSource() == endDay) {
                turnEnd();
                scenesLeft = 1;
            }
            if(scenesLeft == 1) {
                if(day < 3) {
                    day++;
                    newDay();
                }
                else{
                    try {
                        endGame();
                    }catch(Exception ex) {
                        System.out.println("ParserError");
                    }
                }
            }
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }

    //Function for ending game
    private void endGame() throws ParserConfigurationException {
        ArrayList<Player> winners = GameManager.calcWinner(this.players);
        if(winners.size() == 1) {
            JOptionPane.showMessageDialog(null, "The winner is: " + winners.get(0) + " with score " + winners.get(0).getFinalScore());
        }
        else {
            String msg = "It's a tie! The winners are: ";
            for(int i = 0; i < winners.size(); i++) {
                Player winner = winners.get(i);
                msg = msg + "\n" + winner + " with score " + winner.getFinalScore();
            }
            JOptionPane.showMessageDialog(null, msg);
        }
        String[] options = {"Yes! Deadwood is fun.", "No way.  Deadwood is boring."};
        int x = JOptionPane.showOptionDialog(null, "Do you want to play again?",
                "End game",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if(x == 0) {
            setVisible(false);
            main(null);
        }
        else{
            System.exit(0);
        }
    }

    //Function for upgrading rank
    private void upgradeTry() {
        int pInd;
        if(p1Turn) {
            pInd = 0;
        }
        else{
            pInd = 1;
        }
        Player player = players[pInd];

        String[] options = {"2", "3", "4", "5", "6"};
        int x = JOptionPane.showOptionDialog(null, "Which rank would you like to upgrade to?",
                "Upgrade",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if(x != -1) {
            String[] options2 = {"Dollars", "Credits"};
            int y = JOptionPane.showOptionDialog(null, "How would you like to pay for this upgrade?",
                    "Upgrade",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options2, options2[0]);
            if(y != -1) {
                if (CastingOffice.canUpgrade(player, options2[y].toLowerCase(), Integer.parseInt(options[x]))) {
                    player.upgrade(options2[y].toLowerCase(), CastingOffice.getUpgradeAmount(options2[y].toLowerCase(),
                            Integer.parseInt(options[x])), Integer.parseInt(options[x]));
                    JOptionPane.showMessageDialog(null, "New level: " + player.getRank() +
                                                ".  Your turn is now over");
                    //update player icons
                    int rank = player.getRank();
                    CastingOffice office = player.getCastingLocation();
                    if(pInd == 0) {
                        player.setImg("dice/b" + rank + ".png");
                        JLabel newIcon = new JLabel();
                        playerIcons.get(pInd).setVisible(false);
                        JLabel old = playerIcons.get(pInd);
                        bPane.remove(old);
                        ImageIcon icon = new ImageIcon(player.getImgString());
                        newIcon.setIcon(icon);
                        newIcon.setBounds(office.getS1x(), office.getS1y(), icon.getIconWidth(), icon.getIconHeight());
                        playerIcons.set(0,newIcon);
                        bPane.add(playerIcons.get(pInd), new Integer(2));
                        playerIcons.get(pInd).setVisible(true);
                    }
                    else {
                        player.setImg("dice/r" + rank + ".png");
                        JLabel newIcon = new JLabel();
                        playerIcons.get(pInd).setVisible(false);
                        JLabel old = playerIcons.get(pInd);
                        bPane.remove(old);
                        ImageIcon icon = new ImageIcon(player.getImgString());
                        newIcon.setIcon(icon);
                        newIcon.setBounds(office.getS2x(), office.getS2y(), icon.getIconWidth(), icon.getIconHeight());
                        playerIcons.set(1,newIcon);
                        bPane.add(playerIcons.get(pInd), new Integer(2));
                        playerIcons.get(pInd).setVisible(true);
                    }
                    turnEnd();
                }
            }
        }
    }

    //Functionality for the rehearse button
    private void rehearseTry() {
        int pInd;
        if(p1Turn) {
            pInd = 0;
        }
        else{
            pInd = 1;
        }
        Player player = players[pInd];
        if(player.rehearse()) {
            turnEnd();
        }
    }

    //Functionality for the act button
    private void actTry() {
        int pInd;
        if(p1Turn) {
            pInd = 0;
        }
        else{
            pInd = 1;
        }
        Player player = players[pInd];

        if(player.isOnRole()) {
            boolean sceneFinished = player.act();
            if(sceneFinished) {
                this.scenesLeft --;
                FilmRoom room = player.getFilmLocation();
                JLabel scene = sceneMap.remove(room);
                Iterator<JButton> iter = room.getScene().getButtons().iterator();
                while (iter.hasNext()) {
                    JButton role = iter.next();
                    bPane.remove(role);
                    iter.remove();
                }
                bPane.remove(scene);
                if(pInd == 0) {
                    playerIcons.get(pInd).setLocation(room.getS1x(), room.getS1y());
                    Player p2 = players[1];
                    if(p2.getFilmLocation() == room) {
                        playerIcons.get(1).setLocation(room.getS2x(), room.getS2y());
                    }
                }
                if(pInd == 1) {
                    playerIcons.get(pInd).setLocation(room.getS2x(), room.getS2y());
                    Player p1 = players[0];
                    if(p1.getFilmLocation() == room) {
                        playerIcons.get(0).setLocation(room.getS1x(), room.getS1y());
                    }
                }
                room.endRoom();
            }
            turnEnd();
        }
        else{
            JOptionPane.showMessageDialog(null, "You must be on a role to act");
        }
    }


    private void turnEnd() {
        playerMoved = false;
        p1Turn = !p1Turn;
        updateUI();
    }

    //Functionality for taking a role when one is clicked on
    private void takeRoleTry(Role role) {
        int pInd;
        if(p1Turn) {
            pInd = 0;
        }
        else{
            pInd = 1;
        }
        Player player = players[pInd];
        if(player.isOnFilm()) {
            if(player.getFilmLocation().isActive()) {
                FilmRoom film = player.getFilmLocation();
                if(player.takeRole(role)) {
                    if(role.isExtra()) {
                        playerIcons.get(pInd).setLocation(role.getX() + 3, role.getY() + 3);
                    }
                    else{
                        playerIcons.get(pInd).setLocation(film.getX() + role.getX() + 1, film.getY() + role.getY() - 1);
                    }
                    JOptionPane.showMessageDialog(null, "You have successfully taken the role.  Your turn is now over.");
                    turnEnd();
                }
                else{
                    JOptionPane.showMessageDialog(null, "You can't take that role");
                }
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "You can't take a role when you're not on a film square");
        }
    }

    //initializes the adjacent squares for every square
    private void convertAllAdj() {
        for (int i = 0; i < allSquares.size(); i++) {
            BoardSquare square = allSquares.get(i);
            square.convertAdjacent(allSquares);
        }
    }

    //puts scenes in films -- scenes have already been shuffled
    private void initScenes() {
        this.scenesLeft = 0;
        for (int i = 0; i < allSquares.size(); i++) {
            BoardSquare square = allSquares.get(i);
            if (square instanceof FilmRoom) {
                FilmRoom room = (FilmRoom) square;
                Scene scene = unusedScenes.remove(0);
                room.newScene(scene);
                this.scenesLeft++;
            }
        }

        if(this.day == 1) {
            ImageIcon icon = new ImageIcon("CardBack.jpg");
            Image img = icon.getImage();
            Image newimg = img.getScaledInstance(205, 111, Image.SCALE_SMOOTH);
            icon = new ImageIcon(newimg);
            for (int i = 0; i < allSquares.size(); i++) {
                if (allSquares.get(i) instanceof FilmRoom) {
                    FilmRoom fr = (FilmRoom) allSquares.get(i);
                    JLabel scene = new JLabel();
                    scene.setIcon(icon);
                    scene.setBounds(fr.getX(), fr.getY(), icon.getIconWidth(), icon.getIconHeight());
                    bPane.add(scene, new Integer(1));
                    cardBackMap.put(fr, scene);
                }
            }
        }
        else{
            for(int i = 0; i < allSquares.size(); i++) {
                if(allSquares.get(i) instanceof FilmRoom) {
                    cardBackMap.get(allSquares.get(i)).setVisible(true);
                }
            }
        }
    }

    //"flips" a scene card when a FilmRoom hasn't been visited
    private void flipScene(FilmRoom fr){
        ImageIcon icon = new ImageIcon("cards/" + fr.getScene().getImage());
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(205, 111, Image.SCALE_SMOOTH);
        icon = new ImageIcon(newimg);
        cardBackMap.get(fr).setVisible(false);
        JLabel scene = new JLabel();
        scene.setIcon(icon);
        scene.setBounds(fr.getX(), fr.getY(), icon.getIconWidth(), icon.getIconHeight());
        sceneMap.put(fr, scene);
        bPane.add(scene, new Integer(1));

        ArrayList<Role> stars = fr.getScene().getStars();
        for(int i = 0; i < stars.size(); i++) {
            JButton star = new JButton();
            Role s = stars.get(i);
            star.addMouseListener(listener);
            star.setBounds(s.getX() + fr.getX(), s.getY() + fr.getY(), s.getW(), s.getH());
            starMap.put(star, s);
            fr.getScene().addButton(star);
            star.setOpaque(false);
            star.setContentAreaFilled(false);
            star.setBorderPainted(false);
            bPane.add(star, new Integer(2));
        }

        bPane.repaint();
    }

    //Sets coordinates for all rooms
    private void setCoords() {
        for (int i = 0; i < allSquares.size(); i++) {
            if (allSquares.get(i) instanceof FilmRoom) {
                FilmRoom fr = (FilmRoom) allSquares.get(i);
                if (fr.getName().equals("Train Station")) {
                    fr.setS1(17,225);
                    fr.setS2(57,225);
                } else if (fr.getName().equals("Jail")) {
                    fr.setS1(292, 157);
                    fr.setS2(342, 157);
                } else if (fr.getName().equals("Main Street")) {
                    fr.setS1(819, 100);
                    fr.setS2(869, 100);
                } else if (fr.getName().equals("General Store")) {
                    fr.setS1(291, 398);
                    fr.setS2(341, 398);
                } else if (fr.getName().equals("Saloon")) {
                    fr.setS1(642, 404);
                    fr.setS2(692, 404);
                } else if (fr.getName().equals("Ranch")) {
                    fr.setS1(276, 635);
                    fr.setS2(326, 635);
                } else if (fr.getName().equals("Bank")) {
                    fr.setS1(842, 479);
                    fr.setS2(621, 599);
                } else if (fr.getName().equals("Secret Hideout")) {
                    fr.setS1(249, 830);
                    fr.setS2(299, 830);
                } else if (fr.getName().equals("Church")) {
                    fr.setS1(733, 680);
                    fr.setS2(783, 680);
                } else if (fr.getName().equals("Hotel")) {
                    fr.setS1(1121, 639);
                    fr.setS2(1019, 465);
                }
            }
            else if(allSquares.get(i) instanceof CastingOffice){
                allSquares.get(i).setS1(60, 470);
                allSquares.get(i).setS2(110, 470);
            }
            else {
                allSquares.get(i).setS1(994, 270);
                allSquares.get(i).setS2(1150, 270);
            }
        }
    }

    private void createPlayers(int playerNum) {
        this.players = new Player[playerNum];
        for(int i = 0; i < playerNum; i++) {
            Player p = new Player("player" + (i + 1));
            this.players[i] = p;
            if(i == 0) {
                p.setImg("dice/b1.png");
                JLabel plabel = new JLabel();
                ImageIcon icon = new ImageIcon(p.getImgString());
                plabel.setIcon(icon);
                plabel.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
                playerIcons.add(plabel);
                bPane.add(plabel, new Integer(2));
            }
            else if(i == 1) {
                p.setImg("dice/r1.png");
                JLabel plabel = new JLabel();
                ImageIcon icon = new ImageIcon(p.getImgString());
                plabel.setIcon(icon);
                plabel.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
                playerIcons.add(plabel);
                bPane.add(plabel, new Integer(2));
            }
        }
    }

    //Function for starting a new day
    private void newDay() {

        for(int i = 0; i<allSquares.size(); i++) {
            BoardSquare room = allSquares.get(i);
            if(room instanceof FilmRoom) {
                ArrayList<Take> takes = ((FilmRoom) room).getTakes();
                ((FilmRoom) room).resetLowest();
                ((FilmRoom) room).resetShots();
                for(int j = 0; j<takes.size(); j++) {
                    takes.get(j).makeActive();
                }
            }
        }
        //move players to trailers
        BoardSquare trailer = null;
        for(int i = 0; i < allSquares.size(); i++) {
            BoardSquare square = allSquares.get(i);
            if (square.getName().equals("trailer")) {
                trailer = square;
                break;
            } else if (square instanceof FilmRoom) {
                FilmRoom room = (FilmRoom) square;
                room.setNotVisited();
                if (room.getScene() != null) {
                    if(room.getScene().getButtons() != null) {
                        Iterator<JButton> iter = room.getScene().getButtons().iterator();
                        while (iter.hasNext()) {
                            JButton role = iter.next();
                            bPane.remove(role);
                            iter.remove();
                        }
                    }
                    JLabel scene = sceneMap.remove(room);
                    if(scene != null) {
                        bPane.remove(scene);
                    }
                    room.endRoom();
                }
            }
        }
        this.starMap = new HashMap<>();
        initScenes();

        //move playericons to trailers
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            player.moveTo(trailer);
            if (i == 0) {
                playerIcons.get(i).setLocation(trailer.getS1x(), trailer.getS1y());
            }
            if (i == 1) {
                playerIcons.get(i).setLocation(trailer.getS2x(), trailer.getS2y());
            }
        }
        updateUI();
        bPane.repaint();
        JOptionPane.showMessageDialog(null, "There are 3 days in the game.\n"
                + "The day is " + day + ".  All players are now in the trailer");
    }

    //Function for attempting to move to a new room when one is clicked on
    private void moveTry(BoardSquare room) {
        Player player;
        int pt;
        if(p1Turn) {
            player = players[0];
            pt = 0;
        }
        else {
            player = players[1];
            pt = 1;
        }
        if(player.move(room.getName())) {
            if(pt == 0) {
                playerIcons.get(pt).setLocation(room.getS1x(), room.getS1y());
            } else {
                playerIcons.get(pt).setLocation(room.getS2x(), room.getS2y());
            }
            playerMoved = true;
            if(room instanceof FilmRoom) {
                if(!((FilmRoom) room).getIsVisited()) {
                    ((FilmRoom) room).setIsVisited();
                    flipScene((FilmRoom) room);
                }
            }
        }
        else if(!(room.getName().equals(player.getLocation().getName()))) {
            JOptionPane.showMessageDialog(null, "You cannot move to " + room.getName());
        }
        updateUI();
    }

    public static void main(String[] args) throws ParserConfigurationException {
        BoardGUI board = new BoardGUI();
    }

}
