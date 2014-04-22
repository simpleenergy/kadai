// Copyright 2012 Atlassian PTY LTD
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

name := "kadai"

description := "bootstrap tools for a Scala project"

val specs2Version = "2.3.10-scalaz-7.1.0-M6"

libraryDependencies in ThisBuild ++= Seq(
  "org.specs2"                        %% "specs2-core"       % specs2Version  % "test"
, "org.specs2"                        %% "specs2-junit"      % specs2Version  % "test"
, "org.specs2"                        %% "specs2-scalacheck" % specs2Version  % "test"
, "org.scalacheck"                    %% "scalacheck"        % "1.11.3" % "test"
, "junit"                              % "junit"             % "4.11"   % "test"
)
