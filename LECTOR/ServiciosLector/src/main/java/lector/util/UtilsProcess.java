/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package lector.util;

/**
 *
 * @author jorodriguez
 */
public interface UtilsProcess {
    
    static Integer castToInt(String val){
        try{
            return Integer.parseInt(val);
        }catch(NumberFormatException e){
            return 0;
        }
    }
    
    static String castToStri(Integer val){
        try{
            return String.valueOf(val);
        }catch(Exception e){
            return "";
        }
    }
    
}
