package modele.jeu.pieces;

import modele.jeu.Couleur;
import modele.jeu.Piece;
import modele.jeu.decorators.*;
import modele.plateau.Plateau;

public class Roi extends Piece {

    public Roi(Plateau plateau, Couleur couleur) {
        super(plateau, couleur);
        // Le roi ne se déplace que d'une case dans toutes les directions.
        dCA = new DecoratorRoque(this, plateau,
                new DecoratorDiag(this, plateau,
                        new DecoratorLigne(this, plateau, null, 1), 1));
    }


}
