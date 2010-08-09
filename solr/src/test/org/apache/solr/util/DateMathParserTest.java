begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LocalizedTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|DateMathParser
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_comment
comment|/**  * Tests that the functions in DateMathParser  */
end_comment

begin_class
DECL|class|DateMathParserTest
specifier|public
class|class
name|DateMathParserTest
extends|extends
name|LocalizedTestCase
block|{
DECL|field|UTC
specifier|public
specifier|static
name|TimeZone
name|UTC
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"UTC"
argument_list|)
decl_stmt|;
comment|/**    * A formatter for specifying every last nuance of a Date for easy    * refernece in assertion statements    */
DECL|field|fmt
specifier|private
name|DateFormat
name|fmt
decl_stmt|;
comment|/**    * A parser for reading in explicit dates that are convinient to type    * in a test    */
DECL|field|parser
specifier|private
name|DateFormat
name|parser
decl_stmt|;
DECL|method|DateMathParserTest
specifier|public
name|DateMathParserTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|fmt
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"G yyyyy MM ww WW DD dd F E aa HH hh mm ss SSS z Z"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|setTimeZone
argument_list|(
name|UTC
argument_list|)
expr_stmt|;
name|parser
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss.SSS"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setTimeZone
argument_list|(
name|UTC
argument_list|)
expr_stmt|;
block|}
comment|/** MACRO: Round: parses s, rounds with u, fmts */
DECL|method|r
specifier|protected
name|String
name|r
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|u
parameter_list|)
throws|throws
name|Exception
block|{
name|Date
name|d
init|=
name|parser
operator|.
name|parse
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|Calendar
name|c
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|UTC
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|c
operator|.
name|setTime
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|DateMathParser
operator|.
name|round
argument_list|(
name|c
argument_list|,
name|u
argument_list|)
expr_stmt|;
return|return
name|fmt
operator|.
name|format
argument_list|(
name|c
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
comment|/** MACRO: Add: parses s, adds v u, fmts */
DECL|method|a
specifier|protected
name|String
name|a
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|v
parameter_list|,
name|String
name|u
parameter_list|)
throws|throws
name|Exception
block|{
name|Date
name|d
init|=
name|parser
operator|.
name|parse
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|Calendar
name|c
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|UTC
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|c
operator|.
name|setTime
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|DateMathParser
operator|.
name|add
argument_list|(
name|c
argument_list|,
name|v
argument_list|,
name|u
argument_list|)
expr_stmt|;
return|return
name|fmt
operator|.
name|format
argument_list|(
name|c
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
comment|/** MACRO: Expected: parses s, fmts */
DECL|method|e
specifier|protected
name|String
name|e
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|fmt
operator|.
name|format
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
name|s
argument_list|)
argument_list|)
return|;
block|}
DECL|method|assertRound
specifier|protected
name|void
name|assertRound
parameter_list|(
name|String
name|e
parameter_list|,
name|String
name|i
parameter_list|,
name|String
name|u
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|ee
init|=
name|e
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|String
name|rr
init|=
name|r
argument_list|(
name|i
argument_list|,
name|u
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ee
operator|+
literal|" != "
operator|+
name|rr
operator|+
literal|" round:"
operator|+
name|i
operator|+
literal|":"
operator|+
name|u
argument_list|,
name|ee
argument_list|,
name|rr
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAdd
specifier|protected
name|void
name|assertAdd
parameter_list|(
name|String
name|e
parameter_list|,
name|String
name|i
parameter_list|,
name|int
name|v
parameter_list|,
name|String
name|u
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|ee
init|=
name|e
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|String
name|aa
init|=
name|a
argument_list|(
name|i
argument_list|,
name|v
argument_list|,
name|u
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ee
operator|+
literal|" != "
operator|+
name|aa
operator|+
literal|" add:"
operator|+
name|i
operator|+
literal|"+"
operator|+
name|v
operator|+
literal|":"
operator|+
name|u
argument_list|,
name|ee
argument_list|,
name|aa
argument_list|)
expr_stmt|;
block|}
DECL|method|assertMath
specifier|protected
name|void
name|assertMath
parameter_list|(
name|String
name|e
parameter_list|,
name|DateMathParser
name|p
parameter_list|,
name|String
name|i
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|ee
init|=
name|e
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|String
name|aa
init|=
name|fmt
operator|.
name|format
argument_list|(
name|p
operator|.
name|parseMath
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ee
operator|+
literal|" != "
operator|+
name|aa
operator|+
literal|" math:"
operator|+
name|parser
operator|.
name|format
argument_list|(
name|p
operator|.
name|getNow
argument_list|()
argument_list|)
operator|+
literal|":"
operator|+
name|i
argument_list|,
name|ee
argument_list|,
name|aa
argument_list|)
expr_stmt|;
block|}
DECL|method|testCalendarUnitsConsistency
specifier|public
name|void
name|testCalendarUnitsConsistency
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|input
init|=
literal|"2001-07-04T12:08:56.235"
decl_stmt|;
for|for
control|(
name|String
name|u
range|:
name|DateMathParser
operator|.
name|CALENDAR_UNITS
operator|.
name|keySet
argument_list|()
control|)
block|{
try|try
block|{
name|r
argument_list|(
name|input
argument_list|,
name|u
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"no logic for rounding: "
operator|+
name|u
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|a
argument_list|(
name|input
argument_list|,
literal|1
argument_list|,
name|u
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"no logic for rounding: "
operator|+
name|u
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testRound
specifier|public
name|void
name|testRound
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|input
init|=
literal|"2001-07-04T12:08:56.235"
decl_stmt|;
name|assertRound
argument_list|(
literal|"2001-07-04T12:08:56.000"
argument_list|,
name|input
argument_list|,
literal|"SECOND"
argument_list|)
expr_stmt|;
name|assertRound
argument_list|(
literal|"2001-07-04T12:08:00.000"
argument_list|,
name|input
argument_list|,
literal|"MINUTE"
argument_list|)
expr_stmt|;
name|assertRound
argument_list|(
literal|"2001-07-04T12:00:00.000"
argument_list|,
name|input
argument_list|,
literal|"HOUR"
argument_list|)
expr_stmt|;
name|assertRound
argument_list|(
literal|"2001-07-04T00:00:00.000"
argument_list|,
name|input
argument_list|,
literal|"DAY"
argument_list|)
expr_stmt|;
name|assertRound
argument_list|(
literal|"2001-07-01T00:00:00.000"
argument_list|,
name|input
argument_list|,
literal|"MONTH"
argument_list|)
expr_stmt|;
name|assertRound
argument_list|(
literal|"2001-01-01T00:00:00.000"
argument_list|,
name|input
argument_list|,
literal|"YEAR"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddZero
specifier|public
name|void
name|testAddZero
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|input
init|=
literal|"2001-07-04T12:08:56.235"
decl_stmt|;
for|for
control|(
name|String
name|u
range|:
name|DateMathParser
operator|.
name|CALENDAR_UNITS
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertAdd
argument_list|(
name|input
argument_list|,
name|input
argument_list|,
literal|0
argument_list|,
name|u
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAdd
specifier|public
name|void
name|testAdd
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|input
init|=
literal|"2001-07-04T12:08:56.235"
decl_stmt|;
name|assertAdd
argument_list|(
literal|"2001-07-04T12:08:56.236"
argument_list|,
name|input
argument_list|,
literal|1
argument_list|,
literal|"MILLISECOND"
argument_list|)
expr_stmt|;
name|assertAdd
argument_list|(
literal|"2001-07-04T12:08:57.235"
argument_list|,
name|input
argument_list|,
literal|1
argument_list|,
literal|"SECOND"
argument_list|)
expr_stmt|;
name|assertAdd
argument_list|(
literal|"2001-07-04T12:09:56.235"
argument_list|,
name|input
argument_list|,
literal|1
argument_list|,
literal|"MINUTE"
argument_list|)
expr_stmt|;
name|assertAdd
argument_list|(
literal|"2001-07-04T13:08:56.235"
argument_list|,
name|input
argument_list|,
literal|1
argument_list|,
literal|"HOUR"
argument_list|)
expr_stmt|;
name|assertAdd
argument_list|(
literal|"2001-07-05T12:08:56.235"
argument_list|,
name|input
argument_list|,
literal|1
argument_list|,
literal|"DAY"
argument_list|)
expr_stmt|;
name|assertAdd
argument_list|(
literal|"2001-08-04T12:08:56.235"
argument_list|,
name|input
argument_list|,
literal|1
argument_list|,
literal|"MONTH"
argument_list|)
expr_stmt|;
name|assertAdd
argument_list|(
literal|"2002-07-04T12:08:56.235"
argument_list|,
name|input
argument_list|,
literal|1
argument_list|,
literal|"YEAR"
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseStatelessness
specifier|public
name|void
name|testParseStatelessness
parameter_list|()
throws|throws
name|Exception
block|{
name|DateMathParser
name|p
init|=
operator|new
name|DateMathParser
argument_list|(
name|UTC
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|p
operator|.
name|setNow
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"2001-07-04T12:08:56.235"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|e
init|=
name|fmt
operator|.
name|format
argument_list|(
name|p
operator|.
name|parseMath
argument_list|(
literal|""
argument_list|)
argument_list|)
decl_stmt|;
name|Date
name|trash
init|=
name|p
operator|.
name|parseMath
argument_list|(
literal|"+7YEARS"
argument_list|)
decl_stmt|;
name|trash
operator|=
name|p
operator|.
name|parseMath
argument_list|(
literal|"/MONTH"
argument_list|)
expr_stmt|;
name|trash
operator|=
name|p
operator|.
name|parseMath
argument_list|(
literal|"-5DAYS+20MINUTES"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|sleep
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|String
name|a
init|=
name|fmt
operator|.
name|format
argument_list|(
name|p
operator|.
name|parseMath
argument_list|(
literal|""
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"State of DateMathParser changed"
argument_list|,
name|e
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseMath
specifier|public
name|void
name|testParseMath
parameter_list|()
throws|throws
name|Exception
block|{
name|DateMathParser
name|p
init|=
operator|new
name|DateMathParser
argument_list|(
name|UTC
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|p
operator|.
name|setNow
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"2001-07-04T12:08:56.235"
argument_list|)
argument_list|)
expr_stmt|;
comment|// No-Op
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// simple round
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:56.000"
argument_list|,
name|p
argument_list|,
literal|"/SECOND"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:00.000"
argument_list|,
name|p
argument_list|,
literal|"/MINUTE"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T12:00:00.000"
argument_list|,
name|p
argument_list|,
literal|"/HOUR"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T00:00:00.000"
argument_list|,
name|p
argument_list|,
literal|"/DAY"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-01T00:00:00.000"
argument_list|,
name|p
argument_list|,
literal|"/MONTH"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-01-01T00:00:00.000"
argument_list|,
name|p
argument_list|,
literal|"/YEAR"
argument_list|)
expr_stmt|;
comment|// simple addition
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:56.236"
argument_list|,
name|p
argument_list|,
literal|"+1MILLISECOND"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:57.235"
argument_list|,
name|p
argument_list|,
literal|"+1SECOND"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T12:09:56.235"
argument_list|,
name|p
argument_list|,
literal|"+1MINUTE"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T13:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"+1HOUR"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-05T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"+1DAY"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-08-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"+1MONTH"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2002-07-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"+1YEAR"
argument_list|)
expr_stmt|;
comment|// simple subtraction
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:56.234"
argument_list|,
name|p
argument_list|,
literal|"-1MILLISECOND"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:55.235"
argument_list|,
name|p
argument_list|,
literal|"-1SECOND"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T12:07:56.235"
argument_list|,
name|p
argument_list|,
literal|"-1MINUTE"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T11:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"-1HOUR"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-03T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"-1DAY"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-06-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"-1MONTH"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2000-07-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"-1YEAR"
argument_list|)
expr_stmt|;
comment|// simple '+/-'
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"+1MILLISECOND-1MILLISECOND"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"+1SECOND-1SECOND"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"+1MINUTE-1MINUTE"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"+1HOUR-1HOUR"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"+1DAY-1DAY"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"+1MONTH-1MONTH"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"+1YEAR-1YEAR"
argument_list|)
expr_stmt|;
comment|// simple '-/+'
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"-1MILLISECOND+1MILLISECOND"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"-1SECOND+1SECOND"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"-1MINUTE+1MINUTE"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"-1HOUR+1HOUR"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"-1DAY+1DAY"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"-1MONTH+1MONTH"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2001-07-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"-1YEAR+1YEAR"
argument_list|)
expr_stmt|;
comment|// more complex stuff
name|assertMath
argument_list|(
literal|"2000-07-04T12:08:56.236"
argument_list|,
name|p
argument_list|,
literal|"+1MILLISECOND-1YEAR"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2000-07-04T12:08:57.235"
argument_list|,
name|p
argument_list|,
literal|"+1SECOND-1YEAR"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2000-07-04T12:09:56.235"
argument_list|,
name|p
argument_list|,
literal|"+1MINUTE-1YEAR"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2000-07-04T13:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"+1HOUR-1YEAR"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2000-07-05T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"+1DAY-1YEAR"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2000-08-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"+1MONTH-1YEAR"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2000-07-04T12:08:56.236"
argument_list|,
name|p
argument_list|,
literal|"-1YEAR+1MILLISECOND"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2000-07-04T12:08:57.235"
argument_list|,
name|p
argument_list|,
literal|"-1YEAR+1SECOND"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2000-07-04T12:09:56.235"
argument_list|,
name|p
argument_list|,
literal|"-1YEAR+1MINUTE"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2000-07-04T13:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"-1YEAR+1HOUR"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2000-07-05T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"-1YEAR+1DAY"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2000-08-04T12:08:56.235"
argument_list|,
name|p
argument_list|,
literal|"-1YEAR+1MONTH"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2000-07-01T00:00:00.000"
argument_list|,
name|p
argument_list|,
literal|"-1YEAR+1MILLISECOND/MONTH"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2000-07-04T00:00:00.000"
argument_list|,
name|p
argument_list|,
literal|"-1YEAR+1SECOND/DAY"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2000-07-04T00:00:00.000"
argument_list|,
name|p
argument_list|,
literal|"-1YEAR+1MINUTE/DAY"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2000-07-04T13:00:00.000"
argument_list|,
name|p
argument_list|,
literal|"-1YEAR+1HOUR/HOUR"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2000-07-05T12:08:56.000"
argument_list|,
name|p
argument_list|,
literal|"-1YEAR+1DAY/SECOND"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2000-08-04T12:08:56.000"
argument_list|,
name|p
argument_list|,
literal|"-1YEAR+1MONTH/SECOND"
argument_list|)
expr_stmt|;
comment|// "tricky" cases
name|p
operator|.
name|setNow
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"2006-01-31T17:09:59.999"
argument_list|)
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2006-02-28T17:09:59.999"
argument_list|,
name|p
argument_list|,
literal|"+1MONTH"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2008-02-29T17:09:59.999"
argument_list|,
name|p
argument_list|,
literal|"+25MONTH"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2006-02-01T00:00:00.000"
argument_list|,
name|p
argument_list|,
literal|"/MONTH+35DAYS/MONTH"
argument_list|)
expr_stmt|;
name|assertMath
argument_list|(
literal|"2006-01-31T17:10:00.000"
argument_list|,
name|p
argument_list|,
literal|"+3MILLIS/MINUTE"
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseMathExceptions
specifier|public
name|void
name|testParseMathExceptions
parameter_list|()
throws|throws
name|Exception
block|{
name|DateMathParser
name|p
init|=
operator|new
name|DateMathParser
argument_list|(
name|UTC
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|p
operator|.
name|setNow
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
literal|"2001-07-04T12:08:56.235"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|badCommands
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|badCommands
operator|.
name|put
argument_list|(
literal|"/"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|badCommands
operator|.
name|put
argument_list|(
literal|"+"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|badCommands
operator|.
name|put
argument_list|(
literal|"-"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|badCommands
operator|.
name|put
argument_list|(
literal|"/BOB"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|badCommands
operator|.
name|put
argument_list|(
literal|"+SECOND"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|badCommands
operator|.
name|put
argument_list|(
literal|"-2MILLI/"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|badCommands
operator|.
name|put
argument_list|(
literal|" +BOB"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|badCommands
operator|.
name|put
argument_list|(
literal|"+2SECONDS "
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|badCommands
operator|.
name|put
argument_list|(
literal|"/4"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|badCommands
operator|.
name|put
argument_list|(
literal|"?SECONDS"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|command
range|:
name|badCommands
operator|.
name|keySet
argument_list|()
control|)
block|{
try|try
block|{
name|Date
name|out
init|=
name|p
operator|.
name|parseMath
argument_list|(
name|command
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Didn't generate ParseException for: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Wrong pos for: "
operator|+
name|command
operator|+
literal|" => "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|badCommands
operator|.
name|get
argument_list|(
name|command
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|,
name|e
operator|.
name|getErrorOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

