name := "scheduler-example"

version := "1.0"

scalaVersion := "2.11.12"

lazy val akkaVersion = "2.3.14"
lazy val schedulerVersion = "9.1.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalatest" %% "scalatest" % "3.1.0" % Test,
  "com.pagerduty" %% "metrics-dogstatsd" % "2.1.5",
  "com.pagerduty" %% "scheduler-common" % schedulerVersion,
  "com.pagerduty" %% "scheduler-scala-api" % schedulerVersion,
  "com.pagerduty" %% "scheduler" % schedulerVersion
)
