package kadai

import org.scalacheck.Properties
import org.specs2.matcher.Parameters
import org.specs2.Specification
import org.specs2.ScalaCheck

/**
 * Base spec that allows us to check all laws
 */
trait ScalaCheckSpec extends Specification with ScalaCheck {
  def checkAll(props: Properties)(implicit p: Parameters) =
    s2"""
    ${props.name} must satisfy ${
      props.properties.map {
        case (name, prop) => s2"""    
        ${name ! check(prop)(p)} """
      }.reduce { _ append _ }
    }"""
}