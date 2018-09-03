package com.shirael.project1;

import android.graphics.Path;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class Paths
{
    private   List<Point> points=new ArrayList<Point>();
    private Point mlastpoint = null;
    private   Point mfirstpoint = null;
    private   boolean bfirstpoint=false ;
    private   Path path = new Path();
    // private boolean flgPathDraw = true;


    public Paths()
    {
        //points = new ArrayList<Point>();
        // bfirstpoint = false;
    }

    public List<Point> getPoints() {
        return points;
    }

    public boolean isBfirstpoint() {
        return bfirstpoint;
    }

    public Path getPath() {
        return path;
    }

    public Point getMfirstpoint() {
        return mfirstpoint;
    }

    public Point getMlastpoint() {
        return mlastpoint;
    }


    public void setBfirstpoint(boolean bfirstpoint) {
        this.bfirstpoint = bfirstpoint;
    }
    public void  Add(Point p)
    {
        points.add(p);

    }




    public void setMfirstpoint(Point mfirstpoint) {
        this.mfirstpoint = mfirstpoint;
    }

    public void setMlastpoint(Point mlastpoint) {
        this.mlastpoint = mlastpoint;
    }

    public void setPath(Path path) {
        this.path = path;
    }


    public void setPoints(List<Point> points) {
        this.points = points;
    }




}