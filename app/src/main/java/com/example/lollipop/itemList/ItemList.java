package com.example.lollipop.itemList;

public class ItemList
{
    private String searched;
    private int idImage;

    public ItemList(String searched, int delIm)
    {
        this.searched = searched;
        idImage = delIm;
    }

    public String getSearched()
    {
        return  searched;
    }

    public int getIdImage()
    {
        return idImage;
    }
}
