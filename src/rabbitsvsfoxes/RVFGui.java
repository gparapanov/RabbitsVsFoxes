package rabbitsvsfoxes;

import rabbitsvsfoxes.Agent.*;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Georgi
 */
public class RVFGui extends javax.swing.JFrame {

    private final int RABBITS = 10;
    private final int FOXES = 5;
    private final int CARROTS = 20;
    private final int BOMBS = 20;
    private final int size = 45;

    private JPanel panel1;

    private List<JLabel> labels;

    private final Color backgroundC = Color.decode("#169B08");
    private Timer displayTimer;
    private ButtonGroup agentBehaviourGroup;
    private Environment environment;

    public RVFGui() {
        initComponents();//generated method
        environment = new Environment(size, RABBITS, FOXES, CARROTS, BOMBS, this);
        initialiseVariables();
        drawField();

        ActionListener listener = (ActionEvent event) -> {
            step();
        };
        displayTimer = new Timer(200, listener);
        //displayTimer.start();

        this.setContentPane(panel1);
    }

    private void initialiseVariables() {
        labels = new ArrayList<>();
        panel1 = new JPanel(new GridLayout(size, size));
        agentBehaviourGroup = new ButtonGroup();
        agentBehaviourGroup.add(this.goalDrivenOption);
        agentBehaviourGroup.add(this.hybridOption);
        //hybridOption.setSelected(true);

        startMenu.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                displayTimer.start();
                System.out.println("Starting game!");
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        stopMenu.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                displayTimer.stop();
                System.out.println("Stopping game!");
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

    }

    private void drawField() {
        for (int i = 0; i < environment.getSize(); i++) {
            for (int j = 0; j < environment.getSize(); j++) {
                JLabel l = new JLabel("", JLabel.CENTER);
                l.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                l.setBackground(backgroundC);
                //l.setIcon(grassIcon);
                l.setOpaque(true);
                panel1.add(l);
                labels.add(l);
            }
        }
        for (EnvironmentObject eo : environment.getEnvObjects()) {
            if (eo.isAlive()) {
                JLabel curLabel;
                curLabel = this.getLabel(eo.getX(), eo.getY());
                curLabel.setIcon(eo.getIcon());
            }

        }
    }

    private void redrawField() {
        JLabel curLabel;
        for (int i = 0; i < environment.getSize(); i++) {
            for (int j = 0; j < environment.getSize(); j++) {
                curLabel = this.getLabel(j, i);
                curLabel.setBackground(backgroundC);
                curLabel.setIcon(null);
            }
        }
        for (EnvironmentObject eo : environment.getEnvObjects()) {
            if (eo.isAlive()) {
                curLabel = this.getLabel(eo.getX(), eo.getY());
                curLabel.setIcon(eo.getIcon());
            }

        }
    }

    public JLabel getLabel(int x, int y) {
        int index = y * size + x;
        return labels.get(index);
    }

    private void setLabel(int r, int c, JLabel newLabel) {
        int index = r * size + c;
        labels.set(index, newLabel);
    }

    public boolean getFoxesTeamwork() {
        return foxesTeamwork.isSelected();
    }

    private void step() {
        environment.step();
        redrawField();
        updateStatistics();
    }

    /**
     *
     * @return 1 for goal-driven behaviour, 2 for hybrid
     */
    public int getBehaviour() {
        if (goalDrivenOption.isSelected()) {
            return 1;
            //System.out.println("goal drive  ");
        } else {
            return 2;
            //System.out.println("hybrid ");
        }
    }

    public boolean getCarrotsRegenCheck() {
        if (carrotsRegenCheck.isSelected()) {
            return true;
        }
        return false;
    }

    public void updateStatistics() {
        int rabbits = 0, foxes = 0, carrotsF = 0;
        Iterator<EnvironmentObject> iter = environment.getEnvObjects().iterator();
        while (iter.hasNext()) {
            EnvironmentObject eo = iter.next();
            if (eo instanceof RabbitAgent) {
                rabbits++;
            }
            if (eo instanceof FoxAgent) {
                foxes++;
            }
            if (eo instanceof Carrot) {
                carrotsF++;
            }
        }
        rabbitsNumber.setText("" + rabbits);
        foxesNumber.setText("" + foxes);
        carrotsNumber.setText("" + carrotsF);
        //if(rabbits==0)displayTimer.stop();
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
        jMenuItem1 = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        startMenu = new javax.swing.JMenu();
        stopMenu = new javax.swing.JMenu();
        agentTypeButton = new javax.swing.JMenu();
        goalDrivenOption = new javax.swing.JRadioButtonMenuItem();
        hybridOption = new javax.swing.JRadioButtonMenuItem();
        carrotsRegenCheck = new javax.swing.JCheckBoxMenuItem();
        foxesTeamwork = new javax.swing.JCheckBoxMenuItem();
        infoMenuButton = new javax.swing.JMenu();
        showStatsButton = new javax.swing.JMenuItem();

        statisticsDialog.setTitle("Statistics");
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

        jMenuItem1.setText("jMenuItem1");

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

        startMenu.setText("Start");
        jMenuBar1.add(startMenu);

        stopMenu.setText("Stop");
        jMenuBar1.add(stopMenu);

        agentTypeButton.setText("Options");

        goalDrivenOption.setSelected(true);
        goalDrivenOption.setText("Purely Goal-Driven");
        goalDrivenOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goalDrivenOptionActionPerformed(evt);
            }
        });
        agentTypeButton.add(goalDrivenOption);

        hybridOption.setText("Hybrid");
        hybridOption.setToolTipText("");
        agentTypeButton.add(hybridOption);
        agentTypeButton.add(new JSeparator(SwingConstants.HORIZONTAL));

        carrotsRegenCheck.setText("Carrots Regeneration");
        agentTypeButton.add(carrotsRegenCheck);
        agentTypeButton.add(new JSeparator(SwingConstants.HORIZONTAL));

        foxesTeamwork.setText("Foxes Teamwork");
        agentTypeButton.add(foxesTeamwork);

        jMenuBar1.add(agentTypeButton);

        infoMenuButton.setText("Info");
        infoMenuButton.setToolTipText("");

        showStatsButton.setText("Show Statistics");
        showStatsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showStatsButtonActionPerformed(evt);
            }
        });
        infoMenuButton.add(showStatsButton);

        jMenuBar1.add(infoMenuButton);

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

    private void showStatsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showStatsButtonActionPerformed
        statisticsDialog.setVisible(true);
        statisticsDialog.setLocationRelativeTo(null);
    }//GEN-LAST:event_showStatsButtonActionPerformed

    private void goalDrivenOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goalDrivenOptionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_goalDrivenOptionActionPerformed

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
    private javax.swing.JMenu agentTypeButton;
    private javax.swing.JLabel carrotsNumber;
    private javax.swing.JCheckBoxMenuItem carrotsRegenCheck;
    private javax.swing.JLabel foxesNumber;
    private javax.swing.JCheckBoxMenuItem foxesTeamwork;
    private javax.swing.JRadioButtonMenuItem goalDrivenOption;
    private javax.swing.JRadioButtonMenuItem hybridOption;
    private javax.swing.JMenu infoMenuButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel rabbitsNumber;
    private javax.swing.JMenuItem showStatsButton;
    private javax.swing.JMenu startMenu;
    private javax.swing.JDialog statisticsDialog;
    private javax.swing.JMenu stopMenu;
    // End of variables declaration//GEN-END:variables
}
