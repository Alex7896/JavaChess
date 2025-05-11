package modele.jeu;

import modele.plateau.Case;
import modele.jeu.Piece;

import java.awt.*;

public class Coup {
    public Point dep;
    public Point arr;
    public Piece piece; // ✅ la pièce qui joue
    public Piece prise; // ✅ la pièce capturée s’il y en a une

    public Coup(Case caseDep, Case caseArr) {
        Point pos = caseDep.getPosition();
        dep = new Point(pos.x, pos.y);
        pos = caseArr.getPosition();
        arr = new Point(pos.x, pos.y);

        this.piece = caseDep.getPiece();   // on capture la pièce jouée
        this.prise = caseArr.getPiece();   // on capture la pièce prise si elle existe
    }
}
