begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Locale
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|PrefixQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TermRangeQuery
import|;
end_import

begin_comment
comment|/**  * Provides support for converting dates to strings and vice-versa.  * The strings are structured so that lexicographic sorting orders   * them by date, which makes them suitable for use as field values   * and search terms.  *   *<P>This class also helps you to limit the resolution of your dates. Do not  * save dates with a finer resolution than you really need, as then  * {@link TermRangeQuery} and {@link PrefixQuery} will require more memory and become slower.  *   *<P>  * Another approach is {@link LongPoint}, which indexes the  * values in sorted order.  * For indexing a {@link Date} or {@link Calendar}, just get the unix timestamp as  *<code>long</code> using {@link Date#getTime} or {@link Calendar#getTimeInMillis} and  * index this as a numeric value with {@link LongPoint}  * and use {@link org.apache.lucene.search.PointRangeQuery} to query it.  */
end_comment

begin_class
DECL|class|DateTools
specifier|public
class|class
name|DateTools
block|{
DECL|field|GMT
specifier|final
specifier|static
name|TimeZone
name|GMT
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
decl_stmt|;
DECL|field|TL_CAL
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|Calendar
argument_list|>
name|TL_CAL
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Calendar
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Calendar
name|initialValue
parameter_list|()
block|{
return|return
name|Calendar
operator|.
name|getInstance
argument_list|(
name|GMT
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|//indexed by format length
DECL|field|TL_FORMATS
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|SimpleDateFormat
index|[]
argument_list|>
name|TL_FORMATS
init|=
operator|new
name|ThreadLocal
argument_list|<
name|SimpleDateFormat
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|SimpleDateFormat
index|[]
name|initialValue
parameter_list|()
block|{
name|SimpleDateFormat
index|[]
name|arr
init|=
operator|new
name|SimpleDateFormat
index|[
name|Resolution
operator|.
name|MILLISECOND
operator|.
name|formatLen
operator|+
literal|1
index|]
decl_stmt|;
for|for
control|(
name|Resolution
name|resolution
range|:
name|Resolution
operator|.
name|values
argument_list|()
control|)
block|{
name|arr
index|[
name|resolution
operator|.
name|formatLen
index|]
operator|=
operator|(
name|SimpleDateFormat
operator|)
name|resolution
operator|.
name|format
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
return|return
name|arr
return|;
block|}
block|}
decl_stmt|;
comment|// cannot create, the class has static methods only
DECL|method|DateTools
specifier|private
name|DateTools
parameter_list|()
block|{}
comment|/**    * Converts a Date to a string suitable for indexing.    *     * @param date the date to be converted    * @param resolution the desired resolution, see    *  {@link #round(Date, DateTools.Resolution)}    * @return a string in format<code>yyyyMMddHHmmssSSS</code> or shorter,    *  depending on<code>resolution</code>; using GMT as timezone     */
DECL|method|dateToString
specifier|public
specifier|static
name|String
name|dateToString
parameter_list|(
name|Date
name|date
parameter_list|,
name|Resolution
name|resolution
parameter_list|)
block|{
return|return
name|timeToString
argument_list|(
name|date
operator|.
name|getTime
argument_list|()
argument_list|,
name|resolution
argument_list|)
return|;
block|}
comment|/**    * Converts a millisecond time to a string suitable for indexing.    *     * @param time the date expressed as milliseconds since January 1, 1970, 00:00:00 GMT    * @param resolution the desired resolution, see    *  {@link #round(long, DateTools.Resolution)}    * @return a string in format<code>yyyyMMddHHmmssSSS</code> or shorter,    *  depending on<code>resolution</code>; using GMT as timezone    */
DECL|method|timeToString
specifier|public
specifier|static
name|String
name|timeToString
parameter_list|(
name|long
name|time
parameter_list|,
name|Resolution
name|resolution
parameter_list|)
block|{
specifier|final
name|Date
name|date
init|=
operator|new
name|Date
argument_list|(
name|round
argument_list|(
name|time
argument_list|,
name|resolution
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|TL_FORMATS
operator|.
name|get
argument_list|()
index|[
name|resolution
operator|.
name|formatLen
index|]
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
block|}
comment|/**    * Converts a string produced by<code>timeToString</code> or    *<code>dateToString</code> back to a time, represented as the    * number of milliseconds since January 1, 1970, 00:00:00 GMT.    *     * @param dateString the date string to be converted    * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT    * @throws ParseException if<code>dateString</code> is not in the     *  expected format     */
DECL|method|stringToTime
specifier|public
specifier|static
name|long
name|stringToTime
parameter_list|(
name|String
name|dateString
parameter_list|)
throws|throws
name|ParseException
block|{
return|return
name|stringToDate
argument_list|(
name|dateString
argument_list|)
operator|.
name|getTime
argument_list|()
return|;
block|}
comment|/**    * Converts a string produced by<code>timeToString</code> or    *<code>dateToString</code> back to a time, represented as a    * Date object.    *     * @param dateString the date string to be converted    * @return the parsed time as a Date object     * @throws ParseException if<code>dateString</code> is not in the     *  expected format     */
DECL|method|stringToDate
specifier|public
specifier|static
name|Date
name|stringToDate
parameter_list|(
name|String
name|dateString
parameter_list|)
throws|throws
name|ParseException
block|{
try|try
block|{
return|return
name|TL_FORMATS
operator|.
name|get
argument_list|()
index|[
name|dateString
operator|.
name|length
argument_list|()
index|]
operator|.
name|parse
argument_list|(
name|dateString
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Input is not a valid date string: "
operator|+
name|dateString
argument_list|,
literal|0
argument_list|)
throw|;
block|}
block|}
comment|/**    * Limit a date's resolution. For example, the date<code>2004-09-21 13:50:11</code>    * will be changed to<code>2004-09-01 00:00:00</code> when using    *<code>Resolution.MONTH</code>.     *     * @param resolution The desired resolution of the date to be returned    * @return the date with all values more precise than<code>resolution</code>    *  set to 0 or 1    */
DECL|method|round
specifier|public
specifier|static
name|Date
name|round
parameter_list|(
name|Date
name|date
parameter_list|,
name|Resolution
name|resolution
parameter_list|)
block|{
return|return
operator|new
name|Date
argument_list|(
name|round
argument_list|(
name|date
operator|.
name|getTime
argument_list|()
argument_list|,
name|resolution
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Limit a date's resolution. For example, the date<code>1095767411000</code>    * (which represents 2004-09-21 13:50:11) will be changed to     *<code>1093989600000</code> (2004-09-01 00:00:00) when using    *<code>Resolution.MONTH</code>.    *     * @param resolution The desired resolution of the date to be returned    * @return the date with all values more precise than<code>resolution</code>    *  set to 0 or 1, expressed as milliseconds since January 1, 1970, 00:00:00 GMT    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"fallthrough"
argument_list|)
DECL|method|round
specifier|public
specifier|static
name|long
name|round
parameter_list|(
name|long
name|time
parameter_list|,
name|Resolution
name|resolution
parameter_list|)
block|{
specifier|final
name|Calendar
name|calInstance
init|=
name|TL_CAL
operator|.
name|get
argument_list|()
decl_stmt|;
name|calInstance
operator|.
name|setTimeInMillis
argument_list|(
name|time
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|resolution
condition|)
block|{
comment|//NOTE: switch statement fall-through is deliberate
case|case
name|YEAR
case|:
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MONTH
argument_list|,
literal|0
argument_list|)
expr_stmt|;
case|case
name|MONTH
case|:
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|DAY_OF_MONTH
argument_list|,
literal|1
argument_list|)
expr_stmt|;
case|case
name|DAY
case|:
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
case|case
name|HOUR
case|:
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
case|case
name|MINUTE
case|:
name|calInstance
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
case|case
name|SECOND
case|:
name|calInstance
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
case|case
name|MILLISECOND
case|:
comment|// don't cut off anything
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown resolution "
operator|+
name|resolution
argument_list|)
throw|;
block|}
return|return
name|calInstance
operator|.
name|getTimeInMillis
argument_list|()
return|;
block|}
comment|/** Specifies the time granularity. */
DECL|enum|Resolution
specifier|public
specifier|static
enum|enum
name|Resolution
block|{
comment|/** Limit a date's resolution to year granularity. */
DECL|enum constant|YEAR
name|YEAR
argument_list|(
literal|4
argument_list|)
block|,
comment|/** Limit a date's resolution to month granularity. */
DECL|enum constant|MONTH
name|MONTH
argument_list|(
literal|6
argument_list|)
block|,
comment|/** Limit a date's resolution to day granularity. */
DECL|enum constant|DAY
name|DAY
argument_list|(
literal|8
argument_list|)
block|,
comment|/** Limit a date's resolution to hour granularity. */
DECL|enum constant|HOUR
name|HOUR
argument_list|(
literal|10
argument_list|)
block|,
comment|/** Limit a date's resolution to minute granularity. */
DECL|enum constant|MINUTE
name|MINUTE
argument_list|(
literal|12
argument_list|)
block|,
comment|/** Limit a date's resolution to second granularity. */
DECL|enum constant|SECOND
name|SECOND
argument_list|(
literal|14
argument_list|)
block|,
comment|/** Limit a date's resolution to millisecond granularity. */
DECL|enum constant|MILLISECOND
name|MILLISECOND
argument_list|(
literal|17
argument_list|)
block|;
DECL|field|formatLen
specifier|final
name|int
name|formatLen
decl_stmt|;
DECL|field|format
specifier|final
name|SimpleDateFormat
name|format
decl_stmt|;
comment|//should be cloned before use, since it's not threadsafe
DECL|method|Resolution
name|Resolution
parameter_list|(
name|int
name|formatLen
parameter_list|)
block|{
name|this
operator|.
name|formatLen
operator|=
name|formatLen
expr_stmt|;
comment|// formatLen 10's place:                     11111111
comment|// formatLen  1's place:            12345678901234567
name|this
operator|.
name|format
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyyMMddHHmmssSSS"
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|formatLen
argument_list|)
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
expr_stmt|;
name|this
operator|.
name|format
operator|.
name|setTimeZone
argument_list|(
name|GMT
argument_list|)
expr_stmt|;
block|}
comment|/** this method returns the name of the resolution      * in lowercase (for backwards compatibility) */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

