import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;

/**
 * Created by lucasj8 on 11/8/18.
 */
public class Parser {

    public static void parseXML(ArrayList<BoardSquare> allSquares, ArrayList<Scene> unusedScenes) throws ParserConfigurationException {
        Document boardInfo = getDocFromFile("board.xml");
        Document cardInfo = getDocFromFile("cards.xml");
        parseBoard(boardInfo, allSquares);
        parseCard(cardInfo, unusedScenes);
    }


    // returns a Document object after loading the book.xml file.
    private static Document getDocFromFile(String filename)
            throws ParserConfigurationException{
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = null;
            try{
                doc = db.parse(filename);
            } catch (Exception ex){
                System.out.println("XML parse failure");
                ex.printStackTrace();
            }
            return doc;
        } // exception handling
    }


    private static void parseBoard(Document b, ArrayList<BoardSquare> allSquares) {
        Element root = b.getDocumentElement();
        getSets(root.getElementsByTagName("set"), allSquares);
        getTrailer(root.getElementsByTagName("trailer").item(0), allSquares);
        getOffice(root.getElementsByTagName("office").item(0), allSquares);
    }


    private static void getOffice(Node office, ArrayList<BoardSquare> allSquares) {
        CastingOffice off = new CastingOffice();
        off.setName("office");
        NodeList allInfo = office.getChildNodes();
        for(int i = 0; i < allInfo.getLength(); i++) {
            Node info = allInfo.item(i);
            if("neighbors".equals(info.getNodeName())) {
                getNeighbors(info.getChildNodes(), off);
            }
            else if("area".equals(info.getNodeName())) {
                //make this equal to an array later
                getArea(info, off);
            }
            else if("upgrades".equals(info.getNodeName())) {
                getUpgrades(info.getChildNodes(), off);
            }
        }
        allSquares.add(off);
    }


    private static void getUpgrades(NodeList upgrades, CastingOffice office) {
        for(int i = 0; i < upgrades.getLength(); i++) {
            Node upgrade = upgrades.item(i);
            if("upgrade".equals(upgrade.getNodeName())) {
                int level = Integer.parseInt(upgrade.getAttributes().getNamedItem("level").getNodeValue());
                String currency = upgrade.getAttributes().getNamedItem("currency").getNodeValue();
                int amt = Integer.parseInt(upgrade.getAttributes().getNamedItem("amt").getNodeValue());
                Upgrade up = new Upgrade(currency, level, amt);
                //add area
                NodeList sub = upgrade.getChildNodes();
                for(int k = 0; k < sub.getLength(); k++) {
                    Node area = sub.item(k);
                    if ("area".equals(area.getNodeName())) {
                        getArea(area, up);
                    }
                }
                office.addUpgrade(up);
            }
        }
    }


    private static void getTrailer(Node trailer, ArrayList<BoardSquare> allSquares) {
        BoardSquare trail = new BoardSquare();
        allSquares.add(trail);
        trail.setName("trailer");
        NodeList trailInfo = trailer.getChildNodes();
        for(int i = 0; i < trailInfo.getLength(); i++) {
            Node info = trailInfo.item(i);
            if("neighbors".equals(info.getNodeName())) {
                getNeighbors(info.getChildNodes(), trail);
            }
            else if("area".equals(info.getNodeName())) {
                getArea(info, trail);
            }
        }
    }

    private static void getSets(NodeList sets, ArrayList<BoardSquare> allSquares) {
        for(int i = 0; i < sets.getLength(); i++) {
            Node set = sets.item(i);
            FilmRoom film = new FilmRoom();
            String setName = set.getAttributes().getNamedItem("name").getNodeValue();
            film.setName(setName);
            NodeList children = set.getChildNodes();
            for(int j = 0; j < children.getLength(); j++) {
                Node sub = children.item(j);
                if("neighbors".equals(sub.getNodeName())) {
                    getNeighbors(sub.getChildNodes(), film);
                }
                else if("area".equals(sub.getNodeName())) {
                    getArea(sub, film);
                }
                else if("takes".equals(sub.getNodeName())) {
                    getTakes(sub.getChildNodes(), film);
                }
                else if("parts".equals(sub.getNodeName())) {
                    setFilmRoles(sub.getChildNodes(), film);
                }
            }
            allSquares.add(film);
        }
    }


    private static void setFilmRoles(NodeList parts, FilmRoom film) {
        for(int i = 0; i < parts.getLength(); i++) {
            Node part = parts.item(i);
            if("part".equals(part.getNodeName())) {
                Role role = getRole(part, true);
                film.addExtras(role);
            }
        }
    }


    private static Role getRole(Node part, boolean extra) {
        String name = part.getAttributes().getNamedItem("name").getNodeValue();
        int level = Integer.parseInt(part.getAttributes().getNamedItem("level").getNodeValue());
        Role role = new Role(name, level, extra);
        NodeList partInfo = part.getChildNodes();
        for (int k = 0; k < partInfo.getLength(); k++) {
            Node info = partInfo.item(k);
            if ("area".equals(info.getNodeName())) {
                getArea(info, role);
            } else if ("line".equals(info.getNodeName())) {
                String line = info.getTextContent();
                role.addLine(line);
            }
        }
        return role;
    }


    private static void getNeighbors(NodeList neighbors, BoardSquare square) {
        for(int i = 0; i < neighbors.getLength(); i++) {
            Node subChild = neighbors.item(i);
            if("neighbor".equals(subChild.getNodeName())) {
                String neighbor = subChild.getAttributes().getNamedItem("name").getNodeValue();
                square.addAdjacent(neighbor);
            }
        }
    }




    private static void getTakes(NodeList takes, FilmRoom film) {
        for(int i = 0; i < takes.getLength(); i++) {
            Node takeInfo = takes.item(i);
            if("take".equals(takeInfo.getNodeName())) {
                int takeNum = Integer.parseInt(takeInfo.getAttributes().getNamedItem("number").getNodeValue());
                Take currTake = new Take(takeNum);
                Node takeArea = takeInfo.getFirstChild();
                getArea(takeArea, currTake);
                film.addTakes(currTake);
            }
        }
    }




    private static void getArea(Node area, BasicShape square) {
        square.setX(Integer.parseInt(area.getAttributes().getNamedItem("x").getNodeValue()));
        square.setY(Integer.parseInt(area.getAttributes().getNamedItem("y").getNodeValue()));
        square.setH(Integer.parseInt(area.getAttributes().getNamedItem("h").getNodeValue()));
        square.setW(Integer.parseInt(area.getAttributes().getNamedItem("w").getNodeValue()));
    }


    private static void parseCard(Document c, ArrayList<Scene> unusedScenes) {
        Element root = c.getDocumentElement();
        NodeList cards = root.getElementsByTagName("card");
        for(int i = 0; i < cards.getLength(); i ++) {
            Node card = cards.item(i);
            String name = card.getAttributes().getNamedItem("name").getNodeValue();
            String img = card.getAttributes().getNamedItem("img").getNodeValue();
            int budget = Integer.parseInt(card.getAttributes().getNamedItem("budget").getNodeValue());
            Scene newScene = new Scene(name, img, budget);

            NodeList children = card.getChildNodes();
            for(int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                if("scene".equals(child.getNodeName())) {
                    int sceneNum = Integer.parseInt(child.getAttributes().getNamedItem("number").getNodeValue());
                    String cardInfo = child.getTextContent();
                    newScene.addSceneInfo(sceneNum, cardInfo);
                }
                else if("part".equals(child.getNodeName())) {
                    Role newRole = getRole(child, false);
                    newScene.addRole(newRole);
                }
            }
            unusedScenes.add(newScene);
        }
    }
}
