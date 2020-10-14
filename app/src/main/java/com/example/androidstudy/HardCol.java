package com.example.androidstudy;
import java.io.FileDescriptor;

public class HardCol {
    //gpio
    public native static boolean GpioInit(int portIndex,int port);
    public native static int GpioSetDirection(int portIndex,boolean gpioDirection);
    public native static void GpioClose(int portIndex);
    public native static int GpioGetValue(int portIndex);
    public native static void GpioSetValue(int portIndex,int value);
    public native static FileDescriptor SerialOpen(String path,int baudrate,int flags);
    public native static void SerialClose();

    static {
        System.loadLibrary("gpio-lib");
        System.loadLibrary("serial-lib");
    }
}
