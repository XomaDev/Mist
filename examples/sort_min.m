var texts = ["aa", "b", "aaa"]
var sorted = (List::sortMin(texts) -> first, second:
  first.len() < second.len()
)
Sys.println(sorted)