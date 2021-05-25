package retcalc

import org.scalactic.{Equality, TolerantNumerics, TypeCheckedTripleEquals}
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RetCalcSpec extends AnyWordSpec with Matchers with TypeCheckedTripleEquals with EitherValues {

  implicit val doubleEquality: Equality[Double] =
    TolerantNumerics.tolerantDoubleEquality(0.0001)

  "RetCalc.futureCapital" should {
    "calculate the amount of savings after n months" in {
      val actual = RetCalc
        .futureCapital(
          returns = FixedReturns(0.04),
          nbOfMonths = 25 * 12,
          netIncome = 3000,
          monthlyExpenses = 2000,
          initialCapital = 10000
        )
        .value

      val expected = 541267.1990
      actual should ===(expected)
    }

    "calculate the amount of savings left after having lived off savings for n months" in {
      val actual = RetCalc
        .futureCapital(
          returns = FixedReturns(0.04),
          nbOfMonths = 40 * 12,
          netIncome = 0,
          monthlyExpenses = 2000,
          initialCapital = 541267.1990
        )
        .value

      val expected = 309867.53176
      actual should ===(expected)
    }
  }

  "RetCalc.simulatePlan" should {
    val params = RetCalcParams(
      nbOfMonthsInRetirement = 40 * 12,
      netIncome = 3000,
      monthlyExpenses = 2000,
      initialCapital = 10000
    )

    "calculate the capital at retirement and after death" in {
      val (capitalAtRetirement, capitalAfterDeath) =
        RetCalc.simulatePlan(returns = FixedReturns(0.04), params, nbOfMonthsSaving = 25 * 12).value

      capitalAtRetirement should ===(541267.1990)
      capitalAfterDeath should ===(309867.5316)
    }

    "use different returns for saving and retirement" in {
      val nbOfMonthsSaving = 25 * 12

      val returns = VariableReturns(
        Vector.tabulate(nbOfMonthsSaving + params.nbOfMonthsInRetirement)(i =>
          if (i < nbOfMonthsSaving)
            VariableReturn(i.toString, 0.04 / 12)
          else
            VariableReturn(i.toString, 0.03 / 12)
        )
      )

      val (capitalAtRetirement, capitalAfterDeath) =
        RetCalc.simulatePlan(returns, params, nbOfMonthsSaving).value

      capitalAtRetirement should ===(541267.1990)
      capitalAfterDeath should ===(-57737.7227)
    }
  }

  "RetCalc.nbOfMonthsSaving" should {
    "calculate for how many months I need to save in order to live off savings for n years" in {
      val params = RetCalcParams(
        nbOfMonthsInRetirement = 40 * 12,
        netIncome = 3000,
        monthlyExpenses = 2000,
        initialCapital = 10000
      )
      val actual = RetCalc.nbOfMonthsSaving(FixedReturns(0.04), params).value

      val expected = 23 * 12 + 1
      actual should ===(expected)
    }

    "not crash if the result is very high" in {
      val params = RetCalcParams(
        nbOfMonthsInRetirement = 60 * 12,
        netIncome = 3000,
        monthlyExpenses = 2999,
        initialCapital = 0
      )
      val actual = RetCalc.nbOfMonthsSaving(FixedReturns(0.01), params).value

      val expected = 8657
      actual should ===(expected)
    }

    "return a meaningful error if a result cannot be obtained" in {
      val params = RetCalcParams(
        nbOfMonthsInRetirement = 40 * 12,
        netIncome = 1000,
        monthlyExpenses = 2000,
        initialCapital = 10000
      )
      val actual = RetCalc.nbOfMonthsSaving(FixedReturns(0.04), params).left.value

      actual should ===(RetCalcError.MoreExpensesThanIncome(1000, 2000))
    }
  }
}
