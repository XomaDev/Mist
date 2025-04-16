var text = "India Indonesia"
var myMap = {"Ind":"[Ind]", "Indonesia":"[Indonesia]"}
var replaced = text.replaceFromDict(myMap, true)
Sys.println(replaced)