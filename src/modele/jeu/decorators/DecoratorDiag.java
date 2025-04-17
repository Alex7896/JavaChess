package modele.jeu.decorators;

import modele.plateau.Case;
import modele.jeu.Piece;
import modele.plateau.Plateau;

import java.util.ArrayList;

public class DecoratorDiag extends DecoratorCasesAccessibles {
    private int porteeMax;

    public DecoratorDiag(Piece piece, Plateau plateau, DecoratorCasesAccessibles base, int porteeMax) {
        super(piece, plateau, base);
        this.porteeMax = porteeMax;
    }

    @Override
    public ArrayList<Case> getMesCA() {
        ArrayList<Case> casesAccessibles = new ArrayList<>();
        Case positionActuelle = piece.getCase();

        int[] directions = {-1, 1};

        for (int dx : directions) {
            for (int dy : directions) {
                Case temp = positionActuelle;
                int portee = 0;

                while (portee < porteeMax) {
                    temp = plateau.getCaseRelative(temp, dx, dy);
                    if (temp == null) break;

                    if (temp.getPiece() == null) {
                        casesAccessibles.add(temp);
                    } else {
                        if (temp.getPiece().getCouleur() != piece.getCouleur()) {
                            casesAccessibles.add(temp);
                        }
                        break;
                    }

                    portee++;
                }
            }
        }

        return casesAccessibles;
    }
}
