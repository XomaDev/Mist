fun fib(n) {
  if (n < 2) n else fib(n - 1) + fib(n - 2)
}


val result = 0
do {
  for (number: 1 to 5 by 1) {
    result = fib(number) + result
  }
} -> result