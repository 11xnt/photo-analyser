package System;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;

public class Controller implements Initializable {

    Image imageChosen;
    Stage stage;



    private Color selectedColor;
    int[] imageArray;


    @FXML
    public Tab orgTab, calcTab;
    @FXML
    public ImageView originalView, calcView, processedView;
    @FXML
    public Text imageProp1;
    @FXML
    public Slider brightSlider, satSlider;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void open(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose image file");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            file.toURI();
            imageProp1.setText(file.getName() + '\n' + file.length() + " bytes");
            Image image = new Image(file.toURI().toString(), originalView.getFitWidth(), originalView.getFitHeight(), false, true);
            imageChosen = image;
            originalView.setImage(image);
            calcView.setImage(image);
            processedView.setImage(image);

        }
    }

    public void selectColour(ActionEvent event) throws IOException {
        //gets image colour
        calcView.setImage(imageChosen);
        calcView.setOnMousePressed(e -> {
            Color selectedColor = calcView.getImage().getPixelReader().getColor((int) e.getX(), (int) e.getY());
            System.out.println("\nHue: " + selectedColor.getHue());
            System.out.println("Saturation: " + selectedColor.getSaturation());
            System.out.println("Brightness: " + selectedColor.getBrightness());
            setSelectedColor(selectedColor);
        });
    }

    public void updateNewColor(ActionEvent event) {
        calcView.setImage(imageChosen);
        brightSlider.getValue();
        satSlider.getValue();
        //multiply slider value with selected colour b/h/s/c
        double finalHue = getSelectedColor().getHue();                                          //hue
        double finalSat = (satSlider.getValue() * getSelectedColor().getSaturation());          //saturation
        double finalBri = (brightSlider.getValue() * getSelectedColor().getBrightness());       //brightness

        PixelReader pixelReader = calcView.getImage().getPixelReader();
        Color black = new Color(0, 0, 0, 1);
        Color white = new Color(1, 1, 1, 1);
        WritableImage outputImage = new WritableImage((int) calcView.getImage().getWidth(), (int) calcView.getImage().getHeight());
        PixelWriter writer = outputImage.getPixelWriter();
        for (int i = 0; i < imageChosen.getWidth(); i++) {
            for (int j = 0; j < imageChosen.getHeight(); j++) {
                Color oldColor = pixelReader.getColor(i, j);
                //if hue/saturation/brightness out of range -> turn black

                // for red specific fruit (due to the fact red's hue is between 320 degrees and 30 degrees
                if(finalHue >= 320 || finalHue <= 30) {
                    if (((oldColor.getHue() <= finalHue) && (oldColor.getHue() <= 30)) || ((oldColor.getHue() >= finalHue) && (oldColor.getHue() >= 320))) {
                        if(oldColor.getSaturation() <= finalSat-.1 || oldColor.getSaturation() >= finalSat+.1) {
                            if(oldColor.getBrightness() <= finalBri-.4 || oldColor.getBrightness() >= finalBri+.4) {
                                writer.setColor(i, j, black);
                            } else {
                                writer.setColor(i, j, white);
                            }
                        } else {
                            writer.setColor(i, j, black);
                        }
                    } else writer.setColor(i, j, black);
                }

                // for other coloured fruit
                else if(finalHue < 320 || finalHue > 30) {
                    if (((oldColor.getHue()+40 <= finalHue) && (oldColor.getHue() > 30)) || ((oldColor.getHue()-40 >= finalHue) && (oldColor.getHue() < 320))) {
                        if(oldColor.getSaturation() <= finalSat-.6 || oldColor.getSaturation() >= finalSat+.6) {
                            if(oldColor.getBrightness() <= finalBri-.7 || oldColor.getBrightness() >= finalBri+.7) {
                                writer.setColor(i, j, black);
                            } else {
                                writer.setColor(i, j, white);
                            }
                        } else {
                            writer.setColor(i, j, black);
                        }
                    } else writer.setColor(i, j, black);
                }

                //^^^^^^^^^^^^ FIX THIS SHITHEAD







            }
        }
        calcView.setImage(outputImage);
    }

    public void process(ActionEvent event) {
        //create array the size of the width x the height
        imageArray = new int[(int) imageChosen.getHeight() * (int) imageChosen.getWidth()];
        Color white = new Color(1, 1, 1, 1);
        int row = 0;
        int column = 0;
        //go through pixel by pixel, if black { -1 }, white { row*width+column }
        PixelReader pixelReader = calcView.getImage().getPixelReader();
        for (int i = 0; i < calcView.getImage().getHeight(); i++) {
            for (int j = 0; j < calcView.getImage().getWidth(); j++) {
                Color getColor = pixelReader.getColor(j, i);
                //if white set position of array to coords
                if (getColor.equals(white)) {
                    //set white pixel position to the array.
                    imageArray[(i * (int) calcView.getImage().getWidth()) + j] = (i * (int) calcView.getImage().getWidth()) + j;
                } else
                    imageArray[(i * (int) calcView.getImage().getWidth()) + j] = -1; //sets black pixels to -1 in the array.
            }
        }

        for (int data = 0; data < imageArray.length; data++) {
            int right;
            int down;
            if ((data + 1) < imageArray.length) {
                right = data + 1;
            } else right = imageArray.length-1;

            //checks if down is possible
            if ((data + (int) calcView.getImage().getWidth()) < imageArray.length) {
                down = data + (int) calcView.getImage().getWidth();
            } else down = imageArray.length-1;

            if (imageArray[data] > -1) {

                if(down < imageArray.length && imageArray[down] > -1) {
                    union(imageArray, data, down);
                }
                if(right < imageArray.length && imageArray[right] > -1 ) {
                    union(imageArray, data, right);
                }
                //System.out.println("Index: " + data + " root: " + imageArray[data]);
            }
        }
        getRectPositions(imageArray);
    }

    public void getRectPositions(int[] imageArray) {
        HashSet<Integer> roots = new HashSet<>();

        for(int i = 0; i < imageArray.length; i++) if(imageArray[i] != -1) roots.add(find(imageArray,i));

        for(int r : roots) {
            // calculate fruit bounds for every fruit.
            int minX = (int) calcView.getImage().getWidth();
            int minY = (int) calcView.getImage().getHeight();
            int maxX = 0;
            int maxY = 0;

            for (int i = 0; i < imageArray.length; i++) {
                if (imageArray[i] != -1) {
                    int r2 = find(imageArray, i);
                    if (r == r2) {
                        int rx = i % (int) calcView.getImage().getWidth();
                        int ry = i / (int) calcView.getImage().getWidth();
                            if (rx < minX) {
                                minX = rx;
                            }
                            if (ry < minY) {
                                minY = ry;
                            }
                            if (rx > maxX) {
                                maxX = rx;
                            }
                            if (ry > maxY) {
                                maxY = ry;
                            }
                    }
                }
            }
            if ((maxX-minX)*(maxY - minY) > 150) {
                drawRectangles(minX, minY, maxX, maxY);
            }
        }
    }

    public void drawRectangles(int minX, int minY, int maxX, int maxY) {
        //check
        Rectangle rect = new Rectangle(minX, minY, maxX-minX, maxY-minY);
        rect.setFill(Color.TRANSPARENT);
        rect.setStroke(Color.BLUE);
        rect.setLayoutX(calcView.getLayoutX());
        rect.setLayoutY(calcView.getLayoutY());
        ((AnchorPane)processedView.getParent()).getChildren().remove(rect);
        ((AnchorPane)processedView.getParent()).getChildren().add(rect);
    }

    //finds the root of each of the pixels
    public int find(int[] imageArray, int data) {
        if(imageArray[data]==data) return data;
        else return find(imageArray, imageArray[data]);
    }

    //unions the disjoint sets into bigger ones
    public void union(int[] imageArray, int a, int b) {
        imageArray[find(imageArray,b)]=find(imageArray,a); //makes the root of b made to reference a
    }


    public Color getSelectedColor() {
            return selectedColor;
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
    }
}
