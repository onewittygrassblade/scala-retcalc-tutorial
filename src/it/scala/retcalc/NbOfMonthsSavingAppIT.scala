package retcalc

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class NbOfMonthsSavingAppIT extends AnyWordSpec with Matchers with TypeCheckedTripleEquals {
  "NbOfMonthsSavingApp.strMain" should {
    "simulate a retirement plan using market returns" in {
      val actual = NbOfMonthsSavingApp.strMain(
        Array("1952.09,2017.09", "40", "3000", "2000", "10000")
      )

      val expected = "Time needed to save before retirement: 246 months (20 years)"
      actual should ===(expected)
    }
  }
}
