package modele.plateau;

import modele.jeu.*;
import modele.jeu.pieces.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Observable;

public class Plateau extends Observable {
    public static final int SIZE_X = 8;
    public static final int SIZE_Y = 8;
    private Case[][] tab;
    private HashMap<Case, Point> map;
    private Jeu jeu;

    public void setJeu(Jeu jeu) {
        this.jeu = jeu;
    }

    public Jeu getJeu() {
        return jeu;
    }


    public Plateau() {
        initPlateauVide();
        placerPieces();
    }

    private void initPlateauVide() {
        tab = new Case[SIZE_X][SIZE_Y];
        map = new HashMap<>();
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                tab[x][y] = new Case(x, y);
                map.put(tab[x][y], tab[x][y].getPosition());
            }
        }
    }

    private void placerPieces() {
        // Pièces noires
        tab[0][0].setPiece(new Tour(this, Couleur.NOIR));
        tab[0][0].getPiece().setCase(tab[0][0]);

        tab[1][0].setPiece(new Cavalier(this, Couleur.NOIR));
        tab[1][0].getPiece().setCase(tab[1][0]);

        tab[2][0].setPiece(new Fou(this, Couleur.NOIR));
        tab[2][0].getPiece().setCase(tab[2][0]);

        tab[3][0].setPiece(new Dame(this, Couleur.NOIR));
        tab[3][0].getPiece().setCase(tab[3][0]);

        tab[4][0].setPiece(new Roi(this, Couleur.NOIR));
        tab[4][0].getPiece().setCase(tab[4][0]);

        tab[5][0].setPiece(new Fou(this, Couleur.NOIR));
        tab[5][0].getPiece().setCase(tab[5][0]);

        tab[6][0].setPiece(new Cavalier(this, Couleur.NOIR));
        tab[6][0].getPiece().setCase(tab[6][0]);

        tab[7][0].setPiece(new Tour(this, Couleur.NOIR));
        tab[7][0].getPiece().setCase(tab[7][0]);

        for (int i = 0; i < 4; i++) {
            tab[i][1].setPiece(new Pion(this, Couleur.NOIR));
            tab[i][1].getPiece().setCase(tab[i][1]);
        }

        // Pièces blanches
        tab[0][7].setPiece(new Tour(this, Couleur.BLANC));
        tab[0][7].getPiece().setCase(tab[0][7]);

        tab[1][7].setPiece(new Cavalier(this, Couleur.BLANC));
        tab[1][7].getPiece().setCase(tab[1][7]);

        tab[2][7].setPiece(new Fou(this, Couleur.BLANC));
        tab[2][7].getPiece().setCase(tab[2][7]);

        tab[3][7].setPiece(new Dame(this, Couleur.BLANC));
        tab[3][7].getPiece().setCase(tab[3][7]);

        tab[4][7].setPiece(new Roi(this, Couleur.BLANC));
        tab[4][7].getPiece().setCase(tab[4][7]);

        tab[5][7].setPiece(new Fou(this, Couleur.BLANC));
        tab[5][7].getPiece().setCase(tab[5][7]);

        tab[6][7].setPiece(new Cavalier(this, Couleur.BLANC));
        tab[6][7].getPiece().setCase(tab[6][7]);

        tab[7][7].setPiece(new Tour(this, Couleur.BLANC));
        tab[7][7].getPiece().setCase(tab[7][7]);

        for (int i = 4; i < 8; i++) {
            tab[i][6].setPiece(new Pion(this, Couleur.BLANC));
            tab[i][6].getPiece().setCase(tab[i][6]);
        }
    }

    public void notifierChangement() {
        setChanged();
        notifyObservers();
    }

    public Case[][] getCases() {
        return tab;
    }

    // TODO: Faire méthode getCaseRelative(Case source, Direction d) : Case
    public Case getCaseRelative(Case source, int dx, int dy) {
        Point p = new Point(map.get(source)); // copie pour ne pas modifier l'original
        p.x += dx;
        p.y += dy;

        if (p.x < 0 || p.x >= SIZE_X || p.y < 0 || p.y >= SIZE_Y) {
            return null; // en dehors du plateau
        }

        return tab[p.x][p.y];
    }
}
