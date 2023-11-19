
package Controller;

import Iterator.ArrayIterator;
import Model.*;
import eg.edu.alexu.csd.oop.game.GameObject;
import eg.edu.alexu.csd.oop.game.World;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;



public class ClownWorld implements World{
    private static final int maxTime = 1 * 60 * 1000;	// 1 minute
    private final long startTime = System.currentTimeMillis();
    private int score = 0;
    private  int width;
    private int height;
    private int speed;
    private final  List<GameObject> constants = new LinkedList<>();
    private final  List<GameObject> control = new LinkedList<>();
    private final  List<GameObject> moving = new LinkedList<>();
    private final List<GameObject> leftHand = new LinkedList<>();
    private final List<GameObject> rightHand = new LinkedList<>();
    
    private  Shape plateType1;
    private  Shape plateType2;
    private  Shape plateType3;
    private  Shape plateType4;
    private  Shape bomb;
    private ArrayIterator iter;
    private Factory factory =  new Factory();
    private Strategy strategy;
    
    public ClownWorld(int width,int height,Strategy strategy) throws CloneNotSupportedException{
        this.width = width;
        this.height= height;
        this.strategy = strategy;
        int count = 0;
        int count1 = 0;
        GameObject background = new PictureObject(0, 0, "gamebackground.png",width,height,false);
        int borderHeight = 10;
        int borderLenght = 300;
        int i;
        int x1 = 0,x2 = 0,x3 = 700,x4 = 700;
        int y1 = 30,y2 = 100,y3 = 30,y4 = 100;
        constants.add(background);
        constants.add(new Border( x1  ,y1   ,borderLenght , borderHeight,Color.BLACK,1));
        constants.add(new Border(x2  ,y2   , borderLenght,borderHeight,Color.BLACK,2));
        constants.add(new Border(x3,  y3  ,borderLenght,borderHeight,Color.BLACK,3));
        constants.add(new Border(x4 ,  y4  ,borderLenght,borderHeight,Color.BLACK,4));
        
        x3 = width -85;
        x4 = width -85;
        for(i=0; i<25;i++){
            count1++;
            plateType1 = (Shape) factory.getshape(x1 ,y1-10 ,"plate",1);
            plateType2 = (Shape) factory.getshape(x2 ,y2-10 ,"plate",2);
            plateType3 = (Shape) factory.getshape(x3 ,y3-10 ,"plate",3);
            plateType4 = (Shape) factory.getshape(x4 ,y4-10 ,"plate",4);            
            if(count1%4==0){
                switch (count){
                    case 0://new Shape(x1+70  , y1-40    ,"bomb.png", 40, 40,1,randomNonZeroBetween(-1, 1),false)
                        bomb = (Shape) factory.getshape(x1+70 ,y1-40 ,"bomb",1); count++;
                        break;
                    case 1:
                        bomb = (Shape) factory.getshape(x2+70 ,y2-40 ,"bomb",2); count++;
                        break;
                    case 2:
                        bomb = (Shape) factory.getshape(x3+70 ,y3-40 ,"bomb",3); count++;
                        break;
                    case 3:
                        bomb = (Shape) factory.getshape(x4+70 ,y4-40 ,"bomb",4); count=0;
                        break;    
                }
                moving.add( bomb);}
            moving.add(plateType1 );
            moving.add(plateType2);
            moving.add(plateType3 );
            moving.add(plateType4);
             x1 -= 180;
             x2 -= 180;
             x3 += 180;
             x4 += 180;
        }
        control.add(Clown.getInstance());
        
    }
    @Override
    public boolean refresh() {
        boolean timeout = System.currentTimeMillis() - startTime > maxTime; // time end and game over
        for(int i=0;i<moving.size()-1;i++){
            if(moving.get(i).getHeight() == 40){
                try{
                Shape bomb = (Shape) moving.get(i);
                Shape clone = bomb.clone() ; 
                Clown clown =  ((Clown)(control.get(0)) );
                moveShapes(bomb, bomb.getType());
                if(leftHand.isEmpty()){
                    if(clown.intersectLeft(bomb)){
                        score = 0;
                        bomb.setVisible(false);
                        recycle(clone);
                    }
                }
                else  if (intersect (bomb, leftHand.get(leftHand.size() - 1))){
                    score = 0;
                    bomb.setVisible(false);
                    recycle(clone);
                }
                if(rightHand.isEmpty()){
                    if(clown.intersectRight(bomb)){
                        score = 0;
                        bomb.setVisible(false);
                        recycle(clone);
                    }
                }
                else if (intersect (bomb, rightHand.get(rightHand.size() - 1)) ){
                    score = 0;
                    bomb.setVisible(false);
                    recycle(clone);
                }
                if(bomb.getY() == height){
                recycle(clone);
            }
             }catch(CloneNotSupportedException e){
                e.printStackTrace();
            }   
            }else{
            Shape object = (Shape) moving.get(i);
            try{
            Shape clone = object.clone() ; 
            Clown clown =  ((Clown)(control.get(0)) );
            moveShapes(object, object.getType());
            if(leftHand.isEmpty()){
                if(clown.intersectLeft(object)){
                    insertInToLeft(object, true);
                    recycle(clone);
                }
            }
            else  if (intersect (object, leftHand.get(leftHand.size() - 1))){
                insertInToLeft(object , false);
                recycle(clone);
            }
             if(rightHand.isEmpty()){
                if(clown.intersectRight(object)){
                    insertInToRight(object, true);
                    recycle(clone);
                }
            }
            else if (intersect (object, rightHand.get(rightHand.size() - 1)) ){
                insertInToRight(object, false);
                recycle(clone);
            }
//           
            }catch(CloneNotSupportedException e){
                e.printStackTrace();
            }
            checkLeftHand();
            checkRightHand();
            if(object.getY() == height){
                recycle(object);
            }
            if(rightHand.size()>=15||leftHand.size()>=15)
                return false;       
                        
    }}
          
        return !timeout;
    }
    private void recycle(GameObject object){
        Shape shape = (Shape) object;
        if(!moving.contains(object)){
            moving.add(object);
        }
        int x = 0,y;
        int height = shape.getHeight();
        if(shape.getHeight()==40){
            x=bomb.getX();  
        }
        else{
            switch(shape.getType()){
                case 1: x = plateType1.getX(); break;
                case 2: x = plateType2.getX(); break;
                case 3: x = plateType3.getX(); break;
                case 4: x = plateType4.getX(); break;}     
        }
            switch (shape.getType()){
                case 1: {
                x = x - 160;
                y = constants.get(1).getY() - height;
                object.setX(x);
                object.setY(y);
                if(shape.getHeight()==40)
                    bomb = shape;
                else
                    plateType1 = shape;
                break;
                }
                case 2:{
                x = x - 160;
                y = constants.get(2).getY() -height;
                object.setX(x);
                object.setY(y);
                if(shape.getHeight()==40)
                    bomb = shape;
                else
                    plateType2 = shape;
                break;
                }
                case 3:{
                x = x + 160;
                y = constants.get(3).getY()-height;
                object.setX(x);
                object.setY(y); 
                if(shape.getHeight()==40)
                    bomb = shape;
                else
                    plateType3 = shape;
                break;
                }
                case 4:{
                x = x +  160;
                y = constants.get(4).getY() - height;
                object.setX(x);
                object.setY(y);
                if(shape.getHeight()==40)
                    bomb = shape;
                else
                    plateType4 = shape;
                break;
                }  
                default:{                

                break;
                } }}

