scalacOptions ++= Seq(
  "-deprecation"
, "-encoding", "UTF-8" // yes, this is 2 args
, "-unchecked"
, "-Xfatal-warnings"
, "-Xlint"
, "-Yno-adapted-args"
, "-Ywarn-all"
, "-Ywarn-dead-code" // N.B. doesn't work well with the ??? hole
, "-Ywarn-numeric-widen"
, "-Ywarn-value-discard"     
)
