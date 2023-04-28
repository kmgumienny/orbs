package com.kgummy.osrs.orbs;

import java.awt.*;

public class OrbDetails {

    private Polygon polygon;
    private Color fillColor;

    public OrbDetails(Polygon polygon, Color fillColor) {
        this.polygon = polygon;
        this.fillColor = fillColor;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }
}
