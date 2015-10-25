// C++ code that breaks at runtime.
#include<iostream>

int main() {
	char *s = "This is very similar to the C test.";
	*s = 'H';
	return 0;
}

