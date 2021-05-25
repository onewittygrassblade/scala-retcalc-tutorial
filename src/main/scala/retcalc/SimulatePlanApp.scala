package retcalc

object SimulatePlanApp extends App {
  println(strMain(args))

  def strMain(args: Array[String]): String = {
    val (from +: until +: Nil) = args(0).split(",").toList
    val nbOfYearsSaving        = args(1).toInt
    val nbOfYearsInRetirement  = args(2).toInt

    val returns = Returns.fromEquityAndInflationData(
      EquityData.fromResource("sp500.tsv"),
      InflationData.fromResource("cpi.tsv")
    )

    val (capitalAtRetirement, capitalAfterDeath) =
      RetCalc.simulatePlan(
        returns = returns.fromUntil(from, until),
        params = RetCalcParams(
          nbOfMonthsInRetirement = nbOfYearsInRetirement * 12,
          netIncome = args(3).toInt,
          monthlyExpenses = args(4).toInt,
          initialCapital = args(5).toInt
        ),
        nbOfMonthsSaving = nbOfYearsSaving * 12
      )

    s"""
      |Capital after $nbOfYearsSaving years of savings: ${capitalAtRetirement.round}
      |Capital after $nbOfYearsInRetirement years of retirement: ${capitalAfterDeath.round}
      |""".stripMargin
  }
}