    private boolean intersect(GameObject object1 , GameObject object2){
        return Math.abs(   object1.getX()  - object2.getX()  ) <= object1.getWidth()  && Math.abs(object1.getY()   - object2.getY())  <= object1.getHeight() ;
    }
    private void insertInToLeft(GameObject object,boolean LeftStackEmpty){
        Shape plate = (Shape)object;
        Clown clown = (Clown) control.get(0);
        if(LeftStackEmpty){
        }
        else{
            plate.setY(leftHand.get(leftHand.size()-1).getY() - plate.getHeight());
        }
            plate.setStack(1);
            plate.setHorizontalOnly(true);
            leftHand.add(plate);
            moving.remove(plate);
            control.add(object);   
            plate.setLastStack(clown);
    }
        private void insertInToRight(GameObject object,boolean RightStackEmpty){
        Shape shape = (Shape)object;
        Clown clown = (Clown) control.get(0);
        if(RightStackEmpty){
        }
        else{
            shape.setY(rightHand.get(rightHand.size()-1).getY() - shape.getHeight());
        }
            shape.setStack(2);
            shape.setHorizontalOnly(true);
            rightHand.add(shape);
            moving.remove(shape);
            control.add(object);   
            shape.setLastStack(clown);
    }
        
     private void checkRightHand() {
        iter = new ArrayIterator(rightHand);
        int i = 0;
        while(iter.hasNext()){
            i++;
            iter.next();}
        if (i >= 3) {
            Shape first = (Shape) rightHand.get(rightHand.size() - 1);
            Shape second = (Shape) rightHand.get(rightHand.size() - 2);
            Shape third = (Shape) rightHand.get(rightHand.size() - 3);
            if (first.getPath().equals(second.getPath()) && second.getPath().equals(third.getPath())) {
                rightHand.remove(rightHand.size() - 1);
                rightHand.remove(rightHand.size() - 1);
                rightHand.remove(rightHand.size() - 1);
                control.remove(first);
                control.remove(second);
                control.remove(third);
                score++;
            }
        }
    }
  
