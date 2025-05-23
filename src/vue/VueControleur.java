package vue;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import javax.swing.JOptionPane;


import modele.jeu.*;
import modele.jeu.pieces.*;
import modele.plateau.Case;
import modele.plateau.Plateau;


/** Cette classe a deux fonctions :
 *  (1) Vue : proposer une représentation graphique de l'application (cases graphiques, etc.)
 *  (2) Controleur : écouter les évènements clavier et déclencher le traitement adapté sur le modèle (clic position départ -> position arrivée pièce))
 *
 */
public class VueControleur extends JFrame implements Observer {
    private Plateau plateau; // référence sur une classe de modèle : permet d'accéder aux données du modèle pour le rafraichissement, permet de communiquer les actions clavier (ou souris)
    private Jeu jeu;
    private final int sizeX; // taille de la grille affichée
    private final int sizeY;
    private static final int pxCase = 80; // nombre de pixel par case

    // icones des pieces
    private ImageIcon icoRoiB;
    private ImageIcon icoRoiN;
    private ImageIcon icoDameB;
    private ImageIcon icoDameN;
    private ImageIcon icoFouB;
    private ImageIcon icoFouN;
    private ImageIcon icoCavalierB;
    private ImageIcon icoCavalierN;
    private ImageIcon icoTourB;
    private ImageIcon icoTourN;
    private ImageIcon icoPionB;
    private ImageIcon icoPionN;

    private Case caseClic1; // mémorisation des cases cliquées
    private Case caseClic2;

    private JLabel[][] tabJLabel; // cases graphique (au moment du rafraichissement, chaque case va être associée à une icône, suivant ce qui est présent dans le modèle)

    public VueControleur(Jeu _jeu) {
        jeu = _jeu;
        plateau = jeu.getPlateau();
        sizeX = Plateau.SIZE_X;
        sizeY = Plateau.SIZE_Y;

        chargerLesIcones();
        placerLesComposantsGraphiques();

        plateau.addObserver(this);

        mettreAJourAffichage();
    }

    private void chargerLesIcones() {
        icoRoiB = chargerIcone("assets/images/roiBlanc.png");
        icoRoiN = chargerIcone("assets/images/roiNoir.png");
        icoDameB = chargerIcone("assets/images/dameBlanc.png");
        icoDameN = chargerIcone("assets/images/dameNoir.png");
        icoFouB = chargerIcone("assets/images/fouBlanc.png");
        icoFouN = chargerIcone("assets/images/fouNoir.png");
        icoCavalierB = chargerIcone("assets/images/cavalierBlanc.png");
        icoCavalierN = chargerIcone("assets/images/cavalierNoir.png");
        icoTourB = chargerIcone("assets/images/tourBlanc.png");
        icoTourN = chargerIcone("assets/images/tourNoir.png");
        icoPionB = chargerIcone("assets/images/pionBlanc.png");
        icoPionN = chargerIcone("assets/images/pionNoir.png");
    }

    private ImageIcon chargerIcone(String urlIcone) {
        BufferedImage image = null;

        ImageIcon icon = new ImageIcon(urlIcone);

        // Redimensionner l'icône
        Image img = icon.getImage().getScaledInstance(pxCase, pxCase, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(img);

        return resizedIcon;
    }

    private void placerLesComposantsGraphiques() {
        setTitle("Jeu d'Échecs");
        setIconImage(new ImageIcon("assets/images/icone.png").getImage());
        setResizable(false);
        setSize(sizeX * pxCase, sizeX * pxCase);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer l'application à la fermeture de la fenêtre

        JComponent grilleJLabels = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabels va contenir les cases graphiques et les positionner sous la forme d'une grille

        tabJLabel = new JLabel[sizeX][sizeY]; // tableau des icons des pieces sur l'échiquier

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel();

                tabJLabel[x][y] = jlab; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )

                final int xx = x; // permet de compiler la classe anonyme ci-dessous
                final int yy = y;

                // écouteur de clics
                jlab.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println(xx+" "+yy);

                        if (caseClic1 == null) {
                            caseClic1 = plateau.getCases()[xx][yy];
                            Piece pieceClique = caseClic1.getPiece();
                            if (pieceClique != null) {
                                ArrayList<Case> test = pieceClique.dCA.getCA();
                                System.out.println(test);
                            } else {
                                caseClic1 = null;
                            }
                            // TODO Faire en sorte que lorsque la case n'a pas de piece, on ne retient pas la case cliqué.
                        } else {
                            caseClic2 = plateau.getCases()[xx][yy];
                            jeu.envoyerCoup(new Coup(caseClic1, caseClic2));
                            caseClic1 = null;
                            caseClic2 = null;
                        }

                    }
                });


                jlab.setOpaque(true);

                if ((y%2 == 0 && x%2 == 0) || (y%2 != 0 && x%2 != 0)) {
                    tabJLabel[x][y].setBackground(new Color(235, 236, 208));
                } else {
                    tabJLabel[x][y].setBackground(new Color(115, 149, 82));
                }

                grilleJLabels.add(jlab);
            }
        }
        add(grilleJLabels);
    }


    /**
     * Il y a une grille du côté du modèle ( modele.jeu.getGrille() ) et une grille du côté de la vue (tabJLabel)
     */
    private void mettreAJourAffichage() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Case c = plateau.getCases()[x][y];

                if (c != null) {
                    Piece e = c.getPiece();

                    if (e!= null) {
                        if (e instanceof Roi) {
                            if (e.getCouleur()==Couleur.BLANC) tabJLabel[x][y].setIcon(icoRoiB);
                            else tabJLabel[x][y].setIcon(icoRoiN);
                        } else if(e instanceof Dame) {
                            if(e.getCouleur()==Couleur.BLANC) tabJLabel[x][y].setIcon(icoDameB);
                            else tabJLabel[x][y].setIcon(icoDameN);
                        } else if(e instanceof Tour) {
                            if(e.getCouleur()==Couleur.BLANC) tabJLabel[x][y].setIcon(icoTourB);
                            else tabJLabel[x][y].setIcon(icoTourN);
                        } else if(e instanceof Fou) {
                            if(e.getCouleur()==Couleur.BLANC) tabJLabel[x][y].setIcon(icoFouB);
                            else tabJLabel[x][y].setIcon(icoFouN);
                        } else if(e instanceof Cavalier) {
                            if(e.getCouleur()==Couleur.BLANC) tabJLabel[x][y].setIcon(icoCavalierB);
                            else tabJLabel[x][y].setIcon(icoCavalierN);
                        } else if(e instanceof Pion) {
                            if(e.getCouleur()==Couleur.BLANC) tabJLabel[x][y].setIcon(icoPionB);
                            else tabJLabel[x][y].setIcon(icoPionN);
                        }
                    } else {
                        tabJLabel[x][y].setIcon(null);
                    }
                }
            }
        }
    }

    public void afficherFinPartie() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Échec et Mat !\nLa partie est terminée.",
                    "Fin de la partie",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        Runnable updateTask = () -> {
            mettreAJourAffichage();

            if (jeu != null && jeu.estEnEchecEtMat(jeu.getTourActuel())) {
                afficherFinPartie();
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            updateTask.run();
        } else {
            SwingUtilities.invokeLater(updateTask);
        }
    }
}


