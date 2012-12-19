package io.kadai

object Example extends App {
  object CFG extends CmdOpts(List("--name", "bob", "baz")) {
    // NB: lazy val effectively memoizes the result
    lazy val name = opt("--name", (x: (String, String)) => "%s and %s".format(x._1, x._2))
    lazy val all = opt("--all", TRUE)
    lazy val absent = opt("--absent", TRUE)
    override def version = opt("--version", () => "10.1.5")
    override def usage = opt("--help", () => "Some random help text here")
  }
  println(CFG.name)
  println("ALL: " + CFG.all)
  println(CFG.absent)
}
