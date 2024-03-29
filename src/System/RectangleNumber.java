package System;

import java.util.Comparator;

public class RectangleNumber {

    int root, maxX, minX, maxY, minY, area;

    public RectangleNumber(int root, int maxX, int minX, int maxY, int minY, int area) {
        this.root = root;
        this.minX = minX;
        this.maxX = maxX;
        this.maxY = maxY;
        this.minY = minY;
        this.area = area;
    }

    // learnt from a tutorial at https://beginnersbook.com/2013/12/java-arraylist-of-object-sort-example-comparable-and-comparator/
    public static Comparator<RectangleNumber> AreaComparator = new Comparator<RectangleNumber>() {

        public int compare(RectangleNumber rN1, RectangleNumber rN2) {

            int rnArea1 = rN1.getArea();
            int rnArea2 = rN2.getArea();

            return rnArea1-rnArea2;
        }
    };


    public int getRoot() {
        return root;
    }

    public void setRoot(int root) {
        this.root = root;
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getMaxX() { return maxX; }

    public void setMaxX(int maxX) { this.maxX = maxX; }

    public int getMinY() { return minY; }

    public void setMinY(int minY) { this.minY = minY; }

}
