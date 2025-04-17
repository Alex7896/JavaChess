package modele.jeu.pieces;

import modele.jeu.Couleur;
import modele.jeu.Piece;
import modele.jeu.decorators.*;
import modele.plateau.Plateau;

public class Dame extends Piece {

    public Dame(Plateau plateau, Couleur couleur) {
        super(plateau, couleur);
        // La dame peut aller aussi loin que possible dans toutes les directions.
        dCA = new DecoratorDiag(this, plateau,
                new DecoratorLigne(this, plateau, null, 8), 8);
    }
}

