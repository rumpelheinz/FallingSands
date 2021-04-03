import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;

public class Main extends Application {

    Engine board;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                //  Platform.exit();
                try {
                    board.save(new BufferedOutputStream(new FileOutputStream("save.txt")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
        primaryStage.setTitle("Sandbox");
        VBox root = new VBox();


        int  height=600;
        int  width=600;
        try {
            board=new Engine(new BufferedInputStream(new FileInputStream("save.txt")));
        } catch (FileNotFoundException e) {

            board=new Engine(width, height);
//            e.printStackTrace();
        } catch (IOException e) {

            board=new Engine(width, height);
            e.printStackTrace();
        }

        HBox buttonBox = new HBox();
        ButtonVariables[] buttonVars= {
                new ButtonVariables(Color.ORANGE,PixelType.FIRE),
                new ButtonVariables(Color.RED,PixelType.FIRESPOUT),
                new ButtonVariables(Color.BLUE,PixelType.WATER),
                new ButtonVariables(Color.DARKBLUE,PixelType.WATERSPOUT),
                new ButtonVariables(Color.LIGHTGREEN,PixelType.PLANT),
                new ButtonVariables(Color.DARKGREEN,PixelType.PLANTSPOUT),
                new ButtonVariables(Color.BLACK,PixelType.AIR),
                new ButtonVariables(Color.GRAY,PixelType.STONE)
        };

        int btnCount = buttonVars.length;
        for (ButtonVariables butv :buttonVars) {
            Button button=new Button();
            button.setBackground(new Background(new BackgroundFill(butv.color, null, null)));
            button.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    board.setType(butv.type);
                }
            });
            button.prefWidthProperty().bind(buttonBox.widthProperty().divide(btnCount));
            buttonBox.getChildren().add(button);
        }



        StackPane container2 = new StackPane();
        container2.prefHeightProperty().bind(root.heightProperty());
        container2.prefWidthProperty().bind(root.widthProperty());
        container2.setBackground(new Background(new BackgroundFill( Color.GREEN, null, null)));

        root.getChildren().add(buttonBox);
        SwingNode swingNode= new SwingNode();

        //  setSize(width*pixelsize+pixelsize, height*pixelsize+pixelsize );

        swingNode.setContent(board);

        container2.getChildren().add(swingNode);
        root.getChildren().add( container2);
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
    }
}