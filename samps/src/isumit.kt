
//
// File:    isumit.kt
// Purpose: to sum the positive integers from stdin
// Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
//

fun main(args: Array<String>) {
   var sum : Int = 0

   var T = -1
   while (T != 0) {
      T = readLine()!!.toInt()
      if (T > 0) {
         sum += T
      }
   }
   print("The sum of the integers is $sum\n")
}
