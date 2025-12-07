import javax.swing.*;
import java.awt.*;

public class App {
    public static class MenuPanel extends JPanel {

        JButton startBtn;
        JButton exitBtn;
        Image bg;

        public MenuPanel() {
            setLayout(null);

            // BG
            java.net.URL u = getClass().getResource("/assets/menu/bg-start-exit.jpg");
            if (u != null) {
                bg = new ImageIcon(u).getImage();
            }

            // START button
            java.net.URL startURL = getClass().getResource("/assets/menu/startbtn.png");
            startBtn = new JButton(new ImageIcon(startURL));
            startBtn.setBounds(700, 400, 340, 100);
            startBtn.setBorderPainted(false);
            startBtn.setContentAreaFilled(false);
            startBtn.setFocusPainted(false);
            add(startBtn);

            // EXIT button
            java.net.URL exitURL = getClass().getResource("/assets/menu/exitbtn.png");
            exitBtn = new JButton(new ImageIcon(exitURL));
            exitBtn.setBounds(700, 520, 340, 100);
            exitBtn.setBorderPainted(false);
            exitBtn.setContentAreaFilled(false);
            exitBtn.setFocusPainted(false);
            add(exitBtn);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bg != null) {
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
            }
        }
    }

    public static void main(String[] args) {
        int boardWidth = 1720;
        int boardHeight = 880;

        JFrame frame = new JFrame("FattyPig");
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //สร้างหน้าต่างเมนู
        MenuPanel menu = new MenuPanel();
        frame.add(menu);
        frame.setVisible(true);

        //เมื่อกดปุ่ม START
        menu.startBtn.addActionListener(e -> {
            frame.getContentPane().removeAll();
            FattyPig fattyPig = new FattyPig();
            frame.add(fattyPig);
            frame.pack();
            fattyPig.requestFocus();
            frame.setVisible(true);
        });

        //เมื่อกดปุ่ม EXIT
        menu.exitBtn.addActionListener(e -> {
            System.exit(0);
        });

    }

}
