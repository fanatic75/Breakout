import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;


public class Breakout extends GraphicsProgram {

    /** Width and height of application window in pixels */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    /** Dimensions of game board (usually the same) */
    private static final int WIDTH = APPLICATION_WIDTH;
    private static final int HEIGHT = APPLICATION_HEIGHT;

    /** Dimensions of the paddle */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;

    /** Offset of the paddle up from the bottom */
    private static final int PADDLE_Y_OFFSET = 30;

    /** Number of bricks per row */
    private static final int NBRICKS_PER_ROW = 10;

    /** Number of rows of bricks */
    private static final int NBRICK_ROWS = 10;

    /** Separation between bricks */
    private static final int BRICK_SEP = 4;

    /** Width of a brick */
    private static final int BRICK_WIDTH =
            (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

    /** Height of a brick */
    private static final int BRICK_HEIGHT = 8;

    /** Radius of the ball in pixels */
    private static final int BALL_RADIUS = 10;

    /** Offset of the top brick row from the top */
    private static final int BRICK_Y_OFFSET = 70;

    private static final int ENDPOINT=HEIGHT-(PADDLE_Y_OFFSET)+PADDLE_HEIGHT+1;

    /** Number of turns */
    private static final int NTURNS = 3;

    private static final int NO_OF_BRICKS=100;

    /* Method: init() */
    /** Sets up the Breakout program. */
    public void init() {
        addMouseListeners();
    }

    /* Method: run() */
    /** Runs the Breakout program. */
    public void run() {
        while(true){
            setupgame();
            for(int i=0;i<NTURNS&&bricksgone<=NO_OF_BRICKS;i++){
                 setupball();
                 playgame();
            }
            removeAll();
            if(bricksgone<NO_OF_BRICKS){
                GLabel loselabel=new GLabel("YOU LOSE!",100,200);
                add(loselabel);
            }
            if(bricksgone>=(NO_OF_BRICKS)){
                GLabel winlabel=new GLabel("YOU WIN!",100,200);
                add(winlabel);
            }
            waitForClick();
        }
    }

    private void setupgame(){
        for(int i=0;i<NBRICK_ROWS;i++){
            for(int j=0;j<NBRICKS_PER_ROW/2;j++){
                if(i==0){
                    if(j==0){
                        brickr=new GRect((WIDTH/2)+2,BRICK_Y_OFFSET,BRICK_WIDTH,BRICK_HEIGHT);
                        brickl=new GRect((WIDTH/2)-BRICK_WIDTH-2,BRICK_Y_OFFSET,BRICK_WIDTH,BRICK_HEIGHT);
                    }
                    else{
                        brickr=new GRect((WIDTH/2)+2+(BRICK_WIDTH*j)+(BRICK_SEP*j),BRICK_Y_OFFSET+(BRICK_HEIGHT*i),BRICK_WIDTH,BRICK_HEIGHT);
                        brickl=new GRect((WIDTH/2-BRICK_WIDTH)-2-(BRICK_WIDTH*j)-(BRICK_SEP*j),BRICK_Y_OFFSET+(BRICK_HEIGHT*i),BRICK_WIDTH,BRICK_HEIGHT);
                    }
                }
                else if(j==0){
                    brickr=new GRect((WIDTH/2)+2+(BRICK_WIDTH*j),BRICK_Y_OFFSET+(BRICK_HEIGHT*i)+(BRICK_SEP*i),BRICK_WIDTH,BRICK_HEIGHT);
                    brickl=new GRect((WIDTH/2-BRICK_WIDTH)-2-(BRICK_WIDTH*j),BRICK_Y_OFFSET+(BRICK_HEIGHT*i)+(BRICK_SEP*i),BRICK_WIDTH,BRICK_HEIGHT);
                }
                else{
                    brickr=new GRect((WIDTH/2)+2+(BRICK_WIDTH*j)+(BRICK_SEP*j),BRICK_Y_OFFSET+(BRICK_HEIGHT*i)+(BRICK_SEP*i),BRICK_WIDTH,BRICK_HEIGHT);
                    brickl=new GRect((WIDTH/2-BRICK_WIDTH)-2-(BRICK_WIDTH*j)-(BRICK_SEP*j),BRICK_Y_OFFSET+(BRICK_HEIGHT*i)+(BRICK_SEP*i),BRICK_WIDTH,BRICK_HEIGHT);
                }
                if(i<=1){
                    brickl.setColor(Color.RED);
                    brickr.setColor(Color.RED);
                }
                else if(1<i&&i<=3){
                    brickl.setColor(Color.ORANGE);
                    brickr.setColor(Color.ORANGE);
                }
                else if(3<i&&i<=5){
                    brickl.setColor(Color.YELLOW);
                    brickr.setColor(Color.YELLOW);
                }
                else if(5<i&&i<=7){
                    brickl.setColor(Color.GREEN);
                    brickr.setColor(Color.GREEN);
                }
                else {
                    brickl.setColor(Color.CYAN);
                    brickr.setColor(Color.CYAN);
                }
                brickl.setFilled(true);
                brickr.setFilled(true);
                add(brickl);
                add(brickr);

            }
        }
        paddle=new GRect((WIDTH-PADDLE_WIDTH)/2,HEIGHT-(PADDLE_Y_OFFSET),PADDLE_WIDTH,PADDLE_HEIGHT);
        paddle.setFilled(true);
        add(paddle);
    }

    private void setupball(){
        ball=new GOval((APPLICATION_WIDTH/2)-BALL_RADIUS,(APPLICATION_HEIGHT/2)-BALL_RADIUS,BALL_RADIUS,BALL_RADIUS);
        ball.setFilled(true);
        add(ball);
    }

    public void mouseEntered(MouseEvent e){
       paddlelastxposition=paddle.getX();

    }

    public void mouseMoved(MouseEvent e){
        if(e.getX()>=WIDTH-PADDLE_WIDTH){
            paddle.setLocation(WIDTH-PADDLE_WIDTH,HEIGHT-(PADDLE_Y_OFFSET));
            paddlelastxposition=paddle.getX();
        }
        else{
            paddle.move(e.getX()-paddlelastxposition,0);
            paddlelastxposition=paddle.getX();
        }
    }

    private void playgame(){
        waitForClick();
        if(rgen.nextBoolean(0.5)){
            xvelball=-xvelball;
        }
        while(ball.getY()+(2*BALL_RADIUS)<ENDPOINT&&bricksgone<=NO_OF_BRICKS){
            ballmove();
            pause(8);
            collider=getcollider();
            if(collider!=null){
                if(collider!=paddle){
                    remove(collider);
                    bricksgone++;
                }
                bounceClip.play();
                bounceback();
            }
        }
        remove(ball);
    }

    private void bounceback(){
        yvelball=-yvelball;
        ball.move(xvelball,yvelball);
    }

    private GObject getcollider(){
        GObject gobj;
        gobj=getElementAt(ball.getX(),ball.getY());
        if(gobj==null){
            gobj=getElementAt(ball.getX(),ball.getY()+(2*BALL_RADIUS));
            if(gobj==null){
                gobj=getElementAt(ball.getX()+(2*BALL_RADIUS),ball.getY());
                if(gobj==null){
                    gobj=getElementAt(ball.getX()+(2*BALL_RADIUS),ball.getY()+(2*BALL_RADIUS));
                }
            }
        }
        return gobj;
    }

    private void ballmove(){

        if(ball.getX()<=0||(ball.getX()+(2*BALL_RADIUS))>=WIDTH){
            xvelball=-xvelball;
            ball.move(xvelball,yvelball);
        }
        else if(ball.getY()<=0||(ball.getY()+(2*BALL_RADIUS))>=HEIGHT){
            yvelball=-yvelball;
            ball.move(xvelball,yvelball);
        }
        else{
            ball.move(xvelball,yvelball);

        }
    }


	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
    private RandomGenerator rgen= RandomGenerator.getInstance();
    private GRect brickl;
    private GRect brickr;
    private GRect paddle;
    private double paddlelastxposition;
    private GOval ball;
    private double xvelball=rgen.nextDouble(1.0,3.0);
    private double yvelball=3;
    private GObject collider;
    private int bricksgone=1;

}
