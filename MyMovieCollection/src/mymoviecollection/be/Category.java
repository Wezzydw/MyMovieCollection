/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymoviecollection.be;

/**
 *
 * @author Andreas Svendsen
 */
public class Category {
    private String title;
    private int Id;

    public Category(String title)
    {
        this.title = title;
        this.Id = Id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getId()
    {
        return Id;
    }
    public String toString()
    {
        return title;
    }

    
}
