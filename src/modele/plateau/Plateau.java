package modele.plateau;

import modele.jeu.*;
import modele.jeu.pieces.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Observable;

public class Plateau extends Observable implements Cloneable {
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
        tab[1][0].setPiece(new Cavalier(this, Couleur.NOIR));
        tab[2][0].setPiece(new Fou(this, Couleur.NOIR));
        tab[3][0].setPiece(new Dame(this, Couleur.NOIR));
        tab[4][0].setPiece(new Roi(this, Couleur.NOIR));
        tab[5][0].setPiece(new Fou(this, Couleur.NOIR));
        tab[6][0].setPiece(new Cavalier(this, Couleur.NOIR));
        tab[7][0].setPiece(new Tour(this, Couleur.NOIR));

        for (int i = 0; i < 8; i++) {
            tab[i][1].setPiece(new Pion(this, Couleur.NOIR));
        }

        // Pièces blanches
        tab[0][7].setPiece(new Tour(this, Couleur.BLANC));
        tab[1][7].setPiece(new Cavalier(this, Couleur.BLANC));
        tab[2][7].setPiece(new Fou(this, Couleur.BLANC));
        tab[3][7].setPiece(new Dame(this, Couleur.BLANC));
        tab[4][7].setPiece(new Roi(this, Couleur.BLANC));
        tab[5][7].setPiece(new Fou(this, Couleur.BLANC));
        tab[6][7].setPiece(new Cavalier(this, Couleur.BLANC));
        tab[7][7].setPiece(new Tour(this, Couleur.BLANC));

        for (int i = 0; i < 8; i++) {
            tab[i][6].setPiece(new Pion(this, Couleur.BLANC));
        }

        // Affecter les cases aux pièces
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                if (tab[x][y].getPiece() != null) {
                    tab[x][y].getPiece().setCase(tab[x][y]);
                }
            }
        }
    }


    public void notifierChangement() {
        setChanged();
        notifyObservers();
    }

    public Case[][] getCases() {
        return tab;
    }

    public Case getCaseRelative(Case source, int dx, int dy) {
        Point p = new Point(map.get(source)); // copie pour ne pas modifier l'original
        p.x += dx;
        p.y += dy;

        if (p.x < 0 || p.x >= SIZE_X || p.y < 0 || p.y >= SIZE_Y) {
            return null; // en dehors du plateau
        }

        return tab[p.x][p.y];
    }

    public Plateau simulerCoup(Coup c) {
        Plateau simulation = new Plateau();
        simulation.initPlateauVide();

        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                Piece piece = this.tab[x][y].getPiece();
                if (piece != null) {
                    Piece copie = piece.clone();
                    Case caseSim = simulation.tab[x][y];
                    caseSim.setPiece(copie);
                    copie.setCase(caseSim);
                }
            }
        }

        Case dep = simulation.tab[c.dep.x][c.dep.y];
        Case arr = simulation.tab[c.arr.x][c.arr.y];
        Piece pieceAMouv = dep.getPiece();
        arr.setPiece(pieceAMouv);
        dep.setPiece(null);
        pieceAMouv.setCase(arr);

        return simulation;
    }
}
