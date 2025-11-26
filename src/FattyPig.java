
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class FattyPig extends JPanel implements ActionListener, KeyListener {

    int boardWidth = 1720;
    int boardHeight = 880;

    // images
    Image[] backgroundImg = new Image[3];
    Image[][] PigImg = new Image[3][2];
    Image[] topPipeImg = new Image[3];
    Image[] bottomPipeImg = new Image[3];
    Image[] smokeImg = new Image[3];
    Image carrotImg;

    int Pig_skin = 0;
    int bg_skin = 0;

    // bird class
    int PigX = boardWidth / 8;
    int PigY = boardHeight / 2;
    int PigWidth = 134; //134
    int PigHeight = 102; //102


    int flapCounter = 0;
    int flapDuration = 12;

    // logic game
    Pig pig;
    int velocityX = -12;
    int velocityY = 0;
    int gravity = 1;

    int smokeX = PigX;
    int smokeY = PigY;
    int smokeWidth = 134;
    int smokeHeight = 102;
    int currentSmokeFrame = -1;

    ArrayList<Pipe> pipes;
    ArrayList<carrot> carrots;

    Timer gameLoop;
    Timer placePipeTimer;
    Timer change_bg;
    Timer placeCarrotTimer;

    boolean gameOver = false;
    double score = 0;
    double high_score = 0;

    // fade variables
    float fadeOpacity = 0f;
    boolean isFading = false;
    boolean fadeIn = false;

    Timer fadeTimer;
    Timer animation;

    class Pig {
        int x = PigX;
        int y = PigY;
        int width = PigWidth;
        int height = PigHeight;
        Image img;

        Pig(Image img) {
            this.img = img;
        }
    }

    class Pipe {
        int x = boardWidth;
        int y = 0;
        int width = 128;
        int height = 1024;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    class carrot {
        int x = 0;
        int y = 0;
        int width = 116;
        int height = 116;
        Image img;

        carrot(Image img) {
            this.img = img;
        }
    }

    public FattyPig() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        String[] Img_list = {"day", "night", "desert"};
        for (int i = 0; i < Img_list.length; i++) {

            String bgPath = "/assets/bg/fattypigbg_" + Img_list[i] + ".png";
            String topPath = "/assets/pipe/toppipe_" + Img_list[i] + ".png";
            String bottomPath = "/assets/pipe/bottompipe_" + Img_list[i] + ".png";

            java.net.URL u = getClass().getResource(bgPath);
            if (u == null) {
                System.err.println("Resource not found: " + bgPath);
            }
            else {
                backgroundImg[i] = new ImageIcon(u).getImage();
            }

            u = getClass().getResource(topPath);
            if (u == null) {
                System.err.println("Resource not found: " + topPath);
            }
            else {
                topPipeImg[i] = new ImageIcon(u).getImage();
            }

            u = getClass().getResource(bottomPath);
            if (u == null) {
                System.err.println("Resource not found: " + bottomPath);
            }
            else {
                bottomPipeImg[i] = new ImageIcon(u).getImage();
            }

            for (int j = 0; j < 2; j++) {

                String pigPath = "/assets/pig/fattypig_" + Img_list[i] + "_" + j + ".png";

                u = getClass().getResource(pigPath);
                if (u == null) {
                    System.err.println("Resource not found: " + pigPath);
                }
                else {
                    PigImg[i][j] = new ImageIcon(u).getImage();
                }
            }
        }
        carrotImg = new ImageIcon(getClass().getResource("/assets/other/Carrot.png")).getImage();

        for (int i = 0; i < 3; i++) {

            String smokePath = "/assets/other/smoke_" + i + ".png";

            java.net.URL u = getClass().getResource(smokePath);
            if (u == null) {
                System.err.println("Resource not found: " + smokePath);
            } else {
                smokeImg[i] = new ImageIcon(u).getImage();
            }
        }

        pig = new Pig(PigImg[Pig_skin][0]);
        pipes = new ArrayList<>();
        carrots = new ArrayList<>();

        // place pipes timer
        placePipeTimer = new Timer(1500, e -> placePipes());
        placePipeTimer.start();

        // background change
        change_bg = new Timer(15000, e -> startFade());
        change_bg.start();

        // place carrot timer
        placeCarrotTimer = new Timer(5000, e -> placeCarrot());
        placeCarrotTimer.start();

        // game loop
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    void placePipes() {
        int randomPipeY = (int) (0 - 500 - (Math.random() * 300));
        int openingSpace = 300;

        Pipe topPipe = new Pipe(topPipeImg[bg_skin]);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg[bg_skin]);
        bottomPipe.y = topPipe.y + 1024 + openingSpace;
        pipes.add(bottomPipe);
    }

    void placeCarrot() {
        carrot objCarrot = new carrot(carrotImg);
        boolean overlapsPipe = true;

        while (overlapsPipe) {

            objCarrot.x = boardWidth;

            int randomCarrotY = (int) (100 + Math.random() * 600);
            objCarrot.y = randomCarrotY;

            overlapsPipe = false;

            for (Pipe pipe : pipes) {
                if (collision_carrot_with_pipe(objCarrot, pipe)) {
                    overlapsPipe = true;
                    break;
                }
            }
        }
        carrots.add(objCarrot);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg[bg_skin], 0, 0, boardWidth, boardHeight, null);

        g.drawImage(pig.img, pig.x, pig.y, pig.width, pig.height, null);

        if (currentSmokeFrame != -1 && currentSmokeFrame < smokeImg.length) {
            g.drawImage(smokeImg[currentSmokeFrame], pig.x, pig.y, smokeWidth, smokeHeight, null);
        }

        for (carrot carrot : carrots) {
            g.drawImage(carrot.img, carrot.x, carrot.y, carrot.width, carrot.height, null);
        }

        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }


        // score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        g.drawString(gameOver ? "Game Over = " + (int) score : "My Score = " + String.valueOf((int) score), 10, 70);
        g.drawString("High Score = " + String.valueOf((int) high_score), 10, 35);

        // fade overlay
        if (fadeOpacity > 0f) {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(new Color(0f, 0f, 0f, fadeOpacity));
            g2d.fillRect(0, 0, boardWidth, boardHeight);

            if (gameOver) {
                g2d.setFont(new Font("Arial", Font.BOLD, 80));
                g2d.setColor(new Color(139, 0, 0, Math.min(255, (int) (fadeOpacity * 255))));

                String text = "GAME OVER";
                int textWidth = g2d.getFontMetrics().stringWidth(text);
                
                g2d.drawString(text, (boardWidth - textWidth) / 2, boardHeight / 2);
            }
        }
    }

    public void move() {
        velocityY += gravity;
        pig.y += velocityY;
        pig.y = Math.max(pig.y, 0);

        // flap
        if (flapCounter > 0) {
            pig.img = PigImg[Pig_skin][1];
            flapCounter--;
        }
        else {
            pig.img = PigImg[Pig_skin][0];
        }

        // pipes
        for (Pipe pipe : pipes) {
            pipe.x += velocityX;

            if (!pipe.passed && pig.x > pipe.x + pipe.width) {
                score += 0.5;
                pipe.passed = true;
            }

            if (collision(pig, pipe)) {
                triggerFade();
            }
        }

        for (int i = 0; i < carrots.size(); i++) {
            carrot carrot = carrots.get(i);
            carrot.x += velocityX;

            if (carrot.x + carrot.width < 0) {
                carrots.remove(i);
                i--;
            } 
            else if (collision_carrot(pig, carrot)) {
                carrots.remove(i);
                i--;

                random_skin();
            }
        }

        if (pig.y >= boardHeight) {
            triggerFade();
        }
    }

    boolean collision(Pig a, Pipe b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    boolean collision_carrot(Pig a, carrot b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    boolean collision_carrot_with_pipe(carrot a, Pipe b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isFading && !gameOver) {
            move();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -12;
            flapCounter = flapDuration;

            if (gameOver) {
                if (fadeTimer != null && fadeTimer.isRunning()) {
                    fadeTimer.stop();
                }
                if(score > high_score)
                    high_score = score;

                isFading = false;
                fadeIn = false;
                fadeOpacity = 0f;

                bg_skin = 0;
                Pig_skin = 0;

                pig.y = PigY;
                velocityY = 0;
                gameOver = false;
                score = 0;
                pipes.clear();
                carrots.clear();

                gameLoop.start();
                placePipeTimer.start();
                placeCarrotTimer.start();
                change_bg.start();
            }
        }
    }

    void triggerFade() {
        if (!isFading) {
            gameOver = true;

            gameLoop.stop();
            change_bg.stop();
            placePipeTimer.stop();
            placeCarrotTimer.stop();
            startFade();
        }
    }

    void random_skin() {
        int buffer = (int) (Math.random() * 3);
        while (buffer == Pig_skin) {
            buffer = (int) (Math.random() * 3);
        }

        final int final_new_skin = buffer;

        currentSmokeFrame = 0;

        if (animation != null && animation.isRunning()) {
            animation.stop();
        }

        animation = new Timer(100, e -> {
            currentSmokeFrame++;

            if (currentSmokeFrame >= smokeImg.length) {
                Pig_skin = final_new_skin;
                currentSmokeFrame = -1;
                animation.stop();
            }
            repaint();
        });
        animation.start();
    }

    void startFade() {
        isFading = true;
        fadeIn = false;
        fadeTimer = new Timer(50, e -> {
            if (!fadeIn) {
                fadeOpacity += 0.05f;
                if (fadeOpacity >= 1f) {
                    fadeOpacity = 1f;
                    fadeIn = true;

                    pig.y = PigY;
                    velocityY = 0;
                    pipes.clear();

                    if (!gameOver) {
                        int buffer = (int) (Math.random() * 3);

                        while (buffer == bg_skin) {
                            buffer = (int) (Math.random() * 3);
                        }
                        bg_skin = buffer;
                    }
                }
            }
            else {
                if (!gameOver) {
                    fadeOpacity -= 0.05f;
                }

                if (fadeOpacity <= 0f) {
                    fadeOpacity = 0f;
                    isFading = false;
                    fadeTimer.stop();
                }
            }
            repaint();
        });
        fadeTimer.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
