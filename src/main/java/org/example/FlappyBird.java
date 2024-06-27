/*
* Java AWT (Abstract Window Toolkit) is a platform-dependent GUI (Graphical User Interface) toolkit in Java.
* It is used to create graphical user interfaces for desktop applications.
* AWT provides a set of classes and methods that enable developers to design interactive and visually appealing applications across different platforms.
* */

package org.example;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList; //to store all pipes in game
import java.util.Random; //placing pipes at random location
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    int boardWidth = 360;
    int boardHeight = 640;

    //images - java.awt package
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //bird initial positions
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;


    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img){
            this.img = img;
        }
    }

    //pipes
    //pipe will start from top of screen and from right side of screen
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64; //scaled by 1/6
    int pipeHeight = 512; //scaled by 1/6

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;  //if bord has passed the pipe and to keep track of image

        Pipe(Image img){
            this.img = img;
        }
    }

    //game logic
    Bird bird;
    int velocityX = -4; //move pipes to left speed(simulates bird moving right)
    int velocityY = 0;  //move bird up speed
    int gravity = 1;    //gravity to pull bird down

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;

     FlappyBird(){
        /*Dimension class encapsulates the width and height of a component (in integer precision) in a single object.
        *Dimension is of java.AWT package
        */
         //set board width and height
         setPreferredSize(new Dimension(boardWidth, boardHeight));
         setFocusable(true);  //this will take Key Events
         addKeyListener(this); //it will check 3 overridden methods of Keys below
         //load images
         backgroundImg = new ImageIcon(ClassLoader.getSystemResource("./flappyBirdbg.png")).getImage();
         birdImg = new ImageIcon(ClassLoader.getSystemResource("./flappybird.png")).getImage();
         topPipeImg = new ImageIcon(ClassLoader.getSystemResource("./toppipe.png")).getImage();
         bottomPipeImg = new ImageIcon(ClassLoader.getSystemResource("./bottompipe.png")).getImage();

         //bird
         bird = new Bird(birdImg);
         pipes = new ArrayList<Pipe>();

         //place Pipes Timer
         placePipesTimer = new Timer(1500, new ActionListener() { //1.5 sec
             @Override
             public void actionPerformed(ActionEvent e) {
                    placePipes();
             }
         });
         placePipesTimer.start();

         //timer loop for 1000ms =  1 sec and 60 frames per sec
         //THIS refers to flappy bird class
         gameLoop = new Timer(1000/60, this);
         gameLoop.start();
     }

     public void placePipes(){
         //random -> between 0 and 1 * pipeHeight/2 -> (0 and 256)
         int randomPipeY = (int)(pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
         int openingSpace = boardHeight/4; //space between top and bottom pipe

         Pipe topPipe = new Pipe(topPipeImg);
         topPipe.y = randomPipeY;
         pipes.add(topPipe);

         Pipe bottomPipe = new Pipe(bottomPipeImg);
         bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
         pipes.add(bottomPipe);
     }

    /*function of Jpanel
    *JPanel is a general-purpose container that can hold other components.
    * It is used to create GUI components and organize them in a specific layout.
    * It is used to group related components together and make them easier to manage.
    * It can also be used to add borders, colors, and other effects to components.
    */
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        //draw background
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);

        //draw bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        //draw pipes
        for(int i=0; i<pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if(gameOver){
            g.drawString("Game Over : " + String.valueOf((int) score), 10, 35);
        }else{
            g.drawString(String.valueOf((int)score), 10, 35);
        }
    }

    public void move(){
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0); //it cant go out of frame at top

        //pipes
        for(int i=0; i<pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;
            if(!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5; //because 2 pipes -> 0.5 + 0.5 = 1
            }
            if(collision(bird, pipe)){
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b){
        return  a.x < b.x + b.width && //a's top left corner does not go beyond b's top right corner
                a.x + a.width > b.x && //a's top right corner passes b's top left corner
                a.y < b.y + b.height && //a's top left corner does not go beyond b's bottom left corner
                a.y + a.height > b.y;  //a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            velocityY = -9;
            if(gameOver){
                //restart game by resetting conditions
                birdY = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }

}
