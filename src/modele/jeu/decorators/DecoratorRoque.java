package modele.jeu.decorators;

import modele.jeu.Piece;
import modele.jeu.pieces.Roi;
import modele.jeu.pieces.Tour;
import modele.plateau.Case;
import modele.plateau.Plateau;

import java.util.ArrayList;

public class DecoratorRoque extends DecoratorCasesAccessibles {

    public DecoratorRoque(Piece piece, Plateau plateau, DecoratorCasesAccessibles base) {
        super(piece, plateau, base);
    }

    @Override
    public ArrayList<Case> getMesCA() {
        ArrayList<Case> cases = new ArrayList<>();

        if (!(piece instanceof Roi) || piece.aDejaBouge()) {
            return base != null ? base.getCA() : cases;
        }

        Case c = piece.getCase();

        // 🏰 Petit roque (à droite)
        Case droite1 = plateau.getCaseRelative(c, 1, 0);
        Case droite2 = plateau.getCaseRelative(c, 2, 0);
        Case tourDroite = plateau.getCaseRelative(c, 3, 0); // La tour doit être 3 cases à droite du roi

        if (droite1 != null && droite2 != null && tourDroite != null &&
                droite1.getPiece() == null &&
                droite2.getPiece() == null &&
                tourDroite.getPiece() instanceof Tour &&
                !tourDroite.getPiece().aDejaBouge()) {
            cases.add(droite2);
        }

        // 🏰 Grand roque (à gauche)
        Case gauche1 = plateau.getCaseRelative(c, -1, 0);
        Case gauche2 = plateau.getCaseRelative(c, -2, 0);
        Case gauche3 = plateau.getCaseRelative(c, -3, 0);
        Case tourGauche = plateau.getCaseRelative(c, -4, 0); // Tour 4 cases à gauche du roi

        if (gauche1 != null && gauche2 != null && gauche3 != null && tourGauche != null &&
                gauche1.getPiece() == null &&
                gauche2.getPiece() == null &&
                gauche3.getPiece() == null &&
                tourGauche.getPiece() instanceof Tour &&
                !tourGauche.getPiece().aDejaBouge()) {
            cases.add(gauche2);
        }

        if (base != null) {
            cases.addAll(base.getCA());
        }

        return cases;
    }

}