package retcalc

import cats.data.Validated.{Invalid, Valid}
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
      actual should ===(Valid(expected))
    }

    "return an error when the period exceeds the return bounds" in {
      val actual = SimulatePlanApp.strMain(
        Array("1952.09,2017.09", "25", "60", "3000", "2000", "10000")
      )

      val expected = "Cannot get the return for month 780. Accepted range: 0 to 779."
      actual should ===(Invalid(expected))
    }

    "return a usage example when the number of arguments is incorrect" in {
      val actual = SimulatePlanApp.strMain(
        Array("1952.09,2017.09", "25", "60", "3000", "2000")
      )

      val expected =
        """
          |Usage:
          |simulatePlan from,until nbOfYearsSaving nbOfYearsInRetirement netIncome monthlyExpenses initialCapital
          |
          |Example:
          |simulatePlan 1952.09,2017.09 25 40 3000 2000 10000
          |""".stripMargin

      actual should ===(Invalid(expected))
    }

    "return multiple errors when multiple arguments are invalid" in {
      val actual = SimulatePlanApp.strMain(
        Array("1952.09:2017.09", "25.0", "60", "3'000", "2000.0", "10000")
      )

      val expected =
        """Invalid format for fromUntil. Expected: from,until. Actual: 1952.09:2017.09.
          |Invalid number for nbOfYearsSaving: 25.0.
          |Invalid number for netIncome: 3'000.
          |Invalid number for monthlyExpenses: 2000.0.""".stripMargin

      actual should ===(Invalid(expected))
    }
  }
}