    private void moveShapes(Shape shape,int type){ 
        switch (type) {
            case 1:{
                if(shape.getX() + strategy.getSpeed() <= ((Border)constants.get(1)).getWidth() && shape.getY() + shape.getHeight()  <= ((Border)constants.get(1)).getY()){
                    shape.setX(shape.getX() + strategy.getSpeed());
                }
                else {
                    shape.setX(shape.getX() + shape.getXChange());
                    shape.setY(shape.getY() + strategy.getSpeed());
                }
                break;
            }
            case 2:{
              if(shape.getX() + strategy.getSpeed() <= ((Border)constants.get(2)).getWidth() && shape.getY() + shape.getHeight()  <= ((Border)constants.get(2)).getY() )
                {
                    shape.setX(shape.getX() + strategy.getSpeed());
                }else {
                  shape.setX(shape.getX() + shape.getXChange());
                    shape.setY(shape.getY()+strategy.getSpeed());
                }
              break;
            }
            case 3:{
                if(shape.getX() + strategy.getSpeed() > width-((Border)constants.get(3)).getWidth()-shape.getWidth() && shape.getY() + shape.getHeight()   <= ((Border)constants.get(3)).getY() ){
                    shape.setX(shape.getX() - strategy.getSpeed());
                }
                else {
                    shape.setX(shape.getX() + shape.getXChange());
                    shape.setY(shape.getY()+strategy.getSpeed());
                }
                break;
            }    
            case 4:{
                if(shape.getX() + strategy.getSpeed() > width-((Border)constants.get(4)).getWidth()-shape.getWidth()  && shape.getY() + shape.getHeight()  <= ((Border)constants.get(4)).getY()){
                    shape.setX(shape.getX() - strategy.getSpeed());
                }
                else {
                    shape.setX(shape.getX() + shape.getXChange());
                    shape.setY(shape.getY()+strategy.getSpeed());
                }
                break;
            }
            default: { break; }
        }
    }

    private void checkLeftHand() {
        iter = new ArrayIterator(leftHand);
        int i = 0;
        while(iter.hasNext()){
            i++;
            iter.next();
        }
        if (i >= 3) {
            Shape first = (Shape) leftHand.get(leftHand.size() - 1);
            Shape second = (Shape) leftHand.get(leftHand.size() - 2);
            Shape third = (Shape) leftHand.get(leftHand.size() - 3);
            if (first.getPath().equals(second.getPath()) && second.getPath().equals(third.getPath())) {
                leftHand.remove(leftHand.size() - 1);
                leftHand.remove(leftHand.size() - 1);
                leftHand.remove(leftHand.size() - 1);
                control.remove(first);
                control.remove(second);
                control.remove(third);
                score++;
            }
        }
    }
   
    @Override
    public List<GameObject> getConstantObjects() {
        return constants;
    }

    @Override
    public List<GameObject> getMovableObjects() {
        return moving;
    }

    @Override
    public List<GameObject> getControlableObjects() {
        return control;
    }

    @Override
    public int getWidth() {
         return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public String getStatus() {
        return "Score=" + score + "   |   Time=" + Math.max(0, (maxTime - (System.currentTimeMillis() - startTime)) / 1000);	// update status
    }

    @Override
    public int getSpeed() {
        return this.speed ;
    }

    @Override
    public int getControlSpeed() {
        return 20;
    }    
}