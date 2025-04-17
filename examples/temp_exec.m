val myDict = {
  "people":[{
    "first_name": "Tim",
    "last_name": "Beaver"
  }, {
    "first_name": "John",
    "last_name": "Smith"
  }, {
    "first_name": "Jane",
    "last_name": "Doe"
  }]
}

Sys.println(myDict.walk(["people", 1, Dict.walkAll]))