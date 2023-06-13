#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* This program runs a loop which repeatedly invokes malloc() to allocate memory pages.
   PAGE_SZ defines the size of memory pages to allocate (request); the initial setting is 2^12 (4K).
   Variable "gb" defines the total number of GB to request; the initial value is 2 (GB).
   The main loop runs until "gb" worth of memory requests have been made; for each "PAGE_SZ" request
   that is made, if malloc() returns non-null then the program write a value into the returned page
   (to circumvent memory allocation schemes which don't actually reserve memory until it is written to).
   
   When finished requesting memory the program prints a message indicating how much memory was SUCCESSFULLY
   obtained (based on whether malloc() returned non-null or not), then enters an infinite loop 
   (to allow enforcement of a timeout mechanism).
   
   Based on a program originally written by StackOverflow user "eyal" 
   at https://stackoverflow.com/questions/1865501/c-program-on-linux-to-exhaust-memory.
   (Slight) modifications by John Clevenger.
*/
   
#define PAGE_SZ (1<<12)

int main() {
    int i;
    int gb = 2; // memory to consume in GB

    for (i = 0; i < ((unsigned long)gb<<30)/PAGE_SZ ; ++i) {
        void *m = malloc(PAGE_SZ);
        if (!m)
            break;
        memset(m, 0, 1);
    }
    printf("allocated %lu MB\n", ((unsigned long)i*PAGE_SZ)>>20);
    printf("looping...");
    while (1>0) {
    }
    //getchar();
    return 0;
}