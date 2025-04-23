package modele.jeu.decorators;

import modele.jeu.Piece;
import modele.jeu.pieces.Roi;
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

        if (!(piece instanceof Roi)) {
            return base != null ? base.getCA() : cases;
        }

        Case c = piece.getCase();

        // Petit roque : on ajoute simplement la case deux colonnes à droite du roi
        Case droite1 = plateau.getCaseRelative(c, 1, 0);
        Case droite2 = plateau.getCaseRelative(c, 2, 0);

        if (droite2 != null) {
            cases.add(droite2);
        }

        if (base != null) {
            cases.addAll(base.getCA());
        }

        return cases;
    }
}
