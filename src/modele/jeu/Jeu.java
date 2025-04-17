package modele.jeu;

import modele.jeu.pieces.Roi;
import modele.plateau.Plateau;
import modele.plateau.Case;

import java.awt.*;
import java.util.ArrayList;

public class Jeu extends Thread {
    private Plateau plateau;
    private Joueur joueurB;
    private Joueur joueurN;
    public Coup coup;

    private Couleur tourActuel;

    public Jeu() {
        this.plateau = new Plateau();
        this.joueurB = new Joueur(this, Couleur.BLANC);
        this.joueurN = new Joueur(this, Couleur.NOIR);
        this.tourActuel = Couleur.BLANC; // Le tour commence avec les Blancs
    }

    @Override
    public void run() {
        try {
            jouerPartie();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void jouerPartie() throws InterruptedException {
        while (!partieTermine()) {
            Joueur j = getJoueurCourant();
            Coup c = j.getCoup();
            while (!coupValide(c)) {
                System.out.println("coup non valide");
                c = j.getCoup();
            }
            appliquerCoup(c);
            changerTour();

            // Vérifier si c'est un échec et mat
            if (estEnEchecEtMat(getJoueurCourant().getCouleur())) {
                System.out.println("Échec et Mat !");
                break; // Fin de la partie
            }
        }

        // TODO: Logique de fin de partie
    }

    private void appliquerCoup(Coup c) {
        Case dep = plateau.getCases()[c.dep.x][c.dep.y];
        Case arr = plateau.getCases()[c.arr.x][c.arr.y];

        Piece piece = dep.getPiece();

        // ⚠️ On NE change pas encore aDejaBouge ici
        arr.setPiece(piece);
        dep.setPiece(null);

        piece.setCase(arr); // ça ne change plus aDejaBouge
        piece.setADejaBouge(true); // ✅ et là, maintenant c’est safe

        System.out.println("coup applique");

        plateau.notifierChangement();
    }

    public boolean estEnEchec(Couleur couleur) {
        Case caseRoi = trouverRoi(couleur);
        if (caseRoi == null) return false; // Par sécurité

        for (int x = 0; x < Plateau.SIZE_X; x++) {
            for (int y = 0; y < Plateau.SIZE_Y; y++) {
                Piece p = plateau.getCases()[x][y].getPiece();
                if (p != null && p.getCouleur() != couleur) {
                    ArrayList<Case> ca = p.dCA.getCA();
                    if (ca.contains(caseRoi)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean estEnEchecEtMat(Couleur couleur) {
        // Si le roi n'est pas en échec, il ne peut pas être en mat
        if (!estEnEchec(couleur)) {
            return false;
        }

        // Parcourir toutes les pièces du joueur dont c'est le tour
        for (int x = 0; x < Plateau.SIZE_X; x++) {
            for (int y = 0; y < Plateau.SIZE_Y; y++) {
                Case caseActuelle = plateau.getCases()[x][y];
                Piece piece = caseActuelle.getPiece();

                // Si la pièce appartient au joueur courant
                if (piece != null && piece.getCouleur() == couleur) {
                    // Vérifier tous les coups accessibles pour cette pièce
                    ArrayList<Case> casesAccessibles = piece.dCA.getCA();
                    for (Case caseAccessible : casesAccessibles) {
                        Coup coupTemp = new Coup(caseActuelle, caseAccessible);

                        // Simuler ce coup
                        if (coupValide(coupTemp)) {
                            // Si un coup valide permet de sortir de l'échec, ce n'est pas mat
                            return false;
                        }
                    }
                }
            }
        }

        // Si aucun coup ne permet de sortir de l'échec, c'est un mat
        return true;
    }




    private Case trouverRoi(Couleur couleur) {
        for (int x = 0; x < Plateau.SIZE_X; x++) {
            for (int y = 0; y < Plateau.SIZE_Y; y++) {
                Piece p = plateau.getCases()[x][y].getPiece();
                if (p instanceof Roi && p.getCouleur() == couleur) {
                    return plateau.getCases()[x][y];
                }
            }
        }
        return null;
    }



    private boolean coupValide(Coup c) {
        System.out.println("Validation du coup : " + c.dep + " -> " + c.arr);

        Case caseDep = plateau.getCases()[c.dep.x][c.dep.y];
        Case caseArr = plateau.getCases()[c.arr.x][c.arr.y];
        Piece piece = caseDep.getPiece();

        if (piece == null) {
            System.out.println("Aucune pièce à la position de départ.");
            return false;
        }

        if (piece.getCouleur() != getJoueurCourant().getCouleur()) {
            System.out.println("Ce n'est pas le tour de cette pièce !");
            return false;
        }

        if (!piece.dCA.getCA().contains(caseArr)) {
            System.out.println("La case d'arrivée n'est pas accessible.");
            return false;
        }

        // 🔁 SAUVEGARDE pour simulation
        Piece pieceCapturee = caseArr.getPiece();
        Case ancienneCase = piece.getCase();

        // 🔁 SIMULATION
        caseDep.setPiece(null);
        caseArr.setPiece(piece);
        piece.setCase(caseArr);

        boolean roiEnEchec = estEnEchec(piece.getCouleur());

        // 🔁 RESTAURATION
        piece.setCase(ancienneCase);
        caseDep.setPiece(piece);
        caseArr.setPiece(pieceCapturee);

        if (roiEnEchec) {
            System.out.println("Ce coup mettrait votre roi en échec !");
            return false;
        }

        return true;
    }


    private boolean partieTermine() {
        return false;
        // TODO: Ajouter la logique de fin de partie
    }

    public void envoyerCoup(Coup c) {
        coup = c;
        synchronized (this) {
            notify();
        }

        // TODO A finir
    }

    // Méthode pour alterner les tours entre les joueurs
    private void changerTour() {
        // Alterne entre les couleurs de joueur
        tourActuel = (tourActuel == Couleur.BLANC) ? Couleur.NOIR : Couleur.BLANC;
    }

    // Renvoie le joueur dont c'est le tour
    private Joueur getJoueurCourant() {
        if (tourActuel == Couleur.BLANC) {
            return joueurB;
        } else {
            return joueurN;
        }
    }

    public Plateau getPlateau() {
        return plateau;
    }


    // TODO A SUPPRIMER
    public void demandeDeplacementPiece(Case depart, Case arrivee) {
        // Logique de déplacement de la pièce
        Piece piece = depart.getPiece();
        if (piece != null) {
            arrivee.setPiece(piece);
            depart.setPiece(null);
            plateau.notifyObservers();
        }
    }
}
