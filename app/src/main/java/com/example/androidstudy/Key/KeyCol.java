package com.example.androidstudy.Key;

import android.util.Log;

import com.example.androidstudy.HardCol;
import com.example.androidstudy.Gpio.GpioCol;

//GPIOC0(sw3)(index 0) GPIOC1(sw2)(index1)
public class KeyCol implements Runnable{
    private int SW3Status=1;
    private int SW2Status=1;

    public static enum KETSTATUS{
        KEY_DOWN(0),KEY_UP(1);
        private int value;
        private KETSTATUS(int value){
            this.value = value;
        }
        public int getValue(){
            return value;
        }
    }

    private GpioCol gpioCol=new GpioCol();
    public KeyCol()
    {
        gpioCol.gpioInit(0,"GPIOC0");
        gpioCol.gpioInit(1,"GPIOC1");
        gpioCol.gpioSetDirection(0,GpioCol.GpioDirection.GPIO_DIRECTION_INPUT);
        gpioCol.gpioSetDirection(1,GpioCol.GpioDirection.GPIO_DIRECTION_INPUT);
    }

    public void run()
    {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.getStackTrace();
            }

            SW3Status = gpioCol.gpioGetValue(0);
            SW2Status = gpioCol.gpioGetValue(1);

        }
    }

    public int getSW3Status()
    {
        return SW3Status;
    }

    public int getSW2Status()
    {
        return SW2Status;
    }

}
