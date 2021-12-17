[![CI](https://github.com/onewittygrassblade/scala-retcalc-tutorial/actions/workflows/ci.yml/badge.svg)](https://github.com/onewittygrassblade/scala-retcalc-tutorial/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/onewittygrassblade/scala-retcalc-tutorial/branch/main/graph/badge.svg?token=4GKYLP49CP)](https://codecov.io/gh/onewittygrassblade/scala-retcalc-tutorial)

# Retirement calculator - Scala tutorial

This tutorial is from chapters 2 and 3 of the the [Scala Programming Projects](https://github.com/PacktPublishing/Scala-Programming-Projects) book.

This code includes minor changes to the original tutorial to account for deprecated configuration and syntax.

## Code overview

The entire code is packaged within the `retcalc` package.

### RetCalc.futureCapital

This function outputs the owned capital after a number of months of saving. It is equivalent to the FV function in Excel.

Prototype:
```scala
def futureCapital(
      returns: Returns,
      nbOfMonths: Int,
      netIncome: Int,
      monthlyExpenses: Int,
      initialCapital: Double
  ): Either[RetCalcError, Double]
```
where the `Returns` trait is either a fixed or variable return.

### RetCalc.simulatePlan

This function makes use of `futureCapital` to output the owned capital after:
1. Saving money for a number of months
2. Living off savings for another number of months

Prototype:
```scala
def simulatePlan(
      returns: Returns,
      params: RetCalcParams,
      nbOfMonthsSaving: Int
  ): Either[RetCalcError, (Double, Double)]
```

where `RetCalcParams` encapsulate common parameters:
```scala
case class RetCalcParams(
      nbOfMonthsInRetirement: Int,
      netIncome: Int,
      monthlyExpenses: Int,
      initialCapital: Double
)
````

### RetCalc.nbOfMonthsSaving

This function outputs the minimum number of months needed to save money in order to live off savings for a number of months.

Prototype:
```scala
def nbOfMonthsSaving(
      returns: Returns,
      params: RetCalcParams
  ): Either[RetCalcError, Int]
```

If the input parameters are such that no result can be obtained (expenses greater than income), it returns a ` Left(RetCalcError.MoreExpensesThanIncome)`.

### Returns.fromEquityAndInflationData

This function returns a `VariableReturns` generated from equity and inflation data. The objective is to obtain real returns based on existing data following the formula:
```latex
realReturn_n = (price_n + dividends_n)/price_n-1 - inflation_n/inflation_n-1
```

Prototype:
```scala
def fromEquityAndInflationData(
      equities: Vector[EquityData],
      inflations: Vector[InflationData]
  ): VariableReturns
```
where
```scala
case class EquityData(monthId: String, value: Double, annualDividend: Double)
```
and
```scala
case class InflationData(monthId: String, value: Double)
```
can both be generated from a tsv file using the corresponding `fromResource` function.

### Returns.monthlyRate

This function returns the monthly return rate at a given month for a set of fixed or variable returns.

Prototype:
```scala
def monthlyRate(returns: Returns, month: Int): Either[RetCalcError, Double]
```

If the specified month is not defined in the returns, it returns a `Left(RetCalcError.ReturnMonthOutOfBounds)`.