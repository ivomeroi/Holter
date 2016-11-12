#include <xc.h>

#pragma config FOSC = XT        
#pragma config WDTE = OFF       
#pragma config PWRTE = OFF      
#pragma config MCLRE = ON       
#pragma config CP = OFF         
#pragma config CPD = OFF        
#pragma config BOREN = OFF      
#pragma config IESO = ON        
#pragma config FCMEN = ON       
#pragma config LVP = ON         
#pragma config BOR4V = BOR40V 
#pragma config WRT = OFF

int cont=0;
char resizq;
char resder;
char datoizq;
char datoder;

void interrupt ISR(void) {
    if (INTCONbits.T0IF) {
        TMR0=96;
        cont++;
            
        if (cont==15)
        {
            cont=0;
            ADCON0bits.GO=1;
        }
        
        INTCONbits.T0IF=0;
    }    
    
    if (PIR1bits.ADIF) {
        PIR1bits.ADIF=0;
        INTCONbits.GIE=1;
        
        resizq=ADRESH;
        resder=ADRESL;
                
        datoizq=(resizq<<2)+(resder>>6);
        datoder=(resder|0b11000000);
            
        while(!TRMT) {
        }
                
        TXREG=datoizq;
                
        while(!TRMT) {
        }
                
        TXREG=datoder;
    }
}

void main(void) {   
    SPBRG=12;
    BRGH=1;
    BRG16=0;
    SYNC=0;
    SPEN=1;
    TXEN=1;
    
    TRISA=0b00000100;
    ANSEL=0b00000100;
    OPTION_REG=0b00001000;
    INTCON=0b11100000;
    ADCON0=0b01001001;
    ADCON1=0b10000000;
    PIE1=0b01000000;
    TMR0=96;
    
    while(1) {
    }

    return;
}