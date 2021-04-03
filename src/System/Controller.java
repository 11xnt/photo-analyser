package System;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
import java.util.*;

import static java.lang.Integer.parseInt;

public class Controller implements Initializable {

    Image imageChosen, bnW;
    Stage stage;
    ArrayList<RectangleNumber> rectangleNumbers;
    WritableImage baW;

    private Color selectedColor;
    private int clusterCounter;
    int[] imageArray;



    @FXML
    public Tab orgTab, calcTab;
    @FXML
    public ImageView originalView, calcView, processedView;
    @FXML
    public Text imageProp1, estimatedFruitClusterSize, successColouringText;
    @FXML
    public Slider brightSlider, satSlider;
    @FXML
    public Button colourAllDisjointButton;
    @FXML
    public TextField clusterIdText;


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
            estimatedFruitClusterSize.setText("");
            Image image = new Image(file.toURI().toString(), originalView.getFitWidth(), originalView.getFitHeight(), false, true);
            imageChosen = image;
            originalView.setImage(image);
            calcView.setImage(image);
            processedView.setImage(image);
            successColouringText.setVisible(false);
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
        WritableImage baW = new WritableImage((int) calcView.getImage().getWidth(), (int) calcView.getImage().getHeight());
        PixelWriter writer = baW.getPixelWriter();
        for (int i = 0; i < imageChosen.getWidth(); i++) {
            for (int j = 0; j < imageChosen.getHeight(); j++) {
                Color oldColor = pixelReader.getColor(i, j);
                // if hue/saturation/brightness out of range -> turn black
                // for red specific fruit (due to the fact red's hue is around between 320 degrees and 30 degrees
                if(finalHue > 320 || finalHue < 30) {
                    if (((oldColor.getHue() >= finalHue+20) && (finalHue < 30) && (oldColor.getHue() < 320)) || (oldColor.getHue() <= finalHue-60) && (finalHue > 320) && (oldColor.getHue() > 30)) {
                        writer.setColor(i, j, black);
                    } else {
                        if (oldColor.getSaturation() <= finalSat - .9 || oldColor.getSaturation() >= finalSat + .9) {
                            writer.setColor(i, j, black);
                        } else {
                            if (oldColor.getBrightness() <= finalBri - .7 || oldColor.getBrightness() >= finalBri + .2) {
                                writer.setColor(i, j, black);
                            } else {
                                writer.setColor(i, j, white);
                            }
                        }
                    }
                }
                // for orange coloured fruit
                else if(finalHue < 60 && finalHue > 30) {
                    // if hue is out of range turn black
                    if ((oldColor.getHue() <= finalHue-30) || (oldColor.getHue() >= finalHue+30)) {
                        writer.setColor(i, j, black);
                    } else {
                        if(oldColor.getSaturation() <= finalSat-.3 || oldColor.getSaturation() >= finalSat+.3) {
                            writer.setColor(i, j, black);
                        } else {
                            if(oldColor.getBrightness() <= finalBri-.45 || oldColor.getBrightness() >= finalBri+.45) {
                                writer.setColor(i, j, black);
                            } else {
                                writer.setColor(i, j, white);
                            }
                        }
                    }
                }
                // for blue/purple coloured fruit
                else if(finalHue < 320 && finalHue > 150) {
                    // if hue is out of range turn black
                    if ((oldColor.getHue() <= finalHue-60) || (oldColor.getHue() >= finalHue+60)) {
                        writer.setColor(i, j, black);
                    } else {
                        if(oldColor.getSaturation() <= finalSat-.3 || oldColor.getSaturation() >= finalSat+.3) {
                            writer.setColor(i, j, black);
                        } else {
                            if(oldColor.getBrightness() <= finalBri-.45 || oldColor.getBrightness() >= finalBri+.45) {
                                writer.setColor(i, j, black);
                            } else {
                                writer.setColor(i, j, white);
                            }
                        }
                    }
                }
            }
        }
        calcView.setImage(baW);
        setBawImage(baW);
    }

    public void process(ActionEvent event) {
        //create array the size of the width x the height
        imageArray = new int[(int) imageChosen.getHeight() * (int) imageChosen.getWidth()];
        Color white = new Color(1, 1, 1, 1);
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
            }
        }
        getRectPositions(imageArray);
        colourAllDisjointButton.setVisible(true);
    }

    public void getRectPositions(int[] imageArray) {
        ((AnchorPane)processedView.getParent()).getChildren().removeIf(f->f instanceof Rectangle);
        ((AnchorPane)processedView.getParent()).getChildren().removeIf(h->h instanceof Text);
        HashSet<Integer> roots = new HashSet<>();
        ArrayList<RectangleNumber> rectangleNumbers = new ArrayList<RectangleNumber>();


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

            // cleanup for outlying, small disjoint sets by finding the area of each disjoint set.
            if ((((maxX-minX)*(maxY - minY)) > 150) && (((maxX-minX)*(maxY - minY)) < 1000000)) {
                drawRectangles(minX, minY, maxX, maxY);
                rectangleNumbers.add(new RectangleNumber(r, maxX, minX, maxY, minY, estimateAreaSize(r)));
            }
        }
        drawNumbers(rectangleNumbers);
        setRectangleNumbers(rectangleNumbers);
    }

    // draws the numbers of
    public void drawNumbers(ArrayList<RectangleNumber> rectangleNumbers) {
        clusterCounter = 0;
        rectangleNumbers.sort(RectangleNumber.AreaComparator);
        Collections.reverse(rectangleNumbers);
        for (int i = 0; i < rectangleNumbers.size(); i++) {
            Text text = new Text(rectangleNumbers.get(i).getMinX(), rectangleNumbers.get(i).getMaxY(), 1+i+"");
            Text areaText = new Text(rectangleNumbers.get(i).getMinX(), rectangleNumbers.get(i).getMaxY()+15, "Area: "+rectangleNumbers.get(i).getArea()+" pixels");
            text.setLayoutX(calcView.getLayoutX());
            text.setLayoutY(calcView.getLayoutY());
            areaText.setLayoutX(calcView.getLayoutX());
            areaText.setLayoutY(calcView.getLayoutY());
            ((AnchorPane) processedView.getParent()).getChildren().add(text);
            ((AnchorPane) processedView.getParent()).getChildren().add(areaText);
            clusterCounter++;
            setClusterCounter(clusterCounter);
        }
    }

    // creates a rectangle instance and sets the style of having a stroke of blue and adding it to the imageView
    public void drawRectangles(int minX, int minY, int maxX, int maxY) {
        Rectangle rect = new Rectangle(minX, minY, maxX-minX, maxY-minY);
        rect.setFill(Color.TRANSPARENT);
        rect.setStroke(Color.BLUE);
        rect.setStrokeWidth(2);
        rect.setLayoutX(calcView.getLayoutX());
        rect.setLayoutY(calcView.getLayoutY());
        ((AnchorPane)processedView.getParent()).getChildren().add(rect);
    }

    public void estimateClusterSize(ActionEvent event) {
        ((AnchorPane)processedView.getParent()).getChildren().removeAll(estimatedFruitClusterSize);
        estimatedFruitClusterSize.setText("Estimated fruit cluster size: " + getClusterCounter());
        ((AnchorPane)processedView.getParent()).getChildren().add(estimatedFruitClusterSize);
    }

    // estimates the cluster area size for each of cluster (white pixels in a cluster)
    public int estimateAreaSize(int r) {
        int areaCounter = 0;
        for (int j : imageArray) {
            if (r == j) {
                areaCounter++;
            }
        }
        return areaCounter;
    }

    public void colourAllDisjoints(ActionEvent event) {
        calcView.setImage(getBawImage());
        Random rand = new Random();
        //HashSet<Integer> roots = new HashSet<>();
        PixelReader pixelReader = calcView.getImage().getPixelReader();
        WritableImage outputImage = new WritableImage((int) calcView.getImage().getWidth(), (int) calcView.getImage().getHeight());
        PixelWriter writer = outputImage.getPixelWriter();

        // overlaps the image to make the other disjoint sets the same
        overlapBaW(pixelReader, writer);

        for (int i = 0; i < rectangleNumbers.size(); i++) {
            // random 'light' colours sourced from https://stackoverflow.com/questions/4246351/creating-random-colour-in-java/48227323
            float r = (float) (rand.nextFloat() / 2f + 0.5);
            float g = (float) (rand.nextFloat() / 2f + 0.5);
            float b = (float) (rand.nextFloat() / 2f + 0.5);
            Color randColor = new Color(r, g, b, 1);

            for(int k = getRectangleNumbers().get(i).getMinX(); k <= getRectangleNumbers().get(i).getMaxX(); k++) {
                for(int j = getRectangleNumbers().get(i).getMinY(); j <= getRectangleNumbers().get(i).getMaxY(); j++) {
                    if(pixelReader.getColor(k,j).equals(Color.WHITE)) {
                        writer.setColor(k,j,randColor);
                    }
                }
            }
        }
        calcView.setImage(outputImage);
    }

    // gets the x,y coords of the image
    public void colourSingleDisjointSet(ActionEvent event) throws IOException {
        calcView.setImage(getBawImage());
        rectangleNumbers.sort(RectangleNumber.AreaComparator);
        Collections.reverse(rectangleNumbers);
        int selectedCluster = parseInt(clusterIdText.getText())+1;
        PixelReader pixelReader = calcView.getImage().getPixelReader();
        WritableImage outputImage = new WritableImage((int) calcView.getImage().getWidth(), (int) calcView.getImage().getHeight());
        PixelWriter writer = outputImage.getPixelWriter();
        Random rand = new Random();
        float r = (float) (rand.nextFloat() / 2f + 0.5);
        float g = (float) (rand.nextFloat() / 2f + 0.5);
        float b = (float) (rand.nextFloat() / 2f + 0.5);
        Color randColor = new Color(r, g, b, 1);
        // overlaps the image to make the other disjoint sets the same
        overlapBaW(pixelReader, writer);

        for(int i = getRectangleNumbers().get(selectedCluster-2).getMinX(); i <= getRectangleNumbers().get(selectedCluster-2).getMaxX(); i++) {
            for(int j = getRectangleNumbers().get(selectedCluster-2).getMinY(); j <= getRectangleNumbers().get(selectedCluster-2).getMaxY(); j++) {
                if(pixelReader.getColor(i,j).equals(Color.WHITE)) {
                    writer.setColor(i,j,randColor);
                }
            }
        }
        calcView.setImage(outputImage);
        displayText();
    }

    public void displayText() {
        successColouringText.setVisible(true);
        successColouringText.setText("Now go back to the Calculated tab to see the changes");
        ((AnchorPane) processedView.getParent()).getChildren().add(successColouringText);
    }


    // overlaps the image to make the other disjoint sets the same
    public void overlapBaW(PixelReader pixelReader, PixelWriter writer) {
        for (int l = 0; l < imageChosen.getWidth(); l++) {
            for (int k = 0; k < imageChosen.getHeight(); k++) {
                if(pixelReader.getColor(l,k).equals(Color.BLACK)) {
                    writer.setColor(l,k,Color.BLACK);
                }
            }
        }
    }

    // finds the root of each of the pixels
    public int find(int[] imageArray, int data) {
        if(imageArray[data]==data) return data;
        else return find(imageArray, imageArray[data]);
    }

    // unions the disjoint sets into bigger ones
    public void union(int[] imageArray, int a, int b) {
        imageArray[find(imageArray,b)]=find(imageArray,a); //makes the root of b made to reference a
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
    }

    public void setClusterCounter(int clusterCounter) {
        this.clusterCounter = clusterCounter;
    }

    public int getClusterCounter() {
        return clusterCounter;
    }

    private void setBawImage(WritableImage baW) {
        this.baW = baW;
    }

    private WritableImage getBawImage() {
        return baW;
    }

    public ArrayList<RectangleNumber> getRectangleNumbers() {
        return rectangleNumbers;
    }

    public void setRectangleNumbers(ArrayList<RectangleNumber> rectangleNumbers) {
        this.rectangleNumbers = rectangleNumbers;
    }


    //TODO: fix cluster array location and fix cluster sizes
}
