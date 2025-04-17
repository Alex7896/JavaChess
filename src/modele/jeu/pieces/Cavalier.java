package modele.jeu.pieces;

import modele.jeu.Couleur;
import modele.jeu.Piece;
import modele.jeu.decorators.DecoratorCavalier;
import modele.plateau.Plateau;

public class Cavalier extends Piece {

    public Cavalier(Plateau plateau, Couleur couleur) {
        super(plateau, couleur);
        dCA = new DecoratorCavalier(this, plateau, null);
    }
}
