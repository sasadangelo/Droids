/*
 *  Copyright (C) 2016 Salvatore D'Angelo
 *  This file is part of Droids project.
 *
 *  Droids is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Droids is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License.
 */
package org.androidforfun.droids.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/*
 * This is the main model class. It is the entry point that describe the DroidsWorld world.
 */
public class DroidsWorld {
    // The droids world is a grid of 10x20 cells
    static final int WORLD_WIDTH = 10;
    static final int WORLD_HEIGHT = 20;

    // the possible game status values
    public enum GameState {
        Ready,
        Running,
        Paused,
        GameOver
    }

    // the game status
    GameState state = GameState.Ready;

    // this is the list of blocks laying in the botton of screen.
    private List<Block> blocks = new ArrayList<>();
    // the game level
    private int level = 0;
    // In droids falling shape.. This variable contains the falling shape.
    private Shape fallingShape;
    // To help user the game shows also the next shape that will fall after the current one completed
    // its falling.
    private Shape nextShape;
    // the remaining number of lines to fill to complete the current level
    private int goal;
    // the user score
    private int score = 0;

    // the private static instance used to implement the Singleton pattern.
    private static DroidsWorld instance = null;
    
    private DroidsWorld() {
        spawnNextShape();
        makeNextShapeFalling();
        goal=5;
    }

    public static DroidsWorld getInstance() {
        if (instance == null) {
            instance = new DroidsWorld();
        }
        return instance;
    }

    public int getGoal() {
        return goal;
    }

    // When the a shape finished to fall a new one must fall.
    // This function is used to generate a new shape that will be the
    // new falling shape.
    public void spawnNextShape() {
        Random r = new Random();
        int index = r.nextInt(7);

        Shape shapes[] = { new ShapeI(), new ShapeL(), new ShapeJ(), new ShapeCube(), new ShapeZ(), new ShapeT(), new ShapeS()};
        nextShape = shapes[index];
    }

    // When the a shape finished to fall a new one must fall.
    // This function is used to generate a new shape that will be the
    // new falling shape.
    public void makeNextShapeFalling() {
        // Spawn a random shape and add the current falling shape' blocks to the "static" blocks list
        if (fallingShape != null) {
            // The blocks of the falling shape will be added
            // to the list of block present on the main window.
            for (Block block : fallingShape.getBlocks())
                blocks.add(block);
        }

        fallingShape = nextShape;
        spawnNextShape();
    }

    public void update(float deltaTime) {
        if (state == GameState.GameOver)
            return;

        if (state == GameState.Running ) {
            if (fallingShape.collide()) {
                state = GameState.GameOver;
            } else {
                fallingShape.update();
                if (!fallingShape.isFalling()) {
                    Shape layingShape = fallingShape;
                    score+=layingShape.softDropScore;
                    makeNextShapeFalling();
                    deleteLinesOf(layingShape);
                }
            }
        }
    }

    // When the falling shape completes its journey this method is called
    // to check if its block completes one or more lines.
    void deleteLinesOf(Shape shape) {
        List <Integer> deletedLines = new ArrayList<>();

        // Go through each block of the shape and check if the lines they are on are complete.
        // If so, the line of the block will be candidate for removal.
        for (Block shapeBlock : shape.getBlocks()) {
            if (lineComplete(shapeBlock.getY())) {
                deletedLines.add(shapeBlock.getY());
                // Remove from blocks all the block belonging to the same line.
                for (Iterator<Block> itr=blocks.iterator(); itr.hasNext();) {
                    Block block = itr.next();
                    if (block.getY() == shapeBlock.getY()) itr.remove();
                }
            }
        }

        // @lines_cleared is increased by the number of lines deleted.
        //linesCleared += deletedLines.size();
        goal-=deletedLines.size();
        switch (deletedLines.size()) {
            case 1: score+=40*(level+1); break;
            case 2: score+=100*(level+1); break;
            case 3: score+=300*(level+1); break;
            case 4: score+=1200*(level+1); break;
        }
        if (goal<=0) {
            ++level;
            goal+=5*level + 5;
        }

        // This applies the standard gravity found in classic DroidsWorld games - all blocks go down by the
        // amount of lines cleared
        for (Block block : blocks) {
            int count = 0;
            for (int y : deletedLines) {
                if (y > block.getY()) ++count;
            }
            block.setY(block.getY()+count) ;
        }
    }

    // Given an y line on the screen this method says if it is complete.
    // A line is complete when we found WIDTH SCREEN/WIDTH BLOCK (320/32=10 items) on the line.
    private boolean lineComplete(int y) {
        // Important is that the screen resolution should be divisable by the block_width, otherwise there would be gap
        // If the count of blocks at a line is equal to the max possible blocks for any line - the line is complete
        int count = 0;
        for (Block block : blocks) {
            if (block.getY() == y) ++count;
        }
        if (count==WORLD_WIDTH) {
            return true;
        }
        return false;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void clear() {
        blocks.clear();
        fallingShape=null;
        level = 0;
        score = 0;
        spawnNextShape();
        makeNextShapeFalling();
        state = GameState.Ready;
        goal=5;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Shape getNextShape() {
        return nextShape;
    }

    public int getLevel() {
        return level;
    }

    public Shape getFallingShape() {
        return fallingShape;
    }
}
