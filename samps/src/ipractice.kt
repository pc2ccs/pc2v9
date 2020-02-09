
//
// File:    ipractice.kt
// Purpose: to sum the positive integers from stdin
// Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
//

fun main(args: Array<String>) {

   var inval = readLine()!!.toInt()

   while (inval != 0) {

      var sum = 0;

      if (inval < 1)
      {
         for (i in 1 downTo inval )
         {
                sum += i
         }
      }
      else
      {
         for (i in 1 .. (inval))
         {
                sum += i
         }
      }

      println(java.lang.String.format("N = %-3d    Sum = %d", inval, sum))

      inval = readLine()!!.toInt()
   }
}
