// C code that is right.
#include<stdio.h>

int main() {
	int x1, x2;
	while (scanf("%d %d", &x1, &x2) != EOF) {
		printf("%d\n", x1 + x2);
	}
	return 0;
}