package modele.jeu.pieces;

import modele.jeu.Couleur;
import modele.jeu.Piece;
import modele.jeu.decorators.DecoratorLigne;
import modele.plateau.Plateau;

public class Tour extends Piece {
    public Tour(Plateau plateau, Couleur couleur) {
        super(plateau, couleur);
        dCA = new DecoratorLigne(this, plateau, null, 8);
    }
}
