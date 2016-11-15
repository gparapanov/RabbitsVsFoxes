/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rabbitsvsfoxes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Georgi
 */
public class RVFGui extends javax.swing.JFrame {

    private final ImageIcon grassIcon
            = new ImageIcon("images/grass.png", "Grass icon");
    ;
    private JPanel panel1;
    private final int size = 35;
    private List<JLabel> labels;
    private Set<Agent> agents;
    private Set<EnvironmentObject> envObjects;
    private RVFGui env = this;
    private Color backgroundC = Color.decode("#169B08");

    public RVFGui() {
        initComponents();//generated method
        initialiseVariables();
        createEnvironmentObjects(8, 8, 10, 10);
        drawField();

        ActionListener listener = (ActionEvent event) -> {
            step();
        };
        Timer displayTimer = new Timer(500, listener);
        displayTimer.start();

        this.setContentPane(panel1);
    }

    private void initialiseVariables() {
        this.agents = new LinkedHashSet();
        this.envObjects = new LinkedHashSet();
        labels = new ArrayList<>();
        panel1 = new JPanel(new GridLayout(size, size));

    }

    /**
     *
     * @param r number of rabbits
     * @param f number of foxes
     * @param c number of carrots
     * @param b number of bombs
     */
    private void createEnvironmentObjects(int r, int f, int c, int b) {
        int newX, newY;
        for (int i = 0; i < r; i++) {//create rabbits
            do {
                newX = randomRange(0, size - 1);
                newY = randomRange(0, size - 1);
            } while (spaceOccupied(newX, newY));
            RabbitAgent rabbit = new RabbitAgent(newX, newY);
            this.addEnvironmentObject(rabbit);
        }
        for (int i = 0; i < f; i++) {//create foxes
            do {
                newX = randomRange(0, size - 1);
                newY = randomRange(0, size - 1);
            } while (spaceOccupied(newX, newY));
            FoxAgent fox = new FoxAgent(newX, newY);
            this.addEnvironmentObject(fox);
        }
        for (int i = 0; i < c; i++) {//create carrots
            do {
                newX = randomRange(0, size - 1);
                newY = randomRange(0, size - 1);
            } while (spaceOccupied(newX, newY));
            this.addEnvironmentObject(new Carrot(newX, newY));
        }
        for (int i = 0; i < b; i++) {//create bombs
            do {
                newX = randomRange(0, size - 1);
                newY = randomRange(0, size - 1);
            } while (spaceOccupied(newX, newY));
            this.addEnvironmentObject(new Bomb(newX, newY));
        }
    }

