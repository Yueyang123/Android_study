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

    public static native  int     ledInit();
    public static native  boolean ledOn(int fd);
    public static native  boolean ledOff(int fd);
    public static native  int     ledchange(int fd);

    static {
        System.loadLibrary("led-lib");
        System.loadLibrary("gpio-lib");
        System.loadLibrary("serial-lib");
    }
}
