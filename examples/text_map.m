var text = "India Indonesia"
var myMap = {"Ind":"[Ind]", "Indonesia":"[Indonesia]"}
var replaced = text.replaceFromDict(myMap, false)
Sys.println(replaced)