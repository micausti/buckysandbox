name := "untitled"

version := "0.1"

scalaVersion := "2.12.10"

val buckyVersion = "2.0.0-M10"

libraryDependencies ++= Seq(
  "com.itv"                    %% "bucky-core"              % buckyVersion,
  "com.itv"                    %% "bucky-circe"             % buckyVersion,            //for circe based marshallers/unmarshallers
  "com.itv"                    %% "bucky-argonaut"          % buckyVersion,            //for argonaut based marhsallers/unmarshallers
  "com.itv"                    %% "bucky-test"              % buckyVersion % "test,it", //optional
"com.itv"                    %% "bucky-kamon"             % buckyVersion,            //optional
)
