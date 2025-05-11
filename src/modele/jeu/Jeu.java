package modele.jeu;

import modele.jeu.pieces.Dame;
import modele.jeu.pieces.Fou;
import modele.jeu.pieces.Pion;
import modele.jeu.pieces.Roi;
import modele.jeu.pieces.*;
import modele.plateau.Plateau;
import modele.plateau.Case;
import javax.swing.JOptionPane;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;


public class Jeu extends Thread {
    private HashMap<String, Integer> historiquePositions = new HashMap<>();
    private List<Coup> historiqueCoup = new ArrayList<>();

    private Plateau plateau;
    private Joueur joueurB;
    private Joueur joueurN;
    public Coup coup;

    private Couleur tourActuel;
    private Coup dernierCoup; // Pour la prise en passant

    public Jeu() {
        this.plateau = new Plateau();
        this.plateau.setJeu(this); // 🔁 Lien Plateau → Jeu
        this.joueurB = new Joueur(this, Couleur.BLANC);
        this.joueurN = new JoueurIA(this, Couleur.NOIR); // 💥 IA joue les noirs
        this.tourActuel = Couleur.BLANC;
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
            String hashPosition = genererHashPosition();

            // 🔄 Enregistrement dans la hashmap
            historiquePositions.put(hashPosition, historiquePositions.getOrDefault(hashPosition, 0) + 1);
            if (historiquePositions.get(hashPosition) >= 3) {
                System.out.println("Nulle par répétition de position !");
                sauverEnPGN("partie.pgn");
                break; // Fin de la partie
            }
            changerTour();

            // Vérifier si c'est un échec et mat
            if (estEnEchecEtMat(tourActuel)) {
                System.out.println("Échec et Mat !");
                sauverEnPGN("partie.pgn");
                break; // Fin de la partie

            }

        }



