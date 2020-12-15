/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myriad.auto2.engine;

import java.util.HashMap;

/**
 *
 * @author nshegoka
 */
public class ControllerStore {

private static HashMap<String,Object> controllers=new HashMap();

public static void addController(String name,Object object)
{
    controllers.put(name, object);
}
public static Object getController(String name){
    return controllers.get(name);
}
public static HashMap getAllControllers(){return controllers;}
}
