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
  "com.chuusai"           %%  "shapeless"             % "1.2.4"
, "org.scalaz"            %% "scalaz-core"            % "7.0.2"
, "org.scalaz"            %% "scalaz-effect"          % "7.0.2"
, "org.scalaj"             % "scalaj-time_2.10.0-M7"  % "0.6"
)
