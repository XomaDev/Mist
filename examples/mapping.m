var countries = ["Japan", "India", "South Korea"]
var reversedCountries = (List::map(countries) -> country:
  country.reverse()
)
Sys.println(reversedCountries)