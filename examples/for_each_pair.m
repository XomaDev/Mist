var capitals = {"India":"New Delhi", "Japan":"Tokyo", "South Korea":"Seoul"}
(each country::capital -> capitals:
  Sys.println(country + " -> " + capital)
)
