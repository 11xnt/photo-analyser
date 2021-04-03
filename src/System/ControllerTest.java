package System;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    Controller c;
    Image image;
    ImageView originalView;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        c = new Controller();
        c.originalView = new ImageView();
        c.originalView.setFitHeight(388);
        c.originalView.setFitWidth(517);
        c.originalView.setLayoutX(467);
        c.originalView.setLayoutY(98);
        c.originalView.setPickOnBounds(true);
        c.originalView.setPreserveRatio(true);
        image = new Image("System/dummyphoto.png", c.originalView.getFitWidth(), c.originalView.getFitHeight(), false, true);
        //fitHeight="388.0" fitWidth="517.0" layoutX="467.0" layoutY="98.0" pickOnBounds="true" preserveRatio="true"
        c.originalView.setImage(image);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void open() {
        c.originalView.setImage(image);
        assertEquals(image,c.originalView.getImage());

    }

    @org.junit.jupiter.api.Test
    void updateNewColor() {
    }

    @org.junit.jupiter.api.Test
    void estimateClusterSize() {
    }

    @org.junit.jupiter.api.Test
    void estimateAreaSize() {
    }
}