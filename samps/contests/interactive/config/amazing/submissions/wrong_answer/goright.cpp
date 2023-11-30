#include <iostream>
#include <ctime>

using namespace std;

int main() {
    long sleep_msec = 50;
    long sleep_nsec = sleep_msec * 1000 * 1000;
    timespec sleep_time = { 0, sleep_nsec };
    while (1) {
        cout << "right" << endl << flush;
        nanosleep(&sleep_time, nullptr);
    }
    return 0;
}
