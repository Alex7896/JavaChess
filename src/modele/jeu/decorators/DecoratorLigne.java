package modele.jeu.decorators;

import modele.jeu.Piece;
import modele.plateau.Case;
import modele.plateau.Plateau;

import java.util.ArrayList;

public class DecoratorLigne extends DecoratorCasesAccessibles {
    private int porteeMax;

    public DecoratorLigne(Piece piece, Plateau plateau, DecoratorCasesAccessibles base, int porteeMax) {
        super(piece, plateau, base);
        this.porteeMax = porteeMax;
    }

    @Override
    public ArrayList<Case> getMesCA() {
        ArrayList<Case> cases = new ArrayList<>();
        Case c = piece.getCase();

        int[][] directions = {
                {1, 0},  // droite
                {-1, 0}, // gauche
                {0, 1},  // bas
                {0, -1}  // haut
        };

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            Case next = c;
            int portee = 0;

            while (portee < porteeMax) {
                next = plateau.getCaseRelative(next, dx, dy);
                if (next == null) break;

                if (next.getPiece() == null) {
                    cases.add(next);
                } else {
                    if (next.getPiece().getCouleur() != piece.getCouleur()) {
                        cases.add(next);
                    }
                    break;
                }

                portee++;
            }
        }

        return cases;
    }
}
