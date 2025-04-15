var numbers = [1, 2, 3, 4]
var summation = (List::reduce(numbers, 0) -> numb, sumSoFar:
  numb + sumSoFar
)
Sys.println(summation)