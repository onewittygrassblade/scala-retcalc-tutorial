package retcalc

object NbOfMonthsSavingApp extends App {
  println(strMain(args))

  def strMain(args: Array[String]): String = {
    val (from +: until +: Nil) = args(0).split(",").toList
    val nbOfYearsInRetirement  = args(1).toInt

    val returns = Returns.fromEquityAndInflationData(
      EquityData.fromResource("sp500.tsv"),
      InflationData.fromResource("cpi.tsv")
    )

    RetCalc
      .nbOfMonthsSaving(
        returns = returns.fromUntil(from, until),
        params = RetCalcParams(
          nbOfMonthsInRetirement = nbOfYearsInRetirement * 12,
          netIncome = args(2).toInt,
          monthlyExpenses = args(3).toInt,
          initialCapital = args(4).toInt
        )
      ) match {
      case Right(nbOfMonthsSaving) =>
        s"Time needed to save before retirement: $nbOfMonthsSaving months (${nbOfMonthsSaving / 12} years)"
      case Left(err) => err.message
    }
  }
}
