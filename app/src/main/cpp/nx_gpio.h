//------------------------------------------------------------------------------
//
//  Copyright (C) 2013 Nexell Co. All Rights Reserved
//  Nexell Co. Proprietary & Confidential
//
//  NEXELL INFORMS THAT THIS CODE AND INFORMATION IS PROVIDED "AS IS" BASE
//  AND WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING
//  BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
//  FOR A PARTICULAR PURPOSE.
//
//  Module      :
//  File        :
//  Description :
//  Author      :
//  Export      :
//  History     :
//
//------------------------------------------------------------------------------

#ifndef	__NX_GPIO_H__
#define	__NX_GPIO_H__

#include <stdbool.h>
#include <stdint.h>
#include <pthread.h>

typedef void *NX_GPIO_HANDLE;

enum NX_GPIO {
    GPIO_ERROR = -1,

    // GPIOA0 - GPIOA31 ( 0 - 31 )
            GPIOA0,	 GPIOA1,  GPIOA2,  GPIOA3,  GPIOA4,  GPIOA5,  GPIOA6,  GPIOA7,
    GPIOA8,  GPIOA9,  GPIOA10, GPIOA11, GPIOA12, GPIOA13, GPIOA14, GPIOA15,
    GPIOA16, GPIOA17, GPIOA18, GPIOA19, GPIOA20, GPIOA21, GPIOA22, GPIOA23,
    GPIOA24, GPIOA25, GPIOA26, GPIOA27, GPIOA28, GPIOA29, GPIOA30, GPIOA31,

    // GPIOB0 - GPIOB31 ( 32 - 63 )
            GPIOB0,	 GPIOB1,  GPIOB2,  GPIOB3,  GPIOB4,  GPIOB5,  GPIOB6,  GPIOB7,
    GPIOB8,  GPIOB9,  GPIOB10, GPIOB11, GPIOB12, GPIOB13, GPIOB14, GPIOB15,
    GPIOB16, GPIOB17, GPIOB18, GPIOB19, GPIOB20, GPIOB21, GPIOB22, GPIOB23,
    GPIOB24, GPIOB25, GPIOB26, GPIOB27, GPIOB28, GPIOB29, GPIOB30, GPIOB31,

    // GPIOC0 - GPIOC31 ( 64 - 95 )
            GPIOC0,	 GPIOC1,  GPIOC2,  GPIOC3,  GPIOC4,  GPIOC5,  GPIOC6,  GPIOC7,
    GPIOC8,  GPIOC9,  GPIOC10, GPIOC11, GPIOC12, GPIOC13, GPIOC14, GPIOC15,
    GPIOC16, GPIOC17, GPIOC18, GPIOC19, GPIOC20, GPIOC21, GPIOC22, GPIOC23,
    GPIOC24, GPIOC25, GPIOC26, GPIOC27, GPIOC28, GPIOC29, GPIOC30, GPIOC31,

    // GPIOD0 - GPIOD31 ( 96 - 127 )
            GPIOD0,	 GPIOD1,  GPIOD2,  GPIOD3,  GPIOD4,  GPIOD5,  GPIOD6,  GPIOD7,
    GPIOD8,  GPIOD9,  GPIOD10, GPIOD11, GPIOD12, GPIOD13, GPIOD14, GPIOD15,
    GPIOD16, GPIOD17, GPIOD18, GPIOD19, GPIOD20, GPIOD21, GPIOD22, GPIOD23,
    GPIOD24, GPIOD25, GPIOD26, GPIOD27, GPIOD28, GPIOD29, GPIOD30, GPIOD31,

    // GPIOE0 - GPIOE31 ( 128 - 159 )
            GPIOE0,	 GPIOE1,  GPIOE2,  GPIOE3,  GPIOE4,  GPIOE5,  GPIOE6,  GPIOE7,
    GPIOE8,  GPIOE9,  GPIOE10, GPIOE11, GPIOE12, GPIOE13, GPIOE14, GPIOE15,
    GPIOE16, GPIOE17, GPIOE18, GPIOE19, GPIOE20, GPIOE21, GPIOE22, GPIOE23,
    GPIOE24, GPIOE25, GPIOE26, GPIOE27, GPIOE28, GPIOE29, GPIOE30, GPIOE31,

    // ALIVE0 - ALIVE7 ( 160 - 167)
            ALIVE0,	 ALIVE1,  ALIVE2,  ALIVE3,  ALIVE4,  ALIVE5,  ALIVE6,  ALIVE7,

    GPIO_MAX,
};

enum NX_GPIO_DIRECTION {
    GPIO_DIRECTION_IN,
    GPIO_DIRECTION_OUT,
};

enum NX_GPIO_EDGE {
    GPIO_EDGE_NONE,
    GPIO_EDGE_FALLING,
    GPIO_EDGE_RIGING,
    GPIO_EDGE_BOTH,
};



#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
    int32_t			port;			// gpio number
    int32_t 		direction;		// gpio direction
    int32_t			bPost;
    pthread_mutex_t hLock;
} NX_GPIO_HANDLE_INFO;

NX_GPIO_HANDLE	NX_GpioInit		( int32_t nGpio );
void			NX_GpioDeinit	( NX_GPIO_HANDLE hGpio );

int32_t			NX_GpioDirection( NX_GPIO_HANDLE hGpio, int32_t direction );

int32_t			NX_GpioSetValue	( NX_GPIO_HANDLE hGpio, int32_t value );
int32_t			NX_GpioGetValue	( NX_GPIO_HANDLE hGpio );

int32_t			NX_GPioSetEdge	( NX_GPIO_HANDLE hGpio, int32_t edge );
int32_t 		NX_GpioGetInterrupt ( NX_GPIO_HANDLE hGpio );
int32_t 		NX_GpioPostInterrupt( NX_GPIO_HANDLE hGpio );

#ifdef __cplusplus
}
#endif

#endif	//	__NX_GPIO_H__
