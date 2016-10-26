package de.htwg.tictactoe.view.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.google.inject.Inject;

import de.htwg.tictactoe.controller.IController;
import de.htwg.tictactoe.model.impl.Messages;
import javafx.application.Application;

/**
 * @author Youssef Idelhoussain
 */
public class TicTacToeGUI extends JFrame implements ActionListener {


   private Container pane;
   private static final int DEFAULT_Y = 300;
   private static final int DEFAULT_X = 300;
   private static final long serialVersionUID = 1L;
   private JPanel loginPanel;
   JButton start;
   JTextField player1;
   JTextField player2;
   JLabel info;
   IController controller;
   JLabel user1;
   JLabel user2;

   @Inject
   public TicTacToeGUI(IController controller) {
      pane = getContentPane();
      this.controller = controller;
      setTitle(Messages.TITLE);
      setLocationRelativeTo(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setMinimumSize(new Dimension(DEFAULT_X, DEFAULT_Y));
      constructTicTacToePane(controller);
   }

   private void constructTicTacToePane(IController controller2) {
      loginPanel = setLoginPanel(controller2);
      pane.add(loginPanel, BorderLayout.CENTER);
      setVisible(true);
   }

   public JPanel setLoginPanel(IController controller2) {
      start = new JButton("start");
      player1 = new JTextField();
      user1 = new JLabel("First Player");
      user2 = new JLabel("Second Player");
      player2 = new JTextField();
      start.addActionListener(this);
      info = new JLabel("<html><div WIDTH=285>" + controller2.getStatus() + "</div></html>");
      JPanel panel = new JPanel();
      panel.setLayout(new GridLayout(3, 2));
      panel.add(user1);
      panel.add(player1);
      panel.add(user2);
      panel.add(player2);
      panel.add(start);
      JPanel mainPanel = new JPanel();
      mainPanel.setLayout(new GridLayout(2, 1));
      mainPanel.add(info);
      mainPanel.add(panel);
      return mainPanel;
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();
      if (source == start) {
         if (!"".equals(player1.getText().trim()) && !"".equals(player2.getText().trim())) {
            controller.setPlayers(player1.getText(), player2.getText());
            GameGui sw = new GameGui();
            sw.setController(controller);
            this.dispose();
            Application.launch(GameGui.class);
         } else {
            JOptionPane.showMessageDialog(null, Messages.USER_ERROR);
         }
      }
   }
}
