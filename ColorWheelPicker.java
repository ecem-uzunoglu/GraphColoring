package org.example.project1;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

public class ColorWheelPicker {

    private final Canvas canvas;//canvas for the wheel
    private final double centerX;
    private final double centerY;
    private final double radius;
    private final StackPane root;


    public ColorWheelPicker(double width, double height, double radius) {
        this.canvas=new Canvas(width,height);
        this.radius=radius;
        this.centerX=width/2.0;
        this.centerY=height/2.0;

        //drawing the color wheel
        drawColorWheel(canvas.getGraphicsContext2D());

        //adding canvas to stackpane
        root=new StackPane(canvas);

    }

    //drawing the color wheel using HSV model  void drawColorWheel
    private void drawColorWheel(GraphicsContext gc){
        for(int y=0;y<2*radius;y++){
            for(int x=0;x<2*radius;x++){
                double dx=x -radius;
                double dy=y -radius;
                double distance =Math.sqrt(dx*dx+dy*dy);

                if(distance <=radius){
                    double angle =Math.toDegrees(Math.atan2(dy,dx));
                    double saturation =distance/radius;
                    Color color=Color.hsb(angle,saturation,1.0);
                    gc.getPixelWriter().setColor((int) (centerX-radius+x),(int) (centerY-radius+y),color);
                }

            }
        }
    }


    //getting color based on click position
    public Color getColor(MouseEvent event ){
        double dx=event.getX() - centerX;
        double dy=event.getY() - centerY;
        double distance=Math.sqrt(dx*dx+dy*dy);

        if(distance<=radius){
            double angle=Math.toDegrees(Math.atan2(dy,dx));
            double saturation=distance/radius;
            return Color.hsb(angle,saturation,1.0);
        }
        return null;//return null if outside the wheel

    }

    public StackPane getView() {
        return root;
    }
}
