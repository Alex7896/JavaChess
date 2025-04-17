package modele.jeu;

public enum Couleur {
    BLANC,
    NOIR;

    public int getSens() {
        return this == BLANC ? -1 : 1;
    }
}
