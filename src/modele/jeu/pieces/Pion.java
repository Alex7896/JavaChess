package modele.jeu.pieces;

import modele.jeu.Couleur;
import modele.jeu.Piece;
import modele.jeu.decorators.DecoratorPion;
import modele.plateau.Plateau;

public class Pion extends Piece {
    public Pion(Plateau plateau, Couleur couleur) {
        super(plateau, couleur);
        this.setADejaBouge(false); // Initialisation à false
        dCA = new DecoratorPion(this, plateau, null);
    }
}