    private void drawField() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                JLabel l = new JLabel("", JLabel.CENTER);
                l.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                l.setBackground(backgroundC);
                //l.setIcon(grassIcon);
                l.setOpaque(true);
                panel1.add(l);
                labels.add(l);
            }
        }
        for (EnvironmentObject eo : envObjects) {
            if (eo.isAlive()) {
                JLabel curLabel;
                curLabel = this.getLabel(eo.getX(), eo.getY());
                curLabel.setIcon(eo.getIcon());
            }

        }
    }

    private void redrawField() {
        JLabel curLabel;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                curLabel = this.getLabel(j, i);
                curLabel.setBackground(backgroundC);
                curLabel.setIcon(null);
            }
        }
        for (EnvironmentObject eo : envObjects) {
            if (eo.isAlive()) {
                curLabel = this.getLabel(eo.getX(), eo.getY());
                curLabel.setIcon(eo.getIcon());
            }

        }
    }

    public int randomRange(int min, int max) {
        int range = Math.abs(max - min) + 1;
        return (int) (Math.random() * range) + (min <= max ? min : max);
    }

    public boolean spaceOccupied(int x, int y) {

        for (EnvironmentObject eo : envObjects) {
            if (eo.getX() == x && eo.getY() == y) {
                return true;
            }
        }

        return false;
    }

    public void addAgent(Agent a) {
        addEnvironmentObject(a);
    }

    public void removeAgent(Agent a) {
        removeEnvironmentObject(a);
    }

    public void addEnvironmentObject(EnvironmentObject eo) {
        envObjects.add(eo);
        if (eo instanceof Agent) {
            Agent a = (Agent) eo;
            if (!agents.contains(a)) {
                agents.add(a);
            }
        }
    }

    public void removeEnvironmentObject(EnvironmentObject eo) {
        envObjects.remove(eo);
        agents.remove(eo);
    }

    public JLabel getLabel(int x, int y) {
        int index = y * size + x;
        return labels.get(index);
    }

    private void setLabel(int r, int c, JLabel newLabel) {
        int index = r * size + c;
        labels.set(index, newLabel);
    }

    private void step() {

        for (Agent a : agents) {//direction to move
            int x = a.getX();
            int y = a.getY();
            double direction = Math.random();
            if (direction < 0.25) {
                x++;
            } else if (direction < 0.5) {
                y--;
            } else if (direction < 0.75) {
                x--;
            } else {
                y++;
            }
            if (x >= size) {
                x--;
            } else if (x < 0) {
                x++;
            }
            if (y >= size) {
                y--;
            } else if (y < 0) {
                y++;
            }
            if (a instanceof FoxAgent) {
                for (Agent r : agents) {
                    if (r instanceof RabbitAgent && r.getX() == x
                            && r.getY() == y) {
                        r.setAlive(false);
                    }
                }
            }
            if (a instanceof RabbitAgent && spaceOccupied(x, y)) {
                for (EnvironmentObject eo : envObjects) {
                    if (eo instanceof Carrot && eo.getX() == x
                            && eo.getY() == y) {
                        eo.setAlive(false);
                    }
                }
            }
            a.setPosition(x, y);
            redrawField();
        }
        updateStatistics();
    }

    public void updateStatistics() {
        int rabbits = 0, foxes = 0, carrots = 0;
        for (EnvironmentObject eo : envObjects) {
            if (eo.isAlive()) {
                if (eo instanceof RabbitAgent) {
                    rabbits++;
                }
                if (eo instanceof FoxAgent) {
                    foxes++;
                }
                if (eo instanceof Carrot) {
                    carrots++;
                }
            }
        }
        rabbitsNumber.setText(""+rabbits);
        foxesNumber.setText(""+foxes);
        carrotsNumber.setText(""+carrots);
    }

    public void visualise() {

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statisticsDialog = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        rabbitsNumber = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        foxesNumber = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        carrotsNumber = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();

        statisticsDialog.setTitle("Statistics");
        statisticsDialog.setPreferredSize(new java.awt.Dimension(161, 171));
        statisticsDialog.setSize(new java.awt.Dimension(165, 175));

        jLabel1.setText("Objects on the field:");

        jLabel2.setText("Rabbits:");

        rabbitsNumber.setText("0");

        jLabel4.setText("Foxes:");

        foxesNumber.setText("0");

        jLabel6.setText("Carrots:");

        carrotsNumber.setText("0");

        javax.swing.GroupLayout statisticsDialogLayout = new javax.swing.GroupLayout(statisticsDialog.getContentPane());
        statisticsDialog.getContentPane().setLayout(statisticsDialogLayout);
        statisticsDialogLayout.setHorizontalGroup(
            statisticsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statisticsDialogLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(statisticsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(statisticsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(statisticsDialogLayout.createSequentialGroup()
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(carrotsNumber))
                        .addGroup(statisticsDialogLayout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(foxesNumber))
                        .addGroup(statisticsDialogLayout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(30, 30, 30)
                            .addComponent(rabbitsNumber))))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        statisticsDialogLayout.setVerticalGroup(
            statisticsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statisticsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(statisticsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(rabbitsNumber))
                .addGap(18, 18, 18)
                .addGroup(statisticsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(foxesNumber))
                .addGap(18, 18, 18)
                .addGroup(statisticsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(carrotsNumber))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 785, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 493, Short.MAX_VALUE)
        );

        jMenu1.setText("Control");

        jMenuItem1.setText("Start");
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Stop");
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItem3.setText("Show Statistics");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        statisticsDialog.setVisible(true);
        statisticsDialog.setLocationRelativeTo(null);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RVFGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RVFGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RVFGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RVFGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RVFGui().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel carrotsNumber;
    private javax.swing.JLabel foxesNumber;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel rabbitsNumber;
    private javax.swing.JDialog statisticsDialog;
    // End of variables declaration//GEN-END:variables
}
