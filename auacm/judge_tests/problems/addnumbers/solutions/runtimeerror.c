// C code that breaks at runtime.
#include<stdio.h>

int main() {
	char *s = "Hello world";
	*s = 'H';
	return 0;
}

