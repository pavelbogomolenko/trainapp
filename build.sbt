organization  := "bp.tainapp"

name := """trainapp"""

version := "1.0"

scalaVersion := "2.11.1"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= {
  val akkaV = "2.3.6"
  val sprayV = "1.3.2"
  Seq(
    "io.spray"					%%	"spray-can"		%	sprayV,
    "io.spray"					%%	"spray-routing"	%	sprayV,
    "io.spray"					%%	"spray-testkit"	%	sprayV  % "test",
    "io.spray"					%%	"spray-json"	%	"1.3.1",
    "com.typesafe.akka"			%%	"akka-actor"	%	akkaV,
    "com.typesafe.akka"			%%	"akka-testkit"	%	akkaV   % "test",
    "org.specs2"				%%	"specs2-core"	%	"2.3.11" % "test",
    "com.github.nscala-time" 	%%	"nscala-time"	% 	"1.6.0",
    "org.reactivemongo" 		%%	"reactivemongo" %	"0.11.0-SNAPSHOT"
  )
}

EclipseKeys.withSource := true

Revolver.settings