package modele.jeu;

import modele.jeu.decorators.DecoratorCasesAccessibles;
import modele.plateau.Case;
import modele.plateau.Plateau;

public abstract class Piece implements Cloneable {
    protected Plateau p;
    protected Case c;
    protected Couleur couleur;
    public DecoratorCasesAccessibles dCA; // NOTE: Peut etre mettre en protected et faire un getter
    private boolean dejaBouge = false;


    public Piece(Plateau plateau, Couleur couleur) {
        this.couleur = couleur;
        this.p = plateau;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public void setCase(Case c) {
        this.c = c;
    }

    public boolean aDejaBouge() { return dejaBouge; }

    public void setADejaBouge(boolean aDejaBouge) {
        this.dejaBouge = aDejaBouge;
    }



    public Case getCase() {
        return c;
    }

    @Override
    public Piece clone() {
        try {
            return (Piece) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // ne devrait jamais arriver
        }
    }
}
