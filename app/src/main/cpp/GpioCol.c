//
// Created by root on 20-10-11.
//
#include <jni.h>
#include "nx_gpio.h"

NX_GPIO_HANDLE	gpioHandle[3];


JNIEXPORT jboolean JNICALL
Java_com_example_androidstudy_HardCol_GpioInit(JNIEnv *env, jclass clazz, jint port_index,
                                               jint port) {
    gpioHandle[port_index] = NX_GpioInit(port);


    return gpioHandle[port_index]  != NULL;
}
JNIEXPORT jint JNICALL
Java_com_example_androidstudy_HardCol_GpioSetDirection(JNIEnv *env, jclass clazz, jint port_index,
                                                       jboolean gpio_direction) {
    return NX_GpioDirection(gpioHandle[port_index],gpio_direction) == 0;
}
JNIEXPORT void JNICALL
Java_com_example_androidstudy_HardCol_GpioClose(JNIEnv *env, jclass clazz, jint port_index) {
    NX_GpioDeinit(gpioHandle[port_index]);
}
JNIEXPORT jint JNICALL
Java_com_example_androidstudy_HardCol_GpioGetValue(JNIEnv *env, jclass clazz, jint port_index) {
    return NX_GpioGetValue(gpioHandle[port_index]);
}
JNIEXPORT void JNICALL
Java_com_example_androidstudy_HardCol_GpioSetValue(JNIEnv *env, jclass clazz, jint port_index,
                                                   jint value) {
    NX_GpioSetValue(gpioHandle[port_index],value);
}

