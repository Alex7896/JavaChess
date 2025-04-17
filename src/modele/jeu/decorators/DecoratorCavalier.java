package modele.jeu.decorators;

import modele.jeu.Piece;
import modele.plateau.Case;
import modele.plateau.Plateau;

import java.util.ArrayList;

public class DecoratorCavalier extends DecoratorCasesAccessibles {

    public DecoratorCavalier(Piece piece, Plateau plateau, DecoratorCasesAccessibles base) {
        super(piece, plateau, base);
    }

    @Override
    public ArrayList<Case> getMesCA() {
        ArrayList<Case> cases = new ArrayList<>();
        Case c = piece.getCase();

        int[][] mouvements = {
                {2, 1}, {1, 2}, {-1, 2}, {-2, 1},
                {-2, -1}, {-1, -2}, {1, -2}, {2, -1}
        };

        for (int[] mvt : mouvements) {
            Case cible = plateau.getCaseRelative(c, mvt[0], mvt[1]);
            if (cible != null) {
                if (cible.getPiece() == null || cible.getPiece().getCouleur() != piece.getCouleur()) {
                    cases.add(cible);
                }
            }
        }

        return cases;
    }
}
