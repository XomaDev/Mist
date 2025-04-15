var countries = ["India", "Indonesia", "South Korea"]
var filteredCountries = (List::filter(countries) -> country:
  country.startsWith("Ind")
)
Sys.println(filteredCountries)