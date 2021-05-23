package retcalc

import org.scalactic.{Equality, TolerantNumerics, TypeCheckedTripleEquals}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RetCalcSpec extends AnyWordSpec with Matchers with TypeCheckedTripleEquals {

  implicit val doubleEquality: Equality[Double] =
    TolerantNumerics.tolerantDoubleEquality(0.0001)

  "RetCalc.futureCapital" should {
    "calculate the amount of savings in n months" in {
      val actual = RetCalc.futureCapital(
        monthlyInterestRate = 0.04 / 12,
        nbOfMonths = 25 * 12,
        netIncome = 3000,
        monthlyExpenses = 2000,
        initialCapital = 10000
      )

      val expected = 541267.1990
      actual should ===(expected)
    }

    "calculate the amount of savings left after having lived off savings for n months" in {
      val actual = RetCalc.futureCapital(
        monthlyInterestRate = 0.04 / 12,
        nbOfMonths = 40 * 12,
        netIncome = 0,
        monthlyExpenses = 2000,
        initialCapital = 541267.1990
      )

      val expected = 309867.53176
      actual should ===(expected)
    }
  }

  "RetCalc.simulatePlan" should {
    "calculate the capital at retirement and after death" in {
      val (capitalAtRetirement, capitalAfterDeath) =
        RetCalc.simulatePlan(
          monthlyInterestRate = 0.04 / 12,
          nbOfMonthsSaving = 25 * 12,
          nbOfMonthsInRetirement = 40 * 12,
          netIncome = 3000,
          monthlyExpenses = 2000,
          initialCapital = 10000
        )

      capitalAtRetirement should ===(541267.1990)
      capitalAfterDeath should ===(309867.5316)
    }
  }

  "RetCalc.nbOfMonthsSaving" should {
    "calculate for how many months I need to save in order to live off savings for n years" in {
      val actual = RetCalc.nbOfMonthsSaving(
        monthlyInterestRate = 0.04 / 12,
        nbOfMonthsInRetirement = 40 * 12,
        netIncome = 3000,
        monthlyExpenses = 2000,
        initialCapital = 10000
      )

      val expected = 23 * 12 + 1
      actual should ===(expected)
    }

    "not crash if the result is very high" in {
      val actual = RetCalc.nbOfMonthsSaving(
        monthlyInterestRate = 0.01 / 12,
        nbOfMonthsInRetirement = 40 * 12,
        netIncome = 3000,
        monthlyExpenses = 2999,
        initialCapital = 0
      )

      val expected = 8280
      actual should ===(expected)
    }

    "not loop forever if a result cannot be obtained" in {
      val actual = RetCalc.nbOfMonthsSaving(
        monthlyInterestRate = 0.04 / 12,
        nbOfMonthsInRetirement = 40 * 12,
        netIncome = 1000,
        monthlyExpenses = 2000,
        initialCapital = 10000
      )

      actual should ===(Int.MaxValue)
    }
  }
}
