package com.example.androidstudy.Led;

import  com.example.androidstudy.HardCol;

public class LedCol implements Runnable{

    private int fd;

    public LedCol()
    {
        LedInit();
    }

    private int LedInit()
    {
        fd =HardCol.ledInit();
        return fd;
    }

    public  boolean LedOn()
    {
        HardCol.ledOn(fd);
        return true;
    }

    public boolean LedOff()
    {
        HardCol.ledOff(fd);
        return true;
    }

    public void run()
    {
        int status=0;
        while(true)
        {
            status++;
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e)
            {
                e.getStackTrace();
            }
            if(status%2==0) LedOn();
            else LedOff();
        }
    }


}
