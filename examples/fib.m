fun fib(n) {
  if (n < 2) n else fib(n - 1) + fib(n - 2)
}

Sys.println(fib(35))