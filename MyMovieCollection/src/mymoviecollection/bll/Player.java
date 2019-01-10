/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.bll;

import java.io.IOException;

/**
 *
 * @author Andreas Svendsen
 */

public class Player {
  /**
   * Mangler at blive tilkoblet knap
   */
    public void callVlc(String filePath){
               try {
        Process p = Runtime.getRuntime().exec("\"C:\\Program Files\\VideoLAN\\VLC\\vlc.exe\"");
    } catch (IOException e){
        e.printStackTrace();
    }
    
}}

