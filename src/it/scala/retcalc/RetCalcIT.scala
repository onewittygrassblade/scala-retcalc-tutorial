package retcalc

import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class RetCalcIT extends AnyWordSpec with EitherValues {

  implicit val doubleEquality: Equality[Double] =
    TolerantNumerics.tolerantDoubleEquality(0.0001)

  "RetCalc.simulatePlan" should {
    "simulate a retirement plan with real market data" in {
      val params = RetCalcParams(
        nbOfMonthsInRetirement = 40 * 12,
        netIncome = 3000,
        monthlyExpenses = 2000,
        initialCapital = 10000
      )

      val returns = Returns
        .fromEquityAndInflationData(
          equities = EquityData.fromResource("sp500.tsv"),
          inflations = InflationData.fromResource("cpi.tsv")
        )
        .fromUntil("1952.09", "2017.10")

      val (capitalAtRetirement, capitalAfterDeath) =
        RetCalc.simulatePlan(returns, params, nbOfMonthsSaving = 25 * 12).value
      capitalAtRetirement should ===(468924.5522)
      capitalAfterDeath should ===(2958841.7675)
    }
  }
}
