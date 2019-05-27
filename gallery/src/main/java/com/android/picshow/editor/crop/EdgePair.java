package com.android.picshow.editor.crop;

public class EdgePair {

    public Edge primary;
    public Edge secondary;


    public EdgePair(Edge edge1, Edge edge2) {
        primary = edge1;
        secondary = edge2;
    }
}
