begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|utils
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
name|Stack
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

begin_comment
comment|/**  * This class uses the {@link java.text.SimpleDateFormat} class to format dates  * into strings according to given date pattern.  *<p>  * As the creation of<tt>SimpleDateFormat</tt> objects is quiet expensive and  * formating dates is used quiet fequently the objects will be cached and reused  * in subsequent calls.  *</p>  *<p>  * This implementation is thread safe as it uses {@link java.util.Stack} as a  * cache  *</p>  *   * @author Simon Willnauer  *   */
end_comment

begin_class
DECL|class|DateFormater
specifier|public
class|class
name|DateFormater
block|{
DECL|field|objectStack
specifier|private
specifier|final
name|Stack
argument_list|<
name|SimpleDateFormat
argument_list|>
name|objectStack
init|=
operator|new
name|Stack
argument_list|<
name|SimpleDateFormat
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|formater
specifier|private
specifier|static
specifier|final
name|DateFormater
name|formater
init|=
operator|new
name|DateFormater
argument_list|()
decl_stmt|;
comment|/**      * Date format as it is used in Http Last modified header (Tue, 15 Nov 1994      * 12:45:26 GMT)      */
DECL|field|HTTP_HEADER_DATE_FORMAT
specifier|public
specifier|final
specifier|static
name|String
name|HTTP_HEADER_DATE_FORMAT
init|=
literal|"EEE, d MMM yyyy HH:mm:ss z"
decl_stmt|;
comment|/**      *  Date format as it is used in Http Last modified header (Tue, 15 Nov 1994      * 12:45:26 +0000)      */
DECL|field|HTTP_HEADER_DATE_FORMAT_TIME_OFFSET
specifier|public
specifier|final
specifier|static
name|String
name|HTTP_HEADER_DATE_FORMAT_TIME_OFFSET
init|=
literal|"EEE, d MMM yyyy HH:mm:ss Z"
decl_stmt|;
DECL|method|DateFormater
specifier|protected
name|DateFormater
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      * Formats the given Date into the given date pattern.      *       * @param date -      *            the date to format      * @param format -      *            date pattern      * @return - the string representation of the given<tt>Date</tt>      *         according to the given pattern      */
DECL|method|formatDate
specifier|public
specifier|static
name|String
name|formatDate
parameter_list|(
specifier|final
name|Date
name|date
parameter_list|,
name|String
name|format
parameter_list|)
block|{
if|if
condition|(
name|date
operator|==
literal|null
operator|||
name|format
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"given parameters must not be null"
argument_list|)
throw|;
name|SimpleDateFormat
name|inst
init|=
name|formater
operator|.
name|getFormater
argument_list|()
decl_stmt|;
name|inst
operator|.
name|applyPattern
argument_list|(
name|format
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|inst
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
block|}
finally|finally
block|{
name|formater
operator|.
name|returnFomater
argument_list|(
name|inst
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Parses the given string into one of the specified formates      * @param date - the string to parse      * @param formates - formates      * @return a {@link Date} instance representing the given string      * @throws ParseException - if the string can not be parsed      */
DECL|method|parseDate
specifier|public
specifier|static
name|Date
name|parseDate
parameter_list|(
specifier|final
name|String
name|date
parameter_list|,
specifier|final
name|String
modifier|...
name|formates
parameter_list|)
throws|throws
name|ParseException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|formates
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
return|return
name|parseDate
argument_list|(
name|date
argument_list|,
name|formates
index|[
name|i
index|]
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
comment|//
block|}
block|}
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Unparseable date: "
operator|+
name|date
argument_list|,
literal|0
argument_list|)
throw|;
block|}
comment|/**      * Parses the given string into the specified formate      * @param dateString - the string to parse      * @param pattern - the expected formate      * @return a {@link Date} instance representing the given string      * @throws ParseException - if the string can not be parsed      */
DECL|method|parseDate
specifier|public
specifier|static
name|Date
name|parseDate
parameter_list|(
specifier|final
name|String
name|dateString
parameter_list|,
name|String
name|pattern
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|dateString
operator|==
literal|null
operator|||
name|pattern
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"given parameters must not be null"
argument_list|)
throw|;
name|SimpleDateFormat
name|inst
init|=
name|formater
operator|.
name|getFormater
argument_list|()
decl_stmt|;
try|try
block|{
name|inst
operator|.
name|applyPattern
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
return|return
name|inst
operator|.
name|parse
argument_list|(
name|dateString
argument_list|)
return|;
block|}
finally|finally
block|{
name|formater
operator|.
name|returnFomater
argument_list|(
name|inst
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getFormater
specifier|protected
name|SimpleDateFormat
name|getFormater
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|objectStack
operator|.
name|empty
argument_list|()
condition|)
block|{
name|SimpleDateFormat
name|dateFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|DateFormater
operator|.
name|HTTP_HEADER_DATE_FORMAT
argument_list|,
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
name|dateFormat
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
name|dateFormat
return|;
block|}
return|return
name|this
operator|.
name|objectStack
operator|.
name|pop
argument_list|()
return|;
block|}
DECL|method|returnFomater
specifier|protected
name|void
name|returnFomater
parameter_list|(
specifier|final
name|SimpleDateFormat
name|format
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|objectStack
operator|.
name|size
argument_list|()
operator|<=
literal|25
condition|)
name|this
operator|.
name|objectStack
operator|.
name|push
argument_list|(
name|format
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

