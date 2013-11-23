package kadai.hash

trait StringUtils {
  implicit class StringPadOps(s: String) {
    def padLeft(l: Int, c: Char): String =
      (l - s.length) match {
        case 0 => s
        case d =>
          val b = new StringBuilder(l)
          @annotation.tailrec def loop(i: Int) {
            if (i > 0) {
              b.append(c)
              loop(i - 1)
            }
          }
          loop(d)
          b.append(s)
          b.toString
      }
  }
}