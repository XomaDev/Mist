var texts = ["aa", "b", "aaa"]
var sorted = (List::sort(texts) -> first, second:
  first.len() < second.len()
)
Sys.println(sorted)