        // TODO: Logique de fin de partie
    }


    private String genererHashPosition() {
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < Plateau.SIZE_Y; y++) {
            for (int x = 0; x < Plateau.SIZE_X; x++) {
                Piece piece = plateau.getCases()[x][y].getPiece();
                if (piece == null) {
                    sb.append(".");
                } else {
                    String abbrev = piece.getClass().getSimpleName().substring(0, 1);
                    sb.append(piece.getCouleur() == Couleur.BLANC ? abbrev.toUpperCase() : abbrev.toLowerCase());
                }
            }
        }

        sb.append(tourActuel); // Pour distinguer selon le joueur qui doit jouer

        return sb.toString();
    }


    private void appliquerCoup(Coup c) {
        Case dep = plateau.getCases()[c.dep.x][c.dep.y];
        Case arr = plateau.getCases()[c.arr.x][c.arr.y];

        Piece piece = dep.getPiece();

        // Gestion spéciale du roque
        if (piece instanceof Roi && Math.abs(c.arr.x - c.dep.x) == 2) {
            effectuerRoque(dep, arr);
            piece.setADejaBouge(true);
            this.dernierCoup = c;
            plateau.notifierChangement();
            return;
        }

        // 🔥 Prise en passant
        if (piece instanceof Pion &&
                Math.abs(c.arr.x - c.dep.x) == 1 &&
                plateau.getCases()[c.arr.x][c.arr.y].getPiece() == null) {

            int direction = (piece.getCouleur() == Couleur.BLANC) ? 1 : -1;
            Case prise = plateau.getCases()[c.arr.x][c.arr.y + direction];

            if (prise != null && prise.getPiece() instanceof Pion) {
                prise.setPiece(null);
                System.out.println("Prise en passant effectuée !");
            }
        }

        arr.setPiece(piece);
        dep.setPiece(null);

        piece.setCase(arr);
        piece.setADejaBouge(true);

        if (piece instanceof Pion) {
            int ligneArrivee = c.arr.y;
            boolean estEnDerniereLigne = (piece.getCouleur() == Couleur.BLANC && ligneArrivee == 0)
                    || (piece.getCouleur() == Couleur.NOIR && ligneArrivee == 7);

            if (estEnDerniereLigne) {
                demanderPromotion(piece, arr);
            }
        }

        this.dernierCoup = c; // 💾 Sauvegarde du dernier coup
        historiqueCoup.add(c);
        System.out.println("coup applique");

        plateau.notifierChangement();

    }

    private void effectuerRoque(Case depRoi, Case arrRoi) {
        int direction = arrRoi.getX() > depRoi.getX() ? 1 : -1; // 1 pour petit roque, -1 pour grand roque
        int y = depRoi.getY();

        // Déplacer le roi
        arrRoi.setPiece(depRoi.getPiece());
        depRoi.setPiece(null);
        arrRoi.getPiece().setCase(arrRoi);

        // Trouver et déplacer la tour
        int tourX = (direction > 0) ? 7 : 0; // 7 pour petit roque (colonne h), 0 pour grand roque (colonne a)
        Case caseTour = plateau.getCases()[tourX][y];
        Piece tour = caseTour.getPiece();

        // Case destination tour (à côté du roi)
        int newTourX = arrRoi.getX() - direction;
        Case caseDestinationTour = plateau.getCases()[newTourX][y];

        caseDestinationTour.setPiece(tour);
        caseTour.setPiece(null);
        if (tour != null) {
            tour.setCase(caseDestinationTour);
            tour.setADejaBouge(true);
        }
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

    public Couleur getTourActuel() {
        return tourActuel;
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



    public boolean coupValide(Coup c) {

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
        return (tourActuel == Couleur.BLANC) ? joueurB : joueurN;
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public Coup getDernierCoup() {
        return dernierCoup;
    }

    private void demanderPromotion(Piece pion, Case c) {
        String[] options = {"Dame", "Fou", "Tour", "Cavalier"};
        String choix = (String) javax.swing.JOptionPane.showInputDialog(
                null,
                "Choisissez une pièce pour la promotion :",
                "Promotion du pion",
                javax.swing.JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                "Dame"
        );

        if (choix != null) {
            Piece nouvellePiece = null;
            Couleur couleur = pion.getCouleur();

            switch (choix) {
                case "Dame":
                    nouvellePiece = new Dame(plateau, couleur); break;
                case "Fou":
                    nouvellePiece = new Fou(plateau, couleur); break;
                case "Tour":
                    nouvellePiece = new Tour(plateau, couleur); break;
                case "Cavalier":
                    nouvellePiece = new Cavalier(plateau, couleur); break;
            }

            if (nouvellePiece != null) {
                nouvellePiece.setCase(c);
                c.setPiece(nouvellePiece);
            }
        }
    }

        public void sauverEnPGN(String chemin) {
            try (PrintWriter writer = new PrintWriter(chemin)) {
                int num = 1;
                for (int i = 0; i < historiqueCoup.size(); i++) {
                    if (i % 2 == 0) writer.print(num++ + ". ");
                    writer.print(formatCoup(historiqueCoup.get(i)) + " ");
                }
                System.out.println("Partie enregistrée au format PGN : " + chemin);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    private String formatCoup(Coup coup) {
        String nom;
        String symbole;

        if (coup == null) return "";

        // On utilise directement les coordonnées et non pas la pièce sur l'échiquier actuel
        int dx = coup.dep.x;
        int dy = coup.dep.y;
        int ax = coup.arr.x;
        int ay = coup.arr.y;

        // On tente d'inférer la pièce depuis le mouvement (approximatif)
        Piece piece = coup.piece != null ? coup.piece : plateau.getCases()[dx][dy].getPiece();
        if (piece == null) return "";

        nom = piece.getClass().getSimpleName();
        symbole = switch (nom) {
            case "Dame" -> "Q";
            case "Tour" -> "R";
            case "Fou" -> "B";
            case "Cavalier" -> "N";
            case "Roi" -> "K";
            default -> "";
        };

        // Roque
        if (piece instanceof Roi && Math.abs(ax - dx) == 2) {
            return (ax > dx) ? "O-O" : "O-O-O";
        }

        char colonneArr = (char) ('a' + ax);
        int ligneArr = 8 - ay;
        char colonneDep = (char) ('a' + dx);

        boolean estPrise = coup.prise != null;

        String prise = estPrise ? "x" : "";

        if (nom.equals("Pion") && estPrise) {
            return "" + colonneDep + "x" + colonneArr + ligneArr;
        }

        if (nom.equals("Pion")) {
            return "" + colonneArr + ligneArr;
        }

        return symbole + prise + colonneArr + ligneArr;
    }




}
