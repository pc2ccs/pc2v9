#include <cstdio>
#include <cstring>

int main(void) {
    int num_cases;
    scanf("%d", &num_cases);
    for (int i = 0; i < num_cases; ++i) {
        int lo = 1, hi = 1000;
        while (true) {
            int m = (lo+hi)/2;
            printf("%d\n", m);
            //fflush(stdout);
            char res[1000];
            scanf("%s", res);
            if (!strcmp(res, "correct")) break;
            if (!strcmp(res, "lower")) hi = m-1;
            else lo = m+1;
        }
    }
    return 0;
}
