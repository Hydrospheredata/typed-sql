### Typed-sql

This is a frontend library for writing [doobie](https://github.com/tpolecat/doobie) queries.
Its goal to provide a typesafe-dsl for it and keep its constructs as close as it possible to the plain SQL language.

### Usage
In addition to doobie dependencies add the following one to your build.sbt:
```scala
libraryDependencies += Seq( 
  "io.hydrosphere" %% "typed-sql" % "0.1.0"
)
```

Import:
```scala
import doobie._
import doobie.implicits._
import doobie.syntax._

import typed.sql.syntax._
import typed.sql.toDoobie._
```

Declare case class for row, create table from it and columns:
```scala
case class Row(
  a: Int,
  b: String,
  c: String
)

val table = Table.of[Row].name('test)
// or if `a` column is a primary key and has serial type
val table = Table.of[Row].autoColumn('a).name('test)

val a = table.col('a)
val b = table.col('b)
val c = table.col('c)
```

Now it's time for to write queries.
Examples:
```scala
insert.into(table).values(1, "b", "c")
// or if `a` column is a primary key and has serial type
insert.into(table).values("b", "c")

select(*).from(table)
select(*).from(table).where(a === 1)
select(a, b).from(table)

update(table).set(b := "Upd B").where(a === 1)

delete.from(table).where(a === 1)
```

Convert to `Query0`/`Update0` using `toQuery`/`toUpdate`:
```scala

val q0: Query0[Row] = select(*).from(table).toQuery
// the same for update and insert
val u0: Update0 = delete.from(table).where(a === 1).toUpdate
```

#### More examples:
Where:
```scala
select(*).from(table).where(a > 1 and a < 5)
select(*).from(table).where(a >= 1 and a =< 5)
select(*).from(table).where(a === 1 or a === 2)
select(*).from(table).where(b like "BBB%")
select(*).from(table).where(a.in(NonEmptyList.of(1,2,3)))
```

Order By:
```scala
// SELECT * FROM TEST ORDER BY test.a ASC
select(*).from(table).orderBy(a)

// SELECT * FROM TEST ORDER BY test.a DESC
select(*).from(table).orderBy(a.DESC)

// SELECT * FROM TEST ORDER BY test.a ASC test.b ASC
select(*).from(table).orderBy(a, b)
```

Limit/Offset:
```scala
select(*).from(table).limit(10).offset(1)
```

Joins:
```scala
case class Row2(a: Int)
val table2 = Table.of[Row2].name('test2) 
val a2 = table2.col('a2)

// Query0[(Row1, Row2)]
select(*).from(table.innerJoin(table2).on(a1 <==> a2))
// Query0[(Row1, Option[Row2])]
select(*).from(table.leftJoin(table2).on(a1 <==> a2))
// Query0[(Option[Row1], Row2)]
select(*).from(table.rightJoin(table2).on(a1 <==> a2))
// Query0[(Option[Row1], Option[Row2])]
select(*).from(table.fullJoin(table2).on(a1 <==> a2))

// Query0[(Int, Int)]
select(a, a2).from(table.innerJoin(table2).on(a1 <==> a2))
```
Note: it's possible to join more that two tables
