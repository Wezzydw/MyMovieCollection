/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.bll;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andreas Svendsen
 */
public class Player
{

    /**
     * Denne metode åbner VLC playeren igennem programmet, så der herved kan
     * afspilles film ved brug af VLC.
     */
    public void callVlc(String filePath)
    {
        String qq = "C:\\Program Files\\VideoLAN\\VLC\\vlc.exe";
        String test = "\"" + qq + "\"";
        String l = "\\\\wezzy\\Film\\Avengers.Infinity.War.2018.NORDiC.REMUX.1080p.BluRay.AVC.DTS-HD.MA.5.1-CDB\\Avengers.Infinity.War.2018.NORDiC.REMUX.1080p.BluRay.AVC.DTS-HD.MA.5.1-CDB.mkv";
        try
        {
            Process p = Runtime.getRuntime().exec(test + filePath);
            System.out.println(filePath);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Afspiller film med den givne filePath ved brug af system default player
     *
     * @param filePath
     */
    public void openDefaultProgram(String filePath)
    {
        File f = new File(filePath);
        if (f.exists())
        {
            try
            {
                Desktop.getDesktop().open(f);
            } catch (IOException ex)
            {
                System.out.println("Error");
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
