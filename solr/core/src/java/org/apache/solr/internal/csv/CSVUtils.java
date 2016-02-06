begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.internal.csv
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|internal
operator|.
name|csv
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Utility methods for dealing with CSV files  */
end_comment

begin_class
DECL|class|CSVUtils
specifier|public
class|class
name|CSVUtils
block|{
DECL|field|EMPTY_STRING_ARRAY
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|EMPTY_STRING_ARRAY
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
DECL|field|EMPTY_DOUBLE_STRING_ARRAY
specifier|private
specifier|static
specifier|final
name|String
index|[]
index|[]
name|EMPTY_DOUBLE_STRING_ARRAY
init|=
operator|new
name|String
index|[
literal|0
index|]
index|[
literal|0
index|]
decl_stmt|;
comment|/**      *<p><code>CSVUtils</code> instances should NOT be constructed in      * standard programming.       *      *<p>This constructor is public to permit tools that require a JavaBean      * instance to operate.</p>      */
DECL|method|CSVUtils
specifier|public
name|CSVUtils
parameter_list|()
block|{     }
comment|/**      * Converts an array of string values into a single CSV line. All      *<code>null</code> values are converted to the string<code>"null"</code>,      * all strings equal to<code>"null"</code> will additionally get quotes      * around.      *      * @param values the value array      * @return the CSV string, will be an empty string if the length of the      * value array is 0      */
DECL|method|printLine
specifier|public
specifier|static
name|String
name|printLine
parameter_list|(
name|String
index|[]
name|values
parameter_list|,
name|CSVStrategy
name|strategy
parameter_list|)
block|{
comment|// set up a CSVUtils
name|StringWriter
name|stringWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|CSVPrinter
name|csvPrinter
init|=
operator|new
name|CSVPrinter
argument_list|(
name|stringWriter
argument_list|,
name|strategy
argument_list|)
decl_stmt|;
comment|// check for null values an "null" as strings and convert them
comment|// into the strings "null" and "\"null\""
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|values
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
name|values
index|[
name|i
index|]
operator|=
literal|"null"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|values
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"null"
argument_list|)
condition|)
block|{
name|values
index|[
name|i
index|]
operator|=
literal|"\"null\""
expr_stmt|;
block|}
block|}
comment|// convert to CSV
try|try
block|{
name|csvPrinter
operator|.
name|println
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// should not happen with StringWriter
block|}
comment|// as the resulting string has \r\n at the end, we will trim that away
return|return
name|stringWriter
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
return|;
block|}
comment|// ======================================================
comment|//  static parsers
comment|// ======================================================
comment|/**    * Parses the given String according to the default {@link CSVStrategy}.    *     * @param s CSV String to be parsed.    * @return parsed String matrix (which is never null)    * @throws IOException in case of error    */
DECL|method|parse
specifier|public
specifier|static
name|String
index|[]
index|[]
name|parse
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Null argument not allowed."
argument_list|)
throw|;
block|}
name|String
index|[]
index|[]
name|result
init|=
operator|(
operator|new
name|CSVParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
argument_list|)
operator|)
operator|.
name|getAllValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
comment|// since CSVStrategy ignores empty lines an empty array is returned
comment|// (i.e. not "result = new String[][] {{""}};")
name|result
operator|=
name|EMPTY_DOUBLE_STRING_ARRAY
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Parses the first line only according to the default {@link CSVStrategy}.    *     * Parsing empty string will be handled as valid records containing zero    * elements, so the following property holds: parseLine("").length == 0.    *     * @param s CSV String to be parsed.    * @return parsed String vector (which is never null)    * @throws IOException in case of error    */
DECL|method|parseLine
specifier|public
specifier|static
name|String
index|[]
name|parseLine
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Null argument not allowed."
argument_list|)
throw|;
block|}
comment|// uh,jh: make sure that parseLine("").length == 0
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|EMPTY_STRING_ARRAY
return|;
block|}
return|return
operator|(
operator|new
name|CSVParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
argument_list|)
operator|)
operator|.
name|getLine
argument_list|()
return|;
block|}
block|}
end_class

end_unit

