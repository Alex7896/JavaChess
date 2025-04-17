package modele.jeu;

import modele.plateau.Plateau;
import modele.plateau.Case;

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





    private boolean coupValide(Coup c) {
        System.out.println("Validation du coup : " + c.dep + " -> " + c.arr);

        Piece piece = plateau.getCases()[c.dep.x][c.dep.y].getPiece();
        if (piece == null) {
            System.out.println("Aucune pièce à la position de départ.");
            return false;
        }

        System.out.println("Pièce trouvée : " + piece.getClass().getSimpleName() + " (" + piece.getCouleur() + ")");
        System.out.println("aDejaBouge() = " + piece.aDejaBouge());

        Joueur joueurActuel = getJoueurCourant();
        if (piece.getCouleur() != joueurActuel.getCouleur()) {
            System.out.println("Ce n'est pas le tour de cette pièce !");
            return false;
        }

        var accessibles = piece.dCA.getCA();
        System.out.println("Cases accessibles : " + accessibles);

        boolean valide = accessibles.contains(plateau.getCases()[c.arr.x][c.arr.y]);
        System.out.println("Est-ce que la case d'arrivée est dedans ? " + valide);
        return valide;
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
