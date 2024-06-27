package org.example;
import javax.swing.*;
//import org.example.FlappyBird;

public class Main {
    public static void main(String[] args) {
        int boardWidth = 360;
        int boardHeight = 640;

        /*JFrame is a top level container (window)
        *To create GUI with java Swing, we need container, here JFrame
        *All GUI apps require JFrame
        */
        JFrame frame = new JFrame("Flappy Bird");
        frame.setSize(boardWidth, boardHeight);

        /*to position the JFrame in the center of the screen when the application starts.
        *In Java Swing, the setLocationRelativeTo(Component c) method is used to set the location of the window relative to the specified component.
        *If the component is null, or if the specified component is not currently showing, then the window is placed in the center of the screen.
        */
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        //terminate the app when frame is closed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        frame.pack();
        flappyBird.requestFocus();
        frame.setVisible(true); //visibility true only after setting Jpanel to our frame

    }
}