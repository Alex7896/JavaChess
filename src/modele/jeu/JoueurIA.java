package modele.jeu;

import modele.jeu.pieces.Pion;
import modele.jeu.pieces.Roi;
import modele.plateau.Case;
import modele.plateau.Plateau;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class JoueurIA extends Joueur {

    private static final int PROFONDEUR_MAX = 4;

    public JoueurIA(Jeu jeu, Couleur couleur) {
        super(jeu, couleur);
    }

    @Override
    public Coup getCoup() {
        Plateau plateau = jeu.getPlateau();
        Coup meilleurCoup = null;
        int meilleurScore = Integer.MIN_VALUE;

        ArrayList<Coup> coupsPossibles = getCoupsPossibles(plateau, couleur);
        Collections.shuffle(coupsPossibles);
        coupsPossibles.sort(Comparator.comparingInt(c -> -prioriteCoup(plateau, c)));

        for (Coup coup : coupsPossibles) {
            Plateau copie = plateau.simulerCoup(coup);
            int score = minimax(copie, PROFONDEUR_MAX - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            if (score > meilleurScore) {
                meilleurScore = score;
                meilleurCoup = coup;
            }
        }

        return meilleurCoup != null ? meilleurCoup : coupsPossibles.get(0);
    }

    private int minimax(Plateau plateau, int profondeur, int alpha, int beta, boolean maximisant) {
        Couleur couleurCourante = maximisant ? couleur : (couleur == Couleur.BLANC ? Couleur.NOIR : Couleur.BLANC);
        if (profondeur == 0) {
            return evaluerPlateau(plateau, couleur);
        }

        ArrayList<Coup> coups = getCoupsPossibles(plateau, couleurCourante);
        coups.sort(Comparator.comparingInt(c -> -prioriteCoup(plateau, c)));

        if (maximisant) {
            int maxEval = Integer.MIN_VALUE;
            for (Coup coup : coups) {
                Plateau copie = plateau.simulerCoup(coup);
                int eval = minimax(copie, profondeur - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Coup coup : coups) {
                Plateau copie = plateau.simulerCoup(coup);
                int eval = minimax(copie, profondeur - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private ArrayList<Coup> getCoupsPossibles(Plateau plateau, Couleur couleur) {
        ArrayList<Coup> coupsPossibles = new ArrayList<>();
        for (int x = 0; x < Plateau.SIZE_X; x++) {
            for (int y = 0; y < Plateau.SIZE_Y; y++) {
                Case c = plateau.getCases()[x][y];
                if (c.getPiece() != null && c.getPiece().getCouleur() == couleur) {
                    for (Case cible : c.getPiece().dCA.getCA()) {
                        Coup coup = new Coup(c, cible);
                        coupsPossibles.add(coup);
                    }
                }
            }
        }
        return coupsPossibles;
    }

    private int evaluerPlateau(Plateau plateau, Couleur couleurIA) {
        int score = 0;
        int mobilite = 0;
        int structurePion = 0;
        int roiEnDanger = 0;

        Case roiCase = trouverRoi(plateau, couleurIA);
        Case roiOpposeCase = trouverRoi(plateau, couleurIA == Couleur.BLANC ? Couleur.NOIR : Couleur.BLANC);

        for (int x = 0; x < Plateau.SIZE_X; x++) {
            for (int y = 0; y < Plateau.SIZE_Y; y++) {
                Piece p = plateau.getCases()[x][y].getPiece();
                if (p != null) {
                    int valeur = switch (p.getClass().getSimpleName()) {
                        case "Pion" -> 100;
                        case "Cavalier", "Fou" -> 320;
                        case "Tour" -> 500;
                        case "Dame" -> 900;
                        case "Roi" -> 10000;
                        default -> 0;
                    };

                    int facteur = p.getCouleur() == couleurIA ? 1 : -1;
                    score += facteur * valeur;

                    if (p.dCA != null) {
                        mobilite += facteur * p.dCA.getCA().size();
                    }

                    if (p instanceof Pion) {
                        int avancee = (couleurIA == Couleur.BLANC) ? (7 - y) : y;
                        structurePion += facteur * (avancee * 10);
                    }

                    if (p.dCA != null && p.getCouleur() == couleurIA && p.dCA.getCA().contains(roiOpposeCase)) {
                        score += 500;
                    }
                }
            }
        }

        if (roiCase != null) {
            int protection = 0;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    Case voisine = plateau.getCaseRelative(roiCase, dx, dy);
                    if (voisine != null && voisine.getPiece() != null && voisine.getPiece().getCouleur() == couleurIA) {
                        protection++;
                    }
                }
            }
            roiEnDanger -= (8 - protection) * 30;
        }

        return score + mobilite * 5 + structurePion + roiEnDanger;
    }

    private Case trouverRoi(Plateau plateau, Couleur couleur) {
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

    private int prioriteCoup(Plateau plateau, Coup coup) {
        Case cible = plateau.getCases()[coup.arr.x][coup.arr.y];
        if (cible.getPiece() != null) {
            String type = cible.getPiece().getClass().getSimpleName();
            return switch (type) {
                case "Pion" -> 1;
                case "Cavalier", "Fou" -> 3;
                case "Tour" -> 5;
                case "Dame" -> 9;
                default -> 0;
            } * 100;
        }
        return 0;
    }
}