package modele.jeu;

public class Joueur {
    public final Jeu jeu;
    public final Couleur couleur;

    public Joueur(Jeu jeu, Couleur couleur) {
        this.jeu = jeu;
        this.couleur = couleur;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public Coup getCoup() throws InterruptedException {
        synchronized (jeu) {
            jeu.wait();
        }
        return jeu.coup;
    }
}
