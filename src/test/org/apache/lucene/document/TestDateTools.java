begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestDateTools
specifier|public
class|class
name|TestDateTools
extends|extends
name|TestCase
block|{
DECL|method|testStringToDate
specifier|public
name|void
name|testStringToDate
parameter_list|()
throws|throws
name|ParseException
block|{
name|Date
name|d
init|=
literal|null
decl_stmt|;
name|d
operator|=
name|DateTools
operator|.
name|stringToDate
argument_list|(
literal|"2004"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2004-01-01 00:00:00:000"
argument_list|,
name|isoFormat
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|=
name|DateTools
operator|.
name|stringToDate
argument_list|(
literal|"20040705"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2004-07-05 00:00:00:000"
argument_list|,
name|isoFormat
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|=
name|DateTools
operator|.
name|stringToDate
argument_list|(
literal|"200407050910"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2004-07-05 09:10:00:000"
argument_list|,
name|isoFormat
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|=
name|DateTools
operator|.
name|stringToDate
argument_list|(
literal|"20040705091055990"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2004-07-05 09:10:55:990"
argument_list|,
name|isoFormat
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|d
operator|=
name|DateTools
operator|.
name|stringToDate
argument_list|(
literal|"97"
argument_list|)
expr_stmt|;
comment|// no date
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
comment|/* expected exception */
block|}
try|try
block|{
name|d
operator|=
name|DateTools
operator|.
name|stringToDate
argument_list|(
literal|"200401011235009999"
argument_list|)
expr_stmt|;
comment|// no date
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
comment|/* expected exception */
block|}
try|try
block|{
name|d
operator|=
name|DateTools
operator|.
name|stringToDate
argument_list|(
literal|"aaaa"
argument_list|)
expr_stmt|;
comment|// no date
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
comment|/* expected exception */
block|}
block|}
DECL|method|testStringtoTime
specifier|public
name|void
name|testStringtoTime
parameter_list|()
throws|throws
name|ParseException
block|{
name|long
name|time
init|=
name|DateTools
operator|.
name|stringToTime
argument_list|(
literal|"197001010000"
argument_list|)
decl_stmt|;
name|Calendar
name|cal
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|cal
operator|.
name|set
argument_list|(
literal|1970
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
comment|// year=1970, month=january, day=1
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// hour, minute, second
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cal
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cal
operator|.
name|getTime
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|,
name|time
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
literal|1980
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
comment|// year=1980, month=february, day=2
literal|11
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// hour, minute, second
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|time
operator|=
name|DateTools
operator|.
name|stringToTime
argument_list|(
literal|"198002021105"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cal
operator|.
name|getTime
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|,
name|time
argument_list|)
expr_stmt|;
block|}
DECL|method|testDateAndTimetoString
specifier|public
name|void
name|testDateAndTimetoString
parameter_list|()
throws|throws
name|ParseException
block|{
name|Calendar
name|cal
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|cal
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
literal|2004
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
comment|// year=2004, month=february(!), day=3
literal|22
argument_list|,
literal|8
argument_list|,
literal|56
argument_list|)
expr_stmt|;
comment|// hour, minute, second
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|333
argument_list|)
expr_stmt|;
name|String
name|dateString
decl_stmt|;
name|dateString
operator|=
name|DateTools
operator|.
name|dateToString
argument_list|(
name|cal
operator|.
name|getTime
argument_list|()
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|YEAR
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2004"
argument_list|,
name|dateString
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2004-01-01 00:00:00:000"
argument_list|,
name|isoFormat
argument_list|(
name|DateTools
operator|.
name|stringToDate
argument_list|(
name|dateString
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dateString
operator|=
name|DateTools
operator|.
name|dateToString
argument_list|(
name|cal
operator|.
name|getTime
argument_list|()
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MONTH
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"200402"
argument_list|,
name|dateString
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2004-02-01 00:00:00:000"
argument_list|,
name|isoFormat
argument_list|(
name|DateTools
operator|.
name|stringToDate
argument_list|(
name|dateString
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dateString
operator|=
name|DateTools
operator|.
name|dateToString
argument_list|(
name|cal
operator|.
name|getTime
argument_list|()
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|DAY
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"20040203"
argument_list|,
name|dateString
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2004-02-03 00:00:00:000"
argument_list|,
name|isoFormat
argument_list|(
name|DateTools
operator|.
name|stringToDate
argument_list|(
name|dateString
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dateString
operator|=
name|DateTools
operator|.
name|dateToString
argument_list|(
name|cal
operator|.
name|getTime
argument_list|()
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|HOUR
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2004020322"
argument_list|,
name|dateString
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2004-02-03 22:00:00:000"
argument_list|,
name|isoFormat
argument_list|(
name|DateTools
operator|.
name|stringToDate
argument_list|(
name|dateString
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dateString
operator|=
name|DateTools
operator|.
name|dateToString
argument_list|(
name|cal
operator|.
name|getTime
argument_list|()
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MINUTE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"200402032208"
argument_list|,
name|dateString
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2004-02-03 22:08:00:000"
argument_list|,
name|isoFormat
argument_list|(
name|DateTools
operator|.
name|stringToDate
argument_list|(
name|dateString
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dateString
operator|=
name|DateTools
operator|.
name|dateToString
argument_list|(
name|cal
operator|.
name|getTime
argument_list|()
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|SECOND
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"20040203220856"
argument_list|,
name|dateString
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2004-02-03 22:08:56:000"
argument_list|,
name|isoFormat
argument_list|(
name|DateTools
operator|.
name|stringToDate
argument_list|(
name|dateString
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dateString
operator|=
name|DateTools
operator|.
name|dateToString
argument_list|(
name|cal
operator|.
name|getTime
argument_list|()
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MILLISECOND
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"20040203220856333"
argument_list|,
name|dateString
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2004-02-03 22:08:56:333"
argument_list|,
name|isoFormat
argument_list|(
name|DateTools
operator|.
name|stringToDate
argument_list|(
name|dateString
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// date before 1970:
name|cal
operator|.
name|set
argument_list|(
literal|1961
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|,
comment|// year=1961, month=march(!), day=5
literal|23
argument_list|,
literal|9
argument_list|,
literal|51
argument_list|)
expr_stmt|;
comment|// hour, minute, second
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|444
argument_list|)
expr_stmt|;
name|dateString
operator|=
name|DateTools
operator|.
name|dateToString
argument_list|(
name|cal
operator|.
name|getTime
argument_list|()
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MILLISECOND
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"19610305230951444"
argument_list|,
name|dateString
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1961-03-05 23:09:51:444"
argument_list|,
name|isoFormat
argument_list|(
name|DateTools
operator|.
name|stringToDate
argument_list|(
name|dateString
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dateString
operator|=
name|DateTools
operator|.
name|dateToString
argument_list|(
name|cal
operator|.
name|getTime
argument_list|()
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|HOUR
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1961030523"
argument_list|,
name|dateString
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1961-03-05 23:00:00:000"
argument_list|,
name|isoFormat
argument_list|(
name|DateTools
operator|.
name|stringToDate
argument_list|(
name|dateString
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// timeToString:
name|cal
operator|.
name|set
argument_list|(
literal|1970
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
comment|// year=1970, month=january, day=1
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// hour, minute, second
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|dateString
operator|=
name|DateTools
operator|.
name|timeToString
argument_list|(
name|cal
operator|.
name|getTime
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MILLISECOND
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"19700101000000000"
argument_list|,
name|dateString
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
literal|1970
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
comment|// year=1970, month=january, day=1
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
expr_stmt|;
comment|// hour, minute, second
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|dateString
operator|=
name|DateTools
operator|.
name|timeToString
argument_list|(
name|cal
operator|.
name|getTime
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MILLISECOND
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"19700101010203000"
argument_list|,
name|dateString
argument_list|)
expr_stmt|;
block|}
DECL|method|testRound
specifier|public
name|void
name|testRound
parameter_list|()
block|{
name|Calendar
name|cal
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|cal
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
literal|2004
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
comment|// year=2004, month=february(!), day=3
literal|22
argument_list|,
literal|8
argument_list|,
literal|56
argument_list|)
expr_stmt|;
comment|// hour, minute, second
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|333
argument_list|)
expr_stmt|;
name|Date
name|date
init|=
name|cal
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"2004-02-03 22:08:56:333"
argument_list|,
name|isoFormat
argument_list|(
name|date
argument_list|)
argument_list|)
expr_stmt|;
name|Date
name|dateYear
init|=
name|DateTools
operator|.
name|round
argument_list|(
name|date
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|YEAR
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"2004-01-01 00:00:00:000"
argument_list|,
name|isoFormat
argument_list|(
name|dateYear
argument_list|)
argument_list|)
expr_stmt|;
name|Date
name|dateMonth
init|=
name|DateTools
operator|.
name|round
argument_list|(
name|date
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MONTH
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"2004-02-01 00:00:00:000"
argument_list|,
name|isoFormat
argument_list|(
name|dateMonth
argument_list|)
argument_list|)
expr_stmt|;
name|Date
name|dateDay
init|=
name|DateTools
operator|.
name|round
argument_list|(
name|date
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|DAY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"2004-02-03 00:00:00:000"
argument_list|,
name|isoFormat
argument_list|(
name|dateDay
argument_list|)
argument_list|)
expr_stmt|;
name|Date
name|dateHour
init|=
name|DateTools
operator|.
name|round
argument_list|(
name|date
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|HOUR
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"2004-02-03 22:00:00:000"
argument_list|,
name|isoFormat
argument_list|(
name|dateHour
argument_list|)
argument_list|)
expr_stmt|;
name|Date
name|dateMinute
init|=
name|DateTools
operator|.
name|round
argument_list|(
name|date
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MINUTE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"2004-02-03 22:08:00:000"
argument_list|,
name|isoFormat
argument_list|(
name|dateMinute
argument_list|)
argument_list|)
expr_stmt|;
name|Date
name|dateSecond
init|=
name|DateTools
operator|.
name|round
argument_list|(
name|date
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|SECOND
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"2004-02-03 22:08:56:000"
argument_list|,
name|isoFormat
argument_list|(
name|dateSecond
argument_list|)
argument_list|)
expr_stmt|;
name|Date
name|dateMillisecond
init|=
name|DateTools
operator|.
name|round
argument_list|(
name|date
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MILLISECOND
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"2004-02-03 22:08:56:333"
argument_list|,
name|isoFormat
argument_list|(
name|dateMillisecond
argument_list|)
argument_list|)
expr_stmt|;
comment|// long parameter:
name|long
name|dateYearLong
init|=
name|DateTools
operator|.
name|round
argument_list|(
name|date
operator|.
name|getTime
argument_list|()
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|YEAR
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"2004-01-01 00:00:00:000"
argument_list|,
name|isoFormat
argument_list|(
operator|new
name|Date
argument_list|(
name|dateYearLong
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|dateMillisecondLong
init|=
name|DateTools
operator|.
name|round
argument_list|(
name|date
operator|.
name|getTime
argument_list|()
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MILLISECOND
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"2004-02-03 22:08:56:333"
argument_list|,
name|isoFormat
argument_list|(
operator|new
name|Date
argument_list|(
name|dateMillisecondLong
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|isoFormat
specifier|private
name|String
name|isoFormat
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
name|SimpleDateFormat
name|sdf
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm:ss:SSS"
argument_list|)
decl_stmt|;
name|sdf
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sdf
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
block|}
DECL|method|testDateToolsUTC
specifier|public
name|void
name|testDateToolsUTC
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Sun, 30 Oct 2005 00:00:00 +0000 -- the last second of 2005's DST in Europe/London
name|long
name|time
init|=
literal|1130630400
decl_stmt|;
try|try
block|{
name|TimeZone
operator|.
name|setDefault
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
comment|/* "GMT" */
literal|"Europe/London"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|d1
init|=
name|DateTools
operator|.
name|dateToString
argument_list|(
operator|new
name|Date
argument_list|(
name|time
operator|*
literal|1000
argument_list|)
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MINUTE
argument_list|)
decl_stmt|;
name|String
name|d2
init|=
name|DateTools
operator|.
name|dateToString
argument_list|(
operator|new
name|Date
argument_list|(
operator|(
name|time
operator|+
literal|3600
operator|)
operator|*
literal|1000
argument_list|)
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MINUTE
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"different times"
argument_list|,
name|d1
operator|.
name|equals
argument_list|(
name|d2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"midnight"
argument_list|,
name|DateTools
operator|.
name|stringToTime
argument_list|(
name|d1
argument_list|)
argument_list|,
name|time
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"later"
argument_list|,
name|DateTools
operator|.
name|stringToTime
argument_list|(
name|d2
argument_list|)
argument_list|,
operator|(
name|time
operator|+
literal|3600
operator|)
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|TimeZone
operator|.
name|setDefault
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

