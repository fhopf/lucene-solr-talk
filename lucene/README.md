This project contains the code for indexing and searching using Lucene. The folder data contains sample data (Talk descriptions and Slides) that are used for indexing.

To index the talk descriptions in the default folder /tmp/myindex run
`gradle indexProperties`

To index the slides with Apache Tika run 
`gradle indexSlides`

and to make the library available for running the webapp run
`gradle install`
