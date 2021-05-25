package retcalc

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SimulatePlanAppIT extends AnyWordSpec with Matchers with TypeCheckedTripleEquals {
  "SimulatePlanApp.strMain" should {
    "simulate a retirement plan using market returns" in {
      val actual = SimulatePlanApp.strMain(
        Array("1952.09,2017.09", "25", "40", "3000", "2000", "10000")
      )

      val expected =
        """
          |Capital after 25 years of savings: 468925
          |Capital after 40 years of retirement: 2958842
          |""".stripMargin
      actual should ===(expected)
    }

    "return an error when the period exceeds the return bounds" in {
      val actual = SimulatePlanApp.strMain(
        Array("1952.09,2017.09", "25", "60", "3000", "2000", "10000")
      )

      val expected = "Cannot get the return for month 780. Accepted range: 0 to 779."
      actual should ===(expected)
    }
  }
}
