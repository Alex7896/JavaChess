package modele.jeu.decorators;

import modele.jeu.Coup;
import modele.jeu.Piece;
import modele.jeu.pieces.Pion;
import modele.plateau.Case;
import modele.plateau.Plateau;

import java.util.ArrayList;

public class DecoratorPion extends DecoratorCasesAccessibles {

    public DecoratorPion(Piece piece, Plateau plateau, DecoratorCasesAccessibles base) {
        super(piece, plateau, base);
    }

    @Override
    public ArrayList<Case> getMesCA() {
        ArrayList<Case> cases = new ArrayList<>();
        Case c = piece.getCase();

        int direction = piece.getCouleur().getSens(); // 1 ou -1 selon la couleur

        // Avancer d'une case
        Case devant = plateau.getCaseRelative(c, 0, direction);
        if (devant != null && devant.getPiece() == null) {
            cases.add(devant);

            // Avancer de deux cases si en position de départ
            System.out.println("aDejaBouge() = " + piece.aDejaBouge());
            if (!piece.aDejaBouge()) {
                Case deuxDevant = plateau.getCaseRelative(devant, 0, direction);
                if (deuxDevant != null && deuxDevant.getPiece() == null) {
                    cases.add(deuxDevant);
                }
            }
        }

        // Captures diagonales (uniquement devant à gauche et à droite)
        for (int dx : new int[]{-1, 1}) {
            // Vérification de la case diagonale avant
            Case diagonale = plateau.getCaseRelative(c, dx, direction);
            if (diagonale != null && diagonale.getPiece() != null &&
                    diagonale.getPiece().getCouleur() != piece.getCouleur()) {
                cases.add(diagonale);
            }
        }

        Coup dernier = plateau.getJeu().getDernierCoup();
        if (dernier != null) {
            Case dep = plateau.getCases()[dernier.dep.x][dernier.dep.y];
            Case arr = plateau.getCases()[dernier.arr.x][dernier.arr.y];
            Piece pieceDernier = arr.getPiece();

            if (pieceDernier instanceof Pion &&
                    pieceDernier.getCouleur() != piece.getCouleur() &&
                    Math.abs(dernier.arr.y - dernier.dep.y) == 2 &&
                    arr.getY() == c.getY()) {

                int dx = arr.getX() - c.getX();
                if (Math.abs(dx) == 1) {
                    Case cible = plateau.getCaseRelative(c, dx, direction);
                    if (cible != null && cible.getPiece() == null) {
                        cases.add(cible);
                    }
                }
            }
        }

        return cases;
    }

}
