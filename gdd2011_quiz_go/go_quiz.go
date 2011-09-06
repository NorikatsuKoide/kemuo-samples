package main

import (
	"./answer"
	"os"
	"flag"
	"fmt"
	//"syscall"
	"bufio"
	//"strings"
	//"io/ioutil"
)

func main() {
	flag.Parse()
	if flag.NArg() != 1 {
		fmt.Printf("Usage:\n  8.out [png file path]\n")
		os.Exit(1)
	}

	fd, ret := os.Open(flag.Arg(0))
	if ret != nil {
		fmt.Printf("ioutil.ReadFile failed (%d)\n", ret)
		os.Exit(1)
	}

	count := answer.CountColor(bufio.NewReader(fd))
	fmt.Printf("count: %d\n", count)

	fd.Close()
	return
}
