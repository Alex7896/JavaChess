package modele.jeu;

import modele.plateau.Case;
import modele.plateau.Plateau;

import java.util.ArrayList;

public class JoueurIA extends Joueur {

    public JoueurIA(Jeu jeu, Couleur couleur) {
        super(jeu, couleur);
    }

    @Override
    public Coup getCoup() {
        ArrayList<Coup> coupsPossibles = new ArrayList<>();
        Plateau plateau = jeu.getPlateau();

        for (int x = 0; x < Plateau.SIZE_X; x++) {
            for (int y = 0; y < Plateau.SIZE_Y; y++) {
                Case c = plateau.getCases()[x][y];
                if (c.getPiece() != null && c.getPiece().getCouleur() == couleur) {
                    for (Case cible : c.getPiece().dCA.getCA()) {
                        Coup coup = new Coup(c, cible);
                        if (jeu.coupValide(coup)) {
                            coupsPossibles.add(coup);
                        }
                    }
                }
            }
        }

        Coup meilleurCoup = null;
        int meilleurScore = Integer.MIN_VALUE;

        for (Coup coup : coupsPossibles) {
            Plateau copie = plateau.simulerCoup(coup); // ✅ Simulation simple et propre
            int score = evaluerPlateau(copie, couleur);
            if (score > meilleurScore) {
                meilleurScore = score;
                meilleurCoup = coup;
            }
        }

        return meilleurCoup != null ? meilleurCoup : coupsPossibles.get(0);
    }


    private int evaluerPlateau(Plateau plateau, Couleur couleurIA) {
        int score = 0;
        for (int x = 0; x < Plateau.SIZE_X; x++) {
            for (int y = 0; y < Plateau.SIZE_Y; y++) {
                Piece p = plateau.getCases()[x][y].getPiece();
                if (p != null) {
                    int valeur = switch (p.getClass().getSimpleName()) {
                        case "Pion" -> 1;
                        case "Cavalier", "Fou" -> 3;
                        case "Tour" -> 5;
                        case "Dame" -> 9;
                        default -> 0;
                    };
                    score += (p.getCouleur() == couleurIA) ? valeur : -valeur;
                }
            }
        }
        return score;
    }
}
