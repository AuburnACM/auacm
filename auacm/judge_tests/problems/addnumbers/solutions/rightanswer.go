// The correct answer in golang.
package main

import "fmt"

func main() {
	for {
		var x1, x2 int
		_, err := fmt.Scanln(&x1, &x2)
		if err != nil {
			break
		}
		fmt.Println(x1 + x2)
	}
}