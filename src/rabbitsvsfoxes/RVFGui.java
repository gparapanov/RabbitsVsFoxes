package rabbitsvsfoxes;

import rabbitsvsfoxes.Objects.EnvironmentObject;
import rabbitsvsfoxes.Objects.Carrot;
import rabbitsvsfoxes.Agent.*;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Georgi
 */
public class RVFGui extends javax.swing.JFrame {

    private final int RABBITS = 5;
    private final int FOXES = 1;
    private final int CARROTS = 20;
    private final int BOMBS = 20;
    private final int size = 45;

    private JPanel panel1;

    private List<JLabel> labels;

    private final Color backgroundC = Color.decode("#169B08");
    private Timer displayTimer;
    private ButtonGroup agentBehaviourGroup;
    private ButtonGroup speedButtonGroup;
    private Environment environment;

    public RVFGui() {
        initComponents();//generated method
        environment = new Environment(size, RABBITS, FOXES, CARROTS, BOMBS, this);
        initialiseVariables();
        drawField();

        ActionListener listener = (ActionEvent event) -> {
            step();
        };
        displayTimer = new Timer(500, listener);

        //displayTimer.start();
        this.setContentPane(panel1);
    }

    private void initialiseVariables() {
        this.setSize(new java.awt.Dimension(1100, 700));
        labels = new ArrayList<>();
        panel1 = new JPanel(new GridLayout(size, size));
        agentBehaviourGroup = new ButtonGroup();
        agentBehaviourGroup.add(this.goalDrivenOption);
        agentBehaviourGroup.add(this.hybridOption);

        speedButtonGroup = new ButtonGroup();
        speedButtonGroup.add(fastRB);
        speedButtonGroup.add(veryFastRB);
        speedButtonGroup.add(normalRB);
        speedButtonGroup.add(slowRB);

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
                curLabel.setToolTipText(null);
            }
        }
        for (EnvironmentObject eo : environment.getEnvObjects()) {
            if (eo.isAlive()) {
                curLabel = this.getLabel(eo.getX(), eo.getY());
                //curLabel.setIcon(eo.getIcon());
                
                Image image = eo.getIcon().getImage(); // transform it 
                Image newimg = image.getScaledInstance(curLabel.getSize().width, curLabel.getSize().height, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
                curLabel.setIcon(new ImageIcon(newimg));  // transform it back
                
                //show information about objects on hover
                curLabel.setToolTipText("<html>" + eo.toString() + "</html>");
                
                if(eo instanceof FoxAgent || eo instanceof RabbitAgent){
                    curLabel.setBackground(((Agent)eo).getMyColor());
                    if(((Agent)eo).getTeamColor()!=null){
                        curLabel.setBackground(((Agent)eo).getTeamColor());
                    }
                }
            }

        }
    }
    
    public boolean isInBoundaries(int x, int y){
        return (x>=0 && x<size) && (y>=0 && y<size);
    }
    
    public JLabel getLabel(int x, int y) {
        int index = y * size + x;
        return labels.get(index);
    }

    private void setLabel(int r, int c, JLabel newLabel) {
        int index = r * size + c;
        labels.set(index, newLabel);
    }

    public boolean getFoxesTeamwork1() {
        return foxesTeamwork1.isSelected();
    }

    public boolean getFoxesTeamwork2() {
        return foxesTeamwork2.isSelected();
    }

    public boolean getFoxesTeamwork3() {
        return foxesTeamwork3.isSelected();
    }
    
    public boolean getRabbitsTeamwork1() {
        return rabbitsTeamwork1.isSelected();
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
        menuBar = new javax.swing.JMenuBar();
        startMenu = new javax.swing.JMenu();
        stopMenu = new javax.swing.JMenu();
        optionsButton = new javax.swing.JMenu();
        goalDrivenOption = new javax.swing.JRadioButtonMenuItem();
        hybridOption = new javax.swing.JRadioButtonMenuItem();
        carrotsRegenCheck = new javax.swing.JCheckBoxMenuItem();
        foxesTeamwork1 = new javax.swing.JCheckBoxMenuItem();
        foxesTeamwork2 = new javax.swing.JCheckBoxMenuItem();
        foxesTeamwork3 = new javax.swing.JCheckBoxMenuItem();
        rabbitsTeamwork1 = new javax.swing.JCheckBoxMenuItem();
        speedMenu = new javax.swing.JMenu();
        veryFastRB = new javax.swing.JRadioButtonMenuItem();
        fastRB = new javax.swing.JRadioButtonMenuItem();
        normalRB = new javax.swing.JRadioButtonMenuItem();
        slowRB = new javax.swing.JRadioButtonMenuItem();
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
            .addGap(0, 498, Short.MAX_VALUE)
        );

        startMenu.setText("Start");
        startMenu.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        menuBar.add(startMenu);

        stopMenu.setText("Stop");
        stopMenu.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        menuBar.add(stopMenu);

        optionsButton.setText("Options");
        optionsButton.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N

        goalDrivenOption.setSelected(true);
        goalDrivenOption.setText("Purely Goal-Driven");
        goalDrivenOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goalDrivenOptionActionPerformed(evt);
            }
        });
        optionsButton.add(goalDrivenOption);

        hybridOption.setText("Hybrid");
        hybridOption.setToolTipText("");
        hybridOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hybridOptionActionPerformed(evt);
            }
        });
        optionsButton.add(hybridOption);
        optionsButton.add(new JSeparator(SwingConstants.HORIZONTAL));

        carrotsRegenCheck.setText("Carrots Regeneration");
        optionsButton.add(carrotsRegenCheck);
        optionsButton.add(new JSeparator(SwingConstants.HORIZONTAL));

        foxesTeamwork1.setText("Foxes - All for one");
        foxesTeamwork1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                foxesTeamwork1ActionPerformed(evt);
            }
        });
        optionsButton.add(foxesTeamwork1);

        foxesTeamwork2.setText("Foxes - Ambush");
        foxesTeamwork2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                foxesTeamwork2ActionPerformed(evt);
            }
        });
        optionsButton.add(foxesTeamwork2);

        foxesTeamwork3.setText("Foxes - Group work");
        foxesTeamwork3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                foxesTeamwork3ActionPerformed(evt);
            }
        });
        optionsButton.add(foxesTeamwork3);
        optionsButton.add(new JSeparator(SwingConstants.HORIZONTAL));

        rabbitsTeamwork1.setText("Rabbits - Distraction");
        optionsButton.add(rabbitsTeamwork1);

        menuBar.add(optionsButton);

        speedMenu.setText("Speed");
        speedMenu.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N

        veryFastRB.setText("Very Fast");
        veryFastRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                veryFastRBActionPerformed(evt);
            }
        });
        speedMenu.add(veryFastRB);

        fastRB.setText("Fast");
        fastRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fastRBActionPerformed(evt);
            }
        });
        speedMenu.add(fastRB);

        normalRB.setSelected(true);
        normalRB.setText("Normal");
        normalRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normalRBActionPerformed(evt);
            }
        });
        speedMenu.add(normalRB);

        slowRB.setText("Slow");
        slowRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                slowRBActionPerformed(evt);
            }
        });
        speedMenu.add(slowRB);

        menuBar.add(speedMenu);

        infoMenuButton.setText("Info");
        infoMenuButton.setToolTipText("");
        infoMenuButton.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N

        showStatsButton.setText("Show Statistics");
        showStatsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showStatsButtonActionPerformed(evt);
            }
        });
        infoMenuButton.add(showStatsButton);

        menuBar.add(infoMenuButton);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void hybridOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hybridOptionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hybridOptionActionPerformed

    private void veryFastRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_veryFastRBActionPerformed
        displayTimer.setDelay(100);
    }//GEN-LAST:event_veryFastRBActionPerformed

    private void fastRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fastRBActionPerformed
        displayTimer.setDelay(300);
    }//GEN-LAST:event_fastRBActionPerformed

    private void normalRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_normalRBActionPerformed
        displayTimer.setDelay(500);
    }//GEN-LAST:event_normalRBActionPerformed

    private void slowRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_slowRBActionPerformed
        displayTimer.setDelay(700);
    }//GEN-LAST:event_slowRBActionPerformed

    private void foxesTeamwork1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_foxesTeamwork1ActionPerformed
        if (foxesTeamwork1.isSelected()) {
            foxesTeamwork2.setSelected(false);
            foxesTeamwork3.setSelected(false);
        }
    }//GEN-LAST:event_foxesTeamwork1ActionPerformed

    private void foxesTeamwork2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_foxesTeamwork2ActionPerformed
        if (foxesTeamwork2.isSelected()) {
            foxesTeamwork1.setSelected(false);
            foxesTeamwork3.setSelected(false);
        }
    }//GEN-LAST:event_foxesTeamwork2ActionPerformed

    private void foxesTeamwork3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_foxesTeamwork3ActionPerformed
        if (foxesTeamwork3.isSelected()) {
            foxesTeamwork1.setSelected(false);
            foxesTeamwork2.setSelected(false);
        }
    }//GEN-LAST:event_foxesTeamwork3ActionPerformed

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
    private javax.swing.JCheckBoxMenuItem carrotsRegenCheck;
    private javax.swing.JRadioButtonMenuItem fastRB;
    private javax.swing.JLabel foxesNumber;
    private javax.swing.JCheckBoxMenuItem foxesTeamwork1;
    private javax.swing.JCheckBoxMenuItem foxesTeamwork2;
    private javax.swing.JCheckBoxMenuItem foxesTeamwork3;
    private javax.swing.JRadioButtonMenuItem goalDrivenOption;
    private javax.swing.JRadioButtonMenuItem hybridOption;
    private javax.swing.JMenu infoMenuButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JRadioButtonMenuItem normalRB;
    private javax.swing.JMenu optionsButton;
    private javax.swing.JLabel rabbitsNumber;
    private javax.swing.JCheckBoxMenuItem rabbitsTeamwork1;
    private javax.swing.JMenuItem showStatsButton;
    private javax.swing.JRadioButtonMenuItem slowRB;
    private javax.swing.JMenu speedMenu;
    private javax.swing.JMenu startMenu;
    private javax.swing.JDialog statisticsDialog;
    private javax.swing.JMenu stopMenu;
    private javax.swing.JRadioButtonMenuItem veryFastRB;
    // End of variables declaration//GEN-END:variables
}
