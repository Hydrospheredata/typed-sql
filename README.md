### Typed-sql

Dsl library for writing [doobie](https://github.com/tpolecat/doobie) queries in a typesafe way.

Example:
```scala
import doobie._
import doobie.implicits._
import doobie.syntax._

import typed.sql.syntax._

// Declare tables
case class Row1(
  a: Int,
  b: String,
  c: String
)

case class Row2(
  f1: Int,
  f2: String,
  f3: String
)

val table1 = Table.of[Row1].name('test)
val table2 = Table.of[Row2].name('test2)

// Selects

val s1 = select(*).from(table1)
val q1: Query0[Row1] = s1.toQuery
//s1.toQuery.to[List] return doobie.ConnectionIO[List[Row1]]

val a1 = table1.col('a)
val b1 = table1.col('b)

val s2 = select(a1, b1).from(table1)
val q2: Query[(Int, String)] = s2.toQuery

// Where

val s3 = select(*).from(table1).where(a1 > 5)
val s4 = select(*).from(table1).where(a1 > 5 and b like "foo%")

// Joins
val f1 = table2.col('f1)

val s5 = select(*).from(table1 innerJoin table2 on a1 <==> f1)
val q5: Query[(Row1, Row2)] = s5.toQuery
```

WIP:
- [ ] ORDER BY
- [ ] INSERT
- [ ] UPDATE
- [ ] DELETE

