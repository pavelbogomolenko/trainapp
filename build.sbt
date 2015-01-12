organization  := "bp.tainapp"

name := """trainapp"""

version := "1.0"

scalaVersion := "2.11.1"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature")

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

//resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= {
  val akkaV = "2.3.6"
  val sprayV = "1.3.2"
  Seq(
    "io.spray"					%%	"spray-can"			%	sprayV,
    "io.spray"					%%	"spray-routing"		%	sprayV,
    "io.spray"					%%	"spray-testkit"		%	sprayV  % "test",
    "io.spray"					%%	"spray-json"		%	"1.3.1",
    "com.typesafe.akka"			%%	"akka-actor"		%	akkaV,
    "com.typesafe.akka"			%%	"akka-testkit"		%	akkaV   % "test",
    "com.typesafe.akka"			%%  "akka-slf4j"		% 	akkaV,
    "org.specs2"				%%	"specs2-core"		%	"2.3.11" % "test",
    "com.github.nscala-time" 	%%	"nscala-time"		% 	"1.6.0",
    "org.reactivemongo" 		%%	"reactivemongo" 	%	"0.10.5.0.akka23",
    "ch.qos.logback"			%   "logback-classic"	% 	"1.0.13",
    "commons-codec" 			%	"commons-codec"		% 	"1.10",
    "com.typesafe"				%	"config"			%	"1.2.1"
  )
}

EclipseKeys.withSource := true

unmanagedClasspath in Runtime += baseDirectory.value / "src/main/resources"

Revolver.settings