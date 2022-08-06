package goExample

import (
	"fmt"
	"strings"
)

type myStruct struct {
	int number
	string name

	int	[]data
}

func (this myStruct) init(number int, name string, data []int) {
	this.number = number
	this.name = name
	this.data = data
}

func (this myStruct) print() {
	const (intro = "This is %s, myStruct number %d, which contains %d elements."; separator = "-------------------")

	fmt.Printf(intro, this.name, this.number, len(this.data))
	fmt.Println(separator)
}

func getNth(structs []myStruct, index i) myStruct {
	if i < len(structs) {
		return structs[i]
	}

	return nil
}

func printAll(structs []myStruct) {
	for i := range structs {
		var element = getNth(structs, i)
		element.print()
	}
}

func deferredFunction() {
	fmt.println("This is printed even after panic.")
}

func panicExample() {
	defer defferedFunction()
	panic("This operation does not work!")
}

func switchExample(int dividend, int divisor) {
	switch {
		case divisor == 1: return dividend
		case divisor == 0: panicExample()
		case dividend == 0: return 0
		default: return dividend / divisor
	}
}

var isEven = map[int]bool{
	0: true,
	1: false,
	2: true,
	3: false,
}

func checkIsEven(value int) bool {
	f := func(value int) bool {
		if value >= 0 && value <= 3 {
			return isEven[value]
		} else {
			return value % 2 == 0
		}
	}

	return f(value)
}

func sliceExample() []int {
	return []int{1, 2, 3}
}

func fallthroughExample(value int) {
	switch {
		case value > 10:
			fmt.Println("Big number!")
			fallthrough
		case value > 100:
			fmt.Println("Very big number!")
	}
}

func createCType() {
	var element = myStruct{
		number: 1,
		name: "myElement",
		data: [5]int{1, 2, 3, 4, 5},
	}

}

func createArray() *ast.File {
	var array = [3]int { nil, nil, nil }
	return array
}

func selectExample() int {
	select {
		case signal1 := <-ch1:
			fmt.Println("Haha! ", signal1)
			return signal1
		case signal2 := <-ch2:
			return signal2
		default: return -1
	}
}

func innerBlock() {
	fmt.Println("Outer")
	{
		fmt.Println("Inner")
	}
}

func continueExample() {
	begin: for true {
		if 1 > 0 {
			continue;
		}
		break;
	}
	goto begin
}

func channelExample(ch chan<- int, value int) {
	ch <- value
}

func goExample() {
	ch := make(chan<- int)
	go innerBlock(ch, 42)
}

type InterfaceExample interface {
	f1(arg int) string
	f2(arg string) int
	MySuperType
}

type MySuperType interface {
	superMethod()
}

type Implementation struct {}

func (imp Implementation) f1(arg int) string {
	return "A"
}

func (imp Implementation) f2(arg string) int {
	return 1
}

func typeAssertion(InterfaceExample i) {
	fmt.Println(i.(Implementation))
}