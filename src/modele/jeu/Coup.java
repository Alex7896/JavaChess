package modele.jeu;

import modele.plateau.Case;

import java.awt.*;

public class Coup {
    public Point dep;
    public Point arr;

    public Coup(Case caseDep, Case caseArr) {
        Point pos = caseDep.getPosition();
        dep = new Point(pos.x, pos.y);
        pos = caseArr.getPosition();
        arr = new Point(pos.x, pos.y);
    }


}
