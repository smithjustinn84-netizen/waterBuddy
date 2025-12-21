# Kover Report Rules

## Overview

Gemini is permitted and encouraged to access Kover coverage reports to assist with coverage analysis
and reporting.

## Report Locations

- **HTML Report:** `composeApp/build/reports/kover/html/index.html`
- **XML Report:** `composeApp/build/reports/kover/report.xml`

## Instructions

1. **Accessing Reports:** These reports may be ignored by default because they reside in the
   `build/` directory. Gemini has been granted explicit permission via `.aiexclude` to read these
   files.
2. **Analysis:** When asked for coverage stats, Gemini should prioritize reading the `report.xml`
   file for precise data if available.
3. **Troubleshooting:** If the files are still inaccessible, Gemini should remind the user to run
   `./gradlew koverXmlReport` or `./gradlew koverHtmlReport` to ensure the files are generated.
