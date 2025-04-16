Sys.println(List.fromCsvRow("India, Japan, Germany"))
Sys.println(List.fromCsvTable("India, Japan, Germany\nGermany, Japan, India"))

Sys.println(List.fromCsvRow("India, Japan, Germany").toCsvRow())
Sys.println()
Sys.println(List.fromCsvTable("India, Japan, Germany\nGermany, Japan, India").toCsvTable())