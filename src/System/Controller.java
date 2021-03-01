package System;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.TextArea;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.File;
import java.io.IOException;


import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.effect.*;

public class Controller implements Initializable {

    Image imageChosen;
    Stage stage;

    @FXML
    public Tab orgTab, calcTab;
    @FXML
    public ImageView originalView, calcView, processedView;
    @FXML
    public Text imageProp1;
    @FXML
    public Slider brightSlider, hueSlider, satSlider, contSlider;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void open(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose image file");
        File file = fileChooser.showOpenDialog(stage);
        if(file != null) {
            file.toURI();
            imageProp1.setText(file.getName() + '\n' + file.length() + " bytes");
            Image image = new Image(file.toURI().toString());
            imageChosen = image;
            originalView.setImage(image);
            calcView.setImage(image);

        }
    }

    public void selectColour(ActionEvent event) throws IOException {
        calcView.setImage(imageChosen);
        calcView.setOnMousePressed(e->	{
            Color selectedColor = calcView.getImage().getPixelReader().getColor((int)e.getX(),(int)e.getY());
            System.out.println("\nHue: "+selectedColor.getHue());
            System.out.println("Saturation: "+selectedColor.getSaturation());
            System.out.println("Brightness: "+selectedColor.getBrightness());
            update(selectedColor);
    });

    }


    public void update(Color fruitColor){
        PixelReader pixelReader = calcView.getImage().getPixelReader();
        Color black = new Color(0,0,0,1);
        Color white = new Color(1,1,1,1);
        WritableImage outputImage = new WritableImage((int)calcView.getImage().getWidth(), (int)calcView.getImage().getHeight());
        PixelWriter writer = outputImage.getPixelWriter();
        for(int i = 0; i < imageChosen.getWidth(); i++) {
            for(int j = 0; j < imageChosen.getHeight(); j++) {
                Color oldColor = pixelReader.getColor(i,j);
                if (oldColor.getHue() <= fruitColor.getHue()+30 || fruitColor.getHue()-30 >= oldColor.getHue()) {
                    writer.setColor(i,j,black);
                } else writer.setColor(i,j,white);
            }
        } calcView.setImage(outputImage);
    }


}
