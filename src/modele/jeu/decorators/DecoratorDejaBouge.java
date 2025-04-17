package modele.jeu.decorators;

import modele.jeu.Piece;
import modele.plateau.Case;
import modele.plateau.Plateau;

import java.util.ArrayList;

public class DecoratorDejaBouge extends DecoratorCasesAccessibles {

    public DecoratorDejaBouge(Piece piece, Plateau plateau, DecoratorCasesAccessibles base) {
        super(piece, plateau, base);
    }

    @Override
    public ArrayList<Case> getMesCA() {
        // Log optionnel pour débogage :
        System.out.println("DecoratorDejaBouge -> aDejaBouge() = " + piece.aDejaBouge());

        // Retourne les cases accessibles du décorateur sous-jacent
        return base != null ? base.getMesCA() : new ArrayList<>();
    }
}


