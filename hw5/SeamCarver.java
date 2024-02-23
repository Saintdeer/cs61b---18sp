import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {
    private Picture p;
    private double[][] energyMatrix;
    private double[][] distTo;
    private int[] horizontalSeam = null, verticalSeam = null;

    public SeamCarver(Picture p) {
        int width = p.width(), height = p.height();

        this.p = new Picture(p);
        energyMatrix = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                energyMatrix[x][y] = energy(x, y);
            }
        }
    }

    // current picture
    public Picture picture() {
        return new Picture(p);
    }

    // width of current picture
    public int width() {
        return p.width();
    }

    // height of current picture
    public int height() {
        return p.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        int width = p.width(), height = p.height();

        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException();
        }

        int leftX = x == 0 ? width - 1 : x - 1,
                rightX = x == width - 1 ? 0 : x + 1,
                upY = y == 0 ? height - 1 : y - 1,
                downY = y == height - 1 ? 0 : y + 1;

        Color leftC = p.get(leftX, y),
                rightC = p.get(rightX, y),
                upC = p.get(x, upY),
                downC = p.get(x, downY);

        double xr, xg, xb, yr, yg, yb;
        xr = leftC.getRed() - rightC.getRed();
        xg = leftC.getGreen() - rightC.getGreen();
        xb = leftC.getBlue() - rightC.getBlue();
        yr = upC.getRed() - downC.getRed();
        yg = upC.getGreen() - downC.getGreen();
        yb = upC.getBlue() - downC.getBlue();

        return xr * xr + xg * xg + xb * xb + yr * yr + yg * yg + yb * yb;
    }

    private void setEnergyMatrix() {
        int width = p.width(), height = p.height();
        double[][] newEnergyMatrix = new double[width][height];

        if (horizontalSeam != null) {
            int column = 0;
            for (int changedIndex : horizontalSeam) {
                for (int row = 0; row < height; row++) {
                    if (row == changedIndex - 1 || row == changedIndex) {
                        newEnergyMatrix[column][row] = energy(column, row);
                    } else if (row > changedIndex) {
                        newEnergyMatrix[column][row] = energyMatrix[column][row + 1];
                    } else {
                        newEnergyMatrix[column][row] = energyMatrix[column][row];
                    }
                }
                column++;
            }
            energyMatrix = newEnergyMatrix;
            horizontalSeam = null;
        } else if (verticalSeam != null) {
            int row = 0;
            for (int changedIndex : verticalSeam) {
                for (int column = 0; column < width; column++) {
                    if (column == changedIndex - 1 || column == changedIndex) {
                        newEnergyMatrix[column][row] = energy(column, row);
                    } else if (column > changedIndex) {
                        newEnergyMatrix[column][row] = energyMatrix[column + 1][row];
                    } else {
                        newEnergyMatrix[column][row] = energyMatrix[column][row];
                    }
                }
                row++;
            }
            energyMatrix = newEnergyMatrix;
            verticalSeam = null;
        }
    }

    private void setDistTo(boolean isHorizontal) {
        int width = p.width(), height = p.height();
        distTo = new double[width][height];

        if (isHorizontal) { // from right to left
            int rightColumn = width - 1;
            System.arraycopy(energyMatrix[rightColumn], 0, distTo[rightColumn], 0, height);

            for (int column = rightColumn - 1; column >= 0; column--) {
                for (int row = 0; row < height; row++) {
                    distTo[column][row] = minDistTo(column, row, false)
                            + energyMatrix[column][row];
                }
            }
        } else { // from bottom to top
            int deepRow = height - 1;
            for (int column = 0; column < width; column++) {
                distTo[column][deepRow] = energyMatrix[column][deepRow];
            }

            for (int row = deepRow - 1; row >= 0; row--) {
                for (int column = 0; column < width; column++) {
                    distTo[column][row] = minDistTo(column, row, true)
                            + energyMatrix[column][row];
                }
            }
        }
    }

    private double minDistTo(int column, int row, boolean isVertical) {
        int width = p.width(), height = p.height();
        double[] minDist = new double[3];

        if (isVertical) {
            int downRow = row + 1;
            int leftColumn, rightColumn;
            leftColumn = column == 0 ? column : column - 1;
            rightColumn = column == width - 1 ? column : column + 1;

            minDist[0] = distTo[leftColumn][downRow];
            minDist[1] = distTo[column][downRow];
            minDist[2] = distTo[rightColumn][downRow];
        } else {
            int rightColumn = column + 1;
            int upRow, downRow;
            upRow = row == 0 ? row : row - 1;
            downRow = row == height - 1 ? row : row + 1;

            minDist[0] = distTo[rightColumn][upRow];
            minDist[1] = distTo[rightColumn][row];
            minDist[2] = distTo[rightColumn][downRow];
        }

        double min = minDist[0];
        for (double dist : minDist) {
            if (min > dist) {
                min = dist;
            }
        }

        return min;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        setEnergyMatrix();
        setDistTo(true);

        int smallestIndex = 0, height = p.height();
        double newDistTo, smallDistTo = distTo[0][smallestIndex];
        for (int row = 1; row < height; row++) {
            newDistTo = distTo[0][row];
            if (smallDistTo > newDistTo) {
                smallestIndex = row;
                smallDistTo = newDistTo;
            }
        }

        return findHorizontalPath(smallestIndex);
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        setEnergyMatrix();
        setDistTo(false);

        int smallestIndex = 0, width = p.width();
        double newDistTo, smallDistTo = distTo[smallestIndex][0];
        for (int column = 1; column < width; column++) {
            newDistTo = distTo[column][0];
            if (smallDistTo > newDistTo) {
                smallestIndex = column;
                smallDistTo = newDistTo;
            }
        }

        return findVerticalPath(smallestIndex);
    }

    private int[] findVerticalPath(int smallColumn) {
        int width = p.width(), height = p.height();
        int smallHeight = 0;

        int[] path = new int[height];
        path[0] = smallColumn;

        // top to bottom
        for (int lowRow = 1; lowRow < height; lowRow++) {
            for (int offset = -1; offset <= 1; offset++) {
                int lowColumn = smallColumn + offset;
                if (lowColumn < 0 || lowColumn >= width) {
                    continue;
                }

                double differenceEnergy = distTo[smallColumn][smallHeight]
                        - distTo[lowColumn][lowRow];
                if (differenceEnergy == energyMatrix[smallColumn][smallHeight]) {
                    path[lowRow] = lowColumn;
                    smallColumn = lowColumn;
                    smallHeight = lowRow;
                    break;
                }
            }
        }
        return path;
    }

    private int[] findHorizontalPath(int smallRow) {
        int width = p.width(), height = p.height();
        int smallColumn = 0;

        int[] path = new int[width];
        path[0] = smallRow;

        // left to right
        for (int leftColumn = 1; leftColumn < width; leftColumn++) {
            for (int offset = -1; offset <= 1; offset++) {
                int row = smallRow + offset;
                if (row < 0 || row >= height) {
                    continue;
                }

                double differenceEnergy = distTo[smallColumn][smallRow]
                        - distTo[leftColumn][row];
                if (differenceEnergy == energyMatrix[smallColumn][smallRow]) {
                    path[leftColumn] = row;
                    smallRow = row;
                    smallColumn = leftColumn;
                    break;
                }
            }
        }
        return path;
    }

    // remove horizontal seam from picture
    public void removeHorizontalSeam(int[] seam) {
        lengthCheck(seam, p.width());

        if (pictureLengthLessThanTwo()) {
            return;
        }

        p = SeamRemover.removeHorizontalSeam(p, seam);
        horizontalSeam = seam;
    }

    // remove vertical seam from picture
    public void removeVerticalSeam(int[] seam) {
        lengthCheck(seam, p.height());
        if (pictureLengthLessThanTwo()) {
            return;
        }

        p = SeamRemover.removeVerticalSeam(p, seam);
        verticalSeam = seam;
    }

    private void lengthCheck(int[] seam, int length) {
        if (seam.length != length) {
            throw new IllegalArgumentException();
        }

        int first = seam[0];
        for (int s : seam) {
            int difference = first - s;
            if (difference > 1 || difference < -1) {
                throw new IllegalArgumentException();
            }
            first = s;
        }
    }

    private boolean pictureLengthLessThanTwo() {
        return p.width() < 2 || p.height() < 2;
    }
}
