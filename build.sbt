name := """ebean-query-text"""

organization := "com.github.nkmrs"

version := "0.2.0"

scalaVersion := "2.11.7"

crossPaths := false
autoScalaLibrary := false

libraryDependencies ++= Seq(
  "junit"             % "junit"           % "4.12"  % "test",
  "com.novocode"      % "junit-interface" % "0.11"  % "test"
)

libraryDependencies += "com.h2database"   % "h2"                % "1.4.193" % "test"
libraryDependencies += "org.avaje.ebean"  % "ebean"             % "9.2.1"
libraryDependencies += "org.avaje.ebean"  % "ebean-agent"       % "8.2.1"   % "test"
libraryDependencies += "org.avaje"        % "avaje-agentloader" % "2.1.2"   % "test"

publishTo := Some(Resolver.file("file",file("repo")))
