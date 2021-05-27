package retcalc

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import retcalc.RetCalcError.{InvalidArgument, InvalidNumber}

class SimulatePlanAppSpec extends AnyWordSpec with Matchers {
  "SimulatePlanApp.parseInt" should {
    "return a Valid number if the input string can be parsed" in {
      SimulatePlanApp.parseInt("foo", "123") should ===(Valid(123))
    }

    "return an Invalid with a list containing an InvalidNumber error if the input string cannot be parsed" in {
      SimulatePlanApp.parseInt("foo", "123x") should ===(
        Invalid(NonEmptyList.of(InvalidNumber("foo", "123x")))
      )
    }
  }

  "SimulatePlanApp.parseFromUntil" should {
    "return a Valid tuple of length 2 if the input string can be parsed" in {
      SimulatePlanApp.parseFromUntil("foo,bar") should ===(Valid(("foo", "bar")))
    }

    "return an Invalid with a list containing an InvalidArgument if the input string cannot be parsed" in {
      SimulatePlanApp.parseFromUntil("foo:bar") should ===(
        Invalid(NonEmptyList.of(InvalidArgument("fromUntil", "foo:bar", "from,until")))
      )
    }
  }

  "SimulatePlanApp.parseParams" should {
    "return a Valid RetCalcParams if the input array can be parsed" in {
      SimulatePlanApp.parseParams(Array("", "", "40", "3000", "2000", "10000")) should ===(
        Valid(
          RetCalcParams(
            nbOfMonthsInRetirement = 40 * 12,
            netIncome = 3000,
            monthlyExpenses = 2000,
            initialCapital = 10000
          )
        )
      )
    }

    "return an Invalid with a list containing InvalidArgument errors if the input array cannot be parsed" in {
      SimulatePlanApp.parseParams(Array("", "", "40.0", "3000", "2000.0", "10000")) should ===(
        Invalid(
          NonEmptyList.of(
            InvalidNumber("nbOfYearsInRetirement", "40.0"),
            InvalidNumber("monthlyExpenses", "2000.0")
          )
        )
      )
    }
  }
}
