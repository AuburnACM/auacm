// golang runtime errors
package main

func main() {
	arr := make([]int, 10)
	for i:=0; i<15; i++ {
		_ = arr[i]
	}
}

