begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.internal.csv.writer
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
operator|.
name|writer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_comment
comment|/**  * Tries to guess a config based on an InputStream.  *  * @author Martin van den Bemt  * @version $Id$  */
end_comment

begin_class
DECL|class|CSVConfigGuesser
specifier|public
class|class
name|CSVConfigGuesser
block|{
comment|/** The stream to read */
DECL|field|in
specifier|private
name|InputStream
name|in
decl_stmt|;
comment|/**       * if the file has a field header (need this info, to be able to guess better)      * Defaults to false      */
DECL|field|hasFieldHeader
specifier|private
name|boolean
name|hasFieldHeader
init|=
literal|false
decl_stmt|;
comment|/** The found config */
DECL|field|config
specifier|protected
name|CSVConfig
name|config
decl_stmt|;
comment|/**      *       */
DECL|method|CSVConfigGuesser
specifier|public
name|CSVConfigGuesser
parameter_list|()
block|{
name|this
operator|.
name|config
operator|=
operator|new
name|CSVConfig
argument_list|()
expr_stmt|;
block|}
comment|/**      * @param in the inputstream to guess from      */
DECL|method|CSVConfigGuesser
specifier|public
name|CSVConfigGuesser
parameter_list|(
name|InputStream
name|in
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|setInputStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|setInputStream
specifier|public
name|void
name|setInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
comment|/**      * Allow override.      * @return the inputstream that was set.      */
DECL|method|getInputStream
specifier|protected
name|InputStream
name|getInputStream
parameter_list|()
block|{
return|return
name|in
return|;
block|}
comment|/**      * Guess the config based on the first 10 (or less when less available)       * records of a CSV file.      *       * @return the guessed config.      */
DECL|method|guess
specifier|public
name|CSVConfig
name|guess
parameter_list|()
block|{
try|try
block|{
comment|// tralalal
name|BufferedReader
name|bIn
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|(
name|getInputStream
argument_list|()
operator|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
index|[]
name|lines
init|=
operator|new
name|String
index|[
literal|10
index|]
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|bIn
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
operator|&&
name|counter
operator|<=
literal|10
condition|)
block|{
name|lines
index|[
name|counter
index|]
operator|=
name|line
expr_stmt|;
name|counter
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|counter
operator|<
literal|10
condition|)
block|{
comment|// remove nulls from the array, so we can skip the null checking.
name|String
index|[]
name|newLines
init|=
operator|new
name|String
index|[
name|counter
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|lines
argument_list|,
literal|0
argument_list|,
name|newLines
argument_list|,
literal|0
argument_list|,
name|counter
argument_list|)
expr_stmt|;
name|lines
operator|=
name|newLines
expr_stmt|;
block|}
name|analyseLines
argument_list|(
name|lines
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore exception.
block|}
block|}
block|}
name|CSVConfig
name|conf
init|=
name|config
decl_stmt|;
comment|// cleanup the config.
name|config
operator|=
literal|null
expr_stmt|;
return|return
name|conf
return|;
block|}
DECL|method|analyseLines
specifier|protected
name|void
name|analyseLines
parameter_list|(
name|String
index|[]
name|lines
parameter_list|)
block|{
name|guessFixedWidth
argument_list|(
name|lines
argument_list|)
expr_stmt|;
name|guessFieldSeperator
argument_list|(
name|lines
argument_list|)
expr_stmt|;
block|}
comment|/**      * Guess if this file is fixedwidth.      * Just basing the fact on all lines being of the same length      * @param lines      */
DECL|method|guessFixedWidth
specifier|protected
name|void
name|guessFixedWidth
parameter_list|(
name|String
index|[]
name|lines
parameter_list|)
block|{
name|int
name|lastLength
init|=
literal|0
decl_stmt|;
comment|// assume fixedlength.
name|config
operator|.
name|setFixedWidth
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|lines
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|lastLength
operator|=
name|lines
index|[
name|i
index|]
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|lastLength
operator|!=
name|lines
index|[
name|i
index|]
operator|.
name|length
argument_list|()
condition|)
block|{
name|config
operator|.
name|setFixedWidth
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|guessFieldSeperator
specifier|protected
name|void
name|guessFieldSeperator
parameter_list|(
name|String
index|[]
name|lines
parameter_list|)
block|{
if|if
condition|(
name|config
operator|.
name|isFixedWidth
argument_list|()
condition|)
block|{
name|guessFixedWidthSeperator
argument_list|(
name|lines
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|lines
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{         }
block|}
DECL|method|guessFixedWidthSeperator
specifier|protected
name|void
name|guessFixedWidthSeperator
parameter_list|(
name|String
index|[]
name|lines
parameter_list|)
block|{
comment|// keep track of the fieldlength
name|int
name|previousMatch
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|lines
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|last
init|=
literal|' '
decl_stmt|;
name|boolean
name|charMatches
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|lines
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|j
operator|==
literal|0
condition|)
block|{
name|last
operator|=
name|lines
index|[
name|j
index|]
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|last
operator|!=
name|lines
index|[
name|j
index|]
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|charMatches
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|charMatches
condition|)
block|{
if|if
condition|(
name|previousMatch
operator|==
operator|-
literal|1
condition|)
block|{
name|previousMatch
operator|=
literal|0
expr_stmt|;
block|}
name|CSVField
name|field
init|=
operator|new
name|CSVField
argument_list|()
decl_stmt|;
name|field
operator|.
name|setName
argument_list|(
literal|"field"
operator|+
name|config
operator|.
name|getFields
argument_list|()
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|field
operator|.
name|setSize
argument_list|(
operator|(
name|i
operator|-
name|previousMatch
operator|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|addField
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      *       * @return if the field uses a field header. Defaults to false.      */
DECL|method|hasFieldHeader
specifier|public
name|boolean
name|hasFieldHeader
parameter_list|()
block|{
return|return
name|hasFieldHeader
return|;
block|}
comment|/**      * Specify if the CSV file has a field header      * @param hasFieldHeader true or false      */
DECL|method|setHasFieldHeader
specifier|public
name|void
name|setHasFieldHeader
parameter_list|(
name|boolean
name|hasFieldHeader
parameter_list|)
block|{
name|this
operator|.
name|hasFieldHeader
operator|=
name|hasFieldHeader
expr_stmt|;
block|}
block|}
end_class

end_unit

