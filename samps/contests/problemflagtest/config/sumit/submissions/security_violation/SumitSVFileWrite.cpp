//
// File:    SumitSVFileWrite.cpp
// Purpose: to output Hello World then write to a file
// Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
//

// basic file operations
#include <iostream>
#include <fstream>
using namespace std;

int writeFile ()
{
  ofstream myfile;
  cout << "Hello World\n";
  cout << "Writing to file sample.output.file.txt\n";
  cout.flush();
  myfile.open ("sample.output.file.txt");
  myfile << "Writing this to a file.\n";
  myfile << "Writing this to a file.\n";
  myfile << "Writing this to a file.\n";
  myfile << "Writing this to a file.\n";
  myfile.close();
  return 0;
}

main ()
{
  writeFile();
}

// eof SumitSVFileWrite.cpp