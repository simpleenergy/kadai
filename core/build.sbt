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

name := "kadai-core"

libraryDependencies in ThisBuild ++= Seq(
  "com.chuusai"                  %%  "shapeless"         % "1.2.3"                % "provided"
, "org.scalaz"                   %% "scalaz-core"        % "7.0.0-M7"             % "provided"
, "org.scalaz"                   %% "scalaz-effect"      % "7.0.0-M7"             % "provided"
, "org.scala-tools.time"          % "scala-time"         % "2.9.1-atlassian-0.4"  % "provided"
)
