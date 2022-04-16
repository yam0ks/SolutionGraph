package com.model.simplexdata;

public class Section {
    public String title;
    public String description;
    public MatrixItem[][] matrix;

    public Section() {
    }

    public Section(String title, String description, MatrixItem[][] matrix) {
        this.title = title;
        this.description = description;
        this.matrix = matrix;
    }
}
