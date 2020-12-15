/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myriad.auto2.utility;

import javafx.scene.Node;
import javafx.scene.control.Button;

/**
 *
 * @author nshegoka
 */
public class UIUtility {
    
    public enum CSS {
    SUCCESS,
    INFORMATION, 
    WARNING,
    DANGER,
    PRIMARY,
    INFO   
    }

    public UIUtility() {
    }

  
    public static Button updateGraphics(Button button, CSS css)
    {
        button.setStyle(getStyle(css));
        return button;
    }
    
    public static Button updateGraphics(Button button,Node graphics, CSS css)
    {
        button.setGraphic(graphics);
        button.setStyle(getStyle(css));
        return button;
    }
    
    
    public static Button createButton(String text,Node graphics, CSS css ){
        Button button =new Button(text, graphics);
        button.setStyle(getStyle(css));
        return button;
    }
    
    public static String getStyle(CSS css)
    {        
        String cssString="";
        switch (css){
            case DANGER:{
                cssString="-fx-background-color:#c9302c;fx-border-color: #ac2925;";
                break;
            }
            case INFORMATION:{
                cssString="-fx-background-color:#5bc0de;fx-border-color: #46b8da;";
                break;
            }
            case SUCCESS:{
                cssString="-fx-background-color:#5cb85c;fx-border-color: #4cae4c;";
                break;
            }
            case WARNING:{
                cssString="-fx-background-color:#ec971f;fx-border-color: #d58512;";
                break;
            }
            case PRIMARY:{
                cssString="-fx-background-color:#337ab7;fx-border-color: #2e6da4;";
                break;
            }           
        }
        return cssString;
    }
    
    
    
}
