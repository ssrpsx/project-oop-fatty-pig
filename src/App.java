import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 1720;
        int boardHeight = 880;

        JFrame frame = new JFrame("FattyPig");
        frame.setVisible(true);
		frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FattyPig fattyPig = new FattyPig();
        frame.add(fattyPig);
        frame.pack();
        fattyPig.requestFocus();
        frame.setVisible(true);
    }
}
